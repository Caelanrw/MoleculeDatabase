import edu.bu.ec504.project.Atom;
import edu.bu.ec504.project.Molecule;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class represents the molecule database
 */
public class MDB {

    public HashMap<Integer, ArrayList<Molecule>> db;   // Molecule database
    public JTextArea outputTextArea; // Reference to the text area in the GUI

    /**
     * Constructs a database
     */
    public MDB(JTextArea outputTextArea) {
        this.db = new HashMap<>();
        this.outputTextArea = outputTextArea;
    }

    /**
     * Print database statistics to GUI
     */
    public void printDb() {
        // Print number of molecules
        int size = 0;
        for (ArrayList<Molecule> molecules : db.values()) {
            size += molecules.size();
        }
        outputTextArea.append("# of molecules: " + size + "\n\n");

        // Print the list of molecules
        outputTextArea.append("List of molecules: " + "\n\n");
        for (Integer atomCount : this.db.keySet()) {
            ArrayList<Molecule> moleculesWithSameNumAtoms = this.db.get(atomCount);
            for (Molecule molecule : moleculesWithSameNumAtoms) {
                outputTextArea.append("Molecule name: " + molecule.moleculeName + "\n");
                outputTextArea.append("# of atoms: " + atomCount.toString() + "\n\n");
            }
        }

        // Print the largest and biggest molecules
        int maxAtoms = Integer.MIN_VALUE;
        int minAtoms = Integer.MAX_VALUE;
        Molecule largestMolecule = null;
        Molecule smallestMolecule = null;
        for (Map.Entry<Integer, ArrayList<Molecule>> entry : db.entrySet()) {
            int numAtoms = entry.getKey();
            if (numAtoms > maxAtoms) {
                maxAtoms = numAtoms;
                largestMolecule = entry.getValue().get(0); // only print 1 representative molecule
            }
            if (numAtoms < minAtoms) {
                minAtoms = numAtoms;
                smallestMolecule = entry.getValue().get(0); // only print 1 representative molecule
            }
        }
        outputTextArea.append("Smallest molecule: " + smallestMolecule.moleculeName + "\n");
        outputTextArea.append("Largest molecule: " + largestMolecule.moleculeName + "\n\n");

    }

    /**
     * Add a new molecule into the database
     */
    public void addMolecule(Molecule molecule) {
        if (molecule == null) {
            outputTextArea.append("molecule == null\n");
            return;
        }
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
            outputTextArea.append("No ArrayList with correct # of atoms" + "\n");
            return null;
        }
        ArrayList<Molecule> moleculesWithSameNumAtoms = db.get(numAtoms);

        // Iterate through the array list of molecules with the same number of atoms
        for (Molecule dbMolecule : moleculesWithSameNumAtoms) {
            outputTextArea.append(dbMolecule.moleculeName + " vs " + molecule.moleculeName + "\n");
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
    }

    /**
     * Load database from file system
     */
    public void load(String filename) throws IOException {
        FileInputStream fileInStream = new FileInputStream(filename);
        ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
        try {
            this.db = (HashMap<Integer, ArrayList<Molecule>>) objInStream.readObject();
            outputTextArea.append("Database loaded successfully." + "\n");
        } catch (IOException | ClassNotFoundException e) {
            outputTextArea.append("Error loading database: " + e.getMessage() + "\n");
        }
        objInStream.close();
        fileInStream.close();
    }

}
