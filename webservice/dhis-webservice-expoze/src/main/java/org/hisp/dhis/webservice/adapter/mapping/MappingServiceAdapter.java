package org.hisp.dhis.webservice.adapter.mapping;

import java.util.Collection;

import org.hisp.dhis.mapping.MapOrganisationUnitRelation;

public interface MappingServiceAdapter
{
    int addMap( String mapLayerPath, int organisationUnitId, int organisationUnitLevelId, String uniqueColumn, String nameColumn );
    
    int addMapOrganisationUnitRelation( String mapLayerPath, int organisationUnitId, String featureId );
    
    Collection<MapOrganisationUnitRelation> getAvailableMapOrganisationUnitRelations( String mapLayerPath );
}
