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

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    private String label;

    private int noRow;

    private int noColumn;

    private int noColumnLink;

    private String icon;

    private boolean visible;

    private String attached;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

    public void setFormService( FormService formService )
    {
        this.formService = formService;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public int getNoRow()
    {
        return noRow;
    }

    public void setNoRow( int noRow )
    {
        this.noRow = noRow;
    }

    public int getNoColumn()
    {
        return noColumn;
    }

    public void setNoColumn( int noColumn )
    {
        this.noColumn = noColumn;
    }

    public int getNoColumnLink()
    {
        return noColumnLink;
    }

    public void setNoColumnLink( int noColumnLink )
    {
        this.noColumnLink = noColumnLink;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon( String icon )
    {
        this.icon = icon;
    }

    public boolean getVisible()
    {
        return visible;
    }

    public void setVisible( boolean visible )
    {
        this.visible = visible;
    }

    public String getAttached()
    {
        return attached;
    }

    public void setAttached( String attached )
    {
        this.attached = attached;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Form form = formService.getForm( id );

        form.setLabel( CodecUtils.unescape( label ) );

        form.setNoColumn( noColumn );

        form.setNoColumnLink( noColumnLink );

        form.setIcon( icon );

        form.setVisible( visible );

        formService.updateForm( form );

        message = i18n.getString( "success" );

        return SUCCESS;
    }
}
