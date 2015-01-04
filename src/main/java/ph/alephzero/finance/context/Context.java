package ph.alephzero.finance.context;

import java.util.Date;
import java.util.List;

import ph.alephzero.finance.market.Quote;
import ph.alephzero.finance.market.YieldTermStructure;

/**
 * The market, economic context with which to value positions. Specifically a Context
 * object holds these data
 * 
 * <ul>
 *   <li>Quotes for traded instruments</li>
 *   <li>Interest rate curves (yield term structures)</li>
 *   <li>Foreign exchange rates</li>
 * </ul>
 * 
 * In summary context  
 * @author jon
 *
 */
public interface Context {

    Date getBaseDate();
    
    String getBaseCurrency();
    
    // quotes, interest rate curves
    
    // metadata
    List<String> getSymbols();
    
    List<String> getQuoteTypes(String symbol);
    
    /**
     * Returns the dates where a quote type is available for the symbol.
     * If quote type is an interpolated type or if the quote type does not
     * exist, then return null. 
     * 
     * @param symbol 
     * @param type
     * @return dates when quote type is available or null
     */
    List<Date> getQuoteDates(String symbol, String type);
    
    boolean hasSymbol(String symbol);
    
    /**
     * Returns true if symbol exists in this Context and quote type exists
     * for the symbol.
     *  
     * @param symbol
     * @param type quote type
     * @return true if symbol and quote type exists.
     */
    boolean hasQuote(String symbol, String type);
    
    /**
     * Returns true if symbol exists in this Context, quote type exists for
     * the symbol, and for actual quote type there is an available quote for
     * the specified date. For interpolated quote types, return true for all
     * dates (it extrapolates beyond available quote dates in both directions).
     *  
     * @param symbol
     * @param type
     * @param date
     * @return
     */
    boolean hasQuote(String symbol, String type, String date);
    
    List<String> getCurrencies();
    
    List<String> getYieldCurveNames();
    
    List<Date> getYieldCurveDates(String name);

    // get data
    /**
     * Returns the 
     * 
     * @param symbol instrument symbol
     * 
     * @return
     */
    Quote getQuote(String symbol);
    
    /**
     * Returns the quote for the symbol given the type and the context
     * reference date.
     * 
     * @param symbol instrument symbol
     * @param type quote type
     * @return 
     */
    Double getQuote(String symbol, String type);
    
    Double getQuote(String symbol, String type, Date date);
    
    /**
     * Returns the yield curve as of the reporting date.
     * 
     * @param name curve name
     * @return yield curve for the reference date
     */
    YieldTermStructure getCurve(String name);
    
    /**
     * Returns the yield curve as of the given (base) date. If date is after
     * the reporting date, it is assumed that the user wants a forward
     * curve and the reporting date curve is returned instead. If the
     * date is prior to reporting date, it depends on availability.
     * 
     * @param name curve name
     * @param date yield curve base date
     * @return yield curve for the given date 
     */
    YieldTermStructure getCurve(String name, Date date);
    
    /**
     * Returns the discount factor for the given date from the yield curve
     * with the given name and base date equal to the Context's base date.
     * 
     * @param name yield curve name
     * @param date date to get discount factor
     * @return discount factor for the given yield curve name and date
     */
    Double getDiscount(String name, Date date);
    
    
}
