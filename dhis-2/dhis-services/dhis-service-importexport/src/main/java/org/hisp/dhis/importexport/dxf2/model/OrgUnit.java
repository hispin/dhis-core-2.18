package org.hisp.dhis.importexport.dxf2.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrgUnit extends Link
{
    private Link parent;
    
    @XmlElementWrapper(name="children")
    @XmlElement(name="orgUnit")
    private List<Link> children;

    @XmlElementWrapper(name="dataSets")
    @XmlElement(name="dataSet")
    private List<Link> dataSets;
    
    public List<Link> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<Link> dataSets )
    {
        this.dataSets = dataSets;
    }

    public List<Link> getChildren()
    {
        return children;
    }

    public void setChildren( List<Link> children )
    {
        this.children = children;
    }

    public Link getParent()
    {
        return parent;
    }

    public void setParent( Link parent )
    {
        this.parent = parent;
    }   
}
