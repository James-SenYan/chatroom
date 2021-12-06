import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server is the chat room server.
 */
public class Server {

  private ConcurrentHashMap<String, ServerThread> clientsMap;
  private ServerSocket serverSocket;
  private static final int PORT_NUMBER = 8000;

  /**
   * Server class constructor.
   *
   * @param port an integer indicates the port number.
   * @throws IOException throws if fail to connect to this port and create server socket.
   */
  public Server(int port) throws IOException {
    this.serverSocket = new ServerSocket(port);
    this.clientsMap = new ConcurrentHashMap<>();
  }

  /**
   * Returns this Server's clientsMap.
   *
   * @return clientsMap.
   */
  public ConcurrentHashMap<String, ServerThread> getClientsMap() {
    return clientsMap;
  }

  /**
   * Run this Server.
   *
   * @throws IOException if fails to get accepted client or open sub-thread for new accepted client.
   */
  public void run() throws IOException {
    System.out.println("Server starts running on port " + PORT_NUMBER);
    while (true) {
      Socket s = serverSocket.accept();
      System.out.println("New client request received : " + s.getInetAddress().getHostAddress());
      System.out.println("Preparing the chat room for this client...");

      ServerThread serverThread = new ServerThread(s, this);
      Thread thread = new Thread(serverThread);

      thread.start();
    }
  }

  /**
   * Main to start a server
   *
   * @param args user input set default null.
   * @throws IOException throws if fail to initiate this server.
   */
  public static void main(String[] args) throws IOException {
    new Server(PORT_NUMBER).run();
  }




}
