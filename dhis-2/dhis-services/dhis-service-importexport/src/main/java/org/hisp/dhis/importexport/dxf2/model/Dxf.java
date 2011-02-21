package org.hisp.dhis.importexport.dxf2.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Dxf
{

    
    @XmlElementWrapper( name="dataSets" )
    @XmlElement( name="dataSet" )
    private List<DataSet> dataSets;

    @XmlElementWrapper( name="dataValueSets" )
    @XmlElement( name="dataValueSet" )
    private List<DataValueSet> dataValueSets;

    public void setDataValueSets( List<DataValueSet> dataValueSets )
    {
        this.dataValueSets = dataValueSets;
    }

    public List<DataValueSet> getDataValueSets()
    {
        return dataValueSets;
    }

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    
}
