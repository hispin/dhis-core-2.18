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
@XmlRootElement( name = "dataElementCategoryOptionCombos", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategoryOptionCombos extends BaseLinkableObject
{
    private List<DataElementCategoryOptionCombo> dataElementCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

    @XmlElement( name = "dataElementCategoryOptionCombo" )
    @JsonProperty( value = "dataElementCategoryOptionCombos" )
    public List<DataElementCategoryOptionCombo> getDataElementCategoryOptionCombos()
    {
        return dataElementCategoryOptionCombos;
    }

    public void setDataElementCategoryOptionCombos( List<DataElementCategoryOptionCombo> dataElementCategoryOptionCombos )
    {
        this.dataElementCategoryOptionCombos = dataElementCategoryOptionCombos;
    }
}
