package org.hisp.dhis.mobile.db;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.rms.RecordFilter;

/**
 * @author Tran Ng Minh Luan
 *
 */
public class ActivityRecordOrdUnitIdFilter implements RecordFilter{
	private int orgUnitId;
	
	public ActivityRecordOrdUnitIdFilter(int orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	public int getOrgUnitId() {
		return orgUnitId;
	}

	public void setOrgUnitId(int orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	public boolean matches(byte[] candidate){
		ByteArrayInputStream bis = new ByteArrayInputStream(candidate);
		DataInputStream dis = new DataInputStream(bis);
		try{
		if(dis.readInt() == this.orgUnitId){
			return true;
		}else{
			return false;
		}
		}catch(Exception e){
			System.out.println("Activity Filter get exception");
			return false;
		}
		finally{
			try {
				bis.close();
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
