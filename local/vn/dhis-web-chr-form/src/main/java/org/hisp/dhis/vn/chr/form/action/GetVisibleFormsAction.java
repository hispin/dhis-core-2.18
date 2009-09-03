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

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Collection<Form> visibleforms;

    public Collection<Form> getVisibleforms()
    {
        return visibleforms;
    }

    private User curUser;

    public User getCurUser()
    {
        return curUser;
    }

    public void setCurUser( User curUser )
    {
        this.curUser = curUser;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
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
