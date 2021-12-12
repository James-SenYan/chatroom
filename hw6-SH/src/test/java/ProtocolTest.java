import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProtocolTest {

  private Protocol mockProtocol;
  private DataOutputStream dos;
  private DataInputStream dis;
  private ConcurrentHashMap<String, ServerThread> clientMap;
  private ByteArrayOutputStream byteArrayOutputStream;
  private ByteArrayInputStream byteArrayInputStream;


  @BeforeEach
  void setUp() {
    clientMap = new ConcurrentHashMap<>();
    String input = "3 aha";
    byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
    byteArrayOutputStream = new ByteArrayOutputStream();
    dis = new DataInputStream(byteArrayInputStream);
    dos = new DataOutputStream(byteArrayOutputStream);
    mockProtocol = new Protocol(clientMap, dis, dos);
  }

  @Test
  void processInput() throws IOException {
    //mockProtocol.processInput(Identifiers.CONNECT_MESSAGE);
    String out = "Hello, aha" + ". There are 0" + " other connected clients.";
    //assertEquals("20 true " + out.length() + " " + out, byteArrayOutputStream.toString());
  }
}