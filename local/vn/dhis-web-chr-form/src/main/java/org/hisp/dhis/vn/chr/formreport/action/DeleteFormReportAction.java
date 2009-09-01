package org.hisp.dhis.vn.chr.formreport.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.FormReportService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class DeleteFormReportAction
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

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
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

            formReportService.deleteFormReport( id.intValue() );

            message = i18n.getString( "delete" ) + " " + i18n.getString( "success" );

            return SUCCESS;

        }
        catch ( Exception ex )
        {

            ex.printStackTrace();

            message = i18n.getString( "delete" ) + " " + i18n.getString( "error" );

            message += "<br>" + i18n.getString( "delete_message_error" );
        }

        return ERROR;
    }
}
