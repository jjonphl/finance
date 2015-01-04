package ph.alephzero.finance.products.fixedincome;

import static ph.alephzero.finance.util.DateUtil.createDate;
import static org.testng.Assert.assertEquals;

import java.util.Date;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.util.DateUtil;

import org.testng.annotations.Test;

public class BondValuationTest {
      
    @Test
    public void testAccruedInterest() {
        double par = 1000.0;
        assertEquals(par * BondValuation.accruedInterest(createDate(1996,4,1), createDate(1996,8,18), 0.05, 2, DayCountBasis.NASD_30_360), 19.027778, 0.000001);
    }
    
    @Test
    public void testAccruedOddFirstShort() {
        assertEquals(1000.0 * BondValuation.accruedInterestOddFirst(createDate(1996,7,1), createDate(1996,8,18), createDate(1996,10,1), 0.05, 2, DayCountBasis.NASD_30_360), 6.53, 0.01);
    }
    
    @Test
    public void testAccruedOddFirstLong() {
        assertEquals(10000.0 * BondValuation.accruedInterestOddFirst(createDate(1992,7,1), createDate(1993,2,1), createDate(1993,4,1), 0.075, 2, DayCountBasis.ACT_ACT), 441.96, 0.01);
        
    }
    
    @Test
    public void testPriceExcelExample() {
        // test clean price
        assertEquals(100 * BondValuation.price(createDate(2008,2,15), createDate(2017,11,15), 0.0575, 0.0650, 2, DayCountBasis.NASD_30_360, true), 94.634362, 0.000001);
        // test dirty price
        assertEquals(100 * BondValuation.price(createDate(2008,2,15), createDate(2017,11,15), 0.0575, 0.0650, 2, DayCountBasis.NASD_30_360, false), 94.634362 + 1.437500, 0.000001);
    }
    
