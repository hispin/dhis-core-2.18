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

    // private ElementService elementService;

    private FormService formService;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer formID;

    private Form form;

    private Collection<Element> elements;

    private Collection<Form> forms;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    // public void setElementService(ElementService elementService) {
    // this.elementService = elementService;
    // }

    public void setForm( Form form )
    {
        this.form = form;
    }

    public void setElements( Collection<Element> elements )
    {
        this.elements = elements;
    }

    public void setFormID( Integer formID )
    {
        this.formID = formID;
    }

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    public Form getForm()
    {
        return form;
    }

    public Collection<Element> getElements()
    {
        return elements;
    }

    public void setForms( Collection<Form> forms )
    {
        this.forms = forms;
    }

    public Collection<Form> getForms()
    {
        return forms;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
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
