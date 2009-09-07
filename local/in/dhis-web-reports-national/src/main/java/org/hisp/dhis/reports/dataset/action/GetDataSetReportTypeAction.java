package org.hisp.dhis.reports.dataset.action;

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

import org.hisp.dhis.dataset.DataEntryForm;
import org.hisp.dhis.dataset.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.reports.dataset.state.SelectedStateManager;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class GetDataSetReportTypeAction
    extends AbstractAction
{
    private static final String DEFAULT_REPORT = "defaultreport";

    private static final String CUSTOM_REPORT = "customreport";

    // -----------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    // -----------------------------------------------------------------------
    // Input/output parameters
    // -----------------------------------------------------------------------

    private String selectedUnitOnly;

    public String getSelectedUnitOnly()
    {
        return selectedUnitOnly;
    }

    public void setSelectedUnitOnly( String selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }

    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        DataEntryForm dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );

        if ( dataEntryForm != null )
        {
            if ( dataEntryForm.getHtmlCode() != null || dataEntryForm.getHtmlCode() != "" )
            {
                return CUSTOM_REPORT;
            }
        }

        return DEFAULT_REPORT;        
    }
}
