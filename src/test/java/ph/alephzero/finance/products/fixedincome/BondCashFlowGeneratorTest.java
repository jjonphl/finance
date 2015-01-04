package ph.alephzero.finance.products.fixedincome;

import static ph.alephzero.finance.util.DateUtil.createDate;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.CashFlows;

import org.testng.annotations.Test;

public class BondCashFlowGeneratorTest {
    private static Date[] SIFMAExCF3Sched = new Date[] {
        createDate(1994, 1, 1), createDate(1994, 7, 1),
        createDate(1995, 1, 1), createDate(1995, 7, 1),
        createDate(1996, 1, 1), createDate(1996, 7, 1),
        createDate(1997, 1, 1), createDate(1997, 7, 1),
        createDate(1998, 1, 1), createDate(1998, 7, 1),
        createDate(1999, 1, 1), createDate(1999, 7, 1),
        createDate(2000, 1, 1), createDate(2000, 7, 1),
        createDate(2001, 1, 1), createDate(2001, 7, 1),
        createDate(2002, 1, 1), createDate(2002, 7, 1),
        createDate(2003, 1, 1)
    };

    private static Date[] SIFMAExOddSched = new Date[] {
        createDate(1993,3,1), createDate(1993,9,1),
        createDate(1994,3,1), createDate(1994,9,1),
        createDate(1995,3,1), createDate(1995,9,1),
        createDate(1996,3,1), createDate(1996,9,1),
        createDate(1997,3,1), createDate(1997,9,1),
        createDate(1998,3,1), createDate(1998,9,1),
        createDate(1999,3,1), createDate(1999,9,1),
        createDate(2000,3,1), createDate(2000,9,1),
        createDate(2001,3,1), createDate(2001,9,1),
        createDate(2002,3,1), createDate(2002,9,1),
        createDate(2003,3,1), createDate(2003,9,1),
        createDate(2004,3,1), createDate(2004,9,1),
        createDate(2005,3,1)
    };
    
    /**
     * SIFMA TIPS v2 Example CF-3 (p37)
     */
    @Test
    public void testSchedRegularPeriodicInterest() {

        Date[] sched = BondCashFlowGenerator.scheduleRPIBond(createDate(1993, 10, 1), createDate(2003, 1, 1), 2, DayCountBasis.ACT_ACT);

        assertEquals(sched.length, 19);
        for (int i = 0; i < SIFMAExCF3Sched.length; i++) {
            assertEquals(sched[i], SIFMAExCF3Sched[i]);
        }        
    }

    /**
     * SIFMA TIPS v2 Example CF-3 (p37)
     */
    @Test
    public void testRegularPeriodicInterest() {
        Date settlement = createDate(1993, 10, 1);
        Date maturity = createDate(2003, 1, 1);
        CashFlows cf = BondCashFlowGenerator.cashFlowsRPIBond(settlement, maturity, 100.0, 0.045, 2, DayCountBasis.ACT_ACT);

        assertEquals(cf.getCount(), 20); // base date + 19 cash flows
        
        // test interest cash flows
        assertEquals(cf.getCashFlow(settlement, "INTEREST"), 0.0);
        for (int i = 0; i < SIFMAExCF3Sched.length; i++) {
            assertEquals(cf.getCashFlow(SIFMAExCF3Sched[i], "INTEREST"), 2.250000, 0.000001);
        }
        
        // test principal cash flows
        assertEquals(cf.getCashFlow(settlement, "PRINCIPAL"), 0.0);
        for (int i = 0; i < SIFMAExCF3Sched.length - 1; i++) {
            assertEquals(cf.getCashFlow(SIFMAExCF3Sched[i], "PRINCIPAL"), 0.0);
        }
        assertEquals(cf.getCashFlow(maturity, "PRINCIPAL"), 100.0);
        
        // test total cash flows
        assertEquals(cf.getCashFlow(settlement), 0.0);
        for (int i = 0; i < SIFMAExCF3Sched.length - 1; i++) {
            assertEquals(cf.getCashFlow(SIFMAExCF3Sched[i]), 2.250000, 0.000001);
        }
        assertEquals(cf.getCashFlow(maturity), 102.250000, 0.000001);
    }
    
    
    @Test
    public void testScheduleOddBondTIPS() {
        Date settlement;
        Date maturity;
        Date firstCoupon;
        Date lastCoupon;
        Date[] sched;
        
        // SIFMA TIPS v1, p75 Odd Long First
        settlement = createDate(1992,11,11);
        maturity = createDate(2005,3,1);
        firstCoupon = createDate(1993,3,1);
        lastCoupon = createDate(2004,9,1);
        sched = BondCashFlowGenerator.scheduleOddBond(settlement, maturity, firstCoupon, lastCoupon, 2, DayCountBasis.ACT_ACT);
        
        assertEquals(sched.length, SIFMAExOddSched.length);
        for (int i = 0; i < SIFMAExOddSched.length; i++) {
            assertEquals(sched[i], SIFMAExOddSched[i]);
        }        
    }
    
