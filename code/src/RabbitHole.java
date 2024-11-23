import java.awt.Color;
import java.util.ArrayList;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;
import itumulator.world.NonBlocking;

public class RabbitHole implements Actor, DynamicDisplayInformationProvider, NonBlocking{
    ArrayList<Rabbit> rabbits;

    public RabbitHole(World world, Location location) {
        world.add(this);
        world.setTile(location, this);

        rabbits = new ArrayList<>();
    }

    
    /** 
     * @param rabbit
     */
    public void addRabbit(Rabbit rabbit) {
        rabbits.add(rabbit);
    }

    
    /** 
     * @param rabbit
     */
    public void removeRabbit(Rabbit rabbit) {
        rabbits.remove(rabbit);
    }
    
    public void act(World world) {
        //todo remove actor interface?
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "hole");
    }
}