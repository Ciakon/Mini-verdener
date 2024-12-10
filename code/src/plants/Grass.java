
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
        super(world, location);
        world.add(this);
        world.setTile(location, this);
        this.nutritionalValue = 10;
    }

    @Override
    public void act(World world) {
        super.grow(world,3);

        if (isDepleted) {
            world.delete(this); 
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "grass");
    }

    public int getNutritionalValue() {
        return this.nutritionalValue;
    }



}
