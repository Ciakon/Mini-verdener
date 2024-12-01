package animals;

import java.util.ArrayList;

import animals.nests.WolfNest;
import itumulator.world.Location;
import itumulator.world.World;

public class AlphaWolf extends Wolf {
    ArrayList<Wolf> Pack;

    public AlphaWolf(World world, boolean isAdult, Location location) {
        super(world, isAdult, location);
        imageKeyBaby = "Alpha";
        imageKeyAdult = "Alpha";
        imageKeySleepingBaby = "Alpha";
        imageKeySleepingAdult = "Alpha";
    }

    public AlphaWolf(World world, boolean isAdult, WolfNest wolfNest) {
        super(world, isAdult, wolfNest);
    }

    @Override
    void generalAI() {

    }

    @Override
    void dayTimeAI() {
        
    }

    @Override
    void nightTimeAI() {
        
    }
}
