package edu.bu.ec504.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Molecule {

    // TODO:save the molecule information

    // TODO:read the text file (adjacency list), line by line
    //Current implementation is in the constructor

    public Molecule(String MoleculeFile){
        // Read and process input
        try (BufferedReader reader = new BufferedReader(new FileReader(MoleculeFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
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

    int numAtoms;

    int numEdges;

    // numDegrees for each atom;

    int[] numElements; //stores quantity of each element




}

