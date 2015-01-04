package ph.alephzero.finance.cashflows;

import java.util.Date;

public interface MutableDatedCashFlows extends CashFlows {

    /***
     * Add a cash flow for the specified date. If a cash flow already exists for that date,
     * then the amount is added to the existing amount (not overwritten). Otherwise the date
     * is considered to have an amount equal to 0.0 and the new amount is added to it.
     * 
     * This method is applicable only if <code>isDated()</code> is true.
     * 
     * Default component name is not defined, please see doc of implementing class.
     * 
     * @param date date of the cash flow
     * @param amount amount to be added
     */
    void add(Date date, double amount);
    
    /***
     * Add a cash flow for the specified date.
     * 
     * @param date
     * @param amount
     * @param component
     */
    void add(Date date, double amount, String component);
    
    /***
     * Delete all cash flows (e.g. many components) for the specified date.
     * 
     * @param date
     */
    void remove(Date date);
    
    /***
     * Delete a specific cash flow component for the specified date.
     * 
     * @param date
     * @param component
     */
    void remove(Date date, String component);
}
