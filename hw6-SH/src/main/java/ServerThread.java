import java.io.*;
import java.net.Socket;

/**
 * ServerThread is a thread handler for the server
 */
public class ServerThread extends Thread {

  private Socket s;
  private Server server;
  private BufferedReader reader;
  private PrintWriter out;
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
    this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
    this.out = new PrintWriter(s.getOutputStream(), true);
    this.protocol = new Protocol(server.getClientsMap(), reader, out);
  }


  @Override
  public void run(){
    try {
      handleRequest();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleRequest() throws IOException {
    String input = "";
    while ((input = reader.readLine()) != null){
      System.out.println(input);
      String[] tokens = input.split(" ");
      int identifier = Integer.parseInt(tokens[0]);
      if (protocol.getUsername() == null) {
        out.println(protocol.processInput(identifier, tokens));
        this.server.getClientsMap().put(protocol.getUsername(), this);
      } else {
        out.println(protocol.processInput(identifier, tokens));
      }

      System.out.println("The protocol username is : " + protocol.getUsername());
      System.out.println("The clientmap size is : " + this.server.getClientsMap().size());
    }
    System.out.println("Client " + protocol.getUsername() + " has left the chat room.");
  }
}