import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


/**
 *
 */
public class Client{

  private String serverName;
  private int serverPort;
  private Socket socket;
  private OutputStream clientOut;
  private BufferedReader clientIn;


  public Client(String serverName, int serverPort) {
    this.serverName = serverName;
    this.serverPort = serverPort;
  }

  public static void main(String[] args) {
    boolean connected = false;
    Client client = null;
    Scanner scanner = new Scanner(System.in);
    do {
      //ask users to enter serverName and portNumber
      checkInMenu();
      String server= scanner.nextLine();
      int port = Integer.parseInt(scanner.nextLine());
      client = new Client(server, port);
      //goes to main menu(user interface)
      connected = client.connect();
    }while (!connected);
    System.out.println("connect successful");
    printMenu();
    //read user command from terminal
    client.handleInputCmd(scanner.nextLine());
  }

  private void handleInputCmd(String cmd) {
    try {
      this.clientOut.write(cmd.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void checkInMenu(){
    System.out.println("Enter server name and port number: ");
  }

  private static void printMenu() {
    System.out.println("logoff: sends a DISCONNECT_MESSAGE to the server");
    System.out.println("who: sends a QUERY_CONNECTED_USERS to the server");
    System.out.println("@user: sends a DIRECT_MESSAGE to the specified user to the server");
    System.out.println("@all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected");
    System.out.println("!user: sends a SEND_INSULT message to the server, to be sent to the specified user");
  }

  private boolean connect(){
    try {
      //create a socket connect to the server
      socket = new Socket(InetAddress.getByName(serverName), serverPort);
      //client writes to the server
      clientOut = socket.getOutputStream();
      //receive message from the server
      clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}
