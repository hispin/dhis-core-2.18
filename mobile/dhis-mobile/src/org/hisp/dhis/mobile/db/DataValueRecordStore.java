package org.hisp.dhis.mobile.db;

import java.util.Hashtable;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.DataValue;

public class DataValueRecordStore
{

    private Hashtable dataValueRecordID;

    private String dataBaseName;

    public DataValueRecordStore()
    {
        this.dataBaseName = ModelRecordStore.DATAVALUE_DB;
    }

    public void saveDataValue( DataValue dataValue )
    {
        ModelRecordStore modelRecordStore;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.DATAVALUE_DB );
            modelRecordStore.addRecord( DataValue.dataValueToRecord( dataValue ) );
        }
        catch ( RecordStoreException rse )
        {
            System.out.println( rse.getMessage() );
        }
    }

    public Hashtable loadDataValues( Activity activity )
    {
        Hashtable dataValuesTable = new Hashtable();
        RecordStore rs = null;
        RecordEnumeration re = null;
        try
        {
            rs = RecordStore.openRecordStore( ModelRecordStore.DATAVALUE_DB, true );
            re = rs.enumerateRecords( null, null, false );
            while ( re.hasNextElement() )
            {
                DataValue dataValue = DataValue.recordToDataValue( re.nextRecord() );
                if ( dataValue.getProgramInstanceId() == activity.getTask().getProgStageInstId() )
                {
                    dataValuesTable.put( String.valueOf( dataValue.getDataElementId() ), dataValue.getValue() );
                }
            }
            re = null;
            rs = null;
            return dataValuesTable;
        }
        catch ( RecordStoreException rse )
        {
            rse.printStackTrace();
            return null;
        }
    }
    
    

}
