package fr.opsycraft.opsyban.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConverter {
	
	private TimeConverter()
	{
		throw new IllegalStateException("Time Converter Class (Utility Class)");
	}
	
	public static long stringTimeToMillis(String timeString)
	{
		char timeChar = timeString.charAt(timeString.length() - 1);
		if(timeChar == 's')
		{
			String seconds = timeString.substring(0, timeString.length() - 1);
			if(Integer.valueOf(seconds) instanceof Integer)
			{
				return (long) Integer.valueOf(seconds) * 1000;
			}
		}
		else if(timeChar == 'm')
		{
			String minutes = timeString.substring(0, timeString.length() - 1);
			if(Integer.valueOf(minutes) instanceof Integer)
			{
				return (long) Integer.valueOf(minutes) * 60 * 1000;
			}
		}
		else if(timeChar == 'h')
		{
			String hours = timeString.substring(0, timeString.length() - 1);
			if(Integer.valueOf(hours) instanceof Integer)
			{
				return (long) Integer.valueOf(hours) * 3600 * 1000;
			}
		}
		else if(timeChar == 'd')
		{
			String days = timeString.substring(0, timeString.length() - 1);
			if(Integer.valueOf(days) instanceof Integer)
			{
				return (long) Integer.valueOf(days) * 3600 * 24 * 1000;
			}
		}
		return 0;
	}
	
	public static String millisToTime(String prefixString, long millis)
	{
	    String iterationString = "";
		return millisToTimeConv(prefixString, iterationString, millis);
	}
	
	public static String millisToTime(long millis)
	{
	    String prefixString = "";
		String iterationString = "";
	    return	millisToTimeConv(prefixString, iterationString, millis);
	}
	
	private static String millisToTimeConv(String prefixString, String iterationString, long millis)
	{
		if(millis/1000 < 60 &&  millis/1000 > 0)
		{
			String seconds = String.valueOf(millis/1000);
			return millisToTimeConv(prefixString, iterationString + seconds + "s ", millis - (millis/1000) * 1000);
		}
		else if(millis/(60 * 1000) < 60 && millis/(60 * 1000) > 0)
		{
			String minutes = String.valueOf(millis/(60 * 1000));
			return millisToTimeConv(prefixString, iterationString + minutes + "m ", millis - (millis/(60 * 1000)) * (60 * 1000));
		}
		else if(millis/(3600 * 1000) < 60 && millis/(3600 * 1000) > 0)
		{
			String hours = String.valueOf(millis/(3600 * 1000));		
			return millisToTimeConv(prefixString, iterationString + hours + "h ", millis - (millis/(3600 * 1000)) * (3600 * 1000));
		}
		else if(millis/(24 * 3600 * 1000) < 365 && millis/(24 * 3600 * 1000) > 0)
		{
			String days = String.valueOf(millis/(24 * 3600 * 1000));
			return millisToTimeConv(prefixString, iterationString + days + "d ", millis - (millis/(24 * 3600 * 1000)) * (24 * 3600 * 1000));
		}
		else if(millis/(365 * 24 * 3600 * 1000) > 0)
		{
			String years = String.valueOf(millis/(365 * 24 * 3600 * 1000));
			return millisToTimeConv(prefixString, iterationString + years + "y ", millis - (millis/(365 * 24 * 3600 * 1000)) * (365 * 24 * 3600 * 1000));
		}
		else
		{
			if(iterationString.isEmpty())
			{
				return prefixString + millis + "ms";
			}
			return prefixString + iterationString;
		}
	}
	
	public static String millisToDate(long millis)
	{
		Date dateFromMillis = new Date(millis);
		DateFormat df = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
		return df.format(dateFromMillis);
	}
}
