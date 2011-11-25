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
@XmlRootElement( name = "dataSets" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataSets
{
    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public DataSets()
    {

    }

    @XmlElement( name = "dataSet" )
    @JsonProperty( value = "dataSets" )
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }
}
