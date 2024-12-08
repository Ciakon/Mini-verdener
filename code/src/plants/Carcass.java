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

    public Carcass(World world, Location location, boolean IsAdult, int maxEnergy) {
        super(world, location);
        this.nutritionalValue = maxEnergy;
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

    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, this.imageKey);
    }

    public void rot() {
        this.nutritionalValue -= 3;
        if (this.nutritionalValue < 20) {
            world.delete(this);
        }
    }
}
