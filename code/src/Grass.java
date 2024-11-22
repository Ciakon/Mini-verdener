import java.awt.Color;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.world.NonBlocking;

public class Grass implements Actor, DynamicDisplayInformationProvider, NonBlocking{
    public Grass() {

    }



    public void act(World world) {
    grow(world);
    }

    private void grow(World world) {
        Random random = new Random();
        if (random.nextInt(100) < 50) {
            Set<Location> emptyTiles = world.getEmptySurroundingTiles(world.getLocation(this));
            if (!emptyTiles.isEmpty()) {
                Location newLocation = emptyTiles.iterator().next();
                if (world.getTile(newLocation) == null ) {
                    world.add(new Grass());
                    world.setTile(newLocation, new Grass());
                }
            }
        }
    }


    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "bush");
    }
}