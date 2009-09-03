package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

public class CreateTableByFormAction
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
    // Input & Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Form form = formService.getForm( id.intValue() );

        formManager.createTable( form );

        form.setCreated( true );

        formService.updateForm( form );

        message = i18n.getString( "create" ) + " " + i18n.getString( "success" );

        return SUCCESS;
    }

}