    @Test
    public void testPriceUCPBExample() {
        // test clean price
        assertEquals(20000000 * BondValuation.price(createDate(2012,6,8), createDate(2016,2,25), 0.0625, 0.06, 4, DayCountBasis.NASD_30_360, true), 20165068.44, 0.01);
    }
    /**
     * Test clean prices, 30E/360
     */
    @Test
    public void testPricePDEXSAS() {
        // [reissue=2008-06-05, maturity=2013-03-03, conv/yr=2, couponrate=8.75%, 
        //  ytmrate=8.59%] = 100.59
        assertEquals(BondValuation.price(createDate(2008, 6, 5), createDate(2013, 3, 3), 0.0875, 0.0859, 2, DayCountBasis.EUR_30_360), 1.00590, 0.00001);
        
        // [reissue=2008-06-05, maturity=2013-03-03, conv/yr=2, couponrate=8.75%, 
        //  ytmrate=8.375%] = 101.421
        assertEquals(BondValuation.price(createDate(2008, 6, 5), createDate(2013, 3, 3), 0.0875, 0.08375, 2, DayCountBasis.EUR_30_360), 1.01421, 0.00001);
        
        // [reissue=2008-06-05, maturity=2013-03-03, conv/yr=2, couponrate=8.75%, 
        //  ytmrate=8.495%] = 100.956
        assertEquals(BondValuation.price(createDate(2008, 6, 5), createDate(2013, 3, 3), 0.0875, 0.08495, 2, DayCountBasis.EUR_30_360), 1.00956, 0.00001);
        
        // [reissue=2008-03-27, maturity=2017-08-23, conv/yr=2, couponrate=7.75%, 
        //  ytmrate=7.227%] = 103.515
        assertEquals(BondValuation.price(createDate(2008, 3, 27), createDate(2017, 8, 23), 0.0775, 0.07227, 2, DayCountBasis.EUR_30_360), 1.03515, 0.00001);
        
        // [reissue=2008-03-27, maturity=2017-08-23, conv/yr=2, couponrate=7.75%, 
        //  ytmrate=6.95%] = 105.447
        assertEquals(BondValuation.price(createDate(2008, 3, 27), createDate(2017, 8, 23), 0.0775, 0.0695, 2, DayCountBasis.EUR_30_360), 1.05447, 0.00001);
        
        // [reissue=2008-03-27, maturity=2017-08-23, conv/yr=2, couponrate=7.75%, 
        //  ytmrate=6.95%] = 105.447
        assertEquals(BondValuation.price(createDate(2008, 3, 27), createDate(2017, 8, 23), 0.0775, 0.0695, 2, DayCountBasis.EUR_30_360), 1.05447, 0.00001);
        
        // sample PIBD0507B435
        // [reissue=2002-03-21, maturity=2007-02-28, conv/yr=2, couponrate=13.%,
        //  ytmrate=12.5] = 101.780
        assertEquals(BondValuation.price(createDate(2002, 3, 21), createDate(2007, 2, 28), 0.13, 0.125, 2, DayCountBasis.EUR_30_360), 1.01780, 0.00001);
        
        // sample PIBD0507B435
        // [reissue=2002-03-21, maturity=2007-02-28, conv/yr=2, couponrate=13.%,
        //  ytmrate=12.79] = 100.729
        assertEquals(BondValuation.price(createDate(2002, 3, 21), createDate(2007, 2, 28), 0.13, 0.1279, 2, DayCountBasis.EUR_30_360), 1.00729, 0.00001);
        
        // bug at Joey17
        // [original=2009-03-27, maturity=2016-03-27, conv/yr=2, couponrate=0.0%,
        //  ytmrate=9.000%] = 53.997
        assertEquals(BondValuation.price(createDate(2009, 3, 27), createDate(2016, 3, 27), 0.0, 0.09, 2, DayCountBasis.EUR_30_360), 0.53997, 0.00001);
        
        // bug at 20090729-Mike12 (BTr UAT)
        // [reissue=2009-05-28, maturity=2028-12-14, conf/yr=2, couponrate=9.5%
        //  ytmrate=8.814%] = 106.334
        assertEquals(BondValuation.price(createDate(2009, 5, 28), createDate(2028, 12, 14), 0.095, 0.0881377, 2, DayCountBasis.EUR_30_360), 1.06334, 0.00001);
        
        // 1 coupon left before maturity
        assertEquals(BondValuation.price(createDate(2007, 10, 1), createDate(2008, 1, 12), 0.0925, 0.08925, 2, DayCountBasis.EUR_30_360), 1.00039, 0.00001);

    }
    
    
    @Test
    public void testPriceMisc() {
        // based on SIFMA TIPS v1 p75 Odd Long First
        assertEquals(BondValuation.price(createDate(1993,3,1), createDate(2005,3,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.123551829253633, 0.000001) ;
        assertEquals(BondValuation.price(createDate(1993,3,1), createDate(2005,3,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, false), 1.123551829253633, 0.000001) ;        
        
        //TODO: what is the problem here?
        assertEquals(BondValuation.price(createDate(2008,1,5), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.ACT_ACT), 0.94386431058456395, 0.00000001);
    }
    
    @Test
    public void testPriceSIFMAZero() {
        // based on SIFMA TIPS v1 p109 Yield from Price, <= 1 quasi-coupon period
        assertEquals(BondValuation.price(createDate(1992,6,23), createDate(1992,10,1), 0.0, 0.04179647, 2, DayCountBasis.NASD_30_360), 0.98875, 0.00001);
        
        // based on SIFMA TIPS v1 p111 Price from Yield, <= 1 quasi-coupon period
        assertEquals(BondValuation.price(createDate(1992,8,15), createDate(1992,10,1), 0.0, 0.0625, 2, DayCountBasis.NASD_30_360), 0.99207716, 0.00000001);
        
        // based on SIFMA TIPS v1 p113 Yield from Price, > 1 quasi-coupon period
        assertEquals(BondValuation.price(createDate(1992,6,26), createDate(2005,7,1), 0.0, 0.10900794, 2, DayCountBasis.NASD_30_360), 0.25125, 0.00001);
        
        // based on SIFMA TIPS v1 p115 Price form Yield, > 1 quasi-coupon period
        assertEquals(BondValuation.price(createDate(1992,2,12), createDate(2005,7,1), 0.0, 0.1055, 2, DayCountBasis.NASD_30_360), 0.25252446, 0.00000001);
    }
    
    
    @Test
    public void testPriceFebruaryMadness() {
        // from Arlene/PDEX, next coup date is 2012-08-31, not 2012-08-29! 
        // fix depends on BondUtil.nextCouponDateBackwardSmartAdjust()
        assertEquals(BondValuation.price(createDate(2012,2,29), createDate(2027,2,28), 0.063, 0.062, 2, DayCountBasis.EUR_30_360, true), 1.009674744, 0.000001) ;
        
        // adjust next coup date to "2013-02-30" so that days before coup is 6 days (30 - 24) instead of 4 days (28 - 24)
        // fix depends on BondUtil.couponCountFraction() adjustment
        assertEquals(BondValuation.price(createDate(2013,2,24), createDate(2017,2,28), 0.01, 0.02, 2, DayCountBasis.EUR_30_360, true), 0.9615876844, 0.000001);
        
        // adjust next coup date to "2013-02-30" so that days before coup is 5 days (30 - 25) instead of 3 days (28 - 24)
        // fix depends on BondUtil.couponCountFraction() adjustment
        assertEquals(BondValuation.price(createDate(2013,2,25), createDate(2016,2,29), 0.02, 0.04, 4, DayCountBasis.EUR_30_360, true), 0.943478084, 0.000001);
        
    }
    @Test
    public void testWithErrors() {
        //assertEquals(BondValuation.price(createDate(2012,2,29), createDate(2027,2,28), 0.063, 0.062, 2, DayCountBasis.EUR_30_360, true), 1.009674744, 0.000001);
    }
    
    
    /**
     * SIFMA TIPS v1
     */
    @Test
    public void testPriceOddBondTIPS() {                
        // p75 Odd Long First
        // settlement = 1992-11-11, issue = 1992-06-15, maturity = 2005-03-01, first coup = 1993-03-01, last coup = 2004-09-01
        // yield = 0.0775, coupon = 0.0935
        // [1.1247 8106]  -- correct
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,6,15), createDate(2005,3,1), createDate(1993,3,1), null, 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.12478106, 0.00000001);
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,6,15), createDate(2005,3,1), createDate(1993,3,1), createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.12478106, 0.00000001);
        
                
        
