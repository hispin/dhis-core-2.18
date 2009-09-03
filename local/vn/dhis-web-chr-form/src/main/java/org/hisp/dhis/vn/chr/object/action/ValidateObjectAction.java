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

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    // Form ID
    private Integer formId;

    public void setFormId( Integer formId )
    {
        this.formId = formId;
    }

    // Object's data
    private String[] data;

    public void setData( String[] data )
    {
        this.data = data;
    }

    // -------------------------------------------
    // Action Implementation
    // -------------------------------------------

    public String execute()
        throws Exception
    {
        message = "";

        Form form = formService.getForm( formId.intValue() );

        Collection<Egroup> egroups = form.getEgroups();

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

        if ( message.length() > 0 )
        {
            message = i18n.getString( "elements" ) + " : <br>" + message + i18n.getString( "not_null" );

            return ERROR;
        }
        return SUCCESS;
    }

}
