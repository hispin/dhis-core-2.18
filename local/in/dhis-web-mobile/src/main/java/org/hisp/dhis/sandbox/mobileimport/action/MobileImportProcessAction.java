package org.hisp.dhis.sandbox.mobileimport.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.sandbox.mobileimport.api.MobileImportParameters;
import org.hisp.dhis.sandbox.mobileimport.api.MobileImportService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

public class MobileImportProcessAction
    implements Action
{

    // private static final Log LOG = LogFactory.getLog(
    // MobileImportProcessAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MobileImportService mobileImportService;

    public void setMobileImportService( MobileImportService mobileImportService )
    {
        this.mobileImportService = mobileImportService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private String importStatus;

    public String getImportStatus()
    {
        return importStatus;
    }

    private String statusMessage;

    public String getStatusMessage()
    {
        return statusMessage;
    }

    private String storedBy;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        importStatus = "";

        List<String> fileNames = new ArrayList<String>( mobileImportService.getImportFiles() );

        System.out.println( fileNames.size() );

        for ( String importFile : fileNames )
        {

            MobileImportParameters mobImportParameters = mobileImportService.getParametersFromXML( importFile );

            if ( mobImportParameters == null )
            {
                // LOG.debug( importFile +
                // " Import File is not Propely Formated First" );

                System.out.println( importFile + " Import File is not Propely Formated First" );

                importStatus += "<br>" + new Date() + ": " + importFile + " Import File is not Propely Formated.";

                mobileImportService.moveFailedFile( importFile );

                continue;
            }

            User curUser = mobileImportService.getUserInfo( mobImportParameters.getMobileNumber() );

            UserCredentials userCredentials = userStore.getUserCredentials( curUser );

            if ( (userCredentials != null)
                && (mobImportParameters.getMobileNumber().equals( curUser.getPhoneNumber() )) )
            {
                storedBy = userCredentials.getUsername();
            }
            else
            {
                // LOG.debug(
                // " Import File Contains Unrecognised Phone Numbers : " +
                // mobImportParameters.getMobileNumber() );
                System.out.println( " Import File Contains Unrecognised Phone Numbers : "
                    + mobImportParameters.getMobileNumber() );
                importStatus += "<br><font color=red><b>Import File Contains Unrecognised Phone Numbers :"
                    + mobImportParameters.getMobileNumber() + ".</b></font>";

                mobileImportService.moveFailedFile( importFile );
                continue;
                // mobileImportService.moveFailedFile( importFile );
            }

            List<Source> sources = new ArrayList<Source>( curUser.getOrganisationUnits() );

            if ( sources == null || sources.size() <= 0 )
            {
                System.out.println( "Source is NULL" );

                importStatus += "<br><font color=red><b>No User Exist Who Registered Phone No. Is :"
                    + mobImportParameters.getMobileNumber() + ".</b></font>";

                mobileImportService.moveFailedFile( importFile );
                // return SUCCESS;
                continue;
            }
            Source source = sources.get( 0 );

            Period period = mobileImportService.getPeriodInfo( mobImportParameters.getStartDate() );

            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-mm-dd" );

            Date timeStamp = dateFormat.parse( mobImportParameters.getSmsTime() );

            Map<String, Integer> dataValueMap = new HashMap<String, Integer>( mobImportParameters.getDataValues() );

            if ( dataValueMap == null || dataValueMap.size() <= 0 )
            {
                System.out.println( "dataValue map is null" );
            }
            else if ( source == null )
            {
                System.out.println( "source is null" );
            }
            else if ( period == null )
            {
                System.out.println( "period is null" );
            }
            else if ( timeStamp == null )
            {
                System.out.println( "timeStamp is null" );
            }

            if ( source == null || period == null || timeStamp == null || dataValueMap == null
                || dataValueMap.size() <= 0 )
            {

                System.out.println( importFile + " Import File is not Propely Formated" );

                importStatus += "<br>" + new Date() + ": " + importFile + " Import File is not Propely Formated.<br>";

                mobileImportService.moveFailedFile( importFile );

                continue;
            }

            Set<String> keys = dataValueMap.keySet();

            for ( String key : keys )
            {
                // DataElement dataElement = dataElementService.getDataElement(
                // key );

                String parts[] = key.split( "\\." );

                System.out.println( key + "\t : \t" + parts.length );

                String deStr = parts[0];

                String optStr = parts[1];

                String value = String.valueOf( dataValueMap.get( key ) );

                System.out.println( "OPTION ID IS : \t" + optStr );

                DataElement dataElement = dataElementService.getDataElement( Integer.valueOf( deStr ) );

                DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();

                optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                    .valueOf( optStr ) );

                DataValue dataValue = dataValueService.getDataValue( source, dataElement, period, optionCombo );

                if ( dataValue == null )
                {
                    if ( value != null )
                    {
                        dataValue = new DataValue( dataElement, period, source, value, storedBy, timeStamp, null,
                            optionCombo );

                        dataValueService.addDataValue( dataValue );
                    }
                }
                else
                {
                    dataValue.setValue( value );

                    dataValue.setTimestamp( timeStamp );

                    dataValue.setStoredBy( storedBy );

                    dataValueService.updateDataValue( dataValue );
                }
            }

            System.out.println( importFile + " is Imported Successfully" );

            importStatus += "<br>" + new Date() + ": " + importFile + " is Imported Successfully.";

            mobileImportService.moveImportedFile( importFile );
        }

        return SUCCESS;
    }
}
