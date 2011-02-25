package org.hisp.dhis.importexport.dxf2.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class OrgUnitLinks
{

    List<Link> orgUnit;

    public OrgUnitLinks()
    {
    }

    public OrgUnitLinks( List<Link> orgUnit )
    {
        this.orgUnit = orgUnit;
    }

    public List<Link> getOrgUnit()
    {
        return orgUnit;
    }

    public void setOrgUnit( List<Link> orgUnit )
    {
        this.orgUnit = orgUnit;
    }

    
    
}
