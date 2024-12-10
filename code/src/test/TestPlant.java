package test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import animals.Bear;
import org.junit.Assert;
import org.junit.Test;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import plants.BerryBush;
import plants.Grass;
import utils.Functions;

import static org.junit.jupiter.api.Assertions.*;
public class TestPlant {

     /**
      * Tests if grass can spread.
      * This test ensures that after simulation steps, the number of grass entities increases as grass spreads.
      */
     @Test
     public void plantsGrow() {
         Program program = null;
         // load input file.
        /* try {
             program = Functions.createSimulation("test/plantsTest");
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }*/

         // or make new program manually
         program = new Program(5, 100, 10);

         // check if grass spawns
         World world = program.getWorld();

         new Grass(world, new Location(2,2));
         new BerryBush(world, new Location(4,4));

         int grass_count = 0;
         int berry_count = 0;
         Map<Object, Location> entities = world.getEntities();
         for (Object entity : entities.keySet()) {
             if (entity instanceof Grass){
                 grass_count++;
             }
             if(entity instanceof BerryBush){
                 berry_count++;
             }
         }
         // Simulate so it can grow
         for (int i = 0; i < 1000; i++) {
             program.simulate();
         }
         // count the grass growing
         int finalGrassCount = 0;
         int finalBerryCount = 0;
         entities = world.getEntities();
         for (Object entity : entities.keySet()) {
             if (entity instanceof Grass ) {
                 finalGrassCount++;
             }
             if(entity instanceof BerryBush){
                 finalBerryCount++;
             }
         }
         assertTrue(finalGrassCount > grass_count );
         assertTrue(finalBerryCount > berry_count);
     }

}
