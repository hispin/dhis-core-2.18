package org.hisp.dhis.mobile.db;

import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.model.AbstractModel;
import org.hisp.dhis.mobile.model.DataElement;

public class ModelRecordStore {

    public static final String FORM_DB = "FORM";
    public static final String ORGUNIT_DB = "ORGUNIT";
    public static final String DATAELEMENT_DB = "DATAELEMENT";
    public static final String ACTIVITY_DB = "ACTIVITY";   
    
    private String dbName;   

    public ModelRecordStore(String dbName){        
        this.dbName = dbName;        
    }

   public byte[] getRecord( int id ) throws RecordStoreException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        byte nextRec[] = null;
        try {
            rs = RecordStore.openRecordStore(dbName, true);
            re = rs.enumerateRecords(null, null, false);
            while (re.hasNextElement()) {
                nextRec = re.nextRecord();
                if( AbstractModel.recordToAbstractModel( nextRec ).getId() == id)
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

    // The get all record method
	public Vector getAllRecord() throws RecordStoreException {
		RecordStore rs = null;
		RecordEnumeration re = null;
		Vector vector = new Vector();
		byte nextRec[] = null;
		try {
			rs = RecordStore.openRecordStore(dbName, true);
			re = rs.enumerateRecords(null, null, false);
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
    
    public void addRecord(byte[] rec) throws RecordStoreException
    {
        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(dbName, true);
            rs.addRecord(rec, 0, rec.length);
        } finally {
            if (rs != null)
                rs.closeRecordStore();
        }
    }

    //need to do this is in a better way...
    public void AddDataElementRecords(Vector des) throws RecordStoreException
    {
        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(dbName, true);

            for(int i=0; i<des.size(); i++)
            {
                DataElement de = (DataElement)des.elementAt(i);
                byte[] rec = DataElement.dataElementToRecord(de);
                rs.addRecord(rec, 0, rec.length);
            }

        } finally {
            if (rs != null)
                rs.closeRecordStore();
        }
    }    
}