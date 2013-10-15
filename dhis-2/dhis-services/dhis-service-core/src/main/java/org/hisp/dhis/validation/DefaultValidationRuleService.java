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

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsByName;
import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @author Jim Grace
 */
@Transactional
public class DefaultValidationRuleService
    implements ValidationRuleService
{
    private static final Log log = LogFactory.getLog( DefaultValidationRuleService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleStore validationRuleStore;

    public void setValidationRuleStore( ValidationRuleStore validationRuleStore )
    {
        this.validationRuleStore = validationRuleStore;
    }

    private GenericIdentifiableObjectStore<ValidationRuleGroup> validationRuleGroupStore;

    public void setValidationRuleGroupStore( GenericIdentifiableObjectStore<ValidationRuleGroup> validationRuleGroupStore )
    {
        this.validationRuleGroupStore = validationRuleGroupStore;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // ValidationRule business logic
    // -------------------------------------------------------------------------

    public Collection<ValidationResult> validate( Date startDate, Date endDate, Collection<OrganisationUnit> sources )
    {
        log.info( "Validate startDate=" + startDate + " endDate=" + endDate + " sources[" + sources.size() + "]" );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = getAllValidationRules();
        
        return Validator.validate( sources, periods, rules, ValidationRunType.INTERACTIVE, null,
            constantService, expressionService, periodService, dataValueService );
    }

    public Collection<ValidationResult> validate( Date startDate, Date endDate, Collection<OrganisationUnit> sources,
        ValidationRuleGroup group )
    {
    	log.info( "Validate startDate=" + startDate + " endDate=" + endDate + " sources[" + sources.size() + "] group=" + group.getName() );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = group.getMembers();
        
        return Validator.validate( sources, periods, rules, ValidationRunType.INTERACTIVE, null,
            constantService, expressionService, periodService, dataValueService );
    }

    public Collection<ValidationResult> validate( Date startDate, Date endDate, OrganisationUnit source )
    {
    	log.info( "Validate startDate=" + startDate + " endDate=" + endDate + " source=" + source.getName() );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = getAllValidationRules();
        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( source );
        
        return Validator.validate( sources, periods, rules, ValidationRunType.INTERACTIVE, null,
            constantService, expressionService, periodService, dataValueService );
    }

    public Collection<ValidationResult> validate( DataSet dataSet, Period period, OrganisationUnit source )
    {
    	log.info( "Validate dataSet=" + dataSet.getName() + " period=[" + period.getPeriodType().getName() + " "
            + period.getStartDate() + " " + period.getEndDate() + "]" + " source=" + source.getName() );
        Collection<Period> periods = new ArrayList<Period>();
        periods.add( period );

        Collection<ValidationRule> rules = null;
        
        if ( DataSet.TYPE_CUSTOM.equals( dataSet.getDataSetType() ) )
        {
            rules = getRulesForDataSet( dataSet );
        }
        else
        {
            rules = getValidationTypeRulesForDataElements( dataSet.getDataElements() );
        }

        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( source );
        
        return Validator.validate( sources, periods, rules, ValidationRunType.INTERACTIVE, null,
            constantService, expressionService, periodService, dataValueService );
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    /**
     * Returns all validation-type rules which have specified data elements
     * assigned to them.
     * 
     * @param dataElements the data elements to look for
     * @return all validation rules which have the data elements assigned.
     */
    private Collection<ValidationRule> getValidationTypeRulesForDataElements( Set<DataElement> dataElements )
    {
        Set<ValidationRule> rulesForDataElements = new HashSet<ValidationRule>();

        Set<DataElement> validationRuleElements = new HashSet<DataElement>();

        for ( ValidationRule validationRule : getAllValidationRules() )
        {
            if ( validationRule.getRuleType().equals( ValidationRule.RULE_TYPE_VALIDATION ) )
            {
                validationRuleElements.clear();
                validationRuleElements.addAll( validationRule.getLeftSide().getDataElementsInExpression() );
                validationRuleElements.addAll( validationRule.getRightSide().getDataElementsInExpression() );

                if ( dataElements.containsAll( validationRuleElements ) )
                {
                    rulesForDataElements.add( validationRule );
                }
            }
        }

        return rulesForDataElements;
    }

    /**
     * Returns all validation rules which have data elements assigned to them
     * which are members of the given data set.
     * 
     * @param dataSet the data set
     * @return all validation rules which have data elements assigned to them
     *         which are members of the given data set
     */
    private Collection<ValidationRule> getRulesForDataSet( DataSet dataSet )
    {
        Set<ValidationRule> rulesForDataSet = new HashSet<ValidationRule>();

        Set<DataElementOperand> operands = dataEntryFormService.getOperandsInDataEntryForm( dataSet );

        Set<DataElementOperand> validationRuleOperands = new HashSet<DataElementOperand>();

        for ( ValidationRule rule : getAllValidationRules() )
        {
            if ( rule.getRuleType().equals( ValidationRule.RULE_TYPE_VALIDATION ) )
            {
                validationRuleOperands.clear();
                validationRuleOperands.addAll( expressionService.getOperandsInExpression(
                    rule.getLeftSide().getExpression() ) );
                validationRuleOperands.addAll( expressionService.getOperandsInExpression(
                    rule.getRightSide().getExpression() ) );

                if ( operands.containsAll( validationRuleOperands ) )
                {
                    rulesForDataSet.add( rule );
                }
            }
        }

        return rulesForDataSet;
    }
    // -------------------------------------------------------------------------
    // ValidationRule CRUD operations
    // -------------------------------------------------------------------------

    public int saveValidationRule( ValidationRule validationRule )
    {
        return validationRuleStore.save( validationRule );
    }

    public void updateValidationRule( ValidationRule validationRule )
    {
        validationRuleStore.update( validationRule );
    }

    public void deleteValidationRule( ValidationRule validationRule )
    {
        validationRuleStore.delete( validationRule );
    }

    public ValidationRule getValidationRule( int id )
    {
        return i18n( i18nService, validationRuleStore.get( id ) );
    }

    public ValidationRule getValidationRule( String uid )
    {
        return i18n( i18nService, validationRuleStore.getByUid( uid ) );
    }

    public ValidationRule getValidationRuleByName( String name )
    {
        return i18n( i18nService, validationRuleStore.getByName( name ) );
    }

    public Collection<ValidationRule> getAllValidationRules()
    {
        return i18n( i18nService, validationRuleStore.getAll() );
    }

    public Collection<ValidationRule> getValidationRules( final Collection<Integer> identifiers )
    {
        Collection<ValidationRule> objects = getAllValidationRules();

        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<ValidationRule>()
        {
            public boolean retain( ValidationRule object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public Collection<ValidationRule> getValidationRulesByName( String name )
    {
        return getObjectsByName( i18nService, validationRuleStore, name );
    }

    public Collection<ValidationRule> getValidationRulesByDataElements( Collection<DataElement> dataElements )
    {
        return i18n( i18nService, validationRuleStore.getValidationRulesByDataElements( dataElements ) );
    }

    public int getValidationRuleCount()
    {
        return validationRuleStore.getCount();
    }

    public int getValidationRuleCountByName( String name )
    {
        return getCountByName( i18nService, validationRuleStore, name );
    }

    public Collection<ValidationRule> getValidationRulesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, validationRuleStore, first, max );
    }

    public Collection<ValidationRule> getValidationRulesBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, validationRuleStore, name, first, max );
    }

    // -------------------------------------------------------------------------
    // ValidationRuleGroup CRUD operations
    // -------------------------------------------------------------------------

    public int addValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        return validationRuleGroupStore.save( validationRuleGroup );
    }

    public void deleteValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        validationRuleGroupStore.delete( validationRuleGroup );
    }

    public void updateValidationRuleGroup( ValidationRuleGroup validationRuleGroup )
    {
        validationRuleGroupStore.update( validationRuleGroup );
    }

    public ValidationRuleGroup getValidationRuleGroup( int id )
    {
        return i18n( i18nService, validationRuleGroupStore.get( id ) );
    }

    public ValidationRuleGroup getValidationRuleGroup( int id, boolean i18nValidationRules )
    {
        ValidationRuleGroup group = getValidationRuleGroup( id );

        if ( i18nValidationRules )
        {
            i18n( i18nService, group.getMembers() );
        }

        return group;
    }

    public ValidationRuleGroup getValidationRuleGroup( String uid )
    {
        return i18n( i18nService, validationRuleGroupStore.getByUid( uid ) );
    }

    public Collection<ValidationRuleGroup> getAllValidationRuleGroups()
    {
        return i18n( i18nService, validationRuleGroupStore.getAll() );
    }

    public ValidationRuleGroup getValidationRuleGroupByName( String name )
    {
        return i18n( i18nService, validationRuleGroupStore.getByName( name ) );
    }

    public int getValidationRuleGroupCount()
    {
        return validationRuleGroupStore.getCount();
    }

    public int getValidationRuleGroupCountByName( String name )
    {
        return getCountByName( i18nService, validationRuleGroupStore, name );
    }

    public Collection<ValidationRuleGroup> getValidationRuleGroupsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, validationRuleGroupStore, first, max );
    }

    public Collection<ValidationRuleGroup> getValidationRuleGroupsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, validationRuleGroupStore, name, first, max );
    }
}
