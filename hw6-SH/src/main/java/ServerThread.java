import java.io.*;
import java.net.Socket;

/**
 * ServerThread is a thread handler for the server
 */
public class ServerThread extends Thread {

  private Socket s;
  private Server server;
  private BufferedReader is;
  private OutputStream os;
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
    this.is = new BufferedReader(new InputStreamReader(s.getInputStream()));
    this.os = s.getOutputStream();
    this.protocol = new Protocol(server.getClientsMap(), is, os);
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
    System.out.println("handle starts...");
    String input = "";
    while ((input = is.readLine()) != null){
      System.out.println(input);
      String[] tokens = input.split(" ");
      int identifier = Integer.parseInt(tokens[0]);
      PrintWriter out = new PrintWriter(os, true);
      out.println("Server about to process your request, plz wait...");
      os.write("Server about to process your request, plz wait...".getBytes());
      protocol.processInput(identifier, tokens);
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