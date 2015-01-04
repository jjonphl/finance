package ph.alephzero.finance.products.fixedincome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.BasicDatedCashFlows;
import ph.alephzero.finance.cashflows.CashFlows;
import ph.alephzero.finance.cashflows.MergedDatedCashFlows;
import ph.alephzero.finance.util.DateUtil;

/**
 * 
 * @ipc:calculator-class
 * @author jon
 *
 */
public final class BondCashFlowGenerator {

    /**
     * Returns cash flow schedule for zero-coupon securities (discounted, add-on bonds).
     * 
     * Cash flow is only on maturity.
     *  
     * @ipc:calculation
     * @param maturity
     * @param settlement
     * @return principal schedule
     */
    public static Date[] scheduleZeroCouponBond(Date maturity) {
        return new Date[] { maturity };
    }
    
    /**
     * Returns cash flow schedule for RPI bonds starting right after settlement date. 
     * I.e. first cash flow is for the first interest payment <i>after</i> the settlement date.
     * The settlement date is not included in the case that it is a coupon date.  
     * @param settlement
     * @param maturity
     * @param frequency number of coupon payments per year
     * @param basis
     * @param issue
     * 
     * @return coupon and principal schedule
     */
    public static Date[] scheduleRPIBond(Date settlement, Date maturity, int frequency, DayCountBasis basis) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        ArrayList<Date> dates = new ArrayList<Date>();
        Date date = DateUtil.normalize(maturity);
        int months = 12 / frequency;
        
        while (date.compareTo(settlement) > 0) {
            dates.add(date);
            date = DateUtil.normalize(DateUtil.addMonths(date, - months, basis));            
        }
        
        Collections.reverse(dates);
        Date[] out = new Date[dates.size()];
        dates.toArray(out);
        
