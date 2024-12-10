package test;

 import java.io.FileNotFoundException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;

 import animals.Bear;
 import org.junit.Assert;
 import org.junit.Test;

 import animals.Rabbit;
 import animals.nests.RabbitHole;
 import itumulator.executable.Program;
 import itumulator.world.Location;
 import itumulator.world.World;
 import plants.Grass;
 import utils.Functions;

 import static org.junit.jupiter.api.Assertions.*;

 public class TestBear {
     /**
      * Tests if bears can breed and produce offspring.
      * This test ensures that when two bears are placed together, they breed and create a bear cub.
      */
     @Test
     public void testBearBreeding() {
         Program program = null;


         try {
             program = Functions.createSimulation("test/bearBreedingTest");
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }

         assert program != null;
         World world = program.getWorld();


         Bear parent1 = null;
         Bear parent2 = null;
         Map<Object, Location> entities = world.getEntities();
         for (Object entity : entities.keySet()) {
             if (entity instanceof Bear && parent1 == null) {
                 parent1 = (Bear) entity;
             } else if (entity instanceof Bear && parent2 == null) {
                 parent2 = (Bear) entity;
             }
         }


         assert parent1 != null;
         assert parent2 != null;


         int initialBearCount = 0;
         for (Object entity : entities.keySet()) {
             if (entity instanceof Bear) {
                 initialBearCount++;
             }
         }


         for (int i = 0; i < 50; i++) {
             program.simulate();
         }


         entities = world.getEntities();


         int finalBearCount = 0;
         for (Object entity : entities.keySet()) {
             if (entity instanceof Bear) {
                 finalBearCount++;
             }
         }
         Assert.assertTrue(finalBearCount > initialBearCount);
     }
 }