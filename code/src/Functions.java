import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public class Functions {    
    
    /** 
     * @param filename
     * @return Program
     * @throws FileNotFoundException
     */
    public static Program createSimulation(String filename) throws FileNotFoundException {
        Random RNG = new Random();

        File file = new File("data/" + filename + ".txt");
        Scanner scanner = new Scanner(file);

        int N = Integer.parseInt(scanner.nextLine());
        System.out.println("World size: " + N);

        Program program = new Program(N, 700, 200);
        World world = program.getWorld();

        while (scanner.hasNext()) {
            String type = scanner.next();
            String amountSTR = scanner.next();

            int amount = 0;
            if (amountSTR.contains("-")) { //min-max
                int min = Integer.parseInt(amountSTR.split("-")[0]);
                int max = Integer.parseInt(amountSTR.split("-")[1]);

                amount = RNG.nextInt(min, max + 1);
            }
            else {
                amount = Integer.parseInt(amountSTR);
            }

            for (int i = 0; i < amount; i++) {
                createActor(world, type);
            }

            System.out.println("Spawned: " + amount + " " + type);
        }
        scanner.close();
        return program;
    }

    public static Actor createActor(World world, String actor_name) {
        Location location = findRandomValidLocation(world);

        switch (actor_name) {
            case "grass":
                return new Grass(world, location);
            case "rabbit":
                return new Rabbit(world, location);
            case "burrow":
                return new RabbitHole(world, location);
            default:
                throw new IllegalArgumentException(actor_name + " is not a valid actor");
        }
    }

    // todo infinte loop glitch, L
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
