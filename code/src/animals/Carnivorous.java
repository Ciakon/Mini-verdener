package animals;

import itumulator.world.Location;
import itumulator.world.World;
import java.util.ArrayList;
import java.util.Set;
import plants.Carcass;
import utils.Functions;

/**
 * Interface for carnivorous behavior.
 */
public interface Carnivorous {

    /**
     *
     * @param prey The prey to kill
     * @return The prey's nutrional value.
     */
    default void kill(Animal prey) {
        prey.die(); // L bozo.
    }

    /**
     * Checks if an object is a prey
     *
     * @param animal object to check if prey
     * @return Returns true if object is in prefered prey list
     */
    default <T> Boolean isPrey(T type, ArrayList<String> preferedPrey) {
        for (String prey : preferedPrey) {
            if (type.getClass().getSimpleName().equals(prey)) {
                return true;
            }
        }
        return false;
    }

    default ArrayList<Location> findPrey() {
        Animal me = (Animal) this;
        World world = me.world;
        ArrayList<Location> nearbyPrey = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(me), me.visionRange);
        for (Location location : surroundings) {
            if (world.getTile(location) instanceof Animal animal) {
                if (isPrey(animal, me.preferedPrey)) {
                    nearbyPrey.add(location);
                }
            }
        }
        return nearbyPrey;
    }

    /**
     * Moves towards nearest prey. If they are within range, kill them instead.
     * If there is no prey, wander
     */
    default void hunting() {
        Animal me = (Animal) this;
        World world = me.world;
        ArrayList<Location> nearbyPrey = findPrey();

        if (!nearbyPrey.isEmpty()) {
            Location nearestPrey = me.nearestObject(nearbyPrey);

            if (Functions.calculateDistance(world.getLocation(me), nearestPrey) > 1) {
                me.moveTowards(nearestPrey);
            } else if (Functions.calculateDistance(world.getLocation(me), nearestPrey) == 1) {
                kill((Animal) world.getTile(nearestPrey));
            }
        } else {
            me.wander();
        }
    }

    default void findFood() {
        Animal me = (Animal) this;
        World world = me.world;
        ArrayList<Location> nearbyCarcass = Functions.findNearbyObjects(world, world.getLocation(me), Carcass.class, me.visionRange);
        if (!nearbyCarcass.isEmpty()) {
            Location nearestCarcass = me.nearestObject(nearbyCarcass);
            if (Functions.calculateDistance(world.getLocation(me), nearestCarcass) > 1) {
                me.moveTowards(nearestCarcass);
            } else if (Functions.calculateDistance(world.getLocation(me), nearestCarcass) == 1) {
                Carcass c = (Carcass) world.getTile(nearestCarcass);

                eatCarcass(c);
            }
        } else {
            hunting();
        }
    }

    /**
     * takes Nutritional value away from Carcass and gives it as energy.
     *
     * @param carcass Carcass to be eaten from
     */
    default void eatCarcass(Carcass carcass) {
        Animal me = (Animal) this;
        int totalAmount = carcass.getNutritionalValue();
        int desiredAmount = me.maxEnergy - me.energy;
        if (totalAmount < desiredAmount) {
            carcass.setNutritionalValue(0);
            me.addEnergy(totalAmount);
            return;
        }
        carcass.setNutritionalValue(totalAmount - desiredAmount);
        me.addEnergy(desiredAmount);
    }

    /**
     * Handles combat behavior when encountering another carnivore.
     *
     * @param opponent The other carnivore to fight.
     */
    default void engageInCombat(Animal opponent) {
        Animal me = (Animal) this;
        if (opponent instanceof Carnivorous == false) {
            throw new RuntimeException("Carnivorous animal cannot engage in combat with non-Carnivorous opponent");
        }

        if (opponent.energy > me.energy) {
            opponent.energy -= me.energy;
            me.die();
        } else if (me.energy > opponent.energy) {
            me.energy -= opponent.energy;
            opponent.die();
        } else {
            me.die();
            opponent.die();
        }
    }

}
