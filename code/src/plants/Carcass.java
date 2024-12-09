package plants;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;

public class Carcass extends Plant implements Actor, DynamicDisplayInformationProvider {

    protected String imageKey;
    protected int nutritionLoss;
    protected World world;
    protected Double chanceForShrooms;
    protected boolean hasShrooms;
    protected int shroomValue;
    protected int energyLoss;
    protected Mushrooms colonizer;
    protected boolean isColony;

    public Carcass(World world, Location location, boolean IsAdult, int maxEnergy) {
        super(world, location);
        this.nutritionalValue = maxEnergy;
        this.chanceForShrooms = 0.01;
        this.energyLoss = 3;
        this.world = world;
        world.add(this);
        world.setTile(location, this);
        if (IsAdult) {
            this.imageKey = "carcass";
        } else {
            this.imageKey = "carcass-small";
        }
    }

    public Carcass(World world, Location location, boolean IsAdult, int maxEnergy, boolean hasShrooms) {
        super(world, location);
        this.nutritionalValue = maxEnergy;
        this.chanceForShrooms = 0.01;
        this.energyLoss = 3;
        world.add(this);
        world.setTile(location, this);
        if (IsAdult) {
            this.imageKey = "carcass";
        } else {
            this.imageKey = "carcass-small";
        }
    }

    @Override
    public void act(World world) {
        this.shrooms();
        this.rot();
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, this.imageKey);
    }

    public void rot() {
        Location location = world.getLocation(this);

        this.nutritionalValue -= this.energyLoss;
        if (isColony) {
            this.nutritionalValue -= this.energyLoss;
            colonizer.gainEnergy(energyLoss);
        }
        if (this.nutritionalValue < 20) {
            if (isColony) {
                colonizer.removeColony(this);
            }
            if (this.hasShrooms && !this.isColony) {
                System.out.println(location);
                world.delete(this);
                new Mushrooms(this.world, location, this.shroomValue);
            } else {
                world.delete(this);
            }

        }
    }

    public void shrooms() {
        if (!this.hasShrooms && Math.random() < this.chanceForShrooms) {
            this.hasShrooms = true;
            this.shroomValue = 0;
        }
        if (this.hasShrooms) {
            this.nutritionalValue -= 3;
            this.shroomValue += 3;
        }
    }

    public void getColonized(Mushrooms mushrooms) {
        this.isColony = true;
        this.colonizer = mushrooms;
    }

    public boolean getIsColony() {
        if (this.isColony) {
            return true;
        }
        return false;
    }
}
