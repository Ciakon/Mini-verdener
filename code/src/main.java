import itumulator.executable.Program;
import itumulator.world.World;

class Main{
    public static void main(String[] args) {
        System.out.println("L");

        Program program = new Program(10, 700, 200);
        World world = program.getWorld();

        program.show();
    }
}

