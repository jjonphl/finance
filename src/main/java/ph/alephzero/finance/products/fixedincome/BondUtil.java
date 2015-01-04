package ph.alephzero.finance.products.fixedincome;

import java.util.Date;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.CashFlows;
import ph.alephzero.finance.util.DateUtil;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * 
 * @ipc:calculator-class
 * @author jon
 *
 */
public final class BondUtil {

    /**
     * Returns the most recent coupon date before the settlement date,
     * starting from issue date and assuming coupon dates are on regular intervals after issue date. 
     * 
     * @ipc:calculation
     * @param issue
     * @param settlement
     * @param frequency
     * @param basis
     * @param force
     * @return
     */
    public static Date previousCouponDate(Date issue, Date settlement, int frequency, DayCountBasis basis, boolean force) {        
        if (12 % frequency != 0) {            
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        Date d0 = issue, d1 = issue;
        int dmonths = 12 / frequency;
        
        if (settlement.compareTo(issue) < 0) {
            if (force) {
                while (settlement.compareTo(d0) < 0) {                    
                    d0 = DateUtil.addMonths(d0, -dmonths, basis);
                }                
            } else {
                throw new UnsupportedOperationException("Settlement date is before issue date, not supported unless force is true.");
            }
        } else {
            while (d1.compareTo(settlement) < 0) {
                d0 = d1;
                d1 = DateUtil.addMonths(d1, dmonths, basis);
            }
        }
        return d0;
    }
    
    /**
     * Return the most recent coupon date before the settlement date, working backwards from
     * the maturity date. If settlement is a coupon date, return settlement.
     * 
     * @ipc:calculation
     * @param settlement
     * @param issueDate
     * @param frequency
     * @param basis
     * @return
     */
    public static Date previousCouponDate2(Date settlement, Date maturity, int frequency, DayCountBasis basis) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        int nmonths = 12 / frequency;
        Date pcd = maturity;
        
        while (pcd.compareTo(settlement) > 0) pcd = DateUtil.addMonths(pcd, - nmonths, basis);
        
        return pcd;
    }
    
