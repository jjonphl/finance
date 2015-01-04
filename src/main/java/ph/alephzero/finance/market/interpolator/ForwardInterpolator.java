package ph.alephzero.finance.market.interpolator;

import java.util.ArrayList;
import java.util.Date;

import ph.alephzero.finance.market.Interpolator;
import ph.alephzero.finance.market.Quote;

/**
 * This interpolator uses last available quote for dates without quotes. I.e. past quote is extended <i>forward</i>
 * to the missing date.
 * 
 * @author jon
 *
 */
public class ForwardInterpolator extends AbstractInterpolator implements Interpolator {   

    public ForwardInterpolator() {
        reset();
    }
    
    public ForwardInterpolator(Quote quote, String type) {        
        setReference(quote, type);
    }    

    @Override
    public Double getQuote(Date date) {
        Double quote = null;
        lazyInit();
        
        if (interpCache.containsKey(date)) {
            quote = interpCache.get(date);
        } else if (quoteCache.isEmpty()) {
            //quote = null;
        } else if (quoteCache.containsKey(date)) {
            quote = quoteCache.get(date);            
        } else if (date.compareTo(quoteCache.firstKey()) <= 0) {
            // date is prior to first quote date, backward interpolate
            quote = quoteCache.firstEntry().getValue();        
        } else if (date.compareTo(quoteCache.lastKey()) < 0) {
            ArrayList<Date> dates = new ArrayList<Date>(quoteCache.keySet());
            for (int i = 1; i < dates.size(); i++) {
                if (date.compareTo(dates.get(i-1)) >= 0 && date.compareTo(dates.get(i)) <= 0) {
                    quote = quoteCache.get(dates.get(i-1));
                }
            }
        } else {
            quote = quoteCache.lastEntry().getValue();
        }

        //assert(quote != null);
        return quote;
    }

    
}
