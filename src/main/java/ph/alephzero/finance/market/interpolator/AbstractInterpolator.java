package ph.alephzero.finance.market.interpolator;

import java.util.Date;
import java.util.TreeMap;

import ph.alephzero.finance.market.Interpolator;
import ph.alephzero.finance.market.Quote;

public abstract class AbstractInterpolator implements Interpolator {
    protected Quote quote;
    protected String refType;
    protected TreeMap<Date, Double> quoteCache;
    protected TreeMap<Date, Double> interpCache;
    
    @Override
    public boolean dependsOn(String quoteType) {        
        return quoteType.equals(refType);
    }

    @Override
    public boolean isInterpolated(Date date) {
        lazyInit();        
        return !quoteCache.containsKey(date);
    }
    
    @Override
    public void reset() {
        quoteCache = null;
        interpCache = null;        
    }

    @Override
    public void setReference(Quote quote, String type) {
        this.quote = quote;
        this.refType = type;
        reset();
    }
    
    protected void lazyInit() {        
        if (quoteCache == null) {
            quoteCache = new TreeMap<>();
            for (Date d : quote.getQuoteDates()) {
                Double q = quote.getQuote(d, refType);
                if (q != null) quoteCache.put(d, q);
            }
        }
        
        if (interpCache == null) {
            interpCache = new TreeMap<>();
        }
    }

}
