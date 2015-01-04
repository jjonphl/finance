package ph.alephzero.finance.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ph.alephzero.finance.market.Quote;
import ph.alephzero.finance.market.YieldTermStructure;

public class BasicContext implements Context {
    private Date baseDate;
    private String baseCurrency;
    private HashMap<String, Quote> quotes;
    private HashMap<String, YieldTermStructure> yieldCurves;

    public BasicContext(Date baseDate, String baseCurrency) {
        this.baseDate = baseDate;
        this.baseCurrency = baseCurrency;
        this.quotes = new HashMap<>();
        this.yieldCurves = new HashMap<>();
    }
    
    @Override
    public Date getBaseDate() {        
        return baseDate;
    }

    @Override
    public String getBaseCurrency() {        
        return baseCurrency;
    }

    @Override
    public List<String> getSymbols() {
        ArrayList<String> symbols = new ArrayList<>(quotes.keySet());
        Collections.sort(symbols);
        return Collections.unmodifiableList(symbols);
    }

    @Override
    public List<String> getQuoteTypes(String symbol) {
        List<String> ret = null;
        if (quotes.containsKey(symbol)) {
            Quote q = quotes.get(symbol);            
            ArrayList<String> _ret = new ArrayList<>();
            _ret.addAll(q.getQuoteTypes());
            _ret.addAll(q.getInterpolatedTypes());
            ret = Collections.unmodifiableList(_ret);
        }
        return ret;
    }

    @Override
    public List<Date> getQuoteDates(String symbol, String type) {
        List<Date> ret = null;
        if (quotes.containsKey(symbol)) {
            Quote q = quotes.get(symbol);
            if (q.getQuoteTypes().contains(type)) {
                ret = q.getAvailableQuoteDates(type);
            }
        }
        return ret;
    }

    @Override
    public boolean hasSymbol(String symbol) {        
        return quotes.containsKey(symbol);
    }

    @Override
    public boolean hasQuote(String symbol, String type) {        
        return quotes.containsKey(symbol) && 
                (quotes.get(symbol).getQuoteTypes().contains(type) ||
                 quotes.get(symbol).getInterpolatedTypes().contains(type));
    }

    @Override
    public boolean hasQuote(String symbol, String type, String date) {
        boolean ret = false;
        if (quotes.containsKey(symbol)) {
            Quote q = quotes.get(symbol);
            if (q.getInterpolatedTypes().contains(type)) {
                ret = true;
            } else if (q.getQuoteTypes().contains(type)) {
                ret = q.getAvailableQuoteDates(type).contains(date);
            }
        }
        return ret;
    }

    @Override
    public List<String> getCurrencies() {        
        return Arrays.asList(baseCurrency);
    }

    @Override
    public List<String> getYieldCurveNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Date> getYieldCurveDates(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Quote getQuote(String symbol) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getQuote(String symbol, String type) {        
        return getQuote(symbol, type, baseDate);
    }

    @Override
    public Double getQuote(String symbol, String type, Date date) {
        Double ret = null;
        if (quotes.containsKey(symbol)) {
            ret = quotes.get(symbol).getQuote(date, type);            
        }
        return ret;
    }

    @Override
    public YieldTermStructure getCurve(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YieldTermStructure getCurve(String name, Date date) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double getDiscount(String name, Date date) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void addQuote(Quote quote) {
        quotes.put(quote.getSymbol(), quote);
    }
    
    public void addYieldCurve(YieldTermStructure yieldCurve) {
        yieldCurves.put(yieldCurve.getName(), yieldCurve);
    }

}
