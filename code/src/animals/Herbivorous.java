package animals;

import plants.Plant;
import itumulator.world.Location;

import java.util.ArrayList;

/**
 * Interface for herbivorous behavior.
 */
public interface Herbivorous {
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
    public void forage(Animal me) {
        ArrayList<Plant> plants = findNearbyPlants();
        if (!plants.isEmpty()) {
            Plant nearestPlant = plants.get(0);
            moveTowards(world.getLocation(nearestPlant));
            if (world.getLocation(this).equals(world.getLocation(nearestPlant))) {
                this.energy += nearestPlant.getNutritionalValue();
                nearestPlant.consume();
            }
        } else {
            me.wander();
        }
    }
}
