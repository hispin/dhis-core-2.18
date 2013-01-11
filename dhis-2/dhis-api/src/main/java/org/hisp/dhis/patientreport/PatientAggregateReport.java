/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patientreport;

import java.util.List;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.user.User;

/**
 * @author Chau Thu Tran
 * 
 * @version PatientAggregateReport.java 11:24:11 AM Jan 10, 2013 $
 */
public class PatientAggregateReport
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 3261142704777097572L;

    public static final int POSITION_ROW_ORGUNIT_COLUMN_PERIOD = 1;

    public static final int POSITION_ROW_PERIOD_COLUMN_ORGUNIT = 2;

    public static final int POSITION_ROW_ORGUNIT_ROW_PERIOD = 3;

    public static final int POSITION_ROW_PERIOD = 4;

    public static final int POSITION_ROW_ORGUNIT = 5;

    public static final int POSITION_ROW_PERIOD_COLUMN_DATA = 6;

    public static final int POSITION_ROW_ORGUNIT_COLUMN_DATA = 7;

    public static final int POSITION_ROW_DATA = 8;
    
    public static final String FILTER_ORGANISATION_UNIT = "ou";

    public static final String FILTER_PERIOD = "pd";

    public static final String FILTER_DATA = "de";

    private List<OrganisationUnit> organisationUnits;

    private List<Period> periods;

    private List<ProgramStageDataElement> dataElements;

    private int position;

    // Option

    private Integer limitRecords;

    // User created

    private User user;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientAggregateReport()
    {

    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    public void setPeriods( List<Period> periods )
    {
        this.periods = periods;
    }

    public List<ProgramStageDataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<ProgramStageDataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public Integer getLimitRecords()
    {
        return limitRecords;
    }

    public void setLimitRecords( Integer limitRecords )
    {
        this.limitRecords = limitRecords;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

}
