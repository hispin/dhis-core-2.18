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
@XmlRootElement( name = "dataElementCategoryOptions" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategoryOptions
{
    private List<DataElementCategoryOption> dataElementCategoryOptions = new ArrayList<DataElementCategoryOption>();

    public DataElementCategoryOptions()
    {

    }

    @XmlElement( name = "dataElementCategoryOption" )
    @JsonProperty( value = "dataElementCategoryOptions" )
    public List<DataElementCategoryOption> getDataElementCategoryOptions()
    {
        return dataElementCategoryOptions;
    }

    public void setDataElementCategoryOptions( List<DataElementCategoryOption> dataElementCategoryOptions )
    {
        this.dataElementCategoryOptions = dataElementCategoryOptions;
    }
}
