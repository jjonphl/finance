package ph.alephzero.finance.util;

import static ph.alephzero.finance.util.DateUtil.createDate;
import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.Calendar;

import ph.alephzero.finance.DayCountBasis;

import org.testng.annotations.Test;

public class DateUtilTest {    

  @Test
  public void testCreateDateMidnight() {
      // test that date generated has time 00:00:00 (i.e. midnight)
      Date d = createDate(2014, 1, 19);
      Calendar cal = Calendar.getInstance();

      cal.setTime(d);

      assertEquals(cal.get(Calendar.HOUR_OF_DAY), 0);
      assertEquals(cal.get(Calendar.HOUR), 0);
      assertEquals(cal.get(Calendar.AM_PM), Calendar.AM);
      assertEquals(cal.get(Calendar.MINUTE), 0);
      assertEquals(cal.get(Calendar.SECOND), 0);
      assertEquals(cal.get(Calendar.MILLISECOND), 0);
  }

  @Test
  public void testDiffDays30NASDExcelExamples() {
	  assertEquals(DateUtil.diffDays30NASD(createDate(2008,1,30), createDate(2008,2,1), 360), 1);
	  assertEquals(DateUtil.diffDays30NASD(createDate(2008,1,1), createDate(2008,12,31), 360), 360);
	  assertEquals(DateUtil.diffDays30NASD(createDate(2008,1,1), createDate(2008,2,1), 360), 30);
	  
	  // Excel DAYS360(DATE(2012,2,29),DATE(2012,8,31))
	  assertEquals(DateUtil.diffDays30NASD(createDate(2012,2,29), createDate(2012,8,31), 360), 180);
	  
	  // Excel: DAYS360(DATE(2011,2,28),DATE(2012,8,28),TRUE)
      assertEquals(DateUtil.diffDays30NASD(createDate(2011,2,28), createDate(2011,8,28), 360), 178);
      assertEquals(DateUtil.diffDays30NASD(createDate(2011,2,28), createDate(2011,8,29), 360), 179);
      assertEquals(DateUtil.diffDays30NASD(createDate(2011,2,28), createDate(2011,8,30), 360), 180);
      assertEquals(DateUtil.diffDays30NASD(createDate(2011,2,28), createDate(2011,8,31), 360), 180);
  }
  
  @Test
  public void testDiffDays30NASDTIPSExample() {
	  assertEquals(DateUtil.diffDays30NASD(createDate(1994,1,31), createDate(1994,3,16), 360), 46);
  }
  
  @Test
  public void testDiffDaysActualTIPSExample() {
	  assertEquals(DateUtil.diffDaysActual(createDate(1994,5,6), createDate(1994,10,30)), 177);
	  
	  assertEquals(DateUtil.diffDaysActual(createDate(1992,2,7), createDate(1992,3,1)), 23);
  }
  
  @Test
  public void testDiffDays30EuropeanExcel() {
      // Excel: DAYS360(DATE(2012,2,29),DATE(2012,8,31),TRUE) 
      assertEquals(DateUtil.diffDays30European(createDate(2012,2,29), createDate(2012,8,31), 360), 181);
      
      // Excel: DAYS360(DATE(2011,2,28),DATE(2012,8,28),TRUE)
      assertEquals(DateUtil.diffDays30European(createDate(2011,2,28), createDate(2011,8,28), 360), 180);
      assertEquals(DateUtil.diffDays30European(createDate(2011,2,28), createDate(2011,8,29), 360), 181);
      assertEquals(DateUtil.diffDays30European(createDate(2011,2,28), createDate(2011,8,30), 360), 182);
      assertEquals(DateUtil.diffDays30European(createDate(2011,2,28), createDate(2011,8,31), 360), 182);
  }
  
