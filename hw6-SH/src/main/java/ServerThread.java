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
//      out.println("Server about to process your request, plz wait...");
      out.println(protocol.processInput(identifier, tokens));
      System.out.println("The protocol username is : " + protocol.getUsername());
      this.server.getClientsMap().put(protocol.getUsername(), this);
      System.out.println("The clientmap size is : " + this.server.getClientsMap().size());
    }
    System.out.println("server input null");
  }


  public void run2() {
    do {
      try {
        //int identifier = dis.readInt();
        int identifier = 1;
        protocol.processInput(identifier, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } while (protocol.getUsername() == null);

    this.server.getClientsMap().put(protocol.getUsername(), this);
    System.out.println("Welcome client " + protocol.getUsername() + " joining the chat room. <<");

    while (!s.isClosed()) {
      try {
        //int identifier = dis.readInt();
        int identifier = 1;
        protocol.processInput(identifier, null);
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