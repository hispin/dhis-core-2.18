package org.hisp.dhis.reports;

import java.util.ArrayList;
import java.util.List;

public class ReportType
{
    public final static String RT_ROUTINE = "Routine";

    public final static String RT_LINELIST = "Linelisting";

    public static List<String> getReportTypes()
    {
        List<String> reportTypes = new ArrayList<String>();

        reportTypes.add( RT_ROUTINE );

        reportTypes.add( RT_LINELIST );

        return reportTypes;
    }
}
