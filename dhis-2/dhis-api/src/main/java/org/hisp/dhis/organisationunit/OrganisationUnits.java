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
@XmlRootElement( name = "organisationUnits" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class OrganisationUnits
{
    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    public OrganisationUnits()
    {

    }

    @XmlElement( name = "organisationUnit" )
    @JsonProperty( value = "organisationUnits" )
    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }
}
