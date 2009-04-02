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

import java.util.HashSet;
import java.util.Map;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.converter.AbstractCalculatedDataElementConverter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.system.util.UUIdUtils;

import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CalculatedDataElementRowHandler
    extends AbstractCalculatedDataElementConverter implements RowHandler
{
    private ImportParams params;
    
    private DataElementCategoryCombo categoryCombo;
    
    private ExpressionService expressionService;
    
    private Map<Integer, String> calculatedEntryMap;
    
    private Map<Object, Integer> dataElementMapping;
    
    private Map<Object, Integer> categoryOptionComboMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CalculatedDataElementRowHandler( ImportObjectService importObjectService,
        DataElementService dataElementService, 
        ImportParams params,
        DataElementCategoryCombo categoryCombo,
        ImportAnalyser importAnalyser,
        ExpressionService expressionService,
        Map<Integer, String> calculatedEntryMap,
        Map<Object, Integer> dataElementMapping,
        Map<Object, Integer> categoryOptionComboMapping )
    {
        this.importObjectService = importObjectService;
        this.dataElementService = dataElementService;
        this.params = params;
        this.categoryCombo = categoryCombo;
        this.importAnalyser = importAnalyser;
        this.expressionService = expressionService;
        this.calculatedEntryMap = calculatedEntryMap;
        this.dataElementMapping = dataElementMapping;
        this.categoryOptionComboMapping = categoryOptionComboMapping;
    }
    
    // -------------------------------------------------------------------------
    // BatchRowHandler implementation
    // -------------------------------------------------------------------------

    public void handleRow( Object object )
    {
        final CalculatedDataElement dataElement = (CalculatedDataElement) object;
        
        NameMappingUtil.addDataElementMapping( dataElement.getId(), dataElement.getName() );
        NameMappingUtil.addDataElementAggregationOperatorMapping( dataElement.getId(), dataElement.getAggregationOperator() );
        
        dataElement.setUuid( UUIdUtils.getUUId() );
        dataElement.setActive( true );
                    
        if ( dataElement.getCode() != null && dataElement.getCode().trim().length() == 0 )
        {
            dataElement.setCode( null );
        }
        
        dataElement.setCategoryCombo( categoryCombo );
        
        String expression = calculatedEntryMap.get( dataElement.getId() );
        
        expression = expressionService.convertExpression( expression, dataElementMapping, categoryOptionComboMapping );
        dataElement.setExpression( new Expression( expression, null, new HashSet<DataElement>() ) );
        
        read( dataElement, GroupMemberType.NONE, params );
    }
}
