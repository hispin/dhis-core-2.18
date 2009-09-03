package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.EgroupService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class UpdateEgroupAction
    extends ActionSupport
{
    // -----------------------------------------------------------------------------------------------
    // Dependency
    // -----------------------------------------------------------------------------------------------

    private EgroupService egroupService;

    public void setEgroupService( EgroupService egroupService )
    {
        this.egroupService = egroupService;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private int sortOrder;

    public void setSortOrder( byte sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    // -----------------------------------------------------------------------------------------------
    // Action Implementation
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Egroup egroup = egroupService.getEgroup( id.intValue() );

        egroup.setName( CodecUtils.unescape( name ) );

        egroup.setSortOrder( sortOrder );

        egroupService.updateEgroup( egroup );

        message = i18n.getString( "success" );

        return SUCCESS;
    }

}