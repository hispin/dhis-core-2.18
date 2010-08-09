package org.hisp.dhis.patient.api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hisp.dhis.organisationunit.OrganisationUnit;

@XmlRootElement(name="orgUnits")
public class OrgUnits {

    private List<OrgUnit> orgUnitList;

    @XmlElement(name="orgUnit")
    public List<OrgUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    public void setOrgUnitList( List<OrgUnit> orgUnitList )
    {
        this.orgUnitList = orgUnitList;
    }

    public static OrgUnits create( Collection<OrganisationUnit> units, UriInfo uriInfo )
    {
        OrgUnits o = new OrgUnits();
        
        o.orgUnitList = new ArrayList<OrgUnit>();
        for ( OrganisationUnit unit : units )
        {
            o.orgUnitList.add( OrgUnit.create( unit, uriInfo ) );
        }
        
        return o;
    }

}
