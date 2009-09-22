package org.hisp.dhis.mapping.action;

import org.hisp.dhis.mapping.MapOrganisationUnitRelation;
import org.hisp.dhis.mapping.MappingService;

import com.opensymphony.xwork2.Action;

public class GetMapOrganisationUnitRelationByFeatureIdAction
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

    private String featureId;

    public void setFeatureId( String featureId )
    {
        this.featureId = featureId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private MapOrganisationUnitRelation object;

    public MapOrganisationUnitRelation getObject()
    {
        return object;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        object = mappingService.getMapOrganisationUnitRelationByFeatureId( featureId );
        
        return SUCCESS;
    }
}
