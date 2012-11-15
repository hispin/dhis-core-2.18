package org.hisp.dhis.sms.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.smscommand.SMSCode;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Christian and Magnus
 */
public class DefaultParserManager implements ParserManager {

    private CurrentUserService currentUserService;

    private DataValueService dataValueService;

    private UserService userService;

    private SMSCommandService smsCommandService;

    private OutboundSmsService outboundSmsService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    @Transactional
    public void parse(IncomingSms sms) {
        try {
            parse(sms.getOriginator(), sms.getText());
        } catch (SMSParserException e) {
            sendSMS(e.getMessage(), sms.getOriginator());
            return;
        }
        // sendSMS("Your data report has been received", sms.getOriginator());
    }

    private void sendSMS(String message, String sender) {
        if (outboundSmsService != null) {
            outboundSmsService.sendMessage(new OutboundSms(message, sender), null);
        } else {
            // Just for testing
            System.out.println("\n\n\n SMS: " + message + "\n\n\n");
        }
    }

    @Transactional
    private void parse(String sender, String message) throws SMSParserException {

        if (StringUtils.isEmpty(sender)) {
            return;
        }

        User user = getUser(sender);

        if (user == null) {
            throw new SMSParserException("No user associated with this phone number. Please contact your supervisor.");
        }

        if (StringUtils.isEmpty(message)) {
            throw new SMSParserException("No command in SMS");
        }

        String commandString = message.substring(0, message.indexOf(" "));
        message = message.substring(commandString.length());

        boolean foundCommand = false;

        for (SMSCommand command : smsCommandService.getSMSCommands()) {
            if (command.getName().equalsIgnoreCase(commandString)) {
                foundCommand = true;
                if (ParserType.KEY_VALUE_PARSER.equals(command.getParserType())) {
                    runKeyValueParser(sender, message, user, command);
                    break;
                }
            }
        }
        if (!foundCommand) {
            throw new SMSParserException("Command '" + commandString + "' does not exist");
        }
    }

    private void runKeyValueParser(String sender, String message, User user, SMSCommand command) {
        IParser p = new SMSParserKeyValue();
        if (!StringUtils.isBlank(command.getSeparator())) {
            p.setSeparator(command.getSeparator());
        }

        Map<String, String> parsedMessage = p.parse(message);

        Date date = lookForDate(message);

        boolean valueStored = false;

        for (SMSCode code : command.getCodes()) {
            if (parsedMessage.containsKey(code.getCode().toUpperCase())) {
                storeDataValue(sender, user.getOrganisationUnit(), parsedMessage, code, date);
                valueStored = true;
            }
        }

        if (parsedMessage.isEmpty() || !valueStored) {
            if (StringUtils.isEmpty(command.getDefaultMessage())) {
                throw new SMSParserException("No values reported for command '" + command.getName() + "'");
            } else {
                throw new SMSParserException(command.getDefaultMessage());
            }
        }

        sendSuccessFeedback(sender, command, parsedMessage);

    }

    protected void sendSuccessFeedback(String sender, SMSCommand command, Map<String, String> parsedMessage) {
        String reportBack = "Your report included the following values: ";
        String notInReport = "Your report did not include values for: ";
        boolean missingElements = false;
        for (SMSCode code : command.getCodes()) {
            if (parsedMessage.containsKey(code.getCode().toUpperCase())) {
                reportBack += code.getCode() + "=" + parsedMessage.get(code.getCode().toUpperCase()) + " ";
            } else {
                notInReport += code.getCode() + ",";
                missingElements = true;
            }
        }

        notInReport = notInReport.substring(0,notInReport.length() - 1);

        if (missingElements) {
            sendSMS(reportBack + notInReport, sender);
        } else {
            sendSMS(reportBack, sender);
        }
    }

    private Date lookForDate(String message) {
        if (!message.contains(" ")) {
            return null;
        }
        Date date = null;
        String dateString = message.trim().split(" ")[0];
        SimpleDateFormat format = new SimpleDateFormat("ddMM");

        try {
            Calendar cal = Calendar.getInstance();
            date = format.parse(dateString);
            cal.setTime(date);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            if (cal.get(Calendar.MONTH) < month) {
                cal.set(Calendar.YEAR, year);
            } else {
                cal.set(Calendar.YEAR, year - 1);
            }
            date = cal.getTime();
            System.out.println("\nFound date in SMS:" + date.toString() + "\n");
        } catch (Exception e) {
            // no date found
            System.out.println("\nNo date found in SMS \n");
        }
        return date;
    }

    private void storeDataValue(String sender, OrganisationUnit orgunit, Map<String, String> parsedMessage,
            SMSCode code, Date date) {
        String upperCaseCode = code.getCode().toUpperCase();

        String storedBy = currentUserService.getCurrentUsername();

        if (StringUtils.isBlank(storedBy)) {
            storedBy = "[unknown] from [" + sender + "]";
        }

        DataElementCategoryOptionCombo optionCombo = null;
        optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo(code.getOptionId());

        Period period = code.getDataElement().getPeriodType().createPeriod();
        CalendarPeriodType cpt = (CalendarPeriodType) period.getPeriodType();
        period = cpt.getPreviousPeriod(period);

        if (date != null) {
            period = cpt.createPeriod(date);
        }

        DataValue dv = dataValueService.getDataValue(orgunit, code.getDataElement(), period, optionCombo);

        if (dv == null) {
            // New data element
            DataValue dataVal = new DataValue();
            dataVal.setOptionCombo(optionCombo);
            dataVal.setSource(orgunit);
            dataVal.setDataElement(code.getDataElement());
            dataVal.setPeriod(period);
            dataVal.setComment("");
            dataVal.setTimestamp(new java.util.Date());
            dataVal.setStoredBy(storedBy);
            dataVal.setValue(parsedMessage.get(upperCaseCode));
            dataValueService.addDataValue(dataVal);
        } else {
            // Update data element
            dv.setValue(parsedMessage.get(upperCaseCode));
            dv.setOptionCombo(optionCombo);
            dataValueService.updateDataValue(dv);
        }

    }

    private User getUser(String sender) {
        OrganisationUnit orgunit = null;
        User user = null;
        for (User u : userService.getUsersByPhoneNumber(sender)) {
            OrganisationUnit ou = u.getOrganisationUnit();

            // Might be undefined if the user has more than one org.units
            // "attached"
            if (orgunit == null) {
                orgunit = ou;
            } else if (orgunit.getId() == ou.getId()) {
                // same orgunit, no problem...
            } else {
                throw new SMSParserException(
                        "User is associated with more than one orgunit. Please contact your supervisor.");
            }
            user = u;
        }
        return user;
    }

    public void setDataElementCategoryService(DataElementCategoryService dataElementCategoryService) {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    @Required
    public void setSmsCommandService(SMSCommandService smsCommandService) {
        this.smsCommandService = smsCommandService;
    }

    @Required
    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Required
    public void setDataValueService(DataValueService dataValueService) {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setOutboundSmsService(OutboundSmsService outboundSmsService) {
        this.outboundSmsService = outboundSmsService;
    }

}
