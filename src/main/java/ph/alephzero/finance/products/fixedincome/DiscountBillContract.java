package ph.alephzero.finance.products.fixedincome;

import ph.alephzero.finance.products.BaseContract;
import ph.alephzero.finance.products.Contract;
import ph.alephzero.finance.products.InvalidContractException;

public class DiscountBillContract extends BaseContract implements Contract {

    public DiscountBillContract(String identifier, String type, String currency) {
        super(identifier, type, currency);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void validate() throws InvalidContractException {
        // TODO Auto-generated method stub
        
    }
    

}
