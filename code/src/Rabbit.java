import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

public class Rabbit implements Actor, DynamicDisplayInformationProvider{
    
    public Rabbit() {

    }

    public void act(World world) {
        
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "rabbit-large");
    }
}
