package org.hisp.dhis.reports;

import java.util.ArrayList;
import java.util.List;

public class ReportModel 
{

    public final static String RM_STATIC = "static";
	
    public final static String RM_DYNAMIC_WITH_ROOT = "dynamicwithroot";
	
    public final static String RM_DYNAMIC_WITHOUT_ROOT = "dynamicwithoutroot";
	
    public static List<String> getReportModels()
    {
        List<String> reportModels = new ArrayList<String>();

        reportModels.add( RM_STATIC );
	
        reportModels.add( RM_DYNAMIC_WITH_ROOT );

        reportModels.add( RM_DYNAMIC_WITHOUT_ROOT );
		
        return reportModels;		
    }
	
}
