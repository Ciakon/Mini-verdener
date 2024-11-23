
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;
import java.util.*;

public class Grass implements Actor, DynamicDisplayInformationProvider, NonBlocking {

    private int nutritionalValue;

    public Grass(World world, Location location) {
        world.add(this);
        world.setTile(location, this);
        this.nutritionalValue = 50;
    }

    @Override
    public void act(World world) {
        System.out.println(world.contains(this));
        grow(world);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "bush");
    }

    public int getNutritionalValue() {
        return this.nutritionalValue;
    }

    /**
     * grass adds grass randomly by 10 procent from surounding tiles.
     *
     * @param world world which the grass is in
     */
    private void grow(World world) {
        Random random = new Random();
        if (random.nextInt(100) < 10) {
            Set<Location> emptyTiles = world.getEmptySurroundingTiles(world.getLocation(this));
            if (!emptyTiles.isEmpty()) {
                for (Location newLocation : emptyTiles) {
                    if (!world.containsNonBlocking(newLocation)) {
                        new Grass(world, newLocation);
                        break;
                    }
                }
            }
        }
    }
}