      //        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,6,15), createDate(1993,3,1), createDate(1993,3,1), null, 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.00405116, 0.00000001);  // ok
        //assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,6,15), createDate(1993,9,1), createDate(1993,3,1), null, 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.01157682, 0.00000001);  // not ok
                        
    }
    
    @Test
    /**
     * examples from Arvin for switch testing 2013-04-03
     * filename: "bondex tabular pricer for testing.xlsx"
     */
    public void testPriceSwitch() {
        // tab: "Tabular Pricer"
        // FXTN 07-44
        assertEquals(BondValuation.price(createDate(2013,3,25), createDate(2020,8,31), 0.04125, 0.039, 2, DayCountBasis.EUR_30_360), 1.01435903, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,25), createDate(2020,8,31), 0.04125*0.8, 0.039*0.8, 2, DayCountBasis.EUR_30_360), 1.01183169, 0.00000001);
        
        // tab: "Tabular Pricer (testing)", clean price
        assertEquals(BondValuation.price(createDate(2013,2,23), createDate(2022,3,26), 0.15, 0.075, 2, DayCountBasis.EUR_30_360), 1.48777719, 0.00000001);
        //assertEquals(BondValuation.price(createDate(2013,2,24), createDate(2017,2,28), 0.01, 0.02, 2, DayCountBasis.EUR_30_360), 0.96158768, 0.00000001);
        //assertEquals(BondValuation.price(createDate(2013,2,25), createDate(2016,2,29), 0.02, 0.04, 4, DayCountBasis.EUR_30_360), 0.94347808, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,26), createDate(2027,9,30), 0.03, 0.06, 2, DayCountBasis.EUR_30_360), 0.71095778, 0.00000001);                        
        assertEquals(BondValuation.price(createDate(2013,2,27), createDate(2022,7,31), 0.04,0.08,4, DayCountBasis.EUR_30_360), 0.73697661, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,28), createDate(2018,8,29), 0.05,0.1,2, DayCountBasis.EUR_30_360), 0.79233964, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,1), createDate(2020,8,30), 0.06,0.12,4, DayCountBasis.EUR_30_360), 0.70618933, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,2), createDate(2019,8,31), 0.07,0.14,2, DayCountBasis.EUR_30_360), 0.70776896, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,3), createDate(2024,5,25), 0.08,0.01,1, DayCountBasis.EUR_30_360), 1.7398682, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,4), createDate(2024,5,25), 0.09,0.03,2, DayCountBasis.EUR_30_360), 1.56816656, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,5), createDate(2024,5,25), 0.1,0.05,3, DayCountBasis.EUR_30_360), 1.4267193, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,6), createDate(2024,5,25), 0.11,0.07,4, DayCountBasis.EUR_30_360), 1.30908146, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,7), createDate(2024,5,25), 0.12,0.09,6, DayCountBasis.EUR_30_360), 1.21092101, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,8), createDate(2024,5,25), 0.13,0.11,12, DayCountBasis.EUR_30_360), 1.12855067, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,9), createDate(2013,6,6), 0.14,0.13,4, DayCountBasis.EUR_30_360), 1.00230752, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,10), createDate(2023,4,27), 0,0.045,2, DayCountBasis.EUR_30_360), 0.6371042, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,11), createDate(2013,4,28), 0,0.045,4, DayCountBasis.EUR_30_360), 0.99415931, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,12), createDate(2020,8,31), 0.05,0.05,2, DayCountBasis.EUR_30_360), 0.99997794, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,13), createDate(2020,8,31), 0.06,0.07,4, DayCountBasis.EUR_30_360), 0.94226316, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,14), createDate(2020,8,31), 0.04,0.03,2, DayCountBasis.EUR_30_360), 1.06635105, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,28), createDate(2020,8,31), 0.02125,0.0175,4, DayCountBasis.EUR_30_360), 1.02630366, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,4,28), createDate(2020,8,31), 0.0325,0.02,2, DayCountBasis.EUR_30_360), 1.08485009, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,15), createDate(2016,2,29), 0.04375,0.0225,4, DayCountBasis.EUR_30_360), 1.06053834, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,16), createDate(2027,9,30), 0.055,0.025,2, DayCountBasis.EUR_30_360), 1.36379631, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,17), createDate(2022,7,31), 0.06625,0.0275,4, DayCountBasis.EUR_30_360), 1.31908874, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,18), createDate(2018,8,29), 0.0775,0.03,2, DayCountBasis.EUR_30_360), 1.23693517, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,19), createDate(2020,8,30), 0.08875,0.0325,4, DayCountBasis.EUR_30_360), 1.37047527, 0.00000001);
        
        // tab: "Tabular Pricer (testing)", dirty price
        assertEquals(BondValuation.price(createDate(2013,2,23), createDate(2022,3,26), 0.15,0.075,2, DayCountBasis.EUR_30_360, false), 1.54902719, 0.00000001);
        //assertEquals(BondValuation.price(createDate(2013,2,24), createDate(2017,2,28), 0.01,0.02,2, DayCountBasis.EUR_30_360, false), 0.96642102, 0.00000001);
        //assertEquals(BondValuation.price(createDate(2013,2,25), createDate(2016,2,29), 0.02,0.04,4, DayCountBasis.EUR_30_360, false), 0.94820031, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,26), createDate(2027,9,30), 0.03,0.06,2, DayCountBasis.EUR_30_360, false), 0.72312445, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,27), createDate(2022,7,31), 0.04,0.08,4, DayCountBasis.EUR_30_360, false), 0.73997661, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,28), createDate(2018,8,29), 0.05,0.1,2, DayCountBasis.EUR_30_360, false), 0.79233964, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,1), createDate(2020,8,30), 0.06,0.12,4, DayCountBasis.EUR_30_360, false), 0.70668933, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,2), createDate(2019,8,31), 0.07,0.14,2, DayCountBasis.EUR_30_360, false), 0.70854674, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,3), createDate(2024,5,25), 0.08,0.01,1, DayCountBasis.EUR_30_360, false), 1.80164598, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,4), createDate(2024,5,25), 0.09,0.03,2, DayCountBasis.EUR_30_360, false), 1.59291656, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,5), createDate(2024,5,25), 0.1,0.05,3, DayCountBasis.EUR_30_360, false), 1.43783041, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,6), createDate(2024,5,25), 0.11,0.07,4, DayCountBasis.EUR_30_360, false), 1.31244257, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,7), createDate(2024,5,25), 0.12,0.09,6, DayCountBasis.EUR_30_360, false), 1.22492101, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,8), createDate(2024,5,25), 0.13,0.11,12, DayCountBasis.EUR_30_360, false), 1.13324512, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,9), createDate(2013,6,6), 0.14,0.13,4, DayCountBasis.EUR_30_360, false), 1.00347419, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,10), createDate(2023,4,27), 0,0.045,2, DayCountBasis.EUR_30_360, false), 0.6371042, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,11), createDate(2013,4,28), 0,0.045,4, DayCountBasis.EUR_30_360, false), 0.99415931, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,12), createDate(2020,8,31), 0.05,0.05,2, DayCountBasis.EUR_30_360, false), 1.00192238, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,13), createDate(2020,8,31), 0.06,0.07,4, DayCountBasis.EUR_30_360, false), 0.94476316, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,14), createDate(2020,8,31), 0.04,0.03,2, DayCountBasis.EUR_30_360, false), 1.06812883, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,28), createDate(2020,8,31), 0.02125,0.0175,4, DayCountBasis.EUR_30_360, false), 1.02630366, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,4,28), createDate(2020,8,31), 0.0325,0.02,2, DayCountBasis.EUR_30_360, false), 1.09026676, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,15), createDate(2016,2,29), 0.04375,0.0225,4, DayCountBasis.EUR_30_360, false), 1.06260432, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,16), createDate(2027,9,30), 0.055,0.025,2, DayCountBasis.EUR_30_360, false), 1.38915742, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,17), createDate(2022,7,31), 0.06625,0.0275,4, DayCountBasis.EUR_30_360, false), 1.32773805, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,18), createDate(2018,8,29), 0.0775,0.03,2, DayCountBasis.EUR_30_360, false), 1.24124072, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,19), createDate(2020,8,30), 0.08875,0.0325,4, DayCountBasis.EUR_30_360, false), 1.37565235, 0.00000001);

        // tab: "Tabular Pricer (testing)", net settlement price (after tax clean price + after tax accrued int)
        assertEquals(BondValuation.price(createDate(2013,2,23), createDate(2022,3,26), 0.15*0.8, 0.075*0.8, 2, DayCountBasis.EUR_30_360, false), 1.46464676, 0.00000001);
        //assertEquals(BondValuation.price(createDate(2013,2,24), createDate(2017,2,28), 0.01*0.8, 0.02*0.8, 2, DayCountBasis.EUR_30_360, false), 0.97286351, 0.00000001);
        //assertEquals(BondValuation.price(createDate(2013,2,25), createDate(2016,2,29), 0.02*0.8, 0.04*0.8, 4, DayCountBasis.EUR_30_360, false), 0.95798137, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,26), createDate(2027,9,30), 0.03*0.8, 0.06*0.8, 2, DayCountBasis.EUR_30_360, false), 0.75993361, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,27), createDate(2022,7,31), 0.04*0.8, 0.08*0.8, 4, DayCountBasis.EUR_30_360, false), 0.77722496, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,28), createDate(2018,8,29), 0.05*0.8, 0.1*0.8, 2, DayCountBasis.EUR_30_360, false), 0.82479047, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,1), createDate(2020,8,30), 0.06*0.8, 0.12*0.8, 4, DayCountBasis.EUR_30_360, false), 0.74604423, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,2), createDate(2019,8,31), 0.07*0.8, 0.14*0.8, 2, DayCountBasis.EUR_30_360, false), 0.74713304, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,3), createDate(2024,5,25), 0.08*0.8, 0.01*0.8, 1, DayCountBasis.EUR_30_360, false), 1.64843442, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,4), createDate(2024,5,25), 0.09*0.8, 0.03*0.8, 2, DayCountBasis.EUR_30_360, false), 1.48961981, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,5), createDate(2024,5,25), 0.1*0.8, 0.05*0.8, 3, DayCountBasis.EUR_30_360, false), 1.36861637, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,6), createDate(2024,5,25), 0.11*0.8, 0.07*0.8, 4, DayCountBasis.EUR_30_360, false), 1.26791042, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,7), createDate(2024,5,25), 0.12*0.8, 0.09*0.8, 6, DayCountBasis.EUR_30_360, false), 1.19515467, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,8), createDate(2024,5,25), 0.13*0.8, 0.11*0.8, 12, DayCountBasis.EUR_30_360, false), 1.11754706, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,9), createDate(2013,6,6), 0.14*0.8, 0.13*0.8, 4, DayCountBasis.EUR_30_360, false), 1.00279638, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,10), createDate(2023,4,27), 0*0.8, 0.045*0.8, 2, DayCountBasis.EUR_30_360, false), 0.69666086, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,11), createDate(2013,4,28), 0*0.8, 0.045*0.8, 4, DayCountBasis.EUR_30_360, false), 0.99532199, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,12), createDate(2020,8,31), 0.05*0.8, 0.05*0.8, 2, DayCountBasis.EUR_30_360, false), 1.00154139, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,13), createDate(2020,8,31), 0.06*0.8, 0.07*0.8, 4, DayCountBasis.EUR_30_360, false), 0.95348731, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,14), createDate(2020,8,31), 0.04*0.8, 0.03*0.8, 2, DayCountBasis.EUR_30_360, false), 1.05573027, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,2,28), createDate(2020,8,31), 0.02125*0.8, 0.0175*0.8, 4, DayCountBasis.EUR_30_360, false), 1.02132366, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,4,28), createDate(2020,8,31), 0.0325*0.8, 0.02*0.8, 2, DayCountBasis.EUR_30_360, false), 1.07325685, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,15), createDate(2016,2,29), 0.04375*0.8, 0.0225*0.8, 4, DayCountBasis.EUR_30_360, false), 1.05042954, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,16), createDate(2027,9,30), 0.055*0.8, 0.025*0.8, 2, DayCountBasis.EUR_30_360, false), 1.32176609, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,17), createDate(2022,7,31), 0.06625*0.8, 0.0275*0.8, 4, DayCountBasis.EUR_30_360, false), 1.26873525, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,18), createDate(2018,8,29), 0.0775*0.8, 0.03*0.8, 2, DayCountBasis.EUR_30_360, false), 1.19628728, 0.00000001);
        assertEquals(BondValuation.price(createDate(2013,3,19), createDate(2020,8,30), 0.08875*0.8, 0.0325*0.8, 4, DayCountBasis.EUR_30_360, false), 1.30770883, 0.00000001);
    }
    
    //@Test
    public void testError() {
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,2,20), null, createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125239066611389, 0.00000001);        
    }
    
    @Test
    public void testPriceOddBondMisc() {
        // regress RPI
        // presence of first coup date means that there is no coup date between issue & first coup date!
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,3,1), null, null, 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125233705, 0.00000001);
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,3,1), createDate(1993,3,1), null, 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125233705, 0.00000001);
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,3,1), null, createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125233705, 0.00000001);
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,3,1), createDate(1993,3,1), createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125233705, 0.00000001);
        
        // odd short first [1993-09-15,1993-03-01]
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,15), createDate(2005,3,1), createDate(1993,3,1), null, 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.12531629457, 0.00000001);
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,15), createDate(2005,3,1), createDate(1993,3,1), createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.12531629457, 0.00000001);
        
        // odd long first [1992-02-15,1993-03-01]
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,2,15), createDate(2005,3,1), createDate(1993,3,1), null, 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.124077930069192, 0.00000001);
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,2,15), createDate(2005,3,1), createDate(1993,3,1), createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.124077930069192, 0.00000001);
               
        // odd long last
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,3,15), null, createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125419314841787, 0.00000001);
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,3,15), createDate(1993,3,1), createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125419314841787, 0.00000001);
        
        // TODO: when maturity = 2005-03-31, denominator becomes 182???
        //assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,3,15), createDate(1993,3,1), createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125692201131007, 0.00000001);
        
        // odd short last
        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,2,28), null, createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125218616036130, 0.00000001);
