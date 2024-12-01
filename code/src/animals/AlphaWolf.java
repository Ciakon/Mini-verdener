package animals;

import java.util.ArrayList;

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
