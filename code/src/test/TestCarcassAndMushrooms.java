package test;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import animals.Rabbit;
import plants.Carcass;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import plants.Mushrooms;
import utils.Functions;

public class TestCarcassAndMushrooms {

    /**
     * Tests if mushrooms spawns on Carcass correct amount of times.
     */
    @Test
    public void testShroomChances() {
        int successCounter = 0;
        int simulationAmount = 100;

        for (int simulationNumber = 0; simulationNumber < simulationAmount; simulationNumber++) {
            Program program = new Program(10, 100, 200);
            World world = program.getWorld();

            Carcass carcass = new Carcass(world, Functions.findRandomEmptyLocation(world), true, 50);
            carcass.act(world);
            if (carcass.getShroomStatus()) {
                successCounter++;
            }
        }
        double expectedChance = 0.10; //expected 10% success
        double tolerance = 0.05; // 5% tolerance
        double lowerBound = expectedChance - tolerance;
        double upperBound = expectedChance + tolerance;

        assertTrue(successCounter >= simulationAmount * lowerBound && successCounter <= simulationAmount * upperBound);
    }

    /**
     * Tests if mushrooms Colonize carcass. The world size is set to 5 so that
     * the carcass will always be reachable for the Mushrooms.
     */
    @Test
    public void testColonization() {
        int successCounter = 0;
        int simulationAmount = 100;

        for (int simulationNumber = 0; simulationNumber < simulationAmount; simulationNumber++) {
            Program program = new Program(5, 900, 200);
            World world = program.getWorld();

            Carcass carcass = new Carcass(world, Functions.findRandomEmptyLocation(world), true, 50);
            Mushrooms mushrooms = new Mushrooms(world, Functions.findRandomEmptyLocation(world), 50);
            program.simulate();
            if (carcass.getIsColony()) {
                successCounter++;
            }
        }

        assertEquals(successCounter, simulationAmount);
    }
}
