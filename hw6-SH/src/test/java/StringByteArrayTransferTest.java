import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringByteArrayTransferTest {

  private DataInputStream is;

  @BeforeEach
  void setUp() throws IOException {
    InputStream inputStream = IOUtils.toInputStream("username", "UTF-8");
    byte[] bytes1 = IOUtils.toByteArray(inputStream);
    String text = "username";
    byte[] bytes2 = text.getBytes();
    is = new DataInputStream(inputStream);
  }

  @Test
  void byteArrayToString() {
    String s = null;
    try {
      s = StringByteArrayTransfer.byteArrayToString(is, 2);
    } catch (IOException e) {
      e.printStackTrace();
    }
    //Assertions.assertEquals("username", s);
  }
}