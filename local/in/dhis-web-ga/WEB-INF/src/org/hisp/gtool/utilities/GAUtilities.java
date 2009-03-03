package org.hisp.gtool.utilities;

public class GAUtilities {

	/*
	 * To get the Date Format as Jan-2007 From 2007-01-01 Format
	 */
	public String getMonYearFormat(String strDate)
	{		
		String monthNames[] = 
				{
					"","Jan","Feb","Mar","Apr","May","Jun",
					"Jul","Aug","Sep","Oct","Nov","Dec"
				};
		String partsOfStartingDate[] = strDate.split("-");		
		int selectedMonth = 0;
		
		try
		{
			selectedMonth = Integer.parseInt(partsOfStartingDate[1]);
		}
		catch(Exception e)
		{
			selectedMonth = 0;
		}		
		String monYearFormat = monthNames[selectedMonth]+"-"+partsOfStartingDate[0];		
		return monYearFormat;		
	}// getMonYearFormat end

	/*
	 * Returns the concatinated String For Individual String Tokens
	 */
	public String getConcatedString(String[] strArray, String concatOpe)
	{
		int count = 0;
		String concatedString = "";
		while(count < strArray.length -1)
		{
			concatedString += strArray[count] + concatOpe;
			count++;
		}// while loop end
		concatedString +=  strArray[count];
		return concatedString;		
	}// getConcatedString end
	
}// class end
