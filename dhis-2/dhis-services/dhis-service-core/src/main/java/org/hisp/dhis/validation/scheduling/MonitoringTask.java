package org.hisp.dhis.validation.scheduling;

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

import static org.hisp.dhis.system.notification.NotificationLevel.ERROR;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.validation.ValidationResult;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.hisp.dhis.validation.ValidationRunType;
import org.hisp.dhis.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @author Jim Grace
 */
public class MonitoringTask
    implements Runnable
{
    private static final Log log = LogFactory.getLog( MonitoringTask.class );

    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataValueService dataValueService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private MessageService messageService;

    private TaskId taskId;

    public void setTaskId( TaskId taskId )
    {
        this.taskId = taskId;
    }

    // -------------------------------------------------------------------------
    // Runnable implementation
    // -------------------------------------------------------------------------

    @Override
    public void run()
    {
        notifier.clear( taskId ).notify( taskId, "Monitoring data" );
        
        try
        {
            alertRun();
            
            notifier.notify( taskId, INFO, "Monitoring process done", true );
        }
        catch ( RuntimeException ex )
        {
            notifier.notify( taskId, ERROR, "Process failed: " + ex.getMessage(), true );
            
            messageService.sendFeedback( "Monitoring process failed", "Monitoring process failed, please check the logs.", null );
            
            throw ex;
        }
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    /**
     * Evaluates all the validation rules that could generate alerts,
     * and sends results (if any) to users who should be notified.
     */
    private void alertRun()
    {
        // Find all the rules belonging to groups that will send alerts to user roles.
        
        Set<ValidationRule> rules = getAlertRules();

        Collection<OrganisationUnit> sources = organisationUnitService.getAllOrganisationUnits();
        Set<Period> periods = getAlertPeriodsFromRules( rules );
        Date lastAlertRun = (Date) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_LAST_ALERT_RUN );
        
        // Any database changes after this moment will contribute to the next run.
        
        Date thisAlertRun = new Date();
        
        log.info( "alertRun sources[" + sources.size() + "] periods[" + periods.size() + "] rules[" + rules.size()
            + "] last run " + lastAlertRun == null ? "(none)" : lastAlertRun );
        
        Collection<ValidationResult> results = Validator.validate( sources, periods, rules, ValidationRunType.ALERT,
            lastAlertRun, constantService, expressionService, periodService, dataValueService );
        
        log.trace( "alertRun() results[" + results.size() + "]" );
        
        if ( !results.isEmpty() )
        {
            postAlerts( results, thisAlertRun ); // Alert the users.
        }
        
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_LAST_ALERT_RUN, thisAlertRun );
    }

    /**
     * Gets all the validation rules that could generate alerts.
     * 
     * @return rules that will generate alerts
     */
    private Set<ValidationRule> getAlertRules()
    {
        Set<ValidationRule> rules = new HashSet<ValidationRule>();
        
        for ( ValidationRuleGroup validationRuleGroup : validationRuleService.getAllValidationRuleGroups() )
        {
            Set<UserAuthorityGroup> userRolesToAlert = validationRuleGroup.getUserAuthorityGroupsToAlert();
            
            if ( userRolesToAlert != null && !userRolesToAlert.isEmpty() )
            {
                rules.addAll( validationRuleGroup.getMembers() );
            }
        }
        
        return rules;
    }

    /**
     * Gets the current and most recent periods to search, based on
     * the period types from the rules to run.
     * 
     * For each period type, return the period containing the current date
     * (if any), and the most recent previous period. Add whichever of
     * these periods actually exist in the database.
     * 
     * @param rules the ValidationRules to be evaluated on this alert run
     * @return periods to search for new alerts
     */
    private Set<Period> getAlertPeriodsFromRules( Set<ValidationRule> rules )
    {
        Set<Period> periods = new HashSet<Period>();

        Set<PeriodType> rulePeriodTypes = getPeriodTypesFromRules( rules );

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
     * Gets the Set of period types found in a set of rules.
     * 
     * Note that that we have to get periodType from periodService,
     * otherwise the ID will not be present.)
     * 
     * @param rules validation rules of interest
     * @return period types contained in those rules
     */
    private Set<PeriodType> getPeriodTypesFromRules ( Collection<ValidationRule> rules )
    {
        Set<PeriodType> rulePeriodTypes = new HashSet<PeriodType>();
        
        for ( ValidationRule rule : rules )
        {
            rulePeriodTypes.add( periodService.getPeriodTypeByName( rule.getPeriodType().getName() ) );
        }
        
        return rulePeriodTypes;
    }

    /**
     * At the end of an ALERT run, post messages to the users who want to see
     * the results.
     * 
     * Create one message for each set of users who receive the same
     * subset of results. (Not necessarily the same as the set of users who
     * receive alerts from the same subset of validation rules -- because
     * some of these rules may return no results.) This saves on message
     * storage space.
     * 
     * The message results are sorted into their natural order.
     * 
     * TODO: Internationalize the messages according to the user's
     * preferred language, and generate a message for each combination of
     * ( target language, set of results ).
     * 
     * @param validationResults the set of validation error results
     * @param alertRunStart the date/time when the alert run started
     */
    private void postAlerts( Collection<ValidationResult> validationResults, Date alertRunStart )
    {
        SortedSet<ValidationResult> results = new TreeSet<ValidationResult>( validationResults );

        Map<List<ValidationResult>, Set<User>> messageMap = getMessageMap( results );
        
        for ( Map.Entry<List<ValidationResult>, Set<User>> entry : messageMap.entrySet() )
        {
            sendAlertmessage( entry.getKey(), entry.getValue(), alertRunStart );
        }
    }

    /**
     * Returns a map where the key is a (sorted) list of validation results
     * to assemble into a message, and the value is the set of users who
     * should receive this message.
     * 
     * @param results all the validation run results
     * @return map of result sets to users
     */
    private Map<List<ValidationResult>, Set<User>> getMessageMap( Set<ValidationResult> results )
    {
        Map<User, Set<ValidationRule>> userRulesMap = getUserRulesMap();

        Map<List<ValidationResult>, Set<User>> messageMap = new HashMap<List<ValidationResult>, Set<User>>();

        for ( User user : userRulesMap.keySet() )
        {
            // For users receiving alerts, find the subset of results from run.

            Collection<ValidationRule> userRules = userRulesMap.get( user );
            List<ValidationResult> userResults = new ArrayList<ValidationResult>();

            for ( ValidationResult result : results )
            {
                if ( userRules.contains( result.getValidationRule() ) )
                {
                    userResults.add( result );
                }
            }

            // Group this user with other users having the same result subset.

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
        
        return messageMap;
    }

    /**
     * Constructs a Map where the key is each user who is configured to
     * receive alerts, and the value is a list of rules they should receive
     * results for.
     * 
     * @return Map from users to sets of rules
     */
    private Map<User, Set<ValidationRule>> getUserRulesMap()
    {
        Map<User, Set<ValidationRule>> userRulesMap = new HashMap<User, Set<ValidationRule>>();

        for ( ValidationRuleGroup validationRuleGroup : validationRuleService.getAllValidationRuleGroups() )
        {
            Collection<UserAuthorityGroup> userRolesToAlert = validationRuleGroup.getUserAuthorityGroupsToAlert();
            
            if ( userRolesToAlert != null && !userRolesToAlert.isEmpty() )
            {
                for ( UserAuthorityGroup role : userRolesToAlert )
                {
                    for ( UserCredentials userCredentials : role.getMembers() )
                    {
                        User user = userCredentials.getUser();
                        Set<ValidationRule> userRules = userRulesMap.get( user );
                        if ( userRules == null )
                        {
                            userRules = new HashSet<ValidationRule>();
                            userRulesMap.put( user, userRules );
                        }
                        userRules.addAll( validationRuleGroup.getMembers() );
                    }
                }
            }
        }
        
        return userRulesMap;
    }

    /**
     * Generate and send an alert message containing a list of validation
     * results to a set of users.
     * 
     * @param results results to put in this message
     * @param users users to receive these results
     * @param alertRunStart date/time when the alert run started
     */
    private void sendAlertmessage( List<ValidationResult> results, Set<User> users, Date alertRunStart )
    {
        StringBuilder messageBuilder = new StringBuilder();

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );

        Map<String, Integer> importanceCountMap = countTheResultsByImportanceType( results );

        String subject = "DHIS alerts as of " + dateTimeFormatter.format( alertRunStart ) + " - priority High "
            + ( importanceCountMap.get( "high" ) == null ? 0 : importanceCountMap.get( "high" ) ) + ", Medium "
            + ( importanceCountMap.get( "medium" ) == null ? 0 : importanceCountMap.get( "medium" ) ) + ", Low "
            + ( importanceCountMap.get( "low" ) == null ? 0 : importanceCountMap.get( "low" ) );

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

        log.info( "Alerting users[" + users.size() + "] subject " + subject );
        messageService.sendMessage( subject, messageText, null, users );
    }
    
    /**
     * Counts the results of each importance type, for all the importance
     * types that are found within the results.
     * 
     * @param results results to analyze
     * @return Map showing the result count for each importance type
     */
    private Map<String, Integer> countTheResultsByImportanceType ( List<ValidationResult> results )
    {
        Map<String, Integer> importanceCountMap = new HashMap<String, Integer>();
        
        for ( ValidationResult result : results )
        {
            Integer importanceCount = importanceCountMap.get( result.getValidationRule().getImportance() );
            
            importanceCountMap.put( result.getValidationRule().getImportance(), importanceCount == null ? 1
                : importanceCount + 1 );
        }
        
        return importanceCountMap;
    }
}
