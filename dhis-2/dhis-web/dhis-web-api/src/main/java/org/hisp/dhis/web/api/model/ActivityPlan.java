package org.hisp.dhis.web.api.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityPlan implements ISerializable
{

    private List<Activity> activitiesList;

    @XmlElement( name = "activity" )
    public List<Activity> getActivitiesList()
    {
        return activitiesList;
    }

    public void setActivitiesList( List<Activity> activitiesList )
    {
        this.activitiesList = activitiesList;
    }

	@Override
	public byte[] serialize() throws IOException {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        dout.writeInt(this.getActivitiesList().size());

        for(int i=0; i<activitiesList.size(); i++)
        {
            Activity activity = (Activity)activitiesList.get(i);
            activity.serialize(dout);            
        }

        return bout.toByteArray();
	}
	
	public void serialize( OutputStream out ) throws IOException
    {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);       
        
        dout.writeInt(activitiesList.size());
        

        for(int i=0; i<activitiesList.size(); i++)
        {
        	Activity activity = (Activity)activitiesList.get(i);
        	
        	dout.writeLong(activity.getDueDate().getTime());
        	
        	Beneficiary b = activity.getBeneficiary();
        	dout.writeInt(b.getId()); 
        	dout.writeUTF(b.getFirstName()); 
        	dout.writeUTF(b.getMiddleName()); 
        	dout.writeUTF(b.getLastName());
        	dout.writeBoolean(activity.isLate());
        	Set<String> atts = b.getPatientAttValues();
                dout.writeInt( atts.size() );
                for(String att : atts){
                    dout.writeUTF( att );
                }
        	
        	Task t = activity.getTask();
        	dout.writeInt(t.getId()); dout.writeInt(t.getProgramStageId()); dout.writeBoolean(t.isCompleted());           
        }      
        
        bout.flush();
        bout.writeTo(out);
    	
    } 

	@Override
	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
