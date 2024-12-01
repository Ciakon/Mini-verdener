package animals;

import itumulator.world.Location;
import itumulator.world.World;
import plants.Grass;
import utils.Functions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import animals.nests.RabbitHole;

public class Rabbit extends Animal {
    double digNewExitChance = 0.2;
    boolean isInsideRabbithole;
    RabbitHole rabbitHole;
    int breedingEnergy = 15;

    Color color = Color.gray;

    /**
     * British method jumpscare
     */
    void rabbitInit() {
        imageKeyBaby = "rabbit-small";
        imageKeyAdult = "rabbit-large";
        imageKeySleepingBaby = "rabbit-small-sleeping";
        imageKeySleepingAdult = "rabbit-sleeping";

        visionRange = 4;
        maxEnergy = 30;
        energy = 15;
        energyLoss = 1;

        adultAge = 40;

        nutritionalValueAdult = 50;
        nutritionalValueBaby = 20;
    }

    /**
     * This constructor should be used whebn spawning a rabbit outside.
     * 
     * @param world The simulation world.
     * @param isAdult Whether to spawn the rabbit as an adult or baby.
     * @param location Where to spawn it.
     */
    public Rabbit(World world, boolean isAdult, Location location) {
        super(world, isAdult);

        this.isInsideRabbithole = false;
        world.setTile(location, this);

        rabbitInit();
    }

    /**
     * This constructor should be used whebn spawning a rabbit inside a rabbithole.
     * 
     * @param world The simulation world.
     * @param isAdult Whether to spawn the rabbit as an adult or baby.
     * @param location Where to spawn it.
     */
    public Rabbit(World world, boolean isAdult, RabbitHole rabbitHole) {
        super(world, isAdult);

        this.isInsideRabbithole = true;
        this.rabbitHole = rabbitHole;
        this.isSleeping = true;
        rabbitHole.addRabbit(this);

        rabbitInit();
    }

    void generalAI() {
        if (world.isDay()) {
            isSleeping = false;
        }

        if (world.isNight() && this.breedable && !this.hasBred && energy > breedingEnergy && this.isInsideRabbithole) {
            this.findBreedingPartner();
        }

        if (world.getCurrentTime() >= 7 && isInsideRabbithole == false) { // go back in the evening
            moveToOrDigHole();
        }

        if (isInsideRabbithole == false) {
            this.eatIfOnGrass();
            previousPosition = world.getLocation(this);
        }
    }

    @Override
    void dayTimeAI() {
        if (isInsideRabbithole && world.getCurrentTime() < 7) {
            exitHole(); // Not guranteed, rabbits may be in the way.
        }

        if (isInsideRabbithole == false) {
            DayTimeMovementAI();
        }
    }

    @Override
    void nightTimeAI() {
        
    }

    /**
     * Makes Rabbit older. Adult rabbits get nerfed.
     *
     */
    @Override
    public void grow() {
        super.grow();
        if (age == adultAge) {
            this.maxEnergy *= .75;
            this.energyLoss *= 1.5;
        }
    }

    /**
     * breeds with other rabbit
     * 
     * @param partner Rabbit to breed with
     */
    @Override
    void breed(Animal partner) {

        if (partner instanceof Rabbit == false) {
            throw new RuntimeException("bro?");
        }

        new Rabbit(world, false, this.rabbitHole);
        energy -= energyLoss * 2;
        hasBred = true;
    }

    /**
     * finds suitable rabbits to breed
     *
     */
    @Override
    public void findBreedingPartner() {
        ArrayList<Rabbit> rabbitList = this.rabbitHole.getAllRabbits();
        for (Rabbit rabbit : rabbitList) {
            if (rabbit != this && rabbit.breedable && !rabbit.hasBred && rabbit.isInsideRabbithole && rabbit.energy > breedingEnergy) {
                this.breed(rabbit);
                break;
            }
        }
    }

    /**
     * if Rabbit is standing on grass, eat it
     *
     *
     */
    public void eatIfOnGrass() {
        Location closetgrass = this.closetGrass();
        if (closetgrass == world.getLocation(this)) {
            if (world.getNonBlocking(closetgrass) instanceof Grass grass) {
                this.energy += grass.getNutritionalValue();
                if (this.energy > maxEnergy) {
                    this.energy = maxEnergy;
                }
                world.delete(grass);
            }
        }

    }

    /**
     * Moves rabbit in direction of grass if seen otherwise in a random
     * direction
     *
     */
    public void DayTimeMovementAI() {
        ArrayList<Location> nearbyGrass = findGrass(world);
        if (!nearbyGrass.isEmpty()) {

            Location desiredGrass = nearestObject(nearbyGrass);
            moveTowards(world, desiredGrass);
            this.energy -= (int) (energyLoss*0.1);
        } 
        else {

            Set<Location> freeLocations = world.getEmptySurroundingTiles(world.getLocation(this));
            Location nextLocation = randomLocation(freeLocations);
            if (freeLocations != null) {
                world.move(this, nextLocation);
                this.energy -= (int) (energyLoss*0.1);
            }
        }
    }

