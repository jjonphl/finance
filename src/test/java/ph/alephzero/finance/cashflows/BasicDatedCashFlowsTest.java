package ph.alephzero.finance.cashflows;

import static ph.alephzero.finance.util.DateUtil.createDate;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.util.Date;
import java.util.List;

import org.testng.annotations.Test;

public class BasicDatedCashFlowsTest {
  @Test
  public void testLiteralConstructor() {
      CashFlows cf = new BasicDatedCashFlows(
              createDate(2012,1,1), 100.0,
              createDate(2012,1,2), 200.0,
              createDate(2012,1,3), 300.0,
              createDate(2012,1,5), 400.0,
              createDate(2012,1,8), 500.0,
              createDate(2012,1,13), 600.0,
              createDate(2012,1,21), 700.0,
              createDate(2012,2,3), 800.0,
              createDate(2012,2,24), 900.0,
              createDate(2012,3,28), 1000.0);
      
      // test basic properties
      assertEquals(cf.getBaseDate(), createDate(2012,1,1));
      assertEquals(cf.getCount(), 10);      
      assertTrue(cf.isDated());
      
      // test stored dates, also their ordering
      List<Date> dates = cf.getDates();
      assertEquals(dates.size(), 10);
      assertEquals(dates.toArray(), 
              new Object[] { createDate(2012,1,1), createDate(2012,1,2),
                             createDate(2012,1,3), createDate(2012,1,5),
                             createDate(2012,1,8), createDate(2012,1,13),
                             createDate(2012,1,21), createDate(2012,2,3),
                             createDate(2012,2,24), createDate(2012,3,28) });
      
      // test cash flows returned
      for (int i = 0; i < cf.getCount(); i++) {
          assertEquals(cf.getCashFlow(i), 100.0 * (i+1));
          assertEquals(cf.getCashFlow(dates.get(i)), 100.0 * (i+1));
      }

  }
}
