
import java.io.FileNotFoundException;

import itumulator.executable.Program;
import utils.Functions;

/**
 * Runs the program.
 */

class Main {

    /**
     * Runs simulation.
     *
     * @param args L, does nothing
     */
    public static void main(String[] args) {
        try {
            Program program = Functions.createSimulation("gaming");
            program.show();
            // program.run();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
