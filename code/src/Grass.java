import java.awt.Color;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.world.NonBlocking;

public class Grass implements Actor, DynamicDisplayInformationProvider, NonBlocking{
    public Grass(World world, Location location) {
        world.add(this);
        world.setTile(location, this);
    }


    public void act(World world) {

        System.out.println(world.contains(this));

        grow(world);
    }

    private void grow(World world) {
        Random random = new Random();
        if (random.nextInt(100) < 10) {
            Set<Location> emptyTiles = world.getEmptySurroundingTiles(world.getLocation(this));
            if (!emptyTiles.isEmpty()) {
                for (Location newLocation : emptyTiles) {
                 if (!world.containsNonBlocking(newLocation)) {
                    new Grass(world, newLocation);
                    break;
                 }
                }
            }
        }
    }


    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "bush");
    }
}