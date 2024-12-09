package utils;

import animals.AlphaWolf;
import animals.Bear;
import animals.Rabbit;
import animals.Wolf;
import animals.nests.RabbitHole;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import plants.BerryBush;
import plants.Grass;

public class Functions {

    /**
     * Creates the simulation from a text file.
     *
     * @param filename The filename of where the simulation parameters are
     * stored.
     * @return Program The simulation program.
     * @throws FileNotFoundException Happens when the file can't be found.
     */
    public static Program createSimulation(String filename) throws FileNotFoundException {
        Random RNG = new Random();

        File file = new File("data/" + filename + ".txt");
        Scanner scanner = new Scanner(file);

        int N = Integer.parseInt(scanner.nextLine());
        System.out.println("World size: " + N);

        Program program = new Program(N, 1200, 200);
        World world = program.getWorld();

        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            String[] args = line.split(" ");

            String type = args[0];
            String amountSTR = args[1];

            int amount = 0;
            if (amountSTR.contains("-")) { //min-max
                int min = Integer.parseInt(amountSTR.split("-")[0]);
                int max = Integer.parseInt(amountSTR.split("-")[1]);

                amount = RNG.nextInt(min, max + 1);
            } else {
                amount = Integer.parseInt(amountSTR);
            }

            // spawn wolves in pack
            if (args[0].equals("wolf") && amount > 1) {
                createWolfPack(world, amount);
                continue;
            }

            for (int i = 0; i < amount; i++) {
                // When given bear territory position
                if (args.length == 3 && args[0].equals("bear")) {

                    String[] coordinates = args[2].strip().replaceAll("[()]", "").split(",");
                    Location territory = new Location(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));

                    createActor(world, type, territory);
                } else {
                    createActor(world, type);
                }

            }

            System.out.println("Spawned: " + amount + " " + type);
        }
        scanner.close();
        return program;
    }

    /**
     * Creates actors in the world.
     *
     * @param world The world where the actors spawn.
     * @param actor_name The name of the actor in the text file.
     * @return The actor object.
     */
    public static Actor createActor(World world, String actor_name) {
        Location location = findRandomValidLocation(world);

        switch (actor_name) {
            case "grass":
                return new Grass(world, location);
            case "rabbit":
                return new Rabbit(world, true, location);
            case "bear":
                return new Bear(world, true, location);
            case "wolf":
                return new Wolf(world, true, location);
            case "burrow":
                return new RabbitHole(world, location);
            case "berry":
                return new BerryBush(world, location);
            default:
                throw new IllegalArgumentException(actor_name + " is not a valid actor");
        }
    }

    /**
     * Spawns actor with a territory
     *
     * @param world The world where the actors spawn.
     * @param actor_name The name of the actor in the text file.
     * @param territory The actors territory.
     * @return The actor object.
     */
    public static Actor createActor(World world, String actor_name, Location territory) {
        Location location = territory;

        switch (actor_name) {
            case "bear":
                return new Bear(world, true, location, territory);
            default:
                throw new IllegalArgumentException(actor_name + " is not a valid actor");
        }
    }

    /**
     * Spawns actors in a pack
     *
     * @param world The world where the actors spawn.
     * @param actor_name The name of the actor in the text file.
     * @param amount Amount of animals in the pack
     * @return Returns the wolf pack list.
     */
    public static ArrayList<Wolf> createWolfPack(World world, int amount) {

        // spawn alpha wolf
        ArrayList<Wolf> pack = new ArrayList<>();

        Location location = findRandomValidLocation(world);
        AlphaWolf alpha = new AlphaWolf(world, true, location);
        alpha.addToPack(pack);

        // spawn the betas around it.
        for (int i = 1; i < amount; i++) {
            location = alpha.randomFreeLocation();
            if (location == null) {
                location = findRandomValidLocation(world);
            }

            Wolf wolf = new Wolf(world, true, location);
            wolf.addToPack(pack);
            wolf.setAlpha(alpha);
        }
        return pack;
    }

    /**
     * Finds a random empty location in the world
     *
     * @param world The simulation world
     * @return Location The random location.
     */
    // todo infinte loop glitch, L
    public static Location findRandomEmptyLocation(World world) {
        Random RNG = new Random();
        int N = world.getSize();

        while (true) {
            int x = RNG.nextInt(0, N);
            int y = RNG.nextInt(0, N);
            Location location = new Location(x, y);

            if (world.isTileEmpty(location)) {
                return location;
            }
        }
    }

    /**
     * Finds a random location in the world, that is completely empty (no
     * non-blocking object)
     *
     * @param world The simulation world
     * @return Location The random valid location.
     */
    // todo infinte loop glitch, L
    public static Location findRandomValidLocation(World world) {
        while (true) {
            Location location = findRandomValidLocation(world);

            if (world.getTile(location) == null) {
                return location;
            }
        }
    }

    
    

    /**
     * Calculates the Manhattan distance between two locations.
     *
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the Manhattan distance between loc1 and loc2
     */
    public static int calculateDistance(Location loc1, Location loc2) {
        return Math.abs(loc1.getX() - loc2.getX()) + Math.abs(loc1.getY() - loc2.getY());
    }

    public static void swapObjects(World world, Object o1, Object o2) {
        int x1 = world.getLocation(o1).getX();
        int y1 = world.getLocation(o1).getY();
        int x2 = world.getLocation(o2).getX();
        int y2 = world.getLocation(o2).getY();

        Location l1 = new Location(x1, y1);
        Location l2 = new Location(x2, y2);

        world.remove(o1);
        world.move(o2, l1);
        world.setTile(l2, o1);
    }

    /**
     * finds all nearby objects, of certain type, within visionRange of a
     * certain location
     *
     * @return an ArrayList of all nearby objects
     */
    public static <T> ArrayList<Location> findNearbyObjects(World world, Location myLocation, Class<T> type, int range) {
        ArrayList<Location> nearbyObjects = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(myLocation, range);
        if (!surroundings.isEmpty()) {
            for (Location location : surroundings) {
                if (type.isInstance(world.getTile(location))) {
                    nearbyObjects.add((Location) location);
                }
            }
        }
        return nearbyObjects;
    }
}
