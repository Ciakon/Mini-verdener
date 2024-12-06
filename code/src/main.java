
import java.io.FileNotFoundException;

import itumulator.executable.Program;
import utils.Functions;

class Main {

    /**
     * Runs simulation.
     *
     * @param args L, does nothing
     */
    public static void main(String[] args) {
        try {
            Program program = Functions.createSimulation("gaming_2");
            program.show();
            program.run();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
