package org.hisp.dhis.dataelement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hisp.dhis.common.BaseLinkableObject;
import org.hisp.dhis.common.Dxf2Namespace;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "dataElementCategories", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategories extends BaseLinkableObject
{
    private List<DataElementCategory> dataElementCategories = new ArrayList<DataElementCategory>();

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
