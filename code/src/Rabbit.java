
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

    boolean hasEaten;
    int age;
    String imageKey;
    int visionRange;
    boolean canBreed;
    boolean dailyEventTriggered;
    RabbitHole rabbitHole;

    boolean isAlive = true;

    public Rabbit(World world, Location location) {
        world.add(this);
        world.setTile(location, this);

        this.hasEaten = false;
        this.age = 0;
        this.imageKey = "rabbit-small";
        this.visionRange = 3;
        this.canBreed = false;
        this.dailyEventTriggered = false;
    }

    public void act(World world) {
        this.eatIfOnGrass(world);
        this.dailyReset(world);
        this.nightCheck(world);

        if (isAlive == false) {
            world.delete(this);
            return;
        }

        DayTimeMovementAI(world);
    }

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
            if (!this.hasEaten) {
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
        if (closetgrass == world.getLocation(this)) {
            if (world.getNonBlocking(closetgrass) instanceof Grass grass) {
                world.delete(grass);
                this.hasEaten = true;
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
            this.hasEaten = false;
            this.dailyEventTriggered = true;
        }
    }

    public Location closetGrass(World world) {
        ArrayList<Location> nearbyGrass = findGrass(world);
        Location closetGrass = null;
        if (!nearbyGrass.isEmpty()) {
            closetGrass = nearestGrass(world, nearbyGrass);
        }
        return closetGrass;
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
            if (!nearbyGrass.isEmpty() && !this.hasEaten) {

                Location desiredGrass = nearestGrass(world, nearbyGrass);
                moveTowards(world, desiredGrass);
            } else {

                Set<Location> freeLocations = world.getEmptySurroundingTiles(world.getLocation(this));
                Location nextLocation = randomLocation(freeLocations);
                world.move(this, nextLocation);
            }
        }
    }

    /**
     * Finds nearby grass, that is not accoupied by other rabbits
     *
     * @param world world which the rabbit is in
     * @return Arraylist of all grass locations seen nearby
     */
    public ArrayList<Location> findGrass(World world) {
        ArrayList<Location> nearbyGrass = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), this.visionRange); //Set of surrounding tiles within visionRange
        for (Location location : surroundings) {
            if (!world.isTileEmpty(location)) {
                if (world.getTile(location) instanceof Grass) { //will not be seen if any blocking object is currently on the tile
                    nearbyGrass.add(location);
                }
            }
        }
        return nearbyGrass;
    }

    /**
     * Finds nearest grass that is unoccupied
     *
     * @param world world which the rabbit is in
     * @param Arraylist Arraylist of all grass locations seen nearby
     */
    public Location nearestGrass(World world, ArrayList<Location> grass) {
        Location closest = grass.get(0);
        int highestValue;
        if (closest.getX() > closest.getY()) {
            highestValue = closest.getX();
        } else {
            highestValue = closest.getY();
        }
        for (Location location : grass) {
            if (location.getX() < highestValue && location.getY() < highestValue) {
                closest = location;
                if (location.getX() > location.getY()) {
                    highestValue = location.getX();
                } else {
                    highestValue = location.getY();
                }
            }
        }
        return closest;
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
     * chooses random location out of set of locations
     *
     * @param locationSet a Set<> of Location
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

    public void digHole(World world) {
        if (rabbitHole != null) {
            throw new RuntimeException("Rabbits should only dig a hole when they don't have one");
        }

        Location location = findRandomValidLocation(world);
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
