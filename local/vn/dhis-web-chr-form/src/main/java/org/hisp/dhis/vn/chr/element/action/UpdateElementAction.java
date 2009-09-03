package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class UpdateElementAction
    extends ActionSupport
{
    // -----------------------------------------------------------------------------------------------
    // Dependency
    // -----------------------------------------------------------------------------------------------

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

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

    private int formID;

    public void setFormID( int formID )
    {
        this.formID = formID;
    }

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

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String controlType;

    public void setControlType( String controlType )
    {
        this.controlType = controlType;
    }

    private String initialValue;

    public void setInitialValue( String initialValue )
    {
        this.initialValue = initialValue;
    }

    private int formLink;

    public void setFormLink( int formLink )
    {
        this.formLink = formLink;
    }

    private boolean required;

    public void setRequired( boolean required )
    {
        this.required = required;
    }

    private int sortOrder;

    public void setSortOrder( int sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()

    {
        try
        {
            Element element = elementService.getElement( id.intValue() );

            element.setName( CodecUtils.unescape( name ).toLowerCase() );

            element.setLabel( CodecUtils.unescape( label ) );

            element.setType( type );

            element.setControlType( controlType );

            element.setInitialValue( initialValue );

            if ( formLink != 0 )
            {

                element.setFormLink( formService.getForm( formLink ) );

            }
            else
            {
                element.setFormLink( null );

            }

            element.setRequired( required );

            element.setSortOrder( sortOrder );

            elementService.updateElement( element );

            element.setForm( formService.getForm( formID ) );

            message = i18n.getString( "success" );

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            message = i18n.getString( "update" ) + " " + i18n.getString( "error" );
        }
        return ERROR;
    }

}
