package org.hisp.dhis.vn.imports.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.action.ActionSupport;

public class GetInformationAction
    extends ActionSupport
{

    // -------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    private ReportExcelService reportExcelService;

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    // -------------------------------------------------------------
    // Input && Ouput
    // -------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    private List<ReportExcelInterface> reportExcels;

    public List<ReportExcelInterface> getReportExcels()
    {
        return reportExcels;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    public void setPeriods( List<Period> periods )
    {
        this.periods = periods;
    }

    private File fileExcel;

    public File getFileExcel()
    {
        return fileExcel;
    }

    public void setFileExcel( File fileExcel )
    {
        this.fileExcel = fileExcel;
    }

    // -------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // //----------------------------------------------------------
        // //get maximum of sheet
        // int max = 1;
        // maxSheet = 1;
        // ----------------------------------------------------------
        // get selected Organisation unit
        organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        System.out.println( "selected orgunit : " + organisationUnit );
        // ----------------------------------------------------------
        // init reportExcels Object
        reportExcels = new ArrayList<ReportExcelInterface>();
        // get reports list for organisation unit
        List<ReportExcelInterface> listReports = new ArrayList<ReportExcelInterface>( reportExcelService.getALLReport() );

        if ( organisationUnit == null )
        {
            reportExcels.addAll( listReports );
        }
        else
        {

            for ( ReportExcelInterface report : listReports )
            {

                Collection<OrganisationUnit> orgUnits = report.getOrganisationAssocitions();
                // check report's organisationUnit belongs to orgUnits
                for ( OrganisationUnit orgUnit : orgUnits )
                {
                    if ( organisationUnit.getId() == orgUnit.getId() )
                    {
                        reportExcels.add( report );

                        break;
                    } // end if
                }// end for orgUnits

            }// end for reports
        } // end else if organisationUnit

        // ----------------------------------------------------------
        // 

        if ( fileExcel != null )
        {
            message = i18n.getString( "upload_file" ) + " " + i18n.getString( "success" ) + " <br>      ' "
                + fileExcel.getName() + " '";
        }
        return SUCCESS;
    }
}
