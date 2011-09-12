package org.hisp.dhis.importexport.dxf2.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataSetLinks
{
    private List<Link> dataSet;

    public DataSetLinks()
    {
    }

    public DataSetLinks( List<Link> dataSet )
    {
        this.dataSet = dataSet;
    }

    public List<Link> getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( List<Link> dataSet )
    {
        this.dataSet = dataSet;
    }
}
