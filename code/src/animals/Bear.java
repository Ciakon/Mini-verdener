package animals;

import java.awt.Color;

import itumulator.world.Location;
import itumulator.world.World;

public class Bear extends Animal{
    Location territory;
    int territorySize = 3;
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
    }

    public Bear(World world, boolean isAdult, Location location) {
        super(world, isAdult);
        world.setTile(location, this);
        this.territory = location;
        bearInit();
    }

    public Bear(World world, boolean isAdult, Location location, Location territory) {
        super(world, isAdult);
        world.setTile(location, this);
        this.territory = territory;
        bearInit();
    }

    @Override
    void generalAI() {

    }

    @Override
    void dayTimeAI() {

    }

    @Override
    void nightTimeAI() {

    }

    @Override
    void breed(Animal partner) {

    }


}
