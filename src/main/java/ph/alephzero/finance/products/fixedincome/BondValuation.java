package ph.alephzero.finance.products.fixedincome;

import java.util.Date;
import java.util.List;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.Message;
import ph.alephzero.finance.cashflows.CashFlows;
import ph.alephzero.finance.util.DateUtil;
import ph.alephzero.finance.util.MathUtil;
import ph.alephzero.finance.util.MathUtil.Function1;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.PegasusSolver;

/**
 * 
 * All methods assume face/par value of 1.0, output can be multiplied directly to actual face value.
 * 
 * Unqualified methods are for regular-periodic-interest (RPI) bonds. Methods for odd bonds are
 * qualified (e.g. {@link #bondPriceOddFirst()})
 * 
 * @ipc:calculator-class
 * @author jon
 *
 */
public final class BondValuation {

    /**
     * Excel: ACCINT(issue, first_interest, settlement, rate, par, frequency)
     *
     * @ipc:calculation
     * @param issue issue date
     * @param settlement settlement date
     * @param couponRate
     * @param frequency
     * @param basis
     * @return
     */
    public static double accruedInterest(Date issue, Date settlement, double couponRate, int frequency, DayCountBasis basis) {
        Date coupDate0 = BondUtil.previousCouponDate(issue, settlement, frequency, basis, false);
        Date coupDate1 = BondUtil.nextCouponDateForward(issue, settlement, frequency, basis, false);        
                
        return (couponRate/frequency) * (1.0*DateUtil.diffDays(coupDate0, settlement, basis)/BondUtil.couponPeriodDays(coupDate0, coupDate1, frequency, basis));
    }
    

    /**
     * Returns accrued interest for odd periods.
     * 
     * For odd first period, use as specified.
     * 
     * For odd last period, use with issue = last coupon date, firstCoupon = maturity date, and settlement date as is.
     * 
     * @ipc:calculation
     * @param issue
     * @param settlement
     * @param firstCoupon
     * @param couponRate
     * @param frequency
     * @param basis
     * @return
     */
    public static double accruedInterestOddFirst(Date issue, Date settlement, Date firstCoupon, double couponRate, int frequency, DayCountBasis basis) {
        Date refDate = BondUtil.previousCouponDate2(settlement, firstCoupon, frequency, basis);
        double accrint;
        
        if (refDate.compareTo(issue) < 0) { // odd short
            // let NS = # of days in quasi period, A = # of days accrued,
            //     refDate = start of quasi period < issue
            //     NS = (firstCoupon - refDate),
            //     A  = (settlement - issue)
            accrint = (couponRate/frequency) * (1.0*DateUtil.diffDays(issue, settlement, basis)/DateUtil.diffDays(refDate, firstCoupon, basis));
        } else if (refDate.compareTo(issue) > 0) { // odd long
            double accrperiods;     // store accrued periods
            Date lastRefDate;
            int nmonths = 12 / frequency;
            
            // let NL = # of days in quasi period, A = # of days accrued
            
            // 1st quasi-period: refDate = start of 1st quasi-period (working backwards from first coup date)
            //                   NL = (firstCoupon - refDate)
            //                   A  = (settlement - refDate)
            accrperiods = 1.0*DateUtil.diffDays(refDate, settlement, basis)/DateUtil.diffDays(refDate, firstCoupon, basis);
            lastRefDate = refDate;
            refDate = DateUtil.addMonths(refDate, -nmonths, basis); //BondUtil.previousCouponDate2(refDate, firstCoupon, frequency, basis);
            
            // full quasi-periods: NL = A
            while (refDate.compareTo(issue) > 0) {
                accrperiods += 1.0;         // the whole period is accrued, just add 1.0
                lastRefDate = refDate;
                refDate = DateUtil.addMonths(refDate, -nmonths, basis);  //BondUtil.previousCouponDate2(refDate, lastRefDate, frequency, basis);
            }
            
            // last quasi-period: refDate = start of last quasi-period, 
            //                    lastRefDate = end of last quasi-period (= start of 2nd to last quasi-period)
            //                    NL = (lastRefDate - refDate)
            //                    A  = (lastRefDate - issue)
            accrperiods += 1.0*DateUtil.diffDays(issue, lastRefDate, basis)/DateUtil.diffDays(refDate, lastRefDate, basis);
            
            
            accrint = (couponRate/frequency) * accrperiods;
        } else {  // regular
            accrint = accruedInterest(issue, settlement, couponRate, frequency, basis);
        }
        return accrint;
    }
    
