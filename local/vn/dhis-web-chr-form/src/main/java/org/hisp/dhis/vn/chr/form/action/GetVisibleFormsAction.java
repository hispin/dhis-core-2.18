package org.hisp.dhis.vn.chr.form.action;

import java.util.Collection;

import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

import com.opensymphony.xwork2.Action;

public class GetVisibleFormsAction
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormService formService;

    private CurrentUserService currentUserService;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Collection<Form> visibleforms;

    private User curUser;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public Collection<Form> getVisibleforms()
    {
        return visibleforms;
    }

    public void setVisibleforms( Collection<Form> visibleforms )
    {
        this.visibleforms = visibleforms;
    }

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    public User getCurUser()
    {
        return curUser;
    }

    public void setCurUser( User curUser )
    {
        this.curUser = curUser;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        visibleforms = formService.getVisibleForms( true );

        Collection<Form> createdForm = formService.getCreatedForms();

        visibleforms.retainAll( createdForm );

        curUser = currentUserService.getCurrentUser();

        return SUCCESS;
    }

}
