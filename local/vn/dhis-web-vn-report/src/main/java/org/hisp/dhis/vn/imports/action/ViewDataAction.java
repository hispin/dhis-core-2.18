package org.hisp.dhis.vn.imports.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import org.hisp.dhis.vn.imports.ReportItemValue;
import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

public class ViewDataAction
    implements Action
{
    // --------------------------------------------------------------------
    // Dependencies
    // --------------------------------------------------------------------
    private ReportExcelService reportExcelService;

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    // --------------------------------------------------------------------
    // Input && Output
    // --------------------------------------------------------------------

    private Integer reportId;

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    private String uploadFileName;

    public void setUploadFileName( String uploadFileName )
    {
        this.uploadFileName = uploadFileName;
    }

    private List<ReportItemValue> reportItemValues;

    public List<ReportItemValue> getReportItemValues()
    {
        return reportItemValues;
    }

    // --------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------

    public String execute()
    {

        try
        {
            // template report excel
            ReportExcelInterface report = reportExcelService.getReport( reportId );
            // file saves values
            File upload = new File( uploadFileName );
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale( new Locale( "en", "EN" ) );
            Workbook templateWorkbook = Workbook.getWorkbook( upload, ws );

            // get reportItems of the template report
            Collection<ReportItem> reportItems = report.getReportItems();

            Sheet sheet = templateWorkbook.getSheet( 0 );

            // init parametter reportItermValues
            reportItemValues = new ArrayList<ReportItemValue>();

            for ( ReportItem reportItem : reportItems )
            {
                if ( reportItem.getItemType().equals( ReportItem.TYPE.DATAELEMENT ) )
                {
                    String value = ExcelUtils.readValue( reportItem.getRow(), reportItem.getColumn(), sheet );
                    
                    if ( !value.isEmpty() )
                    {
                        ReportItemValue reportItemvalue = new ReportItemValue( reportItem, value );

                        reportItemValues.add( reportItemvalue );
                    } // end if value
                }// end if reportitems
            }// end for

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return ERROR;
    }

}
