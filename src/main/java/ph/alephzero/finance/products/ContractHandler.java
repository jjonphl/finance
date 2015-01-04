package ph.alephzero.finance.products;

import ph.alephzero.finance.context.Context;

/**
 * 
 * @author jon
 *
 */
public interface ContractHandler {
   
    /**
     * Computes valuation, cash flow, etc of the input contract.
     * 
     * @param context  
     * @param contract
     * 
     * @return a ContractResult instance with the calculation results
     */
    ContractResult calculate(Context context, Contract contract);
    
}
