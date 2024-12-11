package plants;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;


/**
 * Represents a carcass in the simulation, which acts as a decaying resource for other entities.
 *
 * The carcass can rot over time, lose nutritional value, and potentially spawn mushrooms
 * that either grow on it or colonize it for energy.
 */
public class Carcass implements Actor, DynamicDisplayInformationProvider {

    protected int nutritionalValue;
    protected String imageKey;
    protected int nutritionLoss;
    protected World world;
    protected Double chanceForShrooms;
    protected boolean hasShrooms;
    protected int shroomValue;
    protected Mushrooms colonizer;
    protected boolean isColony;

    /**
     * Constructs a new Carcass instance at the given location in the simulation world.
     *
     * @param world          The simulation world.
     * @param location       The location of the carcass.
     * @param IsAdult        Indicates if the carcass is from an adult animal.
     * @param nutritionValue The initial nutritional value of the carcass.
     */
    public Carcass(World world, Location location, boolean IsAdult, int nutritionValue) {
        this.nutritionalValue = nutritionValue;
        this.chanceForShrooms = 0.10;
        this.nutritionLoss = 1;
        this.world = world;
        world.add(this);
        world.setTile(location, this);
        if (IsAdult) {
            this.imageKey = "carcass";

        } else {
            this.imageKey = "carcass-small";
        }
    }

    /**
     * Constructs a new Carcass instance with mushroom status.
     *
     * @param world          The simulation world.
     * @param location       The location of the carcass.
     * @param IsAdult        Indicates if the carcass is from an adult animal.
     * @param nutritionValue The initial nutritional value of the carcass.
     * @param hasShrooms     Indicates if the carcass has mushrooms growing on it.
     */
    public Carcass(World world, Location location, boolean IsAdult, int nutritionValue, boolean hasShrooms) {
        this.nutritionalValue = nutritionValue;
        this.chanceForShrooms = 0.05;
        this.nutritionLoss = 1;
        world.add(this);
        world.setTile(location, this);
        if (IsAdult) {
            this.imageKey = "carcass";
        } else {
            this.imageKey = "carcass-small";
        }
        this.hasShrooms = hasShrooms;
    }

    /**
     * Defines the behavior of the carcass during each simulation step:
     * - Manages mushroom growth.
     * - Handles nutritional decay through rotting.
     * - Removes carcass if its nutritional value falls below a threshold.
     *
     * @param world The simulation world.
     */
    @Override
    public void act(World world) {
        this.shrooms();
        this.rot();
        this.purgeLowEnergyCarcass();
    }

    /**
     * Decays the carcass, reducing its nutritional value. If the carcass is colonized by mushrooms,
     * they gain energy as the carcass decays faster.
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, this.imageKey);
    }

    /**
     * Makes Carcass loss nutritional Value. if nutritional value is too low,
     * deletes carcass.
     */
    public void rot() {
        this.nutritionalValue -= this.nutritionLoss;
        if (isColony) {
            this.nutritionalValue -= this.nutritionLoss;
            colonizer.gainEnergy(nutritionLoss);
        }
    }

    /**
     * If carcass nutritional value is too low delete it. If it has been growing
     * mushrooms, mushrooms will spawn on it's location.
     */
    public void purgeLowEnergyCarcass() {
        if (this.nutritionalValue < 20) {
            if (isColony) {
                colonizer.removeColony(this);
            }
            if (this.hasShrooms) {
                Location location = world.getLocation(this);
                world.delete(this);
                new Mushrooms(this.world, location, this.shroomValue);
            } else {
                world.delete(this);
            }

        }
    }

    /**
     * Certain chance to make mushrooms grow on the carcass.
     */
    public void shrooms() {
        if (!this.hasShrooms && Math.random() <= this.chanceForShrooms) {
            this.hasShrooms = true;
            this.shroomValue = 0;
        }
        if (this.hasShrooms) {
            this.nutritionalValue -= nutritionLoss;
            this.shroomValue += nutritionLoss;
        }
    }

    /**
     * Makes mushrooms That are on the map feed on the nutritional value of the
     * carcass which makes it rot faster.
     *
     * @param mushrooms The mushroom that reaches out to.
     */
    public void getColonized(Mushrooms mushrooms) {
        this.isColony = true;
        this.colonizer = mushrooms;
    }

    /**
     * Checks if carcass is being consummed by mushrooms that are on the map.
     *
     * @return Returns true if carcass is being consumed by mushrooms on the
     * map, otherwise returns false.
     */
    public boolean getIsColony() {
        if (this.isColony) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the carcass has mushrooms growing on it.
     *
     * @return `true` if the carcass has mushrooms, `false` otherwise.
     */
    public boolean getShroomStatus() {
        if (this.hasShrooms) {
            return true;
        }
        return false;
    }

    /**
     * Retrieves the current nutritional value of the carcass.
     *
     * @return The nutritional value of the carcass.
     */
    public int getNutritionalValue() {
        return this.nutritionalValue;
    }

    /**
     * Updates the nutritional value of the carcass.
     *
     * @param newNutritionalValue The new nutritional value to set.
     */
    public void setNutritionalValue(int newNutritionalValue) {
        this.nutritionalValue = newNutritionalValue;
    }
}
