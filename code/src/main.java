
import java.io.FileNotFoundException;

import itumulator.executable.Program;

class Main {

    public static void main(String[] args) {
        try {
            Program program = Functions.createSimulation("gaming");
            program.show();
            program.run();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
