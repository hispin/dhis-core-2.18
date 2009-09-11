package org.hisp.dhis.vn.chr.form.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;
import org.hisp.dhis.vn.chr.statement.FormStatement;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class GetInformationForSystemAction
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    public static int curUserid;

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
        try
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
        catch ( Exception ex )
        {

            ex.printStackTrace();
        }
        return ERROR;
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
