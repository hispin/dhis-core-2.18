package org.hisp.dhis.mobile.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.mobile.api.SendSMS;
import org.hisp.dhis.mobile.api.SendSMSService;

import com.opensymphony.xwork2.Action;

public class DraftPageAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    SmsService smsService;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    private SendSMSService sendSMSService;
    
    public void setSendSMSService( SendSMSService sendSMSService )
    {
        this.sendSMSService = sendSMSService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------
    
    private String sendPendingSMS;
    
    public void setSendPendingSMS( String sendPendingSMS )
    {
        /*
        int draftCount = sendSMSService.getRowCount();
        
        if( draftCount == 0 )
        {
            this.result = "No Drafts to send";
        }
        else if( draftCount < 30 )
        {
            List<SendSMS> sendSMSList =  new ArrayList<SendSMS>( sendSMSService.getSendSMS( 0, draftCount ) );
            this.result = smsService.sendMessages( sendSMSList );
        }
        else
        {
            List<SendSMS> sendSMSList =  new ArrayList<SendSMS>( sendSMSService.getSendSMS( 0, 29 ) );
            this.result = smsService.sendMessages( sendSMSList );
        }
        */
        this.result = smsService.sendDrafts();
    }

    private boolean smsServiceStatus;

    public boolean getSmsServiceStatus()
    {
        smsServiceStatus = smsService.getServiceStatus();
        return smsServiceStatus;
    }

    private String statAction;

    public void setStatAction( String statAction )
    {
        if ( statAction.equalsIgnoreCase( "Start" ) )
        {
            this.result = smsService.startService();
        } 
        else
        {
            this.result = smsService.stopService();
        }
    }
    
    private String result = "";

    public String getResult()
    {
        return result;
    }

    private List<SendSMS> pendingMsgList;
    
    public List<SendSMS> getPendingMsgList()
    {
        return pendingMsgList;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        pendingMsgList = new ArrayList<SendSMS>( );
        
        if( smsService.getServiceStatus() )
        {
            pendingMsgList.addAll( sendSMSService.getAllSendSMS() );
        }
        
        return SUCCESS;
    }

}
