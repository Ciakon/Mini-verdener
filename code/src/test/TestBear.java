package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.util.Map;

import animals.Bear;
import animals.Rabbit;
import animals.Wolf;

import org.junit.Assert;
import org.junit.Test;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import plants.Grass;
import utils.Functions;

/**
 * Testing class for {@link Bear}.
 */
public class TestBear {

    /**
     * Tests if bears can breed and produce offspring. This test ensures that
     * when two bears are placed together and are ready to breed, they breed and create a bear cub.
     */
    @Test
    public void testBearBreeding() {
        Program program = null;

        try {
            program = Functions.createSimulation("test/bearBreedingTest");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert program != null;
        World world = program.getWorld();

        Bear parent1 = null;
        Bear parent2 = null;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Bear && parent1 == null) {
                parent1 = (Bear) entity;
            } else if (entity instanceof Bear && parent2 == null) {
                parent2 = (Bear) entity;
            }
        }

        assert parent1 != null;
        assert parent2 != null;

        int initialBearCount = 0;
        for (Object entity : entities.keySet()) {
            if (entity instanceof Bear) {
                initialBearCount++;
            }
        }

        for (int i = 0; i < 20; i++) {
            program.simulate();
        }

        entities = world.getEntities();

        int finalBearCount = 0;
        for (Object entity : entities.keySet()) {
            if (entity instanceof Bear) {
                finalBearCount++;
            }
        }
        Assert.assertTrue(finalBearCount > initialBearCount);
    }

    /**
     * Tests if a bear can hunt rabbits and wolves.
     */
    
    @Test
    public void huntTest() {
        int successfulHunts = 0;
        int timesRabbitKilled = 0;
        int timesWolfKilled = 0;
        int simulationAmount = 20;

        int rabbitAmount = 10;
        int wolfAmount = 10;

        for (int simulationNumber = 1; simulationNumber <= simulationAmount; simulationNumber++) {
            Program program = new Program(10, 100, 200);
            World world = program.getWorld();

            // spawn bear
            Location randomSpawn = Functions.findRandomEmptyLocation(world);
            Bear bear = new Bear(world, true, randomSpawn);

            // spawn grass
            for (int i = 0; i < 20; i++) {
                randomSpawn = Functions.findRandomValidLocation(world);
                new Grass(world, randomSpawn);
            }

            // spawn rabbits
            for (int i = 0; i < rabbitAmount; i++) {
                randomSpawn = Functions.findRandomEmptyLocation(world);
                new Rabbit(world, true, randomSpawn);
            }

            // spawn wolves
            for (int i = 0; i < wolfAmount; i++) {
                randomSpawn = Functions.findRandomEmptyLocation(world);
                new Wolf(world, true, randomSpawn);
            }

            boolean rabbitKilled = false;
            boolean wolfKilled = false;

            // simulate 20 steps
            for (int i = 0; i < 20; i++) {
                bear.act(world);

                // Check if bear kills
                if (Functions.findNearbyObjects(world, new Location(0,0), Rabbit.class, 1000).size() < rabbitAmount) {
                    rabbitKilled = true;
                }
                if (Functions.findNearbyObjects(world, new Location(0,0), Wolf.class, 1000).size() < wolfAmount) {
                    wolfKilled = true;
                }
            }

            if (rabbitKilled) timesRabbitKilled++;
            if (wolfKilled) timesWolfKilled++;

            if (rabbitKilled || wolfKilled) successfulHunts++;
        }

        assertTrue(timesRabbitKilled >= 1);
        assertTrue(timesWolfKilled >= 1);
        assertTrue(successfulHunts >= simulationAmount * 0.75);
    }
}
