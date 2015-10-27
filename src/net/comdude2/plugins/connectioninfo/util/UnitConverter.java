package net.comdude2.plugins.connectioninfo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UnitConverter {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
	
	public String getCurrentDateToString(){
		return toStringDateFormat(new Date().getTime());
	}
	
	public static String toStringDateFormat(long l){
		try{
			Date d = new Date();
			d.setTime(l);
			return sdf.format(d);
		}catch (Exception e){
			return null;
		}
	}
	
	public static long fromStringDateFormat(String date){
		try {
			Date d = sdf.parse(date);
			//Not sure if this is needed - Read the documentation
			if (d != null){
				return d.getTime();
			}
		} catch (ParseException e) {
			return 0L;
		}
		return 0L;
	}
	
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
}
