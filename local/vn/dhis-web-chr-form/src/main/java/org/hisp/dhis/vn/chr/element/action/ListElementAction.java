package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import org.hisp.dhis.vn.chr.Element; //import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

import com.opensymphony.xwork2.Action;

public class ListElementAction
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

    private Integer formID;

    public void setFormID( Integer formID )
    {
        this.formID = formID;
    }

    private Form form;

    public Form getForm()
    {
        return form;
    }

    private Collection<Element> elements;

    public Collection<Element> getElements()
    {
        return elements;
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

        forms = formService.getAllForms();

        form = formService.getForm( formID.intValue() );

        elements = form.getElements();

        return SUCCESS;
    }
}
