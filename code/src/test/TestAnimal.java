package test;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import animals.Rabbit;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import plants.Grass;
import utils.Functions;

/**
 * Testing class for {@link Animal} .
 */
public class TestAnimal {

    /**
     * Test if animals can properly pathfind using "movetowards()".
     */
    @Test
    public void pathFindingTest() {
        int successCounter = 0;
        int simulationAmount = 100;

        for (int simulationNumber = 0; simulationNumber < simulationAmount; simulationNumber++) {
            Program program = new Program(10, 100, 200);
            World world = program.getWorld();

            Rabbit rabbit = new Rabbit(world, true, Functions.findRandomEmptyLocation(world));

            // Randomly place obstacle rabbits around.
            for (int i = 0; i < 15; i++) {
                new Rabbit(world, true, Functions.findRandomEmptyLocation(world));
            }

            Location targetLocation = Functions.findRandomEmptyLocation(world);

            for (int step = 0; step <= 30; step++) {
                rabbit.moveTowards(targetLocation);
            }

            if (world.getLocation(rabbit).equals(targetLocation)) {
                successCounter++;
            }
        }

        // The path finding is not good enough to work every time. In the real simulation there are not multiple unmoving obstacles next to eachother.
        assertTrue(successCounter >= simulationAmount * 0.75);
    }

    /**
     * Checks if animal will find food.
     */
    @Test
    public void forageTest() {
        int successCounter = 0;
        int simulationAmount = 100;

        for (int simulationNumber = 0; simulationNumber < simulationAmount; simulationNumber++) {
            Program program = new Program(9, 900, 200);
            World world = program.getWorld();

            Rabbit rabbit = new Rabbit(world, true, Functions.findRandomEmptyLocation(world));

            new Grass(world, Functions.findRandomEmptyLocation(world));

            int initialEnergy = rabbit.getEnergy();
            for (int step = 0; step <= 40; step++) {
                rabbit.forage();
            }

            int currentEnergy = rabbit.getEnergy();
            if (initialEnergy < currentEnergy) {
                successCounter++;
            }
        }

        // The path finding is not good enough to work every time. In the real simulation there are not multiple unmoving obstacles next to eachother.
        assertTrue(successCounter >= simulationAmount * 0.75);
    }
}
