package animals;

import animals.nests.RabbitHole;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import plants.Grass;
import plants.Plant;
import utils.Functions;

/**
 * The {@code Rabbit} class is a herbivorous animal in the simulation that engages in
 * activities such as foraging for plants, breeding, and digging/inhabiting rabbit holes.
 * Rabbits can grow, age, and interact with their environment dynamically, including the ability
 * to migrate to new holes or create their own.
 * <p>
 * This class implements the {@link Herbivorous} interface to define its plant-eating behavior
 * and extends the {@link Animal} base class to inherit common animal behaviors.
 * </p>
 */
public class Rabbit extends Animal implements Herbivorous {

    double digNewExitChance = 0.2;
    RabbitHole rabbitHole;
    int breedingEnergy = 15;

    /**
     * Initializes the rabbit's attributes, such as vision range, energy levels,
     * preferred plants, and appearance.
     */
    void rabbitInit() {
        imageKeyBaby = "rabbit-small";
        imageKeyAdult = "rabbit-large";
        imageKeySleepingBaby = "rabbit-small-sleeping";
        imageKeySleepingAdult = "rabbit-sleeping";
        color = Color.blue;

        visionRange = 4;
        maxEnergy = 40;
        energy = 15;
        energyLoss = 1;

        adultAge = 40;

        nutritionalValueAdult = 500;
        nutritionalValueBaby = 200;

        if (isAdult) {
            this.maxEnergy *= .75;
        }
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

        this.isInsideNest = false;
        world.setTile(location, this);

        rabbitInit();
    }

    /**
     * This constructor should be used whebn spawning a rabbit inside a
     * rabbithole.
     *
     * @param world The simulation world.
     * @param isAdult Whether to spawn the rabbit as an adult or baby.
     * @param location Where to spawn it.
     */
    public Rabbit(World world, boolean isAdult, RabbitHole rabbitHole) {
        super(world, isAdult);

        this.isInsideNest = true;
        this.rabbitHole = rabbitHole;
        this.isSleeping = true;
        rabbitHole.addRabbit(this);

        rabbitInit();
    }

    /**
     * Executes general AI behavior for the rabbit, checking for a new hole
     * to migrate to if it is the sole inhabitant of its current hole.
     */
    @Override
    void generalAI() {
        if (isInsideNest == false) {
            if (rabbitHole != null && rabbitHole.getAllRabbits().size() == 1) {

                RabbitHole newHole = checkForNewHole(1);
                if (newHole != null) {
                    rabbitHole.removeRabbit(this);
                    newHole.addRabbit(this);
                    rabbitHole = newHole;
                }
            }
        }

    }

    /**
     * Defines the rabbit's daytime behavior, exiting its hole, foraging, and returning
     * to its hole in the evening.
     */
    @Override
    void dayTimeAI() {

        hasBred = false;
        isSleeping = false;

        if (isInsideNest && world.getCurrentTime() < 7) {
            exitHole(); // Not guranteed, rabbits may be in the way.
        }

        if (isInsideNest == false) {
            if (world.getCurrentTime() >= 7) { // go back in the evening
                moveToOrDigHole();
            } else {
                forage();
            }
        }
    }

    /**
     * Defines the rabbit's nighttime behavior, attempting to breed and finding a hole
     * if it is outside.
     */
    @Override
    void nightTimeAI() {
        if (!this.hasBred && energy > breedingEnergy && this.isInsideNest) {
            this.findBreedingPartner();
        }
        if (isInsideNest == false) {
            moveToOrDigHole();
        }

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
        this.energy -= breedingEnergy;
        this.hasBred = true;
        partner.setHasBred(true);
        partner.removeEnergy(breedingEnergy);
    }

    /**
     * finds suitable rabbits to breed
     *
     */
    @Override
    public void findBreedingPartner() {
        ArrayList<Rabbit> rabbitList = this.rabbitHole.getAllRabbits();
        for (Rabbit rabbit : rabbitList) {
            if (this.isAdult && rabbit != this && !rabbit.hasBred && rabbit.isInsideNest && rabbit.energy > breedingEnergy) {
                this.breed(rabbit);
                break;
            }
        }
    }

    /**
     * Finds all nearby grass within the vision range. Overidden from default method.
     *
     * @return An ArrayList of nearby grass locations.
     */
    @Override
    public ArrayList<Plant> findNearbyPlants() {
        Animal me = (Animal) this;
        World world = me.world;
        ArrayList<Plant> nearbyPlants = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(me), me.visionRange);

