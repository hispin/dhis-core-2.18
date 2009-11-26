package org.hisp.dhis.datamart;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.util.Collection;

import org.amplecode.cave.process.SerialToGroup;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datamart.engine.DataMartEngine;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.process.AbstractStatementInternalProcess;

/**
 * @author Lars Helge Overland
 * 
 * @version $Id: DataMartInternalProcess.java 6222 2008-11-07 12:20:46Z larshelg $
 */
public class DataMartInternalProcess
    extends AbstractStatementInternalProcess implements SerialToGroup
{
    public static final String ID = "internal-process-DataMart";
    public static final String PROCESS_TYPE = "DataMart";
    
    private static final String PROCESS_GROUP = "DataMartProcessGroup";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataMartEngine dataMartEngine;

    public void setDataMartEngine( DataMartEngine dataMartEngine )
    {
        this.dataMartEngine = dataMartEngine;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Collection<Integer> dataElementIds;

    public void setDataElementIds( Collection<Integer> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private Collection<Integer> indicatorIds;

    public void setIndicatorIds( Collection<Integer> indicatorIds )
    {
        this.indicatorIds = indicatorIds;
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

    public void setProperties( DataMartExport export )
    {
        this.dataElementIds = getIdentifiers( DataElement.class, export.getDataElements() );
        this.indicatorIds = getIdentifiers( Indicator.class, export.getIndicators() );
        this.periodIds = getIdentifiers( Period.class, export.getPeriods() );
        this.organisationUnitIds = getIdentifiers( OrganisationUnit.class, export.getOrganisationUnits() );
    }

    // -------------------------------------------------------------------------
    // SerialToGroup implementation
    // -------------------------------------------------------------------------

    public String getGroup()
    {
        return PROCESS_GROUP;
    }
    
    // -------------------------------------------------------------------------
    // AbstractStatementInternalProcess implementation
    // -------------------------------------------------------------------------

    @Override
    protected void executeStatements()
        throws Exception
    {
        getState().setMessage( "export_process_started" );
        
        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, getState() );
        
        getState().setMessage( "export_process_finished" );
    }
}
