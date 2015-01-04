package ph.alephzero.finance.market.interpolator;

import java.util.Date;

import ph.alephzero.finance.market.BasicQuote;
import ph.alephzero.finance.util.DateUtil;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

public class ForwardInterpolatorTest {
    private BasicQuote quote;
    private Date[] dates;
    private Double[][] quotes;
    
    public ForwardInterpolatorTest() {
        quote = new BasicQuote("AGI", "CLOSE1", "CLOSE2", "CLOSE3", "CLOSE4", "CLOSE5", "CLOSE6", "CLOSE7");
        dates = new Date[] {
                DateUtil.createDate(2010, 1, 1),
                DateUtil.createDate(2010, 1, 5),
                DateUtil.createDate(2010, 1, 10),
                DateUtil.createDate(2010, 1, 15)
        };
        quotes = new Double[][] {
                new Double[] { 1.0, null, null, null, null,  1.0, null },
                new Double[] { 2.0, 2.0,  null, null, null, null,  2.0 },
                new Double[] { 3.0, 3.0,  3.0,  null, null,  3.0, null },
                new Double[] { 4.0, 4.0,  4.0,   4.0, null, null,  4.0 }
        };
        
        quote.addQuotes(dates, quotes);
    }    
    
    @Test
    public void testEmpty() {
        ForwardInterpolator fi = new ForwardInterpolator();
        BasicQuote quote = new BasicQuote("AGI", "CLOSE1");
        fi.setReference(quote, "CLOSE1");
        
        assertNull(fi.getQuote(dates[0]));
    }
    
    @Test
    public void testAvailableDates() {
        ForwardInterpolator fi = new ForwardInterpolator();
        fi.setReference(quote, "CLOSE1");
        
        assertEquals(fi.getQuote(dates[0]), quotes[0][0]);
        assertEquals(fi.getQuote(dates[1]), quotes[1][0]);
        assertEquals(fi.getQuote(dates[2]), quotes[2][0]);
        assertEquals(fi.getQuote(dates[3]), quotes[3][0]);
    }
    
    @Test
    public void testFilledQuote() {
        ForwardInterpolator fi = new ForwardInterpolator();
        fi.setReference(quote, "CLOSE1");
        
        // backward interpolate if date is before the earliest available date
        assertEquals(fi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[0][0]);
        
        // forward interpolate
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[0][0]);
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[1][0]);
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[2][0]);
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[3][0]);
        assertEquals(fi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[3][0]);
    }
    
    @Test
    public void testMissingQuote() {
        ForwardInterpolator fi = new ForwardInterpolator();
        
        fi.setReference(quote, "CLOSE2");
        assertEquals(fi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[1][1]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[1][1]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[1][1]);  // forward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[2][1]);
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[3][1]);
        assertEquals(fi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[3][1]);
        
        fi = new ForwardInterpolator();
        fi.setReference(quote, "CLOSE3");
        assertEquals(fi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[2][2]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[2][2]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[2][2]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[2][2]); // forward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[3][2]);
        assertEquals(fi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[3][2]);
        
        fi = new ForwardInterpolator();
        fi.setReference(quote, "CLOSE4");
        assertEquals(fi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[3][3]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[3][3]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[3][3]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[3][3]); // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[3][3]); // forward
        assertEquals(fi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[3][3]);
        
        fi = new ForwardInterpolator();
        fi.setReference(quote, "CLOSE5");  // all null, no choice...
        assertNull(fi.getQuote(DateUtil.createDate(2009, 12, 25)));
        assertNull(fi.getQuote(DateUtil.createDate(2010, 1, 3)));
        assertNull(fi.getQuote(DateUtil.createDate(2010, 1, 8)));
        assertNull(fi.getQuote(DateUtil.createDate(2010, 1, 13)));
        assertNull(fi.getQuote(DateUtil.createDate(2010, 1, 18)));
        assertNull(fi.getQuote(DateUtil.createDate(2011, 1, 1)));
        
        fi = new ForwardInterpolator();
        fi.setReference(quote, "CLOSE6"); 
        assertEquals(fi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[0][5]); // backward 
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[0][5]);  // forward 
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[0][5]); 
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[2][5]);
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[2][5]); 
        assertEquals(fi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[2][5]);
        
        fi = new ForwardInterpolator();
        fi.setReference(quote, "CLOSE7"); 
        assertEquals(fi.getQuote(DateUtil.createDate(2009, 12, 25)), quotes[1][6]);
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 3)), quotes[1][6]);  // backward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 8)), quotes[1][6]);  // forward
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 13)), quotes[1][6]); 
        assertEquals(fi.getQuote(DateUtil.createDate(2010, 1, 18)), quotes[3][6]); 
        assertEquals(fi.getQuote(DateUtil.createDate(2011, 1, 1)), quotes[3][6]);
        
    }
}
