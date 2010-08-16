/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.cbhis.db;

import javax.microedition.rms.RecordStoreException;

import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.hisp.dhis.cbhis.model.AbstractModel;
import org.hisp.dhis.cbhis.model.DataElement;

/**
 *
 * @author abyotag_adm
 */
public class ModelRecordStore {

    public static final String FORM_DB = "FORM";
    public static final String DATAELEMENT_DB = "DATAELEMENT";   
    
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
    
    public void AddRecord(byte[] rec) throws RecordStoreException
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