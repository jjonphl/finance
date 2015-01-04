package ph.alephzero.finance.products;

public class InvalidContractException extends Exception {
    private String contractId;

    /**
     * 
     */
    private static final long serialVersionUID = 1419303355599708286L;

    public InvalidContractException(String contractId, String message) {
        super(message);
        this.contractId = contractId;
        
    }
    
    public String getContractId() {
        return contractId;
    }
}
