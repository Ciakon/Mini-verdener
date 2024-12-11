package animals;

import animals.nests.AnimalNest;
import animals.nests.WolfNest;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import plants.Carcass;
import utils.Functions;


/**
 * The {@code Wolf} class represents a wolf in the simulation. Wolves can form packs, hunt prey,
 * share food, breed, and dig or inhabit nests. This class extends the {@link Animal} class
 * and implements the {@link Carnivorous} interface to provide behaviors such as hunting
 * and interacting with {@link Carcass}.
 * <p>
 * Wolves can either belong to a pack or roam individually. They have an alpha wolf in a pack,
 * which they follow. Wolves share food within their pack and engage in combat with wolves
 * outside their pack to establish dominance.
 * </p>
 */


public class Wolf extends Animal implements Carnivorous {
    protected ArrayList<Wolf> pack;
    protected AlphaWolf alpha;

    /**
     * Initializes wolf-specific attributes such as vision range, energy levels,
     * nutritional values, and preferred prey.
     */
    protected void wolfInit() {
        imageKeyBaby = "wolf-small";
        imageKeyAdult = "dog";
        imageKeySleepingBaby = "wolf-small-sleeping";
        imageKeySleepingAdult = "wolf-sleeping";
        color = Color.gray;

        breedingEnergy = 15;
        visionRange = 2;
        maxEnergy = 100;
        energy = 80;
        energyLoss = 1;

        adultAge = 40;

        nutritionalValueAdult = 50;
        nutritionalValueBaby = 20;

        preferedPrey.add("Rabbit");
    }

    public Wolf(World world, boolean isAdult, Location location) {
        super(world, isAdult);
        world.setTile(location, this);
        wolfInit();
    }

    public Wolf(World world, boolean isAdult, AnimalNest WolfNest, ArrayList<Wolf> pack) {
        super(world, isAdult);

        this.isInsideNest = true;
        this.animalNest = WolfNest;
        this.isSleeping = true;
        WolfNest.addAnimal(this);
        this.pack = pack;
        pack.add(this);
        wolfInit();
    }

    /**
     * Executes general AI behavior for the wolf, such as engaging in combat
     */
    @Override
    protected void generalAI() {
        // engage in combat with other wolves.
        if (isInsideNest == false) {
            for (Location wolfLocation : Functions.findNearbyObjects(world, world.getLocation(this), Wolf.class, 1)) {
                Wolf otherWolf = (Wolf) world.getTile(wolfLocation);
    
                if (otherWolf.getPack() != this.pack || this.pack == null) {
                    engageInCombat(otherWolf);
                    break;
                }
            }
        }
    }

    /**
     * Executes daytime behavior, wake up, search for food,
     * or following the alpha wolf.
     */
    @Override
    protected void dayTimeAI() {
        isSleeping = false;
        hasBred = false;

        if (isInsideNest && world.getCurrentTime() < 7) {
            exitHole(); // Not guranteed, rabbits may be in the way.
        }

        if (isInsideNest == false) {
            if (this.alpha == null) {
                if (isHungry()) {
                    findFood();
                }
                else {
                    wander();
                }
            }
            else if (isStarving()) {
                findFood();
            }
            else {
                followAlpha();
            }
        }

    }

    /**
     * Executes nighttime behavior, attempt to breed or returning to the nest.
     */
    @Override
    protected void nightTimeAI() {
        if (!this.hasBred && energy > breedingEnergy && this.isInsideNest && isAdult) {
            this.findBreedingPartner();
        }
        if (isInsideNest == false) {
            this.moveToOrDigHole(); 
        }
        
        
    }
    /**
     * Handles the wolf breeding. Creates a new wolf as an offspring
     * and adds it to the pack.
     *
     * @param partner The wolf to breed with.
     */
    @Override
    protected void breed(Animal partner) {
        new Wolf(world, false, this.animalNest, pack);

        this.energy -= this.breedingEnergy;
        this.hasBred = true;
        partner.setHasBred(true);
        partner.removeEnergy(this.breedingEnergy);
    }

