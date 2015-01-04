package ph.alephzero.finance.products.loan;

import static ph.alephzero.finance.util.DateUtil.createDate;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.CashFlows;

import org.junit.Assert;
import org.testng.annotations.Test;

public class LoanCashFlowGeneratorTest {
  @Test
  public void testAnnuityWithPeriod() {
      // scenario: prin = 55000, int = 15% per annum, period = 2 years
      CashFlows cf = LoanCashFlowGenerator.annuity(55000.0, 0.15/12, 2*12);
      
      Assert.assertFalse(cf.isDated());      
      assertEquals(cf.getCount(), 2*12 + 1);
      
      double [] prin = new double[] { -55000.0, 
          1979.265643, 2004.006463, 2029.056544, 2054.419751, 2080.099998, 2106.101248, 2132.427513, 2159.082857,
          2186.071393, 2213.397285, 2241.064751, 2269.078061, 2297.441536, 2326.159556, 2355.236550, 2384.677007,
          2414.485470, 2444.666538, 2475.224870, 2506.165180, 2537.492245, 2569.210898, 2601.326035, 2633.842610
      };
      
      double [] intr = new double[] { 0.0,
          687.500000, 662.759179, 637.709099, 612.345892, 586.665645, 560.664395, 534.338129, 507.682786,
          480.694250, 453.368357, 425.700891, 397.687582, 369.324106, 340.606087, 311.529093, 282.088636,
          252.280173, 222.099105, 191.540773, 160.600462, 129.273397, 97.554744, 65.439608, 32.923033
      };
                  
      assertEquals(cf.getCashFlow(0, "PRINCIPAL"), -55000.0, 0.000001);
      assertEquals(cf.getCashFlow(0, "INTEREST"), 0.0, 0.000001);
      assertEquals(cf.getCashFlow(0), -55000.0, 0.000001);
      
      for (int i = 1; i <= 24; i++) {
          assertEquals(cf.getCashFlow(i, "PRINCIPAL"), prin[i], 0.000001);
          assertEquals(cf.getCashFlow(i, "INTEREST"), intr[i], 0.000001);
          assertEquals(cf.getCashFlow(i), 2666.765643, 0.000001);
      }
  }
  
  @Test
  public void testAnnuityVariableTerm() {
      // scenario: prin = 55000, int = 15% per annum, annuity = 3000 per month
      CashFlows cf = LoanCashFlowGenerator.annuityVariableTerm(55000.0, 0.15/12, 3000.0);
      
      assertEquals(cf.getCount(), 22);  // time 0 cf + 21 annuities
      Assert.assertFalse(cf.isDated());
      
      double [] prin = new double[] { -55000.0, 
          2312.500000, 2341.406250, 2370.673828, 2400.307251, 2430.311092, 2460.689980, 2491.448605,
          2522.591713, 2554.124109, 2586.050660, 2618.376294, 2651.105997, 2684.244822, 2717.797883,
          2751.770356, 2786.167485, 2820.994579, 2856.257011, 2891.960224, 2928.109727, 2823.112134
      };
      
      double [] intr = new double[] { 0.0,
          687.500000, 658.593750, 629.326172, 599.692749, 569.688908, 539.310020, 508.551395, 
          477.408287, 445.875891, 413.949340, 381.623706, 348.894003, 315.755178, 282.202117,
          248.229644, 213.832515, 179.005421, 143.742989, 108.039776, 71.890273, 35.288902
      };
      
      assertEquals(cf.getCashFlow(0, "PRINCIPAL"), -55000.0, 0.000001);
      assertEquals(cf.getCashFlow(0, "INTEREST"), 0.0, 0.000001);
      assertEquals(cf.getCashFlow(0), -55000.0, 0.000001);
      
      for (int i = 1; i <= 21; i++) {
          assertEquals(cf.getCashFlow(i, "PRINCIPAL"), prin[i], 0.000001);
          assertEquals(cf.getCashFlow(i, "INTEREST"), intr[i], 0.000001);
          assertEquals(cf.getCashFlow(i), (i < 21) ? 3000.0 : 2858.401036, 0.000001);   // last payment is only 2858.401036
      }
  }
  
  @Test
  public void testSimpleAddOn() {
      // scenario: prin = 100, int = 2% per annum, days = 7
      Date settlement = createDate(2013,12,21);
      Date maturity = createDate(2013,12,28);
      
      CashFlows cf = LoanCashFlowGenerator.simpleAddOn(settlement, maturity, 100.0, 0.02, DayCountBasis.ACT_360);
      
      assertEquals(cf.getDates().size(), 2);
      assertEquals(cf.getDates().get(0), settlement);
      assertEquals(cf.getDates().get(1), maturity);
      
      assertEquals(cf.getCashFlow(settlement, "PRINCIPAL"), -100, 0.00000001);
      assertEquals(cf.getCashFlow(settlement, "INTEREST"), 0.0, 0.00000001);
      
      assertEquals(cf.getCashFlow(maturity, "PRINCIPAL"), 100.0, 0.00000001);
      assertEquals(cf.getCashFlow(maturity, "INTEREST"), 0.03888889, 0.00000001);
  }
}
