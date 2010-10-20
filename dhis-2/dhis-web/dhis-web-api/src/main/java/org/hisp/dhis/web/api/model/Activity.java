package org.hisp.dhis.web.api.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Activity implements ISerializable
{

    private Beneficiary beneficiary;

    private boolean late = false;

    private Task task;

    private Date dueDate;   

    public Beneficiary getBeneficiary()
    {
        return beneficiary;
    }

    public void setBeneficiary( Beneficiary beneficiary )
    {
        this.beneficiary = beneficiary;
    }

    public Task getTask()
    {
        return task;
    }

    public void setTask( Task task )
    {
        this.task = task;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( Date dueDate )
    {
        this.dueDate = dueDate;
    }

    
    
	public boolean isLate() {
		return late;
	}

	public void setLate(boolean late) {
		this.late = late;
	}

	@Override
	public byte[] serialize() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void serialize( DataOutputStream dout ) throws IOException
    {
        this.getBeneficiary().serialize(dout);
        this.getTask().serialize(dout);
        
        dout.writeLong(this.getDueDate().getTime());	      
        
        dout.flush();            	
    }
	
	public void serialize( OutputStream out ) throws IOException
    {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        this.getBeneficiary().serialize(dout);
        dout.writeBoolean(late);
        this.getTask().serialize(dout);        
        dout.writeLong(this.getDueDate().getTime());       
        
        bout.flush();
        bout.writeTo(out);
    	
    }

	@Override
	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
