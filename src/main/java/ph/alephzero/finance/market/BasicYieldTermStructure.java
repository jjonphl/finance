package ph.alephzero.finance.market;

import java.util.Date;

import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;

public class BasicYieldTermStructure implements YieldTermStructure {
    private Date baseDate;
    private String name;
    private String description;
    private int frequency;
    private Compounding compounding;
    private DayCountBasis basis;

    public BasicYieldTermStructure(Date baseDate, String name, String description) {
        this.baseDate = baseDate;
        this.name = name;
        this.description = description;
    }
    
    @Override
    public String getName() {        
        return name;
    }

    @Override
    public String getDescription() {        
        return description;
    }

    @Override
    public Date getBaseDate() {
        return baseDate;
    }

    @Override
    public double getDiscount(Date date) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getZeroRate(Date date) {        
        return getZeroRate(date, frequency, compounding, basis);
    }

    @Override
    public double getZeroRate(Date date, int frequency,
            Compounding compounding, DayCountBasis basis) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getForwardRate(Date forward, Date date) {        
        return getForwardRate(forward, date, frequency, compounding, basis);
    }

    @Override
    public double getForwardRate(Date forward, Date date, int frequency,
            Compounding compounding, DayCountBasis basis) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getDefaultFrequency() {        
        return frequency;
    }

    @Override
    public void setDefaultFrequency(int frequency) {        
        if (12 % frequency != 0) {
            throw new UnsupportedOperationException("Frequency of " + Integer.toString(frequency) + " not supported.");
        }
        this.frequency = frequency;
    }

    @Override
    public Compounding getDefaultCompounding() {        
        return compounding;
    }

    @Override
    public void setDefaultCompounding(Compounding compounding) {
        this.compounding = compounding;        
    }

    @Override
    public DayCountBasis getDefaultBasis() {        
        return basis;
    }

    @Override
    public void setDefaultBasis(DayCountBasis basis) {
        this.basis = basis;
    }

}
