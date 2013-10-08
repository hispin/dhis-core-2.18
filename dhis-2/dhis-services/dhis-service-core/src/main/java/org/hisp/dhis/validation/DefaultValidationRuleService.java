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
import static org.hisp.dhis.system.util.MathUtils.expressionIsTrue;
import static org.hisp.dhis.system.util.MathUtils.getRounded;
import static org.hisp.dhis.system.util.MathUtils.zeroIfNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
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
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
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

    /**
     * Defines how many decimal places for rounding the left and right side
     * evaluation values in the report of results.
     */
    private static final int DECIMALS = 1;

    /**
     * Defines the types of alert run.
     */
    private enum ValidationRunType
    {
        INTERACTIVE, ALERT
    }

    /**
     * This private subclass holds information for each organisation unit that
     * is needed during a validation run (either interactive or an alert run).
     * 
     * It is important that they should be copied from Hibernate lazy
     * collections before the multithreaded part of the run starts, otherwise
     * the threads may not be able to access these values.
     */
    private class OrganisationUnitExtended
    {
        OrganisationUnit source;

        Collection<OrganisationUnit> children;

        int level;

        public String toString()
        {
            return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE )
                .append( "\n     name", source.getName() ).append( "\n     children[", children.size() + "]" )
                .append( "\n     level", level ).toString();
        }
    }

    /**
     * This private subclass holds information for each period type that is
     * needed during a validation run (either interactive or an alert run).
     * 
     * By computing these values once at the start of a validation run, we avoid
     * the overhead of having to compute them during the processing of every
     * organisation unit. For some of these properties this is also important
     * because they should be copied from Hibernate lazy collections before the
     * multithreaded part of the run starts, otherwise the threads may not be
     * able to access these values.
     */
    private class PeriodTypeExtended
    {
        PeriodType periodType;

        Collection<Period> periods;

        Collection<ValidationRule> rules;

        Collection<DataElement> dataElements;

        Collection<PeriodType> allowedPeriodTypes;

        Map<OrganisationUnit, Collection<DataElement>> sourceDataElements;

        public String toString()
        {
            return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE )
                .append( "\n periodType", periodType )
                .append( "\n periods", (Arrays.toString( periods.toArray() )) )
                .append( "\n rules", (Arrays.toString( rules.toArray() )) )
                .append( "\n dataElements", (Arrays.toString( dataElements.toArray() )) )
                .append( "\n allowedPeriodTypes", (Arrays.toString( allowedPeriodTypes.toArray() )) )
                .append( "\n sourceDataElements", "[" + sourceDataElements.size() + "]" ).toString();
        }
    }

    /**
     * This private subclass holds common values that are used during a
     * validation run (either interactive or an alert run.) These values don't
     * change during the multi-threaded tasks (except that results entries are
     * added in a threadsafe way.)
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
     */
    private class ValidationRunContext
    {
        private Map<PeriodType, PeriodTypeExtended> PeriodTypeExtendedMap;

        private ValidationRunType runType;

        private Date lastAlertRun;

        private Map<String, Double> constantMap;

        private Collection<OrganisationUnitExtended> sourceXs;

        private Collection<ValidationResult> validationResults;

        public String toString()
        {
            return new ToStringBuilder( this, ToStringStyle.SHORT_PREFIX_STYLE )
                .append( "\n  PeriodTypeExtendedMap", (Arrays.toString( PeriodTypeExtendedMap.entrySet().toArray() )) )
                .append( "\n  runType", runType ).append( "\n  lastAlertRun", lastAlertRun )
                .append( "\n  constantMap", "[" + constantMap.size() + "]" )
                .append( "\n  sourceXs", Arrays.toString( sourceXs.toArray() ) )
                .append( "\n  validationResults", Arrays.toString( validationResults.toArray() ) ).toString();
        }
    }

    /**
     * Runs a validation task on a thread within a multi-threaded validation
     * run.
     * 
     * Each thread looks for validation results in a different organisation
     * unit.
     */
    private class ValidationWorkerThread
        implements Runnable
    {
        private OrganisationUnitExtended sourceX;

        private ValidationRunContext context;

        private ValidationWorkerThread( OrganisationUnitExtended sourceX, ValidationRunContext context )
        {
            this.sourceX = sourceX;
            this.context = context;
        }

        @Override
        public void run()
        {
            validateSource( sourceX, context );
        }
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleStore validationRuleStore;

    public void setValidationRuleStore( ValidationRuleStore validationRuleStore )
    {
        this.validationRuleStore = validationRuleStore;
    }

    private GenericIdentifiableObjectStore<ValidationRuleGroup> validationRuleGroupStore;

    public void setValidationRuleGroupStore(
        GenericIdentifiableObjectStore<ValidationRuleGroup> validationRuleGroupStore )
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
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
        log.info( "validate( startDate=" + startDate + " endDate=" + endDate + " sources[" + sources.size() + "] )" );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = getAllValidationRules();
        return validateInternal( sources, periods, rules, ValidationRunType.INTERACTIVE, null );
    }

    public Collection<ValidationResult> validate( Date startDate, Date endDate, Collection<OrganisationUnit> sources,
        ValidationRuleGroup group )
    {
        log.info( "validate( startDate=" + startDate + " endDate=" + endDate + " sources[" + sources.size()
            + "] group=" + group.getName() + " )" );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = group.getMembers();
        return validateInternal( sources, periods, rules, ValidationRunType.INTERACTIVE, null );
    }

    public Collection<ValidationResult> validate( Date startDate, Date endDate, OrganisationUnit source )
    {
        log.info( "validate( startDate=" + startDate + " endDate=" + endDate + " source=" + source.getName() + " )" );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = getAllValidationRules();
        return validateInternal( source, periods, rules, ValidationRunType.INTERACTIVE, null );
    }

    public Collection<ValidationResult> validate( DataSet dataSet, Period period, OrganisationUnit source )
    {
        log.info( "validate( dataSet=" + dataSet.getName() + " period=[" + period.getPeriodType().getName() + " "
            + period.getStartDate() + " " + period.getEndDate() + "]" + " source=" + source.getName() + " )" );
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

        return validateInternal( source, periods, rules, ValidationRunType.INTERACTIVE, null );
    }

    // TODO: Schedule this to run every night.
    public void alertRun()
    {
        // Find all the rules belonging to groups that will send alerts to user roles.
        Set<ValidationRule> rules = new HashSet<ValidationRule>();
        for ( ValidationRuleGroup validationRuleGroup : getAllValidationRuleGroups() )
        {
            Collection<UserAuthorityGroup> userRolesToAlert = validationRuleGroup.getUserAuthorityGroupsToAlert();
            if ( userRolesToAlert != null && !userRolesToAlert.isEmpty() )
            {
                rules.addAll( validationRuleGroup.getMembers() );
            }
        }

        Collection<OrganisationUnit> sources = organisationUnitService.getAllOrganisationUnits();
        Collection<Period> periods = getAlertPeriodsFromRules( rules );
        Date lastAlertRun = (Date) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_LAST_ALERT_RUN );
        
        // Any database changes after this moment will contribute to the next run.
        
        Date thisAlertRun = new Date();
        
        log.info( "alertRun() sources[" + sources.size() + "] periods[" + periods.size() + "] rules[" + rules.size()
            + "] last run " + lastAlertRun == null ? "(none)" : lastAlertRun );
        Collection<ValidationResult> results = validateInternal( sources, periods, rules, ValidationRunType.ALERT,
            lastAlertRun );
        log.info( "alertRun() results[" + results.size() + "]" );
        
        if ( !results.isEmpty() )
        {
            postAlerts( results, thisAlertRun ); // Alert the users.
        }
        
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_LAST_ALERT_RUN, thisAlertRun );
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    /**
     * Evaluates validation rules for a single organisation unit.
     * 
     * @param source the organisation unit in which to run the validation rules
     * @param periods the periods of data to check
     * @param rules the ValidationRules to evaluate
     * @param runType whether this is an interactive or alert run
     * @param lastAlertRun date/time of the most recent successful alerts run
     *        (needed only for alert run)
     * @return a collection of any validations that were found
     */
    private Collection<ValidationResult> validateInternal( OrganisationUnit source, Collection<Period> periods,
        Collection<ValidationRule> rules, ValidationRunType runType, Date lastAlertRun )
    {
        Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();
        sources.add( source );
        return validateInternal( sources, periods, rules, ValidationRunType.INTERACTIVE, null );
    }

    /**
     * Evaluates validation rules for a collection of organisation units. This
     * method breaks the job down by organisation unit. It assigns the
     * evaluation for each organisation unit to a task that can be evaluated
     * independently in a multithreaded environment.
     * 
     * @param sources the organisation units in which to run the validation
     *        rules
     * @param periods the periods of data to check
     * @param rules the ValidationRules to evaluate
     * @param runType whether this is an INTERACTIVE or ALERT run
     * @param lastAlertRun date/time of the most recent successful alerts run
     *        (needed only for alert run)
     * @return a collection of any validations that were found
     */
    private Collection<ValidationResult> validateInternal( Collection<OrganisationUnit> sources,
        Collection<Period> periods, Collection<ValidationRule> rules, ValidationRunType runType, Date lastAlertRun )
    {
        ValidationRunContext context = buildNewContext( sources, periods, rules, ValidationRunType.ALERT, lastAlertRun );
        boolean singleThreadedOption = false;

        if ( singleThreadedOption )
        {
            for ( OrganisationUnitExtended sourceX : context.sourceXs )
            {
                validateSource( sourceX, context );
            }
        }
        else
        {
            int threadPoolSize = SystemUtils.getCpuCores();
            if ( threadPoolSize > 2 )
            {
                threadPoolSize--;
            }
            if ( threadPoolSize > sources.size() )
            {
                threadPoolSize = sources.size();
            }

            ExecutorService executor = Executors.newFixedThreadPool( threadPoolSize );

            for ( OrganisationUnitExtended sourceX : context.sourceXs )
            {
                Runnable worker = new ValidationWorkerThread( sourceX, context );
                executor.execute( worker );
            }

            executor.shutdown();
            try
            {
                executor.awaitTermination( 23, TimeUnit.HOURS );
            }
            catch ( InterruptedException e )
            {
                executor.shutdownNow();
            }
        }
        return context.validationResults;
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
    private ValidationRunContext buildNewContext( Collection<OrganisationUnit> sources, Collection<Period> periods,
        Collection<ValidationRule> rules, ValidationRunType runType, Date lastAlertRun )
    {
        ValidationRunContext context = new ValidationRunContext();
        context.runType = runType;
        context.lastAlertRun = lastAlertRun;
        context.validationResults = new ConcurrentLinkedQueue<ValidationResult>(); // thread-safe
        context.PeriodTypeExtendedMap = new HashMap<PeriodType, PeriodTypeExtended>();
        context.sourceXs = new HashSet<OrganisationUnitExtended>();

        context.constantMap = new HashMap<String, Double>();
        context.constantMap.putAll( constantService.getConstantMap() );

        // Group the periods by period type.
        for ( Period period : periods )
        {
            PeriodTypeExtended periodTypeX = getOrCreatePeriodTypeExtended( context, period.getPeriodType() );
            periodTypeX.periods.add( period );
        }

        for ( ValidationRule rule : rules )
        {
            // Find the period type extended for this rule
            PeriodTypeExtended periodTypeX = getOrCreatePeriodTypeExtended( context, rule.getPeriodType() ); 
            periodTypeX.rules.add( rule ); // Add this rule to the period type ext.
            
            if ( rule.getCurrentDataElements() != null )
            {
                // Add this rule's data elements to the period extended.
                periodTypeX.dataElements.addAll( rule.getCurrentDataElements() );
            }
            // Add the allowed period types for this rule's data elements:
            periodTypeX.allowedPeriodTypes.addAll( getAllowedPeriodTypesForDataElements( rule.getCurrentDataElements(),
                rule.getPeriodType() ) );
        }

        // We only need to keep period types that are selected and also used by
        // rules that are selected.
        // Start by making a defensive copy so we can delete while iterating.
        Set<PeriodTypeExtended> periodTypeXs = new HashSet<PeriodTypeExtended>( context.PeriodTypeExtendedMap.values() );
        for ( PeriodTypeExtended periodTypeX : periodTypeXs )
        {
            if ( periodTypeX.periods.isEmpty() || periodTypeX.rules.isEmpty() )
            {
                context.PeriodTypeExtendedMap.remove( periodTypeX.periodType );
            }
        }

        for ( OrganisationUnit source : sources )
        {
            OrganisationUnitExtended sourceX = new OrganisationUnitExtended();
            sourceX.source = source;
            sourceX.children = new HashSet<OrganisationUnit>( source.getChildren() );
            sourceX.level = source.getOrganisationUnitLevel();
            context.sourceXs.add( sourceX );

            Map<PeriodType, Set<DataElement>> sourceDataElementsByPeriodType = source
                .getDataElementsInDataSetsByPeriodType();
            for ( PeriodTypeExtended periodTypeX : context.PeriodTypeExtendedMap.values() )
            {
                Collection<DataElement> sourceDataElements = sourceDataElementsByPeriodType
                    .get( periodTypeX.periodType );
                if ( sourceDataElements != null )
                {
                    periodTypeX.sourceDataElements.put( source, sourceDataElements );
                }
                else
                {
                    periodTypeX.sourceDataElements.put( source, new HashSet<DataElement>() );
                }
            }
        }

        return context;
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
    PeriodTypeExtended getOrCreatePeriodTypeExtended( ValidationRunContext context, PeriodType periodType )
    {
        PeriodTypeExtended periodTypeX = context.PeriodTypeExtendedMap.get( periodType );
        if ( periodTypeX == null )
        {
            periodTypeX = new PeriodTypeExtended();
            periodTypeX.periodType = periodType;
            periodTypeX.periods = new HashSet<Period>();
            periodTypeX.rules = new HashSet<ValidationRule>();
            periodTypeX.dataElements = new HashSet<DataElement>();
            periodTypeX.allowedPeriodTypes = new HashSet<PeriodType>();
            periodTypeX.sourceDataElements = new HashMap<OrganisationUnit, Collection<DataElement>>();
            context.PeriodTypeExtendedMap.put( periodType, periodTypeX );
        }
        return periodTypeX;
    }

    /**
     * For an alert run, gets the current and most periods to search, based on
     * the period types from the rules to run.
     * 
     * @param rules the ValidationRules to be evaluated on this alert run
     * @return periods to search for new alerts
     */
    private Collection<Period> getAlertPeriodsFromRules( Collection<ValidationRule> rules )
    {
        Set<Period> periods = new HashSet<Period>();

        // Construct a set of all period types found in validation rules.
        Set<PeriodType> rulePeriodTypes = new HashSet<PeriodType>();
        for ( ValidationRule rule : rules )
        {
            // (Note that we have to get periodType from periodService,
            // otherwise the ID will not be present.)
            rulePeriodTypes.add( periodService.getPeriodTypeByName( rule.getPeriodType().getName() ) );
        }

        // For each period type, find the period containing the current date (if
        // any), and the most recent previous period. Add whichever one(s) of these 
        // are present in the database.
        for ( PeriodType periodType : rulePeriodTypes )
        {
            // This is a bit awkward. The current periodType object is of type
            // periodType, but not of type CalendarPeriodType. In other words, 
            // ( periodType instanceof CalendarPeriodType ) returns false!
            // In order to do periodType calendar math, we want a real
            // "CalendarPeriodType" instance.
            // TODO just cast to calendar period type
            CalendarPeriodType calendarPeriodType = getCalendarPeriodType( periodType );
            if ( calendarPeriodType != null )
            {
                Period currentPeriod = calendarPeriodType.createPeriod();
                Period previousPeriod = calendarPeriodType.getPreviousPeriod( currentPeriod );
                periods.addAll( periodService.getIntersectingPeriodsByPeriodType( periodType,
                    previousPeriod.getStartDate(), currentPeriod.getEndDate() ) );
                // Note: If the last successful daily run was more than one day
                // ago, we might consider adding some additional periods of type 
                // DailyPeriodType so we don't miss any alerts.
            }
        }

        return periods;
    }

    /**
     * Evaluates validation rules for a single organisation unit. This is the
     * central method in validation rule evaluation.
     * 
     * @param sourceX extended object of the organisation unit in which to run
     *        the validation rules
     * @param context the validation run context
     */
    private void validateSource( OrganisationUnitExtended sourceX, ValidationRunContext context )
    {
        if ( context.validationResults.size() < (ValidationRunType.INTERACTIVE == context.runType ? MAX_INTERACTIVE_VIOLATIONS
            : MAX_ALERT_VIOLATIONS) )
        {
            for ( PeriodTypeExtended periodTypeX : context.PeriodTypeExtendedMap.values() )
            {
                Collection<DataElement> sourceDataElements = periodTypeX.sourceDataElements.get( sourceX.source );
                Set<ValidationRule> rules = getRulesBySourceAndPeriodType( sourceX, periodTypeX, context,
                    sourceDataElements );

                if ( !rules.isEmpty() )
                {
                    Set<DataElement> recursiveCurrentDataElements = getRecursiveCurrentDataElements( rules );
                    for ( Period period : periodTypeX.periods )
                    {
                        Map<DataElementOperand, Date> lastUpdatedMap = new HashMap<DataElementOperand, Date>();
                        Set<DataElementOperand> incompleteValues = new HashSet<DataElementOperand>();
                        Map<DataElementOperand, Double> currentValueMap = getDataValueMapRecursive( periodTypeX,
                            periodTypeX.dataElements, sourceDataElements, recursiveCurrentDataElements,
                            periodTypeX.allowedPeriodTypes, period, sourceX.source, lastUpdatedMap, incompleteValues );
                        log.trace( "currentValueMap[" + currentValueMap.size() + "]" );

                        for ( ValidationRule rule : rules )
                        {
                            if ( evaluateCheck( lastUpdatedMap, rule, context ) )
                            {
                                Double leftSide = expressionService.getExpressionValue( rule.getLeftSide(),
                                    currentValueMap, context.constantMap, null, incompleteValues );

                                if ( leftSide != null || Operator.compulsory_pair.equals( rule.getOperator() ) )
                                {
                                    Double rightSide = getRightSideValue( sourceX.source, periodTypeX, period, rule,
                                        currentValueMap, sourceDataElements, context );

                                    if ( rightSide != null || Operator.compulsory_pair.equals( rule.getOperator() ) )
                                    {
                                        boolean violation = false;

                                        if ( Operator.compulsory_pair.equals( rule.getOperator() ) )
                                        {
                                            violation = (leftSide != null && rightSide == null)
                                                || (leftSide == null && rightSide != null);
                                        }
                                        else if ( leftSide != null && rightSide != null )
                                        {
                                            violation = !expressionIsTrue( leftSide, rule.getOperator(), rightSide );
                                        }

                                        if ( violation )
                                        {
                                            context.validationResults.add( new ValidationResult( period,
                                                sourceX.source, rule, getRounded( zeroIfNull( leftSide ), DECIMALS ),
                                                getRounded( zeroIfNull( rightSide ), DECIMALS ) ) );
                                        }

                                        log.trace( "-->Evaluated " + rule.getName() + ": "
                                            + (violation ? "violation" : "OK") + " " + leftSide.toString() + " "
                                            + rule.getOperator() + " " + rightSide.toString() + " ("
                                            + context.validationResults.size() + " results)" );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks to see if the evaluation should go further for this
     * evaluationRule, after the "current" data to evaluate has been fetched.
     * For INTERACTIVE runs, we always go further (always return true.) For
     * ALERT runs, we go further only if something has changed since the last
     * successful alert run -- either the rule definition or one of the
     * "current" data element / option values.
     * 
     * @param lastUpdatedMap when each data value was last updated
     * @param rule the rule that may be evaluated
     * @param context the evaluation run context
     * @return true if the rule should be evaluated with this data, false if not
     */
    private boolean evaluateCheck( Map<DataElementOperand, Date> lastUpdatedMap, ValidationRule rule,
        ValidationRunContext context )
    {
        boolean evaluate = true; // Assume true for now.

        if ( ValidationRunType.ALERT == context.runType )
        {
            if ( context.lastAlertRun != null ) // True if no previous alert run
            {
                if ( rule.getLastUpdated().before( context.lastAlertRun ) )
                {
                    // Get the "current" DataElementOperands from this rule:
                    // Left+Right sides for VALIDATION, Left side only for
                    // MONITORING
                    Collection<DataElementOperand> deos = expressionService.getOperandsInExpression( rule.getLeftSide()
                        .getExpression() );
                    if ( ValidationRule.RULE_TYPE_VALIDATION == rule.getRuleType() )
                    {
                        // Make a copy so we can add to it.
                        deos = new HashSet<DataElementOperand>( deos );                        
                        
                        deos.addAll( expressionService.getOperandsInExpression( rule.getRightSide().getExpression() ) );
                    }

                    // Return true if any data is more recent than the last
                    // ALERT run, otherwise return false.
                    evaluate = false;
                    for ( DataElementOperand deo : deos )
                    {
                        Date lastUpdated = lastUpdatedMap.get( deo );
                        if ( lastUpdated != null && lastUpdated.after( context.lastAlertRun ) )
                        {
                            evaluate = true; // True if new/updated data.
                            break;
                        }
                    }
                }
            }
        }
        return evaluate;
    }

    /**
     * Gets the rules that should be evaluated for a given organisation unit and
     * period type.
     * 
     * @param sourceX the organisation unit extended information
     * @param periodTypeX the period type extended information
     * @param context the alert run context
     * @param sourceDataElements all data elements collected for this
     *        organisation unit
     * @return
     */
    private Set<ValidationRule> getRulesBySourceAndPeriodType( OrganisationUnitExtended sourceX,
        PeriodTypeExtended periodTypeX, ValidationRunContext context, Collection<DataElement> sourceDataElements )
    {
        Set<ValidationRule> periodTypeRules = new HashSet<ValidationRule>();

        for ( ValidationRule rule : periodTypeX.rules )
        {
            if ( (ValidationRule.RULE_TYPE_VALIDATION.equals( rule.getRuleType() )) )
            {
                // For validation-type rules, include only rules where the
                // organisation collects all the data elements in the rule.
                // But if this is some funny kind of rule with no elements (like
                // for testing), include it also.
                Collection<DataElement> elements = rule.getCurrentDataElements();
                if ( elements == null || elements.size() == 0 || sourceDataElements.containsAll( elements ) )
                {
                    periodTypeRules.add( rule );
                }
            }
            else
            {
                // For monitoring-type rules, include only rules for this
                // organisation's unit level.
                // The organisation may not be configured for the data elements
                // because they could be aggregated from a lower level.
                if ( rule.getOrganisationUnitLevel() == sourceX.level )
                {
                    periodTypeRules.add( rule );
                }
            }
        }
        
        return periodTypeRules;
    }

    /**
     * Gets the data elements for which values should be fetched recursively if
     * they are not collected for an organisation unit.
     * 
     * @param rules ValidationRules to be evaluated
     * @return the data elements to fetch recursively
     */
    private Set<DataElement> getRecursiveCurrentDataElements( Set<ValidationRule> rules )
    {
        Set<DataElement> recursiveCurrentDataElements = new HashSet<DataElement>();

        for ( ValidationRule rule : rules )
        {
            if ( ValidationRule.RULE_TYPE_MONITORING.equals( rule.getRuleType() )
                && rule.getCurrentDataElements() != null )
            {
                recursiveCurrentDataElements.addAll( rule.getCurrentDataElements() );
            }
        }
        
        return recursiveCurrentDataElements;
    }

    /**
     * Returns the right-side evaluated value of the validation rule.
     * 
     * @param source organisation unit being evaluated
     * @param periodTypeX period type being evaluated
     * @param period period being evaluated
     * @param rule ValidationRule being evaluated
     * @param currentValueMap current values already fetched
     * @param sourceDataElements the data elements collected by the organisation
     *        unit
     * @param context the validation run context
     * @return the right-side value
     */
    private Double getRightSideValue( OrganisationUnit source, PeriodTypeExtended periodTypeX, Period period,
        ValidationRule rule, Map<DataElementOperand, Double> currentValueMap,
        Collection<DataElement> sourceDataElements, ValidationRunContext context )
    {
        Double rightSideValue = null;

        // If ruleType is VALIDATION, the right side is evaluated using the same
        // (current) data values.
        // If ruleType is MONITORING but there are no data elements in the right
        // side, then it doesn't matter
        // what data values we use, so just supply the current data values in
        // order to evaluate the (constant) expression.

        if ( ValidationRule.RULE_TYPE_VALIDATION.equals( rule.getRuleType() )
            || rule.getRightSide().getDataElementsInExpression().isEmpty() )
        {
            rightSideValue = expressionService.getExpressionValue( rule.getRightSide(), currentValueMap,
                context.constantMap, null );
        }
        else
        // ruleType equals MONITORING, and there are some data elements in the
        // right side expression
        {
            CalendarPeriodType calendarPeriodType = getCalendarPeriodType( period.getPeriodType() );

            if ( calendarPeriodType != null )
            {
                Collection<PeriodType> rightSidePeriodTypes = getAllowedPeriodTypesForDataElements(
                    rule.getPastDataElements(), rule.getPeriodType() );
                List<Double> sampleValues = new ArrayList<Double>();
                Calendar yearlyCalendar = PeriodType.createCalendarInstance( period.getStartDate() );
                int annualSampleCount = rule.getAnnualSampleCount() == null ? 0 : rule.getAnnualSampleCount();
                int sequentialSampleCount = rule.getSequentialSampleCount() == null ? 0 : rule
                    .getSequentialSampleCount();

                for ( int annualCount = 0; annualCount <= annualSampleCount; annualCount++ )
                {

                    // Defensive copy because createPeriod mutates Calendar.
                    Calendar calCopy = PeriodType.createCalendarInstance( yearlyCalendar.getTime() );
                    
                    // To track the period at the same time in preceding years.
                    Period yearlyPeriod = calendarPeriodType.createPeriod( calCopy );

                    // For past years, fetch the period at the same time of year
                    // as this period,
                    // and any periods after this period within the
                    // sequentialPeriod limit.
                    // For the year of the stating period, we will only fetch
                    // previous sequential periods.

                    if ( annualCount > 0 )
                    {
                        // Fetch the period at the same time of year as the
                        // starting period.
                        evaluateRightSidePeriod( periodTypeX, sampleValues, source, rightSidePeriodTypes, yearlyPeriod,
                            rule, sourceDataElements, context );

                        // Fetch the sequential periods after this prior-year
                        // period.
                        Period sequentialPeriod = new Period( yearlyPeriod );
                        for ( int sequentialCount = 0; sequentialCount < sequentialSampleCount; sequentialCount++ )
                        {
                            sequentialPeriod = calendarPeriodType.getNextPeriod( sequentialPeriod );
                            evaluateRightSidePeriod( periodTypeX, sampleValues, source, rightSidePeriodTypes,
                                sequentialPeriod, rule, sourceDataElements, context );
                        }
                    }

                    // Fetch the seqential periods before this period (both this
                    // year and past years):
                    Period sequentialPeriod = new Period( yearlyPeriod );
                    for ( int sequentialCount = 0; sequentialCount < sequentialSampleCount; sequentialCount++ )
                    {
                        sequentialPeriod = calendarPeriodType.getPreviousPeriod( sequentialPeriod );
                        evaluateRightSidePeriod( periodTypeX, sampleValues, source, rightSidePeriodTypes,
                            sequentialPeriod, rule, sourceDataElements, context );
                    }

                    // Move to the previous year:
                    yearlyCalendar.set( Calendar.YEAR, yearlyCalendar.get( Calendar.YEAR ) - 1 );
                }
                
                rightSideValue = rightSideAverage( rule, sampleValues, annualSampleCount, sequentialSampleCount );
            }
        }
        return rightSideValue;
    }

    /**
     * Evaluates the right side of a monitoring-type validation rule for a given
     * organisation unit and period, and adds the value to a list of sample
     * values.
     * 
     * Note that for a monitoring-type rule, evaluating the right side
     * expression can result in sampling multiple periods and/or child
     * organisation units.
     * 
     * @param periodTypeX the period type extended information
     * @param sampleValues the list of sample values to add to
     * @param source the organisation unit
     * @param allowedPeriodTypes the period types in which the data may exist
     * @param period the main period for the validation rule evaluation
     * @param rule the monitoring-type rule being evaluated
     * @param sourceDataElements the data elements configured for this
     *        organisation unit
     * @param context the evaluation run context
     */
    private void evaluateRightSidePeriod( PeriodTypeExtended periodTypeX, List<Double> sampleValues,
        OrganisationUnit source, Collection<PeriodType> allowedPeriodTypes, Period period, ValidationRule rule,
        Collection<DataElement> sourceDataElements, ValidationRunContext context )
    {
        Period periodInstance = periodService.getPeriod( period.getStartDate(), period.getEndDate(),
            period.getPeriodType() );
        
        if ( periodInstance != null )
        {
            Set<DataElement> dataElements = rule.getRightSide().getDataElementsInExpression();
            Set<DataElementOperand> incompleteValues = new HashSet<DataElementOperand>();
            Map<DataElementOperand, Double> dataValueMap = getDataValueMapRecursive( periodTypeX, dataElements,
                sourceDataElements, dataElements, allowedPeriodTypes, period, source, null, incompleteValues );
            Double value = expressionService.getExpressionValue( rule.getRightSide(), dataValueMap,
                context.constantMap, null, incompleteValues );
            
            if ( value != null )
            {
                sampleValues.add( value );
            }
        }
    }

    /**
     * Finds the average right-side sample value. This is used as the right-side
     * expression value to evaluate a monitoring-type rule.
     * 
     * @param rule monitoring-type rule being evaluated
     * @param sampleValues sample values actually collected
     * @param annualSampleCount number of annual samples tried for
     * @param sequentialSampleCount number of sequential samples tried for
     * @return
     */
    Double rightSideAverage( ValidationRule rule, List<Double> sampleValues, int annualSampleCount,
        int sequentialSampleCount )
    {
        // Find the expected sample count for the last period of its type in the
        // database: sequentialSampleCount for the immediately preceding periods 
        // in this year and for every past year: one sample for the same period 
        // in that year, plus sequentialSampleCounts before and after.
        Double average = null;
        if ( !sampleValues.isEmpty() )
        {
            int expectedSampleCount = sequentialSampleCount + annualSampleCount * (1 + 2 * sequentialSampleCount);
            int highOutliers = rule.getHighOutliers() == null ? 0 : rule.getHighOutliers();
            int lowOutliers = rule.getLowOutliers() == null ? 0 : rule.getLowOutliers();

            // If we had fewer than the expected number of samples, then scale
            // back
            if ( highOutliers + lowOutliers > sampleValues.size() )
            {
                highOutliers = (highOutliers * sampleValues.size()) / expectedSampleCount;
                lowOutliers = (lowOutliers * sampleValues.size()) / expectedSampleCount;
            }

            // If we (still) have any high and/or low outliers to remove, then
            // sort the sample values and remove the high and/or low outliers.
            if ( highOutliers + lowOutliers > 0 )
            {
                Collections.sort( sampleValues );
                log.trace( "Removing " + highOutliers + " high and " + lowOutliers + " low outliers from "
                    + Arrays.toString( sampleValues.toArray() ) );
                sampleValues = sampleValues.subList( lowOutliers, sampleValues.size() - highOutliers );
                log.trace( "Result: " + Arrays.toString( sampleValues.toArray() ) );
            }
            Double sum = 0.0;
            for ( Double sample : sampleValues )
            {
                sum += sample;
            }
            average = sum / sampleValues.size();
        }
        return average;
    }

    /**
     * Gets data values for a given organisation unit and period, recursing if
     * necessary to sum the values from child organisation units.
     * 
     * @param periodTypeX period type which we are evaluating
     * @param ruleDataElements data elements configured for the rule
     * @param sourceDataElements data elements configured for the organisation
     *        unit
     * @param recursiveDataElements data elements for which we will recurse if
     *        necessary
     * @param allowedPeriodTypes all the periods in which we might find the data
     *        values
     * @param period period in which we are looking for values
     * @param source organisation unit for which we are looking for values
     * @param lastUpdatedMap map showing when each data values was last updated
     * @param incompleteValues ongoing list showing which values were found but
     *        not from all children
     * @return the map of values found
     */
    private Map<DataElementOperand, Double> getDataValueMapRecursive( PeriodTypeExtended periodTypeX,
        Collection<DataElement> ruleDataElements, Collection<DataElement> sourceDataElements,
        Set<DataElement> recursiveDataElements, Collection<PeriodType> allowedPeriodTypes, Period period,
        OrganisationUnit source, Map<DataElementOperand, Date> lastUpdatedMap, Set<DataElementOperand> incompleteValues )
    {
        Set<DataElement> dataElementsToGet = new HashSet<DataElement>( ruleDataElements );
        dataElementsToGet.retainAll( sourceDataElements );
        log.trace( "getDataValueMapRecursive: source:" + source.getName() + " elementsToGet["
            + dataElementsToGet.size() + "] allowedPeriodTypes[" + allowedPeriodTypes.size() + "]" );

        Map<DataElementOperand, Double> dataValueMap;
        
        if ( dataElementsToGet.isEmpty() )
        {
            // We still might get something recursively
            dataValueMap = new HashMap<DataElementOperand, Double>();
        }
        else
        {
            dataValueMap = dataValueService.getDataValueMap( dataElementsToGet, period.getStartDate(), source,
                allowedPeriodTypes, lastUpdatedMap );
        }

        // See if there are any data elements we need to get recursively:
        Set<DataElement> recursiveDataElementsNeeded = new HashSet<DataElement>( recursiveDataElements );
        recursiveDataElementsNeeded.removeAll( dataElementsToGet );
        if ( !recursiveDataElementsNeeded.isEmpty() )
        {
            int childCount = 0;
            Map<DataElementOperand, Integer> childValueCounts = new HashMap<DataElementOperand, Integer>();
            
            for ( OrganisationUnit child : source.getChildren() )
            {
                Collection<DataElement> childDataElements = periodTypeX.sourceDataElements.get( child );
                Map<DataElementOperand, Double> childMap = getDataValueMapRecursive( periodTypeX,
                    recursiveDataElementsNeeded, childDataElements, recursiveDataElementsNeeded, allowedPeriodTypes,
                    period, child, lastUpdatedMap, incompleteValues );

                for ( DataElementOperand deo : childMap.keySet() )
                {
                    Double baseValue = dataValueMap.get( deo );
                    dataValueMap.put( deo, baseValue == null ? childMap.get( deo ) : baseValue + childMap.get( deo ) );

                    Integer childValueCount = childValueCounts.get( deo );
                    childValueCounts.put( deo, childValueCount == null ? 1 : childValueCount + 1 );
                }

                childCount++;
            }
            
            for ( Map.Entry<DataElementOperand, Integer> entry : childValueCounts.entrySet() )
            {
                if ( childCount != entry.getValue() )
                {
                    // Found this DataElementOperand value in some but not all children
                    incompleteValues.add( entry.getKey() );
                }
            }
        }

        return dataValueMap;
    }

    /**
     * Returns all validation-type rules which have specified data elements
     * assigned to them.
     * 
     * @param dataElements the data elements to look for
     * @return all validation rules which have the data elements assigned to
     *         them
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
                validationRuleOperands.addAll( expressionService.getOperandsInExpression( rule.getLeftSide()
                    .getExpression() ) );
                validationRuleOperands.addAll( expressionService.getOperandsInExpression( rule.getRightSide()
                    .getExpression() ) );

                if ( operands.containsAll( validationRuleOperands ) )
                {
                    rulesForDataSet.add( rule );
                }
            }
        }

        return rulesForDataSet;
    }

    /**
     * Finds all period types that may contain given data elements, whose period
     * type interval is at least as long as the given period type.
     * 
     * @param dataElements data elements to look for
     * @param periodType the minimum-length period type
     * @return all period types that are allowed for these data elements
     */
    private Collection<PeriodType> getAllowedPeriodTypesForDataElements( Collection<DataElement> dataElements,
        PeriodType periodType )
    {
        Collection<PeriodType> allowedPeriodTypes = new HashSet<PeriodType>();
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
        return allowedPeriodTypes;
    }

    /**
     * Returns an instance of type CalendarPeriodType that matches the specified
     * periodType. This can be needed in order to access the calendar-computing
     * methods that are available in a CalendarPeriodType but not an ordinary
     * PeriodType.
     * 
     * Note: Perhaps this should be moved to PeriodService. Or perhaps some
     * refactoring can be done in the relationship between PeriodType and
     * CalendarPeriodType.
     * 
     * @param periodType the period type of interest
     * @return the corresponding CalendarPeriodType
     */
    private CalendarPeriodType getCalendarPeriodType( PeriodType periodType )
    {
        for ( PeriodType p : PeriodType.PERIOD_TYPES )
        {
            if ( periodType.getName().equals( p.getName() ) )
            {
                if ( p instanceof CalendarPeriodType )
                {
                    return (CalendarPeriodType) p;
                }
                else
                {
                    log.error( "DefaultValidationRuleService.getCalendarPeriodType() - PeriodType.PERIOD_TYPES ["
                        + p.getName() + "] is not a CalendarPeriodType!" );
                }
            }
        }
        return null;
    }

    /**
     * At the end of an ALERT run, post messages to the users who want to see
     * the results.
     * 
     * @param validationResults the set of validation error results
     * @param alertRunStart the date/time when the alert run started
     */
    private void postAlerts( Collection<ValidationResult> validationResults, Date alertRunStart )
    {
        SortedSet<ValidationResult> results = new TreeSet<ValidationResult>( validationResults );

        // Find out which users receive alerts, and which ValidationRules they
        // receive alerts for.
        HashMap<User, Collection<ValidationRule>> userRulesMap = new HashMap<User, Collection<ValidationRule>>();
        for ( ValidationRuleGroup validationRuleGroup : getAllValidationRuleGroups() )
        {
            Collection<UserAuthorityGroup> userRolesToAlert = validationRuleGroup.getUserAuthorityGroupsToAlert();
            if ( userRolesToAlert != null && !userRolesToAlert.isEmpty() )
            {
                for ( UserAuthorityGroup role : userRolesToAlert )
                {
                    for ( UserCredentials userCredentials : role.getMembers() )
                    {
                        User user = userCredentials.getUser();
                        Collection<ValidationRule> userRules = userRulesMap.get( user );
                        if ( userRules == null )
                        {
                            userRules = new ArrayList<ValidationRule>();
                            userRulesMap.put( user, userRules );
                        }
                        userRules.addAll( validationRuleGroup.getMembers() );
                    }
                }
            }
        }

        // We will create one message for each set of users who receive the same
        // subset
        // of results. (Not necessarily the same as the set of users who receive
        // alerts
        // from the same subset of validation rules -- because some of these
        // rules
        // may return no results.) This saves on message storage space.

        // TODO: Encapsulate this in another level of Map by the user's
        // language, and
        // generate a message for each combination of ( target language, set of
        // results )
        Map<List<ValidationResult>, Set<User>> messageMap = new HashMap<List<ValidationResult>, Set<User>>();

        for ( User user : userRulesMap.keySet() )
        {
            // For each user who receives alerts, find the subset of results
            // from this run.
            Collection<ValidationRule> userRules = userRulesMap.get( user );
            List<ValidationResult> userResults = new ArrayList<ValidationResult>();
            Map<String, Integer> importanceSummary = new HashMap<String, Integer>();

            for ( ValidationResult result : results )
            {
                if ( userRules.contains( result.getValidationRule() ) )
                {
                    userResults.add( result );
                    String importance = result.getValidationRule().getImportance();
                    Integer importanceCount = importanceSummary.get( importance );
                    if ( importanceCount == null )
                    {
                        importanceSummary.put( importance, 1 );
                    }
                    else
                    {
                        importanceSummary.put( importance, importanceCount + 1 );
                    }
                }
            }

            // Group this user with other users who have the same subset of
            // results.
            if ( !userResults.isEmpty() )
            {
                Set<User> messageReceivers = messageMap.get( userResults );
                if ( messageReceivers == null )
                {
                    messageReceivers = new HashSet<User>();
                    messageMap.put( userResults, messageReceivers );
                }
                messageReceivers.add( user );
            }
        }

        // For each unique subset of results, send a message to all users
        // receiving that subset of results.
        for ( Map.Entry<List<ValidationResult>, Set<User>> entry : messageMap.entrySet() )
        {
            sendAlertmessage( entry.getKey(), entry.getValue(), alertRunStart );
        }
    }

    /**
     * Generate and send an alert message containing alert results to a set of
     * users.
     * 
     * @param results results to put in this message
     * @param users users to receive these results
     * @param alertRunStart date/time when the alert run started
     */
    private void sendAlertmessage( List<ValidationResult> results, Set<User> users, Date alertRunStart )
    {
        StringBuilder messageBuilder = new StringBuilder();
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );

        // Count the number of messages of each importance type.
        Map<String, Integer> importanceCountMap = new HashMap<String, Integer>();
        for ( ValidationResult result : results )
        {
            Integer importanceCount = importanceCountMap.get( result.getValidationRule().getImportance() );
            importanceCountMap.put( result.getValidationRule().getImportance(), importanceCount == null ? 1
                : importanceCount + 1 );
        }

        // Construct the subject line.
        String subject = "DHIS alerts as of " + dateTimeFormatter.format( alertRunStart ) + " - priority High "
            + (importanceCountMap.get( "high" ) == null ? 0 : importanceCountMap.get( "high" )) + ", Medium "
            + (importanceCountMap.get( "medium" ) == null ? 0 : importanceCountMap.get( "medium" )) + ", Low "
            + (importanceCountMap.get( "low" ) == null ? 0 : importanceCountMap.get( "low" ));

        // Construct the text of the message.
        messageBuilder.append( "<html>\n" ).append( "<head>\n" )
            .append( "</head>\n" )
            .append( "<body>\n" )
            .append( subject )
            .append( "\n" )
            // Repeat the subject line at the start of the message.
            .append( "<br />\n" ).append( "<table>\n" ).append( " <tr>\n" ).append( "  <th>Organisation Unit</th>\n" )
            .append( "  <th>Period</th>\n" ).append( "  <th>Importance</th>\n" )
            .append( "  <th>Left side description</th>\n" ).append( "  <th>Value</th>\n" )
            .append( "  <th>Operator</th>\n" ).append( "  <th>Value</th>\n" )
            .append( "  <th>Right side description</th>\n" ).append( " </tr>\n" );

        for ( ValidationResult result : results )
        {
            ValidationRule rule = result.getValidationRule();

            messageBuilder.append( " <tr>\n" ).append( "  <td>" ).append( result.getSource().getName() )
                .append( "<\td>\n" ).append( "  <td>" ).append( result.getPeriod().getName() ).append( "<\td>\n" )
                .append( "  <td>" ).append( rule.getImportance() ).append( "<\td>\n" ).append( "  <td>" )
                .append( rule.getLeftSide().getDescription() ).append( "<\td>\n" ).append( "  <td>" )
                .append( result.getLeftsideValue() ).append( "<\td>\n" ).append( "  <td>" )
                .append( rule.getOperator().toString() ).append( "<\td>\n" ).append( "  <td>" )
                .append( result.getRightsideValue() ).append( "<\td>\n" ).append( "  <td>" )
                .append( rule.getRightSide().getDescription() ).append( "<\td>\n" ).append( " </tr>\n" );
        }

        messageBuilder.append( "</table>\n" ).append( "</body>\n" ).append( "</html>\n" );

        String messageText = messageBuilder.toString();

        log.info( "postUserResults() users[" + users.size() + "] subject " + subject );
        messageService.sendMessage( subject, messageText, null, users );
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