        if (world.getTile(world.getLocation(me)) instanceof Grass plant) {
            nearbyPlants.add(plant);
        }

        for (Location location : surroundings) {
            if (world.getNonBlocking(location) instanceof Grass plant) {
                nearbyPlants.add(plant);
            }
        }

        return nearbyPlants;
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
     * Moves the rabbit toward the nearest rabbit hole using moveTowards() if
     * one exists. If no rabbit hole is nearby, the rabbit digs a new hole.
     *
     * @param world the world in which the rabbit is located
     */
    public void moveToOrDigHole() {
        if (rabbitHole != null) {
            moveTowards(world.getLocation(rabbitHole));
            
            if (previousPosition != null && previousPosition.equals(world.getLocation(rabbitHole)) && world.getLocation(this).equals(previousPosition)) {
                enterHole();
            }
            return;
        }

        RabbitHole nearestHole = findNearestRabbitHole(world);

        if (nearestHole != null) {
            moveTowards(world.getLocation(nearestHole));
            if (world.getLocation(this).equals(world.getLocation(nearestHole))) {
                rabbitHole = nearestHole;
                nearestHole.addRabbit(this);
            }
        } else {
            wander();
            digHole();
        }
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

    /**
     * Makes the rabbit enter its current rabbit hole.
     */
    public void enterHole() {
        if (world.getLocation(this).equals(world.getLocation(rabbitHole)) == false) {
            throw new RuntimeException("Rabbits should only enter their own hole when standing on it");
        }
        isInsideNest = true;
        isSleeping = true;
        world.remove(this); // gaming time
    }

    /**
     * Makes the rabbit exit its current rabbit hole, possibly creating a new exit.
     */
    public void exitHole() {
        Random random = new Random();
        ArrayList<RabbitHole> exits = rabbitHole.getAllConnectedHoles();

        if (isInsideNest == false) {
            throw new RuntimeException("nah");
        } else if (random.nextDouble() < digNewExitChance) {
            Location l = Functions.findRandomValidLocation(world);
            RabbitHole new_exit = new RabbitHole(world, l, exits);
            rabbitHole = new_exit;
        } else {
            // choose a random connected hole in the tunnel. That is now the new and only entrance (dementia moment).
            int hole_nr = random.nextInt(exits.size());
            RabbitHole new_exit = exits.get(hole_nr);
            rabbitHole = new_exit;
        }

        Location holeLocation = world.getLocation(rabbitHole);
        if (world.isTileEmpty(holeLocation)) {
            isInsideNest = false;
            world.setTile(holeLocation, this);
        }
    }

    /**
     * The rabbit looks for a new hole to migrate to. The hole must be in the
     * rabbit's vision range.
     *
     * @param minRabbits The minimum amount of rabbits in the new hole, for it
     * to be considered.
     * @return Returns the new hole. Returns null if no such hole is found.
     */
    RabbitHole checkForNewHole(int minRabbits) {
        RabbitHole rh = null;
        ArrayList<RabbitHole> validHoles = new ArrayList<>();

        // find valid holes
        Set<Location> locations = world.getSurroundingTiles(world.getLocation(this), visionRange);
        for (Location location : locations) {
            Object tile = world.getTile(location);
            if (tile instanceof RabbitHole == false) {
                continue;
            }

            if (tile.equals(rabbitHole)) {
                continue;
            }

            if (((RabbitHole) tile).getAllRabbits().size() >= minRabbits) {
                validHoles.add((RabbitHole) tile);
            }
        }

        if (validHoles.size() == 0) {
            return null;
        }

        // find the closest hole
        rh = validHoles.get(0);
        for (RabbitHole hole : validHoles) {
            int currentDistance = Functions.calculateDistance(world.getLocation(this), world.getLocation(rh));
            int newDistance = Functions.calculateDistance(world.getLocation(this), world.getLocation(hole));
            if (newDistance < currentDistance) {
                rh = hole;
            }
        }

        return rh;
    }

    /**
     * Retrieves the rabbit's current rabbit hole.
     *
     * @return The rabbit's associated {@link RabbitHole}, or {@code null} if the rabbit does not have one.
     */
    public RabbitHole getRabbitHole() {
        return rabbitHole;
    }

    /**
     * Sets the rabbit's current rabbit hole.
     *
     * @param rh The new {@link RabbitHole} to associate with the rabbit.
     */
    public void setRabbitHole(RabbitHole rh) {
        rabbitHole = rh;
    }
}
