package org.hisp.dhis.vn.imports.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.action.ActionSupport;
import org.hisp.dhis.vn.report.utils.ExcelUtils;

public class ImportDataAction
    extends ActionSupport
{
    // --------------------------------------------------------------------
    // Dependencies
    // --------------------------------------------------------------------

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private ReportExcelService reportExcelService;

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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

    private Integer periodId;

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    // --------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // template report excel
        ReportExcelInterface report = reportExcelService.getReport( reportId );

        // organisation unit
        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        // file saves values
        File upload = new File( uploadFileName );
        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale( new Locale( "en", "EN" ) );
        Workbook templateWorkbook = Workbook.getWorkbook( upload, ws );

        // get reportItems of the template report
        Collection<ReportItem> reportItems = report.getReportItems();

        Sheet sheet = templateWorkbook.getSheet( 0 );

        // get period
        Period period = periodService.getPeriod( periodId.intValue() );

        for ( ReportItem reportItem : reportItems )
        {
            if ( reportItem.getItemType().equals( ReportItem.TYPE.DATAELEMENT ) )
            {

                String value = ExcelUtils.readValue( reportItem.getRow(), reportItem.getColumn(), sheet );

                // Get expression of the reportItem
                Operand operand = expressionService.getOperandsInExpression( reportItem.getExpression() ).iterator()
                    .next();

                // dateelement of the reportItem
                DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );

                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService
                    .getDataElementCategoryOptionCombo( operand.getOptionComboId() );

                // logged username
                String storedBy = currentUserService.getCurrentUsername();

                DataValue dataValue = dataValueService
                    .getDataValue( organisationUnit, dataElement, period, optionCombo );

                // if dataValue is not exist, that means data not input
                // add value into database
                if ( dataValue == null )
                {
                    dataValue = new DataValue( dataElement, period, organisationUnit, value + "", storedBy, new Date(),
                        null, optionCombo );
                    dataValueService.addDataValue( dataValue );
                }
                // if dataValue is exist, update new value
                else
                {
                    dataValue.setValue( value + "" );
                    dataValue.setTimestamp( new Date() );
                    dataValue.setStoredBy( storedBy );

                    dataValueService.updateDataValue( dataValue );

                }// end if
            }
        }

        message = i18n.getString( "success" );

        return SUCCESS;
    }

}