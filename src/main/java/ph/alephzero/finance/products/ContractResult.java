package ph.alephzero.finance.products;

import java.util.Set;

import ph.alephzero.finance.cashflows.CashFlows;

/**
 * 
 * @author jon
 *
 */
public interface ContractResult {

    /**
     * Identifier (<code>Contract.getIdentifier()</code>) of the reference contract.
     *  
     * @return identifier of the reference contract
     */
    String getIdentifier();
    
    /**
     * Returns whether there is an error in contract validation or calculation.
     * 
     * @return true if the contract has error
     */
    boolean isError();
    
    /**
     * Returns the error message  if there is an error in the contract.
     * 
     * @return error message
     */
    String getErrorMessage();
    
    /**
     * Returns the valuation (fair value) as computed by the contract handler.
     * 
     * @return contract valuation
     */
    Double getValuation();
    
    /**
     * Returns the cash flows as computed by the contract handler.
     * 
     * @return contract cash flows
     */
    CashFlows getCashFlows();
    
    /**
     * Returns the list of names for other amounts/numeric values included in this result.
     * Use <code>getMiscAmount(amountType)</code> to retrieve the actual amount.
     * @return
     */
    Set<String> getAmountTypes();
    
    /**
     * Returns other amounts/numeric values computed by the contract handler 
     * (e.g. accrued interest, duration, present value of a basis point).
     * 
     * @param amountType identifier of the amount/numeric value needed
     * 
     * @return
     */
    Double getMiscAmount(String amountType);
}
