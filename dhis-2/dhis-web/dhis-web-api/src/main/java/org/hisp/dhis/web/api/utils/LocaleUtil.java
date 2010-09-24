package org.hisp.dhis.web.api.utils;

import java.util.Locale;

public class LocaleUtil {
	
	public static Locale getLocale( String localeString )
	{
		Locale locale;
		try{
			locale = new Locale(localeString.substring(0,localeString.indexOf('-')), localeString.substring(localeString.indexOf('-')+1, localeString.length()));
		}catch(Exception e)
		{
			locale = Locale.UK;			
		}			
		return locale;
	}
}
