package ph.alephzero.finance.cashflows;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Cash flows represent a time series of monetary amounts that are either inflow or outflow...
 * 
 * This interface represents an immutable/read-only cash flow. 
 * 
 * Positive amounts are inflows, negative amounts are outflows.
 * 
 * Indexing is 0-based. The first amount represents the time 0 cash flow. 
 *  
 * @author jon
 *
 */
public interface CashFlows {
	
    /**
     * Returns the base/reference date. For dated cash flows, index 0 corresponds to this base date
     * 
     * @return base date
     */
    Date getBaseDate();
    
	/***
	 * Returns the number of distinct time points with cash flows. For perpetuities, this is -1.
	 * 
	 * @return number of cash flows, or -1 for perpetuities 
	 */
	int getCount();
		
	/***
	 * Returns true if cash flows can be assumed to occur in regular intervals (i.e. equally spaced).
	 * This should be true if <code>isDated()</code> is false.  
	 *  
	 * @return true if cash flows are equally spaced
	 */
	boolean isEquallySpaced();

	/***
	 * Returns true if each cash flow has an associated date, 
	 * false if integer-indexed only. 
	 * 
	 * @return true if cash flows each have an associated date, else false.
	 */
	boolean isDated();
	
	/***
	 * If <code>isDated()</code> is true, return the dates of the cash flows. 
	 * 
	 * @return dates of the cash flows.
	 */
	List<Date> getDates();
	
	/***
	 * Returns the total cash flow for the i-th period.
	 * 
	 * @param i index of the cash flow to be retrieved
	 * @return i-th cash flow
	 */
	double getCashFlow(int i);
	
	/***
	 * Returns a specific cash flow component for the i-th period.
	 * 
	 * @param i index of the cash flow to be retrieved
	 * @param component the cash flow component (e.g. PRINCIPAL, INTEREST)
	 * @return i-th cash flow component
	 */
	double getCashFlow(int i, String component);
	
	/***
	 * Returns the total cash flow for the specified date.
	 * 
	 * @param date date of the cash flow to be retrieved
	 * @return cash flow for the specified date
	 */
	double getCashFlow(Date date);
	
	/***
	 * Returns a specific cash flow component for the specified date.
	 * 
	 * @param date date of the cash flow to be retrieved
	 * @param component the cash flow component (e.g. PRINCIPAL, INTEREST)
	 * @return cash flow component for the specified date
	 */
	double getCashFlow(Date date, String component);		
	
	/**
	 * Return components available. May return an empty set if underlying
	 * implementation does not support components.
	 * 
	 * @return
	 */
	Set<String> getComponents();
	
	/***
	 * Returns a SortedMap<Date, Double> version of this set of cash flows.
	 * This will throw an exception if isDated() is false. 
	 * 
	 * @return a SortedMap version of this set of cash flows
	 */
	SortedMap<Date, Double> toMap();
}
