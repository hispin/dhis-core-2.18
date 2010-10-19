package org.hisp.dhis.mobile.reporting.model;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

public class ActivityPlan implements ISerializable {

	private Vector activities = new Vector();

	public Vector getActivities() {
		return activities;
	}

	public void setActivities(Vector activities) {
		this.activities = activities;
	}

	public void deSerialize(DataInputStream din) throws IOException {
		int size = din.readInt();
	        
		for (int i = 0; i < size; i++) {
			Activity activity = new Activity();
			activity.setDueDate(new Date(din.readLong()));

			Beneficiary b = new Beneficiary();
			b.setId(din.readInt());
			b.setFirstName(din.readUTF());
			b.setMiddleName(din.readUTF());
			b.setLastName(din.readUTF());
			int attsNumb = din.readInt();
			
			Vector attsVector = b.getAttsValues();
			
	                for(int j=0;j<attsNumb;j++){	                    
	                    attsVector.addElement( din.readUTF() );
	                }
			activity.setBeneficiary(b);

			Task t = new Task();
			t.setProgStageInstId(din.readInt());
			t.setProgStageId(din.readInt());
			t.setComplete(din.readBoolean());
			activity.setTask(t);

			this.activities.addElement(activity);
		}
	}

	public byte[] serialize() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub

	}
}
