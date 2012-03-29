package org.hisp.dhis.dxf2.datavalue;

/*
 * Copyright (c) 2011, University of Oslo
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

import static org.hisp.dhis.system.util.DateUtils.getDefaultDate;
import static org.hisp.dhis.importexport.ImportStrategy.*;

import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportCount;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class DefaultDataValueService
    implements DataValueService
{
    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;
    
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private BatchHandlerFactory batchHandlerFactory;
    
    @Transactional
    public ImportSummary saveDataValues( DataValues dataValues, IdentifiableProperty idScheme, boolean dryRun, ImportStrategy strategy )
    {
        ImportSummary summary = new ImportSummary();
        
        Map<String, DataElement> dataElementMap = identifiableObjectManager.getIdMap( DataElement.class, idScheme );
        Map<String, OrganisationUnit> orgUnitMap = identifiableObjectManager.getIdMap( OrganisationUnit.class, idScheme );
        Map<String, DataElementCategoryOptionCombo> categoryOptionComboMap = identifiableObjectManager.getIdMap( DataElementCategoryOptionCombo.class, IdentifiableProperty.UID );
        
        DataElementCategoryOptionCombo fallbackCategoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class ).init();

        int importCount = 0;
        int updateCount = 0;                
        
        for ( org.hisp.dhis.dxf2.datavalue.DataValue dataValue : dataValues.getDataValues() )
        {
            DataValue internalValue = new DataValue();
            
            DataElement dataElement = dataElementMap.get( dataValue.getDataElement() );
            OrganisationUnit orgUnit = orgUnitMap.get( dataValue.getOrgUnit() );
            DataElementCategoryOptionCombo categoryOptionCombo = categoryOptionComboMap.get( dataValue.getCategoryOptionCombo() );
            Period period = PeriodType.getPeriodFromIsoString( dataValue.getPeriod() );
            
            if ( dataElement == null )
            {
                summary.getNoneExistingIdentifiers().add( new ImportConflict( DataElement.class.getSimpleName(), dataValue.getDataElement() ) );
                continue;
            }
            
            if ( orgUnit == null )
            {
                summary.getNoneExistingIdentifiers().add( new ImportConflict( OrganisationUnit.class.getSimpleName(), dataValue.getOrgUnit() ) );
                continue;
            }

            if ( period == null )
            {
                summary.getNoneExistingIdentifiers().add( new ImportConflict( Period.class.getSimpleName(), dataValue.getPeriod() ) );
                continue;
            }
            
            if ( categoryOptionCombo == null )
            {
                categoryOptionCombo = fallbackCategoryOptionCombo;
            }
            
            internalValue.setDataElement( dataElement );
            internalValue.setPeriod( periodService.reloadPeriod( period ) );
            internalValue.setSource( orgUnit );
            internalValue.setOptionCombo( categoryOptionCombo );
            internalValue.setValue( dataValue.getValue() );
            internalValue.setStoredBy( dataValue.getStoredBy() );
            internalValue.setTimestamp( getDefaultDate( dataValue.getTimestamp() ) );
            internalValue.setComment( dataValue.getComment() );
            internalValue.setFollowup( dataValue.getFollowup() );
            
            if ( batchHandler.objectExists( internalValue ) )
            {
                if ( NEW_AND_UPDATES.equals( strategy ) || UPDATES.equals( strategy ) )
                {
                    if ( !dryRun )
                    {
                        batchHandler.updateObject( internalValue );
                    }

                    updateCount++;
                }
            }
            else
            {
                if ( NEW_AND_UPDATES.equals( strategy ) || NEW.equals( strategy ) )
                {
                    if ( !dryRun )
                    {
                        batchHandler.addObject( internalValue );
                    }
                    
                    importCount++;
                }
            }
        }
        
        summary.getCounts().add( new ImportCount( DataValue.class.getSimpleName(), importCount, updateCount ) );
        
        batchHandler.flush();
        
        return summary;
    }
}
