/*
 * Copyright (c) 2004-2008, University of Oslo
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
package org.hisp.dhis.integration.rims.util;

import java.util.Calendar;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_Orgunit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.YearlyPeriodType;

/**
 * Simple class to store details of errors during import or export.
 * 
 * @author Leif Arne Storset
 * @version $Id$
 */
public class ImportExportError
{
    private Period period;

    public ImportExportError( RIMS_Mapping_DataElement mappingDataElement,
        RIMS_Mapping_Orgunit orgUnit, String reason )
    {
        this.mappingDataElement = mappingDataElement;
        this.reason = reason;
        this.mappingOrgUnit = orgUnit;
    }

    public ImportExportError( DataElement dataElement,
        RIMS_Mapping_Orgunit mappingOrgUnit, String reason )
    {
        this.dataElement = dataElement;
        this.reason = reason;
        this.mappingOrgUnit = mappingOrgUnit;
    }
    public ImportExportError( DataElement dataElement,
        RIMS_Mapping_Orgunit mappingOrgUnit, Period period, String reason )
    {
        this.dataElement = dataElement;
        this.reason = reason;
        this.mappingOrgUnit = mappingOrgUnit;
        this.period = period;
    }

    public ImportExportError( DataElement dataElement,
        RIMS_Mapping_Orgunit mappingOrgUnit, Period period, Exception e )
    {
        this.dataElement = dataElement;
        this.reason = e.getClass().getSimpleName() + ": " + e.getMessage();
        this.mappingOrgUnit = mappingOrgUnit;
        this.period = period;
    }

    public ImportExportError( RIMS_Mapping_DataElement mappingDataElement,
        RIMS_Mapping_Orgunit mappingOrgUnit, Period period, Exception e )
    {
        this.mappingDataElement = mappingDataElement;
        this.reason = e.getClass().getSimpleName() + ": " + e.getMessage();
        this.mappingOrgUnit = mappingOrgUnit;
        this.period = period;
    }
    
    public ImportExportError( RIMS_Mapping_DataElement mappingDataElement,
        RIMS_Mapping_Orgunit mappingOrgUnit, Period period, String reason )
    {
        this.mappingDataElement = mappingDataElement;
        this.reason = reason;
        this.mappingOrgUnit = mappingOrgUnit;
        this.period = period;
    }

    private RIMS_Mapping_DataElement mappingDataElement;
    private RIMS_Mapping_Orgunit mappingOrgUnit;
    private DataElement dataElement;
    private String reason;

    public DataElement getDataElement()
    {
        return dataElement;
    }
    public void setDataElement( DataElement de )
    {
        this.dataElement = de;
    }
    public RIMS_Mapping_DataElement getMappingDataElement()
    {
        return mappingDataElement;
    }
    public void setMappingDataElement( RIMS_Mapping_DataElement mappingDe )
    {
        this.mappingDataElement = mappingDe;
    }
    public RIMS_Mapping_Orgunit getMappingOrgUnit()
    {
        return mappingOrgUnit;
    }
    public void setMappingOrgUnit( RIMS_Mapping_Orgunit mappingOu )
    {
        this.mappingOrgUnit = mappingOu;
    }
    public String getReason()
    {
        return reason;
    }
    public void setReason( String reason )
    {
        this.reason = reason;
    }

    public String getPeriod()
    {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime( period.getStartDate() );
        if ( period.getPeriodType() instanceof YearlyPeriodType )
        {
            return startDate.get( Calendar.YEAR ) +"";
        }
        else if ( period.getPeriodType() instanceof MonthlyPeriodType )
        {
            return (startDate.get( Calendar.MONTH ) + 1) +"/"+ 
                   startDate.get( Calendar.YEAR );
        }
        return startDate.toString();
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }
}
