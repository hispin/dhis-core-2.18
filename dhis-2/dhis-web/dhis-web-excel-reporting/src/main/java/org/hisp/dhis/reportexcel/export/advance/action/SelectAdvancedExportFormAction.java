package org.hisp.dhis.reportexcel.export.advance.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitGroupNameComparator;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.export.action.SelectionManager;
import org.hisp.dhis.reportexcel.utils.DateUtils;

import com.opensymphony.xwork2.Action;

public class SelectAdvancedExportFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    private PeriodService periodService;

    private SelectionManager selectionManager;
    
    private ReportExcelService reportService;
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>();

    private List<Period> periods;

    private List<String> reportGroups;
    
    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }
    
    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    public List<String> getReportGroups()
    {
        return reportGroups;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
        throws Exception
    {
        // Report groups list
        reportGroups = new ArrayList<String>( reportService.getReportExcelGroups() );
        
        // Periods list
        PeriodType periodType = periodService.getPeriodTypeByClass( MonthlyPeriodType.class );

        Date firstDateOfThisYear = DateUtils.getFirstDayOfYear( DateUtils.getCurrentYear() );

        Date endDateOfThisMonth = DateUtils.getEndDate( DateUtils.getCurrentMonth(), DateUtils.getCurrentYear() );

        periods = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( periodType,
            firstDateOfThisYear, endDateOfThisMonth ) );

        Collections.sort( periods, new PeriodComparator() );

        selectionManager.setSeletedYear( DateUtils.getCurrentYear() );

        // Organisation groups list
        organisationUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService
            .getAllOrganisationUnitGroups() );

        Collections.sort( organisationUnitGroups, new OrganisationUnitGroupNameComparator() );

        return SUCCESS;
    }

}
