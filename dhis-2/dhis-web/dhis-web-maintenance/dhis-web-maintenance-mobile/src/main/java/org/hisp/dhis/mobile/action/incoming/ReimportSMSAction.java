package org.hisp.dhis.mobile.action.incoming;

import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.hisp.dhis.sms.parse.ParserManager;
import com.opensymphony.xwork2.Action;

public class ReimportSMSAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ParserManager parserManager;

    private IncomingSmsService incomingSmsService;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String incomingSMSId;

    private IncomingSms incomingSMS;

    public ParserManager getParserManager()
    {
        return parserManager;
    }

    public void setParserManager( ParserManager parserManager )
    {
        this.parserManager = parserManager;
    }

    public IncomingSmsService getIncomingSmsService()
    {
        return incomingSmsService;
    }

    public void setIncomingSmsService( IncomingSmsService incomingSmsService )
    {
        this.incomingSmsService = incomingSmsService;
    }

    public String getIncomingSMSId()
    {
        return incomingSMSId;
    }

    public void setIncomingSMSId( String incomingSMSId )
    {
        this.incomingSMSId = incomingSMSId;
    }

    public IncomingSms getIncomingSMS()
    {
        return incomingSMS;
    }

    public void setIncomingSMS( IncomingSms incomingSMS )
    {
        this.incomingSMS = incomingSMS;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        incomingSMS = incomingSmsService.findBy( Integer.parseInt( incomingSMSId ) );

        if ( incomingSMS == null )
        {
            return "error";
        }
        
        try
        {
            parserManager.parse( incomingSMS );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return SUCCESS;
    }

}
