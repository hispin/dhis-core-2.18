package org.hisp.dhis.vn.chr.form.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.statement.FormStatement;

import com.opensymphony.xwork2.Action;

public class GetInformationForSystemAction
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private UserStore userStore;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Collection<Form> visibleforms;

    public static int curUserid;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    public User getCurUser()
    {
        return curUser;
    }

    public void setCurUser( User curUser )
    {
        this.curUser = curUser;
    }

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    public Collection<Form> getVisibleforms()
    {
        return visibleforms;
    }

    public void setVisibleforms( Collection<Form> visibleforms )
    {
        this.visibleforms = visibleforms;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // Get All user can process data into database
        Collection<User> users = new ArrayList<User>();

        curUserid = curUser.getId();

        Collection<OrganisationUnit> orgUnits = curUser.getOrganisationUnits();

        for ( OrganisationUnit orgUnit : orgUnits )
        {

            getUsers( orgUnit, users );
        }// end for

        FormStatement.USERS = users;

        return SUCCESS;
    }

    // Get All user can process data into database
    User curUser = null;

    // Get All users belong to the curUser
    private void getUsers( OrganisationUnit orgUnit, Collection<User> users )
    {

        users.addAll( userStore.getUsersByOrganisationUnit( orgUnit ) );

        Collection<OrganisationUnit> orgUnits = orgUnit.getChildren();

        if ( orgUnits != null )
        {

            for ( OrganisationUnit org : orgUnits )
            {

                getUsers( org, users );
            }
        }
    }

}