        return out;
    }
    
    /**
     * Returns cash flow schedule for odd bonds starting from settlement date.
     * Date calculation always work backwards from maturity date, or if present
     * the last coupon date, down to the settlement date.
     * 
     * @param settlement
     * @param maturity 
     * @param firstCoupon first coupon date, may be null
     * @param lastCoupon last coupon date, may be null
     * @param frequency
     * @param basis
     * 
     * @return coupon and principal schedule
     */
    public static Date[] scheduleOddBond(Date settlement, Date maturity, Date firstCoupon, Date lastCoupon, int frequency, DayCountBasis basis) {        
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        ArrayList<Date> dates = new ArrayList<>();
        Date prevDate;
        int months = 12 / frequency;
        
        if (settlement.compareTo(maturity) > 0) {
            return new Date[0];
        }
        
        dates.add(maturity);
        prevDate = maturity;
        
        if (lastCoupon != null) {
            if (lastCoupon.compareTo(settlement) > 0) {
                dates.add(lastCoupon);
                prevDate = DateUtil.addMonths(lastCoupon, - months, basis);
            } else {
                prevDate = lastCoupon;
            }
        } else {
            prevDate = DateUtil.addMonths(maturity, - months, basis);
        }
        
        while (prevDate.compareTo(settlement) > 0) {
            if (firstCoupon != null && prevDate.compareTo(firstCoupon) <= 0) {                
                if (prevDate.compareTo(firstCoupon) < 0) {
                    System.out.println("WARNING: did not exactly match at firstCoupon...");
                }
                dates.add(firstCoupon);
                break;
            }
            
            dates.add(prevDate);
            prevDate = DateUtil.addMonths(prevDate, - months, basis);
        }
        
        Collections.reverse(dates);
        Date[] out = new Date[dates.size()];
        dates.toArray(out);
        
        return out;
    }
    
    /**
     * Returns cash flow for an RPI bond. Settlement date is included with zero cash flow, 
     * even if it is a coupon date.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param principal
     * @param couponRate annualized coupon rate
     * @param frequency
     * @param basis
     * @return cash flow for an RPI bond
     */
    public static CashFlows cashFlowsRPIBond(Date settlement, Date maturity, double principal, double couponRate, int frequency, DayCountBasis basis) {        
        return cashFlowsRPIBondTF(settlement, maturity, principal, couponRate, frequency, basis, false);
    }
    
    /**
     * Similar to {@link #cashFlowsRPIBond(Date, Date, double, double, int, DayCountBasis)} but 
     * may optionally include time factors (measured in coupon periods, not years) stored in 
     * component TIME_FACTOR.
     * 
     * @param settlement
     * @param maturity
     * @param principal
     * @param couponRate
     * @param frequency
     * @param basis
     * @param timeFactor TODO
     * @return
     */
    public static CashFlows cashFlowsRPIBondTF(Date settlement, Date maturity, double principal, double couponRate, int frequency, DayCountBasis basis, boolean timeFactor) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        Date[] sched = scheduleRPIBond(settlement, maturity, frequency, basis);
        BasicDatedCashFlows prin = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows intr = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows frac = new BasicDatedCashFlows(settlement);
        
        double coupamt = principal * couponRate * 1.0 / frequency;
        Date nextCoupDate = BondUtil.nextCouponDateBackwardSmartAdjust(settlement, maturity, frequency, basis, true);
        double coupFrac = BondUtil.couponCountFraction(settlement, nextCoupDate, frequency, basis);
        
        //System.out.println("First coupon fraction: " + coupFrac + ", " + nextCoupDate);
        // initial coupon fraction adjustments
        
        for (int i = 0; i < sched.length - 1; i++) {
            prin.add(sched[i], 0.0);
            intr.add(sched[i], coupamt);
            frac.add(sched[i], coupFrac);
            
            coupFrac += 1.0;
        }
        
        prin.add(maturity, principal);
        intr.add(maturity, coupamt);
        frac.add(maturity, coupFrac);
        
        MergedDatedCashFlows out = new MergedDatedCashFlows(settlement);
        out.merge(prin, "PRINCIPAL");
        out.merge(intr, "INTEREST");
        if (timeFactor) out.merge(frac, "TIME_FACTOR");
        
        return out;
    }
    
    /**
     * Returns cash flow for odd bonds, any combination of long/short first/last.
     * 
     * @param settlement
     * @param issue
     * @param maturity
     * @param firstCoupon
     * @param lastCoupon
     * @param principal
     * @param couponRate
     * @param frequency
     * @param basis
     * @return
     */
    public static CashFlows cashFlowsOddBond(Date settlement, Date issue, Date maturity, Date firstCoupon, Date lastCoupon, double principal, double couponRate, int frequency, DayCountBasis basis) {                        
        return cashFlowsOddBondTF(settlement, issue, maturity, firstCoupon, lastCoupon, principal, couponRate, frequency, basis, false);
    }
    
    public static CashFlows cashFlowsOddBondTF(Date settlement, Date issue, Date maturity, Date firstCoupon, Date lastCoupon, double principal, double couponRate, int frequency, DayCountBasis basis, boolean timeFactor) {
        Date[] sched = scheduleOddBond(settlement, maturity, firstCoupon, lastCoupon, frequency, basis);
        BasicDatedCashFlows prin = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows intr = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows frac = new BasicDatedCashFlows(settlement);
        
        double coupamt = principal * couponRate * 1.0 / frequency;
        double coupFrac = 0.0;
        //double coupFrac = BondUtil.couponCountFraction(settlement, nextCoupDate, frequency, basis);
        
        if (firstCoupon != null && firstCoupon.compareTo(settlement) > 0) {
            if (!firstCoupon.equals(sched[0])) {
                System.out.println("WARNING [cashFlowsOddBond]: First cf schedule not the same as first coupon date!!!");
            }

            // determine #quasi-periods in 1st coup period, start from firstCoupon back to issue date                        
            coupFrac = BondUtil.quasiPeriods(issue, firstCoupon, frequency, basis, false);
            intr.add(firstCoupon, principal * coupFrac * couponRate * 1.0 / frequency);
            frac.add(firstCoupon, coupFrac);                       
        } else if (sched.length > 0) {
            coupFrac = BondUtil.couponCountFraction(settlement, sched[0], frequency, basis);
            intr.add(sched[0], coupamt);
            frac.add(sched[0], coupFrac);
        }
        
        
        for (int i = 1; i < sched.length - 1; i++) {
            coupFrac += 1.0;
            intr.add(sched[i], coupamt);
            frac.add(sched[0], coupFrac);
        }
        
        if (sched.length > 1) {
            if (lastCoupon != null && lastCoupon.compareTo(settlement) > 0) {
                // determine #quasi-periods in last coup period, start from lastCoupon forward to maturity
                double w = BondUtil.quasiPeriods(lastCoupon, maturity, frequency, basis, true);                
                coupFrac += w;
                intr.add(maturity, principal * w * couponRate * 1.0 / frequency);
            } else {
                coupFrac += 1.0;
                intr.add(maturity, coupamt);
            }
            frac.add(maturity, coupFrac);
        }
        
        prin.add(maturity, principal);

        MergedDatedCashFlows out = new MergedDatedCashFlows(settlement);
        out.merge(prin, "PRINCIPAL");
        out.merge(intr, "INTEREST");
        if (timeFactor) out.merge(frac, "TIME_FACTOR");
        
        return out;
    }
    
    /**
     * Premium/discount amortization using effective interest rate method. Cashflow 
     * components returned are:
     * 
     *  <ul>
     *  <li>PRINCIPAL - cash flow </li>
     *  <li>COUPON - cash flow for coupon payments</li>
     *  <li>EFFECTIVE_INTEREST - accounting effective interest income based on EIR and carrying value</li>
     *  <li>PREMIUM - premium amortization if price > face value</li>
     *  <li>DISCOUNT - discount amortization if price < face value</li>
     *  </ul>
     * 
     * Note that the EFFECTIVE_INTEREST, PREMIUM and DISCOUNT components are
     * <i>accounting concepts</i> and are not cash flows.
     *  
     * The point of view is this method is on purchasing a bond. On settlement date, 
     * the negative PRINCIPAL represents the cash outflow used to purchase the bond
     * (the bond's price). The PREMIUM and DISCOUNT component give the amount
     * to be booked for premium and discount resp on settlement, positive for debit
     * and negative for credit.
     * 
     * The cash flows for the remaining date use the sign convention of positive
     * for inflow (PRINCIPAL, COUPON) or debit (PREMIUM, DISCOUNT), or negative
     * for outflow (PRINCIPAL, COUPON) or credit (PREMIUM, DISCOUNT).
     *   
     * @ipc:calculation
     * @param maturity
     * @param settlement
     * @param principal
     * @param couponRate
     * @param price purchase price (<b>not</b> price per 1.00 par)
     * @param frequency
     * @param basis
     * @return
     */
    public static CashFlows amortEffectiveInterestRateRPIBond(Date maturity, Date settlement, double principal, double couponRate, double price, int frequency, DayCountBasis basis) {
        double eir = BondValuation.effectiveInterestRate(settlement, maturity, couponRate, price/principal, frequency, basis) / frequency;
        CashFlows cf = cashFlowsRPIBond(settlement, maturity, principal, couponRate, frequency, basis);
        List<Date> dates = cf.getDates();
                
        BasicDatedCashFlows prin = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows intr = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows coup = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows prem = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows disc = new BasicDatedCashFlows(settlement);
                
        double premamt = (price > principal) ? price - principal : 0.0;
        double discamt = (price > principal) ? 0.0 : principal - price;
        double netvalue = price;
        
        // accounting entries on settlement
        prin.add(settlement, -price);
        prem.add(settlement, premamt);
        disc.add(settlement, -discamt);
                       
        for (int i = 1; i < dates.size(); i++) {
            Date date = dates.get(i);
            double coupamt = cf.getCashFlow(date, "INTEREST");
            double prinamt = cf.getCashFlow(date, "PRINCIPAL");
            double intramt = netvalue * eir;
            
            prin.add(date, prinamt);
            intr.add(date, intramt);
            coup.add(date, coupamt);
            if (premamt > 0.0) {
                prem.add(date, - (coupamt - intramt));  // credit premium
                premamt -= coupamt - intramt;
            } else if (discamt > 0.0) {
                disc.add(date, intramt - coupamt);      // debit discount
                discamt -= intramt - coupamt;
            }
            netvalue = principal + premamt - discamt;
        }
        
        MergedDatedCashFlows outcf = new MergedDatedCashFlows(settlement);
        outcf.merge(prin, "PRINCIPAL");
        outcf.merge(intr, "EFFECTIVE_INTEREST");
        outcf.merge(coup, "COUPON");
        outcf.merge(prem, "PREMIUM");
        outcf.merge(disc, "DISCOUNT");
        return outcf;
    }
    
    /**
     * Premium/discount amortization using straight line method. Description of
     * returned cashflow components below:
     * 
     *  <ul>
     *  <li>PRINCIPAL - cash flow </li>
     *  <li>COUPON - cash flow for coupon payments</li>
     *  <li>PREMIUM - premium amortization if price > face value</li>
     *  <li>DISCOUNT - discount amortization if price < face value</li>
     *  </ul>
     * 
     * Note that the PREMIUM and DISCOUNT components are
     * <i>accounting concepts</i> and are not cash flows.
     *  
     * The point of view is this method is on purchasing a bond. On settlement date, 
     * the negative PRINCIPAL represents the cash outflow used to purchase the bond
     * (the bond's price). The PREMIUM and DISCOUNT component give the amount
     * to be booked for premium and discount resp on settlement, positive for debit
     * and negative for credit.
     * 
     * The cash flows for the remaining date use the sign convention of positive
     * for inflow (PRINCIPAL, COUPON) or debit (PREMIUM, DISCOUNT), or negative
     * for outflow (PRINCIPAL, COUPON) or credit (PREMIUM, DISCOUNT).
     * 
     * @ipc:calculation
     * @param maturity
     * @param settlement
     * @param principal
     * @param couponRate
     * @param price purchase price (<b>not</b> price per 1.00 par)
     * @param frequency
     * @param basis
     * @return
     */
    public static CashFlows amortStraightLineRPIBond(Date maturity, Date settlement, double principal, double couponRate, double price, int frequency, DayCountBasis basis) {
        CashFlows cf = cashFlowsRPIBond(settlement, maturity, principal, couponRate, frequency, basis);
        List<Date> dates = cf.getDates();        
        
        BasicDatedCashFlows prin = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows coup = new BasicDatedCashFlows(settlement);        
        BasicDatedCashFlows prem = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows disc = new BasicDatedCashFlows(settlement);
        
        double premamt = (price > principal) ? price - principal : 0.0;
        double discamt = (price > principal) ? 0.0 : principal - price;
        double premamort = 0.0;
        double discamort = 0.0;
        double amort;
        int totalDays = DateUtil.diffDays(settlement, maturity, basis);
        
        // accounting entries on settlement
        prin.add(settlement, -price);
        prem.add(settlement, premamt);
        disc.add(settlement, -discamt);
        
        for (int i = 1; i < dates.size(); i++) {
            Date date = dates.get(i);
            double coupamt = cf.getCashFlow(date, "INTEREST");
            double prinamt = cf.getCashFlow(date, "PRINCIPAL");
            int ndays = DateUtil.diffDays(settlement, date, basis);
            
            prin.add(date, prinamt);            
            coup.add(date, coupamt);
            if (premamt > 0.0) {
                amort = premamt * ndays * 1.0 / totalDays;
                prem.add(date, - (amort - premamort));  // credit premium
                premamort = amort;
            } else if (discamt > 0.0) {
                amort = discamt * ndays * 1.0 / totalDays;
                disc.add(date, amort - discamort);      // debit discount
                discamort = amort;
            }            
        }
        
        MergedDatedCashFlows outcf = new MergedDatedCashFlows(settlement);
        outcf.merge(prin, "PRINCIPAL");        
        outcf.merge(coup, "COUPON");
        outcf.merge(prem, "PREMIUM");
        outcf.merge(disc, "DISCOUNT");
        return outcf;
    }
}
