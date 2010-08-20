/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.cbhis.db;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author abyotag_adm
 */
public class SettingsRectordStore {
    
    private String dbName;
    
    private Hashtable hashtable;

    public SettingsRectordStore(String dbName) throws RecordStoreException {
        this.dbName = dbName;
        hashtable = new Hashtable();
        load();
    }

    public String get(String setting) {

        String value = "";        
        if( hashtable.containsKey(setting))
            value = (String)hashtable.get(setting);

        return value;
    }

    public void put(String setting, String value) {
        if (value == null)
            value = "";
        hashtable.put(setting, value);
    }

    private void load() throws RecordStoreException {
        RecordStore rs = null;
        RecordEnumeration re = null;
        try {
            rs = RecordStore.openRecordStore(dbName, true);
            re = rs.enumerateRecords(null, null, false);
            while (re.hasNextElement()) {                
                String settingRecord = new String( re.nextRecord() );
                
                int index = settingRecord.indexOf('|');

                String setting = settingRecord.substring(0, index);
                String value = settingRecord.substring(index + 1);
                put(setting, value);
            }
        } finally {
            if (re != null)
                re.destroy();
            if (rs != null)
                rs.closeRecordStore();
        }
    }
    
    public void save() throws RecordStoreException {
        RecordStore rs = null;
        RecordEnumeration re = null;
        try {
            rs = RecordStore.openRecordStore(dbName, true);
            re = rs.enumerateRecords(null, null, false);           

            //clean and save
            while (re.hasNextElement()) {                
                rs.deleteRecord( re.nextRecordId() );
            }
            
            Enumeration keys = hashtable.keys();
            while (keys.hasMoreElements()) {
                String setting = (String) keys.nextElement();
                String value = get(setting);
                String settingValue = setting + "|" + value;
                
                byte[] raw = settingValue.getBytes();
                rs.addRecord(raw, 0, raw.length);
            }
        } finally {
            if (re != null)
                re.destroy();
            if (rs != null)
                rs.closeRecordStore();
        }
    }
}
