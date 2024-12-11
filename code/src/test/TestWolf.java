package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import animals.Animal;
import org.junit.jupiter.api.Test;

import animals.AlphaWolf;
import animals.Rabbit;
import animals.Wolf;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import plants.Grass;
import utils.Functions;

/**
 * Testing class for the {@link Wolf} {@link Animal}. and {@link AlphaWolf}.
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

    /**
     * Tests if a wolf can hunt rabbits.
     */
    @Test
    public void huntTest() {

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

    /**
     * Tests if a wolfpack can hunt rabbits. The test specifically checks if all the wolf gain energy when the alpha wolf (or another nearby wolf in the pack) hunts a rabbit.
     */
    @Test
    public void packHuntTest() {

        int successfulHunts = 0;
        int simulationAmount = 20;

        for (int simulationNumber = 1; simulationNumber <= simulationAmount; simulationNumber++) {
            Program program = new Program(10, 100, 200);
            World world = program.getWorld();

            Functions.createWolfPack(world, 3);

            // spawn grass
            for (int i = 0; i < 30; i++) {
                Location randomSpawn = Functions.findRandomValidLocation(world);
                new Grass(world, randomSpawn);
            }
            // spawn rabbits
            for (int i = 0; i < 15; i++) {
                Location randomSpawn = Functions.findRandomEmptyLocation(world);
                new Rabbit(world, true, randomSpawn);
            }

            Location alphaLocation = Functions.findNearbyObjects(world, new Location(0,0), AlphaWolf.class, 1000).get(0);
            AlphaWolf alphaWolf = (AlphaWolf) world.getTile(alphaLocation);

            boolean packHasEatenThisSimulation = false;
            
            int alphaPreviousEnergy = alphaWolf.getEnergy();
            ArrayList<Integer> packPreviousEnergy = new ArrayList<>();
            for (Wolf wolf : alphaWolf.getPack()) {
                packPreviousEnergy.add(wolf.getEnergy());
            }

            // simulate 50 steps
            for (int i = 0; i < 50; i++) {
                program.simulate();

                // Check if wolf eats
                if (alphaWolf.getEnergy() > alphaPreviousEnergy) {
                    boolean packHasEaten = true;

                    for (int j = 0; j < packPreviousEnergy.size(); j++) {
                        Wolf wolf = alphaWolf.getPack().get(j);

                        if (wolf.getEnergy() <= packPreviousEnergy.get(j)) {
                            packHasEaten = false;
                        }
                    }

                    if (packHasEaten) {
                        packHasEatenThisSimulation = true;
                    }

                }
            }

            // With the high amount of rabbits, the wolf pack should almost always find enough food to share with eachother.
            if (packHasEatenThisSimulation) {
                successfulHunts++;
            }
        }

        assertTrue(successfulHunts >= simulationAmount*0.75);
    }

    /**
     * This test puts to wolves that are not in the same pack against eachother. It tests if they fight, and that the winner is the one with most energy.
     */
    @Test
    public void wolfFightTest() {
        int simulationAmount = 20;

        for (int simulationNumber = 1; simulationNumber <= simulationAmount; simulationNumber++) {
            Program program = new Program(4, 100, 200);
            World world = program.getWorld();

            Wolf wolf1 = new Wolf(world, true, Functions.findRandomEmptyLocation(world));
            Wolf wolf2 = new Wolf(world, true, Functions.findRandomEmptyLocation(world));

            wolf2.removeEnergy(10);

            // After 10 steps there should only be 1 wolf left.
            for (int i = 0; i < 10; i++) {
                program.simulate();
            }

            assertTrue(world.contains(wolf1));
            assertFalse(world.contains(wolf2));
        }
        
    }

}