    /**
     *
     * @param world The simulation world.
     * @return returns the closet grass to the Rabbit
     */
    public Location closetGrass() {
        ArrayList<Location> nearbyGrass = findGrass(world);
        Location closetGrass = null;
        if (!nearbyGrass.isEmpty()) {
            closetGrass = nearestObject(nearbyGrass);
        }
        return closetGrass;
    }

    /**
     * Finds nearby grass, that is not accoupied by other rabbits
     *
     * @param world world which the rabbit is in
     * @return Arraylist of all grass locations seen nearby
     */
    public ArrayList<Location> findGrass(World world) {
        ArrayList<Location> nearbyGrass = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), this.visionRange);
        Location here = world.getLocation(this);
        if (world.containsNonBlocking(here) && world.getNonBlocking(here) instanceof Grass) {
            nearbyGrass.add(world.getLocation(this));
        }
        for (Location location : surroundings) {
            if (world.containsNonBlocking(location) && world.isTileEmpty(location)) {
                if (world.getNonBlocking(location) instanceof Grass) {
                    nearbyGrass.add(location);
                }
            }
        }
        return nearbyGrass;
    }


    /**
     * Finds the nearest rabbit hole within the rabbit's vision range.
     *
     * @param world the world in which the rabbit is located
     * @return the nearest RabbitHole object, or null if none are found
     */
    public RabbitHole findNearestRabbitHole(World world) {
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), this.visionRange);
        RabbitHole closestHole = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Location location : surroundings) {
            if (world.getTile(location) instanceof RabbitHole) {
                RabbitHole hole = (RabbitHole) world.getTile(location);
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
     * Moves rabbit 1 tile towards a given location
     *
     * @param world world which the rabbit is in
     * @param desiredlocation location to move towards
     */
    public void moveTowards(World world, Location desiredLocation) {
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
     * Moves the rabbit toward the nearest rabbit hole using moveTowards() if
     * one exists. If no rabbit hole is nearby, the rabbit digs a new hole.
     *
     * @param world the world in which the rabbit is located
     */
    public void moveToOrDigHole() {
        if (rabbitHole != null) {
            moveTowards(world, world.getLocation(rabbitHole));
            
            if (previousPosition.equals(world.getLocation(rabbitHole)) && world.getLocation(this).equals(previousPosition)) {
                enterHole();
            }
            return;
        }

        

        RabbitHole nearestHole = findNearestRabbitHole(world);

        if (nearestHole != null) {
            moveTowards(world, world.getLocation(nearestHole));
            if (world.getLocation(this).equals(world.getLocation(nearestHole))) {
                rabbitHole = nearestHole;
                nearestHole.addRabbit(this);
            }
        } else {
            digHole();
        }
    }

    /**
     * chooses random location out of set of locations
     *
     * @param locationSet a Set<> of Location. returns null if Set is empty
     */
    public Location randomLocation(Set<Location> LocationSet) {
        if (LocationSet.isEmpty()) {
            return null;
        }
        List<Location> LocationlList = new ArrayList<>(LocationSet);
        Random random = new Random();
        int randomIndex = random.nextInt(LocationlList.size());
        return LocationlList.get(randomIndex);
    }

    /**
     * Digs a new rabbit hole at the rabbits location in the world. This method
     * sets the rabbit's `rabbitHole` attribute to the new hole.
     *
     * @param world the world in which the rabbit digs a hole
     */
    public void digHole() {
        if (rabbitHole != null) {
            throw new RuntimeException("Rabbits should only dig a hole when they don't have one");
        }

        Location location = world.getLocation(this);

        if (world.getNonBlocking(location) != null) {
            return;
        }

        rabbitHole = new RabbitHole(world, location);
        rabbitHole.addRabbit(this);

        enterHole();
    }

    public void enterHole() {
        if (world.getLocation(this).equals(world.getLocation(rabbitHole)) == false) {
            throw new RuntimeException("Rabbits should only enter their own hole when standing on it");
        }
        isInsideRabbithole = true;

        isSleeping = true;
        world.remove(this); // gaming time

         // TODO chance based so they can dig around tunnels?
    }

    public void exitHole() {
        Random random = new Random();
        ArrayList<RabbitHole> exits = rabbitHole.getAllConnectedHoles();

        if (isInsideRabbithole == false) {
            throw new RuntimeException("nah");
        }
        else if (random.nextDouble() < digNewExitChance) {
            Location l = Functions.findRandomValidLocation(world);
            RabbitHole new_exit = new RabbitHole(world, l, exits);
            rabbitHole = new_exit;
        }
        else {
        // choose a random connected hole in the tunnel. That is now the new and only entrance (dementia moment).
            int hole_nr = random.nextInt(exits.size());
            RabbitHole new_exit = exits.get(hole_nr);
            rabbitHole = new_exit;
        }
    
        Location holeLocation = world.getLocation(rabbitHole);
        if (world.isTileEmpty(holeLocation)) {
            isInsideRabbithole = false;
            world.setTile(holeLocation, this);
        }
    }
}
