package ph.alephzero.finance.cashflows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Basic cash flow: not dated, no components.
 * 
 * @author jon
 *
 */
public class BasicCashFlows implements MutableCashFlows {
	private ArrayList<Double> cashFlows;
	
	public static BasicCashFlows buildCashFlows(int periods, double pv, double pmt, double fv, boolean arrears) {
	    double[] cfArray = new double[periods + 1];
        Arrays.fill(cfArray, pmt);
        
        if (arrears) {
            cfArray[0] = pv;
            cfArray[periods] += fv;
        } else {
            cfArray[0] += pv;
            cfArray[periods] = fv;
        }
        return new BasicCashFlows(cfArray);                	    
	}
	
	public BasicCashFlows(double... cashFlows) {
		this.cashFlows = new ArrayList<Double>();

		for (double cf : cashFlows) {
		    this.cashFlows.add(cf);
		}		
	}

	public int getCount() {
		return cashFlows.size();
	}

	public boolean isEquallySpaced() {
		return true;
	}

	public boolean isDated() {
		return false;
	}

	public List<Date> getDates() {
		throw new UnsupportedOperationException("Cash flow is not dated.");		
	}

	public double getCashFlow(int i) {
		return cashFlows.get(i);
	}

	public double getCashFlow(int i, String component) {
		throw new UnsupportedOperationException("Components are not supported.");
	}

	public double getCashFlow(Date date)  {
		throw new UnsupportedOperationException("Cash flow is not dated.");
	}

	public double getCashFlow(Date date, String component) {		
		throw new UnsupportedOperationException("Cash flow is not dated.");
	}

	public void add(int i, double amount) {
		if (i < cashFlows.size()) {
			// cash flow already exists
			cashFlows.set(i, cashFlows.get(i) + amount);
		} else {			
			cashFlows.add(amount);
		}		
	}

	public void add(int i, double amount, String component) {
		throw new UnsupportedOperationException("Components are not supported.");		
	}

	public void add(double amount) {
	    cashFlows.add(amount);		
	}

	public void remove(int i) {
		cashFlows.remove(i);		
	}

	public void remove(int i, String component) {
		throw new UnsupportedOperationException("Components are not supported.");		
	}

	public SortedMap<Date, Double> toMap() {
		throw new UnsupportedOperationException("Cash flow is not dated.");
	}

    public Set<String> getComponents() {        
        return Collections.emptySet();
    }

    public Date getBaseDate() {
        return null;
    }

}
