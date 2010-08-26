package org.hisp.dhis.mobile.model;

public class Beneficiary
{

    private int id;

    private String firstName, middleName, lastName;

    public Beneficiary()
    {
    }

    public String getFullName()
    {
        boolean space = false;
        String name = "";
        
        if (firstName != null && firstName.length() != 0) {
            name = firstName;
            space = true;
        }
        if (middleName != null && middleName.length() != 0) {
            if (space)
                name += " ";
            name += middleName;
            space = true;
        }
        if (lastName != null && lastName.length() != 0) {
            if (space)
                name += " ";
            name += lastName;
        }
        return name;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

}
