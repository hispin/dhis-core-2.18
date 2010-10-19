package org.hisp.dhis.mobile.reporting.db;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.reporting.gui.DHISMIDlet;
import org.hisp.dhis.mobile.reporting.model.Activity;

/**
 * @author Tran Ng Minh Luan
 * 
 */
public class ActivityRecordStore implements Runnable {
	public static final String LOAD_ALL_ACTIVITYPLAN = "loadall";
	public static final String LOAD_CURRENT_ACTIVITYPLAN = "loadcurrent";
	public static final String LOAD_COMPLETED_ACTIVITYPLAN = "loadcompleted";
	private String dbName;
	private String task;
	private Vector activityVector;
	private DHISMIDlet dhisMIDlet;

	// Constructor
	public ActivityRecordStore(DHISMIDlet dhisMIDlet, String task) {
		this.dbName = ModelRecordStore.ACTIVITY_DB;
		this.dhisMIDlet = dhisMIDlet;
		this.task = task;
	}
	
	public ActivityRecordStore(DHISMIDlet dhisMIDlet) {
		this.dbName = ModelRecordStore.ACTIVITY_DB;
		this.dhisMIDlet = dhisMIDlet;

	}

	// Constructor
	public ActivityRecordStore() {
		this.dbName = ModelRecordStore.ACTIVITY_DB;
	}

	// Getter & Setter

	public Vector getActivityVector() {
		return activityVector;
	}

	public void setActivityVector(Vector activityVector) {
		this.activityVector = activityVector;
	}

	// Supportive methods

	public void update(Activity activity) {
		RecordStore rs = null;
		int recordId = 0;
		recordId = this.getRecordId(activity);
		if (recordId > 0) {
			try {
				rs = RecordStore.openRecordStore(dbName, true);
				byte[] newData = Activity.activityToRecord(activity);
				rs.setRecord(recordId, newData, 0, newData.length);
			} catch (RecordStoreFullException e) {
				e.printStackTrace();
			} catch (RecordStoreNotFoundException e) {
				e.printStackTrace();
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}

	}

	private int getRecordId(Activity activity) {
		RecordStore rs = null;
		RecordEnumeration re = null;
		ActivityRecordFilter rf = new ActivityRecordFilter(
				ActivityRecordFilter.filterByProgStageInstId);
		rf.setProgStageInstId(activity.getTask().getProgStageInstId());
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

		clear();

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

	public void loadAll() {
		RecordStore rs = null;
		RecordEnumeration re = null;
		activityVector = new Vector();
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(new ActivityRecordFilter(
					ActivityRecordFilter.filterByStatusIncomplete), null, false);
			while (re.hasNextElement()) {
				activityVector.addElement(Activity.recordToActivity(re
						.nextRecord()));
			}
		} catch (Exception e) {
		    e.printStackTrace();
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
	
	public void loadCurrentActivityPlan() {
		RecordStore rs = null;
		RecordEnumeration re = null;
		ActivityRecordFilter rf = new ActivityRecordFilter(
				ActivityRecordFilter.filterByStatusIncomplete);
		activityVector = new Vector();
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(rf, null, false);
			while (re.hasNextElement()) {
				activityVector.addElement(Activity.recordToActivity(re
						.nextRecord()));
				System.gc();
			}
			rf = null;
			re = null;
			rs = null;
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public void loadCompletedActivityPlan() {
		RecordStore rs = null;
		RecordEnumeration re = null;
		ActivityRecordFilter rf = new ActivityRecordFilter(
				ActivityRecordFilter.filterByStatusComplete);
		activityVector = new Vector();
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(rf, null, false);
			while (re.hasNextElement()) {
				activityVector.addElement(Activity.recordToActivity(re
						.nextRecord()));
				System.gc();
			}
			rf = null;
			re = null;
			rs = null;
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public void updateActivityStatus(Activity activity) {
		RecordStore rs = null;
		RecordEnumeration re = null;
		ActivityRecordFilter rf = new ActivityRecordFilter(
				ActivityRecordFilter.filterByProgStageInstId);
		rf.setProgStageInstId(dhisMIDlet.getSelectedActivity().getTask()
				.getProgStageInstId());
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			if (activity != null) {
				re = rs.enumerateRecords(rf, null, true);
				if (re.numRecords() == 1) {
					int id = re.nextRecordId();
					byte[] activityBytes = Activity.activityToRecord(dhisMIDlet
							.getSelectedActivity());
					rs.setRecord(id, activityBytes, 0, activityBytes.length);
				}
			}

			rf = null;
			re = null;
			rs = null;
			System.gc();
		} catch (RecordStoreException e) {
			e.printStackTrace();
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

	public void run() {
		// if (task.equalsIgnoreCase(LOAD_ALL_ACTIVITYPLAN)) {
		// loadAll();
		// dhisMIDlet.loadActivityPlan(getActivityVector());
		if (task.equalsIgnoreCase(LOAD_COMPLETED_ACTIVITYPLAN)) {
			loadCompletedActivityPlan();
			dhisMIDlet.loadActivityPlan(getActivityVector());
		} else if (task.equalsIgnoreCase(LOAD_CURRENT_ACTIVITYPLAN)) {
			loadCurrentActivityPlan();
			 dhisMIDlet.loadActivityPlan(getActivityVector());
			// } else if (task.equalsIgnoreCase(UPDATE_ACTIVITY)) {
			// updateActivity();
			//
			// }
			// loadAll();
			// dhisMIDlet.loadActivityPlan(getActivityVector());
		}
		System.gc();
	}
}
