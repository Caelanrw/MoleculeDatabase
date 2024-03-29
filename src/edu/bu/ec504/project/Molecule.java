package edu.bu.ec504.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Molecule {

    // TODO:save the molecule information

    // TODO:read the text file (adjacency list), line by line
    //Current implementation is in the constructor

    public Molecule(String MoleculeFile){
        //Init values
        numElements= new int [119];
        pt.put("C",6);
        pt.put("H",1);
        pt.put("N",7);
        pt.put("O",8);
        pt.put("S",16);
        //Creates dictionary of elements, want to add more to this & relocate this variable outside of molecule class to save space.
        int f,l;

        // Read and process input
        try (BufferedReader reader = new BufferedReader(new FileReader(MoleculeFile))) { //Might want to move reader outside molecule class in the future to save space
            String line;
            moleculeName = reader.readLine(); //Reads name
            numAtoms = Integer.parseInt(reader.readLine()); //Reads # of atoms
            atomArrayList = new ArrayList<Atom>(numAtoms); //Creates arraylist of atoms
            for(int ii = 0; ii <numAtoms;ii++) {
                line = reader.readLine();
                int num = pt.get(line); //Looks up element in Periodic Table
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
                System.out.println(line); // TODO: we can change this to parse the file rather than print it
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    public void saveData(String s){

    }


    // TODO: add molecule to the database


    //FIELDS
    String moleculeName;
    Map <String, Integer> pt = new HashMap<String,Integer>();
    int numAtoms;
    int numEdges;
    ArrayList<Atom> atomArrayList;
    int[] numElements; //stores quantity of each element




}

