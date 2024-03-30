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
    MoleculeDatabase moleculeDb = new MoleculeDatabase();
    moleculeDb.load(dbName);

    // Continue processing commands until "--quit" command is received
    while (!cmd.equals("--quit")) {
      // Perform actions based on the received command
      if (moleculePath.equals("")) {
        System.out.println("moleculePath is empty. moleculePath cannot be empty");
      } else if (cmd.equals("--addMolecule")) {
        System.out.println("received: add " + moleculePath);

        // Add molecule logic here
      } else if (cmd.equals("--findMolecules")) {
        System.out.println("received: find " + moleculePath);

        // Find molecule logic here
      } else {
        System.out.println("unrecognized command: " + cmd);
      }

      // Accept incoming client connections
      Socket clientSocket = serverSocket.accept();
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
    System.out.println("Goodbye");
  }

  /**
   * Main method to start the program
   */
  public static void main(String[] args) throws IOException {
    System.out.println("\r");
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
