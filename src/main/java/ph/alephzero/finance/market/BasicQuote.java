package ph.alephzero.finance.market;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ph.alephzero.finance.util.SortedList;

/**
 * NOT THREAD-SAFE!!
 * 
 * To compute "derived quotes" (e.g. adjusted closing, yield from price), reserve
 * a quote type, populate quotes then "post-process" quotes.
 * 
 * @author jon
 *
 */
public class BasicQuote implements Quote {
    private String symbol;
    private String[] quoteTypes;
    private ArrayList<String> interpTypes;   // interpolated types
    private String defaultQuoteType;
    private HashMap<String, Integer> quoteTypesIndex;
    private HashMap<Date, QuoteRow> quotes;
    private HashMap<String, Interpolator> interpolators;
    private SortedList<Date> dates;

    private class QuoteRow {
        private Double[] quotes;
        public QuoteRow() {
            quotes = new Double[quoteTypes.length];
        }
        public void setQuote(String quoteType, Double quote) {
            int idx = quoteTypesIndex.get(quoteType);
            quotes[idx] = quote;
        }
        public Double getQuote(String quoteType) {
            int idx = quoteTypesIndex.get(quoteType);
            return quotes[idx];
        }
    }
    
    public BasicQuote(String symbol, String... quoteTypes) {
        this.symbol = symbol;
        this.quoteTypes = Arrays.copyOf(quoteTypes, quoteTypes.length);
        this.defaultQuoteType = quoteTypes[0];
        this.quotes = new HashMap<>();
        this.quoteTypesIndex = new HashMap<>(quoteTypes.length);
        this.dates = new SortedList<>();
        this.interpTypes = new ArrayList<>();
        this.interpolators = new HashMap<>();
                
        for (int i = 0; i < quoteTypes.length; i++) {
            quoteTypesIndex.put(quoteTypes[i], i);
        }        
    }        
        
    @Override
    public String getSymbol() {        
        return symbol;
    }

    @Override
    public Double getQuote(Date date) {
        return getQuote(date, defaultQuoteType);        
    }

    @Override
    public Double getQuote(Date date, String type) {
        Double quote = null;
        
        if (quoteTypesIndex.containsKey(type)) {
            QuoteRow qr = quotes.get(date);
            if (qr != null) quote = qr.getQuote(type);
        } else if (interpolators.containsKey(type)) {
            quote = interpolators.get(type).getQuote(date);
        }
        
        return quote;
    }
    
    public void setQuote(Date date, Double quote) {
        setQuote(date, defaultQuoteType, quote);
    }
    
    public void setQuote(Date date, String type, Double quote) {
        QuoteRow qr = quotes.get(date);
        if (qr == null) {
            qr = new QuoteRow();
            quotes.put(date, qr);
            dates.add(date);
        }
        qr.setQuote(type, quote);
    }
    
    private void addQuoteNoReset(Date date, Double[] quotes) {
        assert(quotes.length == quoteTypes.length);
        QuoteRow qr = this.quotes.get(date);
        if (qr == null) {
            qr = new QuoteRow();
            this.quotes.put(date, qr);
            dates.add(date);
        }
        for (int i = 0; i < quotes.length; i++) {
            qr.quotes[i] = quotes[i];
        }
    }
    
    public void addQuote(Date date, Double[] quotes) {
        addQuoteNoReset(date, quotes);
        resetInterpolators(null);
    }
    
    /**
     * Bulk upload quotes
     * 
     * @param dates
     * @param quotes
     */
    public void addQuotes(Date[] dates, Double[][] quotes) {
        assert(dates.length == quotes.length);
        for (int i = 0; i < dates.length; i++) {
            addQuoteNoReset(dates[i], quotes[i]);
        }
        resetInterpolators(null);
    }
    
    public void addQuotes(Date[] dates, List<Double[]> quotes) {
        assert(dates.length == quotes.size());
        for (int i = 0; i < dates.length; i++) {
            addQuoteNoReset(dates[i], quotes.get(i));
        }
        resetInterpolators(null);
    }
    
    public void addInterpolator(Interpolator interpolator, String type, String refType) {
        assert(!quoteTypesIndex.containsKey(type));
        assert(quoteTypesIndex.containsKey(refType));
        
        if (!interpTypes.contains(type)) {
            interpTypes.add(type);
        }
        
        interpolators.put(type, interpolator);
        interpolator.setReference(this, refType);
    }

    @Override
    public List<String> getQuoteTypes() {
        return Collections.unmodifiableList(Arrays.asList(quoteTypes));
    }

    @Override
    public List<String> getInterpolatedTypes() {        
        return Collections.unmodifiableList(interpTypes);
    }
    
    @Override
    public List<Date> getQuoteDates() {            
        return dates.list();
    }

    @Override
    public String getDefaultQuoteType() {
        return defaultQuoteType;
    }
    
    private void resetInterpolators(String type) {
        for (Interpolator i : interpolators.values()) {
            if (i.dependsOn(type)) i.reset();
        }
    }

    @Override
    public List<Date> getAvailableQuoteDates(String type) {
        ArrayList<Date> ret = null;
        if (quoteTypesIndex.containsKey(type)) {
            ret = new ArrayList<>();
            int idx = quoteTypesIndex.get(type);  // optimize 
            
            for (Date d : dates.list()) {
                QuoteRow qr = quotes.get(d);
                //qr.getQuote(type);     // optimize: use index directly
                if (qr.quotes[idx] != null) ret.add(d);
            }
        }
        
        return ret;
    }    

}
