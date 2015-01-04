package ph.alephzero.finance.market.interpolator;

import java.util.ArrayList;
import java.util.Date;

import ph.alephzero.finance.market.Interpolator;
import ph.alephzero.finance.market.Quote;

public class BackwardInterpolator extends AbstractInterpolator implements Interpolator {
    
    public BackwardInterpolator() {
        reset();
    }
    
    public BackwardInterpolator(Quote quote, String type) {
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
        } else if (date.compareTo(quoteCache.lastKey()) >= 0) {
            // date is prior to first quote date, backward interpolate
            quote = quoteCache.lastEntry().getValue();        
        } else if (date.compareTo(quoteCache.firstKey()) > 0) {
            ArrayList<Date> dates = new ArrayList<Date>(quoteCache.keySet());
            for (int i = 1; i < dates.size(); i++) {
                if (date.compareTo(dates.get(i-1)) >= 0 && date.compareTo(dates.get(i)) <= 0) {
                    quote = quoteCache.get(dates.get(i));
                }
            }
        } else {
            quote = quoteCache.firstEntry().getValue();
        }

        //assert(quote != null);
        return quote;
    }

}