    /**
     * Excel: PRICE()
     * 
     * Returns clean price. 
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @return clean price
     */
    public static double price(Date settlement, Date maturity, double couponRate, double yield, int frequency, DayCountBasis basis) {
        return price(settlement, maturity, couponRate, yield, frequency, basis, true);
    }
    
    /**
     * Array version of {@link #price(Date, Date, double, double, int, DayCountBasis)} for rJava.
     * 
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @return
     */
    public static double[] price(Date[] settlement, Date[] maturity, double[] couponRate, double[] yield, int[] frequency, DayCountBasis[] basis) {
        int len = settlement.length;
        
        if (maturity.length != len || couponRate.length != len || yield.length != len || frequency.length != len || basis.length != len) {
            throw new IllegalArgumentException(Message.ERR_ARRAY_ARG_DIFF_LENGTH);
        }
        
        double prices[] = new double[len];
        
        for (int i = 0; i < len; i++) {
            prices[i] = price(settlement[i], maturity[i], couponRate[i], yield[i], frequency[i], basis[i]);
        }
        
        return prices;
    }
    
    /**
     * RPI bond price.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @param clean whether to return clean (true) or dirty (false) price
     * @return
     */
    public static double price(Date settlement, Date maturity, double couponRate, double yield, int frequency, DayCountBasis basis, boolean clean) {
        return priceTF(settlement, maturity, couponRate, yield, frequency, basis, clean);
    }
    
    /**
     * Array version of {@link #price(Date, Date, double, double, int, DayCountBasis, boolean)} for rJava.
     * 
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @param clean
     * @return
     */
    public static double[] price(Date[] settlement, Date[] maturity, double[] couponRate, double[] yield, int[] frequency, DayCountBasis[] basis, boolean[] clean) {
        int len = settlement.length;
        
        if (maturity.length != len || couponRate.length != len || yield.length != len || frequency.length != len || basis.length != len || clean.length != len) {
            throw new IllegalArgumentException(Message.ERR_ARRAY_ARG_DIFF_LENGTH);
        }
        
        double[] prices = new double[len];
        
        for (int i = 0; i < len; i++) {
            prices[i] = price(settlement[i], maturity[i], couponRate[i], yield[i], frequency[i], basis[i], clean[i]);
        }
        
        return prices;
    }

    /**
     * Price based on SIFMA TIPS formulas
     * 
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @param clean
     * @return
     */
    private static double pricePDEX(Date settlement, Date maturity, double couponRate, double yield, int frequency, DayCountBasis basis, boolean clean) {
        Date prevCoupDate = BondUtil.previousCouponDate2(settlement, maturity, frequency, basis);
        Date nextCoupDate = BondUtil.nextCouponDateBackward(settlement, maturity, frequency, basis);
        int daysPrevCoup = DateUtil.diffDays(prevCoupDate, settlement, basis);
        int daysNextCoup = DateUtil.diffDays(settlement, nextCoupDate, basis);
        
        int coupCount = BondUtil.couponCount(settlement, maturity, frequency, basis);
        int wholeCoupCount = coupCount - ((daysPrevCoup > 0) ? 1 : 0);
        
        double principalPV;
        double couponPV;
        double accrint = 0.0;
        
        if (wholeCoupCount > 0) {            
            double w = (wholeCoupCount < coupCount) ? (daysNextCoup * 1.0 / (DateUtil.daysOfYear(basis, settlement) * 1.0 / frequency)) : 1.0;
            double _yield = 1 + yield / frequency;    // yield per coup period
            
            principalPV = Math.pow(1/_yield, coupCount - 1 + w);
            couponPV = (couponRate / yield) * Math.pow(_yield, 1-w) * (1.0 - Math.pow(_yield, - coupCount));
        } else {
            double discount = (1 + yield * daysNextCoup * 1.0 / DateUtil.daysOfYear(basis,settlement));
            principalPV = 1.0 / discount;
            couponPV = (couponRate / frequency) / discount;
        }
        
        if (clean) {
            accrint = (couponRate / frequency) * 
                      (DateUtil.diffDays(prevCoupDate, settlement, basis) * 
                              1.0 / DateUtil.diffDays(prevCoupDate, nextCoupDate, basis));
        }

        return principalPV + couponPV - accrint;
    }
    
