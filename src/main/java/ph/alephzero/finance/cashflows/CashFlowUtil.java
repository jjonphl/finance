package ph.alephzero.finance.cashflows;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 
 * @author jon
 *
 */
public class CashFlowUtil {

    /**
     * Convert a non-dated cash flow to a dated cash flow. If the input cash flow is
     * already dated, then it will be returned as-is.
     * 
     * @param cashFlows original cash flows
     * @param dates dates 
     * @return a dated cash flow
     */
    public static CashFlows asDated(CashFlows cashFlows, Date[] dates) {
        if (cashFlows.isDated()) return cashFlows;
        
        if (cashFlows.getCount() != dates.length) {
            throw new UnsupportedOperationException("Length of cash flow & dates do not match.");
        }
        
        Object[] cf0 = new Object[dates.length * 2];
        for (int i = 0; i < dates.length; i++) {
            cf0[i*2] = dates[i];
            cf0[i*2+1] = cashFlows.getCashFlow(i);
        }
        
        return new BasicDatedCashFlows(cf0);
    }
    
    /**
     * Extract from a cash flow component into a separate cash flow object.
     *  
     * @param cashFlows original cash flow
     * @param component
     * @return cash flow object with the component
     */
    public static CashFlows extractComponent(CashFlows cashFlows, String component) {                        
        CashFlows out;
        if (cashFlows.isDated()) {
            BasicDatedCashFlows cf0 = new BasicDatedCashFlows(cashFlows.getBaseDate());
            
            if (cashFlows.getComponents().contains(component)) {
                for (Date date : cashFlows.getDates()) {
                    cf0.add(date, cashFlows.getCashFlow(date, component));                
                }                
            }            
            
            out = cf0;
        } else {
            BasicCashFlows cf0 = new BasicCashFlows();
            if (cashFlows.getComponents().contains(component)) {
                for (int i = 0; i < cashFlows.getCount(); i++) {
                    cf0.add(cf0.getCashFlow(i));
                }
            }
            out = cf0;
        }
        return out;        
    }
    
    public static String asString(CashFlows cashFlows) {
        StringBuilder buff = new StringBuilder();
        
        if (cashFlows.isDated()) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            for (Date d : cashFlows.getDates()) {
                buff.append(f.format(d));
                buff.append("\t");
                buff.append(cashFlows.getCashFlow(d));
                buff.append("\n");
            }
        } else {
            int count = cashFlows.getCount();
            for (int i = 0; i < count; i++) {
                buff.append(cashFlows.getCashFlow(i));
                buff.append("\n");
            }
        }
        
        return buff.toString();
    }
}
