
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

    @Test
    public void t1_2fg() {
        Program program = null;

        // load input file.
        try {
            program = Functions.createSimulation("t1-2fg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        World world = program.getWorld();
        int rabbithole_count = 0;

        ArrayList<Rabbit> rabbits = new ArrayList<>();
        ArrayList<RabbitHole> rabbitholes = new ArrayList<>();        

        // simulate 100 steps
        for (int i = 0; i < 100; i++) {

            Map<Object, Location> entities = world.getEntities();
            for (Object entity : entities.keySet()) {
                if (entity instanceof Rabbit) {
                    rabbits.add((Rabbit) entity);
                    ((Rabbit) entity).isImmortal = true;
                }
                if (entity instanceof RabbitHole) {
                    rabbitholes.add((RabbitHole) entity);
                }
            }

            // since rabbits die without food, we simply remove their mortality.
            for (Rabbit rabbit : rabbits) {
                System.out.println("Energy " + rabbit.getEnergy());
            }

            
            // System.out.println(world.getEntities().keySet().size());


            // if (world.isNight()) {
            //     rabbithole_count = 0;
            //         if (entity instanceof RabbitHole) {
            //             rabbithole_count++;

            // }



            

            program.simulate();
        }
        

    }
}
