package src;

import itumulator.executable.Program;
import itumulator.world.World;

class main {
    public static void main(String[] args) {
        System.out.println("L");

        Program program = new Program(10, 900, 200);
        World world = program.getWorld();

        program.show();
    }
}