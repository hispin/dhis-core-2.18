package org.hisp.dhis.web.api.model;

import javax.xml.bind.annotation.XmlAttribute;

public class Link
{

    private String url;

    public Link()
    {
    }

    public Link( String url )
    {
        this.url = url;
    }

    @XmlAttribute
    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }


}
