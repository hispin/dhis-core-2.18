package org.hisp.dhis.codelist;

import org.hisp.dhis.common.AbstractIdentifiableObject;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "codeList" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class CodeList<T extends AbstractIdentifiableObject>
{
    private List<T> list = new ArrayList<T>();

    public CodeList()
    {

    }

    public CodeList( List<T> list )
    {
        this.list = list;
    }

    @XmlElement( name = "code" )
    public List<T> getList()
    {
        return list;
    }

    public void setList( List<T> list )
    {
        this.list = list;
    }
}
