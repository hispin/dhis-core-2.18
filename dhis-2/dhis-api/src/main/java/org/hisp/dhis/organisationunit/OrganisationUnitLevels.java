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
@XmlRootElement( name = "organisationUnitLevels" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class OrganisationUnitLevels
{
    private List<OrganisationUnitLevel> organisationUnitLevels = new ArrayList<OrganisationUnitLevel>();

    public OrganisationUnitLevels()
    {
    }

    @XmlElement( name = "organisationUnitLevel" )
    @JsonProperty( value = "organisationUnitLevels" )
    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    public void setOrganisationUnitLevels( List<OrganisationUnitLevel> organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }
}
