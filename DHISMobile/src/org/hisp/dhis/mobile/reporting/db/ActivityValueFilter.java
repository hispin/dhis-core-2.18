package org.hisp.dhis.mobile.reporting.db;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.rms.RecordFilter;

public class ActivityValueFilter implements RecordFilter {
	private int proStageInsID;
	
	public ActivityValueFilter(int proStageInsID) {
		this.proStageInsID = proStageInsID;
	}
	
	public boolean matches(byte[] candidate) {
		ByteArrayInputStream bis = new ByteArrayInputStream(candidate);
		DataInputStream dis = new DataInputStream(bis);
		try {
			if (dis.readInt() == proStageInsID){
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