//        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,2,20), null, createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125239066611389, 0.00000001);
//        assertEquals(BondValuation.priceOddBond(createDate(1992,11,11), createDate(1992,9,1), createDate(2005,2,20), createDate(1993,3,1), createDate(2004,9,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.125239066611389, 0.00000001);
        
        // odd long first, long last
        
        // odd long first, short last
        
        // odd short first, long last
        
        // odd short first, short last        
    }
    
    @Test
    public void testPriceOddFirstBondTIPS() {
        // p75 Odd Long First
        // settlement = 1992-11-11, issue = 1992-06-15, maturity = 2005-03-01, first coup = 1993-03-01, last coup = 2004-09-01
        // yield = 0.0775, coupon = 0.0935
        // [1.1247 8106]  -- correct
        assertEquals(BondValuation.priceOddFirstBond(createDate(1992,11,11), createDate(1992,6,15), createDate(2005,3,1), createDate(1993,3,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.12478106, 0.00000001);
        assertEquals(BondValuation.priceOddFirstBond(createDate(1992,11,11), createDate(1992,6,15), createDate(1993,9,1), createDate(1993,3,1), 0.0935, 0.0775, 2, DayCountBasis.ACT_ACT, true), 1.01157682, 0.00000001); 
    }
    
    /**
     * PDEX SASFinance.computeTBillPVNet uses daysToMaturity instead of (settleDate, MaturityDate),
     * and returns net of tax.
     * 
     * TODO: implement DateUtil.addDays()
     */
    @Test
    public void testPriceDiscountedBillPDEXSAS() {
        Date base = createDate(2012,1,1);
        DayCountBasis basis = DayCountBasis.ACT_360;
        
        // from SASFinance
        // P[net] = (1 - P[gross])*tax + P[gross]
        // P[gross] is the "gross price" of the discounted bill
        // P[net] is P[gross] + tax on interest (1 - P[gross]) at maturity
        // to test, check P[gross] = (P[net] - tax) / (1 - tax)
        
        //assertEquals(computeTBillPVNet(91, 0.05000, 0.2), 0.99002, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 91, basis), 0.05, basis), (0.99002 - 0.2) / (1.0 - 0.2), 0.00001);        
        //assertEquals(computeTBillPVNet(91, 0.04995, 0.2), 0.99002, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 91, basis), 0.04995, basis), (0.99002 - 0.2) / (1.0 - 0.2), 0.00001);
        
        //assertEquals(computeTBillPVNet(182, 0.05248, 0.2), 0.97932, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 182, basis), 0.05248, basis), (0.97932 - 0.2) / (1.0 - 0.2), 0.00001);
        //assertEquals(computeTBillPVNet(182, 0.05125, 0.2), 0.97979, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 182, basis), 0.05125, basis), (0.97979 - 0.2) / (1.0 - 0.2), 0.00001);
        //assertEquals(computeTBillPVNet(182, 0.05350, 0.2), 0.97894, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 182, basis), 0.05350, basis), (0.97894 - 0.2) / (1.0 - 0.2), 0.00001);
        
        //assertEquals(computeTBillPVNet(364, 0.05373, 0.2), 0.95878, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 364, basis), 0.05373, basis), (0.95878 - 0.2) / (1.0 - 0.2), 0.00001);
        //assertEquals(computeTBillPVNet(364, 0.05250, 0.2), 0.95967, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 364, basis), 0.05250, basis), (0.95967 - 0.2) / (1.0 - 0.2), 0.00001);
        //assertEquals(computeTBillPVNet(364, 0.05473, 0.2), 0.95805, 0.00001);
        assertEquals(BondValuation.priceDiscountedBill(base, DateUtil.addDays(base, 364, basis), 0.05473, basis), (0.95805 - 0.2) / (1.0 - 0.2), 0.00001);
    }
    
    @Test
    void testPriceDiscountedBillTBS() {
        // they use this for Zero coupon...
        assertEquals(BondValuation.priceDiscountedBill(createDate(2013,11,14), createDate(2019,5,14), 0.037894586, DayCountBasis.ACT_360), 0.8255850, 0.0000001);
    }
    
    @Test
    void testPriceDiscountedBillWithTaxTBS() {
        // 1st excel example
        assertEquals(BondValuation.priceDiscountedBillWithTax(createDate(2013,9,11), createDate(2013,12,4), 0.0086, DayCountBasis.ACT_360, 0.2),  0.99839788198, 0.000000001);
        
        // 2nd excel
        assertEquals(BondValuation.priceDiscountedBillWithTax(createDate(2013,11,14), createDate(2019,5,14), 0.037894586, DayCountBasis.ACT_360, 0.2), 0.8604680, 0.0000001);
    }
    
    @Test
    public void testPriceDiscountedBill2TIPS() {
        assertEquals(DateUtil.diffDaysActual(createDate(1992,2,7), createDate(1992,3,1)), 23);
        assertEquals(DateUtil.daysOfYear(DayCountBasis.ACT_360, createDate(1992,3,1)), 360);
        assertEquals(BondValuation.priceDiscountedBill2(createDate(1992,2,7), createDate(1992,3,1), 0.0535, DayCountBasis.ACT_360), 0.996581944, 0.000000001);
    }
    
    
    @Test
    public void testYieldDiscountedBillPDEXSAS() {
        Date base = createDate(2012,1,1);
        DayCountBasis basis = DayCountBasis.ACT_360;
        
        // from SASFinance
        // P[net] = (1 - P[gross])*tax + P[gross]
        // P[gross] is the "gross price" of the discounted bill
        // P[net] is P[gross] + tax on interest (1 - P[gross]) at maturity
        // to test, check P[gross] = (P[net] - tax) / (1 - tax)
        
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 91, basis), (0.99002 - 0.2) / (1.0 - 0.2), basis), 0.05, 0.0001);
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 91, basis), (0.99002 - 0.2) / (1.0 - 0.2), basis), 0.04995, 0.0001);
        
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 182, basis), (0.97932 - 0.2) / (1.0 - 0.2), basis), 0.05248, 0.0001);
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 182, basis), (0.97979 - 0.2) / (1.0 - 0.2), basis), 0.05125, 0.0001);
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 182, basis), (0.97894 - 0.2) / (1.0 - 0.2), basis), 0.05350, 0.0001);
        
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 364, basis), (0.95878 - 0.2) / (1.0 - 0.2), basis), 0.05373, 0.0001);
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 364, basis), (0.95967 - 0.2) / (1.0 - 0.2), basis), 0.05250, 0.0001);
        assertEquals(BondValuation.yieldDiscountedBill(base, DateUtil.addDays(base, 364, basis), (0.95805 - 0.2) / (1.0 - 0.2), basis), 0.05473, 0.0001);
    }
    
    @Test
    public void testYieldDiscountedBill2TIPS() {
        assertEquals(BondValuation.yieldDiscountedBill2(createDate(1992,2,7), createDate(1992,3,1), 0.996581944, DayCountBasis.ACT_360), 0.0535, 0.0001);
    }
    
    @Test
    public void testYieldDiscountedBill2Excel() {
        assertEquals(BondValuation.yieldDiscountedBill2(createDate(2008,3,31), createDate(2008,6,1), 0.9845, DayCountBasis.EUR_30_360), 0.091417, 0.0001);
    }
    
    @Test
    public void testYieldPDEXSAS() {
     // [reissue=2008-06-05, maturity=2013-03-03, conv/yr=2, couponrate=8.75%, 
        //  ytmrate=8.59%] = 100.59
        assertEquals(BondValuation.yield(createDate(2008, 6, 5), createDate(2013, 3, 3), 0.0875, 1.0059, 2, DayCountBasis.EUR_30_360), 0.0859, 0.00001);
        
        // [reissue=2008-06-05, maturity=2013-03-03, conv/yr=2, couponrate=8.75%, 
        //  ytmrate=8.375%] = 101.421
        assertEquals(BondValuation.yield(createDate(2008, 6, 5), createDate(2013, 3, 3), 0.0875, 1.01421, 2, DayCountBasis.EUR_30_360), 0.08375, 0.00001);
        
        // [reissue=2008-06-05, maturity=2013-03-03, conv/yr=2, couponrate=8.75%, 
        //  ytmrate=8.495%] = 100.956
        assertEquals(BondValuation.yield(createDate(2008, 6, 5), createDate(2013, 3, 3), 0.0875, 1.00956, 2, DayCountBasis.EUR_30_360), 0.08495, 0.00001);
        
        // [reissue=2008-03-27, maturity=2017-08-23, conv/yr=2, couponrate=7.75%, 
        //  ytmrate=7.227%] = 103.515
        assertEquals(BondValuation.yield(createDate(2008, 3, 27), createDate(2017, 8, 23), 0.0775, 1.03515, 2, DayCountBasis.EUR_30_360), 0.07227, 0.00001);
        
        // [reissue=2008-03-27, maturity=2017-08-23, conv/yr=2, couponrate=7.75%, 
        //  ytmrate=6.95%] = 105.447
        assertEquals(BondValuation.yield(createDate(2008, 3, 27), createDate(2017, 8, 23), 0.0775, 1.05447, 2, DayCountBasis.EUR_30_360), 0.0695, 0.00001);        
        
        // sample PIBD0507B435
        // [reissue=2002-03-21, maturity=2007-02-28, conv/yr=2, couponrate=13.%,
        //  ytmrate=12.5] = 101.780
        assertEquals(BondValuation.yield(createDate(2002, 3, 21), createDate(2007, 2, 28), 0.13, 1.01780, 2, DayCountBasis.EUR_30_360), 0.125, 0.00001);
        
        // sample PIBD0507B435
        // [reissue=2002-03-21, maturity=2007-02-28, conv/yr=2, couponrate=13.%,
        //  ytmrate=12.79] = 100.729
        assertEquals(BondValuation.yield(createDate(2002, 3, 21), createDate(2007, 2, 28), 0.13, 1.00729, 2, DayCountBasis.EUR_30_360), 0.1279, 0.00001);
        
        // bug at Joey17
        // [original=2009-03-27, maturity=2016-03-27, conv/yr=2, couponrate=0.0%,
        //  ytmrate=9.000%] = 53.997
        assertEquals(BondValuation.yield(createDate(2009, 3, 27), createDate(2016, 3, 27), 0.0, 0.53997, 2, DayCountBasis.EUR_30_360), 0.09, 0.00001);
        
        // bug at 20090729-Mike12 (BTr UAT)
        // [reissue=2009-05-28, maturity=2028-12-14, conf/yr=2, couponrate=9.5%
        //  ytmrate=8.814%] = 106.334
        assertEquals(BondValuation.yield(createDate(2009, 5, 28), createDate(2028, 12, 14), 0.095, 1.06334, 2, DayCountBasis.EUR_30_360), 0.0881377, 0.0001);
        
        // 1 coupon left before maturity (not ok when delta=0.00001!!!)
        assertEquals(BondValuation.yield(createDate(2007, 10, 1), createDate(2008, 1, 12), 0.0925, 1.00039, 2, DayCountBasis.EUR_30_360), 0.08925, 0.0001);
    }
    
    @Test
    public void testDurationMacaulayExcel() {
        // settlement on coup date
        assertEquals(BondValuation.durationMacaulay(createDate(2008,1,1), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.ACT_ACT), 5.993775, 0.000001);
        
        // inexact, settlement not on coup date
        // matlab: [md, yd, pd] = bnddury(0.09, 0.08,'2008-01-05', '2016-01-01', 2, 0)   (0 = ACT/ACT)
        assertEquals(BondValuation.durationMacaulay(createDate(2008,1,5), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.ACT_ACT), 5.982785945, 0.000001);
        
        assertEquals(BondValuation.durationMacaulay(createDate(2008,1,5), createDate(2016,1,1), 0.0, 0.09, 2, DayCountBasis.ACT_ACT), 7.989010989010988, 0.000001);
        
        // 30/360E
        assertEquals(BondValuation.durationMacaulay(createDate(2008,1,5), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.EUR_30_360), 5.982663844, 0.000001);
    }
    
    @Test
    public void testDurationModifiedExcel() {
        // settlement on coup date
        assertEquals(BondValuation.durationModified(createDate(2008,1,1), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.ACT_ACT), 5.735669814, 0.000001);
        
        // inexact, settlement not on coup date
        // matlab: [md, yd, pd] = bnddury(0.09, 0.08,'2008-01-05', '2016-01-01', 2, 0)   (0 = ACT/ACT)
        assertEquals(BondValuation.durationModified(createDate(2008,1,5), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.ACT_ACT), 5.725154014, 0.000001);
        
        assertEquals(BondValuation.durationModified(createDate(2008,1,5), createDate(2016,1,1), 0.0, 0.09, 2, DayCountBasis.ACT_ACT), 7.644986592, 0.000001);
        
        // 30/360E
        assertEquals(BondValuation.durationModified(createDate(2008,1,5), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.EUR_30_360), 5.725037172, 0.000001);
    }
    
    @Test
    public void testConvexity() {
        // settlement on coup date
        assertEquals(BondValuation.convexity(createDate(2008,1,1), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.ACT_ACT), 41.95760284, 0.000001);
        
        // inexact, settlement not on coup date
        // matlab: [md, yd, pd] = bnddury(0.09, 0.08,'2008-01-05', '2016-01-01', 2, 0)   (0 = ACT/ACT)
        assertEquals(BondValuation.convexity(createDate(2008,1,5), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.ACT_ACT), 41.83205162, 0.000001);
        
        assertEquals(BondValuation.convexity(createDate(2008,1,5), createDate(2016,1,1), 0.0, 0.09, 2, DayCountBasis.ACT_ACT), 62.10370832, 0.000001);
        
        // 30/360E
        //assertEquals(BondValuation.convexity(createDate(2008,1,5), createDate(2016,1,1), 0.08, 0.09, 2, DayCountBasis.EUR_30_360), 41.83205162, 0.000001);
    }
    
    @Test
    public void testEffectiveInterestRate() {
        // UCPB example #1
        assertEquals(BondValuation.effectiveInterestRate(createDate(2012,11,23), createDate(2031,7,19), 0.08, 1.223715077, 2, DayCountBasis.EUR_30_360), 0.059921388, 0.000000001);
        
        // UCPB example #2
        assertEquals(BondValuation.effectiveInterestRate(createDate(2012,11,1), createDate(2031,7,19), 0.08, 1.224168692, 2, DayCountBasis.EUR_30_360), 0.0599174281015384, 0.00000001);
        
        // UCPB example #3
        assertEquals(BondValuation.effectiveInterestRate(createDate(2012,11,30), createDate(2031,7,19), 0.08, 1.223572761, 2, DayCountBasis.EUR_30_360), 0.0599224473613468, 0.00000001);
        
        // UCPB example #4
        assertEquals(BondValuation.effectiveInterestRate(createDate(2012,2,29), createDate(2031,7,19), 0.08, 1.229224985, 2, DayCountBasis.EUR_30_360), 0.0598612588367067, 0.00000001);
        
        // UCPB example #5
        assertEquals(BondValuation.effectiveInterestRate(createDate(2012,11,23), createDate(2019,6,8), 0.062731, 1.023514838, 4, DayCountBasis.EUR_30_360), 0.0583806673915195, 0.00000001);
        
        // UCPB exmaple #6
        assertEquals(BondValuation.effectiveInterestRate(createDate(2012,11,1), createDate(2019,6,8), 0.062731, 1.023695703, 4, DayCountBasis.EUR_30_360), 0.0583806689312849, 0.0000000001);
        
        // settlement on coupon date, should be equal to YTM
        assertEquals(BondValuation.effectiveInterestRate(createDate(2008,3,3), createDate(2013,3,3), 0.0875, 1.00589894, 2, DayCountBasis.EUR_30_360), 0.086023538, 0.00000001);
        
        // Che test case
        assertEquals(BondValuation.effectiveInterestRate(createDate(2012,11,23), createDate(2031,7,19), 0.08, 1.223715077, 2, DayCountBasis.EUR_30_360, 0.000000001, 26, true), 0.059921388, 0.000000001);
        //assertEquals(BondValuation.effectiveInterestRate(createDate(2012,11,23), createDate(2031,7,19), 0.08, 1.223715077, 2, DayCountBasis.EUR_30_360, 0.000000001, 25, true), 0.059921388, 0.000000001);
    }   
}
