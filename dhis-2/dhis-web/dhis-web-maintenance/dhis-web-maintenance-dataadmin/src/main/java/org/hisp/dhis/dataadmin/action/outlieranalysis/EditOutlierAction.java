package org.hisp.dhis.dataadmin.action.outlieranalysis;

/*
 * Copyright (c) 2004-${year}, University of Oslo
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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;

import java.util.Date;
import com.opensymphony.xwork2.Action;

/**
 * Edit an outlier value identified by a tuple of: (sourceId, dataElementId,
 * periodId)
 * 
 * @author Jon Moen Drange
 * 
 */
public class EditOutlierAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    private String organisationUnitId;

    public void setOrganisationUnitId( String organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private String dataElementId;

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Period period = null;
        DataElement dataElement = null;
        OrganisationUnit organisationUnit = null;

        try
        {
            int i = Integer.parseInt( periodId );

            period = periodService.getPeriod( i );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            statusCode = 1;
            message = "invalid data value";
            return ERROR;
        }

        try
        {
            int i = Integer.parseInt( dataElementId );

            dataElement = dataElementService.getDataElement( i );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            statusCode = 2;
            message = "invalid data value";
            return ERROR;
        }

        try
        {
            int i = Integer.parseInt( organisationUnitId );

            organisationUnit = organisationUnitService.getOrganisationUnit( i );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            statusCode = 3;
            message = "invalid data value";
            return ERROR;
        }

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        if ( value != null )
        {
            value = value.trim();
        }

        DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period );

        if ( dataValue == null )
        {
            statusCode = 4;
            message = "data value does not exist";
            return ERROR;
        }
        if ( !dataValue.getDataElement().getType().equals( "int" ) )
        {
            // can only find outlier values for data elements of where
            // type="int", and therefore, only update such values
            
            statusCode = 5;
            message = "invalid data value";
            return ERROR;
        }

        String storedBy = currentUserService.getCurrentUsername();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        dataValue.setValue( value );
        dataValue.setStoredBy( storedBy );
        dataValue.setTimestamp( new Date() );

        dataValueService.updateDataValue( dataValue );

        statusCode = 0;
        message = ""; // "success" - no message set

        return SUCCESS;
    }
}
