package ph.alephzero.finance.products.fixedincome;

import static org.testng.Assert.assertEquals;
import ph.alephzero.finance.DayCountBasis;
import static ph.alephzero.finance.util.DateUtil.createDate;

import org.testng.annotations.Test;

public class BondUtilTest {
    @Test
    public void testCouponPeriodDays() {
        assertEquals(BondUtil.couponPeriodDays(createDate(1996, 4, 1), createDate(1996, 10, 1), 2, DayCountBasis.NASD_30_360), 180);
        assertEquals(BondUtil.couponPeriodDays(createDate(1996, 4, 1), createDate(1996, 10, 1), 2, DayCountBasis.ACT_ACT), 183);
                
        assertEquals(BondUtil.couponPeriodDays(createDate(2008,3,3), createDate(2008,9,3), 2, DayCountBasis.EUR_30_360), 180);
    }
        
    @Test
    public void testPreviousCouponDate2PDEXSAS() {
        // couppcd(2000-08-25, 2025-03-15, 2) = 2000-03-15
        assertEquals(BondUtil.previousCouponDate2(createDate(2000, 8, 25), createDate(2025, 3, 15), 2, DayCountBasis.EUR_30_360), createDate(2000, 3, 15));
        
        // couppcd(2008-06-05, 2013-03-03, 2) = 2008-03-03
        assertEquals(BondUtil.previousCouponDate2(createDate(2008, 6, 5), createDate(2013, 3, 3), 2, DayCountBasis.EUR_30_360), createDate(2008, 3, 3));
        
        // couppcd(2008-06-05, 2013-03-03, 4) = 2008-06-03
        assertEquals(BondUtil.previousCouponDate2(createDate(2008, 6, 5), createDate(2013, 3, 3), 4, DayCountBasis.EUR_30_360), createDate(2008, 6, 3));
    }
    
    @Test
    public void testCouponCountPDEXSAS() {
        // including maturity:
        //  2009-02-07, 2008-08-07, 2008-02-07, 2007-08-07, 2007-02-07
        assertEquals(BondUtil.couponCount(createDate(2007, 2, 1), createDate(2009, 2, 17), 2, DayCountBasis.EUR_30_360), 5);

    }
    
    @Test
    public void testPreviousCoponDate() {
        // from SIFMA AccrInt Formula B Example
        assertEquals(BondUtil.previousCouponDate(createDate(1996,7,1), createDate(1996,8,18), 2, DayCountBasis.NASD_30_360, false), createDate(1996,7,1));
    }
    
    @Test
    public void testPreviousCouponDate2() {
        // from SIFMA AccrInt Formula B Example
        assertEquals(BondUtil.previousCouponDate2(createDate(1996,8,18), createDate(1996,10,1), 2, DayCountBasis.NASD_30_360), createDate(1996,4,1));
        
        // if settlement date is a coupon date, then return the settlement date
        assertEquals(BondUtil.previousCouponDate2(createDate(1996,4,1), createDate(1996,10,1), 2, DayCountBasis.NASD_30_360), createDate(1996,4,1));

        // test from Che
        assertEquals(BondUtil.previousCouponDate2(createDate(2014,1,19), createDate(2026,1,19), 2, DayCountBasis.EUR_30_360), createDate(2014,1,19));
    }
    
    @Test
    public void testCouponCountFraction() {
        // SIFMA vol1 Benchmark #2A
        assertEquals(BondUtil.couponCountFraction(createDate(1993,3,1), createDate(2007,1,1), 2, DayCountBasis.ACT_ACT), 27 + 122.0 / (122.0 + 59), 0.000001);
        
        // SIFMA vol1 Benchmark #2B
        assertEquals(BondUtil.couponCountFraction(createDate(1993,3,1), createDate(2007,1,1), 2, DayCountBasis.NASD_30_360), 27 + 120.0 / 180.0, 0.000001);
        
        // settlement date is last coupon date before maturity
        assertEquals(BondUtil.couponCountFraction(createDate(2008,3,3), createDate(2008,9,3), 2, DayCountBasis.EUR_30_360), 1.0, 0.00000001);        
    }
    
    @Test
    public void testQuasiPeriods() {
        // exact coupon periods
        assertEquals(BondUtil.quasiPeriods(createDate(2004,9,1), createDate(2005,3,1), 2, DayCountBasis.ACT_ACT, true), 1.0, 0.000001);
        assertEquals(BondUtil.quasiPeriods(createDate(2004,9,1), createDate(2005,3,1), 2, DayCountBasis.ACT_ACT, false), 1.0, 0.000001);
        
        // SIFMA v1 p75 Odd Long First, count backwards
        // [1992-09-01,1993-03-01] = 1 qp
        // [1992-06-15,1992-09-01] = 78 days
        // [1992-03-01,1992-09-01] = 184 days
        // total qp = 1 + 78 / 184 = 1.423913
        assertEquals(BondUtil.quasiPeriods(createDate(1992,6,15), createDate(1993,3,1), 2, DayCountBasis.ACT_ACT, false), 1.423913, 0.000001);
    }
    
    @Test
    public void testNextCouponDateBackward() {
        // normal
        assertEquals(BondUtil.nextCouponDateBackward(createDate(2008,2,15), createDate(2013,3,3), 2, DayCountBasis.EUR_30_360), createDate(2008,3,3));
        
        // settlement date is a coupon date, return next coupon date
        assertEquals(BondUtil.nextCouponDateBackward(createDate(2008,3,3), createDate(2013,3,3), 2, DayCountBasis.EUR_30_360), createDate(2008,9,3));
        
        // February madness!
        // February end-of-month adjustment: note 8/31 instead of naive 8/29!
        assertEquals(BondUtil.nextCouponDateBackward(createDate(2012,2,29), createDate(2027,2,28), 2, DayCountBasis.EUR_30_360), createDate(2012,8,31));
        assertEquals(BondUtil.nextCouponDateBackward(createDate(2002,3,21), createDate(2007,2,28), 2, DayCountBasis.EUR_30_360), createDate(2002,8,31));
        assertEquals(BondUtil.nextCouponDateBackward(createDate(2013,2,24), createDate(2017,2,28), 2, DayCountBasis.EUR_30_360), createDate(2013,2,28));
        
    }
    
    @Test
    public void testBondEIRResidual() {
        BondUtil.BondEIRResidual resid;
        
        resid = new BondUtil.BondEIRResidual(createDate(2012,11,23), createDate(2031,7,19), 0.08, 1.223715077, 2, DayCountBasis.EUR_30_360);        
        assertEquals(resid.value(0.0599213884150036), 0.0, 0.00000001);
                
        resid = new BondUtil.BondEIRResidual(createDate(2008,3,3), createDate(2013,3,3), 0.0875, 1.00589894, 2, DayCountBasis.EUR_30_360);
        assertEquals(resid.value(0.086023538), 0.0, 0.00000001);
        
    }
}
