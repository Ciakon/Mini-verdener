package animals;

import itumulator.world.Location;

import java.util.ArrayList;

/**
 * Interface for carnivorous behavior.
 */
public interface Carnivorous {
    /**
     * Finds nearby prey to hunt.
     *
     * @return A list of nearby prey animals.
     */
    ArrayList<Animal> findNearbyPrey();

    /**
     * Hunts and eats prey.
     */
    void hunt();

    /**
     * Engages in combat with another carnivore.
     *
     * @param opponent The carnivore to fight.
     */
    void engageInCombat(Carnivore opponent);
}
