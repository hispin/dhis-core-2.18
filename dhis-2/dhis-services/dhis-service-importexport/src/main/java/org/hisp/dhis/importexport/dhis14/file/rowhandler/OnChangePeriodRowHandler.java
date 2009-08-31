package org.hisp.dhis.importexport.dhis14.file.rowhandler;

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

import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.converter.AbstractPeriodConverter;
import org.hisp.dhis.importexport.dhis14.util.Dhis14PeriodUtil;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * @author Lars Helge Overland
 * @version $Id: OnChangePeriodRowHandler.java 5946 2008-10-16 15:46:43Z larshelg $
 */
public class OnChangePeriodRowHandler
    extends AbstractPeriodConverter implements RowHandler
{
    private ImportParams params;
    
    private Map<String, Integer> periodTypeMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public OnChangePeriodRowHandler( BatchHandler<Period> batchHandler,
        ImportObjectService importObjectService,
        PeriodService periodService,
        Map<String, Integer> periodTypeMapping,
        ImportParams params )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.periodService = periodService;
        this.periodTypeMapping = periodTypeMapping;
        this.params = params;
    }
    
    // -------------------------------------------------------------------------
    // RowHandler implementation
    // -------------------------------------------------------------------------
    
    public void handleRow( Object object )
    {
        final Period period = (Period) object;
        
        period.getPeriodType().setId( periodTypeMapping.get( period.getPeriodType().getName() ) ); // Could be onchange or other
        
        if ( Dhis14PeriodUtil.periodIsUnique( period ) )
        {
            period.setId( Dhis14PeriodUtil.getUniqueIdentifier() ); // Necessary in case of preview

            Dhis14PeriodUtil.addPeriod( period ); // For uniqueness
            
            NameMappingUtil.addPeriodMapping( period.getId(), period ); // For later reference by semi-permanent datavalues
            
            read( period, GroupMemberType.NONE, params );
        }
    }
}
