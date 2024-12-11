package plants;


import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;



/**
 * Represents a berry bush in the simulation world.
 *
 * The `BerryBush` provides nutritional value to herbivores and can grow new bushes
 * and replenish its berries after depletion. It interacts with the world dynamically
 * as an actor and provides visual information for display.
 */
public class BerryBush extends Plant implements Actor, DynamicDisplayInformationProvider, NonBlocking {



    private int defaultNutritionalValue;
    private boolean depleted;
    private int stepsSinceDepletion;

    /**
     * Constructs a new `BerryBush` at the specified location in the simulation world.
     *
     * @param world    The simulation world.
     * @param location The location of the bush in the world.
     */
    public BerryBush(World world, Location location) {
        super(world, location);
        world.add(this);
        world.setTile(location, this);
        this.defaultNutritionalValue = 10;
        this.nutritionalValue = this.defaultNutritionalValue;
        this.depleted = false;
        this.stepsSinceDepletion = 0;

    }


    /**
     * Defines the behavior of the berry bush during each simulation step.
     *
     * If the bush is depleted, it tracks the steps since depletion and replenishes
     * itself after a set number of steps. else, it attempts to grow new bushes.
     *
     * @param world The simulation world.
     */
    @Override
    public void act(World world) {
        if (depleted) {
            stepsSinceDepletion++;
            if (stepsSinceDepletion >= 20) {
                replenish();
            }
        } else {
            super.grow(world, 1);

        }
    }

    /**
     * Reduces the bush's nutritional value and marks it as depleted.
     *
     */
    @Override
    public void consume() {
        if (depleted) return;

        nutritionalValue = 0;
        depleted = true;
        stepsSinceDepletion = 0;
    }

    /**
     * Replenishes the bush by resetting its nutritional value and texture.
     */
    private void replenish() {
        this.nutritionalValue = this.defaultNutritionalValue;
        this.depleted = false;
        this.stepsSinceDepletion = 0;
    }

    /**
     * Provides the display information for the berry bush.
     *
     * The texture changes based on whether the bush is depleted or not.
     *
     * @return A `DisplayInformation` object containing the bush's color and texture.
     */
    @Override
    public DisplayInformation getInformation() {
        String texture = depleted ? "bush" : "bush-berries";
        return new DisplayInformation(Color.MAGENTA, texture);
    }



    /**
     * Checks whether the bush is currently depleted.
     *
     * @return `true` if the bush is depleted, otherwise `false`.
     */
    public boolean isDepleted() {
        return depleted;
    }

}
