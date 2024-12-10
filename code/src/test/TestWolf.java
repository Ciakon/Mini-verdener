package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import animals.Rabbit;
import animals.Wolf;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import plants.Grass;
import utils.Functions;

/**
 * Testing class for the Wolf animal.
 */
public class TestWolf {
    /**
     * Tests if a wolf can die. This doesn't require RNG, so it only runs once.
     */

    @Test
    void testDeath() {
        Program program = null;
        try {
            program = Functions.createSimulation("test/empty");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        World world = program.getWorld();

        Wolf wolf = new Wolf(world, true, new Location(1, 1));

        assertTrue(world.contains(wolf));
        assertTrue(world.isOnTile(wolf));

        wolf.removeEnergy(100000);
        program.simulate();

        assertFalse(world.contains(wolf));
    }

    @Test
    void huntTest() {

        int successfulHunts = 0;
        int simulationAmount = 20;

        for (int simulationNumber = 1; simulationNumber <= simulationAmount; simulationNumber++) {
            Program program = new Program(10, 100, 200);
            World world = program.getWorld();

            // spawn wolf
            Location randomSpawn = Functions.findRandomEmptyLocation(world);
            Wolf wolf = new Wolf(world, true, randomSpawn);

            // spawn grass
            for (int i = 0; i < 20; i++) {
                randomSpawn = Functions.findRandomValidLocation(world);
                new Grass(world, randomSpawn);
            }
            // spawn rabbits
            for (int i = 0; i < 10; i++) {
                randomSpawn = Functions.findRandomEmptyLocation(world);
                new Rabbit(world, true, randomSpawn);
            }

            boolean wolfHasEaten = false;
            int wolfPreviousEnergy = wolf.getEnergy();

            // simulate 50 steps
            for (int i = 0; i < 50; i++) {
                program.simulate();

                // Check if wolf eats
                if (wolf.getEnergy() > wolfPreviousEnergy) {
                    wolfHasEaten = true;
                }
            }

            // With 10 rabbits around, the wolf should eat in 50 steps
            if (wolfHasEaten) {
                successfulHunts++;
            }
        }

        assertTrue(successfulHunts >= simulationAmount*0.75);
    }

}
