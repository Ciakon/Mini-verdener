package plants;

import itumulator.world.Location;
import itumulator.world.World;
import java.util.Random;
import java.util.Set;

public class Plant {

    protected int nutritionalValue;
    protected boolean isDepleted;

    /**
     * Constructor for Plant
     */
    public Plant(World world, Location location) {
        this.isDepleted = false;
        this.nutritionalValue = 20;
    }

    /**
     * Returns the nutritional value of the plant.
     */
    public int getNutritionalValue() {
        return nutritionalValue;
    }

    /**
     * Returns the nutritional value of the plant.
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
                        new Grass(world, newLocation);  // You can replace "Grass" with your specific plant subclass
                        break;
                    }
                }
            }
        }
    }
}
