package ph.alephzero.finance.cashflows;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ph.alephzero.finance.util.SortedList;

/**
 * 
 * @author jon
 *
 */
public class MergedDatedCashFlows implements CashFlows {
    private Date baseDate;
    private String totalComponentName;
    private SortedList<Date> dates;  
    private HashMap<String, HashMap<Date, Double>> cashFlows; 
    
    public MergedDatedCashFlows(Date baseDate) {
        dates = new SortedList<Date>();
        cashFlows = new HashMap<String, HashMap<Date,Double>>();
        this.baseDate = baseDate;        
        totalComponentName = null;
        
        dates.add(baseDate);
    }    

    public Date getBaseDate() {        
        return baseDate;
    }

    public int getCount() {
        return dates.size();
    }

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
        return getCashFlow(dates.get(i));        
    }

    public double getCashFlow(int i, String component) {
        return getCashFlow(dates.get(i), component);
    }

    public double getCashFlow(Date date) {        
        double cf = 0.0;
        Map<Date, Double> cfmap;
        
        if (dates.contains(date)) {
            if (totalComponentName == null) {
                for (String comp : cashFlows.keySet()) {
                    cfmap = cashFlows.get(comp);
                    if (cfmap.containsKey(date)) cf += cfmap.get(date);
                }
            } else {
                cfmap = cashFlows.get(totalComponentName);
                cf = cfmap.containsKey(date) ? cfmap.get(date) : 0.0;
            }
        }

        return cf;        
    }

    public double getCashFlow(Date date, String component) {        
        if (!cashFlows.containsKey(component)) {
            throw new UnsupportedOperationException("Cash flow component " + component + " is not available.");
        }

        Map<Date, Double> cf = cashFlows.get(component);
        return cf.containsKey(date) ? cf.get(date) : 0.0;
    }

    public Set<String> getComponents() {        
        return Collections.unmodifiableSet(cashFlows.keySet());
    }

    public SortedMap<Date, Double> toMap() {
        TreeMap<Date, Double> map = new TreeMap<Date, Double>();
        
        for (Date date : dates.list()) {
            map.put(date, getCashFlow(date));
        }
        
        return map;
    }
    
    public void merge(CashFlows cashFlows, String component) {
        merge(cashFlows, component, false);
    }
    
    public void merge(CashFlows cashFlows, String component, boolean overwrite) {
        if (!cashFlows.isDated()) {
            throw new UnsupportedOperationException("Cannot merge with non-dated cash flows.");        
        }
        
        if (this.cashFlows.containsKey(component) && !overwrite) {
            throw new UnsupportedOperationException("Component [" + component + "] already exists and overwrite is false.");
        }
        
        if (!baseDate.equals(cashFlows.getBaseDate())) {
            throw new UnsupportedOperationException("Only cash flows with the same base date can be merged.");
        }
        
        HashMap<Date, Double> cf;
        
        if (this.cashFlows.containsKey(component)) {
            cf = this.cashFlows.get(component);
        } else {
            cf = new HashMap<Date, Double>();            
            this.cashFlows.put(component, cf);
        }
        
        for (Date date : cashFlows.getDates()) {
            if (!dates.contains(date)) dates.add(date);
            cf.put(date, (cf.containsKey(date) ? cf.get(date) : 0.0) + cashFlows.getCashFlow(date));
        }
    }

    public String getTotalComponentName() {
        return totalComponentName;
    }

    public void setTotalComponentName(String totalComponentName) {
        if (!cashFlows.containsKey(totalComponentName)) {
            throw new UnsupportedOperationException("Merge the cash flow component first before setting the TOTAL component name.");
        }
        
        this.totalComponentName = totalComponentName;
    }

}
