package ph.alephzero.finance.products;

import java.util.HashMap;
import java.util.Set;

import ph.alephzero.finance.cashflows.CashFlows;

public class BaseContractResult implements ContractResult {
    private String identifier;
    private Double valuation;
    private CashFlows cashFlows;
    private HashMap<String, Double> miscAmounts;
    private boolean error;
    private String errorMessage;

    public BaseContractResult(String identifier) {
        this.identifier = identifier;
        miscAmounts = new HashMap<>();
        error = false;
        errorMessage = null;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Double getValuation() {        
        return valuation;
    }
    
    public void setValuation(Double valuation) {
        this.valuation = valuation;
    }

    @Override
    public CashFlows getCashFlows() {
        return cashFlows;
    }
    
    public void setCashFlows(CashFlows cashFlows) {
        this.cashFlows = cashFlows; 
    }

    @Override
    public Set<String> getAmountTypes() {        
        return miscAmounts.keySet();
    }

    @Override
    public Double getMiscAmount(String amountType) {        
        return (miscAmounts.containsKey(amountType)) ? miscAmounts.get(amountType) : null;
    }
    
    public void setMiscAmount(String amountType, Double amount) {
        if (amount == null) {
            miscAmounts.remove(amountType);
        } else {
            miscAmounts.put(amountType, amount);
        }
    }

    @Override
    public boolean isError() {
        return error;
    }
    
    public void setError(boolean error) {
        this.error = error;        
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
