package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.EgroupService;
import com.opensymphony.xwork2.Action;


/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class GetEgroupById
    implements Action
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
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

    private Egroup egroup;

    public Egroup getEgroup()
    {
        return egroup;
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
