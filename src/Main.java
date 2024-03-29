import java.io.*;
import java.net.*;

public class Main {

  static MoleculeDatabase moleculeDb;

  public static void dbLoad() throws IOException, ClassNotFoundException {
    FileInputStream fileInStream = new FileInputStream("molecule.db");
    ObjectInputStream objInStream = new ObjectInputStream(fileInStream);
    moleculeDb = (MoleculeDatabase) objInStream.readObject();
    objInStream.close();
    fileInStream.close();
  }

  public static void dbSave() throws IOException {
    FileOutputStream fileOutStream = new FileOutputStream("molecule.db");
    ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
    objOutStream.writeObject(moleculeDb);
    objOutStream.close();
    fileOutStream.close();
  }

  public static void runClient(Socket clientSocket, String argument)
      throws IOException {
    OutputStream outStream = clientSocket.getOutputStream();
    PrintWriter writer = new PrintWriter(outStream);
    writer.println(argument);
    writer.flush();
    writer.close();
    outStream.close();
  }

  public static void runServer(ServerSocket serverSocket)
      throws IOException {
    try {
      dbLoad();
    } catch (IOException e) {
      moleculeDb = new MoleculeDatabase();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    Socket clientSocket = serverSocket.accept();
    InputStream inStream = clientSocket.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
    String[] arguments = reader.readLine().split(" ");
    String command = arguments[0];
    while (!command.equals("quit")) {
      String filePath = arguments[1];

      if (command.equals("--addMolecule")) {

      } else if (command.equals("--findMolecule")) {

      }

      clientSocket = serverSocket.accept();
      inStream = clientSocket.getInputStream();
      reader = new BufferedReader(new InputStreamReader(inStream));
      arguments = reader.readLine().split(" ");
      command = arguments[0];
    }
    reader.close();
    inStream.close();
    clientSocket.close();
  }

  public static void main(String[] args) throws IOException {
    final int PORT_NUMBER = Integer.parseInt(args[0]);
    String command = "";
    String filePath = "";
    if (args.length == 2) {
      command = args[1];
    } else if (args.length == 3) {
      command = args[1];
      filePath = args[2];
    } else {
      System.out.println("invalid number of arguments");
    }

    try (Socket clientSocket = new Socket("localhost", PORT_NUMBER)) {
      runClient(clientSocket, command + " " + filePath);
//      clientSocket.close();
    } catch (ConnectException e) {
      ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
      serverSocket.setSoTimeout(60 * 1000);
      runServer(serverSocket);
      serverSocket.close();
      dbSave();
      System.out.println("goodbye");
    }
  }
}