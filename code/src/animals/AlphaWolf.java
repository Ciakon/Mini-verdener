package animals;

import animals.nests.AnimalNest;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import utils.Functions;


/**
 * The {@code AlphaWolf} class represents the leader of a wolf pack.
 * Alpha wolves have specialized behaviors and responsibilities, including managing the pack,
 * finding food, and selecting a successor upon death.
 * <p>
 * This class extends the {@link Wolf} class and overrides specific methods to define alpha-specific
 * behaviors, such as pack leadership and enhanced decision-making during the simulation.
 * </p>
 */
public class AlphaWolf extends Wolf {

    ArrayList<Wolf> Pack;

    /**
     * Initializes the alpha wolf's specific attributes, such as unique appearance and adding itself to the pack.
     */

    void alphaWolfInit() {
        imageKeyBaby = "Alpha";
        imageKeyAdult = "Alpha";
        imageKeySleepingBaby = "Alpha";
        imageKeySleepingAdult = "Alpha";

        pack = new ArrayList<>();
        pack.add(this);
    }

    /**
     * Constructs an {@code AlphaWolf}
     *
     * @param world    The simulation world.
     * @param isAdult  Whether the AlphaWolf spawns as an adult.
     * @param location The initial location of the AlphaWolf in the simulation world.
     */
    public AlphaWolf(World world, boolean isAdult, Location location) {
        super(world, isAdult, location);
        alphaWolfInit();
    }

    /**
     * Constructs an {@code AlphaWolf} for when {@link Wolf} to {@code AlphaWolf} conversion
     *
     * @param world    The simulation world.
     * @param isAdult  Whether the AlphaWolf spawns as an adult.
     * @param location The initial location of the AlphaWolf.
     * @param wolfNest The {@link AnimalNest} associated with the AlphaWolf.
     * @param energy   The initial energy level of the AlphaWolf.
     * @param age      The initial age of the AlphaWolf.
     */
    public AlphaWolf(World world, boolean isAdult, Location location, AnimalNest wolfNest, int energy, int age) {
        super(world, isAdult, location);
        alphaWolfInit();

        this.animalNest = wolfNest;
        this.energy = energy;
        this.age = age;
    }
    /**
     * Defines the general behavior of the AlphaWolf.
     *
     */
    @Override
    void generalAI() {
    }
    /**
     * Defines the daytime behavior for the AlphaWolf.
     * The AlphaWolf attempts to exit its nest, find food, or manage its surroundings
     * (e.g., swapping positions with stuck pack members).
     */
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
    /**
     * Defines the nighttime behavior for the AlphaWolf.
     * The AlphaWolf attempts to breed and find or dig a hole to rest in.
     */
    @Override
    void nightTimeAI() {
        if (!this.hasBred && energy > breedingEnergy && this.isInsideNest) {
            this.findBreedingPartner();
        }
        if (isInsideNest == false) {
            this.moveToOrDigHole(); 
        }
    }

    /**
     * Handles the death of the AlphaWolf. Upon death, a new AlphaWolf is selected
     * from the existing pack based on the highest energy level.
     */
    @Override
    void die() {
        super.die();
        createNewAlpha();
    }
    /**
     * Selects a new AlphaWolf for the pack.
     * The new AlphaWolf is chosen from the pack based on the highest energy level.
     * If no suitable candidate is found, no new AlphaWolf is created.
     */
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
