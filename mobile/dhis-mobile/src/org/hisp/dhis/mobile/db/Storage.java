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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.hisp.dhis.mobile.model.AbstractModel;
import org.hisp.dhis.mobile.model.Activity;
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

    public static void storeActivities( Vector activitiesVector )
    {
        ModelRecordStore modelRecordStore = new ModelRecordStore( ModelRecordStore.ACTIVITY_DB );
        Enumeration activities = activitiesVector.elements();
        Activity activity = null;
        int i = 0;
        while ( activities.hasMoreElements() )
        {
            try
            {
                activity = (Activity) activities.nextElement();
                modelRecordStore.addRecord( Activity.activityToRecord( activity ) );
                i += 1;
            }
            catch ( RecordStoreException rse )
            {
            }
        }
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

    public static void saveOrgUnit( OrgUnit orgUnit )
    {
        // ModelRecordStore modelRecordStore;
        // try
        // {
        // modelRecordStore = new ModelRecordStore( ModelRecordStore.ORGUNIT_DB
        // );
        // modelRecordStore.addRecord( OrgUnit.orgUnitToRecord( orgUnit ) );
        // }
        // catch ( RecordStoreException rse )
        // {
        // }
        OrgUnitRecordStore orgUnitStore = new OrgUnitRecordStore();
        orgUnitStore.save( orgUnit );
        orgUnitStore = null;
    }

    public static void saveUser( User user )
   
    {
        clear(ModelRecordStore.USER_DB);
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
               System.out.println(e.getMessage());
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
