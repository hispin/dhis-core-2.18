/**
 * 
 */
package org.hisp.dhis.web.api.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author abyotag_adm
 *
 */
public class DefaultPeriodService implements IPeriodService {

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private org.hisp.dhis.period.PeriodService periodService;
	
	
	// -------------------------------------------------------------------------
    // PeriodService
    // -------------------------------------------------------------------------
	
	
	public Period getPeriod(String periodName, DataSet dataSet) {
		org.hisp.dhis.period.Period period = null;
		org.hisp.dhis.period.Period persistedPeriod = null;
		if( dataSet.getPeriodType().getName().equals("Daily"))
		{
			String pattern = "yyyy-MM-dd";
		    SimpleDateFormat formatter = new SimpleDateFormat(pattern);		    
		    Date date = new Date();
			
		    try {
				
		    	date = formatter.parse(periodName);				
				DailyPeriodType dailyPeriodType = new DailyPeriodType();
				period = dailyPeriodType.createPeriod(date);
				
			} catch (ParseException e) {				
				e.printStackTrace();
			}					
		}
		else if( dataSet.getPeriodType().getName().equals("Weekly"))
		{			
			try{
				int week = Integer.parseInt( periodName.substring(0,periodName.indexOf('-')) );
				int year = Integer.parseInt( periodName.substring(periodName.indexOf('-') + 1, periodName.length()) );
				
				Calendar cal = Calendar.getInstance();								
				cal.set( Calendar.YEAR, year );
				cal.set( Calendar.WEEK_OF_YEAR, week );	
				cal.setFirstDayOfWeek(Calendar.MONDAY);	
		        
		        WeeklyPeriodType weeklyPeriodType = new WeeklyPeriodType();				
				period = weeklyPeriodType.createPeriod( cal.getTime() );				
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}			
		}
		
		else if( dataSet.getPeriodType().getName().equals("Monthly"))
		{			
			try{
				int month = Integer.parseInt( periodName.substring(0,periodName.indexOf('-')) );
				int year = Integer.parseInt( periodName.substring(periodName.indexOf('-') + 1, periodName.length()) );
				
				Calendar cal = Calendar.getInstance();
				cal.set( Calendar.YEAR, year );
				cal.set( Calendar.MONTH, month );				
				
				MonthlyPeriodType monthlyPeriodType = new MonthlyPeriodType();
				period = monthlyPeriodType.createPeriod( cal.getTime() );			
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}			
		}
		
		else if( dataSet.getPeriodType().getName().equals("Yearly"))
		{
			Calendar cal = Calendar.getInstance();
			cal.set( Calendar.YEAR, Integer.parseInt(periodName) );
			
			YearlyPeriodType yearlyPeriodType = new YearlyPeriodType();
			
			period = yearlyPeriodType.createPeriod( cal.getTime() );			
		}else if(dataSet.getPeriodType().getName().equals("Quarterly")){
			Calendar cal = Calendar.getInstance();
			
			int month = 0;
			if(periodName.substring(0,periodName.indexOf(" ")).equals("Jan")){
				month = 1;
			}else if(periodName.substring(0,periodName.indexOf(" ")).equals("Apr")){
				month = 4;
			}else if(periodName.substring(0,periodName.indexOf(" ")).equals("Jul")){
				month = 6;
			}else if(periodName.substring(0,periodName.indexOf(" ")).equals("Oct")){
				month = 10;
			}
				
			int year = Integer.parseInt(periodName.substring(periodName.lastIndexOf(" ")+1));
			
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.YEAR, year);
			
			QuarterlyPeriodType quarterlyPeriodType = new QuarterlyPeriodType();
			if(month != 0){
				period =  quarterlyPeriodType.createPeriod(cal.getTime());
			}
			
		}
		
		if( period != null )
		{	
			persistedPeriod = periodService.getPeriod( period.getStartDate(), period.getEndDate(), dataSet.getPeriodType() );
			
			if(  persistedPeriod == null )
			{				
				periodService.addPeriod( period );				
				persistedPeriod = periodService.getPeriod( period.getStartDate(), period.getEndDate(), dataSet.getPeriodType() );
			}
		}
		
		return persistedPeriod;	
	}

}
