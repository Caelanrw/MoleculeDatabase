import edu.bu.ec504.project.Molecule;

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
public class MoleculeDatabase {

    public HashMap<Integer, ArrayList<Molecule>> db;   // Molecule database

    public boolean verbose = false;

    public void printVerbose(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

    /**
     * Constructs a database
     */
    public MoleculeDatabase() {
        this.db = new HashMap<>();
    }

    public void printDb() {
        for (Integer atomCount : this.db.keySet()) {
            System.out.println("\n# atoms: " + atomCount.toString());
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
        if (molecule == null) {
            printVerbose("molecule == null");
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
            printVerbose("no ArrayList with correct # of atoms");
            return null;
        }
        ArrayList<Molecule> moleculesWithSameNumAtoms = db.get(numAtoms);

        // Iterate through the array list of molecules with the same number of atoms
        for (Molecule dbMolecule : moleculesWithSameNumAtoms) {
            printVerbose(dbMolecule.moleculeName + " vs " + molecule.moleculeName);
            Molecule result = dbMolecule.areMoleculesEqual(molecule);
            if (result != null) {
                return result; // Return the isomorphic molecule
            }
        }
        return null; // Return null if molecule not found
    }


    /**
     * Find the most similar Molecule from the database
     */
    public Molecule similarMolecule(Molecule molecule) {

        // If an exact match is not found then find the most similar
        int maxResult=0;
        Molecule similar=null;
        for (Map.Entry<Integer, ArrayList<Molecule>> entry : db.entrySet()) {
            // Access the key and value of each entry
            Integer numberAtoms = entry.getKey();

            //only check for similarity if they have similar number of atoms within tolerance of 100
            if( (molecule.getNumAtoms()-100)<numberAtoms && numberAtoms<(molecule.getNumAtoms()+100) )
            {
                for (Molecule dbMolecule : db.get(numberAtoms)) {
                    int res = dbMolecule.mostSimilar(molecule);
                    if (res > maxResult) {
                        similar = dbMolecule; // save the similar molecule
                        maxResult = res;
                    }
                }
            }
        }

        return similar;
    }

    /**
     * Find subgraph
     */
    public ArrayList<Molecule> findSubgraph(Molecule molecule) {
        ArrayList<Molecule> returnList = new ArrayList<Molecule>();
        int startingNumber = molecule.getNumAtoms();
        for(int ii : db.keySet()) {
            if (ii >= startingNumber) {
                for(Molecule m: db.get(ii)) {
                    if(m.isSubGraphPresent(molecule) != null) {
                        returnList.add(m);
                        System.out.println(m.moleculeName);
                    }
                }
            }
        }

        return returnList;
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
            printVerbose("Database loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            printVerbose("Error loading database: " + e.getMessage());
        }
        objInStream.close();
        fileInStream.close();
    }
}
