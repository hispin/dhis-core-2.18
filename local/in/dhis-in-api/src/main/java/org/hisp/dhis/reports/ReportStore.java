package org.hisp.dhis.reports;

import java.util.Collection;

import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

public interface ReportStore 
{
	String ID = ReportStore.class.getName();
	
    // -------------------------------------------------------------------------
    // Report_in
    // -------------------------------------------------------------------------

	int addReport( Report_in report );
	
	void updateReport( Report_in report );
	
	void deleteReport( Report_in report );
	
	Report_in getReport( int id );
	
	Report_in getReportByName( String name );
	
	Collection<Report_in> getReportBySource( Source source );
	
	Collection<Report_in> getAllReports();
	
	Collection<Report_in> getReportsByReportType( String reportType );
	
	Collection<Report_in> getReportsByPeriodType( PeriodType periodType );
	
	Collection<Report_in> getReportsByPeriodAndReportType( PeriodType periodType, String reportType );

	Collection<Report_in> getReportsByPeriodSourceAndReportType( PeriodType periodType, Source source, String reportType );
}
