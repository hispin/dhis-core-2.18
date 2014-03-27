package org.hisp.dhis.eventreport;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.user.User;

/**
* @author Lars Helge Overland
*/
public class EventReport
    extends BaseAnalyticalObject
{
    /**
     * Program. Required.
     */
    private Program program;
    
    /**
     * Program stage.
     */
    private ProgramStage programStage;

    /**
     * Start date.
     */
    private Date startDate;
    
    /**
     * End date.
     */
    private Date endDate;
    
    /**
     * Dimensions to crosstabulate / use as columns.
     */
    private List<String> columnDimensions = new ArrayList<String>();
    
    /**
     * Dimensions to use as rows.
     */
    private List<String> rowDimensions = new ArrayList<String>();
    
    /**
     * Dimensions to use as filter.
     */
    private List<String> filterDimensions = new ArrayList<String>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EventReport()
    {
    }
    
    public EventReport( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // AnalyticalObject
    // -------------------------------------------------------------------------

    @Override
    public void init( User user, Date date, OrganisationUnit organisationUnit, List<OrganisationUnit> organisationUnitsAtLevel, 
        List<OrganisationUnit> organisationUnitsInGroups, I18nFormat format )
    {        
    }

    @Override
    public void populateAnalyticalProperties()
    {        
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    public List<String> getColumnDimensions()
    {
        return columnDimensions;
    }

    public void setColumnDimensions( List<String> columnDimensions )
    {
        this.columnDimensions = columnDimensions;
    }

    public List<String> getRowDimensions()
    {
        return rowDimensions;
    }

    public void setRowDimensions( List<String> rowDimensions )
    {
        this.rowDimensions = rowDimensions;
    }

    public List<String> getFilterDimensions()
    {
        return filterDimensions;
    }

    public void setFilterDimensions( List<String> filterDimensions )
    {
        this.filterDimensions = filterDimensions;
    }
}
