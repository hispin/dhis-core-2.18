package org.hisp.dhis.mobile.reporting.db;

import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.reporting.model.AbstractModel;

public class ValueRecordStore {

	public static final String VALUE_DB = "DATA_VALUE";

	private String dbName;

	public ValueRecordStore(String dbName) {
		this.dbName = dbName;
	}

	public byte[] getRecord(int id) throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		byte nextRec[] = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			AbstractModelComparator comparator = new AbstractModelComparator(
					AbstractModelComparator.SORT_BY_ID);
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
			AbstractModelComparator comparator = new AbstractModelComparator(
					AbstractModelComparator.SORT_BY_ID);
			re = rs.enumerateRecords(filter, comparator, false);
			while (re.hasNextElement()) {
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
			AbstractModelComparator comparator = new AbstractModelComparator(
					AbstractModelComparator.SORT_BY_ID);
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
			AbstractModelComparator comparator = new AbstractModelComparator(
					AbstractModelComparator.SORT_BY_NAME);
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
			AbstractModelComparator comparator = new AbstractModelComparator(
					AbstractModelComparator.SORT_BY_ID);
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
}