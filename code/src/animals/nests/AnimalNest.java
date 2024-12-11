package animals.nests;

import animals.Animal;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Abstract base class for animal nests in the simulation.
 *
 * This class represents a general nest or shelter used by animals. It handles
 * the storage of animals associated with the nest and provides utility methods
 * for interacting with them.
 */
public abstract class AnimalNest implements DynamicDisplayInformationProvider, NonBlocking {
    protected ArrayList<Animal> animals;

    /**
     * Constructs a new `AnimalNest` at a specified location in the simulation world.
     *
     * @param world The simulation world where the nest exists.
     * @param location The location of the nest in the world.
     */
    public AnimalNest(World world, Location location) {
        world.add(this);
        world.setTile(location, this);

        this.animals = new ArrayList<>();
    }
    /**
     * Provides the display information for the nest, including its color and image key.
     *
     * @return A `DisplayInformation` object containing the visual attributes of the nest.
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.black, "hole");
    }

    /**
     * Adds a animal to the AnimalNext.
     *
     * @param animal The animal object.
     */
    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    /**
     * Removes an animal from the rabbithole.
     *
     * @param animal The animal object.
     */
    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    /**
     * Gets all animals connected to the rabbithole
     *
     * @return list of animals.
     */
    public ArrayList<Animal> getAllAnimals() {
        return this.animals;
    }

}
