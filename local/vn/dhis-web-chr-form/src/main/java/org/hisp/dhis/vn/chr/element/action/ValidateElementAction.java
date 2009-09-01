package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class ValidateElementAction
    extends ActionSupport
{

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String name;

    private String label;

    private Integer sortOrder;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    // -------------------------------------------
    // Implement
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
