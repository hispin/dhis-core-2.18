/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.mobile.action;


import com.opensymphony.xwork2.Action;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.hisp.dhis.mobile.BulkSMSHttpInterface;
import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.mobile.api.SendSMS;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitGroupNameComparator;
import org.smslib.http.BulkSmsHTTPGateway;

/**
 * 
 * @author harsh
 */
public class SendBulkSMSThirdPartyAction
    implements Action
{

    SmsService smsService;

    private int succ = 0, modemError = 0;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    boolean smsServiceStatus;

    public boolean getSmsServiceStatus()
    {
        smsServiceStatus = smsService.getServiceStatus();
        return smsServiceStatus;
    }

    String statAction;

    public void setStatAction( String statAction )
    {
        if ( statAction.equalsIgnoreCase( "Start" ) )
        {
            this.result = smsService.startService();
        } else
        {
            this.result = smsService.stopService();
        }
    }

    private String[] strArr;

    private List<String> phonenos;

    BulkSMSHttpInterface conn;

    // OrganisationUnitGroup orgUnitGrp;
    OrganisationUnitGroupService orgUnitGrpService;

    public void setOrgUnitGrpService( OrganisationUnitGroupService orgUnitGrpService )
    {
        this.orgUnitGrpService = orgUnitGrpService;
    }

    private List<OrganisationUnitGroup> availableOrganisationUnitGroups;

    public List<OrganisationUnitGroup> getAvailableOrganisationUnitGroups()
    {
        return availableOrganisationUnitGroups;
    }

    String message;

    public void setMessage( String message )
    {
        this.message = message;
    }

    String tempString;

    public void setTempString( String tempString )
    {
        this.tempString = tempString;
    }

    String selectedIdString;

    public void setSelectedIdString( String selectedIdString )
    {
        this.selectedIdString = selectedIdString;
    }

    String result = "";

    public String getResult()
    {
        return result;
    }

    @Override
    public String execute()
        throws Exception
    {
        //selectedOrganisationUnitGroup=new ArrayList<OrganisationUnitGroup>();

        availableOrganisationUnitGroups = new ArrayList<OrganisationUnitGroup>( orgUnitGrpService.getAllOrganisationUnitGroups() );
        System.out.println( "size(bulksmsmaction)==" + availableOrganisationUnitGroups.size() );
        Collections.sort( availableOrganisationUnitGroups, new OrganisationUnitGroupNameComparator() );

        conn = new BulkSMSHttpInterface();
        phonenos = new ArrayList<String>();


        //System.out.println( tempString );
        if ( selectedIdString != null )
        {
            strArr = selectedIdString.split( "," );

            for ( int i = 0; i < strArr.length; i++ )
            {
                if ( !( strArr[i].equals( "" ) ) )
                {
                    System.out.println( "strArrid=" + strArr[i] );
                    int parseInt = Integer.parseInt( strArr[i] );


                    for ( int j = 0; j < availableOrganisationUnitGroups.size(); j++ )
                    {
                        if ( availableOrganisationUnitGroups.get( j ).getId() == parseInt )
                        {

                            //System.out.println( "  -" + availableOrganisationUnitGroups.get( j ).getMembers().size() );
                            Iterator itr = availableOrganisationUnitGroups.get( j ).getMembers().iterator();
                            while ( itr.hasNext() )
                            {
                                OrganisationUnit temp = (OrganisationUnit) itr.next();
                                if ( temp.getPhoneNumber() != null )
                                {
                                    phonenos.add( temp.getPhoneNumber() );
                                }
                                //  System.out.println( "       phone=" + temp.getPhoneNumber() );
                            }

                        }
                    }
                }
            }

            // fill in the sendSMS fields...
            List<SendSMS> sendSMSList = new ArrayList();
            SendSMS tempSendSMS;
            for ( int i = 0; i < phonenos.size(); i++ )
            {
                tempSendSMS = new SendSMS( phonenos.get( i ), message );
                sendSMSList.add( tempSendSMS );
            }

            /* for testing........
            
            
            List<SendSMS> sendSMSListForTesting=new ArrayList();
            sendSMSListForTesting.add( new SendSMS( "9654", message) );
            sendSMSListForTesting.add( new SendSMS( "9718", message) );
            for (int i=0;i<80;i++){  
            sendSMSListForTesting.add( new SendSMS( "9654", message) );
            sendSMSListForTesting.add( new SendSMS( "9718", message) );       
            }              
            result =smsService.sendMessages( sendSMSListForTesting ); 
             */

            // for third party=
            if ( phonenos.size() > 0 )
            {
                result = conn.sendMessages( message, phonenos );
                System.out.println( result );

                String temp = conn.checkBalance();
                result += "Balance=" + temp;
            } else
            {
                result = "no numbers found";
            }

            /*
            // for gsm modem
            if (!sendSMSList.isEmpty()){
            result=smsService.sendMessages(sendSMSList);
            
            }else result="no numbers found";
             */


            /* for testing...
            if (message.startsWith( "group") )
            result=     sendGroupMessage();
            else if (message.startsWith( "one") )
            result=     sendGroupMessageOneByOne();
            else result="message not yet supported";
            System.out.println("result="+result);
            System.out.println(".............................>>success="+succ+"  error="+modemError);
             */
        }

        return SUCCESS;
        // throw new UnsupportedOperationException("Not supported yet.");
    }
    /* for testing......
    
    
    private String sendGroupMessage() throws FileNotFoundException, IOException
    {
    List<String> phoneno=new ArrayList<String>(); 
    FileReader fr=new FileReader( System.getenv( "DHIS2_HOME")+"test.prop");
    BufferedReader bfr=new BufferedReader( fr );
    
    while(bfr.ready()){
    phoneno.add( bfr.readLine());                
    }
    System.out.println("------group:"+phoneno.get( 0) );
    result=smsService.sendMessageToGroup( "testing", phoneno, message);
    
    return result;
    }
    
    private String sendGroupMessageOneByOne() throws FileNotFoundException, IOException, InterruptedException
    {
    List<String> phonenoList=new ArrayList<String>(); 
    FileReader fr=new FileReader( System.getenv( "DHIS2_HOME")+"test.prop");
    BufferedReader bfr=new BufferedReader( fr );
    String temp;
    while(bfr.ready()){
    
    phonenoList.add( bfr.readLine());                
    // System.out.println("------group:"+phoneno.get( 0) );
    }
    String strDelay;
    int delay;
    strDelay=message.substring( 3, 8);
    delay=Integer.parseInt( strDelay);
    System.out.println("repeat="+Integer.parseInt( message.substring( 8, 12)) );
    for (int j=0;j<Integer.parseInt( message.substring( 8, 12) );j++)
    for (int i=0;i<phonenoList.size();i++){ 
    if (smsService.getServiceStatus()){          
    temp=smsService.sendMessage( phonenoList.get( i ), message);
    if (temp.equals( "SUCCESS") )
    succ++;
    else if (temp.equals( "MODEMERROR") )
    {modemError++;
    i--;
    }
    result+=temp+" ";
    
    System.out.println("..............succeded="+succ+"   failed="+modemError);
    }else{ i--;
    
    System.out.println("servicestatus:false..............succeded="+succ+"   failed="+modemError);
    }
    Thread.currentThread().sleep(delay );
    }
    //result=smsService.sendMessageToGroup( "testing", phoneno, message);
    
    return result;
    }
     */
}
