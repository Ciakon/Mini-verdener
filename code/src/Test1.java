
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
        int true_rabbit_count = 1;
        World world = program.getWorld();

        int rabbit_count = 0;
        Map<Object, Location> entities = world.getEntities();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Rabbit) {
                rabbit_count++;
            }
        }

        assertEquals(true_rabbit_count, rabbit_count);
    }

    //@Test
    public static void k1_2b() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-2fg");
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
        System.err.println(initial_rabbit_count);
        System.err.println(current_rabbit_count);
        System.out.println(current_rabbit_count < initial_rabbit_count);
    }

    //@Test
    public static void k1_2c() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-c1");
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
        for (int i = 0; i < 20; i++) {
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

        System.out.println(current_rabbit_count == initial_rabbit_count);
    }

}
