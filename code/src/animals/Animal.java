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
 * The {@code Animal} class represents the base class for all animal entities in the simulation.
 * It provides shared properties and behaviors, such as movement, growth, breeding, energy management,
 * and interactions with nests and other entities.
 * <p>
 * Subclasses of {@code Animal} are expected to implement behaviors by overriding
 * abstract methods such as {@code dayTimeAI()}, {@code nightTimeAI()}, and {@code generalAI()}.
 * </p>
 */
public abstract class Animal implements Actor, DynamicDisplayInformationProvider {

    protected int age;
    protected int adultAge = 120;

    protected int visionRange = 3;
    protected int maxEnergy = 40;
    protected int energy = 20;
    protected int energyLoss = 1;

    protected int nutritionalValue;
    protected int nutritionalValueAdult = 30;
    protected int nutritionalValueBaby = 10;
    protected int breedingEnergy = 15;

    protected boolean isSleeping = false;
    protected boolean hasBred = false;
    protected boolean isAdult;
    protected boolean willDie;
    protected boolean isInsideNest;

    protected String imageKeyBaby;
    protected String imageKeyAdult;
    protected String imageKeySleepingBaby;
    protected String imageKeySleepingAdult;
    protected Color color = Color.red;

    protected Location previousPosition;
    protected Location wanderGoal; // Animals will wander towards this tile, updated daily.
    protected World world;
    protected AnimalNest animalNest;
    protected ArrayList<String> preferedPrey = new ArrayList<>();

    /**
     * Constructs an animal
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

    /**
     * Defines the general behavior of animal during the simulation step.
     * Handles AI logic based on the time of day and other conditions.
     *
     * @param world The simulation world.
     */
    @Override
    public void act(World world) {
        if (world.isDay()) {
            dayTimeAI();
        } else {
            nightTimeAI();
        }
        generalAI(); // Remember this runs last.

        grow();

        if (world.contains(this) == false) return;

        if (world.isOnTile(this)) {
            previousPosition = world.getLocation(this);
        }

        this.energy -= energyLoss;
        if (energy <= 0) {
            die();
        }
        if (energy >= maxEnergy) {
            energy = maxEnergy;
        }
        
    }
    /**
     * Provides display information for the animal, including its image and color,
     * based on its current state (e.g., sleeping or awake, baby or adult).
     *
     * @return A {@link DisplayInformation} object containing display data.
     */
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
     * Deletes Animal and replaces it with {@link Carcass}
     */
    protected void die() {
        if (world.isOnTile(this)) {
            Location location = world.getLocation(this);
            world.delete(this);
            new Carcass(this.world, location, this.isAdult, this.nutritionalValue);
        } else {
            world.delete(this);
        }
    }

    /**
     * Moves randomly in a random direction
     */
    protected void wander() {
        moveTowards(wanderGoal);

        // Update wander goal.
        if (world.getLocation(this).equals(wanderGoal) || world.getCurrentTime() == 0) {
            wanderGoal = Functions.findRandomEmptyLocation(world);
        }
    }

    /**
     * Finds empty surrounding location to move to
     *
     * @return Returns random empty surrounding {@link Location} if there exists at
     * least 1
     * else {@code null}
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

    /**
     * Abstract method to define general AI behavior. to be implemented by subclasses.
     */
    abstract protected void generalAI();

    /**
     * Abstract method to define daytime AI behavior. to be implemented by subclasses.
     */
    abstract protected void dayTimeAI();

    /**
     * Abstract method to define nighttimeAI behavior. to be implemented by subclasses.
     */
    abstract protected void nightTimeAI();

    /**
     * Animal grows once a day. When it reaches a certain age, it becomes an
     * adult.
     */
    protected void grow() {
        age++;
        if (age == adultAge) {
            isAdult = true;
            nutritionalValue = nutritionalValueAdult;
        }
    }

