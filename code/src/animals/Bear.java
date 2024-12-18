package animals;

import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import plants.BerryBush;
import plants.Plant;
import utils.Functions;

/**
 * The {@code Bear} class represents an animal capable of both carnivorous
 * and herbivorous behaviors. Bears have defined territories, can form family groups,
 * and engage in activities such as hunting, foraging, breeding, and defending their territory.
 * <p>
 * This class implements both {@link Carnivorous} and {@link Herbivorous} interfaces, allowing
 * the bear to eat prey and consume plants (specifically berries). It also extends the
 * {@link Animal} base class to inherit common animal behaviors.
 * </p>
 */

public class Bear extends Animal implements Carnivorous, Herbivorous {

    private Location territory;
    private int territorySize = 2;
    private ArrayList<Bear> family;
    private ArrayList<Animal> killList;

    /**
     * Initializes the bear's attributes and preferences, such as vision range,
     * energy levels, preferred prey, and appearance.
     */
    protected void bearInit() {
        visionRange = 3;
        maxEnergy = 150;
        energy = 130;
        breedingEnergy = 100;
        energyLoss = 1;

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

        preferedPrey.add("Rabbit");
        preferedPrey.add("Wolf");
        preferedPrey.add("AlphaWolf");
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

    /**
     * Executes general AI behavior for the bear, checking for
     * intrusions in its territory.
     */
    @Override
    protected void generalAI() {
        checkForIllegalActivity();
    }


    /**
     * Defines daytime behavior for the bear, such as hunting, foraging,
     * and attempt breeding.
     */
    @Override
    protected void dayTimeAI() {
        isSleeping = false;

        if (world.getCurrentTime() >= 7) {
            sleeperTime(); 
            return;
        }
        attemptBreeding();
        if (killList.isEmpty()) {
            if (isStarving()) {
                findFood();
            } else if (isHungry()) {
                forage();
            } else {
                moveTowards(territory);
            }
        } else {
            hitman();
        }

    }
    /**
     * Defines nighttime behavior for the bear, returning to its
     * territory to sleep.
     */
    @Override
    protected void nightTimeAI() {
        sleeperTime();
    }

    /**
     * finds animals in bears kill list and kills them.
     */
    private void hitman() {
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
                    kill(prey);
                }
            }
        }
    }

    /**
     * Handles the process of bear breeding.
     *
     * When two bears meet the conditions for breeding (both are adults, have
     * sufficient energy, and have not already bred during this cycle), this
     * method creates a new bear as their offspring. The child inherits the
     * parents' territory.
     *
     * @param partner The other bear participating in the breeding process.
     * @throws RuntimeException If the partner is not a bear.
     */
    @Override
    protected void breed(Animal partner) {
        if (!(partner instanceof Bear)) {
            throw new RuntimeException("ayo!");
        }
        Location childLocation = Functions.findRandomEmptyLocation(world);
        Bear child = new Bear(world, false, childLocation, this.territory, this.family);

        this.energy -= this.breedingEnergy;
        this.hasBred = true;
        partner.setHasBred(true);
        partner.removeEnergy(this.breedingEnergy);
        this.family.add((Bear) partner);
        this.family.add(child);
        ((Bear) partner).setFamiliy(this.family);
    }

    /**
     * Checks if the animal checks off criteria to breed.
     *
     * @return Returns true if all criteria for breeding are met otherwise
     * returns false.
     */
    public boolean isBreedable() {
        if (this.age >= this.adultAge && !this.hasBred && this.getEnergy() > this.breedingEnergy) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempts to find a suitable breeding partner in the adjacent tiles.
     *
     * A bear will attempt to breed with another bear if the following
     * conditions are met: - Both bears are adults. - Both have sufficient
     * energy for breeding. - Neither bear has already bred during that day.
     *
     * If a suitable partner is found, the `breed` method is called.
     */
    private void attemptBreeding() {
        if (isBreedable() == false) {
            return;
        }

        Set<Location> surroundingTiles = world.getSurroundingTiles(world.getLocation(this), 2);

        for (Location location : surroundingTiles) {
            if (world.getTile(location) instanceof Bear partner) {
                if (partner.isBreedable()) {
                    breed(partner);
                    return;
                }
            }
        }
    }

    /**
     * Checks if an animal enters the bears territory and sets it as a target.
     * Does not target family members.
     */
    private void checkForIllegalActivity() {
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
                if (animal instanceof Bear == false || this.isBreedable() == false) {
                    killList.add(animal);
                }
            }
        }
    }


    /**
     * Goes back to its territory and sleeps.
     */
    private void sleeperTime() {
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
    private boolean isInsideTerritory() {
        for (Location l : world.getSurroundingTiles(territory, territorySize)) {
            if (l.equals(world.getLocation(this))) {
                return true;
            }
        }

        if (territory.equals(world.getLocation(this))) {
            return true;
        }

        return false;
    }

    /**
     * Finds all nearby berryBushes within the vision range. Overidden from default method.
     *
     * @return An ArrayList of nearby berryBush locations.
     */
    @Override
    public ArrayList<Plant> findNearbyPlants() {
        Animal me = (Animal) this;
        World world = me.world;

        ArrayList<Plant> nearbyPlants = new ArrayList<>();
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(me), me.visionRange);

        if (world.getTile(world.getLocation(me)) instanceof BerryBush plant && plant.isDepleted() == false) {
            nearbyPlants.add(plant);
        }

        for (Location location : surroundings) {
            if (world.getNonBlocking(location) instanceof BerryBush plant && plant.isDepleted() == false) {
                nearbyPlants.add(plant);
            }
        }

        return nearbyPlants;
    }

    /**
     * Updates the bear's family group.
     *
     * @param family The new family group.
     */
    public void setFamiliy(ArrayList<Bear> family) {
        this.family = family;
    }

}
