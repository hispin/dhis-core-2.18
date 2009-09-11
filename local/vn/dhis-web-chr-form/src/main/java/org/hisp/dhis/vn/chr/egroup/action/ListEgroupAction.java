package org.hisp.dhis.vn.chr.egroup.action;

import java.util.Collection;

import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ListEgroupAction
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

    private Collection<Egroup> egroups;

    public Collection<Egroup> getEgroups()
    {
        return this.egroups;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        form = formService.getForm( formID.intValue() );

        egroups = form.getEgroups();

        return SUCCESS;
    }

}
