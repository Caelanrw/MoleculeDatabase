import java.io.*;
import java.net.*;
package edu.bu.ec504.project;

public class Main {

    /**
     * Method to run the client side of the program
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
    public static void runServer(ServerSocket serverSocket, String filename) throws IOException {
        // Load the database
        MoleculeDatabase.load(filename);
        // Accept incoming client connections
        Socket clientSocket = serverSocket.accept();
        InputStream inStream = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String[] arguments = reader.readLine().split(" ");
        String command = arguments[0];
        // Continue processing commands until "quit" command is received
        while (!command.equals("quit")) {
            String filePath = arguments[1];

            // Perform actions based on the received command
            if (command.equals("--addMolecule")) {
              // Add molecule logic here
            } else if (command.equals("--findMolecule")) {
              // Find molecule logic here
            }

            // Accept the next client connection
            clientSocket = serverSocket.accept();
            inStream = clientSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inStream));
            arguments = reader.readLine().split(" ");
            command = arguments[0];
        }
        // Close the streams and save the database before exiting
        reader.close();
        inStream.close();
        clientSocket.close();
        MoleculeDatabase.save(filename);
        System.out.println("Goodbye");
    }

    /**
     * Main method to start the program
     */
    public static void main(String[] args) throws IOException {
        // Get the port number from the command line arguments
        final int PORT_NUMBER = Integer.parseInt(args[0]);
        // Set the default filename for the database
        String filename = "molecule.db";

        // Check if an alternative filename is provided as a command line argument
        if (args.length > 1) {
            filename = args[1];
        }

        try (Socket clientSocket = new Socket("localhost", PORT_NUMBER)) {
            // If a client connection is successful, run the client side of the program
            runClient(clientSocket, filename);
        }
        catch (ConnectException e) {
            // If a client connection fails, run the server side of the program
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            serverSocket.setSoTimeout(60 * 1000);
            runServer(serverSocket, filename);
            serverSocket.close();
        }
    }
}
