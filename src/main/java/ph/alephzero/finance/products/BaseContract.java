package ph.alephzero.finance.products;

import java.util.Date;

public abstract class BaseContract implements Contract {
    private String identifier;
    private String type;
    private String currency;
    private String symbol;
    private Double amount;
    private Double position;
    private Double originalAmount;
    private Date valueDate;
    private Date maturityDate;
    private Double accruedInterest;

    public BaseContract(String identifier, String type, String currency) {
        this.identifier = identifier;
        this.type = type;
        this.currency = currency;
    }
    
    public String getIdentifier() {        
        return identifier;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;        
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public Double getPosition() {
        return position;
    }

    public void setPosition(Double position) {
        this.position = position;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }
    
    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Date getValueDate() {
        return valueDate;
    }
    
    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }
    
    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Double getAccruedInterest() {
        return accruedInterest;
    }

    public void setAccruedInterest(Double accruedInterest) {
        this.accruedInterest = accruedInterest;
    }
}
