package org.hisp.dhis.mobile.db;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.DataValue;
import org.hisp.dhis.mobile.model.DataElement;
import org.hisp.dhis.mobile.util.StringUtil;

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

    public Vector loadAllDataValues(){
        Vector dataValuesTable = new Vector();
        Hashtable dataElmntsTable = new Hashtable();
        RecordStore rs = null;
        RecordStore rs2 = null;
        RecordEnumeration re = null;
        RecordEnumeration re2 = null;
        DataElement dataElement = null;
        Date date = new Date();
        try
        {
            rs2 = RecordStore.openRecordStore( ModelRecordStore.DATAELEMENT_DB, true );
            re2 = rs2.enumerateRecords( null, null, false );
            while ( re2.hasNextElement() )
            {
                dataElement = DataElement.recordToDataElement( re2.nextRecord() );
                dataElmntsTable.put( String.valueOf(dataElement.getId()), String.valueOf(dataElement.getType()) );
            }
            
            rs = RecordStore.openRecordStore( ModelRecordStore.DATAVALUE_DB, true );
            re = rs.enumerateRecords( null, null, false );
            while ( re.hasNextElement() )
            {
                DataValue dataValue = DataValue.recordToDataValue( re.nextRecord() );
                if(dataElmntsTable.get( String.valueOf(dataValue.getDataElementId())).equals( "3")){
                    System.out.println("date and type");
                    date.setTime(Long.parseLong(dataValue.getValue()));
                    dataValue.setValue( StringUtil.getStringFromDate(date ));
                    dataValuesTable.addElement( dataValue );
                }else{
                    System.out.println("other");
                    dataValuesTable.addElement( dataValue );
                }
                System.out.println("temporary loaded datavalue:"+dataValuesTable.size());
            }
            re = null;
            rs = null;
            System.out.println("Loaded datavalue:"+dataValuesTable.size());
            return dataValuesTable;
        }
        catch ( RecordStoreException rse )
        {
            rse.printStackTrace();
            return null;
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
                    dataValuesTable.put( String.valueOf( dataValue.getDataElementId() ), dataValue );
                }                
            }
            re = null;
            rs = null;
            System.out.println("Loaded datavalue of certain activity:"+dataValuesTable.size());
            return dataValuesTable;
        }
        catch ( RecordStoreException rse )
        {
            rse.printStackTrace();
            return null;
        }
    }
    
    

}
