package edu.bu.ec504.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Molecule {

    public Molecule(String MoleculeFile){
        //Init values
        numElements= new int [119];

        int f,l;

        // Read and process input
        try (BufferedReader reader = new BufferedReader(new FileReader(MoleculeFile))) { //Might want to move reader outside molecule class in the future to save space
            String line;
            moleculeName = reader.readLine(); //Reads name
            numAtoms = Integer.parseInt(reader.readLine()); //Reads # of atoms
            atomArrayList = new ArrayList<Atom>(numAtoms); //Creates arraylist of atoms
            for(int ii = 0; ii <numAtoms;ii++) {
                line = reader.readLine();
                PeriodicTable element= PeriodicTable.valueOf(line);
                int num = element.getAtomicNumber();  //Looks up element in Periodic Table
                atomArrayList.add(new Atom(line+numElements[num],num)); //Adds atom to list (with index)
                numElements[num]++; //Increases count for specific atom
            }
            while ((line = reader.readLine()) != null) {
                numEdges++; //Counts # of edges
                int beginIndex = line.indexOf(' ')+1;
                f = Integer.parseInt(line.substring(0,beginIndex-1)); //Reads first atom in edge
                l = Integer.parseInt(line.substring(beginIndex)); //Reads second atom in edge
                atomArrayList.get(f).addEdge(atomArrayList.get(l)); //Marks edge for first atom
                atomArrayList.get(l).addEdge(atomArrayList.get(f)); //Marks edge for second atom
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }


    //FIELDS
    String moleculeName;
    int numAtoms;
    int numEdges;
    ArrayList<Atom> atomArrayList;
    int[] numElements; //stores quantity of each element

}

