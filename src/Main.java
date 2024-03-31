import edu.bu.ec504.project.Molecule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    static MoleculeDatabase moleculeDb = null;
    static boolean verbose = false;

    public static void printVerbose(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

    public static void commandHandler1(String cmd) {
        switch (cmd) {
            case "--printDb":
                moleculeDb.printDb();
                break;
            default:
                printVerbose("unrecognized command: " + cmd);
                break;
        }
    }

    public static void commandHandler2(String cmd, String moleculePath) {
        switch (cmd) {
            case "--addMolecule":
                moleculeDb.addMolecule(new Molecule(moleculePath));
                break;
            case "--findMolecule":
                Molecule molecule = moleculeDb.findMolecule(new Molecule(moleculePath));
                if (molecule == null) {
                    System.out.println("NOT FOUND");
                } else {
                    printVerbose("FOUND");
                }
                break;
            default:
                printVerbose("unrecognized command: " + cmd);
                break;
        }
    }

    /**
     * Method to run the client side of the program
     *
     * @param clientSocket
     * @param argument
     * @throws IOException
     */
    public static void runClient(Socket clientSocket, String argument) throws IOException {
        // Set up output stream to send data to the server
        OutputStream outStream = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(outStream);

        // Write the argument to the output stream
        writer.println(argument);
        writer.flush();

        // Close the streams
        writer.close();
        outStream.close();
    }

    /**
     * Method to run the server side of the program
     */
    public static void runServer(ServerSocket serverSocket, String cmd, String moleculePath,
                                 String dbName) throws IOException {
        // Load the database
        moleculeDb = new MoleculeDatabase();
        moleculeDb.load(dbName);

        // Continue processing commands until "--quit" command is received
        while (!cmd.equals("--quit")) {
            // Perform actions based on the received command
            if (moleculePath.isEmpty()) {
                commandHandler1(cmd);
            } else {
                commandHandler2(cmd, moleculePath);
            }

            // Accept incoming client connections
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("connection timed out");
                break;
            }
            InputStream inStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String[] args = reader.readLine().split(" ");

            // close the streams
            reader.close();
            inStream.close();
            clientSocket.close();

            // parse argument from client
            cmd = args[0];
            moleculePath = "";
            if (args.length > 1) {
                moleculePath = args[1];
            }
        }

        // save the database before exiting
        moleculeDb.save(dbName);
        System.out.println("Database saved successfully.");
        System.out.println("Goodbye");
    }

    /**
     * Main method to start the program
     */
    public static void main(String[] args) throws IOException {
        final int ARG_COUNT = args.length;

        // Get the port number from the command line arguments
        final int PORT_NUMBER = Integer.parseInt(args[0]);

        // get other command line arguments
        String cmd = args[1];
        String moleculePath = "";
        if (ARG_COUNT > 2) {
            moleculePath = args[2];
        }

        // Set the default filename for the database
        String dbName = "molecule.db";

        // Check if an alternative filename is provided as a command line argument
        if (ARG_COUNT > 3) {
            dbName = args[3];
        }
        try (Socket clientSocket = new Socket("localhost", PORT_NUMBER)) {
            // If a client connection is successful, run the client side of the program
            runClient(clientSocket, cmd + " " + moleculePath);
        } catch (ConnectException e) {
            // If a client connection fails, run the server side of the program
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            serverSocket.setSoTimeout(60 * 1000);
            runServer(serverSocket, cmd, moleculePath, dbName);
            serverSocket.close();
        }
    }
}
