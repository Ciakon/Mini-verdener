import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.NonBlocking;

public class RabbitHole implements Actor, DynamicDisplayInformationProvider, NonBlocking{
    public RabbitHole() {

    }
    
    public void act(World world) {
        
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "hole");
    }
}