    /**
     * Abstract method to handle the breeding process. to be implemented by subclasses.
     *
     * @param partner The {@code Animal} partner involved in breeding.
     */
    abstract protected void breed(Animal partner);
    // here is an example of breeding

    // Animal breed(Animal partner) {
    //     Animal child = new Animal(world, isAdult = false);
    //     energy -= energyLoss;
    //     hasBred = true; 
    //     return child;
    // }


    /**
     * Searches for a suitable breeding partner within the same nest and attempts to breed.
     */
    protected void findBreedingPartner() {
        ArrayList<Animal> animalList = this.animalNest.getAllAnimals();
        for (Animal animal : animalList) {
            if (animal.isInsideNest == false) continue;
            if (animal.isBreedable() && animal != this) {
                this.breed(animal);
                break;
            }
        }
    }

    /**
     * Checks if the animal checks off criteria to breed.
     *
     * @return Returns true if all criteria for breeding are met otherwise
     * returns false.
     */
    public boolean isBreedable() {
        if (this.age >= this.adultAge && !this.hasBred && this.getIsInsideNest() && this.getEnergy() > this.breedingEnergy) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves 1 tile towards a given location
     *
     * @param desiredLocation {@link Location} to move towards
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
        } else if (world.getLocation(this).equals(desiredLocation) == false) {
            // try another position nearby
            for (Location surroundingLocation : world.getEmptySurroundingTiles(world.getLocation(this))) {
                for (Location desireableLocation : world.getEmptySurroundingTiles(movement)) {
                    if (surroundingLocation.equals(desireableLocation)) {
                        world.move(this, desireableLocation);
                        return;
                    }
                }
            }
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

    /**
     * Sets hasbred variable
     *
     * @param hasBred Boolean to change variable.
     */
    public void setHasBred(boolean hasBred) {
        this.hasBred = hasBred;
    }

    /**
     * Checks of this Animal is inside it's nest.
     *
     * @return Returns a boolean of whether it's true.
     */
    public boolean getIsInsideNest() {
        return this.isInsideNest;
    }

    /**
     * Gives energy of animal.
     *
     * @return Returns int amount of energy.
     */
    public int getEnergy() {
        return this.energy;
    }

    /**
     * removes a certain amount of energy
     *
     * @param amount positive int of how much energy needs to be removed
     */
    public void removeEnergy(int amount) {
        this.energy -= amount;
    }

    /**
     * Finds the nearest nest within the animals's vision range.
     *
     *
     * @return the nearest nest object, or null if none are found
     */
    protected AnimalNest findNearestNest() {
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
    protected void moveToOrDigHole() {
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
    protected void digHole() {
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

    /**
     * Makes Animal go inside hole if they are on their hole tile, otherwise
     * throws RunTimeException.
     */
    protected void enterHole() {
        if (world.getLocation(this).equals(world.getLocation(this.animalNest)) == false) {
            throw new RuntimeException("animals should only enter their own hole when standing on it");
        }
        this.isInsideNest = true;

        isSleeping = true;
        world.remove(this); // gaming time
    }

    /**
     * Makes animal exit hole if the hole tile is empty.
     */
    protected void exitHole() {
        Location holeLocation = world.getLocation(this.animalNest);
        if (world.isTileEmpty(holeLocation)) {
            isInsideNest = false;
            world.setTile(holeLocation, this);
        }
    }

    /**
     * Checks if animal energy is below 70 percent of max energy.
     *
     * @return Returns boolean if true otherwise false.
     */
    protected boolean isHungry() {
        if (energy < maxEnergy * 0.7) {
            return true;
        }
        return false;
    }

    /**
     * Checks if animal energy is below 20 percent of max energy.
     *
     * @return Returns boolean if true otherwise false.
     */
    protected boolean isStarving() {
        if (energy < maxEnergy * 0.2) {
            return true;
        }
        return false;
    }

    /**
     * Adds a certain amount of energy.
     *
     * @param energy int of amount of energy to add.
     */
    public void addEnergy(int energy) {
        this.energy += energy;
    }
}
