package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.EgroupService;
import com.opensymphony.xwork2.Action;

public class GetEgroupById
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private EgroupService egroupService;

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private Integer id;

    private Egroup egroup;

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

    public void setEgroupService( EgroupService egroupService )
    {
        this.egroupService = egroupService;
    }

    public Egroup getEgroup()
    {
        return egroup;
    }

    public void setEgroup( Egroup egroup )
    {
        this.egroup = egroup;
    }

    // -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        egroup = egroupService.getEgroup( id.intValue() );

        return SUCCESS;
    }

}
