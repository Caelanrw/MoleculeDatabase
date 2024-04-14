import edu.bu.ec504.project.Molecule;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;

public class GUI extends JFrame {
    private JTextArea outputTextArea;
    private JButton chooseFileButton;
    private JButton addMoleculeButton;
    private JButton findMoleculeButton;
    private JButton statisticsButton;
    private JButton displayMoleculeButton;
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
        addMoleculeButton = new JButton("Add Molecule");
        findMoleculeButton = new JButton("Find Molecule");
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
        controlPanel.add(addMoleculeButton);
        controlPanel.add(findMoleculeButton);
        controlPanel.add(displayMoleculeButton);
        controlPanel.add(statisticsButton);
        controlPanel.add(filePathLabel);
        controlPanel.add(filePathField);
        add(controlPanel, BorderLayout.NORTH); // to show the control panel (e.g., buttons)
        add(scrollPane, BorderLayout.CENTER); // to show the printed output text area

        // Initialize molecule database
        MDB moleculeDb = new MDB(outputTextArea);

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
                // Display the result in the output text area
                if (molecule == null) {
                    outputTextArea.append("NOT FOUND\n\n");
                } else {
                    outputTextArea.append("FOUND\n\n");
                    outputTextArea.append("Molecule found: " + moleculePath + "\n\n");
                }
            }
        });

        // Action listener for Display Molecule button
        displayMoleculeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Get the API for the molecule
                String moleculePath = filePathField.getText();
                // Split the file path by "/"
                String[] parts = moleculePath.split("/");
                // Extract the filename (last part of the path)
                String filename = parts[parts.length - 1];
                // Remove the file extension (.txt) to get the molecule name
                String moleculeName = filename.replace(".txt", "");
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
                    // Handle the IOException
                    ex.printStackTrace(); // You can print the stack trace or handle it differently
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
                moleculeDb.save(dbName); // save the database

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
