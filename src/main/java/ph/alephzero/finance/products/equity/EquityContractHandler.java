package ph.alephzero.finance.products.equity;

import ph.alephzero.finance.context.Context;
import ph.alephzero.finance.products.BaseContractResult;
import ph.alephzero.finance.products.Contract;
import ph.alephzero.finance.products.ContractHandler;
import ph.alephzero.finance.products.ContractResult;

public class EquityContractHandler implements ContractHandler {
    private String quoteType;
    
    public EquityContractHandler(String quoteType) {
        this.quoteType = quoteType;
    }

    @Override
    public ContractResult calculate(Context context, Contract contract) {
        BaseContractResult res = new BaseContractResult(contract.getIdentifier());
        Double quote = context.getQuote(contract.getSymbol(), quoteType);
        if (quote != null) {
            res.setValuation(quote * contract.getPosition());
        } else {
            res.setValuation(null);
        }
        return res;
    }

}
