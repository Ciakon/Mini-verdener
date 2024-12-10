package animals.nests;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.ArrayList;
import java.util.Random;

import animals.Rabbit;

public class RabbitHole extends AnimalNest implements Actor {

    ArrayList<RabbitHole> connectedHoles; // includes itself.
    int collapseTimer = 0;
    int collapseTime = 120;
    ArrayList<Rabbit> rabbits;

    public RabbitHole(World world, Location location) {
        super(world, location);
        this.connectedHoles = new ArrayList<>();
        this.connectedHoles.add(this);
        this.rabbits = new ArrayList<>();
    }

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
     * Connect another hole to the current rabbithole and alle connected rabbit holes.
     * @param new_hole The hole to connect.
     */

    public void addHole(RabbitHole new_hole) {
        this.connectedHoles.add(new_hole);
    }

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
     * Gets all connected holes
     * @return connected holes.
     */

    public ArrayList<RabbitHole> getAllConnectedHoles() {
        return this.connectedHoles;
    }

    public void act(World world) {
        collapseTimer++;
        if (collapseTimer >= collapseTime) {
            collapse(world);
        }
    }

    void collapse(World world) {
        

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

    public void resetCollapseTimer() {
        collapseTimer = 0;
    }
}
