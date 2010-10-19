package org.hisp.dhis.mobile.reporting.db;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import org.hisp.dhis.mobile.reporting.model.ActivityValue;

public class ActivityValueRecordStore {
	public static final String ACTIVITYVALUE_DB = "ACTIVITYVALUE";
	private String dbName;
	
	public ActivityValueRecordStore(String dbName) {
		super();
		this.dbName = dbName;
	}
	
	public String saveActivityValue(ActivityValue activityValue){
		RecordStore rs = null;
		RecordEnumeration re = null;
		ActivityValueFilter activityValueFilter = new ActivityValueFilter(activityValue.getProgramInstanceId());
		
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(activityValueFilter, null, true);
			if (re.numRecords() > 0){
				int id = re.nextRecordId();
				rs.setRecord(id, activityValue.serialize(), 0, activityValue.serialize().length);
				System.out.println("Update");
			} else {
				rs.addRecord(activityValue.serialize(), 0, activityValue.serialize().length);
				System.out.println("Save");
			}
			rs.closeRecordStore();
			activityValueFilter = null;
			re = null;
			rs = null;

		} catch (Exception e) {
			e.printStackTrace();
			return "Fail to Save Data Value";
		}
		return "Data Values Saved";
	}
	
	public ActivityValue load(int proStageInsID){
		RecordStore rs = null;
		RecordEnumeration re = null;
		ActivityValueFilter activityValueFilter = new ActivityValueFilter(proStageInsID);
		ActivityValue activityValue = new ActivityValue();
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(activityValueFilter, null, true);
			if (re.numRecords() > 0){
				activityValue.deSerialize(re.nextRecord());
				return activityValue;
			}
			rs.closeRecordStore();
			activityValueFilter = null;
			re = null;
			rs = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
