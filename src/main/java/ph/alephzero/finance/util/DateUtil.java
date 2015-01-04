package ph.alephzero.finance.util;

import java.util.Calendar;
import java.util.Date;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.Message;
/**
 * Date utilities. The code is hideous, but we got to do what we got to do.
 * 
 * We could have gotten away with a lot of things by using JodaTime, but the goal is 
 * to have no dependency.
 *  
 * @author jon
 *
 */
public final class DateUtil {	
	/**
	 * Utility class for date arithmetic.
	 * 
	 * @author jon
	 *
	 */
	public static class DateTriple {
		private int year;
		private int month;
		private int day;
		private Date date;		
		
		public DateTriple(Date date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			this.date = date;
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH) - Calendar.JANUARY + 1;  // normalize, January==1
			day = cal.get(Calendar.DAY_OF_MONTH);						
		}

		public DateTriple(int year, int month, int day) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, Calendar.JANUARY + (month - 1));
			cal.set(Calendar.DAY_OF_MONTH, day);
			//cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			date = cal.getTime();
			this.year = year;
			this.month = month;
			this.day = day;
		}
		
		public Date getDate() {
			return date;
		}
		
		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		public int getDay() {
			return day;
		}
		
		public int getJulianDate() {
			return day - 32075 + (1461 * (year + 4800 + (month - 14)/12)) / 4 
					+ (367 * (month - 2 - ((month - 14)/12)*12)) / 12
					- (3 * ((year + 4900 + (month - 14)/12)/100)) / 4;
		}
	}

	/**
	 * NASD approach, follow algorithm in TIPS.
	 * 
	 * @param date1
	 * @param date2
	 * @param daysPerYear
	 * @return
	 */
	public static int diffDays30NASD(Date date1, Date date2, int daysPerYear) {
		int sign = 1;		
		
		// if date1 > date2, swap values
		if (date1.compareTo(date2) > 0) {
			sign = -1;
			Date tmp = date1;
			date1 = date2;
			date2 = tmp;
		}
		
		int year1, month1, day1, lastDayOfMonth1;
		int year2, month2, day2, lastDayOfMonth2;
		DateTriple dt;
		
		dt = new DateTriple(date1);
		year1 = dt.getYear();
		month1 = dt.getMonth();
		day1 = dt.getDay();
		lastDayOfMonth1 = lastDayOfMonth(year1, month1);
		
		dt = new DateTriple(date2);
		year2 = dt.getYear();
		month2 = dt.getMonth();
		day2 = dt.getDay();
		lastDayOfMonth2 = lastDayOfMonth(year2, month2);
		
		// follow algorithm from TIPS
		if ((day2 == lastDayOfMonth2 && month2 == 2) && (day1 == lastDayOfMonth1 && month1 == 2)) {
			day2 = 30;
		} 
		if (day1 == lastDayOfMonth1 && month1 == 2) {
			day1 = 30;
		} 
		if (day2 == 31 && (day1 == 30 || day1 == 31)) {
			day2 = 30;
		} 
		if (day1 == 31) {
			day1 = 30;
		}				
		
		return sign * ((year2 - year1) * daysPerYear) + ((month2 - month1) * 30) + (day2 - day1);
	}
	
	public static int diffDays30European(Date date1, Date date2, int daysPerYear) {
	    DateTriple dt1 = new DateTriple(date1);
	    DateTriple dt2 = new DateTriple(date2);
	    int d1 = dt1.getDay();
	    int d2 = dt2.getDay();
	    
	    if (d1 == 31) d1 = 30;
	    if (d2 == 31) d2 = 30;
	    
	    return 360 * (dt2.getYear() - dt1.getYear()) +
	            30 * (dt2.getMonth() - dt1.getMonth()) +
	            (d2 - d1);	    		
	}
	
	/**
	 * Actual days between dates. Convert first to Julian date (number) then subtract.
	 * Used formula from TIPS.
	 * 
	 * TODO: what if more than 1 year for ACT/360, ACT/365?
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int diffDaysActual(Date date1, Date date2) {
		int sign = 1;		
		
		// if date1 > date2, swap values
		if (date1.compareTo(date2) > 0) {
			sign = -1;
			Date tmp = date1;
			date1 = date2;
			date2 = tmp;
		}
		
		int julian1 = new DateTriple(date1).getJulianDate();
		int julian2 = new DateTriple(date2).getJulianDate();		
				
		return sign * (julian2 - julian1);
	}
	
	public static int diffDays(Date date1, Date date2, DayCountBasis basis) {
		int diff = Integer.MIN_VALUE;
		switch (basis) {
		case NASD_30_360:
			diff = diffDays30NASD(date1, date2, basis.getDaysPerYear());
			break;
		case EUR_30_360:
			diff = diffDays30European(date1, date2, basis.getDaysPerYear());
			break;
		case ACT_360:
		case ACT_365:
		case ACT_ACT:
			diff = diffDaysActual(date1, date2);
		}
		return diff;
	}
	
	public static Date addDays30NASD(Date date, int days) {
		return null;
	}
	
	public static Date addDays30European(Date date, int days) {
		return null;
	}
	
	public static Date addDaysActual(Date date, int days) {	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.DATE, days);
	    
		return cal.getTime();
	}
	
	public static Date addDays(Date date, int days, DayCountBasis basis) {
		Date date2 = null;
		switch(basis) {
		case NASD_30_360:
			date2 = addDays30NASD(date, days);
			break;
		case EUR_30_360:
			date2 =addDays30European(date, days);
			break;
		case ACT_360:
		case ACT_365:
		case ACT_ACT:
			date2 = addDaysActual(date, days);
		}
		return date2;
	}
	
	public static Date addMonths30NASD(Date date, int months) {
	    DateTriple dt = new DateTriple(date);
	    int year = dt.getYear();
	    int month = dt.getMonth();
	    int day = dt.getDay();	    
	    	    
	    month += months;
	    if (month > 12) {
	        int nyears = (month / 12);
	        if (month % 12 == 0) nyears -= 1;  // month==12 (December) is ok
	        year += nyears;
	        month -= 12 * nyears;
	    } else if (month < 1) {
	        int nyears = (-1*month/12) + 1;
	        year -= nyears;
	        month += 12 * nyears;
	    }
	    
	    int lastDay2 = lastDayOfMonth(year, month);
	    
	    if (day > lastDay2) {
	        day = lastDay2;
	    }

		return new DateTriple(year, month, day).getDate();
	}
	
	public static Date addMonths30European(Date date, int months) {
		return addMonths30NASD(date, months);
	}
	
	public static Date addMonthsActual(Date date, int months) {
		return addMonths30NASD(date, months);
	}
	
	public static Date addMonths(Date date, int months, DayCountBasis basis) {
		Date date2 = null;
		switch(basis) {
		case NASD_30_360:
			date2 = addMonths30NASD(date, months);
			break;
		case EUR_30_360:
			date2 = addMonths30European(date, months);
			break;
		case ACT_360:
		case ACT_365:
		case ACT_ACT:
			date2 = addMonthsActual(date, months);			
		}
		return date2;
	}
	
	public static boolean isWeekend(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
	}		
	
	public static Date previousWorkingDate(Date date) {
		Calendar cal = Calendar.getInstance();
		while (isWeekend(date)) {
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			date = cal.getTime();
		}
		return date;
	}
	
	public static Date nextWorkingDate(Date date) {
		Calendar cal = Calendar.getInstance();
		while (isWeekend(date)) {
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			date = cal.getTime();
		}
		return date; 
	}
	
	public static boolean isSameMonth(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
	}
	
	public static int lastDayOfMonth(int year, int month) {
		switch (month) {
		case 2:
			return (year % 4 == 0) ? 29 : 28;			
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;			
		default:
			return 30;
		}
	}
	
	public static int daysOfYear(DayCountBasis basis, int year) {
	    int ndays = basis.getDaysPerYear();
	    if (ndays < 0) {
	        ndays = (year % 4 == 0) ? 366 : 365; 
	    }
	    return ndays;
	}
	
	public static int daysOfYear(DayCountBasis basis, Date refDate) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(refDate);
	    return daysOfYear(basis, cal.get(Calendar.YEAR));
	}
	
	public static Date createDate(int year, int month, int day) {
		return new DateTriple(year, month, day).getDate();
	}
	
	/**
	 * Array version of {@link #createDate(int, int, int)}, for rJava
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date[] createDate(int[] year, int[] month, int[] day) {
	    int len = year.length;
	    
	    if (len != month.length || len != day.length) {
	        throw new IllegalArgumentException(Message.ERR_ARRAY_ARG_DIFF_LENGTH);
	    }
	    
	    Date[] dates = new Date[len];
	    
	    for (int i = 0; i < len; i++) {
	        dates[i] = createDate(year[i], month[i], day[i]);
	    }
	    
	    return dates;
	}
	
	public static Date normalize(Date date) {
	    return new DateTriple(date).getDate(); 
	}
	
	public static boolean isLastDayOfMonth(Date date) {
	    DateTriple dt = new DateTriple(date);
	    boolean isLastDay = false;
	    
	    switch (dt.month) {
	    case 1:
	    case 3:
	    case 5:
	    case 7:
	    case 8:
	    case 10:
	    case 12:
	        isLastDay = dt.day == 31; break;
	    case 4:
	    case 6:
	    case 9:
	    case 11:
	        isLastDay = dt.day == 30; break;
	    case 2:
	        isLastDay = ((dt.year % 4 == 0) && (dt.day == 29)) || (dt.day == 28);
	    }
	    
	    return isLastDay;	    
	}
	
	public static Date lastDayOfMonth(Date date) {
	    DateTriple dt = new DateTriple(date);
	    int lastDay = 0;
	    
	    switch (dt.month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            lastDay = 31; break;
        case 4:
        case 6:
        case 9:
        case 11:
            lastDay = 30; break;
        case 2:
            lastDay = (dt.year % 4 == 0) ? 29 : 28;
        }
	    
	    return createDate(dt.year, dt.month, lastDay);
	}
	
	public static boolean isMonth(Date date, int month) {
	    DateTriple dt = new DateTriple(date);
	    return dt.month == month;
	}
	
	public static int getDay(Date date) {
	    return new DateTriple(date).day;
	}
	
	public static int adjustEndOfMonthCount(Date date1, Date date2, DayCountBasis basis, int frequency) {
	    // TODO
	    return 0;
	}
}
