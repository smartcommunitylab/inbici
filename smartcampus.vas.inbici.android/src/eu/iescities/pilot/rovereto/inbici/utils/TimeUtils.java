package eu.iescities.pilot.rovereto.inbici.utils;

import java.util.Calendar;


public class TimeUtils {
	

	
	public static long getCurrentDateTimeForSearching() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.add(Calendar.DATE, -1);
		return c.getTimeInMillis();
	}

	
	
	
	
	
	
	
}
