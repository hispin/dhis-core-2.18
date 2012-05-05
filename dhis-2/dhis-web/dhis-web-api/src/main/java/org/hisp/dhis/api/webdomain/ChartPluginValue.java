package org.hisp.dhis.api.webdomain;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.Dxf2Namespace;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "dxf2", namespace = Dxf2Namespace.NAMESPACE )
public class ChartPluginValue
{
    private List<String[]> v = new ArrayList<String[]>();
    
    private List<Integer> p = new ArrayList<Integer>();

    @JsonProperty
    public List<String[]> getV()
    {
        return v;
    }

    public void setV( List<String[]> v )
    {
        this.v = v;
    }

    @JsonProperty
    public List<Integer> getP()
    {
        return p;
    }

    public void setP( List<Integer> p )
    {
        this.p = p;
    }
}
