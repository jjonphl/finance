package ph.alephzero.finance.market;

import java.util.Date;

/**
 * Interpolator implementations are expected to be lazy. BasicQuote will call <code>reset()</code> a lot.
 * 
 * @author jon
 *
 */
public interface Interpolator {
    
    void setReference(Quote quote, String type);
    
    /**
     * Return true if interpolator depends on quote type.
     * 
     * @param quoteType
     * @return
     */
    boolean dependsOn(String quoteType);
    
    /**
     * Return true if the quote returned for given date is interpolated.
     * 
     * @param date
     * @return true if quote returned for the date is interpolated.
     */
    boolean isInterpolated(Date date);
    
    /**
     * Quote for the given date
     * 
     * @param date
     * @return underlying or interpolated quote for given date
     */
    Double getQuote(Date date);
    
    /**
     * Reset interpolator. E.g. an interpolator may cache previously calculated
     * values that may become stale after the underlying is changed.
     * 
     */
    void reset();

}
