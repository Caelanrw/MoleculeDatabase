package edu.bu.ec504.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a molecule containing atoms and edges.
 */
public class Molecule implements Serializable {

    //FIELDS
    public String moleculeName; // name of the molecule
    int numAtoms; // number of atoms in the molecule
    int numEdges; // number of edges in the molecule
    ArrayList<Atom> atomArrayList;  // list of atoms in the molecule
    int[] numElements; // array to store quantity of each element

    /**
     * Constructs a molecule by reading data from a file.
     *
     * @param moleculeFile the file containing the molecule.
     */
    public Molecule(String moleculeFile) {
        //Init values
        numElements = new int[119];

        int f, l;

        // Read and process input
        try (BufferedReader reader = new BufferedReader(new FileReader(moleculeFile))) { //Might want to move reader outside molecule class in the future to save space
            String line;
            moleculeName = reader.readLine(); //Reads name
            numAtoms = Integer.parseInt(reader.readLine()); //Reads # of atoms
            atomArrayList = new ArrayList<Atom>(numAtoms); //Creates arraylist of atoms
            for (int ii = 0; ii < numAtoms; ii++) {
                line = reader.readLine();
                PeriodicTable element = PeriodicTable.valueOf(line);
                int num = element.getAtomicNumber();  //Looks up element in Periodic Table
                atomArrayList.add(new Atom(line + numElements[num], num)); //Adds atom to list (with index)
                numElements[num]++; //Increases count for specific atom
            }
            while ((line = reader.readLine()) != null) {
                numEdges++; //Counts # of edges
                int beginIndex = line.indexOf(' ') + 1;
                f = Integer.parseInt(line.substring(0, beginIndex - 1)); //Reads first atom in edge
                l = Integer.parseInt(line.substring(beginIndex)); //Reads second atom in edge
                atomArrayList.get(f).addEdge(atomArrayList.get(l)); //Marks edge for first atom
                atomArrayList.get(l).addEdge(atomArrayList.get(f)); //Marks edge for second atom
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    /**
     * Compare two molecules and check if they are isomorphic.
     *
     * @param otherMolecule The molecule to compare with.
     * @return The first molecule if they are equal, otherwise null.
     */
    public Molecule areMoleculesEqual(Molecule otherMolecule) {
        // Compare the name of the molecule
//        if (!this.moleculeName.equals(otherMolecule.moleculeName)) {
//            System.out.println("different names");
//            return null; // Names are different, molecules are not equal
//        }

        // Compare the number of atoms
//        if (this.numAtoms != otherMolecule.numAtoms) {
//            System.out.println("different numAtoms");
//            return null; // Number of atoms is different, molecules are not equal
//        }

        // Compare the number of elements
        if (!Arrays.equals(this.numElements, otherMolecule.numElements)) {
            System.out.println("different numElements");
            return null; // Number of elements is different, molecules are not equal
        }

        // Compare the number of edges
        if (this.numEdges != otherMolecule.numEdges) {
            System.out.println("different numEdges");
            return null; // Number of edges is different, molecules are not equal
        }

        // Compare the atom lists
        for(Atom dbAtom : this.atomArrayList) {
            boolean atomFound = false;
            for (Atom newAtom : otherMolecule.atomArrayList) {
                if (newAtom.degree == dbAtom.degree && newAtom.elementType == dbAtom.elementType) { //Check if degrees and elements are the same
                    boolean sameConnected = true;
                    // Compare connected of each atom
                    //for each connected atom in dbAtom
                    for (Atom.ElemOrderPair dbValues : dbAtom.connected.values()) {
                        boolean matchingEdgeIsFound = false;
                        //for each connected element in  input
                        for (Atom.ElemOrderPair newAtomValues : newAtom.connected.values()) {
                            //if its a match
                            if (dbValues.eType == newAtomValues.eType && dbValues.bondOrder == newAtomValues.bondOrder) {
                                //mark the newAtom edge as already found
                                newAtomValues.eType = -1;
                                matchingEdgeIsFound = true;
                                //go to next connected atom in dbAtom (break)
                                break;
                            }
                        }
                        if (!matchingEdgeIsFound)
                            sameConnected = false;
                    }
                    if (sameConnected) {
                        atomFound = true;
                        newAtom.degree = -1;
                        break;
                    }
                    //if connected isnt the same
                    //go to next newAtom;
                }
            }
            if(!atomFound)
                return null;
        }


                        //boolean matchingEdgeIsFound = false;
                        //for each connected element in cyphered input
                            //if its a match
                                //mark the newAtom edge as already found
                                //matchingEdgeIsFound = true;
                                //go to next connected atom in dbAtom (break)
                            //if its not a match
                                //go to next connected element in cyphered input (do nothing)
                        //if !matchingEdgeIsFound, this is the wrong "newAtom"
                            //sameConnected = false;
                    //if connected is the same
                        //atomFound = true;
                        //change newAtom degree to invalid number (0)
                        //go to next dbAtom;
                    //if connected isnt the same
                        //go to next newAtom;
            //if !atomFound
                //return null;





        // If all comparisons passed, the molecules are equal
        return this;
    }

    /**
     * Return number of atoms of the molecule
     */
    public int getNumAtoms() {
        return numAtoms;
    }


}

