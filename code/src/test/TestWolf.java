package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import animals.Wolf;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import utils.Functions;

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

    

}
