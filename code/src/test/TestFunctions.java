package test;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.junit.Test;

import animals.Bear;
import animals.Rabbit;
import animals.Wolf;
import animals.nests.RabbitHole;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import plants.BerryBush;
import plants.Carcass;
import plants.Grass;
import utils.Functions;

/**
 * This class tests functions, that aren't connected to a specific class.
 */
public class TestFunctions {
    
    /**
     * Tests if every input file is properly loaded. The test checks if every entity in the input file is placed on a tile.
     */
    @Test
    public void testInputFiles() {

        // Load every file 10 times to be sure
        for (int i = 0; i < 10; i ++) {
            Program program = null;
            World world = null;

            try {
                program = Functions.createSimulation("t1-1a");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 3);

                program = Functions.createSimulation("t1-1b");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 1);

                program = Functions.createSimulation("t1-1c");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 20, 30);
                verifySpawns(world, Rabbit.class, 1);

                program = Functions.createSimulation("t1-2a");
                world = program.getWorld();
                verifySpawns(world, Rabbit.class, 1);

                program = Functions.createSimulation("t1-2b");
                world = program.getWorld();
                verifySpawns(world, Rabbit.class, 2);

                program = Functions.createSimulation("t1-2cde");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 3, 10);
                verifySpawns(world, Rabbit.class, 2, 4);

                program = Functions.createSimulation("t1-2fg");
                world = program.getWorld();
                verifySpawns(world, Rabbit.class, 4);

                program = Functions.createSimulation("t1-3a");
                world = program.getWorld();
                verifySpawns(world, RabbitHole.class, 1);
                verifySpawns(world, Rabbit.class, 5);

                program = Functions.createSimulation("t1-3b");
                world = program.getWorld();
                verifySpawns(world, RabbitHole.class, 1);
                verifySpawns(world, Rabbit.class, 5);


                program = Functions.createSimulation("t2-1ab");
                world = program.getWorld();
                verifySpawns(world, Wolf.class, 1);

                program = Functions.createSimulation("t2-1c");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 2, 7);
                verifySpawns(world, Rabbit.class, 2);
                verifySpawns(world, Wolf.class, 1);

                program = Functions.createSimulation("t2-2a");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 8, 15);
                verifySpawns(world, Rabbit.class, 2, 5);
                verifySpawns(world, Wolf.class, 3);

                program = Functions.createSimulation("t2-3a");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 8, 15);
                verifySpawns(world, Wolf.class, 6);

                program = Functions.createSimulation("t2-4a");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 10, 15);
                verifySpawns(world, Bear.class, 1);

                program = Functions.createSimulation("t2-4b");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 10, 15);
                verifySpawns(world, Rabbit.class, 5, 7);
                verifySpawns(world, Wolf.class, 2, 4);
                verifySpawns(world, Bear.class, 2);

                program = Functions.createSimulation("t2-5a");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 10, 15);
                verifySpawns(world, Rabbit.class, 5, 7);
                verifySpawns(world, Bear.class, 1);

                program = Functions.createSimulation("t2-6a");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 10, 15);
                verifySpawns(world, BerryBush.class, 7, 12);
                verifySpawns(world, Bear.class, 1);


                program = Functions.createSimulation("t3-1a");
                world = program.getWorld();
                verifySpawns(world, Carcass.class, 2, 5);

                program = Functions.createSimulation("t3-1b");
                world = program.getWorld();
                verifySpawns(world, Rabbit.class, 1, 2);
                verifySpawns(world, Wolf.class, 2);
                verifySpawns(world, Bear.class, 1);

                program = Functions.createSimulation("t3-1c");
                world = program.getWorld();
                verifySpawns(world, Rabbit.class, 1, 2);
                verifySpawns(world, Wolf.class, 1);

                program = Functions.createSimulation("t3-2ab");
                world = program.getWorld();
                verifySpawns(world, Grass.class, 10);
                verifySpawns(world, Carcass.class, 9, 14);
                verifySpawns(world, Rabbit.class, 2, 4);
                verifySpawns(world, Wolf.class, 2);
                verifySpawns(world, Bear.class, 2);

                // For t3-2ab we are also going check if 5-10 carcasses are infected with shrooms.
                ArrayList<Location> carcassLocations = Functions.findNearbyObjects(world, new Location(0, 0), Carcass.class, 1000);
                ArrayList<Carcass> carcasses = new ArrayList<>();

                for (Location location : carcassLocations) {
                    carcasses.add((Carcass) world.getTile(location));
                }

                int infectedCarcasses = 0;
                for (Carcass carcass : carcasses) {
                    if (carcass.getShroomStatus()) {
                        infectedCarcasses++;
                    }
                }
                assertTrue(infectedCarcasses >= 5 && infectedCarcasses <= 10);


            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found: " + e.getMessage());
            }
        }
    }

    /**
     * Finds all "type" objects in the world and verifies whether it mathces the presumed min and max.
     * @param world The simulation world
     * @param type The type of object to verify
     * @param min The minimum amount of objects there should be in the world
     * @param max The maximum amount of objects there should be in the world
     */
    <T> void verifySpawns(World world, Class<T> type, int min, int max) {
        
        int count = Functions.findNearbyObjects(world, new Location(0, 0), type, 1000).size();
        assertTrue(count >= min && count <= max);
    }

    /**
     * Finds all "type" objects in the world and verifies whether it mathces the presumed amount.
     * @param world The simulation world
     * @param type The type of object to verify
     * @param amount The amount of objects there should be in the world
     */
    <T> void verifySpawns(World world, Class<T> type, int amount) {
        verifySpawns(world, type, amount, amount);
    }
}
