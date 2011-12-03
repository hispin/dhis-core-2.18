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
@XmlRootElement( name = "dataElementCategoryCombos", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategoryCombos extends BaseLinkableObject
{
    private List<DataElementCategoryCombo> dataElementCategoryCombos = new ArrayList<DataElementCategoryCombo>();

    @XmlElement( name = "dataElementCategoryCombo" )
    @JsonProperty( value = "dataElementCategoryCombos" )
    public List<DataElementCategoryCombo> getDataElementCategoryCombos()
    {
        return dataElementCategoryCombos;
    }

    public void setDataElementCategoryCombos( List<DataElementCategoryCombo> dataElementCategoryCombos )
    {
        this.dataElementCategoryCombos = dataElementCategoryCombos;
    }
}
