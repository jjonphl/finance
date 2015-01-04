package ph.alephzero.finance.products.fixedincome;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.BaseContract;
import ph.alephzero.finance.products.Contract;
import ph.alephzero.finance.products.InvalidContractException;

public class BondContract extends BaseContract implements Contract {
    private String securityId;
    private Double couponRate;
    private Double yield;
    private Integer frequency;
    private DayCountBasis basis;

    public BondContract(String identifier, String type, String currency) {
        super(identifier, type, currency);        
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public Double getCouponRate() {
        return couponRate;
    }

    public void setCouponRate(Double couponRate) {
        this.couponRate = couponRate;
    }

    public Double getYield() {
        return yield;
    }

    public void setYield(Double yield) {
        this.yield = yield;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public DayCountBasis getBasis() {
        return basis;
    }

    public void setBasis(DayCountBasis basis) {
        this.basis = basis;
    }

    @Override
    public void validate() throws InvalidContractException {
        // TODO Auto-generated method stub
        
    }

    
}
