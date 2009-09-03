package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

public class UpdateFormAction
    extends ActionSupport
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private FormService formService;

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String label;

    public void setLabel( String label )
    {
        this.label = label;
    }

    private int noColumnLink;

    public void setNoColumnLink( int noColumnLink )
    {
        this.noColumnLink = noColumnLink;
    }

    private String icon;

    public void setIcon( String icon )
    {
        this.icon = icon;
    }

    private boolean visible;

    public void setVisible( boolean visible )
    {
        this.visible = visible;
    }

    private int noColumn;

    public void setNoColumn( int noColumn )
    {
        this.noColumn = noColumn;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        try
        {
            Form form = formService.getForm( id.intValue() );

            form.setLabel( CodecUtils.unescape( label ) );

            form.setNoColumn( noColumn );

            form.setNoColumnLink( noColumnLink );

            form.setIcon( icon );

            form.setVisible( visible );

            formService.updateForm( form );

            message = i18n.getString( "success" );

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            message = i18n.getString( "update" ) + " " + i18n.getString( "error" );

            ex.printStackTrace();
        }
        return ERROR;
    }
}
