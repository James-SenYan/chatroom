import java.io.*;
import java.net.Socket;

/**
 * ServerThread is a thread handler for the server
 */
public class ServerThread extends Thread {

  private Socket s;
  private Server server;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Protocol protocol;

  /**
   * Server class constructor.
   *
   * @param s socket
   * @param server server
   * @throws IOException throws if fail to connect to this port and create server socket.
   */
  public ServerThread(Socket s, Server server) throws IOException {
    this.s = s;
    this.server = server;
    this.dis = new DataInputStream(s.getInputStream());
    this.dos = new DataOutputStream(s.getOutputStream());
    this.protocol = new Protocol(server.getClientsMap(), dis, dos);
  }

  @Override
  public void run() {
    do {
      try {
        int identifier = dis.readInt();
        protocol.processInput(identifier);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } while (protocol.getUsername() == null);

    this.server.getClientsMap().put(protocol.getUsername(), this);
    System.out.println("Welcome client " + protocol.getUsername() + " joining the chat room. <<");

    while (!s.isClosed()) {
      try {
        int identifier = dis.readInt();
        protocol.processInput(identifier);
      } catch (EOFException e) {
        try {
          s.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      } catch (IOException e) {
        String msg = e.getMessage();
        if (msg.equals("Connection reset") || msg.equals("Socket closed")) {
          try {
            s.close();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        } else {
          e.printStackTrace();
        }
      }

    }

    System.out.println("Client " + protocol.getUsername() + " has left the chat room.");
    this.server.getClientsMap().remove(protocol.getUsername());
  }

}