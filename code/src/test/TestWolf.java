package test;

import java.io.FileNotFoundException;

import animals.Wolf;
import itumulator.executable.Program;
import itumulator.world.Location;
import utils.Functions;

public class TestWolf {
    
    void testDeath() {
        Program program = null;
        try {
            program = Functions.createSimulation("test/empty");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Wolf wolf = new Wolf(program.getWorld(), true, new Location(1, 1));
    }

    

}
