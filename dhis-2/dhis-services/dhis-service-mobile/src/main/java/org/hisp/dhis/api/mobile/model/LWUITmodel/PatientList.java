package org.hisp.dhis.api.mobile.model.LWUITmodel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.api.mobile.model.DataStreamSerializable;

public class PatientList
    implements DataStreamSerializable
{
    private String clientVersion;

    private List<Patient> patientList = new ArrayList<Patient>();
    
    public PatientList()
    {
        
    }
    
    public List<Patient> getPatientList()
    {
        return patientList;
    }
    
    public void setPatientList( List<Patient> patients )
    {
        this.patientList = patients;
    }

    public String getClientVersion()
    {
        return clientVersion;
    }

    public void setClientVersion( String clientVersion )
    {
        this.clientVersion = clientVersion;
    }
    
    @Override
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        if ( patientList != null )
        {
            dataOutputStream.writeInt( patientList.size() );
            for ( Patient patient : patientList )
            {
                
                patient.serialize( dataOutputStream );
            }
        }
        else
        {
            dataOutputStream.writeInt( 0 );
        }
    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        int size = 0;
        size = dataInputStream.readInt();
        if ( size > 0 )
        {
            patientList = new ArrayList<Patient>();
            for ( int i = 0; i < size; i++ )
            {
                Patient patient = new Patient();
                patient.deSerialize( dataInputStream );
                patientList.add( patient );
            }
        }
    }

    @Override
    public void serializeVersion2_8( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void serializeVersion2_9( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void serializeVersion2_10( DataOutputStream dataOutputStream )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }

}
