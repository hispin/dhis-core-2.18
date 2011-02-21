package org.hisp.dhis.importexport.dxf2.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class DataSet
{
    @XmlAttribute(name="id")
    private String uuid;
        
    @XmlAttribute
    private String name;
    
    @XmlAttribute
    private String shortName;
    
    @XmlAttribute
    private String code;
    
    @XmlAttribute
    private String periodType;

    @XmlElementWrapper(name="members")
    @XmlElement(name="dataElement")
    private List<DataElement> members;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid( String uuid )
    {
        this.uuid = uuid;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public List<DataElement> getMembers()
    {
        return members;
    }

    public void setMembers( List<DataElement> members )
    {
        this.members = members;
    }


}
