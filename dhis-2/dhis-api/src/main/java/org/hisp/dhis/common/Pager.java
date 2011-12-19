package org.hisp.dhis.common;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "pager", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Pager
{
    public static final int DEFAULT_PAGE_SIZE = 50;

    private int page = 1;

    private int total = 0;

    private int pageSize = Pager.DEFAULT_PAGE_SIZE;

    public Pager()
    {

    }

    public Pager( int page, int total )
    {
        this.page = page;
        this.total = total;

        if ( this.page > getPageCount() )
        {
            this.page = getPageCount();
        }

        if ( this.page < 1 )
        {
            this.page = 1;
        }
    }

    public Pager( int page, int total, int pageSize )
    {
        this.page = page;
        this.total = total;
        this.pageSize = pageSize;

        if ( this.page > getPageCount() )
        {
            this.page = getPageCount();
        }

        if ( this.page < 1 )
        {
            this.page = 1;
        }
    }

    @XmlElement
    @JsonProperty
    public int getPage()
    {
        return page;
    }

    @XmlElement
    @JsonProperty
    public int getTotal()
    {
        return total;
    }

    @XmlElement
    @JsonProperty
    public int getPageSize()
    {
        return pageSize;
    }

    @XmlElement
    @JsonProperty
    public int getPageCount()
    {
        int pageCount = 1;
        int totalTmp = total;

        while ( totalTmp > pageSize )
        {
            totalTmp -= pageSize;
            pageCount++;
        }

        return pageCount;
    }

    public int getOffset()
    {
        return (page * pageSize) - pageSize;
    }
}
