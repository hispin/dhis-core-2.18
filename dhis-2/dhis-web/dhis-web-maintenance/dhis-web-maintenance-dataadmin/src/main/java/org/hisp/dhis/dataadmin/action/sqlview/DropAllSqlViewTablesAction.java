package org.hisp.dhis.dataadmin.action.sqlview;

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

import org.hisp.dhis.sqlview.SqlViewService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Dang Duy Hieu
 * @version $Id DropAllSqlViewTablesAction.java July 07, 2010$
 */
public class DropAllSqlViewTablesAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SqlViewService sqlViewService;

    public void setSqlViewService( SqlViewService sqlViewService )
    {
        this.sqlViewService = sqlViewService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private boolean organisationUnit;

    public void setOrganisationUnit( boolean organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    private boolean groupSet;

    public void setGroupSet( boolean groupSet )
    {
        this.groupSet = groupSet;
    }

    private boolean dataElementGroupSetStructure;

    public void setDataElementGroupSetStructure( boolean dataElementGroupSetStructure )
    {
        this.dataElementGroupSetStructure = dataElementGroupSetStructure;
    }

    private boolean indicatorGroupSetStructure;

    public void setIndicatorGroupSetStructure( boolean indicatorGroupSetStructure )
    {
        this.indicatorGroupSetStructure = indicatorGroupSetStructure;
    }

    private boolean organisationUnitGroupSetStructure;

    public void setOrganisationUnitGroupSetStructure( boolean organisationUnitGroupSetStructure )
    {
        this.organisationUnitGroupSetStructure = organisationUnitGroupSetStructure;
    }

    private boolean categoryStructure;

    public void setCategoryStructure( boolean categoryStructure )
    {
        this.categoryStructure = categoryStructure;
    }

    private boolean categoryOptionComboName;

    public void setCategoryOptionComboName( boolean categoryOptionComboName )
    {
        this.categoryOptionComboName = categoryOptionComboName;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public boolean isOrganisationUnit()
    {
        return organisationUnit;
    }

    public boolean isGroupSet()
    {
        return groupSet;
    }

    public boolean isDataElementGroupSetStructure()
    {
        return dataElementGroupSetStructure;
    }

    public boolean isIndicatorGroupSetStructure()
    {
        return indicatorGroupSetStructure;
    }

    public boolean isOrganisationUnitGroupSetStructure()
    {
        return organisationUnitGroupSetStructure;
    }

    public boolean isCategoryStructure()
    {
        return categoryStructure;
    }

    public boolean isCategoryOptionComboName()
    {
        return categoryOptionComboName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        sqlViewService.dropViewTable( sqlViewService.getAllSqlViewNames() );

        return SUCCESS;
    }

}
