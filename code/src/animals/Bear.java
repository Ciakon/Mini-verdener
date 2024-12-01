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
    int territorySize = 20;
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
        killList = new ArrayList<>();
        this.family.add(this);
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
            if (findNearestBerryBush() != null) {
                interactWithBerryBush();
            } else if (isInsideTerritory() == false) {
                moveTowards(territory);
            } else {
                // Chill
            }
        } else {
            System.out.println("isHunting");
            hunting();
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
        if (!killList.isEmpty()) {
            killList.removeIf(animal -> !world.contains(animal));

            ArrayList<Location> hitList = new ArrayList<>();
            for (Animal animal : killList) {
                if (world.isOnTile(animal)) {
                    hitList.add(world.getLocation(animal));
                }
            }

            if (!hitList.isEmpty()) {
                Location nearestPrey = nearestObject(hitList);

                int distance = Functions.calculateDistance(world.getLocation(this), nearestPrey);
                if (distance > 1) {
                    moveTowards(nearestPrey);
                } else if (distance == 1) {
                    Animal prey = (Animal) world.getTile(nearestPrey);
                    killList.remove(prey);
                    this.energy += kill(prey);
                }
            }
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
                if (killList.contains(animal)) {
                    continue;
                }
                killList.add(animal);
            }
        }
    }

    /**
     * Finds the nearest non-depleted BerryBush within the bear's vision range.
     *
     * @return The nearest BerryBush, or null if none are found.
     */
    public BerryBush findNearestBerryBush() {
        Set<Location> territoryTiles = world.getSurroundingTiles(territory, territorySize);

        for (Location location : territoryTiles) {
            if (world.getNonBlocking(location) instanceof BerryBush berryBush && !berryBush.isDepleted()) {
                return berryBush;
            }
        }

        return null;
    }

    /**
     * Consumes berries from a BerryBush if the bear is at the same location as
     * the bush.
     *
     * @param berryBush The BerryBush to consume berries from.
     * @return True if berries were consumed, false otherwise.
     */
    public boolean eatBerryBush(BerryBush berryBush) {
        if (berryBush == null) {
            return false;
        }

        if (world.getLocation(this).equals(world.getLocation(berryBush))) {
            int nutrition = berryBush.consumeBerries();
            this.energy += nutrition;

            if (this.energy > maxEnergy) {
                this.energy = maxEnergy;
            }
            return true;
        }

        return false;
    }

    /**
     * Handles the bear's interaction with a BerryBush: Finds the nearest
     * BerryBush Moves toward it if not already there Eats it when at the same
     * location
     */
    public void interactWithBerryBush() {
        BerryBush nearestBush = findNearestBerryBush();
        if (nearestBush != null) {
            if (!eatBerryBush(nearestBush)) {
                moveTowards(world.getLocation(nearestBush));
            }
        }
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
