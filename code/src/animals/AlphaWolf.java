package animals;

import animals.nests.AnimalNest;
import itumulator.world.Location;
import itumulator.world.World;

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

    public AlphaWolf(World world, boolean isAdult, AnimalNest wolfNest, ArrayList<Wolf> pack) {
        super(world, isAdult, wolfNest, pack);
        alphaWolfInit();
    }

    public AlphaWolf(World world, boolean isAdult, Location location, AnimalNest wolfNest, int energy, int age) {
        super(world, isAdult, location);
        alphaWolfInit();

        this.animalNest = wolfNest;
        this.energy = energy;
        this.age = age;
    }

    @Override
    void generalAI() {
    }

    @Override
    void dayTimeAI() {
        isSleeping = false;

        if (isInsideNest && world.getCurrentTime() < 7) {
            exitHole(); // Not guranteed, rabbits may be in the way.
        }

        if (isInsideNest == false) {
            findFood();

            // If Alpha is stuck, swap with one of the betas.
            if (world.getEmptySurroundingTiles(world.getLocation(this)).isEmpty()) {
                for (Location tile : world.getSurroundingTiles()) {
                    if (world.getTile(tile) instanceof Animal == false) continue;
                    Animal animal = (Animal) world.getTile(tile);
                    if (pack.contains(animal)) {
                        Functions.swapObjects(world, this, animal);
                    }
                }
            }
        }

        
    }

    @Override
    void nightTimeAI() {
        if (!this.hasBred && energy > breedingEnergy && this.isInsideNest) {
            this.findBreedingPartner();
        }
        if (isInsideNest == false) {
            this.moveToOrDigHole(); 
        }
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
}
