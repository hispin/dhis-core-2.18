package org.hisp.dhis.patient.api.resources;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.sun.jersey.api.core.ResourceContext;

public class OrgUnitResource {

    private static final Log LOG = LogFactory.getLog( OrgUnitResource.class );

    @Context ResourceContext rc;
    
    private OrganisationUnit organisationUnit;

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    @Path( "activities" )
    public ActivitiesResource getActivitiesResource() {
        ActivitiesResource subResource = rc.getResource( ActivitiesResource.class );
        subResource.setOrganisationUnit(organisationUnit);
        
        return subResource;
    }

}
