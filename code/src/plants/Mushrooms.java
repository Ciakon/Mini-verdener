package plants;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import utils.Functions;


/**
 * Represents mushrooms in the simulation world.
 *
 * Mushrooms act as decomposers by colonizing nearby carcasses to drain its energy.
 * They decay over time, losing energy, and can be removed if their energy reaches zero.
 */

public class Mushrooms extends Plant implements Actor, DynamicDisplayInformationProvider {

    private String imageKey;
    private int maxEnergy = 150;

    private int energy;
    private int energyLoss;
    private int reachRange;

    private Location location;
    private World world;

    private ArrayList<Carcass> colonies;

    /**
     * Constructs a new `Mushrooms` instance at the specified location with a given initial energy.
     *
     * @param world   The simulation world.
     * @param location The location of the mushrooms in the world.
     * @param energy   The initial energy of the mushrooms.
     */

    public Mushrooms(World world, Location location, int energy) {
        super(world, location);

        this.energy = energy + 20;
        this.energyLoss = 1;
        this.reachRange = 5;

        this.world = world;
        this.location = location;

        this.colonies = new ArrayList<>();

        this.imageKey = "fungi-small";
        world.setTile(this.location, this);
    }

    /**
     * Defines the behavior of the mushrooms during each simulation step.
     *
     * - Updates the texture based on energy level.
     * - Searches for nearby carcasses to colonize.
     * - Reduces energy through decay.
     *
     * @param world The simulation world.
     */
    @Override
    public void act(World world) {
        changeImageKey();
        findColonies();
        decay();
    }

    /**
     * Provides display information for rendering the mushrooms in the simulation.
     *
     * @return A `DisplayInformation` object containing the mushrooms' color and texture.
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, this.imageKey);
    }

    /**
     * Updates the texture of the mushrooms based on their energy level.
     *
     * - If energy > 50, the texture is set to "fungi".
     * - Otherwise, the texture is set to "fungi-small".
     */

    private void changeImageKey() {
        if (this.energy > 50) {
            this.imageKey = "fungi";
        } else {
            this.imageKey = "fungi-small";
        }
    }

    /**
     * Makes mushrooms lose energy. also caps mushroom energy to maxEnergy.
     */
    private void decay() {
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
        this.energy -= this.energyLoss;
        if (energy <= 0) {
            world.delete(this);
        }
    }

    /**
     * Searches for nearby carcasses within the mushrooms' reach range and creates colonies on them.
     */
    private void findColonies() {
        ArrayList<Location> pontentialColonies = Functions.findNearbyObjects(this.world, world.getLocation(this), Carcass.class, this.reachRange);
        for (Location location : pontentialColonies) {
            if (!((Carcass) world.getTile(location)).getIsColony()) {
                createColony((Carcass) world.getTile(location));
            }
        }
    }

    /**
     * Gives energy to the mushrooms given an amount.
     *
     * @param amount The amount of energy gained by the mushrooms.
     */
    public void gainEnergy(int amount) {
        this.energy += amount;
    }

    /**
     * Connects and feeds off carcass.
     *
     * @param carcass Carcass to feed off of.
     */
    private void createColony(Carcass carcass) {
        carcass.getColonized(this);
        this.colonies.add(carcass);
    }

    /**
     * Removes carcass from mushroom network.
     *
     * @param carcass Carcass to remove.
     */
    public void removeColony(Carcass carcass) {
        this.colonies.remove(carcass);
    }
}