    /**
     * Price based on computed time-factors.
     * 
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @param clean
     * @return
     */
    private static double priceTF(Date settlement, Date maturity, double couponRate, double yield, int frequency, DayCountBasis basis, boolean clean) {
        CashFlows cf = BondCashFlowGenerator.cashFlowsRPIBondTF(settlement, maturity, 1.0, couponRate, frequency, basis, true);
        
        double P0 = 0.0;
        double accrint = 0.0;
        double y = yield / frequency;
        double t, cf0, df;
                
        if (cf.getCount() > 2) {
            for (int i = 1; i < cf.getCount(); i++) {
                t = cf.getCashFlow(i, "TIME_FACTOR");
                cf0 = cf.getCashFlow(i, "PRINCIPAL") + cf.getCashFlow(i, "INTEREST");
                df = Math.pow(1 + y, t);
                
                //System.out.println("TIME_FACTOR: " + t + ", CASH_FLOW: " + cf0 + ", DF: " + df);
                P0 += cf0 / df;
            }
        } else {
            t = cf.getCashFlow(1, "TIME_FACTOR");
            cf0 = cf.getCashFlow(1, "PRINCIPAL") + cf.getCashFlow(1, "INTEREST");
            df = 1 + y * t;
            
            P0 = cf0 / df;            
        }
        
        if (clean) {
            accrint = (couponRate / frequency) * (1 - cf.getCashFlow(1, "TIME_FACTOR"));            
        }
        
        //System.out.println("ACCRINT: " + accrint);
        
        return P0 - accrint;
    }
    
    /**
     * General price calculator for any combination of odd long/short first/last bond.
     * This calculator discounts the cash flows directly. 
     * 
     * TODO: only works for odd first or last, not yet working for odd first and last.
     * 
     * @ipc:calculation
     * @param settlement
     * @param issue
     * @param maturity
     * @param firstCoupon
     * @param lastCoupon
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @param clean
     * @return
     */
    public static double priceOddBond(Date settlement, Date issue, Date maturity, Date firstCoupon, Date lastCoupon, double couponRate, double yield, int frequency, DayCountBasis basis, boolean clean) {
        CashFlows cf = BondCashFlowGenerator.cashFlowsOddBond(settlement, issue, maturity, firstCoupon, lastCoupon, 1.0, couponRate, frequency, basis);
        List<Date> dates = cf.getDates();
        double accrint = 0.0;
        double pv = 0.0;
        double _yield = 1 + yield / frequency;
        double timeFactor1 = 0.0;
        double timeFactorN = 0.0;
        boolean oddlast = false;
        
        if (cf.getCount() == 1) {
            timeFactor1 = 1.0;
        } else {
            timeFactor1 = BondUtil.discountPeriods(settlement, cf.getDates().get(1), frequency, basis);
        }
        
        if (lastCoupon != null) {
            double lastperiod = BondUtil.discountPeriods(lastCoupon, maturity, frequency, basis);
            if (lastperiod != 1.0) {
                Date settle0 = BondUtil.previousCouponDate(settlement, lastCoupon, frequency, basis, false);                
                timeFactorN = BondUtil.discountPeriods(settle0, maturity, frequency, basis);
                oddlast = true;
            }
        }
                
        // if oddlast, separate discounting for last cash flow
        for (int i = 1; i < dates.size() - ((oddlast) ? 1 : 0); i++) {
            Date date = dates.get(i);
            double discountFactor = Math.pow(_yield, -1 * (i - 1 + timeFactor1));
//            System.out.println("df: " + discountFactor);
//            System.out.println("time factor (" + date + ") = " + (i - 1 + timeFactor1));
            pv += discountFactor * cf.getCashFlow(date);
        }
        
        if (oddlast) {
            int N = dates.size();
            Date date = dates.get(N - 1);
            double discountFactor = Math.pow(_yield, -1 * (N - 3 + timeFactorN));
//            System.out.println("time factor last (" + date + ") = " + (N - 3 + timeFactorN));
            pv += discountFactor * cf.getCashFlow(date);
        }
        
        if (clean) {
            if (firstCoupon != null) {                
                accrint = accruedInterestOddFirst(issue, settlement, firstCoupon, couponRate, frequency, basis);
            } else {
                accrint = accruedInterest(issue, settlement, couponRate, frequency, basis);
            }
        }
        
//        System.out.println("pv: " + pv);
//        System.out.println("accrint: " + accrint);        
//        System.out.println(CashFlowUtil.asString(cf));        
        return pv - accrint;
    }
    
