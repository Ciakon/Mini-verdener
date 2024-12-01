
package plants;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;
import java.util.*;

public class Grass extends Plant implements Actor, DynamicDisplayInformationProvider, NonBlocking {

    private int nutritionalValue;

    public Grass(World world, Location location) {
        world.add(this);
        world.setTile(location, this);
        this.nutritionalValue = 10;
    }

    @Override
    public void act(World world) {
        //System.out.println(world.contains(this));
        super.grow(world,5);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "grass");
    }

    public int getNutritionalValue() {
        return this.nutritionalValue;
    }



}
