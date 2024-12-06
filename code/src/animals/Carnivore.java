package animals;

import itumulator.world.Location;
import itumulator.world.World;
import utils.Functions;

import java.util.ArrayList;
import java.util.Set;


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
     * Finds all nearby prey within vision range.
     *
     * @return An ArrayList of nearby prey locations.
     */
    public ArrayList<Animal> findNearbyPrey() {
        ArrayList<Animal> preyList = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), visionRange);

        for (Location location : surroundings) {
            if (world.getTile(location) instanceof Animal animal && isPrey(animal)) {
                preyList.add(animal);
            }
        }

        return preyList;
    }

    /**
     * Handles hunting behavior for the carnivore.
     */
    public void hunt() {
        ArrayList<Animal> prey = findNearbyPrey();
        if (!prey.isEmpty()) {
            Animal nearestPrey = prey.get(0);
            moveTowards(world.getLocation(nearestPrey));
            if (Functions.calculateDistance(world.getLocation(this), world.getLocation(nearestPrey)) <= 1) {
                this.energy += kill(nearestPrey);
            }
        } else {
            moveRandomly();
        }
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

    /**
     * Moves randomly if no prey is found.
     */
    private void moveRandomly() {
        Location randomLocation = randomFreeLocation();
        if (randomLocation != null) {
            world.move(this, randomLocation);
        }
    }

    @Override
    void dayTimeAI() {
        hunt();
    }
}
