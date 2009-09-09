package org.hisp.dhis.vn.imports.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.vn.imports.ReportItemValue;
import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.action.ActionSupport;
import org.hisp.dhis.vn.report.state.ReportLocationManager;
import org.hisp.dhis.vn.report.utils.ExcelUtils;

import java.io.File;
import java.util.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ViewDataFromExcelFileAction
    extends ActionSupport
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    private ReportExcelService reportExcelService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String fileName;

    private Integer reportId;

    private List<ReportItemValue> values;

    private ReportExcelInterface report;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    public ReportExcelInterface getReport()
    {
        return report;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public List<ReportItemValue> getValues()
    {
        return values;
    }

    public String execute()
        throws Exception
    {
        this.report = reportExcelService.getReport( reportId );
        
        System.out.println("Sheet no: " + 1);

        this.values = new ArrayList<ReportItemValue>();
        
        System.out.println("Sheet no: " + 2);

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        
        System.out.println("Sheet no: " + 3);

        File excel = new File( reportLocationManager.getDirectory( organisationUnit ), fileName );
         
        Workbook templateWorkbook = Workbook.getWorkbook( excel );
        
        System.out.println("Sheet no: " + 5);

        for ( Integer sheetNo : reportExcelService.getSheets( reportId ) )
        {
            
            Sheet sheet = templateWorkbook.getSheet( sheetNo );

            Collection<ReportItem> reportItems = reportExcelService.getReportItem( sheetNo, reportId );

            for ( ReportItem item : reportItems )
            {
                Cell cell = ExcelUtils.getValue( item.getRow(), item.getColumn(), sheet );

                values.add( new ReportItemValue( item, cell.getContents() ) );
            }

        }

        return SUCCESS;
    }
}
