package org.hisp.dhis.validation;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * Holds common values that are used during a validation run (either interactive
 * or an alert run.) These values don't change during the multi-threaded tasks
 * (except that results entries are added in a threadsafe way.)
 * 
 * Some of the values are precalculated collections, to save CPU time during
 * the run. All of these values are stored in this single "context" object
 * to allow a single object reference for each of the scheduled tasks. (This
 * also reduces the amount of memory needed to queue all the multi-threaded
 * tasks.)
 * 
 * For some of these properties this is also important because they should
 * be copied from Hibernate lazy collections before the multithreaded part
 * of the run starts, otherwise the threads may not be able to access these
 * values.
 * 
 * @author Jim Grace
 */
public class ValidationRunContext
{
    private Map<PeriodType, PeriodTypeExtended> periodTypeExtendedMap;

    private ValidationRunType runType;

    private Date lastAlertRun;

    private Map<String, Double> constantMap;

    private Map<ValidationRule, ValidationRuleExtended> ruleXMap;

    private Collection<OrganisationUnitExtended> sourceXs;
    
    private Collection<ValidationResult> validationResults;

    private ExpressionService expressionService;
    
    private PeriodService periodService;
    
    private DataValueService dataValueService;
    
    private ValidationRunContext()
    {
    }
    
