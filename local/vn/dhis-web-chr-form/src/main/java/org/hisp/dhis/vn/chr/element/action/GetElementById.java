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

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    private Element element;

    // -----------------------------------------------------------------------------------------------
    // Getters && Setter
    // -----------------------------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setElementService( ElementService elementService )
    {
        this.elementService = elementService;
    }

    public Element getElement()
    {
        return element;
    }

    public void setElement( Element element )
    {
        this.element = element;
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
