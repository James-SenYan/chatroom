import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
@TestInstance(Lifecycle.PER_CLASS)
class ClientTest {

  private Client mockClient;
  private Server server;
  private DataInputStream dis;
  private DataOutputStream dos;
  private ServerThread serverThread;

  @BeforeAll
  void setUp(){
    try {
      startServer();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  void test1() throws IOException, InterruptedException {
    String text = """
    localhost
    8000
    ?
    who
    login sen
    who
    @all hello
    @user sen hello
    @user haoyu hello
    logoff
    who
    login haoyu
    !user haoyu
    @user haoyu hello
    @all hello
    """;

    InputStream stream = new ByteArrayInputStream(text.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    dos = new DataOutputStream(out);
    System.setIn(stream);
    System.setOut(ps);
    dis = new DataInputStream(stream);
    //startServer();
    Client.main(new String[]{});
    //mockClient = new Client("localhost", 8000);
    //startClient();
    String standardOut = """
    Enter server name and port number:
    connect successful
    New client request received : /127.0.0.1
    Preparing the chat room for this client...
    Enter cmd:
    You can enter ? to see instruction of using chat room
    logoff: sends a DISCONNECT_MESSAGE to the server
    who: sends a QUERY_CONNECTED_USERS to the server
    @user: sends a DIRECT_MESSAGE to the specified user to the server
    @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected
    !user: sends a SEND_INSULT message to the server, to be sent to the specified user
    Enter cmd:
    You can enter ? to see instruction of using chat room
    Enter server name and port number:
    connect successful
    New client request received : /127.0.0.1
    Preparing the chat room for this client...
    Enter cmd:
    You can enter ? to see instruction of using chat room
    logoff: sends a DISCONNECT_MESSAGE to the server
    who: sends a QUERY_CONNECTED_USERS to the server
    @user: sends a DIRECT_MESSAGE to the specified user to the server
    @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected
    !user: sends a SEND_INSULT message to the server, to be sent to the specified user
    Enter cmd:
    You can enter ? to see instruction of using chat room
    
    """;
    assertNotEquals(standardOut, out.toString());
  }

  private void startServer() throws InterruptedException {
    Thread thread = new Thread(){
      @Override
      public void run(){
        try {
          server = new Server(8000);
          server.run();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    thread.start();
    thread.join(5000);
  }


  private void startClient(){
    Thread thread = new Thread(){
      @Override
      public void run(){
        String text = """
    localhost
    8000
    login haoyu
    """;
        InputStream stream = new ByteArrayInputStream(text.getBytes());
        System.setIn(stream);
        Client.main(new String[]{});
      }
    };
    thread.start();
  }




  void test2() throws IOException, InterruptedException {
    String text = """
    localhost
    8000
    ?
    who
    login haoyu
    !user sen
    """;
    InputStream stream = new ByteArrayInputStream(text.getBytes());
    System.setIn(stream);
    //startServer();
    Client.main(new String[]{});
  }
}