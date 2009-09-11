package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class GetFormByName
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

    private Form form;

    public Form getForm()
    {
        return form;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        form = formService.getFormByName( name );

        return SUCCESS;
    }

}
