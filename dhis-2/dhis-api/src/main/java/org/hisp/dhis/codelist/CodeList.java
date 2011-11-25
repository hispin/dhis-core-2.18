package org.hisp.dhis.codelist;

import org.hisp.dhis.common.BaseIdentifiableObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "codeList" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class CodeList<T extends BaseIdentifiableObject>
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
