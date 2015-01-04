package ph.alephzero.finance.cashflows;

import ph.alephzero.finance.util.MathUtil;

/**
 * Time value of money calculations (PV, PMT, FV) return the same sign as the input cash flows
 * (unlike the reverse sign convention e.g. in Excel, HP 130c).
 *  
 * @ipc:calculator-class
 * @author jon
 *
 */
public final class CashFlowCalculator {

    /**
     * Excel: PV(rate, nper, pmt, [fv=0], [type=0])
     *
     * @ipc:calculation
     * @param rate rate to be used for discounting
     * @param periods number of periods
     * @param annuity equal payment amount
     * @param futureValue cash flow amount on the last period
     * @param arrears annuity payment in arrears (TRUE) or at the beginning of the period (FALSE)
     * @return present value of cash flows represented by the input
     */
    public static double presentValue(double rate, double periods, double annuity, double futureValue, boolean arrears) {
        double value;
        
        // get value from annuities
        if (arrears) {
            value = annuity * (1.0 - Math.pow(1.0 + rate, -1.0 * periods)) / rate;
        } else {
            value = annuity * (1.0 + rate - Math.pow(1.0 + rate, 1.0 - periods)) / rate;
        }
        
        // value from from futureValue
        value += futureValue * Math.pow(1.0 + rate, -1.0 * periods);
        
        return value;
    }
    
    
    /**
     * Excel: NPV(rate, values...)
     * 
     * @ipc:calculation
     * @param cashflows non-dated cash flow
     * @param rate rate to be used for discounting
     * @return present value of cash flows
     */
    public static double presentValue(CashFlows cashflows, double rate) {
        double value = 0.0;
        double discountFactor = 1 / (1 + rate);
        
        if (cashflows.isDated()) {
            throw new UnsupportedOperationException("Dated cash flow is not supported.");        
        }

        for (int i = 0; i < cashflows.getCount(); i++) {
            value += cashflows.getCashFlow(i) * Math.pow(discountFactor, i);
        }
        
        return value;
    }
    
    /**
     * Excel: FV(rate, nper, pmt, [pv=0], [type=0])
     * 
     * @ipc:calculation
     * @param rate rate to be used for discounting
     * @param periods number of periods
     * @param annuity equal payment amount
     * @param presentValue cash flow amount on the first period at time 0
     * @param arrears payment in arrears (TRUE) or at the beginning of the period (FALSE)
     * @return future value of cash flows represented by the input
     */
    public static double futureValue(double rate, double periods, double annuity, double presentValue, boolean arrears) {
        double value;
        
        // get value from annuities
        if (arrears) {
        	value = annuity * (-1.0 + Math.pow(1.0 + rate, periods)) / rate;
        } else {
        	value = annuity * (1.0 + rate) * (-1.0 + Math.pow(1.0 + rate, periods)) / rate;
        }
        
        // get value from presentValue
        value += presentValue * Math.pow(1.0 + rate, periods);
        
        return value;
    }
    
    /**
     * 
     * @ipc:calculation
     * @param cashflows non-dated cash flow
     * @param rate rate to be used for discounting
     * @param periods number of periods. If -1 then use cashflows.getCount() - 1.
     * 
     * @return future value of cash flows
     */
    public static double futureValue(CashFlows cashflows, double rate, int periods) {
        double value = 0.0;
        double compoundFactor = (1+rate);
        
        if (cashflows.isDated()) {
            throw new UnsupportedOperationException("Dated cash flow is not supported.");        
        }

        for (int i = 0; i <= periods; i++) {
            value += cashflows.getCashFlow(i) * Math.pow(compoundFactor, periods - i);
        }
        return value;
    }
    
    /**
     * Excel: PMT(rate, nper, pv, [fv=0], [type=0])
     * 
     * @ipc:calculation
     * @param rate rate to be used for discounting
     * @param periods number of periods
     * @param presentValue cash flow amount on the first period at time 0
     * @param futureValue cash flow amount on the last period
     * @param arrears payment in arrears (TRUE) or at the beginning of the period (FALSE)
     * @return equal payment amount equivalent of cash flows represented by the input
     */
    public static double annuities(double rate, int periods, double presentValue, double futureValue, boolean arrears) {
        double annuity;
        
        if (arrears) {
            annuity =  presentValue / ((1.0 - Math.pow(1.0 + rate, -1.0 * periods)) / rate) +
                       futureValue / ((-1.0 + Math.pow(1.0 + rate, periods)) / rate);
        } else {
            annuity = presentValue / ((1.0 + rate - Math.pow(1.0 + rate, 1.0 - periods)) / rate) +
                      futureValue / ((1.0 + rate) * (-1.0 + Math.pow(1.0 + rate, periods)) / rate);
        }
        return annuity;
    }
    
    /**
     * 
     * @ipc:calculation
     * @param cashflows
     * @param rate rate to be used for discounting
     * @param periods
     * @param arrears
     * 
     * @return equal payment amount equivalent of cash flows
     */
    public static double annuities(CashFlows cashflows, double rate, int periods, boolean arrears) {
        double pv = presentValue(cashflows, rate);
        return annuities(rate, periods, pv, 0, arrears);
    }
    
    /**
     * Excel: RATE(nper, pmt, pv, [fv=0], [type=0], [guess=0.10])
     * 
     * @ipc:calculation
     * @param periods
     * @param annuity
     * @param presentValue
     * @param futureValue
     * @param arrears
     * @return
     */
    public static double internalRateOfReturn(int periods, double annuity, double presentValue, double futureValue, boolean arrears) {
        final int _periods = periods;
        final double _annuity = annuity;
        final double _presentValue = presentValue;
        final double _futureValue = futureValue;
        final int _from = (arrears) ? 1 : 0;
        final int _to = (arrears) ? periods : (periods - 1);        
        
        MathUtil.Function1 f = new MathUtil.Function1() {            
            public double f(double rate) {
                double df = 1 + rate;
                return _presentValue + 
                       MathUtil.geometricSeriesSum(_annuity, 1.0/df, _from, _to) + 
                       _futureValue * Math.pow(1.0/df, _periods);                                                
            }
        };
        return MathUtil.rootNewton(f, 0.10);
    }
    
    /**
     * Excel: IRR(values..., [guess=0.10])
     * 
     * @ipc:calculation
     * @param cashflows rate to be used for discounting
     * @return
     */
    public static double internalRateOfReturn(CashFlows cashflows) {        
        final CashFlows _cashflows = cashflows;
        
        MathUtil.Function1 f = new MathUtil.Function1() {            
            public double f(double rate) {                
                return presentValue(_cashflows, rate);
            }                        
        };
        return MathUtil.rootNewton(f, 0.10);
    }
    
}
