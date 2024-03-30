import java.io.Serializable;
import java.util.ArrayList;
package edu.bu.ec504.project;

/**
 * A class represents the molecule database
 */
public class MoleculeDatabase implements Serializable {

    private ArrayList<MoleculeDatabase> db;   // Molecule database

    /**
     * Private constructor to prevent instantiation from outside
     */
    private MoleculeDatabase() {
        this.db = new ArrayList<>();
    }

    /**
     * Add a new molecule into the database
     */
    public void addMolecule(Molecule molecule) {
      return null; // for now
    }

    /**
     * Find isomorphic molecule from the database
     */
    public Molecule findMolecule(Molecule molecule) {
      return null; // for now
    }

    /**
     * Save database to file system
     */
    public void save(String filename) {
      try {
        FileOutputStream fileOutStream = new FileOutputStream(filename);
        ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
        objOutStream.writeObject(this.molecules);
        objOutStream.close();
        fileOutStream.close();
        System.out.println("Database saved successfully.");
      }
      catch (IOException e) {
        System.err.println("Error saving database: " + e.getMessage());
      }
    }

    /**
     * Load database from file system
     */
    public void load(String filename) {
      try {
        FileInputStream fileInStream = new FileInputStream(filename);
        ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
        this.molecules = (ArrayList<Molecule>) objInStream.readObject();
        objInStream.close();
        fileInStream.close();
        System.out.println("Database loaded successfully.");
      }
      catch (IOException | ClassNotFoundException e) {
        System.err.println("Error loading database: " + e.getMessage());
      }
    }

}
