package plants;

import itumulator.world.Location;
import itumulator.world.World;
import java.util.Random;
import java.util.Set;

/**
 * Represents a generic plant in the simulation world.
 *
 * Plants provide nutritional value to herbivores and have the ability to grow
 * in surrounding tiles based on a probability.
 */
public class Plant {

    protected int nutritionalValue;
    protected boolean isDepleted;

    /**
     * Constructs a new `Plant` instance at the specified location in the simulation world.
     *
     * @param world    The simulation world.
     * @param location The location of the plant in the world.
     */
    public Plant(World world, Location location) {
        this.isDepleted = false;
        this.nutritionalValue = 20;
    }

    /**
     * Retrieves the nutritional value of the plant.
     *
     * @return The current nutritional value of the plant.
     */
    public int getNutritionalValue() {
        return nutritionalValue;
    }

    /**
     * Sets the nutritional value of the plant.
     *
     * @param newNutritionalValue The new nutritional value to assign to the plant.
     */
    public void setNutritionalValue(int newNutritionalValue) {
        this.nutritionalValue = newNutritionalValue;
    }

    /**
     * Marks the plant as consumed, setting its nutritional value to 0.
     */
    public void consume() {
        if (!isDepleted) {
            this.isDepleted = true;
            this.nutritionalValue = 0;
        }
    }

    /**
     * Plants grow at surrounding tiles based on a percentage chance.
     *
     * @param world The world in which the plant is located.
     * @param percent The percentage chance for the plant to grow in surrounding
     * tiles.
     */
    public void grow(World world, int percent) {
        Random random = new Random();
        if (random.nextInt(100) < percent) {
            Set<Location> emptyTiles = world.getEmptySurroundingTiles(world.getLocation(this));
            if (!emptyTiles.isEmpty()) {
                for (Location newLocation : emptyTiles) {
                    if (!world.containsNonBlocking(newLocation)) {
                        try {
                            this.getClass()
                                    .getConstructor(World.class, Location.class)
                                    .newInstance(world, newLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;

                        }
                    }
                }
            }
        }
    }

