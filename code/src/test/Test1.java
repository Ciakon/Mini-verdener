
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Test1 {

    @Test
    public void t1_1a() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-1a");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // check if grass spawns
        int expected_grass_count = 3;
        World world = program.getWorld();

        int grass_count = 0;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                grass_count++;
            }
        }

        assertEquals(expected_grass_count, grass_count);
    }

    @Test
    public void t1_2a() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-2a");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // check if rabbit spawns
        int expected_rabbit_count = 1;
        World world = program.getWorld();

        int rabbit_count = 0;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                rabbit_count++;
            }
        }

        assertEquals(expected_rabbit_count, rabbit_count);
    }

    @Test
    public void t1_2b() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-2cde");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        World world = program.getWorld();
        int initial_rabbit_count = 0;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                initial_rabbit_count++;
            }
        }
        for (int i = 0; i < 10; i++) { //simulate till night
            program.simulate();
        }
        entities.clear();
        entities = world.getEntities(); //update HashMap
        int current_rabbit_count = 0;
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                current_rabbit_count++;
            }
        }

        assertTrue(current_rabbit_count < initial_rabbit_count);
        // System.err.println(initial_rabbit_count);
        // System.err.println(current_rabbit_count);
        // System.out.println();
    }

    @Test
    public void t1_2c() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-2cde");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        World world = program.getWorld();
        int initial_rabbit_count = 0;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                initial_rabbit_count++;
            }
        }
        for (int i = 0; i < 20; i++) {
            program.simulate();
        }
        entities.clear();
        entities = world.getEntities(); //update HashMap
        int current_rabbit_count = 0;
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                current_rabbit_count++;
            }
        }

        assertTrue(current_rabbit_count == initial_rabbit_count);
    }

    // TODO this test may break if digging holes requires energy in the future.
    @Test
    public void t1_2fg() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-2fg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        World world = program.getWorld();

        ArrayList<Rabbit> rabbits = new ArrayList<>();
        ArrayList<RabbitHole> rabbitholes = new ArrayList<>();

        // simulate 100 steps
        for (int i = 0; i < 100; i++) {

            // here we find all rabbits and rabbitholes.
            Map<Object, Location> entities = world.getEntities();
            for (Object entity : entities.keySet()) {
                if (entity instanceof Rabbit) {
                    rabbits.add((Rabbit) entity);
                    ((Rabbit) entity).isImmortal = true; // since rabbits die without food, we simply remove their mortality.
                }
                if (entity instanceof RabbitHole) {
                    rabbitholes.add((RabbitHole) entity);
                }
            }

            // for the firt day, there should be no rabbitholes
            if (i < 10) {
                assertEquals(0, rabbitholes.size());
            } // after that, there should be at least one rabbithole.
            else {
                assertTrue(rabbitholes.size() > 0);
            }

            // Check if rabbits go towards their holes at night
            if (world.isNight()) {
                for (Rabbit rabbit : rabbits) {

                    // check if they be sleeping when in hole
                    // TODO may break if sleeping becomes chance based.
                    if (rabbit.isInsideRabbithole) {
                        assertTrue(rabbit.isSleeping);
                        continue;
                    }
                    if (rabbit.rabbitHole == null) continue;

                    Location rabbitLocation = world.getLocation(rabbit);
                    Location holeLocation = world.getLocation(rabbit.rabbitHole);

                    if (rabbitLocation.equals(holeLocation)) {
                        continue;
                    }

                    int distance = calculateDistance(rabbitLocation, holeLocation);
                    int previousDistance = calculateDistance(rabbit.previousPosition, holeLocation);

                    // check if the rabbit moved closer to the hole.
                    if (rabbitLocation.equals(rabbit.previousPosition) == false) {
                        System.out.println("gaming");
                        assertTrue(distance < previousDistance);
                    }

                }
            }

            program.simulate();
        }
        

    }

    @Test
    public void t1_3a() {
        // Rabbits digging holes has already been tested in t1_2fg, so it will not be tested here.

        Program program = null;
        // load input file.
        try {
            program = Functions.createSimulation("t1-3a");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // check if hole spawns
        int expected_hole_count = 1;
        World world = program.getWorld();

        int hole_count = 0;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof RabbitHole) {
                hole_count++;
            }
        }

        assertEquals(expected_hole_count, hole_count);
    }

    // TODO move to functions file instead of duplicating.
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

    @Test
    public void tf1_1() {
        Program program = null;
        // load input file.
        try {
            program = Functions.createSimulation("tf1-1");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        World world = program.getWorld();

        ArrayList<Rabbit> rabbits = new ArrayList<>();
        ArrayList<RabbitHole> rabbitholes = new ArrayList<>();

        Map<Rabbit, RabbitHole> holeOwners = new HashMap<>();


        int newExitsCount = 0;
        // simulate 100 steps
        for (int i = 0; i < 100; i++) {

            // here we find all rabbits and rabbitholes.
            Map<Object, Location> entities = world.getEntities();
            for (Object entity : entities.keySet()) {
                if (entity instanceof Rabbit) {
                    rabbits.add((Rabbit) entity);
                    ((Rabbit) entity).isImmortal = true; // since rabbits die without food, we simply remove their mortality.
                }
                if (entity instanceof RabbitHole) {
                    rabbitholes.add((RabbitHole) entity);
                }
            }

            // map each rabbit to their hole right before day.
            if (world.getCurrentTime() == 19) {
                for (Rabbit rabbit : rabbits) {
                    holeOwners.put(rabbit, rabbit.rabbitHole);
                }
            }
            
            // when they exit at day, check if they exited at a new hole.
            if (world.getCurrentTime() == 0) {
                for (Rabbit rabbit : rabbits) {
                    if (rabbit.rabbitHole == null) continue;

                    if (rabbit.rabbitHole.equals(holeOwners.get(rabbit)) == false) {
                        newExitsCount++;
                        assertTrue(rabbit.rabbitHole.connectedHoles.contains(holeOwners.get(rabbit)));
                    }
                }
            }

            program.simulate();
        }

        // There is high probability that at least one rabbit dug a new hole at night.
        assertTrue(newExitsCount > 0);
    }
}