    /**
     * Loop forward from issue date. 
     * @ipc:calculation
     * @param issueDate
     * @param settleDate
     * @param frequency
     * @param basis
     * @param force
     * @return
     */
    public static Date nextCouponDateForward(Date issueDate, Date settleDate, int frequency, DayCountBasis basis, boolean force) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }

        Date d0 = issueDate, d1 = issueDate;
        int dmonths = 12 / frequency;
        
        if (settleDate.compareTo(issueDate) < 0) {
            if (force) {
                while (settleDate.compareTo(d1) < 0) {
                    d0 = d1;
                    d1 = DateUtil.addMonths(d1, -dmonths, basis);
                }
            } else {
                throw new UnsupportedOperationException("Settle date is before issue date, not supported unless force is true.");
            }
        } else {
            while (d0.compareTo(settleDate) < 0) {
                d0 = DateUtil.addMonths(d0, dmonths, basis);
            }
        }
        return d0;
    }
    
    /**
     * Excel: COUPNCD(settlement, maturity, frequency, [basis])
     * 
     * Loop backwards from maturity date.
     * 
     * @param settlement
     * @param maturity
     * @param frequency
     * @param basis
     * @return
     */
    public static Date nextCouponDateBackward(Date settlement, Date maturity, int frequency, DayCountBasis basis) {
        return nextCouponDateBackwardSmartAdjust(settlement, maturity, frequency, basis, false);
    }
    
    public static Date nextCouponDateBackwardSmartAdjust(Date settlement, Date maturity, int frequency, DayCountBasis basis, boolean smartAdjust) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        int nmonths = 12 / frequency;
        Date pcd = maturity, ncd = maturity;
        
        while (pcd.compareTo(settlement) > 0) {
            ncd = pcd;
            pcd = DateUtil.addMonths(pcd, - nmonths, basis);
        }
        
        
        if (!smartAdjust) {
            // adjust if maturity is last day of month, compatible with Excel COUPNCD
            if (DateUtil.isLastDayOfMonth(maturity) && !DateUtil.isLastDayOfMonth(ncd)) {
                ncd = DateUtil.lastDayOfMonth(ncd);
            }
        }  else {
            // adjust only if settlement is Feb!
            if (DateUtil.isLastDayOfMonth(maturity) && !DateUtil.isLastDayOfMonth(ncd) && DateUtil.isMonth(settlement, 2)) {
                ncd = DateUtil.lastDayOfMonth(ncd);
            }
        }
        return ncd;
    }
    
    /**
     * Excel: COUPNUM()
     * 
     * If settlement date is a coupon date, exclude it in count.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param frequency
     * @param basis
     * @return number of coupon dates from settlement, exclusive, up to maturity
     */
    public static int couponCount(Date settlement, Date maturity, int frequency, DayCountBasis basis) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        int nmonths = 12 / frequency, coupCount = 0;
        Date pcd = maturity;
        
        while (pcd.compareTo(settlement) > 0) {
            coupCount++;
            pcd = DateUtil.addMonths(pcd, - nmonths, basis);
        }
        
        return coupCount;
    }
    
    /**
     * Returns the number (+fraction) of coupon periods remaining from settlement to maturity.
     * 
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param frequency
     * @param basis
     * @return number (+fraction) of coupon periods remaining from settlement to maturity.
     */
    public static double couponCountFraction(Date settlement, Date maturity, int frequency, DayCountBasis basis) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }        
        
        int nmonths = 12 / frequency;
        double coupCount = 0.0;
        Date lastCoupDate = maturity;
        Date coupDate = DateUtil.addMonths(lastCoupDate, - nmonths, basis); 
        
        while (coupDate.compareTo(settlement) >= 0) {
            coupCount += 1.0;
            lastCoupDate = coupDate;
            coupDate = DateUtil.addMonths(lastCoupDate, - nmonths, basis);
        }

        if (DateUtil.diffDays(coupDate, settlement, basis) > 0) {
            int num = DateUtil.diffDays(settlement, lastCoupDate, basis);
            int den = couponPeriodDays(coupDate, lastCoupDate, frequency, basis);
            
            //System.out.println("num: " + num + ", den: " + den + ", " + settleDate + ", " + lastCoupDate);
            
            // adjustments...
            if (DateUtil.isMonth(lastCoupDate, 2) && DateUtil.isMonth(settlement, 2) && 
                    DateUtil.isLastDayOfMonth(lastCoupDate) && !DateUtil.isLastDayOfMonth(settlement)) {
                num += (30 - DateUtil.getDay(lastCoupDate));
            }
            
            coupCount +=  num * 1.0 / den; 
        }

        return coupCount;
    }
    
    /**
     * Returns the number of days in a coupon period.
     * 
     * For NASD_30_360 & EUR_30_360, this is just 12/frequency * 30. Issue and settlement date is not used.
     * 
     * For ACT_*, previous and next coupon dates before and after settlement date are computed
     * wrt issue date then actual number of days is returned.
     * 
     * @param coupDate0
     * @param coupDate1
     * @param frequency
     * @param basis
     * @return
     */
    public static int couponPeriodDays(Date coupDate0, Date coupDate1, int frequency, DayCountBasis basis) {
        int ndays;
        switch (basis) {
        case NASD_30_360:
        case EUR_30_360:
            if (12 % frequency != 0) {
                throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
            }
            ndays = (12/frequency) * basis.getDaysPerMonth();
            break;
        case ACT_360:
        case ACT_365:
        case ACT_ACT:
            ndays = DateUtil.diffDays(coupDate0, coupDate1, basis);
            break;
        default:
            throw new UnsupportedOperationException("Day count basis not supported.");
        }

        return ndays;
    }

    /**
     * 
     * @param start
     * @param end
     * @param frequency
     * @param basis
     * @param forward
     * @return
     */
    public static double quasiPeriods(Date start, Date end, int frequency, DayCountBasis basis, boolean forward) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        int qp = 0;
        double fraction = 0.0;
        Date current, next, prev;
        int months = 12 / frequency;
        
        if (forward) {
            current = start;
            next = DateUtil.addMonths(start, months, basis);
            
            while (next.compareTo(end) <= 0) {
                qp++;
                current = next;
                next = DateUtil.addMonths(current, months, basis);
            }
            
            fraction = DateUtil.diffDays(current, end, basis) * 1.0 / DateUtil.diffDays(current, next, basis);
        } else {
            current = end;
            prev = DateUtil.addMonths(end, -months, basis);
            
            while (prev.compareTo(start) >= 0) {
                qp++;
                current = prev;
                prev = DateUtil.addMonths(current, -months, basis);
            }
            
            fraction = DateUtil.diffDays(start, current, basis) * 1.0 / DateUtil.diffDays(prev, current, basis);
        }
        
        return qp + fraction;        
    }
    
    /**
     * 
     * @param start
     * @param end
     * @param frequency
     * @param basis
     * @return
     */
    public static double discountPeriods(Date start, Date end, int frequency, DayCountBasis basis) {
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");        
        }
        
        int periods = 0;        
        double fraction = 0.0;
        Date current, next;
        int months = 12 / frequency;
        
        current = start;
        next = DateUtil.addMonths(start, months, basis);
        
        // count forward from start to end
        while (next.compareTo(end) <= 0) {
            periods++;
            current = next;
            next = DateUtil.addMonths(current, months, basis);
        }
        
        //fraction = DateUtil.diffDays(current, end, basis) * 1.0 / (DateUtil.daysOfYear(basis, current) / frequency);
        fraction = DateUtil.diffDays(current, end, basis) * 1.0 / DateUtil.diffDays(current, next, basis);
        
        return periods + fraction;
    }           
    
    public static class BondEIRResidual implements UnivariateFunction {
        private CashFlows cf;
        private int frequency;
        private double price;
        private double couponFraction;
        private int start;
        
        public BondEIRResidual(Date settlement, Date maturity, double couponRate, double price, int frequency, DayCountBasis basis) {
            this.frequency = frequency;
            this.price = price;
            
            Date lastCoupDate = nextCouponDateBackward(settlement, maturity, frequency, basis);
            couponFraction = couponCountFraction(settlement, lastCoupDate, frequency, basis);
            
            cf = BondCashFlowGenerator.cashFlowsRPIBond(settlement, maturity, 1.0, couponRate, frequency, basis);
            start = (cf.getCashFlow(0) != 0.0) ? 0 : 1;
            //System.out.println("START=" + start + ", cf0=" + cf.getCashFlow(start) + ", couponFraction=" + couponFraction);
        }

        @Override
        public double value(double effectiveInterestRate) {
            double eir = effectiveInterestRate / frequency;    // de-annualize
            double prin = price;
            double interestDue, coupon, prin2;

            // [settlement date, first coup date] may not cover 1 whole coup period           
            interestDue = eir * prin * couponFraction;
            coupon = cf.getCashFlow(start, "INTEREST") * couponFraction;
            prin2 = cf.getCashFlow(start, "PRINCIPAL");
            prin -= (coupon - interestDue + prin2);
            //System.out.println("eir=" + eir + ", coupon=" + coupon + ", prin=" + prin);
            
            for (int i = start + 1; i < cf.getCount(); i++) {
                interestDue = eir * prin;
                coupon = cf.getCashFlow(i, "INTEREST");
                prin2 = cf.getCashFlow(i, "PRINCIPAL");
                prin -= (coupon - interestDue + prin2);
                //System.out.println("eir=" + eir + ", coupon=" + coupon + ", prin=" + prin);
            }
            
            //System.out.println("Principal: " + prin);
            return prin;
        }
    }
}