  @Test
  public void testAddMonths30NASD() {
      // simplest case, no day adjustment
      assertEquals(DateUtil.addMonths30NASD(createDate(2008,4,1), 6), createDate(2008,10,1));
      
      // day in ref date is more days in resulting month, adjust day
      assertEquals(DateUtil.addMonths30NASD(createDate(2008,1,31), 1), createDate(2008,2,29));
      
      // day in ref date is within # of days in resulting month, no adjustment
      assertEquals(DateUtil.addMonths30NASD(createDate(2008,2,29), 1), createDate(2008,3,29));            
            
      // december of the next year, to test year adjustment 
      assertEquals(DateUtil.addMonths30NASD(createDate(2008,3,15), 21), createDate(2009,12,15));
      
      // get 6 months prior
      assertEquals(DateUtil.addMonths30NASD(createDate(2008,1,31), -6), createDate(2007,7,31));
  }
  
  @Test
  public void testDiffDays30EURShouldReturn30DaysPerMonthPDEXSAS() {
      assertEquals(DateUtil.diffDays(createDate(2008, 1, 1), createDate(2008, 2, 1), DayCountBasis.EUR_30_360), 30);
      assertEquals(DateUtil.diffDays(createDate(2008, 1, 1), createDate(2008, 2, 2), DayCountBasis.EUR_30_360), 31);
      
      assertEquals(DateUtil.diffDays(createDate(2008, 2, 1), createDate(2008, 3, 1), DayCountBasis.EUR_30_360), 30);
      assertEquals(DateUtil.diffDays(createDate(2008, 2, 1), createDate(2008, 3, 2), DayCountBasis.EUR_30_360), 31);
  }

  @Test
  public void testDiffDays30NASDShouldReturn30DaysPerMonthPDEXSAS() {
      assertEquals(DateUtil.diffDays(createDate(2008, 1, 1), createDate(2008, 2, 1), DayCountBasis.NASD_30_360), 30);
      assertEquals(DateUtil.diffDays(createDate(2008, 1, 1), createDate(2008, 2, 2), DayCountBasis.NASD_30_360), 31);
      
      assertEquals(DateUtil.diffDays(createDate(2008, 2, 1), createDate(2008, 3, 1), DayCountBasis.NASD_30_360), 30);
      assertEquals(DateUtil.diffDays(createDate(2008, 2, 1), createDate(2008, 3, 2), DayCountBasis.NASD_30_360), 31);
  }
  
  @Test
  public void testAddDaysActual() {
      Date ref = createDate(2012, 1, 1);
      assertEquals(DateUtil.addDaysActual(ref, 1), createDate(2012,1,2));
      assertEquals(DateUtil.addDaysActual(ref, 2), createDate(2012,1,3));
      assertEquals(DateUtil.addDaysActual(ref, 3), createDate(2012,1,4));
      assertEquals(DateUtil.addDaysActual(ref, 5), createDate(2012,1,6));
      assertEquals(DateUtil.addDaysActual(ref, 8), createDate(2012,1,9));
      assertEquals(DateUtil.addDaysActual(ref, 13), createDate(2012,1,14));
      assertEquals(DateUtil.addDaysActual(ref, 21), createDate(2012,1,22));
      assertEquals(DateUtil.addDaysActual(ref, 34), createDate(2012,2,4));
      assertEquals(DateUtil.addDaysActual(ref, 55), createDate(2012,2,25));
      assertEquals(DateUtil.addDaysActual(ref, 89), createDate(2012,3,30));
      assertEquals(DateUtil.addDaysActual(ref, 144), createDate(2012,5,24));
      assertEquals(DateUtil.addDaysActual(ref, 233), createDate(2012,8,21));
      assertEquals(DateUtil.addDaysActual(ref, 377), createDate(2013,1,12));
      assertEquals(DateUtil.addDaysActual(ref, 610), createDate(2013,9,2));
      assertEquals(DateUtil.addDaysActual(ref, 987), createDate(2014,9,14));
      assertEquals(DateUtil.addDaysActual(ref, 1597), createDate(2016,5,16));
  }
}
