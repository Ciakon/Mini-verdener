package animals;

import java.awt.Color;
import java.util.ArrayList;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

abstract class Animal implements Actor, DynamicDisplayInformationProvider {
    int age;
    int adultAge = 6;

    int visionRange = 3;
    int maxEnergy = 200;
    int energy = 100;
    int energyLoss = 1;

    int nutritionalValue;
    int nutritionalValueAdult = 100;
    int nutritionalValueBaby = 30;

    boolean isSleeping = false;
    boolean hasBred = false;
    boolean breedable;
    boolean isAdult;

    String imageKeyBaby;
    String imageKeyAdult;
    String imageKeySleepingBaby;
    String imageKeySleepingAdult;
    Color color = Color.red;
    
    Location previousPosition;
    World world;

    public Animal(World world, boolean isAdult) {
        this.world = world;
        world.add(this);

        if (isAdult) {
            this.isAdult = true;
            this.age = adultAge;
            this.breedable = true;
            this.nutritionalValue = nutritionalValueAdult;
        }
        else {
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
        }
        else {
            nightTimeAI();
        }
        generalAI(); // Remember this runs last.

        if (world.getCurrentTime() == 0) {
            grow();
        }

        previousPosition = world.getCurrentLocation();

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
            }
            else {
                imageKey = imageKeySleepingBaby;
            }
        }
        else {
            if (isAdult) {
                imageKey = imageKeyAdult;
            }
            else {
                imageKey = imageKeyBaby;
            }
        }

        return new DisplayInformation(color, imageKey);
    }

    void die() {
        world.delete(this);
    }

    int kill(Animal prey) {
        int NV = prey.nutritionalValue;
        prey.die();
        return NV;
    }

    // Create the following AI methods in the animal
    abstract void generalAI();

    abstract void dayTimeAI();

    abstract void nightTimeAI();

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




    int getEnergy() {
        return this.energy;
    }





}
