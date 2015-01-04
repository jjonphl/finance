package ph.alephzero.finance.context;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ph.alephzero.finance.util.DateUtil;

/***
 * 
 * @author jon
 *
 */
public class Holidays {
	
	private Set<Date> dates;
	
	public Holidays() {
		new Holidays(new Date[0]);
	}
	
	public Holidays(Date[] dates) {
		this.dates = new HashSet<Date>();
		this.dates.addAll(Arrays.asList(dates));		
	}
	
	public boolean isHoliday(Date date) {
		return dates.contains(date);
	}
	
	public boolean isWeekend(Date date) {
	    return DateUtil.isWeekend(date);
	}
	
	public boolean isNonWorkingDay(Date date) {
	    return isWeekend(date) || isHoliday(date); 
	}
	
	public Date adjustDate(Date date, BusinessDayConvention conv) {
		if (isNonWorkingDay(date)) {
			Date newDate;
			switch(conv) {
			case PREVIOUS: 				
				date = adjustDate(DateUtil.previousWorkingDate(date), conv);
				break;
			case FORWARD:				
				date = adjustDate(DateUtil.nextWorkingDate(date), conv);
				break;
			case PREVIOUS_MODIFIED:
				newDate = DateUtil.previousWorkingDate(date);
				if (!DateUtil.isSameMonth(date, newDate)) {
					date = adjustDate(DateUtil.nextWorkingDate(date), BusinessDayConvention.FORWARD);
				} else {
					date = adjustDate(newDate, conv);
				}
				break;
			case FORWARD_MODIFIED:
				newDate = DateUtil.nextWorkingDate(date);
				if (!DateUtil.isSameMonth(date, newDate)) {
					date = adjustDate(DateUtil.previousWorkingDate(date), BusinessDayConvention.PREVIOUS);
				} else {
					date = adjustDate(newDate, conv);
				}
				break;
			case NONE:				
			}			
		}
		
		return date;
	}
	
}
