package org.hisp.dhis.web.api.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Beneficiary
    implements ISerializable
{
    private int id;

    private String firstName;

    private String middleName;

    private String lastName;

    private Set<String> patientAttValues;

    private PatientAttribute groupAttribute;

    public PatientAttribute getGroupAttribute()
    {
        return groupAttribute;
    }

    public void setGroupAttribute( PatientAttribute groupAttribute )
    {
        this.groupAttribute = groupAttribute;
    }

    public Set<String> getPatientAttValues()
    {
        return patientAttValues;
    }

    public void setPatientAttValues( Set<String> patientAttValues )
    {
        this.patientAttValues = patientAttValues;
    }

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

    public byte[] serialize()
        throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void serialize( DataOutputStream out )
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( bout );

        dout.writeInt( this.getId() );
        dout.writeUTF( this.getFirstName() );
        dout.writeUTF( this.getMiddleName() );
        dout.writeUTF( this.getLastName() );
        //Write attribute which is used as group factor of beneficiary.
        /* False: no group factor
         * True: with group factor
         * */
        if ( this.getGroupAttribute() != null )
        {
            dout.writeBoolean( true );
            this.getGroupAttribute().serialize( dout );
        }
        else
        {
            dout.writeBoolean( false );
        }

        Set<String> atts = this.getPatientAttValues();
        dout.writeInt( atts.size() );
        for ( String att : atts )
        {
            dout.writeUTF( att );
        }

        bout.flush();
        bout.writeTo( out );
    }

    public void deSerialize( byte[] data )
        throws IOException
    {
        // TODO Auto-generated method stub
    }

}
