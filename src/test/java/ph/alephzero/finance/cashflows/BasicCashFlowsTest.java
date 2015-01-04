package ph.alephzero.finance.cashflows;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

public class BasicCashFlowsTest {
  @Test
  public void testLiteralConstructor() {
      CashFlows cf = new BasicCashFlows(100.0, 200.0, 300.0, 400.0, 500.0, 600.0, 700.0, 800.0, 900.0, 1000.0);
      
      assertEquals(cf.getBaseDate(), null);
      assertEquals(cf.getCount(), 10);
      assertTrue(!cf.isDated());
      
   // test cash flows returned
      for (int i = 0; i < cf.getCount(); i++) {
          assertEquals(cf.getCashFlow(i), 100.0 * (i+1));          
      }      
  }
}
