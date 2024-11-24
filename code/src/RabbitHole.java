
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;

public class RabbitHole implements Actor, DynamicDisplayInformationProvider, NonBlocking {

    private ArrayList<Rabbit> rabbits;

    public RabbitHole(World world, Location location) {
        world.add(this);
        world.setTile(location, this);

        this.rabbits = new ArrayList<>();
    }

    /**
     * Hassan er gay
     * 
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

    public ArrayList getAllRabbits() {
        return this.rabbits;
    }

    public void act(World world) {
        //todo remove actor interface?
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "hole");
    }
}
