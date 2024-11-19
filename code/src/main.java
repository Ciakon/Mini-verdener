import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

class Main{
    public static void main(String[] args) {
        try {
            runSimulation("t1-1c");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    public static void runSimulation(String test) throws FileNotFoundException {
        Random RNG = new Random();

        File file = new File("data/" + test + ".txt");
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
                spawnActor(createActor(type), world);
            }

            System.out.println("Spawned: " + amount + " " + type);
        }
        scanner.close();

        program.show();
    }

    public static Actor createActor(String string) {
        switch (string) {
            case "grass":
                return new Grass();
            case "rabbit":
                return new Rabbit();
            default:
                throw new IllegalArgumentException(string + " is not a valid actor");
        }
    }

    // todo fix infinite loop
    public static void spawnActor(Actor actor, World world) {
        Random RNG = new Random();
        int N = world.getSize();

        while (true) {
            int x = RNG.nextInt(0, N);
            int y = RNG.nextInt(0, N);
            Location location = new Location(x, y);

            if (world.getTile(location) == null) {
                world.add(actor);
                world.setTile(location, actor);
                break;
            }
        }
    }
}