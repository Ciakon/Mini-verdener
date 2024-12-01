package animals;

import java.util.ArrayList;

import animals.nests.WolfNest;
import itumulator.world.Location;
import itumulator.world.World;
import utils.Functions;

public class AlphaWolf extends Wolf {
    ArrayList<Wolf> Pack;

    void wolfInit() {
        imageKeyBaby = "Alpha";
        imageKeyAdult = "Alpha";
        imageKeySleepingBaby = "Alpha";
        imageKeySleepingAdult = "Alpha";
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
        System.out.println(energy);
    }

    @Override
    void dayTimeAI() {
        hunting();

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

        System.out.println("pack size: " + pack.size());
    }

    /**
     * Moves towards nearest prey. If they are within range, eat them instead.
     * If there is no prey move randomly
     */
    void hunting() {
        ArrayList<Location> nearbyPrey = findPrey();
        if (!nearbyPrey.isEmpty()) {
            Location nearestPrey = nearestObject(nearbyPrey);
            if (Functions.calculateDistance(world.getLocation(this), nearestPrey) > 1) {
                moveTowards(nearestPrey);
            } else if (Functions.calculateDistance(world.getLocation(this), nearestPrey) == 1) {
                int nv = kill((Animal) world.getTile(nearestPrey));
                for (Wolf wolf : pack) {
                    wolf.energy += Math.ceil(nv / pack.size());
                }

            } else {
                Location nextLocation = randomFreeLocation();
                if (nextLocation != null) {
                    world.move(this, nextLocation);
                    this.energy -= (int) (this.energyLoss * 2);
                }
            }
        }
    }

    void createNewAlpha() {
        Wolf alpha = pack.get(0);
        for (Wolf wolf : pack) {
            if (wolf instanceof AlphaWolf) continue;

            if (wolf.getEnergy() > alpha.getEnergy()) {
                alpha = wolf;
            }
        }
        if (alpha instanceof AlphaWolf) return;
        alpha.andrewTateMode();
    }
}
