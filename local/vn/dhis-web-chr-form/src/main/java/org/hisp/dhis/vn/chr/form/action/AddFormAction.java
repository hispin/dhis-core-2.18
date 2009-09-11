package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class AddFormAction
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

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
    {

        try
        {
            Form form = new Form();

            form.setName( CodecUtils.unescape( name ) );

            form.setLabel( CodecUtils.unescape( label ) );

            form.setNoColumn( noColumn );

            form.setNoColumnLink( noColumnLink );

            form.setIcon( icon );

            form.setVisible( visible );

            form.setCreated( false );

            formService.addForm( form );

            message = i18n.getString( "success" );

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            message = i18n.getString( "add" ) + " " + i18n.getString( "error" );
            
            ex.printStackTrace();
        }
        return ERROR;
    }
}
