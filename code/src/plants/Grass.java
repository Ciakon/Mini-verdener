
package plants;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;



/**
 * Represents a patch of grass in the simulation world.
 *
 * Grass provides a small amount of nutritional value to herbivores and can grow
 * in surrounding tiles based on a low probability.
 * If depleted, the grass is removed from the simulation world.
 */

public class Grass extends Plant implements Actor, DynamicDisplayInformationProvider, NonBlocking {


    /**
     * Constructs a new `Grass` instance at the specified location in the simulation world.
     *
     * @param world    The simulation world.
     * @param location The location of the grass in the world.
     */
    public Grass(World world, Location location) {
        super(world, location);
        world.add(this);
        world.setTile(location, this);
        this.nutritionalValue = 10;
    }

    /**
     * Defines the behavior of the grass during each simulation step.
     *
     * - Attempts to grow in surrounding tiles.
     * - If the grass is depleted, it is removed from the simulation world.
     *
     * @param world The simulation world.
     */
    @Override
    public void act(World world) {
        super.grow(world,0.02);

        if (isDepleted) {
            world.delete(this); 
        }
    }

    /**
     * Provides display information for rendering the grass in the simulation.
     *
     * @return A `DisplayInformation` object containing the grass's color and texture.
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "grass");
    }





}
