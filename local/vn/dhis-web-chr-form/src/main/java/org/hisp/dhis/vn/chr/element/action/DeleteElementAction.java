package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class DeleteElementAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependency
    // -----------------------------------------------------------------------------------------------

    private ElementService elementService;

    public void setElementService( ElementService elementService )
    {
        this.elementService = elementService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
    {
        try
        {
            elementService.deleteElement( id.intValue() );

            message = i18n.getString( "success" );

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            message = i18n.getString( "delete" ) + " " + i18n.getString( "error" );
        }
        return ERROR;
    }

}
