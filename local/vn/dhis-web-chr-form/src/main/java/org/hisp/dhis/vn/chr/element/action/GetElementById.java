package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import com.opensymphony.xwork2.Action;

public class GetElementById
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
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

    private Element element;

    public Element getElement()
    {
        return element;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        element = elementService.getElement( id.intValue() );

        return SUCCESS;
    }

}
