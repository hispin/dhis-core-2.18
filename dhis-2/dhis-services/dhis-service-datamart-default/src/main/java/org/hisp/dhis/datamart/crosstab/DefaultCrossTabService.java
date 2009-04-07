package org.hisp.dhis.datamart.crosstab;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore;
import org.hisp.dhis.jdbc.BatchHandler;
import org.hisp.dhis.jdbc.BatchHandlerFactory;
import org.hisp.dhis.jdbc.batchhandler.DataValueCrossTabBatchHandler;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultCrossTabService.java 6268 2008-11-12 15:16:02Z larshelg $
 */
public class DefaultCrossTabService
    implements CrossTabService
{
    private static final Log log = LogFactory.getLog( DefaultCrossTabService.class );
    
    private static final int MAX_LENGTH = 20;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;
        
    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private CrossTabStore crossTabStore;
        
    public void setCrossTabStore( CrossTabStore crossTabTableManager )
    {
        this.crossTabStore = crossTabTableManager;
    }
    
    private DataMartStore dataMartStore;
        
    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }
    
    // -------------------------------------------------------------------------
    // CrossTabService implementation
    // -------------------------------------------------------------------------

    public Collection<Operand> populateCrossTabTable( final Collection<Operand> operands, 
        final Collection<Integer> periodIds, final Collection<Integer> organisationUnitIds )
    {
        final Set<Operand> operandsWithData = new HashSet<Operand>( operands );

        if ( validate( operands, periodIds, organisationUnitIds ) )
        {
            final List<Operand> operandList = new ArrayList<Operand>( operands );
            
            Collections.sort( operandList );

            crossTabStore.dropCrossTabTable();
            
            log.info( "Dropped crosstab table" );
            
            crossTabStore.createCrossTabTable( operandList );
            
            log.info( "Created crosstab table" );
            
            final BatchHandler batchHandler = batchHandlerFactory.createBatchHandler( DataValueCrossTabBatchHandler.class );
            
            batchHandler.init();
            
            Map<Operand, String> map = null;
            
            List<String> valueList = null;
            
            boolean hasValues = false;
            
            String value = null;
            
            for ( final Integer periodId : periodIds )
            {
                for ( final Integer sourceId : organisationUnitIds )
                {
                    map = dataMartStore.getDataValueMap( periodId, sourceId );
                    
                    valueList = new ArrayList<String>( operandList.size() + 2 );
                    
                    valueList.add( String.valueOf( periodId ) );
                    valueList.add( String.valueOf( sourceId ) );

                    hasValues = false;
                    
                    for ( Operand operand : operandList )
                    {
                        value = map.get( operand );
                        
                        if ( value != null && value.length() > MAX_LENGTH )
                        {
                            log.warn( "Value ignored, too long: '" + value + 
                                "', for dataelement id: '" + operand.getDataElementId() +
                                "', categoryoptioncombo id: '" + operand.getOptionComboId() +
                                "', period id: '" + periodId + 
                                "', source id: '" + sourceId + "'" );
                            
                            value = null;
                        }                        
                        
                        if ( value != null )
                        {
                            hasValues = true;                            
                            operandsWithData.add( operand );                            
                        }
                        
                        valueList.add( value );
                    }
                    
                    if ( hasValues )
                    {
                        batchHandler.addObject( valueList );
                    }
                }
                
                log.info( "Crosstabulated data for period " + periodId );
            }
            
            batchHandler.flush();
        }

        return operandsWithData;
    }
    
    public void dropCrossTabTable()
    {
        crossTabStore.dropCrossTabTable();
    }
    
    public void trimCrossTabTable( Collection<Operand> operands )
    {
        // TODO use H2 in-memory table for datavaluecrosstab table ?
        
        crossTabStore.createTrimmedCrossTabTable( operands );
        
        crossTabStore.dropCrossTabTable();
        
        crossTabStore.renameTrimmedCrossTabTable();
    }
    
    public Map<Operand, Integer> getOperandIndexMap( Collection<Operand> operands )
    {
        final Map<Integer, String> columnNames = crossTabStore.getCrossTabTableColumns();

        final Map<Operand, Integer> operandMap = new HashMap<Operand, Integer>();
        
        for ( final Map.Entry<Integer, String> entry : columnNames.entrySet() )
        {
            if ( entry.getValue().startsWith( CrossTabStore.COLUMN_PREFIX ) )
            {
                final String operandString = entry.getValue().replace( CrossTabStore.COLUMN_PREFIX, "" );
                
                final int dataElementId = Integer.parseInt( operandString.substring( 0, operandString.indexOf( CrossTabStore.SEPARATOR ) ) );
                final int categoryOptionComboId = Integer.parseInt( operandString.substring( operandString.indexOf( CrossTabStore.SEPARATOR ) + 1 ) );
                
                final Operand operand = new Operand( dataElementId, categoryOptionComboId );
                
                if ( operands.contains( operand ) )
                {
                    operandMap.put( operand, entry.getKey() );
                }
            }
        }
        
        return operandMap;
    }

    public int validateCrossTabTable( Collection<Operand> operands )
    {
        return crossTabStore.validateCrossTabTable( operands );
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Validates whether the given collections of identifiers are not null and of size greater than 0.
     */
    private boolean validate( Collection<Operand> operands, Collection<Integer> periodIds, Collection<Integer> unitIds )
    {
        if ( operands == null || periodIds == null || unitIds == null )
        {
            return false;
        }
        
        if ( operands.size() == 0 || periodIds.size() == 0 || unitIds.size() == 0 )
        {
            return false;
        }
        
        return true;
    }
}
