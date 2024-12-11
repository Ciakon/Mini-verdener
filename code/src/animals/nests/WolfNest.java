package animals.nests;

import itumulator.world.Location;
import itumulator.world.World;

/**
 * Represents a Wolf's hole in the simulation world.
 *
 * A `WolfNest` acts as a shelter for wolves.
 */
public class WolfNest extends AnimalNest {
    /**
     * Constructs a new WolfNest at a specified location in the world.
     *
     * @param world    The simulation world where the nest exists.
     * @param location The location of the nest in the world.
     */
    public WolfNest(World world, Location location) {
        super(world, location);
    }

}
