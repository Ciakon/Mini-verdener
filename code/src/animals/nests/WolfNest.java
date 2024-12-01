package animals.nests;

import animals.Wolf;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.ArrayList;

public class WolfNest extends AnimalNest<Wolf> {

    ArrayList<Wolf> wolves;

    public WolfNest(World world, Location location) {
        super(world, location);
        this.wolves = new ArrayList<>();
    }

}
