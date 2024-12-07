package animals;

import animals.nests.AnimalNest;
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
import plants.Carcass;
import utils.Functions;

/**
 *
 */
public abstract class Animal implements Actor, DynamicDisplayInformationProvider {

    int age;
    int adultAge = 120;

    int visionRange = 3;
    int maxEnergy = 40;
    int energy = 20;
    int energyLoss = 1;

    int nutritionalValue;
    int nutritionalValueAdult = 30;
    int nutritionalValueBaby = 10;
    int breedingEnergy = 15;

    boolean isSleeping = false;
    boolean hasBred = false;
    boolean isAdult;
    boolean willDie;
    boolean isInsideNest;

    String imageKeyBaby;
    String imageKeyAdult;
    String imageKeySleepingBaby;
    String imageKeySleepingAdult;
    Color color = Color.red;

    Location previousPosition;
    Location wanderGoal; // Animals will wander towards this tile, updated daily.
    World world;
    AnimalNest<Animal> animalNest;

    /**
     *
     * @param world The simulation world.
     * @param isAdult Whether to spawn the animal as an adult or baby.
     */
    public Animal(World world, boolean isAdult) {
        this.world = world;
        world.add(this);
        wanderGoal = Functions.findRandomEmptyLocation(world);

        if (isAdult) {
            this.isAdult = true;
            this.age = adultAge;
            this.nutritionalValue = nutritionalValueAdult;
        } else {
            this.isAdult = false;
            this.age = 0;
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
        if (energy >= maxEnergy) {
            energy = maxEnergy;
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
        Location location = world.getLocation(this);
        if (world.contains(this)) {
            world.delete(this);
            new Carcass(this.world, location, this.isAdult, this.maxEnergy);
        } else {
            world.delete(this);
        }
    }

    /**
     * Moves randomly in a random direction
     */
    void wander() {
        moveTowards(wanderGoal);

        // Update wander goal.
        if (world.getLocation(this).equals(wanderGoal) || world.getCurrentTime() == 0) {
            wanderGoal = Functions.findRandomEmptyLocation(world);
        }
    }

    /**
     * Finds empty surrounding location to move to
     *
     * @return Returns random empty surrounding location if there exists at
     * least 1
     */
    public Location randomFreeLocation() {
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

    /**
     * Animal grows once a day. When it reaches a certain age, it becomes an
     * adult.
     */
    void grow() {
        age++;
        if (age == adultAge) {
            isAdult = true;
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
        ArrayList<Animal> animalList = this.animalNest.getAllAnimals();
        for (Animal animal : animalList) {
            if (animal.isBreedable() && animal != this) {
                this.breed(animal);
                break;
            }
        }
    }

    public boolean isBreedable() {
        if (this.age > this.adultAge && !this.getHasBred() && this.getIsInsideNest() && this.getEnergy() > this.breedingEnergy) {
            return true;
        } else {
            return false;
        }
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

    boolean getHasBred() {
        return this.hasBred;
    }

    void setHasBred(boolean hasBred) {
        this.hasBred = hasBred;
    }

    boolean getIsInsideNest() {
        return this.isInsideNest;
    }

    int getEnergy() {
        return this.energy;
    }

    /**
     * removes a certain amount of energy
     *
     * @param amount positive int of how much energy needs to be removed
     */
    void removeEnergy(int amount) {
        this.energy -= amount;
    }

    /**
     * Finds the nearest nest within the animals's vision range.
     *
     *
     * @return the nearest nest object, or null if none are found
     */
    public AnimalNest findNearestNest() {
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), this.visionRange);
        AnimalNest closestHole = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Location location : surroundings) {
            if (world.getTile(location) instanceof AnimalNest hole) {
                int distance = Functions.calculateDistance(world.getLocation(this), location);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestHole = hole;
                }
            }
        }
        return closestHole;
    }

    /**
     * Moves the animal toward the nearest nest using moveTowards() if one
     * exists. If no nest is nearby, the animal digs a new nest.
     *
     * @param world the world in which the animal is located
     */
    public void moveToOrDigHole() {
        if (this.animalNest != null) {
            moveTowards(world.getLocation(animalNest));

            if (previousPosition.equals(world.getLocation(animalNest)) && world.getLocation(this).equals(previousPosition)) {
                enterHole();
            }
            return;
        }

        AnimalNest nearestHole = findNearestNest();

        if (nearestHole != null) {
            moveTowards(world.getLocation(nearestHole));
            if (world.getLocation(this).equals(world.getLocation(nearestHole))) {
                animalNest = nearestHole;
                nearestHole.addAnimal(this);
            }
        } else {
            digHole();
        }
    }

    /**
     * Digs a new animal nest at the animals location in the world. This method
     * sets the animals's `animalNest` attribute to the new hole.
     */
    public void digHole() {
        if (this.animalNest != null) {
            throw new RuntimeException("animals should only dig a hole when they don't have one");
        }

        Location location = world.getLocation(this);

        if (world.getNonBlocking(location) != null) {
            return;
        }

        //this.animalNest = new AnimalNest(this.world, location); //commented out for the sake of my sanity
        animalNest.addAnimal(this);

        enterHole();
    }

    public void enterHole() {
        if (world.getLocation(this).equals(world.getLocation(this.animalNest)) == false) {
            throw new RuntimeException("animals should only enter their own hole when standing on it");
        }
        this.isInsideNest = true;

        isSleeping = true;
        world.remove(this); // gaming time
    }

    public void exitHole() {
        Location holeLocation = world.getLocation(this.animalNest);
        if (world.isTileEmpty(holeLocation)) {
            isInsideNest = false;
            world.setTile(holeLocation, this);
        }
    }
}
