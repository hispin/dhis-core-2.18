package org.hisp.dhis.completeness;

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

import java.util.Collection;

import org.amplecode.cave.process.SerialToGroup;
import org.hisp.dhis.system.process.AbstractStatementInternalProcess;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataSetCompletenessInternalProcess
    extends AbstractStatementInternalProcess implements SerialToGroup
{
    public static final String ID = "internal-process-DataSetCompleteness";

    private static final String PROCESS_GROUP = "DataMartProcessGroup";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetCompletenessService completenessService;

    public void setCompletenessService( DataSetCompletenessService completenessService )
    {
        this.completenessService = completenessService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Collection<Integer> dataSetIds;

    public void setDataSetIds( Collection<Integer> dataSetIds )
    {
        this.dataSetIds = dataSetIds;
    }

    private Collection<Integer> periodIds;

    public void setPeriodIds( Collection<Integer> periodIds )
    {
        this.periodIds = periodIds;
    }

    private Collection<Integer> organisationUnitIds;

    public void setOrganisationUnitIds( Collection<Integer> organisationUnitIds )
    {
        this.organisationUnitIds = organisationUnitIds;
    }
    
    // -------------------------------------------------------------------------
    // SerialToGroup implementation
    // -------------------------------------------------------------------------

    public String getGroup()
    {
        return PROCESS_GROUP;
    }
    
    // -------------------------------------------------------------------------
    // AbstractTransactionalInternalProcess implementation
    // -------------------------------------------------------------------------

    @Override
    public void executeStatements()
    {
        completenessService.exportDataSetCompleteness( dataSetIds, periodIds, organisationUnitIds );
    }
}