    /**
     * Finds the nearest nest within the wolf's vision range.
     *
     *
     * @return the nearest nest object, or null if none are found
     */
    @Override
    protected WolfNest findNearestNest() {
        Set<Location> surroundings = world.getSurroundingTiles(world.getLocation(this), this.visionRange);
        WolfNest closestHole = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Location location : surroundings) {
            if (world.getTile(location) instanceof WolfNest hole) {
                int distance = Functions.calculateDistance(world.getLocation(this), location);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestHole = hole;
                }
            }
        }
        return closestHole;
    }

    /**
     * Moves the wolf toward the nearest nest using moveTowards() if one exists.
     * If no nest is nearby, the wolf digs a new nest.
     *
     * @param world the world in which the animal is located
     */
    @Override
    protected void moveToOrDigHole() {
        if (this.animalNest != null) {
            moveTowards(world.getLocation(animalNest));

            if (previousPosition == null) return;

            if (previousPosition.equals(world.getLocation(animalNest)) && world.getLocation(this).equals(previousPosition)) {
                enterHole();
            }
            return;
        }

        WolfNest nearestHole = findNearestNest();

        if (nearestHole != null) {
            moveTowards(world.getLocation(nearestHole));
            if (world.getLocation(this).equals(world.getLocation(nearestHole))) {
                animalNest = nearestHole;
                nearestHole.addAnimal(this);
            }
        } else {
            digHole();
        }
    }

    /**
     * Digs a new {@link AnimalNest} at the wolves location in the world. This method
     * sets the wolf's {@link WolfNest} attribute to the new hole.
     */
    @Override
    protected void digHole() {
        if (this.animalNest != null) {
            throw new RuntimeException("animals should only dig a hole when they don't have one");
        }

        Location location = world.getLocation(this);

        if (world.getNonBlocking(location) != null) {
            return;
        }

        WolfNest newNest = new WolfNest(this.world, location);

        if (pack != null) {
            for (Wolf wolf : pack) {
                wolf.animalNest = newNest;
                newNest.addAnimal(wolf);
            }
        }
        else {
            animalNest = newNest;
            animalNest.addAnimal(this);
        }

        
        enterHole();
    }
    /**
     * Allows the wolf to enter its nest. The wolf must be standing on its nest to enter.
     *
     * @throws RuntimeException If the wolf is not standing on its own nest when attempting to enter.
     */
    @Override
    protected void enterHole() {
        if (world.getLocation(this).equals(world.getLocation(this.animalNest)) == false) {
            throw new RuntimeException("animals should only enter their own hole when standing on it");
        }
        this.isInsideNest = true;

        isSleeping = true;
        world.remove(this); // gaming time
    }

    /**
     * Exits the wolf's nest, placing it back in the world at the nest's location.
     */
    @Override
    protected void exitHole() {
        Location holeLocation = world.getLocation(this.animalNest);
        if (world.isTileEmpty(holeLocation)) {
            isInsideNest = false;
            world.setTile(holeLocation, this);
        }
    }

    /**
     * Adds the wolf to a specified pack.
     *
     * @param pack  An arraylist (The pack) to which the wolf will be added.
     */
    public void addToPack(ArrayList<Wolf> pack) {
        this.pack = pack;
        pack.add(this);
    }

    /**
     * Handles the wolf's death. Removes the wolf from the world and if it has one its pack .
     */
    @Override
    protected void die() {
        super.die();

        if (pack != null) {
            pack.remove(this);
        }
    }

    /**
     * Converts the wolf into an {@link AlphaWolf}, creating a new alpha leader for the pack.
     *
     * @return The newly created {@link{@code AlphaWolf}}.
     */
    public void setAlpha(AlphaWolf alpha) {
        this.alpha = alpha;
    }

    public AlphaWolf andrewTateMode() {
        Location l = null;
        if (world.isOnTile(this)) {
            l = world.getLocation(this);
        }

        world.delete(this);
        pack.remove(this);

        AlphaWolf alpha;
        if (l != null) {
            alpha = new AlphaWolf(world, isAdult, l, animalNest, energy, age);
            alpha.addToPack(this.pack);
        }
        else {
            alpha = new AlphaWolf(world, isAdult, animalNest, pack, energy, age);
        }        

        for (Wolf wolf : pack) {
            wolf.setAlpha(alpha);
        }

        return alpha;
    }
    /**
     * wolf will follow the alpha wolf. If no alpha wolf is found, the wolf will wander randomly.
     */
    protected void followAlpha() {
        if (alpha == null) {
            wander();
            return;
        }

        if (world.isOnTile(alpha) == false) {
            wander();
            return;
        }
        if (Functions.calculateDistance(world.getLocation(this), world.getLocation(alpha)) == 1) {
            return;
        }
        moveTowards(world.getLocation(alpha));
    }

    /**
     * sharing of a carcass among pack members.
     * Distributes the carcass's nutritional value evenly among nearby pack members.
     *
     * @param carcass The carcass to eat.
     */
    @Override
    public void eatCarcass(Carcass carcass) {
        ArrayList<Wolf> nearbyPackMembers = new ArrayList<>(); // Includes the wolf itself

        if (pack == null) {
            Carnivorous.super.eatCarcass(carcass);
            return;
        }

        for (Wolf wolf : pack) {
            if (wolf.isInsideNest) continue;
            if (Functions.calculateDistance(world.getLocation(this), world.getLocation(wolf)) <= 2) {
                nearbyPackMembers.add(wolf);
            }
        }

        int totalAmount = carcass.getNutritionalValue();
        int desiredAmount = 0;
        int individualEnergy;

        for (Wolf wolf : nearbyPackMembers) {
            desiredAmount += wolf.maxEnergy - wolf.energy;
        }

        if (totalAmount < desiredAmount) {
            carcass.setNutritionalValue(0);
            individualEnergy = Math.ceilDiv(totalAmount, nearbyPackMembers.size());
        } else {
            carcass.setNutritionalValue(totalAmount - desiredAmount);
            individualEnergy = Math.ceilDiv(desiredAmount, nearbyPackMembers.size());;
        }

        for (Wolf wolf : nearbyPackMembers) {
            wolf.addEnergy(individualEnergy);
        }
    }

    /**
     * returns the pack to which the wolf belongs.
     *
     * @return The pack of wolves.
     */
    public ArrayList<Wolf> getPack() {
        return pack;
    }

}
