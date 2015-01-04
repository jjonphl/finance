package ph.alephzero.finance.cashflows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

public class MergedCashFlows implements CashFlows {       
    private HashMap<String, ArrayList<Double>> cashFlows;
    private int size;
    private String totalComponentName;     // total cash flows are stored in a separate component     
    
    public MergedCashFlows() {
        cashFlows = new HashMap<String, ArrayList<Double>>();
        size = 0;
        totalComponentName = null;
    }

    public Date getBaseDate() {
        return null;
    }

    public int getCount() {        
        return size;
    }

    public boolean isEquallySpaced() {
        return true;
    }

    public boolean isDated() {
        return false;
    }

    public List<Date> getDates() {
        return null;
    }

    public double getCashFlow(int i) {
        if (i >= size) {
            throw new ArrayIndexOutOfBoundsException("Only cash flows from 0 to " + (size-1) + " are available.");
        }
        
        double cf = 0.0;
        
        if (totalComponentName == null) {
            for (String comp : cashFlows.keySet()) {
                cf += cashFlows.get(comp).get(i);
            }
        } else {
            cf = cashFlows.get(totalComponentName).get(i);
        }
        return cf;
    }

    public double getCashFlow(int i, String component) {
        if (i >= size) {
            throw new ArrayIndexOutOfBoundsException("Only cash flows from 0 to " + (size-1) + " are available.");
        }
        
        if (!cashFlows.containsKey(component)) {
            throw new UnsupportedOperationException("Cash flow component " + component + " is not available.");
        }
                
        return cashFlows.get(component).get(i);
    }

    public double getCashFlow(Date date) {
        throw new UnsupportedOperationException("Cash flow is not dated.");        
    }

    public double getCashFlow(Date date, String component) {
        throw new UnsupportedOperationException("Cash flow is not dated.");        
    }

    public Set<String> getComponents() {        
        return Collections.unmodifiableSet(cashFlows.keySet());
    }

    public SortedMap<Date, Double> toMap() {
        throw new UnsupportedOperationException("Cash flow is not dated.");
    }
    
    public void merge(CashFlows cashFlows, String component) {
        merge(cashFlows, component, false);
    }
    
    public void merge(CashFlows cashFlows, String component, boolean overwrite) {
        if (cashFlows.isDated()) {
            throw new UnsupportedOperationException("Cannot merge with dated cash flows.");        
        } 
        
        if (this.cashFlows.containsKey(component) && !overwrite) {
            throw new UnsupportedOperationException("Component [" + component + "] already exists and overwrite is false.");
        }
        
        if (cashFlows.getCount() > size) {
            size = cashFlows.getCount();
        }
                
        ArrayList<Double> cf;
        
        if (this.cashFlows.containsKey(component)) {
            cf = this.cashFlows.get(component);
            for (int i = 0; i < cashFlows.getCount(); i++) {    
                if (i < cf.size()) {
                    cf.set(i, cf.get(i) + cashFlows.getCashFlow(i));
                } else {
                    cf.add(cf.get(i) + cashFlows.getCashFlow(i));
                }
            }
        } else {
            cf = new ArrayList<Double>(size);            
            this.cashFlows.put(component, cf);
            
            for (int i = 0; i < cashFlows.getCount(); i++) {
                cf.add(cashFlows.getCashFlow(i));
            }
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
