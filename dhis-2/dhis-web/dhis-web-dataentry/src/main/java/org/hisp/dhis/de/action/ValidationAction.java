package org.hisp.dhis.de.action;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.ListUtils;
import org.hisp.dhis.validation.ValidationResult;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

import com.opensymphony.xwork2.Action;

/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @version $Id: ValidationAction.java 5426 2008-06-16 04:33:05Z larshelg $
 */
public class ValidationAction
    implements Action
{
    private static final Log log = LogFactory.getLog( ValidationAction.class );

    private static final double STD_DEV = 2.0;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataAnalysisService stdDevOutlierAnalysisService;

    public void setStdDevOutlierAnalysisService( DataAnalysisService stdDevOutlierAnalysisService )
    {
        this.stdDevOutlierAnalysisService = stdDevOutlierAnalysisService;
    }

    private DataAnalysisService minMaxOutlierAnalysisService;

    public void setMinMaxOutlierAnalysisService( DataAnalysisService minMaxOutlierAnalysisService )
    {
        this.minMaxOutlierAnalysisService = minMaxOutlierAnalysisService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<ValidationResult> results;

    public List<ValidationResult> getResults()
    {
        return results;
    }

    private Map<Integer, String> leftsideFormulaMap;

    public Map<Integer, String> getLeftsideFormulaMap()
    {
        return leftsideFormulaMap;
    }

    private Map<Integer, String> rightsideFormulaMap;

    public Map<Integer, String> getRightsideFormulaMap()
    {
        return rightsideFormulaMap;
    }

    private Collection<DeflatedDataValue> dataValues;

    public Collection<DeflatedDataValue> getDataValues()
    {
        return dataValues;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public String execute()
        throws Exception
    {
        OrganisationUnit orgUnit = selectedStateManager.getSelectedOrganisationUnit();

        Period selectedPeriod = selectedStateManager.getSelectedPeriod();

        Period period = periodService.getPeriod( selectedPeriod.getStartDate(), selectedPeriod.getEndDate(),
            selectedPeriod.getPeriodType() );

        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        // ---------------------------------------------------------------------
        // Min-max and outlier analysis
        // ---------------------------------------------------------------------

        Collection<DeflatedDataValue> stdDevs = (Collection<DeflatedDataValue>)stdDevOutlierAnalysisService.
            analyse( orgUnit, dataSet.getDataElements(), ListUtils.getCollection( period ), STD_DEV );

        Collection<DeflatedDataValue> minMaxs = (Collection<DeflatedDataValue>)minMaxOutlierAnalysisService.
            analyse( orgUnit, dataSet.getDataElements(), ListUtils.getCollection( period ), null );
        
        dataValues = CollectionUtils.union( stdDevs, minMaxs );
        
        log.info( "Number of outlier values: " + dataValues.size() );

        // ---------------------------------------------------------------------
        // Validation rule analysis
        // ---------------------------------------------------------------------

        results = new ArrayList<ValidationResult>( validationRuleService.validate( dataSet, period, orgUnit ) );

        log.info( "Number of validation violations: " + results.size() );
        
        if ( results.size()> 0 )
        {
            leftsideFormulaMap = new HashMap<Integer, String>( results.size() );
            rightsideFormulaMap = new HashMap<Integer, String>( results.size() );

            for ( ValidationResult result : results )
            {
                ValidationRule rule = result.getValidationRule();

                leftsideFormulaMap.put( rule.getId(), expressionService.getExpressionDescription( rule.getLeftSide().getExpression() ) );
                rightsideFormulaMap.put( rule.getId(), expressionService.getExpressionDescription( rule.getRightSide().getExpression() ) );
            }
        }
        
        return dataValues.size() == 0 && results.size() == 0 ? NONE : SUCCESS;
    }
}
