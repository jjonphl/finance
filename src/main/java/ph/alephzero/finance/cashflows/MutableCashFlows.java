package ph.alephzero.finance.cashflows;


public interface MutableCashFlows extends CashFlows {    

    /***
     * Add
     * @param i
     * @param amount
     */
    void add(int i, double amount);

    /***
     * 
     * @param i
     * @param amount
     * @param component
     */
    void add(int i, double amount, String component);
        
    /***
     * Adds a cash flow one period after the current last period.
     * @param amount
     */
    void add(double amount);
    
    /***
     * Removes the i-th cash flow for all components. 
     * 
     * If cash flow is not dated, then this removes the i-th cash flow and then bumps forward the 
     * remaining cash flow. E.g. if content of cash flow object is [10,11,12,13], then a call
     * to <code>remove(2)</code> will result to [10,11,13]. I.e. the previously 3rd cash flow 
     * (0-based) becomes the second cash flow.
     *  
     * If cash flow is dated, then remove the i-th date.
     * 
     * @param i
     */
    void remove(int i);
    
    /***
     * 
     * @param i
     * @param component
     */
    void remove(int i, String component);       
    
//    /***
//     * Add all cash flows from another CashFlows object. Components in the other CashFlows object
//     * is ignored, only the total cash flow is considered.
//     * 
//     * Default component name is not defined, please see doc of implementing class.
//     * 
//     * @param cashFlows the other CashFlows object
//     */
//    void merge(CashFlows cashFlows);
//    
//    /***
//     * 
//     * @param cashFlows
//     * @param component
//     */
//    void merge(CashFlows cashFlows, String component);
}
