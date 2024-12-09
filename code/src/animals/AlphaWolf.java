package animals;

import animals.nests.WolfNest;
import itumulator.world.Location;
import itumulator.world.World;
import plants.Carcass;

import java.util.ArrayList;
import utils.Functions;

public class AlphaWolf extends Wolf {

    ArrayList<Wolf> Pack;

    void alphaWolfInit() {
        imageKeyBaby = "Alpha";
        imageKeyAdult = "Alpha";
        imageKeySleepingBaby = "Alpha";
        imageKeySleepingAdult = "Alpha";

        pack = new ArrayList<>();
        pack.add(this);
    }

    public AlphaWolf(World world, boolean isAdult, Location location) {
        super(world, isAdult, location);
        alphaWolfInit();
    }

    public AlphaWolf(World world, boolean isAdult, WolfNest wolfNest) {
        super(world, isAdult, wolfNest);
        alphaWolfInit();
    }

    public AlphaWolf(World world, boolean isAdult, Location location, WolfNest wolfNest, int energy, int age) {
        super(world, isAdult, location);
        alphaWolfInit();

        this.wolfNest = wolfNest;
        this.energy = energy;
        this.age = age;
    }

    @Override
    void generalAI() {

    }

    @Override
    void dayTimeAI() {
        findFood(world, this);

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
        super.die();
        createNewAlpha();
    }

    void createNewAlpha() {
        if (pack.isEmpty()) return;

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

    public ArrayList<Wolf> getPack() {
        return pack;
    }
}
