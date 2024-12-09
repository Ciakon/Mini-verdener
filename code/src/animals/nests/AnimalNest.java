package animals.nests;

import animals.Animal;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;

public abstract class AnimalNest<Type extends Animal> implements DynamicDisplayInformationProvider, NonBlocking {

    private ArrayList<Type> animals;

    public AnimalNest(World world, Location location) {
        world.add(this);
        world.setTile(location, this);

        this.animals = new ArrayList<>();
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.black, "hole");
    }

    /**
     * Adds a animal to the AnimalNext.
     *
     * @param animal The animal object.
     */
    public void addAnimal(Type animal) {
        animals.add(animal);
    }

    /**
     * Removes a animal from the rabbithole.
     *
     * @param animal The animal object.
     */
    public void removeAnimal(Type animal) {
        animals.remove(animal);
    }

    /**
     * Gets all animals connected to the rabbithole
     *
     * @return list of animals.
     */
    public ArrayList<Type> getAllAnimals() {
        return (ArrayList<Type>) this.animals;
    }

}