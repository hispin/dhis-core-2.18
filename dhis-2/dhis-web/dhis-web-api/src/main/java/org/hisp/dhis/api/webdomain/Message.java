package org.hisp.dhis.api.webdomain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.UserXmlAdapter;
import org.hisp.dhis.user.User;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "message", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Message
{
    private String subject;

    private String text;

    private Set<User> users = new HashSet<User>();

    @XmlElement
    @JsonProperty
    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    @XmlElement
    @JsonProperty
    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    @XmlElementWrapper( name = "users" )
    @XmlElement( name = "user" )
    @XmlJavaTypeAdapter( UserXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers( Set<User> users )
    {
        this.users = users;
    }
}
