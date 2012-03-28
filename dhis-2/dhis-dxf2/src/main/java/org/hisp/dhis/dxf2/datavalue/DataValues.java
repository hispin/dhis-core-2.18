package org.hisp.dhis.dxf2.datavalue;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.Dxf2Namespace;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "dxf2", namespace = Dxf2Namespace.NAMESPACE )
public class DataValues
{
    private List<DataValue> dataValues = new ArrayList<DataValue>();

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataValues", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataValue", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<DataValue> dataValues )
    {
        this.dataValues = dataValues;
    }
}
