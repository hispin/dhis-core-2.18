package org.hisp.dhis.vn.chr.egroup.action;

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.EgroupService;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class AddEgroupAction
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

    private EgroupService egroupService;

    public void setEgroupService( EgroupService egroupService )
    {
        this.egroupService = egroupService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer formID;

    public void setFormID( Integer formID )
    {
        this.formID = formID;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private Integer sortOrder;

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        try
        {
            Egroup egroup = new Egroup();

            egroup.setName( CodecUtils.unescape( name ) );

            egroup.setSortOrder( sortOrder.intValue() );

            egroup.setForm( formService.getForm( formID.intValue() ) );

            egroupService.addEgroup( egroup );

            message = i18n.getString( "success" );

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            message = i18n.getString( "error" );

            ex.printStackTrace();

        }

        return ERROR;
    }

}
