import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import itumulator.executable.Program;
import itumulator.world.World;

class Main{
    public static void main(String[] args) {
        Program program = new Program(10, 700, 200);
        World world = program.getWorld();

        // program.show();
        try {
            runSimulation("t1-1c");
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
    
    public static void runSimulation(String test) throws FileNotFoundException {
        Random RNG = new Random();

        File file = new File("data/" + test + ".txt");
        Scanner scanner = new Scanner(file);

        HashMap<String, Class<?> > classes = new HashMap<>();
        classes.put("grass", Grass.class);
        classes.put("rabbit", Rabbit.class);

        int N = Integer.parseInt(scanner.nextLine());
        System.out.println("N: " + N);

        while (scanner.hasNext()) {
           Class<?> cl = classes.get(scanner.next());

            String amountSTR = scanner.next();

            int amount = 0;
            if (amountSTR.contains("-")) { //min-max
                int min = Integer.parseInt(amountSTR.split("-")[0]);
                int max = Integer.parseInt(amountSTR.split("-")[1]);

                amount = RNG.nextInt(min, max + 1);
            }
            else {
                amount = Integer.parseInt(amountSTR);
            }

            

            System.out.println("Class: " + cl);
            System.out.println("Amount: " + amount);
            // TODO add to world
            
        }

        scanner.close();
        System.out.println("Done");
    }
}