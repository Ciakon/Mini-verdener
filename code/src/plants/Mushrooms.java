package plants;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import utils.Functions;

public class Mushrooms extends Plant implements Actor, DynamicDisplayInformationProvider {

    protected String imageKey;

    protected int energy;
    protected int energyLoss;
    protected int reachRange;

    protected Location location;
    protected World world;

    protected ArrayList<Carcass> colonies;

    Mushrooms(World world, Location location, int energy) {
        super(world, location);

        this.energy = energy;
        this.energyLoss = 3;
        this.reachRange = 5;

        this.world = world;
        this.location = location;

        this.colonies = new ArrayList<>();

        this.imageKey = "fungi";
    }

    @Override
    public void act(World world) {
        changeImageKey();
        findColonies();
        decay();
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, this.imageKey);
    }

    /**
     * changes imagekey depending on how much energy the mushrooms have.
     */
    public void changeImageKey() {
        if (energy > 100) {
            this.imageKey = "fungi";
        } else {
            this.imageKey = "fungi-small";
        }
    }

    /**
     * Makes mushrooms lose energy.
     */
    public void decay() {
        this.energy -= this.energyLoss;
    }

    /**
     * Looks for nearby carcass to feed off.
     */
    public void findColonies() {
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
    void gainEnergy(int amount) {
        this.energy += amount;
    }

    /**
     * Connects and feeds off carcass.
     *
     * @param carcass Carcass to feed off of.
     */
    public void createColony(Carcass carcass) {
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