    @Test
    public void testCashFlowsOddBondTIPS() {
        Date settlement;
        Date issue;
        Date maturity;
        Date firstCoupon;
        Date lastCoupon;
        CashFlows cf;
        List<Date> dates;

        // SIFMA TIPS v1, p75 Odd Long First
        settlement = createDate(1992,11,11);
        issue = createDate(1992,6,15);
        maturity = createDate(2005,3,1);
        firstCoupon = createDate(1993,3,1);
        lastCoupon = createDate(2004,9,1);
        cf = BondCashFlowGenerator.cashFlowsOddBond(settlement, issue, maturity, firstCoupon, lastCoupon, 100.0, 0.0935, 2, DayCountBasis.ACT_ACT);
        dates = cf.getDates(); 
        
        assertEquals(cf.getCount(), SIFMAExOddSched.length + 1); // including 0 cash flow on reference date (settlement date)
        assertEquals(cf.getBaseDate(), settlement);
        assertEquals(dates.get(1), firstCoupon);
        // quasi periods = 1+78/184 = 1.423913, 1st coup = 100 * 1.423913 * 0.0935 / 2 = 6.656793
        assertEquals(cf.getCashFlow(firstCoupon), 6.656793, 0.000001);  
        
        for (int i = 1; i < SIFMAExOddSched.length - 1; i++) {            
            assertEquals(cf.getCashFlow(SIFMAExOddSched[i]), 4.675, 0.000001);
            assertEquals(cf.getCashFlow(SIFMAExOddSched[i], "INTEREST"), 4.675, 0.000001);
            assertEquals(cf.getCashFlow(SIFMAExOddSched[i], "PRINCIPAL"), 0.0, 0.000001);
        }
        
        assertEquals(dates.get(SIFMAExOddSched.length), maturity);
        assertEquals(cf.getCashFlow(maturity), 100 + 4.675, 0.000001);
        assertEquals(cf.getCashFlow(maturity, "INTEREST"), 4.675, 0.000001);
        assertEquals(cf.getCashFlow(maturity, "PRINCIPAL"), 100.0, 0.000001);
    }
    
    @Test
    public void testCashFlowsOddBondMisc() {        
        CashFlows cf;
        Date settlement;
        Date issue;
        Date maturity;
        Date firstCoupon;
        Date lastCoupon;
//        List<Date> dates;
        
        // test long first period, first coup = maturity
        settlement = createDate(1992,11,11);
        issue = createDate(1992,6,15);
        maturity = createDate(1993,3,1);
        firstCoupon = maturity;
        lastCoupon = null;
        cf = BondCashFlowGenerator.cashFlowsOddBond(settlement, issue, maturity, firstCoupon, lastCoupon, 100.0, 0.0935, 2, DayCountBasis.ACT_ACT);
        
        assertTrue(cf.isDated());
        assertEquals(cf.getCount(), 2);   // ref date + maturity
        assertEquals(cf.getDates().get(0), settlement);
        assertEquals(cf.getDates().get(1), firstCoupon);
        assertEquals(cf.getCashFlow(firstCoupon, "INTEREST"), 6.65679347826087, 0.000001);
        assertEquals(cf.getCashFlow(firstCoupon, "PRINCIPAL"), 100.0, 0.000001);
    }
    
    @Test
    public void testAmortEffectiveInterestRateRPIBond() {
        CashFlows cf;
        Date settlement;        
        Date maturity;
        List<Date> dates;
        double coupamt; 
        double premsum;
        // test exact bond: settlement on issue date/coupon date
        settlement = createDate(2008,3,3);
        maturity = createDate(2013,3,3);
        cf = BondCashFlowGenerator.amortEffectiveInterestRateRPIBond(maturity, settlement, 100.0, 0.0875, 100.589894, 2, DayCountBasis.EUR_30_360);
        dates = cf.getDates();
        
        coupamt = 100 * 0.0875/2;
        premsum = 0.0;
        assertTrue(cf.isDated());
        assertEquals(cf.getBaseDate(), settlement);        
        for (int i = 1; i < dates.size(); i++) {
            Date date = dates.get(i);
            premsum += cf.getCashFlow(date, "PREMIUM");
            assertEquals(cf.getCashFlow(date, "DISCOUNT"), 0.0);
            assertEquals(cf.getCashFlow(date, "COUPON"), coupamt, 0.000001);
            assertEquals(cf.getCashFlow(date, "PRINCIPAL"), (i == dates.size() - 1) ? 100.0 : 0.0, 0.000001);
        }
        
        assertEquals(cf.getCashFlow(settlement, "PREMIUM") + premsum, 0.0, 0.000001);
    }
    
    @Test
    public void testAmortStraightLineRPIBond() {
        CashFlows cf;
        Date settlement;        
        Date maturity;
        List<Date> dates;
        double coupamt; 
        double premsum;
        // test exact bond: settlement on issue date/coupon date
        settlement = createDate(2008,3,3);
        maturity = createDate(2013,3,3);
        cf = BondCashFlowGenerator.amortStraightLineRPIBond(maturity, settlement, 100.0, 0.0875, 100.589894, 2, DayCountBasis.EUR_30_360);
        dates = cf.getDates();
        
        coupamt = 100 * 0.0875/2;
        premsum = 0.0;
        assertTrue(cf.isDated());
        assertEquals(cf.getBaseDate(), settlement);        
        for (int i = 1; i < dates.size(); i++) {
            Date date = dates.get(i);
            premsum += cf.getCashFlow(date, "PREMIUM");
            assertEquals(cf.getCashFlow(date, "DISCOUNT"), 0.0);
            assertEquals(cf.getCashFlow(date, "COUPON"), coupamt, 0.000001);
            assertEquals(cf.getCashFlow(date, "PRINCIPAL"), (i == dates.size() - 1) ? 100.0 : 0.0, 0.000001);
        }
        
        assertEquals(cf.getCashFlow(settlement, "PREMIUM") + premsum, 0.0, 0.000001);
    }
}
