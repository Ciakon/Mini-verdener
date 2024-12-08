package animals;

import itumulator.world.Location;
import itumulator.world.World;
import java.util.ArrayList;
import java.util.Set;
import plants.Carcass;
import utils.Functions;

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
     *
     * @param prey The prey to kill
     * @return The prey's nutrional value.
     */
    void kill(Animal prey) {
        prey.die();
    }

    /**
     * Checks if an object is a prey
     *
     * @param animal object to check if prey
     * @return Returns true if object is in prefered prey list
     */
    public <T> Boolean isPrey(T type) {
        for (String prey : preferedPrey) {
            if (type.getClass().getSimpleName().equals(prey)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Location> findPrey() {
        ArrayList<Location> nearbyPrey = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), this.visionRange);
        for (Location location : surroundings) {
            if (world.getTile(location) instanceof Animal animal) {
                if (isPrey(animal)) {
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
    public void hunting() {
        ArrayList<Location> nearbyPrey = findPrey();
        if (!nearbyPrey.isEmpty()) {
            Location nearestPrey = nearestObject(nearbyPrey);
            if (Functions.calculateDistance(world.getLocation(this), nearestPrey) > 1) {
                moveTowards(nearestPrey);
            } else if (Functions.calculateDistance(world.getLocation(this), nearestPrey) == 1) {
                kill((Animal) world.getTile(nearestPrey));
            } else {
                Location nextLocation = randomFreeLocation();
                if (nextLocation != null) {
                    world.move(this, nextLocation);
                }
            }
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

    @Override
    void dayTimeAI() {
        findFood();
    }
}