    /**
     * EXCEL: ODDFPRICE()
     * 
     * @ipc:calculation
     * @param settlement
     * @param issue
     * @param maturity
     * @param firstCoupon
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @param clean
     * @return
     */
    public static double priceOddFirstBond(Date settlement, Date issue, Date maturity, Date firstCoupon, double couponRate, double yield, int frequency, DayCountBasis basis, boolean clean) {        
        // discount cf to first coup date
        double pv1 = price(firstCoupon, maturity, couponRate, yield, frequency, basis, false);
        
        // compute first coup amount
        double w = BondUtil.quasiPeriods(issue, firstCoupon, frequency, basis, false);
        double coup1 = w * couponRate * 1.0 / frequency;
        
        // discount everything to settlement date
        double coup1df = Math.pow(1 + yield/frequency, -BondUtil.discountPeriods(settlement, firstCoupon, frequency, basis)); // discount factor               
        double pv = (pv1 + coup1) * coup1df;
        
        double accrint = 0.0;
        if (clean) {                        
            accrint = accruedInterestOddFirst(issue, settlement, firstCoupon, couponRate, frequency, basis);            
        }

        return pv - accrint;
    }

    /**
     * Returns price for discounted bills (e.g. T-Bills). The formula is P = 1 / (1 + yield * f),
     * where f is the year fraction.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param yield
     * @param basis
     * @return
     */
    public static double priceDiscountedBill(Date settlement, Date maturity, double yield, DayCountBasis basis) {
        double f =  DateUtil.diffDays(settlement, maturity, basis) * 1.0 / DateUtil.daysOfYear(basis, maturity);
        return 1.0 / (1.0 + yield * f);
    }
    
    /**
     * Array version of {@link #priceDiscountedBill(Date, Date, double, DayCountBasis)} for rJava.
     * @param settlement
     * @param maturity
     * @param yield
     * @param basis
     * @return
     */
    public static double[] priceDiscountedBill(Date[] settlement, Date[] maturity, double[] yield, DayCountBasis[] basis) {
        int len = settlement.length;
        
        if (maturity.length != len || yield.length != len || basis.length != len) {
            throw new IllegalArgumentException(Message.ERR_ARRAY_ARG_DIFF_LENGTH);
        }
        
        double prices[] = new double[len];
        
        for (int i = 0; i < len; i++) {
            prices[i] = priceDiscountedBill(settlement[i], maturity[i], yield[i], basis[i]);
        }
        
        return prices;
    }
    
    /**
     * Returns price for discounted bills (e.g. T-Bills) plus tax due. The formula for discounted price is
     * P = 1 / (1 + yield * f) where f is the year fraction, while the formula for tax due is
     * T = (1 - P) * taxRate. 
     * 
     * @see #priceDiscountedBill(Date, Date, double, DayCountBasis)
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param yield
     * @param basis
     * @return
     */
    public static double priceDiscountedBillWithTax(Date settlement, Date maturity, double yield, DayCountBasis basis, double taxRate) {
        double p = priceDiscountedBill(settlement, maturity, yield, basis);
        return p + (1.0 - p) * taxRate;        
    }
    
