package org.hisp.dhis.patient.api.service.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.api.model.Link;
import org.hisp.dhis.patient.api.model.OrgUnit;
import org.hisp.dhis.patient.api.model.OrgUnits;
import org.hisp.dhis.patient.api.resources.ProgramFormsResource;
import org.hisp.dhis.patient.api.service.MappingFactory;

public class OrgUnitsMapper
    implements BeanMapper<Collection<OrganisationUnit>, OrgUnits>
{

    @Override
    public OrgUnits getModel( Collection<OrganisationUnit> units, MappingFactory mappingFactory, UriInfo uriInfo )
    {
        OrgUnits o = new OrgUnits();
        
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        o.setOrgUnitList( orgUnitList );
        
        for ( OrganisationUnit unit : units )
        {
            orgUnitList.add( create( unit, uriInfo ) );
        }
        
        return o;

    }

    private OrgUnit create(OrganisationUnit unit, UriInfo uriInfo) {
        OrgUnit m = new OrgUnit();
        m.setId(unit.getId());
        m.setName(unit.getShortName());
        m.setProgramFormsLink( new Link(uriInfo.getBaseUriBuilder().fromResource( ProgramFormsResource.class).build( unit.getId()).toString()));
        m.setActivitiesLink( new Link(uriInfo.getBaseUriBuilder().path( "v0.1/orgUnits/{id}/activities/plan/current" ).build( unit.getId() ).toString()));
        
        return m;
    }

}
