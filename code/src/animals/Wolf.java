package animals;

import animals.nests.AnimalNest;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;

public final class Wolf extends Animal {

    int breedingEnergy = 15;

    Color color = Color.gray;
    int visionRange;
    int maxEnergy;
    int energy;
    int energyLoss;

    int adultAge;

    int nutritionalValueAdult;
    int nutritionalValueBaby;

    /**
     * Another br'ish method jumpscare
     */
    void wolfInit() {
        visionRange = 4;
        maxEnergy = 30;
        energy = 15;
        energyLoss = 1;

        adultAge = 40;

        nutritionalValueAdult = 50;
        nutritionalValueBaby = 20;
    }

    public Wolf(World world, boolean isAdult, Location location) {
        super(world, isAdult);
        world.setTile(location, this);
        wolfInit();
    }

    public Wolf(World world, boolean isAdult, AnimalNest animalNest) {
        super(world, isAdult);

        this.isInsideNest = true;
        this.animalNest = animalNest;
        this.isSleeping = true;
        animalNest.addAnimal(this);

        wolfInit();
    }

    @Override
    void generalAI() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generalAI'");
    }

    @Override
    void dayTimeAI() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dayTimeAI'");
    }

    @Override
    void nightTimeAI() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nightTimeAI'");
    }

    @Override
    void breed(Animal partner) {
        new Wolf(world, false, this.animalNest);
        this.energy -= this.breedingEnergy;
        this.hasBred = true;
        partner.setHasBred(true);
        partner.removeEnergy(this.breedingEnergy);
    }

}
