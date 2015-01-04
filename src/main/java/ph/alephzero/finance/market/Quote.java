package ph.alephzero.finance.market;

import java.util.Date;
import java.util.List;

/**
 * Implementations of this class are used to store time series of quotes for a specific
 * symbol. Quotes are assumed to be numbers (double). What the quote means vary per instrument, 
 * e.g. for market quote for stocks is price per share, for bonds can be yield or price per 100 par. 
 * 
 * @author jon
 *
 */
public interface Quote {
    
    /**
     * The symbol for this quote.
     * 
     * @return
     */
    String getSymbol();
    
    /**
     * Returns the quote (default quote type) for a specific date.
     * 
     * @param date
     * @return
     */
    Double getQuote(Date date);
    
    /**
     * Returns the quote for the specified quote type and date.
     * 
     * @param date
     * @param type
     * @return
     */
    Double getQuote(Date date, String type);
    
    /**
     * Returns list of quote types available for this symbol.
     * 
     * @return list of quote types available for this symbol
     */
    List<String> getQuoteTypes();
    
    /**
     * Returns list of interpolated types available for this symbol.
     * 
     * @return list of interpolated types available for this symbol
     */
    List<String> getInterpolatedTypes();
    
    /**
     * Returns the sorted list of dates where at least 1 quote type is available.
     * 
     * @return sorted list of dates where at least 1 quote type is available
     */
    List<Date> getQuoteDates();
    
    List<Date> getAvailableQuoteDates(String type);
    
    /**
     * Returns the default quote type.
     * 
     * @return default quote type.
     */
    String getDefaultQuoteType();

}
