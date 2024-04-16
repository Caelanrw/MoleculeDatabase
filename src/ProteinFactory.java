import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class ProteinFactory {

    private static ProteinFactory INSTANCE;

    private ProteinFactory() throws IOException {

    }

    public static ProteinFactory getInstance() throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new ProteinFactory();
        }
        return INSTANCE;
    }

    static Random rng = new Random();

    static final AminoAcid ALANINE;
    static final AminoAcid GLYCINE;

    static {
        try {
            ALANINE = new AminoAcid("testcases/amino_acid/Alanine.txt",
                    2, 12, 10, 5, 4);
            GLYCINE = new AminoAcid("testcases/amino_acid/Glycine.txt",
                    1, 9, 7, 4, 3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public class AminoAcid {
        int atomCount;
        StringBuffer atomBuffer;
        int bondCount = 0;
        ArrayList<Integer> bondList = new ArrayList<>();
        String name;
        ArrayList<Integer> termini = new ArrayList<>();
        int terminusC;
        int terminusHN;
        int terminusHO;
        int terminusN;
        int terminusO;

        public AminoAcid(
                String filePath, int terminusC, int terminusHN, int terminusHO, int terminusN, int terminusO)
                throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                name = reader.readLine();
                atomCount = Integer.parseInt(reader.readLine());
                atomBuffer = new StringBuffer(atomCount);
                for (int atomIdx = 0; atomIdx < atomCount; atomIdx++) {
                    atomBuffer.append(reader.readLine());
                }
                String bond;
                while ((bond = reader.readLine()) != null) {
                    String[] atoms = bond.split(" ");
                    bondList.add(Integer.parseInt(atoms[0]));
                    bondList.add(Integer.parseInt(atoms[1]));
                    bondCount++;
                }
            }
            termini.add(terminusHN);
            termini.add(terminusHO);
            termini.add(terminusO);
            termini.sort(null);
            this.terminusC = terminusC;
            this.terminusHN = terminusHN;
            this.terminusHO = terminusHO;
            this.terminusN = terminusN;
            this.terminusO = terminusO;
        }
    }

    static public class Protein {
        Integer atomCount;
        StringBuffer atomBuffer = new StringBuffer();
        ArrayList<String> bondList = new ArrayList<>();
        int terminusC;
        boolean complete = false;
        String name;

        public Protein(String name, AminoAcid aminoAcid) {
            int min = Math.min(aminoAcid.terminusHO, aminoAcid.terminusO);
            atomBuffer.append(aminoAcid.atomBuffer, 0, min);
            int max = Math.max(aminoAcid.terminusHO, aminoAcid.terminusO);
            atomBuffer.append(aminoAcid.atomBuffer, min + 1, max);
            atomBuffer.append(aminoAcid.atomBuffer, max + 1, aminoAcid.atomCount);

            atomCount = aminoAcid.atomCount - 2;
            int bondCount = aminoAcid.bondCount;
            int terminusHO = aminoAcid.terminusHO;
            int terminusO = aminoAcid.terminusO;
            for (int bondIdx = 0; bondIdx < bondCount; bondIdx++) {
                int atom0 = aminoAcid.bondList.get(2 * bondIdx);
                if (atom0 == terminusHO || atom0 == terminusO) {
                    continue;
                }
                int atom1 = aminoAcid.bondList.get(2 * bondIdx + 1);
                if (atom1 == terminusHO || atom1 == terminusO) {
                    continue;
                }
                int atomDecrement = 0;
                atomDecrement += (atom0 > terminusHO) ? 1 : 0;
                atomDecrement += (atom0 > terminusO) ? 1 : 0;
                atom0 -= atomDecrement;
                atomDecrement = 0;
                atomDecrement += (atom1 > terminusHO) ? 1 : 0;
                atomDecrement += (atom1 > terminusO) ? 1 : 0;
                atom1 -= atomDecrement;
                bondList.add(String.format("%d %d", atom0, atom1));
            }
            int newTerminusC = aminoAcid.terminusC;
            newTerminusC -= (newTerminusC > terminusHO) ? 1 : 0;
            newTerminusC -= (newTerminusC > terminusO) ? 1 : 0;
            terminusC = newTerminusC;

            this.name = name;
        }

        public void print() {
            System.out.println(atomCount);
            System.out.println(atomBuffer);
            System.out.println(bondList);
            System.out.println(terminusC);
            System.out.println(complete);
        }

        public void write(String folderName) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(folderName + "/" + name + ".txt"));
            writer.write(name + "\n");
            writer.write(atomCount.toString());
            writer.write("\n");
            for (int ii = 0; ii < atomCount; ii++) {
                writer.write(atomBuffer.charAt(ii) + "\n");
            }
            for (String b : bondList) {
                writer.write(b + "\n");
            }
            writer.close();
        }
    }

    public static void addAminoAcid(Protein protein, AminoAcid aminoAcid, boolean isLast) {
        if (protein.complete) {
            System.out.println("cannot add to completed protein");
            return;
        }
        if (isLast) {
            int baseIdx = protein.atomCount;
            int indexH = aminoAcid.atomBuffer.indexOf("H");
            protein.atomBuffer.append(aminoAcid.atomBuffer, 0, indexH);
            protein.atomBuffer.append(aminoAcid.atomBuffer, indexH + 1, aminoAcid.atomCount);
            protein.atomCount += aminoAcid.atomCount - 1;
            protein.bondList.add(String.format("%d %d", protein.terminusC, baseIdx + aminoAcid.terminusN));
            int bondCount = aminoAcid.bondCount;
            int terminusHN = aminoAcid.terminusHN;
            for (int bondIdx = 0; bondIdx < bondCount; bondIdx++) {
                int atom0 = aminoAcid.bondList.get(2 * bondIdx);
                if (atom0 == terminusHN) {
                    continue;
                } else if (atom0 > terminusHN) {
                    atom0--;
                }
                int atom1 = aminoAcid.bondList.get(2 * bondIdx + 1);
                if (atom1 == terminusHN) {
                    continue;
                } else if (atom1 > terminusHN) {
                    atom1--;
                }
                protein.bondList.add(String.format("%d %d", baseIdx + atom0, baseIdx + atom1));
            }
            protein.complete = true;
        } else {
            int baseIdx = protein.atomCount;
            int start = -1;
            for (Integer t : aminoAcid.termini) {
                protein.atomBuffer.append(aminoAcid.atomBuffer.substring(start + 1, t));
                start = t;
            }
            protein.atomBuffer.append(aminoAcid.atomBuffer.substring(start + 1));
            protein.atomCount += aminoAcid.atomCount - 3;
            protein.bondList.add(String.format("%d %d", protein.terminusC, baseIdx + aminoAcid.terminusN));
            int bondCount = aminoAcid.bondCount;
            int terminusHN = aminoAcid.terminusHN;
            int terminusHO = aminoAcid.terminusHO;
            int terminusO = aminoAcid.terminusO;
            for (int bondIdx = 0; bondIdx < bondCount; bondIdx++) {
                int atom0 = aminoAcid.bondList.get(2 * bondIdx);
                int atom1 = aminoAcid.bondList.get(2 * bondIdx + 1);
                if (aminoAcid.termini.contains(atom0) || aminoAcid.termini.contains(atom1)) {
                    continue;
                }
                int atomDecrement = 0;
                atomDecrement += (atom0 > terminusHN) ? 1 : 0;
                atomDecrement += (atom0 > terminusHO) ? 1 : 0;
                atomDecrement += (atom0 > terminusO) ? 1 : 0;
                atom0 -= atomDecrement;
                atomDecrement = 0;
                atomDecrement += (atom1 > terminusHN) ? 1 : 0;
                atomDecrement += (atom1 > terminusHO) ? 1 : 0;
                atomDecrement += (atom1 > terminusO) ? 1 : 0;
                atom1 -= atomDecrement;
                protein.bondList.add(String.format("%d %d", baseIdx + atom0, baseIdx + atom1));
            }
            int newTerminusC = aminoAcid.terminusC;
            newTerminusC -= (newTerminusC > terminusHN) ? 1 : 0;
            newTerminusC -= (newTerminusC > terminusHO) ? 1 : 0;
            newTerminusC -= (newTerminusC > terminusO) ? 1 : 0;
            protein.terminusC = newTerminusC;
        }
    }

    public void generateProtein(int atomCount) {

    }

    public void generateProteins(int proteinCount, int atomsPerProtein) {

    }

    public static void main(String[] args) throws IOException {
        Protein protein = new Protein("protein1", GLYCINE);
        addAminoAcid(protein, GLYCINE, true);
        protein.write("testcases/protein");
    }
}
