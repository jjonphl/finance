package ph.alephzero.finance.products.fixedincome;

import ph.alephzero.finance.context.Context;
import ph.alephzero.finance.products.BaseContractResult;
import ph.alephzero.finance.products.Contract;
import ph.alephzero.finance.products.ContractHandler;
import ph.alephzero.finance.products.ContractResult;
import ph.alephzero.finance.products.InvalidContractException;

public class BondContractHandler implements ContractHandler {

    public BondContractHandler() {
        
    }
    
    @Override
    public ContractResult calculate(Context context, Contract contract) {
        BaseContractResult result = new BaseContractResult(contract.getIdentifier());
        BondContract c = (BondContract) contract;
        
        try {
            c.validate();
                        
            double price = BondValuation.price(context.getBaseDate(), c.getMaturityDate(), c.getCouponRate(), c.getYield(), c.getFrequency(), c.getBasis());
            result.setValuation(price);
            
            double accruedInterest = BondValuation.accruedInterest(c.getValueDate(), context.getBaseDate(), c.getCouponRate(), c.getFrequency(), c.getBasis());
            result.setMiscAmount("ACCRUED_INTEREST", accruedInterest);
            
        } catch (InvalidContractException e) {
            result.setError(true);
            result.setErrorMessage(e.getMessage());            
        }                
        
        return result;
    }

}
