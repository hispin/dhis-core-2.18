package org.hisp.dhis.cbhis.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Activity implements Persistent
{

    private Beneficiary beneficiary;

    private Task task;

    // private Date dueDate;
    private String dueDate;

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

    public String getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( String dueDate )
    {
        this.dueDate = dueDate;
    }

    // public Date getDueDate() {
    // return dueDate;
    // }
    //
    //
    // public void setDueDate(Date dueDate) {
    // this.dueDate = dueDate;
    // }

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

            // activity.setDueDate(new Date(din.readLong()));
            activity.setDueDate( din.readUTF() );

            task.setProgStageInstId( din.readInt() );
            task.setProgStageId( din.readInt() );
            task.setProgStageName( din.readUTF() );
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
            // dout.writeLong(activity.getDueDate().getTime());
            dout.writeUTF( activity.getDueDate() );
            // Write Task Information
            dout.writeInt( activity.getTask().getProgStageInstId() );
            dout.writeInt( activity.getTask().getProgStageId() );
            dout.writeUTF( activity.getTask().getProgStageName() );
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

    public void read( DataInputStream dis )
        throws IOException, InstantiationException, IllegalAccessException
    {
        // TODO Auto-generated method stub
        
    }

    public void write( DataOutputStream dos )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }


}
