import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * ServerThread is a thread handler for the server
 */
public class ServerThread extends Thread {

  private Socket s;
  private Server server;
  private DataInputStream serverIn;
  private DataOutputStream serverOut;
  private Protocol protocol;

  /**
   * Server class constructor.
   *
   * @param s      socket
   * @param server server
   * @throws IOException throws if fail to connect to this port and create server socket.
   */
  public ServerThread(Socket s, Server server) throws IOException {
    this.s = s;
    this.server = server;
    this.serverIn = new DataInputStream(s.getInputStream());
    this.serverOut = new DataOutputStream(s.getOutputStream());
    this.protocol = new Protocol(server.getClientsMap(), serverIn, serverOut);
  }

  /**
   * @return return protocol of the server thread
   */
  public Protocol getProtocol() {
    return protocol;
  }

  /**
   * Execute thread
   */
  @Override
  public void run() {
    try {
      handleRequest();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handle request from client
   * @throws IOException throw an IO exception
   */
  private void handleRequest() throws IOException {
    while (s.isConnected()) {
      int identifiers = this.serverIn.readInt();
      if (protocol.getUsername() == null) {
        protocol.processInput(identifiers);
        this.server.getClientsMap().put(protocol.getUsername(), this);
      } else {
        protocol.processInput(identifiers);
      }
      System.out.println("The protocol username is : " + protocol.getUsername());
      System.out.println("The number of active users in total: " + this.server.getClientsMap().size());
    }
    System.out.println("Client " + protocol.getUsername() + " has left the chat room.");
  }
}