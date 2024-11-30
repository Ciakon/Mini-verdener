package animals;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
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
    int visionRange = 3;
    int maxEnergy = 200;
    int energy = 100;
    int energyLoss = 1;

    double digNewExitChance = 0.2;
    boolean isInsideRabbithole;
    RabbitHole rabbitHole;

    String imageKeyBaby = "rabbit-small";
    String imageKeyAdult = "rabbit-large";
    String imageKeySleepingBaby = "rabbit-small-sleeping";
    String imageKeySleepingAdult = "rabbit-sleeping";
    Color color = Color.gray;

    int nutritionalValueAdult = 50;
    int nutritionalValueBaby = 20;

    public Rabbit(World world, boolean isAdult, Location location) {
        super(world, false);

        this.isInsideRabbithole = false;
        world.setTile(location, this);
    }

    public Rabbit(World world, boolean isAdult, RabbitHole rabbitHole) {
        super(world, false);

        this.isInsideRabbithole = true;
        this.rabbitHole = rabbitHole;
        this.isSleeping = true;
        rabbitHole.addRabbit(this);
    }

    void generalAI() {
        if (world.isDay()) {
            isSleeping = false;
        }
        else {
            isSleeping = true;
        }

        if (world.isNight() && this.breedable && !this.hasBred && energy > 30 && this.isInsideRabbithole) {
            this.findBreedingPartner();
        }

        if (isInsideRabbithole == false) {
            previousPosition = world.getCurrentLocation();
        }
    }

    @Override
    void dayTimeAI() {
        this.eatIfOnGrass();
        DayTimeMovementAI();

        if (isInsideRabbithole) {
            exitHole(world);
        }   
    }

    @Override
    void nightTimeAI() {
        moveToOrDigHole();
    }

    /**
     * Makes Rabbit older. Adult rabbits get nerfed.
     *
     */
    @Override
    public void grow() {
        super.grow();
        if (age == adultAge) {
            this.maxEnergy *= 1.5;
            this.energyLoss *= 1.5;
        }
    }

    /**
     * breeds with other Rabbit_new
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
        for (Rabbit Rabbit_new : rabbitList) {
            if (Rabbit_new != this && Rabbit_new.breedable && !Rabbit_new.hasBred && Rabbit_new.isInsideRabbithole && Rabbit_new.energy > 30) {
                this.breed(Rabbit_new);
                break;
            }
        }
    }

    /**
     * if Rabbit_new is standing on grass, eat it
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
     * Moves Rabbit_new in direction of grass if seen otherwise in a random
     * direction
     *
     */
    public void DayTimeMovementAI() {
        ArrayList<Location> nearbyGrass = findGrass(world);
        if (!nearbyGrass.isEmpty()) {

            Location desiredGrass = nearestObject(world, nearbyGrass);
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
     * @param world
     * @return returns the closet grass to the Rabbit
     */
    public Location closetGrass() {
        ArrayList<Location> nearbyGrass = findGrass(world);
        Location closetGrass = null;
        if (!nearbyGrass.isEmpty()) {
            closetGrass = nearestObject(world, nearbyGrass);
        }
        return closetGrass;
    }

    /**
     * Finds nearby grass, that is not accoupied by other rabbits
     *
     * @param world world which the Rabbit_new is in
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
     * Finds nearest item in given ArrayList
     *
     * @param world world which the Rabbit_new is in
     * @param Arraylist Arraylist of all object locations seen nearby
     * @return returns location of the closest object
     */
    public Location nearestObject(World world, ArrayList<Location> object) {
        Location closest = object.get(0);
        for (Location location : object) {
            if (Functions.calculateDistance(world.getLocation(this), location) <Functions.calculateDistance(world.getLocation(this), closest)) {
                closest = location;
            }
        }
        return closest;
    }

    /**
     * Finds the nearest Rabbit_new hole within the Rabbit_new's vision range.
     *
     * @param world the world in which the Rabbit_new is located
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
     * Moves Rabbit_new 1 tile towards a given location
     *
     * @param world world which the Rabbit_new is in
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
     * Moves the Rabbit_new toward the nearest Rabbit_new hole using moveTowards() if
     * one exists. If no Rabbit_new hole is nearby, the Rabbit_new digs a new hole.
     *
     * @param world the world in which the Rabbit_new is located
     */
    public void moveToOrDigHole() {
        
        if (rabbitHole != null) {
            moveTowards(world, world.getLocation(rabbitHole));
            if (previousPosition.equals(world.getLocation(rabbitHole))) {
                enterHole(world);
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
            digHole(world);
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
     * Digs a new Rabbit_new hole at the rabbits location in the world. This method
     * sets the Rabbit_new's `rabbitHole` attribute to the new hole.
     *
     * @param world the world in which the Rabbit_new digs a hole
     */
    public void digHole(World world) {
        if (rabbitHole != null) {
            throw new RuntimeException("Rabbits should only dig a hole when they don't have one");
        }

        Location location = world.getLocation(this);

        if (world.getNonBlocking(location) != null) {
            return;
        }

        rabbitHole = new RabbitHole(world, location);
        rabbitHole.addRabbit(this);

        enterHole(world);
    }

    public void enterHole(World world) {
        if (world.getLocation(this).equals(world.getLocation(rabbitHole)) == false) {
            throw new RuntimeException("Rabbits should only enter their own hole when standing on it");
        }
        isInsideRabbithole = true;

        isSleeping = true;
        world.remove(this); // gaming time

         // TODO chance based so they can dig around tunnels?
    }

    public void exitHole(World world) {
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

    public int getEnergy() {
        return energy;
    }
}
