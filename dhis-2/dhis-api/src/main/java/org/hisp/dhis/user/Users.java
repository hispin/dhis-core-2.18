package org.hisp.dhis.user;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "users" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Users
{
    private List<User> users = new ArrayList<User>();

    public Users()
    {

    }

    @XmlElement( name = "user" )
    @JsonProperty( value = "users" )
    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers( List<User> users )
    {
        this.users = users;
    }
}
