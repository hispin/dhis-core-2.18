package org.hisp.dhis.dataelement;

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
@XmlRootElement( name = "dataElementCategories" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategories
{
    private List<DataElementCategory> dataElementCategories = new ArrayList<DataElementCategory>();

    public DataElementCategories()
    {

    }

    @XmlElement( name = "dataElementCategory" )
    @JsonProperty( value = "dataElementCategories" )
    public List<DataElementCategory> getDataElementCategories()
    {
        return dataElementCategories;
    }

    public void setDataElementCategories( List<DataElementCategory> dataElementCategories )
    {
        this.dataElementCategories = dataElementCategories;
    }
}
