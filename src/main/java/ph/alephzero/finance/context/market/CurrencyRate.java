package ph.alephzero.finance.context.market;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Historical (+ forecasted) exchange rate for multiple sources between a currency pair.
 * 
 * Internally, exchange rates are stored as CCY1:CCY2 (or CCY2/CCY1), i.e. base currency is
 * CCY2 and the rate is how much a unit of CCY1 costs in CCY2. E.g. USD:PHP is how much (CCY1) USD 1 
 * in (CCY2) PHP. 
 * 
 * @author jon
 *
 */
public class CurrencyRate {
    private String ccy1, ccy2;
    
    public CurrencyRate(String currency1, String currency2) {
        ccy1 = currency1;
        ccy2 = currency2;
    }
    
    public String getBaseCurrency() {
        return ccy1;
    }
    
    public String getForeignCurrency() {
        return ccy2;
    }
    
    public Set<String> getTypes() {
        return null;
    }
    
    public List<Date> getAvailableDates(String type) {
        return null;
    }
    
    public void addRate(String type, Date date, double rate) {
        
    }
    
    public void removeRate(String type, Date date) {
        
    }
    
    public Double getRate(String type, Date date) {
        return null;
    }
        
    public Double convert(String currency, double amount, String type, Date date) {
        return null;
    }

}
