package org.hisp.dhis.patient.api.model;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAttribute;

import org.hisp.dhis.patient.api.resources.ProgramFormsResource;

public class Link
{

    @XmlAttribute
    private String url;

    public Link()
    {
    }

    public Link( String url )
    {
        this.url = url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }


}
