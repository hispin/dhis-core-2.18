package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class ValidateObjectAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormService formService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    // Form ID
    private Integer formId;

    // Object's data
    private String[] data;

    // error string
    private String message;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public Integer getFormId()
    {
        return formId;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    public String[] getData()
    {
        return data;
    }

    public void setData( String[] data )
    {
        this.data = data;
    }

    // -------------------------------------------
    // Implement
    // -------------------------------------------

    public String execute()
        throws Exception
    {

        message = "";

        Form form = formService.getForm( formId.intValue() );

        Collection<Egroup> egroups = form.getEgroups();

        // Position of element, which has not-null property to be "yes"
        int pos = 0;

        for ( Egroup egroup : egroups )
        {

            for ( Element element : egroup.getElements() )
            {

                if ( element.isRequired() )
                {

                    if ( data[pos].length() == 0 )
                    {

                        message += " - " + element.getLabel() + "<br>";

                    }// end not null

                }// end if

            }// end for element

        }// end for egroup

        // System.out.print("\n\n\n message : " + message);

        if ( message.length() > 0 )
        {
            message = i18n.getString( "elements" ) + " : <br>" + message + i18n.getString( "not_null" );
            return ERROR;
        }
        return SUCCESS;
    }

}
