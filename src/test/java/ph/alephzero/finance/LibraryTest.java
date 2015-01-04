package ph.alephzero.finance;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LibraryTest {
  @Test
  public void testVersion() {
      Assert.assertNotEquals(Library.VERSION, "UNKNOWN");
      System.out.println("** LIBRARY VERSION = " + Library.VERSION);
  }
}
