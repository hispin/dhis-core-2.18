/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.mobile.reporting.db;

import javax.microedition.rms.RecordStoreException;

import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.reporting.model.AbstractModel;

/**
 * 
 * @author abyotag_adm
 */
public class ModelRecordStore {

	public static final String DATASET_DB = "DATASET";	
	public static final String PROGRAM_DB = "PROGRAM";
	public static final String PROGRAM_STAGE_DB = "PROGRAMSTAGE";
	public static final String ACTIVITY_PLAN_DB = "ACTIVITYPLAN";
	public static final String ACTIVITY_DB = "ACTIVITY";

	private String dbName;

	public ModelRecordStore(String dbName) {
		this.dbName = dbName;
	}

	public byte[] getRecord(int id) throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		byte nextRec[] = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			AbstractModelComparator comparator = new AbstractModelComparator(AbstractModelComparator.SORT_BY_ID);
			re = rs.enumerateRecords(null, comparator, false);
			while (re.hasNextElement()) {
				nextRec = re.nextRecord();
				if (AbstractModel.recordToAbstractModel(nextRec).getId() == id)
					return nextRec;
			}
		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				rs.closeRecordStore();
		}
		return null;
	}
	
	public byte[] getRecord(AbstractModel model) throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;		
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			AbstractModelFilter filter = new AbstractModelFilter(model);
			AbstractModelComparator comparator = new AbstractModelComparator(AbstractModelComparator.SORT_BY_ID);
			re = rs.enumerateRecords(filter, comparator, false);
			while( re.hasNextElement() ) {
				return re.nextRecord();				
			}
		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				rs.closeRecordStore();
		}
		return null;
	}

	public void AddRecord(byte[] rec) throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			AbstractModel model = AbstractModel.recordToAbstractModel(rec);
			AbstractModelFilter filter = new AbstractModelFilter(model);
			AbstractModelComparator comparator = new AbstractModelComparator(AbstractModelComparator.SORT_BY_ID);
			re = rs.enumerateRecords(filter, comparator, false);
			while (re.hasNextElement()) {
				int id = re.nextRecordId();
				rs.deleteRecord(id);
			}
			rs.addRecord(rec, 0, rec.length);
		} finally {
			if (rs != null)
				rs.closeRecordStore();
		}
	}

	// The get all record method
	public Vector getAllRecord() throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		Vector vector = new Vector();
		byte nextRec[] = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			AbstractModelComparator comparator = new AbstractModelComparator(AbstractModelComparator.SORT_BY_NAME);
			re = rs.enumerateRecords(null, comparator, false);
			while (re.hasNextElement()) {
				nextRec = re.nextRecord();
				vector.addElement((AbstractModel.recordToAbstractModel(nextRec)));
			}
		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				rs.closeRecordStore();
		}
		return vector;
	}

	public void deleteRecord(AbstractModel model) throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			AbstractModelFilter filter = new AbstractModelFilter(model);
			AbstractModelComparator comparator = new AbstractModelComparator(AbstractModelComparator.SORT_BY_ID);
			re = rs.enumerateRecords(filter, comparator, false);
			while (re.hasNextElement()) {
				int id = re.nextRecordId();
				rs.deleteRecord(id);
			}
		} finally {
			if (re != null)
				re.destroy();
			if (rs != null)
				rs.closeRecordStore();
		}
	}
	
	public static void clear(String db) {
		RecordStore rs = null;
		RecordEnumeration re = null;
		try {
			rs = RecordStore.openRecordStore(db, true);
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
}