
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;

public class RabbitHole implements Actor, DynamicDisplayInformationProvider, NonBlocking {

    ArrayList<Rabbit> rabbits;
    ArrayList<RabbitHole> connectedHoles; // includes itself.

    public RabbitHole(World world, Location location) {
        world.add(this);
        world.setTile(location, this);

        this.rabbits = new ArrayList<>();
        this.connectedHoles = new ArrayList<>();
        this.connectedHoles.add(this);
    }

    public RabbitHole(World world, Location location, ArrayList<RabbitHole> connectedHoles) {
        world.add(this);
        world.setTile(location, this);

        this.rabbits = new ArrayList<>();

        this.connectedHoles = connectedHoles;
        this.connectedHoles.add(this);
    }

    /**
     * Adds a rabbit to the rabbithole.
     * 
     * @param rabbit The rabbit object.
     */
    public void addRabbit(Rabbit rabbit) {
        rabbits.add(rabbit);
    }

    /**
     * Removes a rabbit from the rabbithole.
     * 
     * @param rabbit The rabbit object.
     */
    public void removeRabbit(Rabbit rabbit) {
        rabbits.remove(rabbit);
    }

    /**
     * Connect another hole to the current rabbithole and alle connected rabbit holes.
     * @param new_hole The hole to connect.
     */

    public void addHole(RabbitHole new_hole) {
        this.connectedHoles.add(new_hole);
    }

    /**
     * Gets all rabbits connected to the rabbithole
     * 
     * @return list of rabbits.
     */

    public ArrayList<Rabbit> getAllRabbits() {
        return this.rabbits;
    }

    /**
     * Gets all connected holes
     * @return connected holes.
     */

    public ArrayList<RabbitHole> getAllConnectedHoles() {
        return this.connectedHoles;
    }

    public void act(World world) {
        //todo remove actor interface?
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "hole");
    }
}
