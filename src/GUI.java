import edu.bu.ec504.project.Molecule;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI extends JFrame {
    private JTextArea outputTextArea;
    private JButton chooseFileButton;
    private JButton addMoleculeButton;
    private JButton findMoleculeButton;
    private JButton statisticsButton;
    private JButton displayMoleculeButton;
    private JButton findSubgraphButton;
    private JButton downloadPubChemButton;
    private JTextField filePathField;
    private static MDB moleculeDb;
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    /**
     * GUI constructor
     */
    public GUI() {
        // Set up the JFrame
        setTitle("Molecule Database");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // Create components
        outputTextArea = new JTextArea(20, 50); // the text area for all outputs
        outputTextArea.setBackground(Color.BLACK); // Set the background color of the text area
        outputTextArea.setForeground(Color.WHITE); // Set the text color
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        chooseFileButton = new JButton("Choose File");
        downloadPubChemButton = new JButton("Download PubChem");
        addMoleculeButton = new JButton("Add Molecule");
        findMoleculeButton = new JButton("Find Molecule");
        findSubgraphButton = new JButton("Find Subgraph");
        statisticsButton = new JButton("Database Statistics");
        displayMoleculeButton = new JButton("Display Molecule");
        filePathField = new JTextField(20); // to show the file path
        JLabel filePathLabel = new JLabel("File Path:");
        filePathLabel.setForeground(Color.WHITE); // Set the text color
        filePathField.setBackground(Color.WHITE); // Set the background color of the text field
        filePathField.setForeground(Color.BLACK); // Set the text color

        // Add components to the JFrame
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.BLACK);
        controlPanel.add(chooseFileButton);
        controlPanel.add(downloadPubChemButton);
        controlPanel.add(addMoleculeButton);
        controlPanel.add(findMoleculeButton);
        controlPanel.add(findSubgraphButton);
        controlPanel.add(displayMoleculeButton);
        controlPanel.add(statisticsButton);
        controlPanel.add(filePathLabel);
        controlPanel.add(filePathField);
        add(controlPanel, BorderLayout.NORTH); // to show the control panel (e.g., buttons)
        add(scrollPane, BorderLayout.CENTER); // to show the printed output text area

        // Initialize molecule database
        moleculeDb = new MDB(outputTextArea);

        // Action listener for Choose File button
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose a file");
                int result = fileChooser.showOpenDialog(GUI.this);
                // If a file is selected, set its path in the molecule path field
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        // Action listener for Download PubChem button
        downloadPubChemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the molecule path from the text field
                // Input format for the file path is "start,end" where 'start' and 'end' are the starting and ending CID indices of molecules in PubChem
                // For example, enter 12,24 to download molecules 12-24
                String twoIndices = filePathField.getText();

                // Repurposed moleculePath should be in format "start,end"
                String[] indexes = twoIndices.split(",");

                if (indexes.length == 2) {
                    String start = indexes[0];
                    String end = indexes[1];
                    moleculeDb.downloadPubChem(start, end);
                } else {
                    outputTextArea.append("Invalid Input" + "\n\n");
                }
            }
        });

        // Action listener for Add Molecule button
        addMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the molecule path from the text field
                String moleculePath = filePathField.getText();
                // Execute the addMolecule command
                moleculeDb.addMolecule(new Molecule(moleculePath));
                // Display output
                outputTextArea.append("Molecule added: " + moleculePath + "\n\n");
            }
        });

        // Action listener for Find Molecule button
        findMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the molecule path from the text field
                String moleculePath = filePathField.getText();
                // Execute the findMolecule command
                Molecule molecule = moleculeDb.findMolecule(new Molecule(moleculePath));
                if (molecule == null) {
                    outputTextArea.append("NO EXACT MATCH FOUND" + "\n\n");
                    molecule= moleculeDb.similarMolecule(new Molecule(moleculePath));
                    if(molecule!=null)
                    {
                        // Perform most similar function
                        outputTextArea.append(molecule.moleculeName + " is the most similar" + "\n\n");
                    }
                } else {
                    outputTextArea.append("FOUND\n\n");
                }
            }
        });

        // Action listener for Find Subgraph button
        findSubgraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String moleculePath = filePathField.getText();
                ArrayList<Molecule> mList = moleculeDb.findSubgraph(new Molecule(moleculePath));
                if (mList.isEmpty())
                    outputTextArea.append("No subgraphs found" + "\n\n");
                else
                    for (Molecule m : mList)
                        outputTextArea.append("Subgraph found: " + m.moleculeName + "\n\n");
            }
        });

        // Action listener for Display Molecule button
        displayMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Get the API for the molecule
                String moleculePath = filePathField.getText();
                String moleculeName = null;

                try (BufferedReader reader = new BufferedReader(new FileReader(moleculePath))) {
                    moleculeName = reader.readLine(); // Read the first line to get the molecule name

                } catch (IOException ex) {
                    System.err.println("Error reading the file: " + ex.getMessage());
                    outputTextArea.append("Error reading the file:" + ex.getMessage() + "\n\n");
                    return;
                }

                // https://cactus.nci.nih.gov/chemical/structure/isopropanol/image
                String imageURL = "https://cactus.nci.nih.gov/chemical/structure/" + moleculeName + "/image";

                try {
                    // Download the image from the URL
                    URL url = new URL(imageURL);
                    BufferedImage image = ImageIO.read(url);

                    // Create a JLabel to display the image
                    JLabel imageLabel = new JLabel(new ImageIcon(image));

                    // Create a new JFrame to display the image
                    JFrame imageFrame = new JFrame("Molecule Display");
                    imageFrame.getContentPane().add(imageLabel, BorderLayout.CENTER);
                    imageFrame.setSize(400, 400);
                    imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    imageFrame.setLocationRelativeTo(null); // Center the frame
                    imageFrame.setVisible(true); // Make the frame visible

                } catch (IOException ex) {
                    ex.printStackTrace();
                    outputTextArea.append("Molecule display failed. The provided molecule name may be incorrect or does not match any records in the PubChem database.\n\n");
                }
            }
        });

        // Action listener for Statistics button
        statisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moleculeDb.printDb(); // print the content of database
            }
        });

        // Window listener to save the database before closing the GUI
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    moleculeDb.save("molecule.db"); // save the database
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Connect to client or server
        connectToServerOrClient();

        // Display the JFrame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Load database on startup
        try {
            initDb("molecule.db");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Run as client or server
     */
    private void connectToServerOrClient() {
        try {
            // Run the client side
            clientSocket = new Socket("localhost", 5000);
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (ConnectException e) {
            // Run the server side
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                serverSocket.setSoTimeout(60 * 1000);

                // Close the server socket after accepting the connection
                serverSocket.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initialize a database
     */
    public void initDb(String dbName) throws IOException {
        // Load the database
        moleculeDb = new MDB(outputTextArea);
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            moleculeDb.load(dbName);
        }
    }

    /**
     * Main function
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI gui = new GUI();
            }
        });
    }
}
