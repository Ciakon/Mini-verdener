package animals;

import plants.Plant;
import utils.Functions;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.Set;

/**
 * Interface for herbivorous behavior.
 */
public interface Herbivorous {
    /**
     * Finds all nearby plants within the vision range.
     *
     * @return An ArrayList of nearby plant locations.
     */
    default ArrayList<Plant> findNearbyPlants() {
        Animal me = (Animal) this;
        World world = me.world;
        ArrayList<Plant> nearbyPlants = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(me), me.visionRange);

        if (world.getTile(world.getLocation(me)) instanceof Plant plant) {
            nearbyPlants.add(plant);
        }

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
    default void forage() {
        Animal me = (Animal) this;
        World world = me.world;
        ArrayList<Plant> plants = findNearbyPlants();
        if (!plants.isEmpty()) {
            Plant nearestPlant = plants.get(0);

            for (Plant plant : plants) {
                if (Functions.calculateDistance(world.getLocation(this), world.getLocation(plant)) < Functions.calculateDistance(world.getLocation(this), world.getLocation(nearestPlant))) {
                    nearestPlant = plant;
                }
            }

            me.moveTowards(world.getLocation(nearestPlant));
            if (world.getLocation(me).equals(world.getLocation(nearestPlant))) {
                me.energy += nearestPlant.getNutritionalValue();
                nearestPlant.consume();
            }
        } else {
            me.wander();
        }
    }
}