    /**
     * EXCEL: TBILLPRICE()
     * 
     * Returns price for discounted bills based on SIFMA formula P = 1 - yield * f, where f is
     * the year fraction.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param yield
     * @param basis
     * @return
     */
    public static double priceDiscountedBill2(Date settlement, Date maturity, double yield, DayCountBasis basis) {
        double f = DateUtil.diffDays(settlement, maturity, basis) * 1.0 / DateUtil.daysOfYear(basis, maturity);
        return 1 - yield * f;
    }
    
    /**
     * RPI yield given clean price.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param price <b>clean price per 1.0 par value</b> 
     * @param frequency
     * @param basis
     * @return
     */
    public static double yield(final Date settlement, final Date maturity, final double couponRate, final double price, final int frequency, final DayCountBasis basis) {
        Function1 f = new Function1() {            
            @Override
            public double f(double x) {              
                return price - BondValuation.price(settlement, maturity, couponRate, x, frequency, basis);
            }
        };

        return MathUtil.rootNewton(f, 0.10);        
    }
    
    /**
     * Array version of {@link #yield(Date, Date, double, double, int, DayCountBasis)} for rJava.
     * 
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param price
     * @param frequency
     * @param basis
     * @return
     */
    public static double[] yield(final Date[] settlement, final Date[] maturity, final double[] couponRate, final double[] price, final int[] frequency, final DayCountBasis[] basis) {
        int len = settlement.length;
        
        if (maturity.length != len || couponRate.length != len || price.length != len || frequency.length != len || basis.length != len) {
            throw new IllegalArgumentException(Message.ERR_ARRAY_ARG_DIFF_LENGTH);
        }
        
        double[] yields = new double[len];
        
        for (int i = 0; i < len; i++) {
            yields[i] = yield(settlement[i], maturity[i], couponRate[i], price[i], frequency[i], basis[i]);
        }
        
        return yields;
    }
    
    /**
     * Returns yield for discounted bills (e.g. T-Bills) according to the formula  
     * yield = (1 - P) / (f*P) = (1/f) (1/P - 1), where f is the year fraction.
     * 
     * @ipc:calculation
     * @see BondValuation#priceDiscountedBill(Date, Date, double, DayCountBasis)
     * @param settlement
     * @param maturity
     * @param price
     * @param basis
     * @return
     */
    public static double yieldDiscountedBill(Date settlement, Date maturity, double price, DayCountBasis basis) {
        double f = DateUtil.diffDays(settlement, maturity, basis) * 1.0 / DateUtil.daysOfYear(basis, maturity);
        return (1.0 - price) / (price * f);
    }
    
    /**
     * Array version of {@link #yieldDiscountedBill(Date, Date, double, DayCountBasis)} for rJava.
     * 
     * @param settlement
     * @param maturity
     * @param price
     * @param basis
     * @return
     */
    public static double[] yieldDiscountedBill(Date[] settlement, Date[] maturity, double[] price, DayCountBasis[] basis) {
        int len = settlement.length;
        
        if (maturity.length != len || price.length != len || basis.length != len) {
            throw new IllegalArgumentException(Message.ERR_ARRAY_ARG_DIFF_LENGTH);
        }
        
        double yields[] = new double[len];
        
        for (int i = 0; i < len; i++) {
            yields[i] = yieldDiscountedBill(settlement[i], maturity[i], price[i], basis[i]);
        }
        
        return yields;
    }
    
    /**
     * EXCEL: TBILLYIELD()
     * 
     * Returns price for discounted bills based on SIFMA formula yield = (1 - price) / f, where f is
     * the year fraction.
     * 
     * @ipc:calculation
     * @see BondValuation#priceDiscountedBill2(Date, Date, double, DayCountBasis)
     * @param settlement
     * @param maturity
     * @param price
     * @param basis
     * @return
     */
    public static double yieldDiscountedBill2(Date settlement, Date maturity, double price, DayCountBasis basis) {
        double f = DateUtil.diffDays(settlement, maturity, basis) * 1.0 / DateUtil.daysOfYear(basis, maturity);
        return (1 - price) / f;
    }
    

