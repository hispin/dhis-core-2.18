package org.hisp.dhis.reports.upward.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GenerateUpwardReportAnalyserFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    /*
     * private ReportService reportService;
     * 
     * public void setReportService( ReportService reportService ) {
     * this.reportService = reportService; }
     */
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    private final int ALL = 0;

    public int getALL()
    {
        return ALL;
    }

    // private String raFolderName;

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private Collection<OrganisationUnit> organisationUnits;

    public Collection<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    private Collection<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        // raFolderName = reportService.getRAFolderName();

        reportTypeName = ReportType.RT_GOI;

        /* Period Info */
        periodTypes = periodService.getAllPeriodTypes();

        Iterator<PeriodType> alldeIterator = periodTypes.iterator();
        while ( alldeIterator.hasNext() )
        {
            PeriodType type = alldeIterator.next();
            if ( type.getName().equalsIgnoreCase( "Monthly" ) || type.getName().equalsIgnoreCase( "quarterly" )
                || type.getName().equalsIgnoreCase( "yearly" ) )
            {
                periods.addAll( periodService.getPeriodsByPeriodType( type ) );
            }
            else
            {
                alldeIterator.remove();
            }
        }

        return SUCCESS;
    }
}
