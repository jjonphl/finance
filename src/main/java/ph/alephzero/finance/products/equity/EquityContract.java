package ph.alephzero.finance.products.equity;

import ph.alephzero.finance.products.BaseContract;
import ph.alephzero.finance.products.InvalidContractException;

public class EquityContract extends BaseContract {

    public EquityContract(String identifier, String type, String currency) {
        super(identifier, type, currency);        
    }

    @Override
    public void validate() throws InvalidContractException {
        
        
    }

}
