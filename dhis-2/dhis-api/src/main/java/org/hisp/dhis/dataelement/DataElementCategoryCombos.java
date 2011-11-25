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
@XmlRootElement( name = "dataElementCategoryCombos" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategoryCombos
{
    private List<DataElementCategoryCombo> dataElementCategoryCombos = new ArrayList<DataElementCategoryCombo>();

    public DataElementCategoryCombos()
    {

    }

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
