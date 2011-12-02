package org.hisp.dhis.organisationunit;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "organisationUnitGroups" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class OrganisationUnitGroups
{
    private List<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>();

    public OrganisationUnitGroups()
    {

    }

    @XmlElement( name = "organisationUnitGroup" )
    @JsonProperty( value = "organisationUnitGroups" )
    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( List<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }
}
