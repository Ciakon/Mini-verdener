import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import static org.junit.jupiter.api.Assertions.*;

public class Test1 {

    /**
     * Tests if grass properly spawns from input file.
     */

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

    /**
     * Tests if grass can spread.
     * This test ensures that after simulation steps, the number of grass entities increases as grass spreads.
     */

    @Test
    public void t1_1b() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-1b");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // check if grass spawns
        World world = program.getWorld();

        int grass_count = 1;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                grass_count++;
            }
        }

        // Simulate so it can grow
        for (int i = 0; i < 100; i++) {
            program.simulate();
        }

        // count the grass growing
        int finalGrassCount = 0;
        entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                finalGrassCount++;
            }
        }

        assertTrue(finalGrassCount > grass_count);
    }

    /**
     * Tests if rabbit can stand on grass.
     * This test verifies that when a rabbit is placed on grass, the grass's location and state remain unchanged.
     */

    @Test
    public void t1_1c() {
        Program program = null;
        try {
            program = Functions.createSimulation("t1-1c");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        World world = program.getWorld();

        // Find grass and rabbit
        Grass grass = null;
        Rabbit rabbit = null;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass && grass == null) {
                grass = (Grass) entity;
            }
            if (entity instanceof Rabbit && rabbit == null) {
                rabbit = (Rabbit) entity;
            }
        }

        //check if grass and rabbit is there then put the rabbit on the grass
        assertNotNull(grass);
        assertNotNull(rabbit);

        Location initialGrassLocation = world.getLocation(grass);

        world.move(rabbit,world.getLocation(grass));

        assertEquals(initialGrassLocation, world.getLocation(grass));
        assertEquals(initialGrassLocation, world.getLocation(rabbit));

    }



    /**
     * Tests if rabbits properly spawn from input file.
     */

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
            program = Functions.createSimulation("t1-2b");
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
    }

    @Test
    public void t1_2c() {
        int young_rabbit_energy = 0;
        int old_rabbit_energy = 0;
        Program program = null;
        for(int i =0;i<20;i++) {
            // load input file.
            try {
                program = Functions.createSimulation("t1-2cde");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            assert program != null;
            World world = program.getWorld();
            Map<Object, Location> entities = world.getEntities();
            for (int j = 0; j < 20; j++) {
                program.simulate();
                for (Object entity : entities.keySet()) {
                    if (entity instanceof Rabbit) {
                        ((Rabbit) entity).isImmortal = true;
                        young_rabbit_energy += ((Rabbit) entity).energy;
                    }
                }
            }
        }
        for(int i =0;i<20;i++) {
            try {
                program = Functions.createSimulation("t1-2cde");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            World world = program.getWorld();
            Map<Object, Location> entities = world.getEntities();
            for (Object entity : entities.keySet()) {
                if (entity instanceof Rabbit) {
                    ((Rabbit) entity).age += 6;

                }
            }
            for (int j = 0; j < 20; j++) {
                program.simulate();
                for (Object entity : entities.keySet()) {
                    if (entity instanceof Rabbit) {
                        old_rabbit_energy += ((Rabbit) entity).energy;
                    }
                }
            }
        }
        assertTrue(old_rabbit_energy<young_rabbit_energy);
    }
    @Test
    public void t1_2e() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-2cde");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        World world = program.getWorld();
        Location myRabbitHoleLocation = Functions.findRandomValidLocation(world);
        RabbitHole myRabbitHole = new RabbitHole(world, myRabbitHoleLocation);

        Map<Object, Location> entities = world.getEntities();
        int initial_rabbit_count = 0;
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                ((Rabbit)entity).isImmortal = true;
                ((Rabbit)entity).age =6;
                initial_rabbit_count++;
                ((Rabbit)entity).rabbitHole = myRabbitHole;
                myRabbitHole.addRabbit((Rabbit) entity);
                ((Rabbit) entity).energy +=200;
            }
        }

        for (int i = 0; i < 59; i++) {
            program.simulate();
            entities.clear();
            entities = world.getEntities();
            int grassAmount = 0;
            for (Object entity : entities.keySet()) {
                if (entity instanceof Rabbit) {
                    ((Rabbit) entity).isImmortal = true;
                    ((Rabbit) entity).energy +=200;
                }
            }

        }
        int currentRabbitCount = 0;
        entities.clear();
        entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                currentRabbitCount++;
            }
        }
        assertTrue(initial_rabbit_count<currentRabbitCount);
    }



    // TODO this test may break if digging holes requires energy in the future. might also break if rabbits don't always sleep.
    /**
     *  Tests if the rabbits can dig holes. Also tests if the rabbits move towards their holes at night.
     */
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

                    int distance = Functions.calculateDistance(rabbitLocation, holeLocation);
                    int previousDistance = Functions.calculateDistance(rabbit.previousPosition, holeLocation);

                    // check if the rabbit moved closer to the hole.
                    if (rabbitLocation.equals(rabbit.previousPosition) == false) {
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


    /**
     * Tests if rabbits can stand on rabbi tholes.
     * This test verifies that when a rabbit is placed on its hole.
     */

    @Test
    public void t1_3b() {
        Program program = null;
        try {
            program = Functions.createSimulation("t1-3b");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        World world = program.getWorld();

        // Find grass and rabbit
        RabbitHole hole = null;
        Rabbit rabbit = null;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof RabbitHole && hole == null) {
                hole = (RabbitHole) entity;
            }
            if (entity instanceof Rabbit && rabbit == null) {
                rabbit = (Rabbit) entity;
            }
        }

        //check if grass and rabbit is there then put the rabbit on the grass
        assertNotNull(hole);
        assertNotNull(rabbit);

        Location initialGrassLocation = world.getLocation(hole);
        world.move(rabbit,world.getLocation(hole));

        assertEquals(initialGrassLocation, world.getLocation(hole));
        assertEquals(initialGrassLocation, world.getLocation(rabbit));

    }

    // TODO move to functions file instead of duplicating.
    /**
     * Tests if rabbits can dig new exits in their tunnel.
     */

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
