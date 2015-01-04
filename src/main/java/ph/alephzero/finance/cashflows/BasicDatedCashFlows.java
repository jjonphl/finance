package ph.alephzero.finance.cashflows;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ph.alephzero.finance.util.DateUtil;
import ph.alephzero.finance.util.SortedList;

/**
 * 
 * @author jon
 *
 */
public class BasicDatedCashFlows implements MutableDatedCashFlows {
    private Date baseDate;
    private SortedList<Date> dates;
    private HashMap<Date, Double> cashFlows;
    
    public BasicDatedCashFlows(Date baseDate) {
        this.baseDate = DateUtil.normalize(baseDate);
        dates = new SortedList<Date>();
        cashFlows = new HashMap<Date, Double>();
        
        dates.add(baseDate);
        cashFlows.put(baseDate, 0.0);        
    }
    
    /**
     * Easy construction of a dated cash flow.
     * 
     * @param cashFlowSpec array with format [date1, cf1, date2, cf2, ...]
     */
    public BasicDatedCashFlows(Object... cashFlowSpec) {
        Date date;
                
        if (cashFlowSpec.length % 2 != 0) {
            throw new UnsupportedOperationException("Invalid cash flow spec: length is not even.");
        }
        
        
        // special handling for base date
        if (!(cashFlowSpec[0] instanceof Date)) {
            throw new UnsupportedOperationException("Invalid cash flow spec: element 0 is not a Date object.");
        }        
        
        // initialize storage
        dates = new SortedList<Date>();
        cashFlows = new HashMap<Date, Double>();
        
        this.baseDate = date = DateUtil.normalize((Date) cashFlowSpec[0]);        
        
        for (int i = 1; i < cashFlowSpec.length; i++) {            
            if (i % 2 == 0) { // date
                if (!(cashFlowSpec[i] instanceof Date)) {
                    throw new UnsupportedOperationException("Invalid cash flow spec: element " + i + " is not a Date object.");
                } else {
                    date = DateUtil.normalize((Date) cashFlowSpec[i]);                    
                }
            } else {
                if (!(cashFlowSpec[i] instanceof Double)) {
                    throw new UnsupportedOperationException("Invalid cash flow spec: element " + i + " is not a Double object.");
                } else {
                    dates.add(date);
                    cashFlows.put(date, (Double) cashFlowSpec[i]);                    
                }
            }
        }
    }

    public Date getBaseDate() {        
        return baseDate;
    }
    
    public int getCount() {
        return dates.size();
    }

    /**
     * Not equally spaced in general.
     * 
     * @return false 
     */
    public boolean isEquallySpaced() {
        return false;
    }

    public boolean isDated() {
        return true;
    }

    public List<Date> getDates() {        
        return dates.list();
    }

    public double getCashFlow(int i) {        
        return cashFlows.get(dates.get(i));
    }

    public double getCashFlow(int i, String component) { 
        throw new UnsupportedOperationException("Components are not supported.");
    }

    public double getCashFlow(Date date) {        
        return cashFlows.containsKey(date) ? cashFlows.get(date) : 0.0;
    }

    public double getCashFlow(Date date, String component) {
        throw new UnsupportedOperationException("Components are not supported.");
    }

    public Set<String> getComponents() {
        return Collections.emptySet();
    }

    public SortedMap<Date, Double> toMap() {        
        return new TreeMap<Date, Double>(cashFlows);
    }

    public void add(Date date, double amount) {
        date = DateUtil.normalize(date);
        
        if (date.compareTo(baseDate) < 0) {
            throw new UnsupportedOperationException("Date cannot be earlier than cash flow's base date.");
        }

        if (dates.contains(date)) {
            double curamt = cashFlows.get(date);
            cashFlows.put(date, curamt + amount);
        } else {
            dates.add(date);
            cashFlows.put(date, amount);
        }
        
    }

    public void add(Date date, double amount, String component) {
        throw new UnsupportedOperationException("Components are not supported.");        
    }

    public void remove(Date date) {
        date = DateUtil.normalize(date);
        dates.remove(date);
        cashFlows.remove(date);        
    }

    public void remove(Date date, String component) {
        throw new UnsupportedOperationException("Components are not supported.");        
    }    

}
