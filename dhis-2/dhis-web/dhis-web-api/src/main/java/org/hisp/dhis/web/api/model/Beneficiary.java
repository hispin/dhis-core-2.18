package org.hisp.dhis.web.api.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Beneficiary
{

    private int id;

    private String firstName;

    private String middleName;

    private String lastName;

    @XmlAttribute
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @XmlAttribute
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    @XmlAttribute
    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    @XmlAttribute
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

}
