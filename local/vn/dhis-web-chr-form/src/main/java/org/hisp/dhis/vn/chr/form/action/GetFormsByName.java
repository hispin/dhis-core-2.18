package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

import com.opensymphony.xwork2.Action;

public class GetFormsByName
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private Collection<Form> forms;

    public Collection<Form> getForms()
    {
        return forms;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        forms = formService.getFormsByName( name );
        //		
        return SUCCESS;
    }

}
