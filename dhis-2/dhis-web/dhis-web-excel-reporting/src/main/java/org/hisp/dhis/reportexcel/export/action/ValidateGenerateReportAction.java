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
package org.hisp.dhis.reportexcel.export.action;

import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.reportexcel.action.ActionSupport;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ValidateGenerateReportAction
    extends ActionSupport
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer reportId;

    private Integer periodId;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    public String execute()
        throws Exception
    {
        if ( organisationUnitSelectionManager.getSelectedOrganisationUnit() == null )
        {
            message = i18n.getString( "organisationunit_is_null" );
            return ERROR;
        }
        if ( (reportId == null) || (reportId == -1) )
        {
            message = i18n.getString( "report_is_null" );
            return ERROR;
        }
        if ( periodId == null )
        {
            message = i18n.getString( "period_is_null" );
            return ERROR;
        }
        return SUCCESS;
    }

}
