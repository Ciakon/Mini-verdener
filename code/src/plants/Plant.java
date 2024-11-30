package plants;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.Random;
import java.util.Set;

public class Plant {
    /**
     * plants adds plants randomly by a chosen int percentage, at surounding tiles.
     *
     * @param world world which the plant is in
     * @param int percentage of chance
     *
     */
    public void grow(World world, int percent) {
        Random random = new Random();
        if (random.nextInt(100) < percent) {
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
}
