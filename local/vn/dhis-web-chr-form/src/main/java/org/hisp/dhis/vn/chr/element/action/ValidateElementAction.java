package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.form.action.ActionSupport;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ValidateElementAction
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

    private String label;

    public void setLabel( String label )
    {
        this.label = label;
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
            message = i18n.getString( "name_is_null" );
            return ERROR;
        }

        if ( label == null || label.trim().length() == 0 )
        {
            message = i18n.getString( "label_is_null" );
            return ERROR;
        }

        if ( sortOrder == 0 )
        {
            message = i18n.getString( "sortOrder_is_null" );
            return ERROR;
        }

        return SUCCESS;
    }
}
