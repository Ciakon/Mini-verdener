package animals;

import itumulator.world.Location;
import itumulator.world.World;
import utils.Functions;

import java.util.ArrayList;
import java.util.Set;

import plants.Carcass;
import plants.Plant;

/**
 * Abstract class for carnivorous animals that hunt prey.
 */
public abstract class Carnivore extends Animal {

    /**
     * Constructor for Carnivore.
     *
     * @param world The simulation world.
     * @param isAdult Whether the carnivore spawns as an adult.
     */
    public Carnivore(World world, boolean isAdult) {
        super(world, isAdult);
    }

    /**
     * Handles combat behavior when encountering another carnivore.
     *
     * @param opponent The other carnivore to fight.
     */
    public void engageInCombat(Carnivore opponent) {
        if (opponent.energy > this.energy) {
            opponent.energy -= this.energy;
            this.die();
        } else {
            this.energy -= opponent.energy;
            opponent.die();
        }
    }

    @Override
    void dayTimeAI() {
        hunting();
    }
}
