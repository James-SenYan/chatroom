import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringByteArrayTransferTest {

  private DataInputStream is;

  @BeforeEach
  void setUp() {
    String text = "this is a test.";
    InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    is = new DataInputStream(stream);
  }

  @Test
  void byteArrayToString(){
    String s = null;
    try {
      s = StringByteArrayTransfer.byteArrayToString(is, 15);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals("this is a test.", s);
  }
}