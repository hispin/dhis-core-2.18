package org.hisp.dhis.sms.parse;

public class SMSParserException
    extends RuntimeException
{
    private String reason;

    private static final long serialVersionUID = -8088120989819092567L;

    public static final String NO_VALUE = "no_value";

    public static final String WRONG_FORMAT = "wrong_format";

    public static final String MORE_THAN_ONE_ORGUNIT = "more_than_one_orgunit";

    public SMSParserException( String message )
    {
        super( message );
    }

    public SMSParserException( String message, String reason )
    {
        super( message );
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason( String reason )
    {
        this.reason = reason;
    }
}
