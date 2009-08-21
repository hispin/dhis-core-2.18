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

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    private String name;

    private int sortOrder;

    // -----------------------------------------------------------------------------------------------
    // Getters && Setters
    // -----------------------------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setSortOrder( byte sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public void setEgroupService( EgroupService egroupService )
    {
        this.egroupService = egroupService;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Egroup egroup = egroupService.getEgroup( id );

        egroup.setName( CodecUtils.unescape( name ) );

        egroup.setSortOrder( sortOrder );

        egroupService.updateEgroup( egroup );

        message = i18n.getString( "success" );

        return SUCCESS;
    }

}
