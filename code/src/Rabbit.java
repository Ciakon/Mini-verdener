
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

public class Rabbit implements Actor, DynamicDisplayInformationProvider {

    // boolean hasEaten;
    int age;
    String imageKey;
    int visionRange;
    boolean canBreed;
    boolean hasBreed;
    boolean dailyEventTriggered;
    RabbitHole rabbitHole;
    int energy;
    int maxEnergy;

    boolean isAlive = true;

    public Rabbit(World world, Location location) {
        world.add(this);
        world.setTile(location, this);
        this.maxEnergy = 150;
        this.energy = 0;
        // this.hasEaten = false;
        this.age = 0;
        this.imageKey = "rabbit-small";
        this.visionRange = 4;
        this.canBreed = false;
        this.dailyEventTriggered = false;
    }

    @Override
    public void act(World world) {
        this.eatIfOnGrass(world);
        this.dailyReset(world);
        this.nightCheck(world);
        if (isAlive == false) {
            world.delete(this);
            return;
        }
        if (world.isNight() && this.canBreed && !this.hasBreed && energy > 30) {
            findBreedingPartner(world);
        }
        if (world.isNight()) {
            if (rabbitHole == null) {
                moveToOrDigHole(world);
            } else {
                moveTowards(world, world.getLocation(rabbitHole));
            }
            return;
        }
        DayTimeMovementAI(world);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, this.imageKey);
    }

    /**
     * removes rabbit if they have not eaten
     *
     * @param world
     */
    public void nightCheck(World world) {
        if (world.isNight() && this.dailyEventTriggered) {
            this.energy -= 50;
            if (this.energy < 0) {
                isAlive = false;
            }
            this.dailyEventTriggered = false;
        }
    }

    /**
     * Makes rabbit older. Rabbits that are 6 years get attribute changes
     *
     */
    public void grow() {
        this.age++;
        if (this.age == 6) {
            this.imageKey = "rabbit-large";
            this.visionRange = 2;
            this.canBreed = true;
            this.maxEnergy = 100;
        }
    }

    /**
     * breeds with other rabbit
     *
     * @param world world which the rabbit is in
     * @param rabbit rabbit to breed with
     */
    public void breed(World world, Rabbit rabbit) {
        this.hasBreed = true;
        rabbit.hasBreed = true;
        Set<Location> freeLocations = world.getEmptySurroundingTiles(world.getLocation(rabbit));
        Location rabbitSpawnPoint = randomLocation(freeLocations);
        Rabbit child = new Rabbit(world, world.getLocation(rabbitSpawnPoint));
        child.rabbitHole = this.rabbitHole;
        this.energy -= 30;
        rabbit.energy -= 30;
    }

    /**
     * finds suitable rabbits to breed
     *
     * @param world world which the rabbit is in
     */
    public void findBreedingPartner(World world) {
        if (world.getLocation(this) == world.getLocation(this.rabbitHole)) {
            ArrayList rabbitList = this.rabbitHole.getAllRabbits();
            for (Object object : rabbitList) {
                if (object instanceof Rabbit rabbit) {
                    if (rabbit != this && rabbit.canBreed && !rabbit.hasBreed && world.getLocation(rabbit) == world.getLocation(rabbit.rabbitHole) && energy > 30) {
                        this.breed(world, rabbit);
                    }
                }
            }
        }
    }

    /**
     * if Rabbit is standing on grass, eat it
     *
     * @param world world which the rabbit is in
     *
     */
    public void eatIfOnGrass(World world) {
        Location closetgrass = this.closetGrass(world);
        System.out.println("closets grass:" + closetgrass);
        if (closetgrass == world.getLocation(this)) {
            if (world.getNonBlocking(closetgrass) instanceof Grass grass) {
                this.energy += grass.getNutritionalValue();
                if (this.energy > maxEnergy) {
                    this.energy = maxEnergy;
                }
                world.delete(grass);
                System.out.println("eating grass");
                // this.hasEaten = true;
            }
        }

    }

    /**
     * once per day resets hasEaten to false and sets dailyEventTriggered to
     * true
     *
     * @param world world which the rabbit is in
     */
    public void dailyReset(World world) {
        if (world.isDay() && !this.dailyEventTriggered) {
            grow();
            // this.hasEaten = false;
            this.energy = 0;
            this.dailyEventTriggered = true;
        }
    }

    /**
     * Moves rabbit in direction of grass if seen otherwise in a random
     * direction
     *
     * @param world world which the rabbit is in
     */
    public void DayTimeMovementAI(World world) {
        if (world.isDay()) {

            ArrayList<Location> nearbyGrass = findGrass(world);
            if (!nearbyGrass.isEmpty()) {

                Location desiredGrass = nearestObject(world, nearbyGrass);
                moveTowards(world, desiredGrass);
                System.out.println("moving towards grass");
                this.energy -= 2;
            } else {

                Set<Location> freeLocations = world.getEmptySurroundingTiles(world.getLocation(this));
                Location nextLocation = randomLocation(freeLocations);
                if (freeLocations != null) {
                    world.move(this, nextLocation);
                    this.energy -= 2;
                    System.out.println("moving randomly");
                }
            }
        }
    }

    /**
     *
     * @param world
     * @return returns the closet grass to the Rabbit
     */
    public Location closetGrass(World world) {
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
     * Finds nearest item in given ArrayList
     *
     * @param world world which the rabbit is in
     * @param Arraylist Arraylist of all object locations seen nearby
     * @return returns location of the closest object
     */
    public Location nearestObject(World world, ArrayList<Location> object) {
        Location closest = object.get(0);
        for (Location location : object) {
            if (calculateDistance(world.getLocation(this), location) < calculateDistance(world.getLocation(this), closest)) {
                closest = location;
            }
        }
        return closest;
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
                int distance = calculateDistance(world.getLocation(this), location);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestHole = hole;
                }
            }
        }
        return closestHole;
    }

    /**
     * Moves Rabbit 1 tile towards a given location
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
        world.move(this, movement);
    }

    /**
     * Moves the rabbit toward the nearest rabbit hole using moveTowards() if
     * one exists. If no rabbit hole is nearby, the rabbit digs a new hole.
     *
     * @param world the world in which the rabbit is located
     */
    public void moveToOrDigHole(World world) {
        RabbitHole nearestHole = findNearestRabbitHole(world);
        if (nearestHole != null) {
            moveTowards(world, world.getLocation(nearestHole));
        } else {
            digHole(world);
        }
    }

    /**
     * Calculates the Manhattan distance between two locations.
     *
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the Manhattan distance between loc1 and loc2
     */
    public int calculateDistance(Location loc1, Location loc2) {
        return Math.abs(loc1.getX() - loc2.getX()) + Math.abs(loc1.getY() - loc2.getY());
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
    public void digHole(World world) {
        if (rabbitHole != null) {
            throw new RuntimeException("Rabbits should only dig a hole when they don't have one");
        }

        Location location = world.getLocation(this);
        rabbitHole = new RabbitHole(world, location);
        rabbitHole.addRabbit(this);
    }

    // TODO move to "functions" file. and fix infinite loop glitch.
    // TODO duplicate function from main file.
    public static Location findRandomValidLocation(World world) {
        Random RNG = new Random();
        int N = world.getSize();

        while (true) {
            int x = RNG.nextInt(0, N);
            int y = RNG.nextInt(0, N);
            Location location = new Location(x, y);

            if (world.getTile(location) == null) {
                return location;
            }
        }
    }

}
