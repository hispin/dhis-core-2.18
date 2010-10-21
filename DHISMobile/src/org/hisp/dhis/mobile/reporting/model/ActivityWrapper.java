package org.hisp.dhis.mobile.reporting.model;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

/**
 * @author Tran Ng Minh Luan
 *
 */
public class ActivityWrapper {
	private Vector programs;
	private ActivityPlan activityPlan;
	
	public ActivityWrapper() {
	}

	public Vector getPrograms() {
		return programs;
	}

	public void setPrograms(Vector programs) {
		this.programs = programs;
	}

	public ActivityPlan getActivityPlan() {
		return activityPlan;
	}

	public void setActivityPlan(ActivityPlan activityPlan) {
		this.activityPlan = activityPlan;
	}
	
	public void deSerialize(DataInputStream din) throws IOException {
		this.activityPlan = new ActivityPlan();
		this.programs = new Vector();
		
		int numbProgram = din.readInt();
		System.out.println("Deserialize of ActivityWrapper - Number of Programs:"+numbProgram);
		activityPlan.deSerialize(din);
		System.out.println("deserialize programs:");
		for(int i=0; i<numbProgram;i++){
			Program program = new Program();
			program.deSerialize(din);
			programs.addElement(program);
		}
		
		System.out.println("ActivityWrapper: number of programs:"+programs.size());
	}
	
	
	
	
}

