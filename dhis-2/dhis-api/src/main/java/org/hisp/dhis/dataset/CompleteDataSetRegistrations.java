package org.hisp.dhis.dataset;

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
@XmlRootElement( name = "completeDataSetRegistrations" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class CompleteDataSetRegistrations
{
    private List<CompleteDataSetRegistration> completeDataSetRegistrations = new ArrayList<CompleteDataSetRegistration>();

    public CompleteDataSetRegistrations()
    {

    }

    @XmlElement( name = "completeDataSetRegistration" )
    @JsonProperty( value = "completeDataSetRegistrations" )
    public List<CompleteDataSetRegistration> getCompleteDataSetRegistrations()
    {
        return completeDataSetRegistrations;
    }

    public void setCompleteDataSetRegistrations( List<CompleteDataSetRegistration> completeDataSetRegistrations )
    {
        this.completeDataSetRegistrations = completeDataSetRegistrations;
    }
}