    /**
     * EXCEL: DURATION()
     * 
     * Returns Macaulay duration for RPI bond. 
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @return
     */
    public static double durationMacaulay(Date settlement, Date maturity, double couponRate, double yield, int frequency, DayCountBasis basis) {
        CashFlows cf = BondCashFlowGenerator.cashFlowsRPIBondTF(settlement, maturity, 1.0, couponRate, frequency, basis, true);
        
        double duration = 0.0;
        double P0 = 0.0; //price(settlement, maturity, couponRate, yield, frequency, basis, true);
        double y = yield / frequency;
                
        for (int i = 1; i < cf.getCount(); i++) {
            double t = cf.getCashFlow(i, "TIME_FACTOR");
            double cf0 = cf.getCashFlow(i, "PRINCIPAL") + cf.getCashFlow(i, "INTEREST");
            double df = Math.pow(1 + y, t);
            
            duration += cf0 * t / df;
            P0 += cf0 / df;
        }
        
        //System.out.println("PRICE: " + P0);
        return duration / (frequency * P0);                    
    }
    
    /**
     * EXCEL: MDURATION()
     * 
     * Returns modified duration for RPI bond.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @return
     */
    public static double durationModified(Date settlement, Date maturity, double couponRate, double yield, int frequency, DayCountBasis basis) {
        return durationMacaulay(settlement, maturity, couponRate, yield, frequency, basis) / (1 + yield / frequency);
    }
    
    /**
     * Returns convexity for RPI bond.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param yield
     * @param frequency
     * @param basis
     * @return
     */
    public static double convexity(Date settlement, Date maturity, double couponRate, double yield, int frequency, DayCountBasis basis) {
        CashFlows cf = BondCashFlowGenerator.cashFlowsRPIBondTF(settlement, maturity, 1.0, couponRate, frequency, basis, true);
        
        double convexity = 0.0;
        double P0 = 0.0;
        double y = yield / frequency;
                
        for (int i = 1; i < cf.getCount(); i++) {
            double t = cf.getCashFlow(i, "TIME_FACTOR");
            double cf0 = cf.getCashFlow(i, "PRINCIPAL") + cf.getCashFlow(i, "INTEREST");
            double df = Math.pow(1 + y, t);
                        
            convexity += cf0 * t * (t + 1) / df;
            P0 += cf0 / df;
        }
        
        return convexity / (frequency*frequency * P0 * (1 + y)*(1 + y));
    }
    
    /**
     * RPI effective interest rate (annualized).
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param priceOrYield the price or yield of the bond, depending on value of usePrice. 
     *    Price is per 1.00 par, and yield must be annualized.
     * @param frequency
     * @param basis
     * @param accuracy
     * @param maxEvaluations
     * @param usePrice if true, interpret priceOrYield as price; else interpret as yield 
     * @return
     */
    public static double effectiveInterestRate(Date settlement, Date maturity, double couponRate, double priceOrYield, int frequency, DayCountBasis basis, double accuracy, int maxEvaluations, boolean usePrice) {
        double price = (usePrice) ? priceOrYield : price(settlement, maturity, couponRate, priceOrYield, frequency, basis);
        BondUtil.BondEIRResidual eir = new BondUtil.BondEIRResidual(settlement, maturity, couponRate, price, frequency, basis);
        BaseUnivariateSolver<UnivariateFunction> solver = new PegasusSolver(accuracy);
    
        return solver.solve(maxEvaluations, eir, 0.0, 1.0);
    }
    
    /**
     * RPI effective interest rate (annualized) from price. Default max 10000 iterations and accuracy of 0.00000001.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param couponRate
     * @param price price per 1.00 par.
     * @param frequency
     * @param basis
     * @return
     */
    public static double effectiveInterestRate(Date settlement, Date maturity, double couponRate, double price, int frequency, DayCountBasis basis) {
        return effectiveInterestRate(settlement, maturity, couponRate, price, frequency, basis, 0.00000001, 10000, true);
    }
}
