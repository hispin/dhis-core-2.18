package org.hisp.dhis.mapping.action;

import java.util.Collection;

import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MappingService;

import com.opensymphony.xwork.Action;

public class GetMapsByTypeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<Map> object;

    public Collection<Map> getObject()
    {
        return object;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        object = mappingService.getMapsByType( type );
        
        return SUCCESS;
    }
}
