package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class GetObjectAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormManager formManager;

    private FormService formService;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    // Form ID
    private Integer formId;

    // Object's ID
    private Integer id;

    // Object's data
    private ArrayList data;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    public ArrayList getData()
    {
        return data;
    }

    public void setData( ArrayList data )
    {
        this.data = data;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getFormId()
    {
        return this.formId;
    }

    public void setFormManager( FormManager formManager )
    {
        this.formManager = formManager;
    }

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement : process Select SQL
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        try
        {

            Form form = formService.getForm( formId.intValue() );

            data = formManager.getObject( form, id.intValue() );

            return SUCCESS;

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return ERROR;
    }
}