    public String toString()
    {
        return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE )
            .append( "\n  PeriodTypeExtendedMap", (Arrays.toString( periodTypeExtendedMap.entrySet().toArray() )) )
            .append( "\n  runType", runType ).append( "\n  lastAlertRun", lastAlertRun )
            .append( "\n  constantMap", "[" + constantMap.size() + "]" )
            .append( "\n  ruleXMap", "[" + ruleXMap.size() + "]" )
            .append( "\n  sourceXs", Arrays.toString( sourceXs.toArray() ) )
            .append( "\n  validationResults", Arrays.toString( validationResults.toArray() ) ).toString();
    }
    
    /**
     * Creates and fills a new context object for a validation run.
     * 
     * @param sources organisation units for validation
     * @param periods periods for validation
     * @param rules validation rules for validation
     * @param runType whether this is an INTERACTIVE or ALERT run
     * @param lastAlertRun (for ALERT runs) date of previous alert run
     * @return context object for this run
     */
    public static ValidationRunContext getNewValidationRunContext( Collection<OrganisationUnit> sources, Collection<Period> periods,
        Collection<ValidationRule> rules, Map<String, Double> constantMap, ValidationRunType runType, Date lastAlertRun,
        ExpressionService expressionService, PeriodService periodService, DataValueService dataValueService )
    {
        ValidationRunContext context = new ValidationRunContext();
        context.runType = runType;
        context.lastAlertRun = lastAlertRun;
        context.validationResults = new ConcurrentLinkedQueue<ValidationResult>(); // thread-safe
        context.periodTypeExtendedMap = new HashMap<PeriodType, PeriodTypeExtended>();
        context.ruleXMap = new HashMap<ValidationRule, ValidationRuleExtended>();
        context.sourceXs = new HashSet<OrganisationUnitExtended>();
        context.constantMap = constantMap;
        context.expressionService = expressionService;
        context.periodService = periodService;
        context.dataValueService = dataValueService;
        context.initialize( sources, periods, rules );
        return context;
    }
    
    /**
     * Initializes context values based on sources, periods and rules
     * 
     * @param sources
     * @param periods
     * @param rules
     */
    private void initialize( Collection<OrganisationUnit> sources, Collection<Period> periods, Collection<ValidationRule> rules )
    {
        // Group the periods by period type.
        for ( Period period : periods )
        {
            PeriodTypeExtended periodTypeX = getOrCreatePeriodTypeExtended( period.getPeriodType() );
            periodTypeX.getPeriods().add( period );
        }

        for ( ValidationRule rule : rules )
        {
            // Find the period type extended for this rule
            PeriodTypeExtended periodTypeX = getOrCreatePeriodTypeExtended( rule.getPeriodType() ); 
            periodTypeX.getRules().add( rule ); // Add this rule to the period type ext.
            
            if ( rule.getCurrentDataElements() != null )
            {
                // Add this rule's data elements to the period extended.
                periodTypeX.getDataElements().addAll( rule.getCurrentDataElements() );
            }
            // Add the allowed period types for rule's current data elements:
            periodTypeX.getAllowedPeriodTypes().addAll(
            		getAllowedPeriodTypesForDataElements( rule.getCurrentDataElements(), rule.getPeriodType() ) );
            
            // Add the ValidationRuleExtended
            Collection<PeriodType> allowedPastPeriodTypes =
            		getAllowedPeriodTypesForDataElements( rule.getPastDataElements(), rule.getPeriodType() );
            ValidationRuleExtended ruleX = new ValidationRuleExtended( rule, allowedPastPeriodTypes );
            ruleXMap.put( rule, ruleX );
        }

        // We only need to keep period types that are selected and also used by
        // rules that are selected.
        // Start by making a defensive copy so we can delete while iterating.
        Set<PeriodTypeExtended> periodTypeXs = new HashSet<PeriodTypeExtended>( periodTypeExtendedMap.values() );
        for ( PeriodTypeExtended periodTypeX : periodTypeXs )
        {
            if ( periodTypeX.getPeriods().isEmpty() || periodTypeX.getRules().isEmpty() )
            {
                periodTypeExtendedMap.remove( periodTypeX.getPeriodType() );
            }
        }

        for ( OrganisationUnit source : sources )
        {
            OrganisationUnitExtended sourceX = new OrganisationUnitExtended( source );
            sourceXs.add( sourceX );

            Map<PeriodType, Set<DataElement>> sourceDataElementsByPeriodType = source
                .getDataElementsInDataSetsByPeriodType();
            for ( PeriodTypeExtended periodTypeX : periodTypeExtendedMap.values() )
            {
                Collection<DataElement> sourceDataElements = sourceDataElementsByPeriodType
                    .get( periodTypeX.getPeriodType() );
                if ( sourceDataElements != null )
                {
                    periodTypeX.getSourceDataElements().put( source, sourceDataElements );
                }
                else
                {
                    periodTypeX.getSourceDataElements().put( source, new HashSet<DataElement>() );
                }
            }
        }
    }

    /**
     * Gets the PeriodTypeExtended from the context object. If not found,
     * creates a new PeriodTypeExtended object, puts it into the context object,
     * and returns it.
     * 
     * @param context validation run context
     * @param periodType period type to search for
     * @return period type extended from the context object
     */
    private PeriodTypeExtended getOrCreatePeriodTypeExtended( PeriodType periodType )
    {
        PeriodTypeExtended periodTypeX = periodTypeExtendedMap.get( periodType );
        if ( periodTypeX == null )
        {
            periodTypeX = new PeriodTypeExtended( periodType );
            periodTypeExtendedMap.put( periodType, periodTypeX );
        }
        return periodTypeX;
    }

    /**
     * Finds all period types that may contain given data elements, whose period
     * type interval is at least as long as the given period type.
     * 
     * @param dataElements data elements to look for
     * @param periodType the minimum-length period type
     * @return all period types that are allowed for these data elements
     */
    private static Collection<PeriodType> getAllowedPeriodTypesForDataElements( Collection<DataElement> dataElements,
        PeriodType periodType )
    {
        Collection<PeriodType> allowedPeriodTypes = new HashSet<PeriodType>();
        if ( dataElements != null )
        {
	        for ( DataElement dataElement : dataElements )
	        {
	            for ( DataSet dataSet : dataElement.getDataSets() )
	            {
	                if ( dataSet.getPeriodType().getFrequencyOrder() >= periodType.getFrequencyOrder() )
	                {
	                    allowedPeriodTypes.add( dataSet.getPeriodType() );
	                }
	            }
	        }
        }
        return allowedPeriodTypes;
    }

    // -------------------------------------------------------------------------
    // Set and get methods
    // -------------------------------------------------------------------------  

    public Map<PeriodType, PeriodTypeExtended> getPeriodTypeExtendedMap() {
		return periodTypeExtendedMap;
	}

	public ValidationRunType getRunType() {
		return runType;
	}

	public Date getLastAlertRun() {
		return lastAlertRun;
	}

	public Map<String, Double> getConstantMap() {
		return constantMap;
	}

	public Map<ValidationRule, ValidationRuleExtended> getRuleXMap() {
		return ruleXMap;
	}

	public Collection<OrganisationUnitExtended> getSourceXs() {
		return sourceXs;
	}

	public Collection<ValidationResult> getValidationResults() {
		return validationResults;
	}

	public ExpressionService getExpressionService() {
		return expressionService;
	}

	public PeriodService getPeriodService() {
		return periodService;
	}

	public DataValueService getDataValueService() {
		return dataValueService;
	}
}

