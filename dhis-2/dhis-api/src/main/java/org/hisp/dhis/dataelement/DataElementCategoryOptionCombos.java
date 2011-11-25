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
@XmlRootElement( name = "dataElementCategoryOptionCombos" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategoryOptionCombos
{
    private List<DataElementCategoryOptionCombo> dataElementCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

    public DataElementCategoryOptionCombos()
    {

    }

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
