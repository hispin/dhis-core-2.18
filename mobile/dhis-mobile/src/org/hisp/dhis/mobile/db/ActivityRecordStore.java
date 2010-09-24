package org.hisp.dhis.mobile.db;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.hisp.dhis.mobile.model.Activity;

/**
 * @author Tran Ng Minh Luan
 *
 */
public class ActivityRecordStore {
	private String dbName;
	private Vector activityVector;

	//Constructor
	public ActivityRecordStore() {
		this.dbName = ModelRecordStore.ACTIVITY_DB;
	}
	
	//Getter & Setter
	
	public Vector getActivityVector() {
		return activityVector;
	}

	public void setActivityVector(Vector activityVector) {
		this.activityVector = activityVector;
	}
	
	//Supportive methods
	
	public void update(Activity activity){
            RecordStore rs = null;
            int recordId = 0;
            recordId = this.getRecordId( activity );
            if(recordId > 0){
                try
            {
                rs = RecordStore.openRecordStore(dbName, true);
                byte[] newData = Activity.activityToRecord( activity );
                rs.setRecord( recordId, newData, 0, newData.length );
            }
            catch ( RecordStoreFullException e )
            {
                e.printStackTrace();
            }
            catch ( RecordStoreNotFoundException e )
            {
                e.printStackTrace();
            }
            catch ( RecordStoreException e )
            {
                e.printStackTrace();
            }
            }
            
            
        }
        
        private int getRecordId(Activity activity){
            RecordStore rs = null;
            RecordEnumeration re = null;
            ActivityRecordFilter rf = new ActivityRecordFilter( ActivityRecordFilter.filterByProgStageInstId ); 
            rf.setProgStageInstId( activity.getTask().getProgStageInstId() );
            int recordId = 0;
            try {
                    rs = RecordStore.openRecordStore(dbName, true);
                    re = rs.enumerateRecords(rf, null, false);
                    while (re.hasNextElement()) {
                        recordId = re.nextRecordId();
                        return recordId;                 
                    }
                    return recordId;
            } catch (Exception e) {

            } finally {
                    if (re != null)
                            re.destroy();
                    if (rs != null)
                            try {
                                    rs.closeRecordStore();
                            } catch (RecordStoreNotOpenException e) {
                                    e.printStackTrace();
                            } catch (RecordStoreException e) {
                                    e.printStackTrace();
                            }
            }
            return recordId;
        }
	public void save() {
		RecordStore rs = null;
		clear();
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			
			if (activityVector != null && activityVector.size() > 0) {
				
				Enumeration activities = activityVector.elements();
				byte[] activityByte;
				Activity activity;
				while (activities.hasMoreElements()) {
					
					activity = (Activity) activities.nextElement();
					
					activityByte = Activity.activityToRecord(activity);
					rs.addRecord(activityByte, 0, activityByte.length);
				}
				activityByte = null;
				activity = null;
			}
		} catch (RecordStoreException e) {
		} finally {
			if (rs != null)
				try {
					rs.closeRecordStore();
				} catch (RecordStoreNotOpenException e) {
					e.printStackTrace();
				} catch (RecordStoreException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void clear() {
		RecordStore rs = null;
		RecordEnumeration re = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(null, null, false);
			int id;
			while (re.hasNextElement()) {
				id = re.nextRecordId();
				rs.deleteRecord(id);
			}
		} catch (Exception e) {

		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				try {
					rs.closeRecordStore();
				} catch (RecordStoreNotOpenException e) {
					e.printStackTrace();
				} catch (RecordStoreException e) {
					e.printStackTrace();
				}
		}
	}
	
//	public Vector loadAll(){
//		RecordStore rs = null;
//		RecordEnumeration re = null;
//		activityVector = new Vector();
//		try {
//			rs = RecordStore.openRecordStore(dbName, true);
//			re = rs.enumerateRecords(null, null, false);
//			while (re.hasNextElement()) {
//				activityVector.addElement(Activity.recordToActivity(re.nextRecord()));
//			}
//		} catch (Exception e) {
//
//		} finally {
//			if (re != null)
//				re.destroy();
//			if (rs != null)
//				try {
//					rs.closeRecordStore();
//				} catch (RecordStoreNotOpenException e) {
//					e.printStackTrace();
//				} catch (RecordStoreException e) {
//					e.printStackTrace();
//				}
//		}
//		return getActivityVector();
//	}
	public Vector loadAll(){
            RecordStore rs = null;
            RecordEnumeration re = null;
            activityVector = new Vector();
            try {
                    rs = RecordStore.openRecordStore(dbName, true);
                    re = rs.enumerateRecords(new ActivityRecordFilter( ActivityRecordFilter.filterByStatusIncomplete ), null, false);
                    while (re.hasNextElement()) {
                            activityVector.addElement(Activity.recordToActivity(re.nextRecord()));
                    }
            } catch (Exception e) {

            } finally {
                    if (re != null)
                            re.destroy();
                    if (rs != null)
                            try {
                                    rs.closeRecordStore();
                            } catch (RecordStoreNotOpenException e) {
                                    e.printStackTrace();
                            } catch (RecordStoreException e) {
                                    e.printStackTrace();
                            }
            }
            return getActivityVector();
    }
    
	
	
	
	//This Method is used in case two CHW share one mobile, so we need to filter activities of different orgunits.
	
//	public Vector load(int orgUnitId){
//		RecordStore rs = null;
//		RecordEnumeration re = null;
//		this.activityVector = new Vector();
//		RecordFilter rf = new ActivityRecordOrdUnitIdFilter(orgUnitId);
//		
//		try {
//			rs = RecordStore.openRecordStore(dbName, true);
//			re = rs.enumerateRecords(rf, null, false);
//			while (re.hasNextElement()) {
//				byte[] activityByte = re.nextRecord();
//				Activity activity = Activity.recordToActivity(activityByte);
//				activityVector.addElement(activity);
//			}
//			rf = null;
//		} catch (RecordStoreFullException e) {
//			System.out.println("exception record store activity full");
//			e.printStackTrace();
//		} catch (RecordStoreNotFoundException e) {
//			System.out.println("exception record store activity not found");
//			e.printStackTrace();
//		} catch (RecordStoreException e) {
//			System.out.println("exception record store activity general");
//			e.printStackTrace();
//		} finally {
//		        rf = null;
//			if (re != null)
//				re.destroy();
//			if (rs != null)
//				try {
//					rs.closeRecordStore();
//				} catch (RecordStoreNotOpenException e) {
//					e.printStackTrace();
//				} catch (RecordStoreException e) {
//					e.printStackTrace();
//				}
//		}
//		return getActivityVector();
//	}
	
}
