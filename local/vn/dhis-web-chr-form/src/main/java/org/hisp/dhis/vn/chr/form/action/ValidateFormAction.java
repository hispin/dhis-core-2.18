package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

public class ValidateFormAction
    extends ActionSupport
{

   
    // -------------------------------------------
    // Input && Output
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

    private int noColumn;

    public void setNoColumn( int noColumn )
    {
        this.noColumn = noColumn;
    }

    // -------------------------------------------
    // Implement
    // -------------------------------------------

    public String execute()
        throws Exception
    {

        if ( name == null || name.trim().length() == 0 )
        {
            message = i18n.getString( "name" ) + " " + i18n.getString( "not_null" );
            return ERROR;
        }

        if ( label == null || label.trim().length() == 0 )
        {
            message = i18n.getString( "label" ) + " " + i18n.getString( "not_null" );
            return ERROR;
        }

        if ( noColumn == 0 )
        {
            message = i18n.getString( "noColumn" ) + " " + i18n.getString( "not_null" );
            return ERROR;
        }

        return SUCCESS;
    }
}
