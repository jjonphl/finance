package ph.alephzero.finance.engine;

import ph.alephzero.finance.products.ContractResult;

public interface ResultHandler {
    
    /**
     * Called by FinancialEngine just before it begins calculating contracts.
     * 
     * @param batchName
     */
    void starting(String batchName);
    
    /**
     * Called by FinancialEngine after it has finished calculating all contracts.
     * 
     * @param batchName
     */
    void finished(String batchName);
    
    /**
     * Called by FinancialEngine to consume results.
     *  
     * @param batchName
     * @param result
     */
    void handleResults(String batchName, ContractResult result);

}
