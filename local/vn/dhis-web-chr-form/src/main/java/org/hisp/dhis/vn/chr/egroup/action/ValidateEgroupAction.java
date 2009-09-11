package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.form.action.ActionSupport;


/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ValidateEgroupAction
    extends ActionSupport
{

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private Integer sortOrder;

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    // -------------------------------------------
    // Action Implementation
    // -------------------------------------------

    public String execute()
        throws Exception
    {

        if ( name == null || name.trim().length() == 0 )
        {
            message = i18n.getString( "name" ) + " " + i18n.getString( "not_null" );
            return ERROR;
        }

        if ( sortOrder == null  )
        {
            message = i18n.getString( "sortOrder" ) + " " + i18n.getString( "not_null" );
            return ERROR;
        }else if(sortOrder.intValue() == 0){
            message = i18n.getString( "sortOrder" ) + " " + i18n.getString( "not_string" );
            return ERROR;
        }

        return SUCCESS;
    }
}