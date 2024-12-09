package animals;

import itumulator.world.Location;
import itumulator.world.World;
import plants.Carcass;

import java.util.ArrayList;
import java.util.Set;

/**
 * Interface for carnivorous behavior.
 */
public interface Carnivorous {
    ArrayList<String> preferedPrey = new ArrayList<>();

    /**
     *
     * @param prey The prey to kill
     * @return The prey's nutrional value.
     */
    default void kill(Animal prey) {
        prey.die();
    }

    /**
     * Checks if an object is a prey
     *
     * @param animal object to check if prey
     * @return Returns true if object is in prefered prey list
     */
    default public <T> Boolean isPrey(T type, ArrayList<String> preferedPrey) {
        for (String prey : preferedPrey) {
            if (type.getClass().getSimpleName().equals(prey)) {
                return true;
            }
        }
        return false;
    }

    default public ArrayList<Location> findPrey(World world, Animal me) {
        ArrayList<Location> nearbyPrey = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(me), me.visionRange);
        for (Location location : surroundings) {
            if (world.getTile(location) instanceof Animal animal) {
                if (isPrey(animal, preferedPrey)) {
                    nearbyPrey.add(location);
                }
            }
        }
        return nearbyPrey;
    }

    /**
     * Moves towards nearest prey. If they are within range, eat them instead.
     * If there is no prey move randomly
     */
    public void hunting(Animal me) {
        ArrayList<Location> nearbyPrey = findPrey();
        if (!nearbyPrey.isEmpty()) {
            Location nearestPrey = nearestObject(nearbyPrey);
            if (Functions.calculateDistance(world.getLocation(this), nearestPrey) > 1) {
                moveTowards(nearestPrey);
            } else if (Functions.calculateDistance(world.getLocation(this), nearestPrey) == 1) {
                kill((Animal) world.getTile(nearestPrey));
            }
        } else {
            me.wander();
        }
    }

    public void findFood() {
        ArrayList<Location> nearbyCarcass = Functions.findNearbyObjects(this.world, world.getLocation(this), Carcass.class, this.visionRange);
        if (!nearbyCarcass.isEmpty()) {
            Location nearestCarcass = nearestObject(nearbyCarcass);
            if (Functions.calculateDistance(world.getLocation(this), nearestCarcass) > 1) {
                moveTowards(nearestCarcass);
            } else if (Functions.calculateDistance(world.getLocation(this), nearestCarcass) == 1) {
                this.eatCarcass((Carcass) world.getTile(nearestCarcass));
            }
        } else {
            this.hunting();
        }
    }

    /**
     * takes Nutritional value away from Carcass and gives it as energy.
     *
     * @param carcass Carcass to be eaten from
     * @return Returns the amount of Nutritional value eaten
     */
    public int eatCarcass(Carcass carcass) {
        int totalAmount = carcass.getNutritionalValue();
        int desiredAmount = this.maxEnergy - this.energy;
        if (totalAmount < desiredAmount) {
            carcass.setNutritionalValue(0);
            return totalAmount;
        }
        carcass.setNutritionalValue(totalAmount - desiredAmount);
        return desiredAmount;
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

}
