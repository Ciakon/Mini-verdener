package plants;


import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;
import java.util.*;

public class BerryBush extends Plant implements Actor, DynamicDisplayInformationProvider, NonBlocking {



    private int nutritionalValue;
    private boolean depleted;
    private int stepsSinceDepletion;

    public BerryBush(World world, Location location) {
        super(world, location);
        world.add(this);
        world.setTile(location, this);
        this.nutritionalValue = 10;
        this.depleted = false;
        this.stepsSinceDepletion = 0;

    }


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
     * @return The amount of nutrition provided to the animal.
     */
    public int consumeBerries() {
        if (depleted) return 0;

        int nutrition = nutritionalValue;
        nutritionalValue = 0;
        depleted = true;
        stepsSinceDepletion = 0;

        return nutrition;
    }

    /**
     * Replenishes the bush by resetting its nutritional value and texture.
     */
    private void replenish() {
        this.nutritionalValue = 10;
        this.depleted = false;
        this.stepsSinceDepletion = 0;
    }


    @Override
    public DisplayInformation getInformation() {
        String texture = depleted ? "bush" : "bush-berries";
        return new DisplayInformation(Color.MAGENTA, texture);
    }

    public int getNutritionalValue() {
        return this.nutritionalValue;
    }
    public boolean isDepleted() {
        return depleted;
    }

}
