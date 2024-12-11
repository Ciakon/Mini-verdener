package animals.nests;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.ArrayList;
import java.util.Random;

import animals.Rabbit;


/**
 * Represents a rabbit hole in the simulation world.
 *
 * A `RabbitHole` acts as a shelter for rabbits and can connect to other rabbit holes,
 * forming a network of tunnels. Over time, rabbit holes can collapse, forcing rabbits
 * to relocate to other connected holes.
 *
 */
public class RabbitHole extends AnimalNest implements Actor {

    ArrayList<RabbitHole> connectedHoles; // includes itself.
    private int collapseTimer = 0;
    private int collapseTime = 120;
    private ArrayList<Rabbit> rabbits;

    /**
     * Constructs a new rabbit hole at a specified location in the world.
     *
     * @param world    The simulation world where the hole exists.
     * @param location The location of the hole in the world.
     */
    public RabbitHole(World world, Location location) {
        super(world, location);
        this.connectedHoles = new ArrayList<>();
        this.connectedHoles.add(this);
        this.rabbits = new ArrayList<>();
    }

    /**
     * Constructs a new rabbit hole and connects it to an existing network of rabbit holes.
     *
     * @param world          The simulation world where the hole exists.
     * @param location       The location of the hole in the world.
     * @param connectedHoles The list of rabbit holes to connect to this hole.
     */
    public RabbitHole(World world, Location location, ArrayList<RabbitHole> connectedHoles) {
        super(world, location);

        this.connectedHoles = connectedHoles;
        this.connectedHoles.add(this);
        this.rabbits = connectedHoles.get(0).getAllRabbits();
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
     * Connect another hole to the current rabbithole and all connected rabbit holes.
     * @param new_hole The hole to connect.
     */
    public void addHole(RabbitHole new_hole) {
        this.connectedHoles.add(new_hole);
    }

    /**
     * Disconnects a rabbit hole from this hole's network.
     *
     * @param hole The rabbit hole to disconnect.
     */
    public void removeHole(RabbitHole hole) {
        this.connectedHoles.remove(hole);
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
     * Retrieves all rabbit holes connected to this hole, including itself.
     *
     * @return A list of connected rabbit holes.
     */
    public ArrayList<RabbitHole> getAllConnectedHoles() {
        return this.connectedHoles;
    }


    /**
     * Simulates the rabbit hole's behavior in the world during each simulation step.
     *
     * Tracks the time until the rabbit hole collapses and triggers the collapse if necessary.
     *
     * @param world The simulation world.
     */
    @Override
    public void act(World world) {
        collapseTimer++;
        if (collapseTimer >= collapseTime) {
            collapse(world);
        }
    }

    /**
     * Collapses the rabbit hole, forcing rabbits to relocate to another connected hole.
     *
     * If other rabbit holes are available in the network, rabbits are randomly assigned
     * to one of the remaining connected holes. The collapsed hole is removed from the
     * world and the network of connected holes.
     *
     * @param world The simulation world.
     */
    private void collapse(World world) {
        

        if (getAllConnectedHoles().size() > 1) {
            connectedHoles.remove(this);
            world.delete(this);

            Random random = new Random();
            int randomNumber = random.nextInt(connectedHoles.size());

            // replace the rabbithole with another in the tunnel for the rabbits.
            for (Rabbit rabbit : getAllRabbits()) {
                if (rabbit.getRabbitHole().equals(this)) {
                    rabbit.setRabbitHole(connectedHoles.get(randomNumber));
                }
            }

        }
        
    }
}
