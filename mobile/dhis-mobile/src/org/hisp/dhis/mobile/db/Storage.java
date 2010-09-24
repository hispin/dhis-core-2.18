/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.mobile.db;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.hisp.dhis.mobile.model.AbstractModel;
import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.DataValue;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.ProgramStageForm;
import org.hisp.dhis.mobile.model.User;

public class Storage
{
    // Get all Form from RMS
    public static Vector getAllForm()
    {
        ModelRecordStore modelRecordStore = null;
        Vector downloadedFormVector = null;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.FORM_DB );
            downloadedFormVector = modelRecordStore.getAllRecord();
        }
        catch ( RecordStoreException rse )
        {

        }
        return downloadedFormVector;
    }

    public static ProgramStageForm fetchForm( int formId )
    {
        ModelRecordStore modelRecordStore = null;
        ProgramStageForm frm = null;

        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.FORM_DB );
            byte rec[] = modelRecordStore.getRecord( formId );
            if ( rec != null )
                frm = ProgramStageForm.recordToProgramStageForm( rec );
        }
        catch ( RecordStoreException rse )
        {
        }

        return frm;
    }

    public static AbstractModel getForm( int index )
    {
        return (AbstractModel) getAllForm().elementAt( index );
    }

    public static void storeActivities( Vector activityVector )
    {
        clear( ModelRecordStore.ACTIVITY_DB );
        ActivityRecordStore activityRecordStore = new ActivityRecordStore();
        activityRecordStore.setActivityVector( activityVector );
        activityRecordStore.save();
        activityRecordStore = null;
    }

    public static Vector loadActivities()
    {
        ActivityRecordStore activityRecordStore = new ActivityRecordStore();
        return activityRecordStore.loadAll();

    }

    public static void storeForm( ProgramStageForm programStageForm )
    {
        ModelRecordStore modelRecordStore;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.FORM_DB );
            modelRecordStore.addRecord( ProgramStageForm.programStageFormToRecord( programStageForm ) );
        }
        catch ( RecordStoreException rse )
        {
        }

        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.DATAELEMENT_DB );
            modelRecordStore.AddDataElementRecords( programStageForm.getDataElements() );
        }
        catch ( RecordStoreException rse )
        {
        }
    }

    public static void storeForms( Vector programStageFormVector )
    {
        clear( ModelRecordStore.FORM_DB );
        clear( ModelRecordStore.DATAELEMENT_DB );
        ModelRecordStore modelRecordStore;
        for ( int i = 0; i < programStageFormVector.size(); i++ )
        {
            try
            {
                modelRecordStore = new ModelRecordStore( ModelRecordStore.FORM_DB );
                modelRecordStore.addRecord( ProgramStageForm
                    .programStageFormToRecord( (ProgramStageForm) programStageFormVector.elementAt( i ) ) );
                modelRecordStore = new ModelRecordStore( ModelRecordStore.DATAELEMENT_DB );
                modelRecordStore.AddDataElementRecords( ((ProgramStageForm) programStageFormVector.elementAt( i ))
                    .getDataElements() );
            }
            catch ( RecordStoreException rse )
            {
                System.out.println( rse.getMessage() );
            }
        }
    }

    public static Vector loadForms()
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        Vector formsVector = new Vector();
        try
        {
            rs = RecordStore.openRecordStore( ModelRecordStore.FORM_DB, true );
            re = rs.enumerateRecords( null, null, false );
            while ( re.hasNextElement() )
            {
                formsVector.addElement( ProgramStageForm.recordToProgramStageForm( re.nextRecord() ) );
            }
            re = null;
            rs = null;
            return formsVector;
        }
        catch ( RecordStoreException rse )
        {
            rse.printStackTrace();
            return null;
        }
    }

    public static void storeDataValue( DataValue dataValue )
        throws RecordStoreException
    {
        ModelRecordStore modelRecordStore;
        modelRecordStore = new ModelRecordStore( ModelRecordStore.DATAVALUE_DB );
        modelRecordStore.addRecord( DataValue.dataValueToRecord( dataValue ) );
    }
    
    public static Vector loadAllDataValues(  )
    {
        DataValueRecordStore dataValueRs = new DataValueRecordStore();
        return dataValueRs.loadAllDataValues();
    }
    
    public static Hashtable loadDataValues( Activity activity )
    {
        DataValueRecordStore dataValueRs = new DataValueRecordStore();
        return dataValueRs.loadDataValues( activity );
//        Hashtable dataValuesTable = new Hashtable();
//        RecordStore rs = null;
//        RecordEnumeration re = null;
//        try
//        {
//            rs = RecordStore.openRecordStore( ModelRecordStore.DATAVALUE_DB, true );
//            re = rs.enumerateRecords( null, null, false );
//            while ( re.hasNextElement() )
//            {
//                DataValue dataValue = DataValue.recordToDataValue( re.nextRecord() );
//                if ( dataValue.getProgramInstanceId() == activity.getTask().getProgStageInstId() )
//                {
//                    dataValuesTable.put( String.valueOf( dataValue.getDataElementId() ), dataValue );
//                }
//            }
//            re = null;
//            rs = null;
//            return dataValuesTable;
//        }
//        catch ( RecordStoreException rse )
//        {
//            rse.printStackTrace();
//            return null;
//        }
    }

    public static void updateDataValue( Activity activity, DataValue newDataValue )
        throws RecordStoreException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;

        DataValueFilter filter = new DataValueFilter();
        filter.setDataElementID( newDataValue.getDataElementId() );
        filter.setProStageInstanceID( activity.getTask().getProgStageInstId() );
        rs = RecordStore.openRecordStore( ModelRecordStore.DATAVALUE_DB, true );
        re = rs.enumerateRecords( filter, null, false );
        while ( re.hasNextElement() )
        {
            if ( re.numRecords() == 1 )
            {
                int id = re.nextRecordId();
                byte[] data = DataValue.dataValueToRecord( newDataValue );
                rs.setRecord( id, data, 0, data.length );
            }
        }
        re = null;
        rs = null;

    }

    public static void deleteDataValue( Activity activity, DataValue newDataValue )
        throws RecordStoreException
    {
        RecordStore rs = null;
        RecordEnumeration re = null;

        DataValueFilter filter = new DataValueFilter();
        filter.setDataElementID( newDataValue.getDataElementId() );
        filter.setProStageInstanceID( activity.getTask().getProgStageInstId() );
        rs = RecordStore.openRecordStore( ModelRecordStore.DATAVALUE_DB, true );
        re = rs.enumerateRecords( filter, null, false );
        while ( re.hasNextElement() )
        {
            if ( re.numRecords() == 1 )
            {
                int id = re.nextRecordId();
                rs.deleteRecord( id );
            }
        }
        filter = null;
        re = null;
        rs = null;

    }

    public static void saveOrgUnit( OrgUnit orgUnit )
    {
        clear( ModelRecordStore.ORGUNIT_DB );
        ModelRecordStore modelRecordStore;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.ORGUNIT_DB );
            modelRecordStore.addRecord( OrgUnit.orgUnitToRecord( orgUnit ) );
        }
        catch ( RecordStoreException rse )
        {
        }
    }

    public static OrgUnit loadOrgUnit()
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        OrgUnit orgUnit = null;
        try
        {
            rs = RecordStore.openRecordStore( ModelRecordStore.ORGUNIT_DB, true );
            re = rs.enumerateRecords( null, null, false );
            while ( re.hasNextElement() )
            {
                orgUnit = OrgUnit.recordToOrgUnit( re.nextRecord() );
            }
            return orgUnit;
        }
        catch ( RecordStoreException rse )
        {
            rse.printStackTrace();
            return null;
        }
    }

    public static void saveUser( User user )

    {
        clear( ModelRecordStore.USER_DB );
        ModelRecordStore modelRecordStore;
        try
        {
            modelRecordStore = new ModelRecordStore( ModelRecordStore.USER_DB );
            modelRecordStore.addRecord( User.userToRecord( user ) );
        }
        catch ( RecordStoreException rse )
        {
        }
    }

    public static User loadUser()
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        User user = null;
        try
        {
            rs = RecordStore.openRecordStore( ModelRecordStore.USER_DB, true );
            re = rs.enumerateRecords( null, null, false );
            while ( re.hasNextElement() )
            {
                user = User.recordToUser( re.nextRecord() );
            }
            return user;
        }
        catch ( RecordStoreException rse )
        {
            rse.printStackTrace();
            return null;
        }
    }

    public static void clear( String dbName )
    {
        RecordStore rs = null;
        RecordEnumeration re = null;
        try
        {
            rs = RecordStore.openRecordStore( dbName, true );
            re = rs.enumerateRecords( null, null, false );
            int id;
            while ( re.hasNextElement() )
            {
                id = re.nextRecordId();
                rs.deleteRecord( id );
            }
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
        finally
        {
            if ( re != null )
                re.destroy();
            if ( rs != null )
                try
                {
                    rs.closeRecordStore();
                }
                catch ( RecordStoreNotOpenException e )
                {
                    e.printStackTrace();
                }
                catch ( RecordStoreException e )
                {
                    e.printStackTrace();
                }
        }
    }
}
