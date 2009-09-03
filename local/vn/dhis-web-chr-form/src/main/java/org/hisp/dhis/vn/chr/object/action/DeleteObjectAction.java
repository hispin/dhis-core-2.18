package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class DeleteObjectAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormManager formManager;

    public void setFormManager( FormManager formManager )
    {
        this.formManager = formManager;
    }

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer formId;

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()

    {
        try
        {
            Form form = formService.getForm( formId.intValue() );

            formManager.deleteObject( form, id.intValue() );

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
