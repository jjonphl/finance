package ph.alephzero.finance.market;

import java.util.Date;

import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;

public interface YieldTermStructure {
    
    String getName();
    
    String getDescription();
    
    Date getBaseDate();
    
    double getDiscount(Date date);
    
    double getZeroRate(Date date);
    
    double getZeroRate(Date date, int frequency, Compounding compounding, DayCountBasis basis);        
    
    double getForwardRate(Date forward, Date date);
    
    double getForwardRate(Date forward, Date date, int frequency, Compounding compounding, DayCountBasis basis);
    
    int getDefaultFrequency();
    
    void setDefaultFrequency(int frequency);
    
    Compounding getDefaultCompounding();
    
    void setDefaultCompounding(Compounding compounding);
    
    DayCountBasis getDefaultBasis();
    
    void setDefaultBasis(DayCountBasis basis);
}
