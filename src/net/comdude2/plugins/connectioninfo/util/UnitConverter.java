/*
ConnectionInfo - A network monitoring plugin for Minecraft
Copyright (C) 2015  comdude2 (Matt Armer)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Contact: admin@mcviral.net
*/

package net.comdude2.plugins.connectioninfo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UnitConverter {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static SimpleDateFormat getSDF(){
		return sdf;
	}
	
	public static String getCurrentDateToString(){
		return toStringDateFormat(new Date().getTime());
	}
	
	public static long getCurrentTimestamp(){
		return new Date().getTime();
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
	
	//THIS IS WRONG i know this isn't the best but it will do for now.
	public static String getDateDiff(Date newer, Date older){
		try{
			String snew = sdf.format(newer);
			String sold = sdf.format(older);
			int years = ((Integer.parseInt(snew.substring(0,4)) - (Integer.parseInt(sold.substring(0, 4)))));
			int months = ((Integer.parseInt(snew.substring(5,7)) - (Integer.parseInt(sold.substring(5, 7)))));
			int days = ((Integer.parseInt(snew.substring(8,10)) - (Integer.parseInt(sold.substring(8, 10)))));
			int hours = ((Integer.parseInt(snew.substring(11,13)) - (Integer.parseInt(sold.substring(11, 13)))));
			int minutes = ((Integer.parseInt(snew.substring(14,16)) - (Integer.parseInt(sold.substring(14, 16)))));
			int seconds = ((Integer.parseInt(snew.substring(17,19)) - (Integer.parseInt(sold.substring(17, 19)))));
			int milliseconds = ((Integer.parseInt(snew.substring(20,22)) - (Integer.parseInt(sold.substring(20, 22)))));
			
			//Check
			while (years < 0 || months < 0 || days < 0 || hours < 0 || minutes < 0 || seconds < 0 || milliseconds < 0){
				if (years < 0){
					//Erm?
					//Added this to make sure loop doesn't go infinite.
					years = 0;
				}
				if (months < 0){
					years--;
					months = 12 + months;
				}
				if (days < 0){
					months--;
					days = 31 + days;
				}
				if (hours < 0){
					days--;
					hours = 24 + hours;
				}
				if (minutes < 0){
					hours--;
					minutes = 60 + minutes;
				}
				if (seconds < 0){
					minutes--;
					seconds = 60 + seconds;
				}
				if (milliseconds < 0){
					seconds--;
					milliseconds = 1000 + milliseconds;
				}
			}
			return "Years: " + years + " Months: " + months + " Days: " + days + " Hours: " + hours + " Minutes: " + minutes + " Seconds: " + seconds + " Milliseconds: " + milliseconds;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
