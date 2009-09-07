package org.hisp.dhis.reports;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

public interface ReportService 
{
    String ID = ReportService.class.getName();
	
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

    // -------------------------------------------------------------------------
    // Report_in Design
    // -------------------------------------------------------------------------

    Collection<Report_inDesign> getReportDesign( Report_in report);
    
    // -------------------------------------------------------------------------
    // 
    // -------------------------------------------------------------------------
    
    public List<Calendar> getStartingEndingPeriods( String deType, Date startDate, Date endDate );
    
    public String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit, String aggCB );
}
