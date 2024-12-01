package animals;

import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import plants.BerryBush;
import utils.Functions;

/**
 * "bear is kil" "no"
 */
public class Bear extends Animal {

    Location territory;
    int territorySize = 2;
    ArrayList<Bear> family;

    ArrayList<Animal> killList;

    /**
     * Another br'ish method jumpscare
     */
    void bearInit() {
        visionRange = 5;
        maxEnergy = 150;
        energy = 75;
        energyLoss = 2;

        adultAge = 40;

        nutritionalValueAdult = 80;
        nutritionalValueBaby = 20;

        imageKeyBaby = "bear-small";
        imageKeyAdult = "bear";
        imageKeySleepingBaby = "bear-small-sleeping";
        imageKeySleepingAdult = "bear-sleeping";
        color = Color.black;
        preferedPrey.add("Rabbit");
        preferedPrey.add("Wolf");
    }

    /**
     * This constructor is used when creating a bear normally.
     *
     * @param world The simulation world.
     * @param isAdult Whether to spawn the bear as an adult or baby.
     * @param location Where to spawn it.
     */
    public Bear(World world, boolean isAdult, Location location) {
        super(world, isAdult);
        world.setTile(location, this);
        this.territory = location;
        this.family = new ArrayList<>();
        bearInit();
    }

    /**
     * This constructor is used when creating a bear with a given territory.
     *
     * @param world The simulation world.
     * @param isAdult Whether to spawn the bear as an adult or baby.
     * @param location Where to spawn it.
     * @param territory Where to place its territory .
     */
    public Bear(World world, boolean isAdult, Location location, Location territory) {
        super(world, isAdult);
        world.setTile(location, this);
        this.territory = territory;
        this.family = new ArrayList<>();
        bearInit();
    }

    /**
     * This constructor is used when bears breed.
     *
     * @param world The simulation world.
     * @param isAdult Whether to spawn the bear as an adult or baby.
     * @param location Where to spawn it.
     * @param territory Where to place its territory .
     * @param family Their family members.
     */
    public Bear(World world, boolean isAdult, Location location, Location territory, ArrayList<Bear> family) {
        super(world, isAdult);
        world.setTile(location, this);
        this.territory = territory;
        this.family = family;
        bearInit();
    }

    @Override
    void generalAI() {
        checkForIllegalActivity();
        previousPosition = world.getLocation(this);
    }

    @Override
    void dayTimeAI() {
        Random random = new Random();
        isSleeping = false;

        if (killList.isEmpty()) {
            if (isInsideTerritory() == false) {
                moveTowards(territory);
            } else {
                // Chill
            }
        } else {
            hunting();
            System.out.println(killList.get(0) + " is kil");
        }

        if (world.getCurrentTime() >= 7) {
            sleeperTime();
        }
    }

    @Override
    void nightTimeAI() {
        sleeperTime();
    }

    @Override
    void hunting() {
        // todo change target to killlist.
        if (this.target != null && world.contains(target)) {
            if (Functions.calculateDistance(world.getLocation(this), world.getLocation(target)) == 1) {
                kill(target);
            } else {
                moveTowards(world.getLocation(target));
            }
        } else {
            super.hunting();
        }
    }

    @Override
    void breed(Animal partner) {
        // Bear child;
        // this.family.add(child);
    }

    /**
     * Checks if an animal enters the bears territory and sets it as a target.
     * Does not target family members.
     */
    void checkForIllegalActivity() {
        Set<Location> territoryTiles = world.getSurroundingTiles(territory, territorySize);

        for (Location location : territoryTiles) {
            if (world.getTile(location) instanceof Animal) {
                Animal animal = (Animal) world.getTile(location);

                if (family.contains(animal)) {
                    continue;
                }
                if (killList.contains(animal)) continue;
                killList.add(animal);
            }
        }
    }

    /**
     * Finds and eats the nearest non-depleted BerryBush if no animals are in
     * its territory.
     *
     * @return True if a BerryBush was eaten; false otherwise.
     */
    boolean eatBerryBush() {
        Set<Location> territoryTiles = world.getSurroundingTiles(territory, territorySize);
        for (Location location : territoryTiles) {
            if (world.getNonBlocking(location) instanceof BerryBush berryBush && !berryBush.isDepleted()) {
                moveTowards(location);

                if (world.getLocation(this).equals(location)) {
                    int nutrition = berryBush.consumeBerries();
                    this.energy += nutrition;

                    if (this.energy > maxEnergy) {
                        this.energy = maxEnergy;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Goes back to its territory and sleeps.
     */
    void sleeperTime() {
        if (isInsideTerritory()) {
            isSleeping = true;
        }

        if (isSleeping) {
            return;
        }

        moveTowards(territory);
    
    }

    /**
     * Check if the bear is inside its territory
     *
     * @return whether the bear is in the terrotory.
     */
    boolean isInsideTerritory() {
        for (Location l : world.getSurroundingTiles(territory, territorySize)) {
            if (l.equals(world.getLocation(this))) {
                return true;
            }
        }
        return false;
    }

}
