package org.hisp.dhis.mobile.reporting.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class Activity implements ISerializable
{

    private Beneficiary beneficiary;

    private Task task;

    private Date dueDate;   
    
    public Activity()
    {
    }

    // Getter and Setter
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

    public static Activity recordToActivity( byte[] rec )
    {
        ByteArrayInputStream bin = new ByteArrayInputStream( rec );
        DataInputStream din = new DataInputStream( bin );

        Activity activity = new Activity();
        Beneficiary beneficiary = new Beneficiary();
        Task task = new Task();
        try
        {
            beneficiary.setId( din.readInt() );
            beneficiary.setLastName( din.readUTF() );
            beneficiary.setMiddleName( din.readUTF() );
            beneficiary.setFirstName( din.readUTF() );

            activity.setDueDate(new Date(din.readLong()));

            task.setProgStageInstId( din.readInt() );
            task.setProgStageId( din.readInt() );
            task.setComplete( din.readBoolean() );

            activity.setTask( task );
            activity.setBeneficiary( beneficiary );
        }
        catch ( IOException ioe )
        {
        }

        return activity;
    }

    public static byte[] activityToRecord( Activity activity )
    {
        ByteArrayOutputStream deOs = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( deOs );

        try
        {
            // Write Beneficiary Information
            dout.writeInt( activity.getBeneficiary().getId() );
            dout.writeUTF( activity.getBeneficiary().getLastName() );
            dout.writeUTF( activity.getBeneficiary().getMiddleName() );
            dout.writeUTF( activity.getBeneficiary().getFirstName() );
            // Write Due Date
            dout.writeLong(activity.getDueDate().getTime());
            // Write Task Information
            dout.writeInt( activity.getTask().getProgStageInstId() );
            dout.writeInt( activity.getTask().getProgStageId() );
            dout.writeBoolean( activity.getTask().isComplete() );
            dout.flush();
        }
        catch ( IOException e )
        {
            System.out.println( e );
            e.printStackTrace();
        }
        return deOs.toByteArray();
    }   
    
    
    public void deSerialize(DataInputStream din) throws IOException {
    	
    	this.setDueDate(new Date(din.readLong()));    	
    	this.beneficiary.deSerialize(din);    	
		this.task.deSerialize(din);		
	}

	public byte[] serialize() throws IOException {
		
		ByteArrayOutputStream deOs = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream( deOs );

        try
        {
            // Write Beneficiary Information
            dout.writeInt( this.getBeneficiary().getId() );
            dout.writeUTF( this.getBeneficiary().getLastName() );
            dout.writeUTF( this.getBeneficiary().getMiddleName() );
            dout.writeUTF( this.getBeneficiary().getFirstName() );
            // Write Due Date
            dout.writeLong(this.getDueDate().getTime());
            // Write Task Information
            dout.writeInt( this.getTask().getProgStageInstId() );
            dout.writeInt( this.getTask().getProgStageId() );
            dout.writeBoolean( this.getTask().isComplete() );
            dout.flush();
        }
        catch ( IOException e )
        {
            System.out.println( e );
            e.printStackTrace();
        }
        
        return deOs.toByteArray();
	}

	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
