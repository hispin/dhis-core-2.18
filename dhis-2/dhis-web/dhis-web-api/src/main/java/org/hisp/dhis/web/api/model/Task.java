package org.hisp.dhis.web.api.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Task implements ISerializable
{
    private int id;   
    
    private int programStageId;
    
    private boolean completed;

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
    public int getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( int programStageId )
    {
        this.programStageId = programStageId;
    }
    
    @XmlAttribute
    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

	@Override
	public byte[] serialize() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void serialize( DataOutputStream dout ) throws IOException
    {		
		dout.writeInt(this.getId());
        dout.writeInt(this.getProgramStageId());        
        dout.writeBoolean(this.isCompleted());
        
        dout.flush();            	
    }
	
	public void serialize( OutputStream out ) throws IOException
    {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        dout.writeInt(this.getId());
        dout.writeInt(this.getProgramStageId());        
        dout.writeBoolean(this.isCompleted());            
        
        bout.flush();
        bout.writeTo(out);
    	
    } 

	@Override
	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
