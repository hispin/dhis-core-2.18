package org.hisp.dhis.web.api.service;

public class NotAllowedException
    extends Exception
{

    private String reason;

    public NotAllowedException( String reason )
    {
        this.reason = reason;
    }

    public NotAllowedException( String reason, String message )
    {
        super(message);
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }


}
