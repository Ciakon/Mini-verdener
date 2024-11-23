import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

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

        int true_grass_count = 3;
        World world = program.getWorld();
    
        int grass_count = 0;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Grass) {
                grass_count++;
            }
        }

        assertEquals(true_grass_count, grass_count);
    }
}

