package org.hisp.dhis.sms.input;

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.exolab.castor.types.Date;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsStore;
import org.hisp.dhis.sms.incoming.SmsMessageEncoding;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * 
 * @author Christian and Magnus
 */
public class SMSInput
    implements Action
{

    private String msisdn, sender, message, dca, reffering_batch, network_id, concat_reference, concat_num_segments,
        concat_seq_num, received_time;

    private String source_id; // Probably like message id and should be an int

    private int msg_id; // unique for each sms

    private IncomingSms sms;

    private IncomingSmsStore smsStore;

    // Services
    private CurrentUserService currentUserService;

    private DataValueService dataValueService;

    private UserService userService;

    private SMSCommandService smsCommandService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    private OutboundSmsService outboundSmsService;

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    public SMSInput()
    {
    }

    @Override
    public String execute()
        throws Exception
    {
        System.out.println( "Sender: " + sender + ", Message: " + message );
        IncomingSms sms = new IncomingSms();
        sms.setText( message );
        sms.setOriginator( sender );

        java.util.Date rec = null;
        try
        {
            Date received = Date.parseDate( received_time );
            rec = received.toDate();
        }
        catch ( ParseException pe )
        {
            System.out.println( "ERROR: No received_time input" );
            return ERROR;
        }
        sms.setReceivedDate( rec );
        sms.setSentDate( rec ); // This should probably be removed from incoming
                                // SMS entirely. Though other gateways may use
                                // it?
        sms.setEncoding( SmsMessageEncoding.ENC7BIT );
        sms.setStatus( SmsMessageStatus.INCOMING );
        sms.setId( msg_id );
        sms.setGatewayId( "HARDCODEDTESTGATEWAY" );

        smsStore.save( sms );

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
                sendSMS( "User is associated with more than one orgunit. Please contact your supervisor." );
                return ERROR;
            }
        }

        if ( orgunit == null )
        {
            sendSMS( "No user associated with this phone number. Please contact your supervisor." );
            return ERROR;
        }

        String[] marr = message.trim().split( " " );
        if ( marr.length < 1 )
        {
            sendSMS("No marr");
            return ERROR;
        }
        String commandString = marr[0];

        boolean foundCommand = false;
        for ( SMSCommand command : smsCommandService.getSMSCommands() )
        {
            if ( command.getName().equalsIgnoreCase( commandString ) )
            {
                foundCommand = true;
                // Insert message type handler later :)
                SMSParserKeyValue p = new SMSParserKeyValue();
                if ( !StringUtils.isBlank( command.getSeperator() ) )
                {
                    p.setSeparator( command.getSeperator() );
                }

                Map<String, String> parsedMessage = p.parse( message );
                if ( parsedMessage.isEmpty() )
                {
                    sendSMS( command.getDefaultMessage() );
                    return ERROR;
                }
                for ( SMSCode code : command.getCodes() )
                {

                    String upperCaseCode = code.getCode().toUpperCase();
                    if ( parsedMessage.containsKey( upperCaseCode ) ) // Or fail
                                                                      // hard???
                    {

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

                        DataValue dv = dataValueService.getDataValue( orgunit, code.getDataElement(), period,
                            optionCombo );

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
                }
            }
        }
        if ( foundCommand )
        {
            sendSMS( "SMS successfully received" );
        }
        else
        {
            sendSMS( "Command '" + commandString + "' does not exist" );
            return ERROR;
        }

        // TODO DataEntry stuff
        return SUCCESS;
    }

    private void sendSMS( String message )
    {
        if ( outboundSmsService != null )
        {
            outboundSmsService.sendMessage( new OutboundSms( message, sender ), null );
        }
        else
        {
            // Just for testing
            System.out.println( "\n\n\n SMS: " + message + "\n\n\n" );
        }
    }

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    public void setSmsCommandService( SMSCommandService smsCommandService )
    {
        this.smsCommandService = smsCommandService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public void setSmsStore( IncomingSmsStore smsStore )
    {
        System.out.println( "Setting SMSStore: " + smsStore );
        this.smsStore = smsStore;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public String getConcat_num_segments()
    {
        return concat_num_segments;
    }

    public void setConcat_num_segments( String concat_num_segments )
    {
        this.concat_num_segments = concat_num_segments;
    }

    public String getConcat_reference()
    {
        return concat_reference;
    }

    public void setConcat_reference( String concat_reference )
    {
        this.concat_reference = concat_reference;
    }

    public String getConcat_seq_num()
    {
        return concat_seq_num;
    }

    public void setConcat_seq_num( String concat_seq_num )
    {
        this.concat_seq_num = concat_seq_num;
    }

    public String getDca()
    {
        return dca;
    }

    public void setDca( String dca )
    {
        this.dca = dca;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public int getMsg_id()
    {
        return msg_id;
    }

    public void setMsg_id( int msg_id )
    {
        this.msg_id = msg_id;
    }

    public String getMsisdn()
    {
        return msisdn;
    }

    public void setMsisdn( String msisdn )
    {
        this.msisdn = msisdn;
    }

    public String getNetwork_id()
    {
        return network_id;
    }

    public void setNetwork_id( String network_id )
    {
        this.network_id = network_id;
    }

    public String getReceived_time()
    {
        return received_time;
    }

    public void setReceived_time( String received_time )
    {
        this.received_time = received_time;
    }

    public String getReffering_batch()
    {
        return reffering_batch;
    }

    public void setReffering_batch( String reffering_batch )
    {
        this.reffering_batch = reffering_batch;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender( String sender )
    {
        this.sender = sender;
    }

    public IncomingSms getSms()
    {
        return sms;
    }

    public void setSms( IncomingSms sms )
    {
        this.sms = sms;
    }

    public String getSource_id()
    {
        return source_id;
    }

    public void setSource_id( String source_id )
    {
        this.source_id = source_id;
    }

}
