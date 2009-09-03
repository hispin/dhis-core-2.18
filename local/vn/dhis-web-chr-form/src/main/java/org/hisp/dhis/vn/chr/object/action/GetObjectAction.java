package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

public class GetObjectAction
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

    private ArrayList<String> data;

    public ArrayList<String> getData()
    {
        return data;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Form form = formService.getForm( formId.intValue() );

        data = formManager.getObject( form, id.intValue() );

        return SUCCESS;

    }
}
