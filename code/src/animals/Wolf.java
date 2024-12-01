package animals;

import animals.nests.WolfNest;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import utils.Functions;

public class Wolf extends Animal {
    WolfNest wolfNest;
    ArrayList<Wolf> pack;
    AlphaWolf alpha;

    /**
     * Another br'ish method jumpscare
     */
    void wolfInit() {
        imageKeyBaby = "wolf-small";
        imageKeyAdult = "wolf";
        imageKeySleepingBaby = "wolf-small-sleeping";
        imageKeySleepingAdult = "wolf-sleeping";
        color = Color.gray;

        breedingEnergy = 15;
        visionRange = 4;
        maxEnergy = 30;
        energy = 15;
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

    public Wolf(World world, boolean isAdult, WolfNest WolfNest) {
        super(world, isAdult);

        this.isInsideNest = true;
        this.wolfNest = WolfNest;
        this.isSleeping = true;
        WolfNest.addAnimal(this);
        wolfInit();
    }

    @Override
    void generalAI() {
        if (world.contains(alpha) == false) {
            //breed new alpha later
        }
        else {
            moveTowards(world.getLocation(alpha));
        }

        if (world.isDay()) {
            isSleeping = false;
        }
    }

    @Override
    void dayTimeAI() {
        // hunting();
    }

    @Override
    void nightTimeAI() {
        // if (this.breedable && !this.hasBred && energy > breedingEnergy && this.isInsideNest) {
        //     this.findBreedingPartner();
        // }
        // this.moveToOrDigHole();
    }

    @Override
    void breed(Animal partner) {

        if (world.contains(alpha) || alpha == null) {
            AlphaWolf alpha = new AlphaWolf(world, false, this.wolfNest);

            for (Wolf wolf : pack) {
                wolf.setAlpha(alpha);
            }
        }
        else {
            new Wolf(world, false, this.wolfNest);
        }
        
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
        if (this.wolfNest != null) {
            moveTowards(world.getLocation(wolfNest));

            if (previousPosition.equals(world.getLocation(wolfNest)) && world.getLocation(this).equals(previousPosition)) {
                enterHole();
            }
            return;
        }

        WolfNest nearestHole = findNearestNest();

        if (nearestHole != null) {
            moveTowards(world.getLocation(nearestHole));
            if (world.getLocation(this).equals(world.getLocation(nearestHole))) {
                wolfNest = nearestHole;
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
        if (this.wolfNest != null) {
            throw new RuntimeException("animals should only dig a hole when they don't have one");
        }

        Location location = world.getLocation(this);

        if (world.getNonBlocking(location) != null) {
            return;
        }

        this.wolfNest = new WolfNest(this.world, location);
        wolfNest.addAnimal(this);

        enterHole();
    }

    public void enterHole() {
        if (world.getLocation(this).equals(world.getLocation(this.wolfNest)) == false) {
            throw new RuntimeException("animals should only enter their own hole when standing on it");
        }
        this.isInsideNest = true;

        isSleeping = true;
        world.remove(this); // gaming time
    }

    public void exitHole() {
        Location holeLocation = world.getLocation(this.wolfNest);
        if (world.isTileEmpty(holeLocation)) {
            isInsideNest = false;
            world.setTile(holeLocation, this);
        }
    }

    public void addToPack(ArrayList<Wolf> pack) {
        this.pack = pack;
        pack.add(this);
    }

    public void setAlpha(AlphaWolf alpha) {
        this.alpha = alpha;
    }

}
