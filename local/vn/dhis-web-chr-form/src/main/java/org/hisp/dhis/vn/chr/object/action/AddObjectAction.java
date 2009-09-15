package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class AddObjectAction
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

    private String[] data;

    public void setData( String[] data )
    {
        this.data = data;
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

            for ( int i = 0; i < data.length; i++ )
            {
                data[i] = CodecUtils.unescape( data[i] );
            }

            formManager.addObject( form, data );

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            message = i18n.getString( "add" ) + " " + i18n.getString( "error" );

            ex.printStackTrace();
        }
        return ERROR;
    }
}
