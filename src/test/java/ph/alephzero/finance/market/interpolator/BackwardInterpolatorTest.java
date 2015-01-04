package ph.alephzero.finance.market.interpolator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Date;

import ph.alephzero.finance.market.BasicQuote;
import ph.alephzero.finance.util.DateUtil;

import org.testng.annotations.Test;

public class BackwardInterpolatorTest {
    private BasicQuote quote;
    private Date[] dates;
    private Double[][] quotes;
    
    public BackwardInterpolatorTest() {
        quote = new BasicQuote("AGI", "CLOSE1", "CLOSE2", "CLOSE3", "CLOSE4", "CLOSE5", "CLOSE6", "CLOSE7");
        dates = new Date[] {
                DateUtil.createDate(2010, 1, 1),
                DateUtil.createDate(2010, 1, 5),
                DateUtil.createDate(2010, 1, 10),
                DateUtil.createDate(2010, 1, 15)
        };
        quotes = new Double[][] {
                new Double[] { 1.0,  1.0,  1.0,  1.0, null,  1.0, null },
                new Double[] { 2.0,  2.0,  2.0, null, null, null,  2.0 },
                new Double[] { 3.0,  3.0, null, null, null,  3.0, null },
                new Double[] { 4.0, null, null, null, null, null,  3.0 }
        };
        
        quote.addQuotes(dates, quotes);
    }
    
    @Test
    public void testEmpty() {
        BackwardInterpolator bi = new BackwardInterpolator();
        BasicQuote quote = new BasicQuote("AGI", "CLOSE1");
        bi.setReference(quote, "CLOSE1");
        
        assertNull(bi.getQuote(dates[0]));
    }
    
    @Test
    public void testAvailableDates() {
        BackwardInterpolator bi = new BackwardInterpolator();
        bi.setReference(quote, "CLOSE1");
        
        assertEquals(bi.getQuote(dates[0]), quotes[0][0]);
        assertEquals(bi.getQuote(dates[1]), quotes[1][0]);
        assertEquals(bi.getQuote(dates[2]), quotes[2][0]);
        assertEquals(bi.getQuote(dates[3]), quotes[3][0]);
    }
    
    @Test
    public void testFilledQuote() {
        BackwardInterpolator bi = new BackwardInterpolator();
        bi.setReference(quote, "CLOSE1");
        
        // backward interpolate
        assertEquals(bi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[0][0]);               
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[1][0]);
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[2][0]);
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[3][0]);
        
        // forward interpolate for dates beyond last date
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[3][0]);        
        assertEquals(bi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[3][0]);
    }
    
    @Test
    public void testMissingQuote() {
        BackwardInterpolator bi = new BackwardInterpolator();
        
        bi.setReference(quote, "CLOSE2");
        assertEquals(bi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[0][1]);  
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[1][1]);  
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[2][1]);   // backward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[2][1]);  // forward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[2][1]);
        assertEquals(bi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[2][1]);
        
        bi = new BackwardInterpolator();
        bi.setReference(quote, "CLOSE3");
        assertEquals(bi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[0][2]);  
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[1][2]);  // backward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[1][2]);  // forward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[1][2]); 
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[1][2]);
        assertEquals(bi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[1][2]);
        
        bi = new BackwardInterpolator();
        bi.setReference(quote, "CLOSE4");
        assertEquals(bi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[0][3]);  // backward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[0][3]);  // forward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[0][3]);  
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[0][3]); 
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[0][3]); 
        assertEquals(bi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[0][3]);
        
        bi = new BackwardInterpolator();
        bi.setReference(quote, "CLOSE5");  // all null, no choice...
        assertNull(bi.getQuote(DateUtil.createDate(2009, 12, 25)));
        assertNull(bi.getQuote(DateUtil.createDate(2010, 1, 3)));
        assertNull(bi.getQuote(DateUtil.createDate(2010, 1, 8)));
        assertNull(bi.getQuote(DateUtil.createDate(2010, 1, 13)));
        assertNull(bi.getQuote(DateUtil.createDate(2010, 1, 18)));
        assertNull(bi.getQuote(DateUtil.createDate(2011, 1, 1)));
        
        bi = new BackwardInterpolator();
        bi.setReference(quote, "CLOSE6");  // all null, no choice...
        assertEquals(bi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[0][5]);
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[2][5]);  
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[2][5]);  // forward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[2][5]); // backward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[2][5]); 
        assertEquals(bi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[2][5]);
        
        bi = new BackwardInterpolator();
        bi.setReference(quote, "CLOSE7");  // all null, no choice...
        assertEquals(bi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[1][6]);
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[1][6]);  
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[3][6]);  // forward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[3][6]); // backward
        assertEquals(bi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[3][6]); 
        assertEquals(bi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[3][6]);
        
    }
}
