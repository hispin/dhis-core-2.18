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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        log.info( "Validate startDate=" + startDate + " endDate=" + endDate + " sources[" + sources.size() + "]" );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = getAllValidationRules();
        return validateInternal( sources, periods, rules, ValidationRunType.INTERACTIVE, null );
    }

    public Collection<ValidationResult> validate( Date startDate, Date endDate, Collection<OrganisationUnit> sources,
        ValidationRuleGroup group )
    {
    	log.info( "Validate startDate=" + startDate + " endDate=" + endDate + " sources[" + sources.size() + "] group=" + group.getName() );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = group.getMembers();
        return validateInternal( sources, periods, rules, ValidationRunType.INTERACTIVE, null );
    }

    public Collection<ValidationResult> validate( Date startDate, Date endDate, OrganisationUnit source )
    {
    	log.info( "Validate startDate=" + startDate + " endDate=" + endDate + " source=" + source.getName() );
        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
        Collection<ValidationRule> rules = getAllValidationRules();
        return validateInternal( source, periods, rules, ValidationRunType.INTERACTIVE, null );
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
        
        log.info( "alertRun sources[" + sources.size() + "] periods[" + periods.size() + "] rules[" + rules.size()
            + "] last run " + lastAlertRun == null ? "(none)" : lastAlertRun );
        
        Collection<ValidationResult> results = validateInternal( sources, periods, rules, ValidationRunType.ALERT,
            lastAlertRun );
        
        log.trace( "alertRun() results[" + results.size() + "]" );
        
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
     * Evaluates validation rules for a collection of organisation units.
     * This method breaks the job down by organisation unit. It assigns the
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
        ValidationRunContext context = ValidationRunContext.getNewValidationRunContext( sources, periods, rules,
        		constantService.getConstantMap(), ValidationRunType.ALERT, lastAlertRun,
        		expressionService, periodService, dataValueService);

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

        for ( OrganisationUnitExtended sourceX : context.getSourceXs() )
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
        return context.getValidationResults();
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
            CalendarPeriodType calendarPeriodType = ( CalendarPeriodType ) periodType;
            Period currentPeriod = calendarPeriodType.createPeriod();
            Period previousPeriod = calendarPeriodType.getPreviousPeriod( currentPeriod );
            periods.addAll( periodService.getIntersectingPeriodsByPeriodType( periodType,
                previousPeriod.getStartDate(), currentPeriod.getEndDate() ) );
            // Note: If the last successful daily run was more than one day
            // ago, we might consider adding some additional periods of type 
            // DailyPeriodType so we don't miss any alerts.
        }

        return periods;
    }

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
        // subset of results. (Not necessarily the same as the set of users who
        // receive alerts from the same subset of validation rules -- because
        // some of these rules may return no results.) This saves on message
        // storage space.

        // TODO: Encapsulate this in another level of Map by the user's
        // language, and generate a message for each combination of ( target
        // language, set of results )
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
        messageBuilder
            .append( "<html>" )
            .append( "<head>" ).append( "</head>" )
            .append( "<body>" ).append( subject ).append( "<br />" )
            .append( "<table>" )
            .append( "<tr>" )
            .append( "<th>Organisation Unit</th>" )
            .append( "<th>Period</th>" )
            .append( "<th>Importance</th>" )
            .append( "<th>Left side description</th>" )
            .append( "<th>Value</th>" )
            .append( "<th>Operator</th>" )
            .append( "<th>Value</th>" )
            .append( "<th>Right side description</th>" )
            .append( "</tr>" );

        for ( ValidationResult result : results )
        {
            ValidationRule rule = result.getValidationRule();

            messageBuilder
            	.append( "<tr>" )
            	.append( "<td>" ).append( result.getSource().getName() ).append( "<\td>" )
                .append( "<td>" ).append( result.getPeriod().getName() ).append( "<\td>" )
                .append( "<td>" ).append( rule.getImportance() ).append( "<\td>" )
                .append( "<td>" ).append( rule.getLeftSide().getDescription() ).append( "<\td>" )
                .append( "<td>" ).append( result.getLeftsideValue() ).append( "<\td>" )
                .append( "<td>" ).append( rule.getOperator().toString() ).append( "<\td>" )
                .append( "<td>" ).append( result.getRightsideValue() ).append( "<\td>" )
                .append( "<td>" ).append( rule.getRightSide().getDescription() ).append( "<\td>" )
                .append( "</tr>" );
        }

        messageBuilder
        	.append( "</table>" )
        	.append( "</body>" )
        	.append( "</html>" );

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
