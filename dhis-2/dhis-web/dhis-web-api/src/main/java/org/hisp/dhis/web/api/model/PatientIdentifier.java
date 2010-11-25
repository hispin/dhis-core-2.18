package org.hisp.dhis.web.api.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PatientIdentifier implements DataStreamSerializable
{
    private String identifierType;
    
    private String identifier;
    
    public PatientIdentifier( String identifierType, String identifier )
    {
        this.identifierType = identifierType;
        this.identifier = identifier;
    }

    public String getIdentifierType()
    {
        return identifierType;
    }

    public void setIdentifierType( String identifierType )
    {
        this.identifierType = identifierType;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }

    @Override
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeUTF( identifierType );
        dataOutputStream.writeUTF( identifier );
    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        
    }
    
    

}
