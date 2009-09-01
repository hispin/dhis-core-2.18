package org.hisp.dhis.vn.chr.formreport.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.FormReport;
import org.hisp.dhis.vn.chr.FormReportService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class GetFormReportAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------
    private FormReportService formReportService;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------
    private Integer id;

    private FormReport formReport;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
    }

    public FormReport getFormReport()
    {
        return formReport;
    }

    public void setFormReportService( FormReportService formReportService )
    {
        this.formReportService = formReportService;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement : process Select SQL
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        try
        {

            formReport = formReportService.getFormReport( id.intValue() );

            return SUCCESS;

        }
        catch ( Exception ex )
        {

            ex.printStackTrace();

            message = i18n.getString( "get_data" ) + " " + i18n.getString( "error" );
        }

        return ERROR;
    }
}
