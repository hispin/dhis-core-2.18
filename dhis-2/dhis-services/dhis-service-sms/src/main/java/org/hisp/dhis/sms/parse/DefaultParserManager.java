package org.hisp.dhis.sms.parse;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author Christian and Magnus
 */
public class DefaultParserManager
    implements ParserManager
{

    private CurrentUserService currentUserService;

    private DataValueService dataValueService;

    private UserService userService;

    private SMSCommandService smsCommandService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    public void parse( String sender, String message )
        throws SMSParserException
    {

        OrganisationUnit orgunit = getOrganisationUnit( sender );

        if ( orgunit == null )
        {
            throw new SMSParserException( "No user associated with this phone number. Please contact your supervisor." );
        }

        if ( message.indexOf( " " ) < 1 )
        {
            throw new SMSParserException( "No command in SMS" );
        }

        String commandString = message.substring( 0, message.indexOf( " " ) );
        message = message.substring( commandString.length() );

        boolean foundCommand = false;

        for ( SMSCommand command : smsCommandService.getSMSCommands() )
        {
            if ( command.getName().equalsIgnoreCase( commandString ) )
            {
                foundCommand = true;
                if ( ParserType.KEY_VALUE_PARSER.equals( command.getParserType() ) )
                {
                    runKeyValueParser( sender, message, orgunit, command );
                    break;
                }
            }
        }
        if ( !foundCommand )
        {
            throw new SMSParserException( "Command '" + commandString + "' does not exist" );
        }
    }

    private void runKeyValueParser( String sender, String message, OrganisationUnit orgunit, SMSCommand command )
    {
        IParser p = new SMSParserKeyValue();
        if ( !StringUtils.isBlank( command.getSeparator() ) )
        {
            p.setSeparator( command.getSeparator() );
        }

        Map<String, String> parsedMessage = p.parse( message );
        if ( parsedMessage.isEmpty() )
        {
            throw new SMSParserException( command.getDefaultMessage() );
        }

        for ( SMSCode code : command.getCodes() )
        {
            if ( parsedMessage.containsKey( code.getCode().toUpperCase() ) )
            {
                storeDataValue( sender, orgunit, parsedMessage, code );
            }
        }
    }

    private void storeDataValue( String sender, OrganisationUnit orgunit, Map<String, String> parsedMessage,
        SMSCode code )
    {
        String upperCaseCode = code.getCode().toUpperCase();

        String storedBy = currentUserService.getCurrentUsername();

        if ( StringUtils.isBlank( storedBy ) )
        {
            storedBy = "[unknown] from [" + sender + "]";
        }

        DataElementCategoryOptionCombo optionCombo = null;
        optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( code.getOptionId() );

        Period period = code.getDataElement().getPeriodType().createPeriod();
        CalendarPeriodType cpt = (CalendarPeriodType) period.getPeriodType();
        period = cpt.getPreviousPeriod( period );

        DataValue dv = dataValueService.getDataValue( orgunit, code.getDataElement(), period, optionCombo );

        if ( dv == null )
        {
            // New data element
            DataValue dataVal = new DataValue();
            dataVal.setOptionCombo( optionCombo );
            dataVal.setSource( orgunit );
            dataVal.setDataElement( code.getDataElement() );
            dataVal.setPeriod( period );
            dataVal.setComment( "" );
            dataVal.setTimestamp( new java.util.Date() );
            dataVal.setStoredBy( storedBy );
            dataVal.setValue( parsedMessage.get( upperCaseCode ) );
            dataValueService.addDataValue( dataVal );
        }
        else
        {
            // Update data element
            dv.setValue( parsedMessage.get( upperCaseCode ) );
            dv.setOptionCombo( optionCombo );
            dataValueService.updateDataValue( dv );
        }
    }

    private OrganisationUnit getOrganisationUnit( String sender )
    {
        OrganisationUnit orgunit = null;
        for ( User user : userService.getUsersByPhoneNumber( sender ) )
        {
            OrganisationUnit ou = user.getOrganisationUnit();

            // Might be undefined if the user has more than one org.units
            // "attached"
            if ( orgunit == null )
            {
                orgunit = ou;
            }
            else if ( orgunit.getId() == ou.getId() )
            {
                // same orgunit, no problem...
            }
            else
            {
                throw new SMSParserException(
                    "User is associated with more than one orgunit. Please contact your supervisor." );
            }
        }
        return orgunit;
    }

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    @Required
    public void setSmsCommandService( SMSCommandService smsCommandService )
    {
        this.smsCommandService = smsCommandService;
    }

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Required
    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

}
