package animals;

import plants.Plant;
import itumulator.world.Location;

import java.util.ArrayList;

/**
 * Interface for herbivorous behavior.
 */
public interface Herbivorous {
    /**
     * Finds nearby plants to eat.
     *
     * @return A list of nearby plants.
     */
    ArrayList<Plant> findNearbyPlants();

    /**
     * Eats the plant at the current location.
     *
     * @param plant The plant to eat.
     */
    void eatPlant(Plant plant);

    /**
     * Moves towards a plant and eats it if on the same location.
     */
    void forage();
}
