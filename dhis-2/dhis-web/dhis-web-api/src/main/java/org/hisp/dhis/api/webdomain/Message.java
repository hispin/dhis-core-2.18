package org.hisp.dhis.api.webdomain;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.user.User;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "message", namespace = Dxf2Namespace.NAMESPACE )
public class Message
{
    private String subject;

    private String text;

    private Set<User> users = new HashSet<User>();

    public Message()
    {
    }

    public Message( String subject, String text )
    {
        this.subject = subject;
        this.text = text;
    }

    @JsonProperty
    @JsonView( {DetailedView.class} )
    @JacksonXmlProperty
    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    @JsonProperty
    @JsonView( {DetailedView.class} )
    @JacksonXmlProperty
    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class} )
    @JacksonXmlElementWrapper( localName = "users" )
    @JacksonXmlProperty( localName = "user" )
    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers( Set<User> users )
    {
        this.users = users;
    }
}
