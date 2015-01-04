package ph.alephzero.finance.products;

import java.util.Date;

/***
 * Generic contract. See documentation in sub-classes for asset-class-specific
 * meaning of the fields.
 * 
 * @author jon
 *
 */
public interface Contract {
    
    /**
     * Required. The identifier uniquely identifies a contract. May be used as map keys
     * inside the engine.
     * 
     * @return identifier of the contract
     */
    String getIdentifier();
    
    /**
     * Required. The deal/contract type. This is used inside the engine to find the appropriate 
     * {@link ContractHandler} for this contract.
     * 
     * @return contract type
     */
    String getType();
    
    /**
     * Required. The (main) currency of the amounts used in this contract.
     *  
     * @return currency of contract
     */
    String getCurrency();
    
    /**
     * Optional. The symbol for the instrument involved in this contract.
     * This is required for contracts involving quoted financial instruments.
     * 
     * @return
     */
    String getSymbol();

    /**
     * Optional. The outstanding (principal) amount for each position
     * ({@link #getPosition()}) as of reporting date 
     * ({@link #getBaseDate()}). This is not specified e.g.
     * when we want to find its fair value given a yield curve.
     * 
     * @return outstanding amount
     */
    Double getAmount();
    
    /**
     * Optional. The position in the asset-class represented by this contract as of
     * reporting date. Positive for long, negative for short.
     *  
     * @return
     */
    Double getPosition();
    
    /**
     * Optional. The original principal amount at the start of the contract.
     * 
     * @return original principal amount
     */
    Double getOriginalAmount();    
    
    /**
     * Optional. The value date of the contract.
     * 
     * @return value date of the contract
     */
    Date getValueDate();
    
    /**
     * Optional. The maturity date of the contract.
     * 
     * @return maturity date of the contract
     */
    Date getMaturityDate();
    
    /**
     * Optional. The accrued interest as of reporting date computed outside the engine.
     * 
     * @return accrued interest as of reporting date
     */
    Double getAccruedInterest();
    
    /**
     * Validates the current contract.
     * @throws InvalidContractException TODO
     */
    void validate() throws InvalidContractException;
}
