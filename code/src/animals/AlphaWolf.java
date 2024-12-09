package animals;

import animals.nests.WolfNest;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.ArrayList;
import utils.Functions;

public class AlphaWolf extends Wolf {

    ArrayList<Wolf> Pack;

    void wolfInit() {
        imageKeyBaby = "Alpha";
        imageKeyAdult = "Alpha";
        imageKeySleepingBaby = "Alpha";
        imageKeySleepingAdult = "Alpha";
        preferedPrey.add("Rabbit");
    }

    public AlphaWolf(World world, boolean isAdult, Location location) {
        super(world, isAdult, location);
        wolfInit();
    }

    public AlphaWolf(World world, boolean isAdult, WolfNest wolfNest) {
        super(world, isAdult, wolfNest);
        wolfInit();
    }

    public AlphaWolf(World world, boolean isAdult, Location location, WolfNest wolfNest, int energy, int age) {
        super(world, isAdult, location);
        wolfInit();

        this.wolfNest = wolfNest;
        this.energy = energy;
        this.age = age;
    }

    @Override
    void generalAI() {
    }

    @Override
    void dayTimeAI() {
        hunting(world, this);

        // If Alpha is stuck, swap with one of the betas.
        if (world.getEmptySurroundingTiles(world.getLocation(this)).isEmpty()) {
            for (Location tile : world.getSurroundingTiles()) {
                Animal animal = (Animal) world.getTile(tile);
                if (pack.contains(animal)) {
                    Functions.swapObjects(world, this, animal);
                }
            }
        }
    }

    @Override
    void nightTimeAI() {

    }

    @Override
    void die() {
        createNewAlpha();
        super.die();
    }

    void createNewAlpha() {
        Wolf alpha = pack.get(0);
        for (Wolf wolf : pack) {
            if (wolf instanceof AlphaWolf) {
                continue;
            }

            if (wolf.getEnergy() > alpha.getEnergy()) {
                alpha = wolf;
            }
        }
        if (alpha instanceof AlphaWolf) {
            return;
        }
        alpha.andrewTateMode();
    }
}
