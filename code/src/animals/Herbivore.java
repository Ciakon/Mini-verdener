package animals;

import itumulator.world.Location;
import itumulator.world.World;
import plants.Plant;
import java.util.ArrayList;
import java.util.Set;

/**
 * Abstract class for herbivorous animals that interact with plants.
 */
public abstract class Herbivore extends Animal {

    /**
     * Constructor for Herbivore.
     *
     * @param world The simulation world.
     * @param isAdult Whether the herbivore spawns as an adult.
     */
    public Herbivore(World world, boolean isAdult) {
        super(world, isAdult);
    }

    /**
     * Finds all nearby plants within the vision range.
     *
     * @return An ArrayList of nearby plant locations.
     */
    public ArrayList<Plant> findNearbyPlants() {
        ArrayList<Plant> nearbyPlants = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), visionRange);

        for (Location location : surroundings) {
            if (world.getNonBlocking(location) instanceof Plant plant) {
                nearbyPlants.add(plant);
            }
        }

        return nearbyPlants;
    }

    /**
     * Herbivore-specific AI for locating and eating plants.
     */
    public void forage() {
        ArrayList<Plant> plants = findNearbyPlants();
        if (!plants.isEmpty()) {
            Plant nearestPlant = plants.get(0);
            moveTowards(world.getLocation(nearestPlant));
            if (world.getLocation(this).equals(world.getLocation(nearestPlant))) {
                this.energy += nearestPlant.getNutritionalValue();
                nearestPlant.consume();
            }
        } else {
            moveRandomly();
        }
    }

    /**
     * Moves randomly if no food is found.
     */
    private void moveRandomly() {
        Location randomLocation = randomFreeLocation();
        if (randomLocation != null) {
            world.move(this, randomLocation);
        }
    }

    @Override
    void dayTimeAI() {
        forage();
    }
}
