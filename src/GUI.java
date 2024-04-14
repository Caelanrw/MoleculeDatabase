import edu.bu.ec504.project.Molecule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class GUI extends JFrame {
    private JTextArea outputTextArea;
    private JButton chooseFileButton;
    private JButton addMoleculeButton;
    private JButton findMoleculeButton;
    private JButton statisticsButton;
    private JButton viewMoleculeButton;
    private JTextField filePathField;

    private static MoleculeDatabase moleculeDb;
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

        // Create components
        outputTextArea = new JTextArea(20, 50); // the text area for all outputs
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        chooseFileButton = new JButton("Choose File");
        addMoleculeButton = new JButton("Add Molecule");
        findMoleculeButton = new JButton("Find Molecule");
        statisticsButton = new JButton("Database Statistics");
        viewMoleculeButton = new JButton("Display Molecule");
        filePathField = new JTextField(20); // to show the file path

        // Add components to the JFrame
        JPanel controlPanel = new JPanel();
        controlPanel.add(chooseFileButton);
        controlPanel.add(addMoleculeButton);
        controlPanel.add(findMoleculeButton);
        controlPanel.add(statisticsButton);
        controlPanel.add(new JLabel("File Path:"));
        controlPanel.add(filePathField);
        add(controlPanel, BorderLayout.NORTH); // to show the control panel (e.g., buttons)
        add(scrollPane, BorderLayout.CENTER); // to show the printed output text area

        // Initialize molecule database
        MoleculeDatabase moleculeDb = new MoleculeDatabase(outputTextArea);

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

        // Action listener for Add Molecule button
        addMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the molecule path from the text field
                String moleculePath = filePathField.getText();
                // Execute the addMolecule command
                moleculeDb.addMolecule(new Molecule(moleculePath));
                // Display output
                outputTextArea.append("Molecule added: " + moleculePath + "\n");
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
                // Display the result in the output text area
                if (molecule == null) {
                    outputTextArea.append("NOT FOUND\n");
                } else {
                    outputTextArea.append("FOUND\n");
                    outputTextArea.append("Molecule found: " + moleculePath + "\n");
                }
            }
        });

        // Action listener for View Molecule button
        viewMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                // Get the molecule path from the text field
//                String moleculePath = moleculePathField.getText();
//                // Instantiate MoleculeViewer and display the molecule
//                MoleculeViewer moleculeViewer = new MoleculeViewer(moleculePath);
//                moleculeViewer.setVisible(true);
            }
        });

        // Action listener for Statistics button
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moleculeDb.printDb(); // print the content of database
            }
        });

        // Connect to client or server
        connectToServerOrClient();

        // Display the JFrame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
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
            // If a client connection fails, run the server side of the program
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                serverSocket.setSoTimeout(60 * 1000);

                // Set the default filename for the database
                String dbName = "molecule.db";

                // Initialize the database
                initDb(dbName);

                // Accept incoming client connections
                clientSocket = serverSocket.accept();
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Close the server socket after accepting the connection
                serverSocket.close();
                moleculeDb.save(dbName);

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
        moleculeDb = new MoleculeDatabase(outputTextArea);
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
