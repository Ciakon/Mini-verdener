package animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import utils.Functions;

/**
 * Crazy
 */
abstract class Animal implements Actor, DynamicDisplayInformationProvider {

    int age;
    int adultAge = 120;

    int visionRange = 3;
    int maxEnergy = 40;
    int energy = 20;
    int energyLoss = 1;

    int nutritionalValue;
    int nutritionalValueAdult = 30;
    int nutritionalValueBaby = 10;

    boolean isSleeping = false;
    boolean hasBred = false;
    boolean breedable;
    boolean isAdult;
    boolean willDie;

    String imageKeyBaby;
    String imageKeyAdult;
    String imageKeySleepingBaby;
    String imageKeySleepingAdult;
    Color color = Color.red;

    Location previousPosition;
    World world;
    ArrayList<String> preferedPrey;

    /**
     *
     * @param world The simulation world.
     * @param isAdult Whether to spawn the animal as an adult or baby.
     */
    public Animal(World world, boolean isAdult) {
        this.world = world;
        world.add(this);
        if (isAdult) {
            this.isAdult = true;
            this.age = adultAge;
            this.breedable = true;
            this.nutritionalValue = nutritionalValueAdult;
        } else {
            this.isAdult = false;
            this.age = 0;
            this.breedable = false;
            this.nutritionalValue = nutritionalValueBaby;
        }
    }

    @Override
    public void act(World world) {
        if (world.isDay()) {
            dayTimeAI();
        } else {
            nightTimeAI();
        }
        generalAI(); // Remember this runs last.

        grow();

        this.energy -= energyLoss;
        if (energy <= 0) {
            die();
        }
    }

    @Override
    public DisplayInformation getInformation() {
        String imageKey;

        if (isSleeping) {
            if (isAdult) {
                imageKey = imageKeySleepingAdult;
            } else {
                imageKey = imageKeySleepingBaby;
            }
        } else {
            if (isAdult) {
                imageKey = imageKeyAdult;
            } else {
                imageKey = imageKeyBaby;
            }
        }
        return new DisplayInformation(color, imageKey);
    }

    /**
     * L, bozo.
     */
    void die() {
        world.delete(this);
    }

    /**
     *
     * @param prey The prey to kill
     * @return The prey's nutrional value.
     */
    int kill(Animal prey) {
        int NV = prey.nutritionalValue;
        prey.die();
        return NV;
    }

    ArrayList<Location> findPrey() {
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

    void hunting() {
        ArrayList<Location> nearbyPrey = findPrey();
        if (!nearbyPrey.isEmpty()) {
            Location nearestPrey = nearestObject(nearbyPrey);
            if (Functions.calculateDistance(world.getLocation(this), nearestPrey) > 1) {
                moveTowards(nearestPrey);
            } else if (Functions.calculateDistance(world.getLocation(this), nearestPrey) == 1) {
                this.energy += kill((Animal) world.getTile(nearestPrey));
            } else {
                Location nextLocation = randomFreeLocation();
                if (nextLocation != null) {
                    world.move(this, nextLocation);
                    this.energy -= (int) (energyLoss * 0.1);
                }
            }
        }
    }

    /**
     * Finds empty surrounding location to move to
     *
     * @return Returns random empty surrounding location if there exists at
     * least 1
     */
    Location randomFreeLocation() {
        Set<Location> freeLocations = world.getEmptySurroundingTiles(world.getLocation(this));
        if (freeLocations.isEmpty()) {
            return null;
        }
        List<Location> LocationlList = new ArrayList<>(freeLocations);
        Random random = new Random();
        int randomIndex = random.nextInt(LocationlList.size());
        return LocationlList.get(randomIndex);
    }

    abstract void generalAI();

    abstract void dayTimeAI();

    abstract void nightTimeAI();

    Boolean isPrey(Animal animal) {
        for (String prey : preferedPrey) {
            if (animal.getClass().getSimpleName().equals(prey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Animal grows once a day. When it reaches a certain age, it becomes an
     * adult.
     */
    void grow() {
        age++;
        if (age == adultAge) {
            isAdult = true;
            breedable = true;
            nutritionalValue = nutritionalValueAdult;
        }
    }

    abstract void breed(Animal partner);
    // here is an example of breeding

    // Animal breed(Animal partner) {
    //     Animal child = new Animal(world, isAdult = false);
    //     energy -= energyLoss;
    //     hasBred = true; 
    //     return child;
    // }
    public void findBreedingPartner() {
        //TODO cook.
    }

    /**
     * Moves 1 tile towards a given location
     *
     * @param desiredlocation location to move towards
     */
    public void moveTowards(Location desiredLocation) {
        int movementInX = world.getLocation(this).getX();
        int movementInY = world.getLocation(this).getY();
        if (world.getLocation(this).getX() < desiredLocation.getX()) {
            movementInX++;
        } else if (world.getLocation(this).getX() > desiredLocation.getX()) {
            movementInX--;
        }

        if (world.getLocation(this).getY() < desiredLocation.getY()) {
            movementInY++;
        } else if (world.getLocation(this).getY() > desiredLocation.getY()) {
            movementInY--;
        }
        Location movement = new Location(movementInX, movementInY);
        if (world.isTileEmpty(movement)) {
            world.move(this, movement);
        }
    }

    /**
     * Finds nearest item in given ArrayList
     *
     * @param Arraylist Arraylist of multiple objects in the world.
     * @return returns location of the closest object
     */
    public Location nearestObject(ArrayList<Location> object) {
        Location nearest = object.get(0);
        for (Location location : object) {
            if (Functions.calculateDistance(world.getLocation(this), location) < Functions.calculateDistance(world.getLocation(this), nearest)) {
                nearest = location;
            }
        }
        return nearest;
    }
}
