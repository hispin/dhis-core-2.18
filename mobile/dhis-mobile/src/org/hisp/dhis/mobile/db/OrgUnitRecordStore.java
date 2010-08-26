package org.hisp.dhis.mobile.db;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.model.AbstractModel;
import org.hisp.dhis.mobile.model.OrgUnit;

/**
 * @author Tran Ng Minh Luan
 * 
 */
public class OrgUnitRecordStore {

	private String dbName;

	private Vector orgUnitsVector;

	// Constructor
	public OrgUnitRecordStore() {

	}

	// Getter & Setter
	public void setOrgUnitsVector(Vector orgUnitsVector) {
		this.orgUnitsVector = orgUnitsVector;
	}
	
	// Getter & Setter

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	// Supportive Methods
	public void save() {
		clear();
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			if (orgUnitsVector != null && orgUnitsVector.size() > 0) {
				Enumeration orgUnits = orgUnitsVector.elements();
				byte[] orgUnitByte;
				while (orgUnits.hasMoreElements()) {
					orgUnitByte = OrgUnit.orgUnitToRecord((OrgUnit) orgUnits
							.nextElement());
					rs.addRecord(orgUnitByte, 0, orgUnitByte.length);
				}
				orgUnitByte = null;
			}
		} catch (RecordStoreException e) {
			System.out.println(e.getMessage());
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

	public void update(AbstractModel model) {
		RecordStore rs = null;
		RecordEnumeration re = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			RecordFilter rsFilter = new AbstractModelRecordFilter(model);
			re = rs.enumerateRecords(rsFilter, null, false);
			Integer id;
			byte[] orgUnitByte;
			while (re.hasNextElement()) {
				id = new Integer(re.nextRecordId());
				orgUnitByte = OrgUnit.orgUnitToRecord((OrgUnit) model);
				rs.setRecord(id.intValue(), orgUnitByte, 0, orgUnitByte.length);

			}
			// release variable
			orgUnitByte = null;
			id = null;
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

}
