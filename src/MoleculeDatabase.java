import edu.bu.ec504.project.Molecule;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class represents the molecule database
 */
public class MoleculeDatabase {

    public HashMap<Integer, ArrayList<Molecule>> db;   // Molecule database

    /**
     * Constructs a database
     */
    public MoleculeDatabase() {
        this.db = new HashMap<>();
    }

    public void printDb() {
        for (Integer atomCount : this.db.keySet()) {
            System.out.println(atomCount);
            ArrayList<Molecule> moleculesWithSameNumAtoms = this.db.get(atomCount);
            for (Molecule molecule : moleculesWithSameNumAtoms) {
                System.out.println(molecule.moleculeName);
            }
        }
    }

    /**
     * Add a new molecule into the database
     */
    public void addMolecule(Molecule molecule) {
        int numAtoms = molecule.getNumAtoms();
        if (this.db.containsKey(numAtoms)) {
            this.db.get(numAtoms).add(molecule);
        } else {
            ArrayList<Molecule> moleculesWithSameNumAtoms = new ArrayList<>();
            moleculesWithSameNumAtoms.add(molecule);
            this.db.put(numAtoms, moleculesWithSameNumAtoms);
        }
    }

    /**
     * Find isomorphic molecule from the database
     */
    public Molecule findMolecule(Molecule molecule) {
        // Retrieve the partitioned array list based on the number of atoms
        int numAtoms = molecule.getNumAtoms();
        if (!db.containsKey(numAtoms)) {
//      System.out.println("no ArrayList with correct # of atoms");
            return null;
        }
        ArrayList<Molecule> moleculesWithSameNumAtoms = db.get(numAtoms);

        // Iterate through the array list of molecules with the same number of atoms
        for (Molecule dbMolecule : moleculesWithSameNumAtoms) {
            System.out.println(dbMolecule.moleculeName + " vs " + molecule.moleculeName);
            Molecule result = dbMolecule.areMoleculesEqual(molecule);
            if (result != null) {
                return result; // Return the isomorphic molecule
            }
        }
        return null; // Return null if molecule not found
    }

    /**
     * Save database to file system
     */
    public void save(String filename) throws IOException {
        FileOutputStream fileOutStream = new FileOutputStream(filename);
        ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
        objOutStream.writeObject(this.db);
        objOutStream.close();
        fileOutStream.close();
        System.out.println("Database saved successfully.");
    }

    /**
     * Load database from file system
     */
    public void load(String filename) throws IOException {
        FileInputStream fileInStream = new FileInputStream(filename);
        ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
        try {
            this.db = (HashMap<Integer, ArrayList<Molecule>>) objInStream.readObject();
            System.out.println("Database loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading database: " + e.getMessage());
        }
        objInStream.close();
        fileInStream.close();
    }
}
