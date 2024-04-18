package edu.bu.ec504.project;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Molecule> moleculeArrayList = new ArrayList<Molecule>();
        final String filename="example.txt";
        File directory = new File("Molecules");
        // Check if the directory exists
        if (directory.exists() && directory.isDirectory()) {
            // Get list of files in the directory
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        for(int oo = 0;oo <1000;oo++)
                            moleculeArrayList.add(new Molecule(file.getPath()));
                    }
                }
            }
        }
        System.out.println(moleculeArrayList.size());
        Molecule tester = new Molecule(filename);
        for(Molecule m: moleculeArrayList) {
            if(m.isSubGraphPresent(tester) != null) {
                System.out.println(m.moleculeName);
            }
        }
    }

}
