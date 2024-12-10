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

public class Wolf extends Animal implements Carnivorous {
    ArrayList<Wolf> pack;
    AlphaWolf alpha;

    /**
     * Another br'ish method jumpscare
     */
    void wolfInit() {
        imageKeyBaby = "wolf-small";
        imageKeyAdult = "dog";
        imageKeySleepingBaby = "wolf-small-sleeping";
        imageKeySleepingAdult = "wolf-sleeping";
        color = Color.gray;

        breedingEnergy = 15;
        visionRange = 4;
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

    @Override
    void generalAI() {
    }

    @Override
    void dayTimeAI() {
        isSleeping = false;
        hasBred = false;

        if (isInsideNest && world.getCurrentTime() < 7) {
            exitHole(); // Not guranteed, rabbits may be in the way.
        }

        if (isInsideNest == false) {
            if (isHungry()) {
                findFood(world, this);
            }
            else {
                followAlpha();
            }
        }

    }

    @Override
    void nightTimeAI() {
        if (!this.hasBred && energy > breedingEnergy && this.isInsideNest && isAdult) {
            this.findBreedingPartner();
        }
        if (isInsideNest == false) {
            this.moveToOrDigHole(); 
        }
        
        
    }

    @Override
    void breed(Animal partner) {
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
    public WolfNest findNearestNest() {
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
    public void moveToOrDigHole() {
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
     * Digs a new AnimalNest at the wolfs location in the world. This method
     * sets the wolf's `WolfNest` attribute to the new hole.
     */
    public void digHole() {
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

    public void enterHole() {
        if (world.getLocation(this).equals(world.getLocation(this.animalNest)) == false) {
            throw new RuntimeException("animals should only enter their own hole when standing on it");
        }
        this.isInsideNest = true;

        isSleeping = true;
        world.remove(this); // gaming time
    }

    public void exitHole() {
        Location holeLocation = world.getLocation(this.animalNest);
        if (world.isTileEmpty(holeLocation)) {
            isInsideNest = false;
            world.setTile(holeLocation, this);
        }
    }

    /**
     * Handles combat between this wolf and another animal.
     *
     * @param opponent The animal this wolf is fighting.
     */
    public void combat(Animal opponent) {
        if (opponent instanceof Wolf) {
            wolfCombat((Wolf) opponent);
        }
    }

    /**
     * Resolves a fight between two wolves based on their energy.
     *
     * @param otherWolf The wolf this wolf is fighting.
     */
    private void wolfCombat(Wolf otherWolf) {
        if (this.energy > otherWolf.energy) {
            this.energy -= otherWolf.energy;
            otherWolf.die();
        } else if (this.energy < otherWolf.energy) {
            otherWolf.energy -= this.energy;
            this.die();
        } else {
            this.die();
            otherWolf.die();
        }
    }

    public void addToPack(ArrayList<Wolf> pack) {
        this.pack = pack;
        pack.add(this);
    }

    @Override
    void die() {
        super.die();

        if (pack != null) {
            pack.remove(this);
        }
    }

    public void setAlpha(AlphaWolf alpha) {
        this.alpha = alpha;
    }

    public AlphaWolf andrewTateMode() {
        Location l = world.getLocation(this);

        world.delete(this);
        pack.remove(this);

        AlphaWolf alpha = new AlphaWolf(world, isAdult, l, animalNest, energy, age);
        alpha.addToPack(this.pack);

        for (Wolf wolf : pack) {
            wolf.setAlpha(alpha);
        }

        return alpha;
    }

    void followAlpha() {
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
     * When wolves eat a carcass, they share it with nearby pack members.
     */
    @Override
    public void eatCarcass(Carcass carcass, Animal me) {
        ArrayList<Wolf> nearbyPackMembers = new ArrayList<>(); // Includes the wolf itself

        if (pack == null) {
            Carnivorous.super.eatCarcass(carcass, me);
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

}
