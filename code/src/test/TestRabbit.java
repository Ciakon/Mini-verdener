package test;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import plants.Grass;
import animals.Rabbit;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import utils.Functions;

/**
 * Testing class for the {@link Rabbit} animal.
 */
public class TestRabbit {

    /**
     * Tests Rabbit breeding.
     */
    @Test
    public void rabbitBreedingTest() {
        int successCounter = 0;
        int simulationAmount = 100;

        for (int simulationNumber = 0; simulationNumber < simulationAmount; simulationNumber++) {
            Program program = new Program(6, 100, 200);
            World world = program.getWorld();

            int initialRabbitAmount = 3;
            int currentRabbitAmount = 0;
            for (int i = 0; i < 15; i++) {
                new Grass(world, Functions.findRandomEmptyLocation(world));
            }

            for (int i = 0; i < initialRabbitAmount; i++) {
                new Rabbit(world, true, Functions.findRandomEmptyLocation(world));
            }

            for (int i = 0; i < 20; i++) {
                program.simulate();
            }

            Map<Object, Location> entities = world.getEntities();
            for (Object entity : entities.keySet()) {
                if (entity instanceof Rabbit) {
                    currentRabbitAmount++;
                }
            }

            if (currentRabbitAmount > initialRabbitAmount) {
                successCounter++;
            }
        }

        assertTrue(successCounter >= simulationAmount * 0.75);
    }

    /**
     * Tests if rabbit goes inside hole at night.
     */
    @Test
    public void rabbitholeTest() {
        int successCounter = 0;
        int simulationAmount = 100;

        for (int simulationNumber = 0; simulationNumber < simulationAmount; simulationNumber++) {
            Program program = new Program(6, 100, 200);
            World world = program.getWorld();

            Rabbit rabbit = new Rabbit(world, true, Functions.findRandomEmptyLocation(world));

            for (int i = 0; i < 19; i++) {
                rabbit.addEnergy(5);
                program.simulate();
            }

            if (!world.isOnTile(rabbit)) {
                successCounter++;
            }
        }

        assertTrue(successCounter >= simulationAmount * 0.75);
    }

}
