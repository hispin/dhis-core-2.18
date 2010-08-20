package org.hisp.dhis.cbhis.connection;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.microedition.io.*;
import org.hisp.dhis.cbhis.gui.CBHISMIDlet;
import org.hisp.dhis.cbhis.model.AbstractModel;
import org.hisp.dhis.cbhis.model.Activity;
import org.hisp.dhis.cbhis.model.Beneficiary;
import org.hisp.dhis.cbhis.model.DataElement;
import org.hisp.dhis.cbhis.model.OrgUnit;
import org.hisp.dhis.cbhis.model.ProgramStageForm;
import org.hisp.dhis.cbhis.model.Task;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.sun.midp.io.Base64;

public class DownloadManager extends Thread {

    public static final String DOWNLOAD_FORMS = "forms";
    public static final String DOWNLOAD_FORM = "form";
    public static final String DOWNLOAD_ACTIVITYPLAN = "activityPlan";
    public static final String DOWNLOAD_ORGUNIT = "orgUnits";
    public static final String FORMS_TAG = "iProgramStages";
    public static final String FORM_TAG = "form";
    public static final String DATAELEMENTS_TAG = "des";
    public static final String ORGUNIT_TAG = "orgUnits";
    public static final String ACTIVITYPLAN_TAG = "activityPlan";
    
    
    

    Vector programStagesVector = new Vector();
    Vector programStageDataElementsVctr = new Vector();
    Vector orgunitVector = new Vector();
    Vector activitiesVector = new Vector();
    private ProgramStageForm form;

    private String ua;
    private CBHISMIDlet cbhisMIDlet;
    private String rootUrl;
    private String userName;
    private String password;
    private String task;
    private int param;
    
  
    public DownloadManager() {}

    public DownloadManager(CBHISMIDlet cbhisMIDlet, String rootUrl, String userName, String password, String task) {

        this.cbhisMIDlet = cbhisMIDlet;
        this.rootUrl = rootUrl;
        this.userName = userName;
	this.password = password;
        this.task = task;

	ua = "Profile/" + System.getProperty("microedition.profiles")
			+ " Configuration/"
			+ System.getProperty("microedition.configuration");
    }
    
    public DownloadManager(CBHISMIDlet cbhisMIDlet, String rootUrl, String userName, String password, String task, int param) {

        this.cbhisMIDlet = cbhisMIDlet;
        this.rootUrl = rootUrl;
        this.userName = userName;
	this.password = password;
        this.task = task;
        this.param = param;

	ua = "Profile/" + System.getProperty("microedition.profiles")
			+ " Configuration/"
			+ System.getProperty("microedition.configuration");
    }

    public void run()
    {       
        if( task.equals( DOWNLOAD_FORMS)){
            download( rootUrl + "cbhis-webservice/forms",  FORMS_TAG );
            cbhisMIDlet.displayFormsForDownload(programStagesVector);
        }
        else if( task.equals( DOWNLOAD_FORM)){
            download( rootUrl + "cbhis-webservice/forms/"+param,  FORM_TAG );
            cbhisMIDlet.saveForm(form);
            cbhisMIDlet.renderForm(form, cbhisMIDlet.getDataEntryForm());
        }
        else if( task.equals( DOWNLOAD_ORGUNIT)){
        	//download OrgUnits and save to RMS then download Activities
            download( rootUrl + "api/cbhis/v0.1/",  ORGUNIT_TAG );
            cbhisMIDlet.saveOrgUnits(orgunitVector);
            cbhisMIDlet.displayOrgUnitToDownloadActivities();
        }else if(task.equals(DOWNLOAD_ACTIVITYPLAN)){
        	download( rootUrl,  ACTIVITYPLAN_TAG );
        	cbhisMIDlet.saveActivities(activitiesVector);
        	cbhisMIDlet.displayCurActivities();
        }
    }

    private void download(String url, String xmlTag)
    {       
        HttpConnection hcon = null;    	
        InputStream inStream = null;   	
    	
        try {
            int redirectTimes = 0;
            boolean redirect;
            do {
                redirect = false;

                hcon = (HttpConnection) Connector.open(url);
                
                if(xmlTag.equals(ACTIVITYPLAN_TAG)||xmlTag.equals(ORGUNIT_TAG)){
                	//dhis-web-cbhis-api return application/xml
                	hcon.setRequestProperty("Accept", "application/xml");
                	configureConnection(hcon);	
                }else{
                	//dhis-web-cbhis-webservice return text/xml
                	hcon.setRequestProperty("Accept", "text/xml");
                	configureConnection(hcon);
                }
                
                inStream = hcon.openInputStream();
                System.out.println("Server Response Code:"+hcon.getResponseCode());
                readXMLData(inStream, xmlTag);
                		
                int status = hcon.getResponseCode();
				switch (status) {
		                    case HttpConnection.HTTP_OK: // Success!
					break;
		                    case HttpConnection.HTTP_TEMP_REDIRECT:
		                    case HttpConnection.HTTP_MOVED_TEMP:
		                    case HttpConnection.HTTP_MOVED_PERM:
		                        // Redirect: get the new location
					url = hcon.getHeaderField("location");
					
		                        if (inStream != null) inStream.close();                        
		                        if (hcon != null) hcon.close();
		
		                        hcon = null;
					redirectTimes++;
					redirect = true;
		                        break;
		                    default:
					// Error: throw exception
					hcon.close();
					throw new IOException("Response status not OK:" + status);
		}

						// max 5 redirects
				            } while (redirect == true && redirectTimes < 5);
				
				            if (redirectTimes == 5) {
				                throw new IOException("Too much redirects");
				            }
				        } catch (Exception e) {            
					} finally {
				            try {
						if (hcon != null)
				                    hcon.close();		
				                if (inStream != null)
				                    inStream.close();
				            } catch (IOException ioe) {
				            }
	}
    }   
    
    private void configureConnection(HttpConnection conn) throws IOException {

        conn.setRequestProperty("User-Agent", ua);
	String locale = System.getProperty("microedition.locale");
	if (locale == null) { 
            locale = "en-US";
        }	
	conn.setRequestMethod( HttpConnection.GET );	
	conn.setRequestProperty("Accept-Language", locale);	
	conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );

        // set HTTP basic authentification
	if (userName != null && password != null) {
			
		//conn.setRequestProperty("Authorization", "Basic " + BasicAuth.encode(userName, password));		
		byte[] arrayBytes = (userName + ":" + password).getBytes();
		conn.setRequestProperty( "Authorization", "Basic " + Base64.encode( arrayBytes, 0, arrayBytes.length ));
	}
	
    }

    private void readXMLData(InputStream inStream, String tag)
        throws IOException, XmlPullParserException {
    	        
        KXmlParser parser = new KXmlParser();
        parser.setInput( new InputStreamReader( inStream) );

        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, tag);

        if( tag.equals(FORMS_TAG) )
        {
            while (parser.nextTag () != XmlPullParser.END_TAG)
                parseForms( parser );
        }
        if( tag.equals(FORM_TAG) )
        {
            form = new ProgramStageForm();
            //picking ID
            parser.nextTag();
            form.setId( Integer.parseInt(parser.nextText()) );
            
            //picking name
            parser.nextTag();
            form.setName( parser.nextText() );

            //picking dataElements
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, DATAELEMENTS_TAG);
            while (parser.nextTag () != XmlPullParser.END_TAG)
                parseDataElements( parser );

            form.setDataElements(programStageDataElementsVctr);
        }
        if( tag.equals(ORGUNIT_TAG) )
        {
            while (parser.nextTag () != XmlPullParser.END_TAG){
            	parseOrgUnit( parser );
            }
                
        }
        if( tag.equals(ACTIVITYPLAN_TAG) )
        {
            while (parser.nextTag () != XmlPullParser.END_TAG){
            	try{
            		parseActivityPlan( parser );	
            	}catch(Exception e){
            		System.out.println("Parse Failed");
            	}
            	
            }
                
        }
    }

	private void parseForms(KXmlParser parser)
        throws IOException, XmlPullParserException {

        AbstractModel programStage = new AbstractModel();

        parser.require(XmlPullParser.START_TAG, null, "iProgramStage");
        
        while (parser.nextTag () != XmlPullParser.END_TAG)
        {
            parser.require(XmlPullParser.START_TAG, null, null);
            String name = parser.getName();
            String text = parser.nextText();

            if( name.equals("id") )
            {
                programStage.setId(Integer.valueOf(text).intValue());
            }
            else if( name.equals("name"))
            {
                programStage.setName(text);
            }
        }

        programStagesVector.addElement(programStage);
        parser.require(XmlPullParser.END_TAG, null, "iProgramStage");        
    }

    private void parseDataElements(KXmlParser parser)
        throws IOException, XmlPullParserException {       

        DataElement de = new DataElement();

        parser.require(XmlPullParser.START_TAG, null, "de");

        while (parser.nextTag () != XmlPullParser.END_TAG)
        {
            parser.require(XmlPullParser.START_TAG, null, null);
            String name = parser.getName();
            String text = parser.nextText();

            if( name.equals("deId") )
            {
                de.setId(Integer.valueOf(text).intValue());
            }
            else if( name.equals("deName"))
            {
                de.setName(text);
            }
            else if( name.equals("deType"))
            {
                if(text.equals("int"))
                    de.setType(DataElement.TYPE_INT);
                else if( text.equals("date"))
                    de.setType(DataElement.TYPE_DATE);
                else if( text.equals("bool"))
                    de.setType(DataElement.TYPE_BOOL);
                else
                    de.setType(DataElement.TYPE_STRING);
            }
        }

        programStageDataElementsVctr.addElement(de);
        parser.require(XmlPullParser.END_TAG, null, "de");
    }
    
    private void parseOrgUnit(KXmlParser parser) 
    	throws IOException, XmlPullParserException {
    	
    	OrgUnit orgUnit = new OrgUnit();
    	
    	parser.require(XmlPullParser.START_TAG, null, "orgUnit");
    	//picking Name and ID attribute
    	String name = parser.getAttributeValue(0);
        String id = parser.getAttributeValue(1);            
        orgUnit.setName(name);
        orgUnit.setId(Integer.parseInt(id));
        //picking URLs
        		parser.nextTag();
    			if( parser.getName().equals("allProgramForms") )
	            {
	            	orgUnit.setProgramFormsLink(parser.getAttributeValue(0));
	            }
    			parser.nextTag ();
    			parser.nextTag ();
	            if( parser.getName().equals("currentActivities"))
	            {
	            	orgUnit.setActivitiesLink(parser.getAttributeValue(0));
	            }
    			orgunitVector.addElement(orgUnit);
    			parser.nextTag();
	}
    
    private void parseActivityPlan(KXmlParser parser)
        throws IOException, XmlPullParserException {
        
    	Activity activity = new Activity();
    	Task task = new Task();
    	Beneficiary beneficiary = new Beneficiary();
    	parser.require(XmlPullParser.START_TAG, null, "activity");
    	while(parser.nextTag() != XmlPullParser.END_TAG){
    		if(parser.getName().equals("beneficiary")){
	    		
    			beneficiary.setMiddleName(parser.getAttributeValue(0));
	    		beneficiary.setLastName(parser.getAttributeValue(1));	
	    		beneficiary.setId(Integer.parseInt(parser.getAttributeValue(2)));	
	    		beneficiary.setFirstName(parser.getAttributeValue(3));
	    		activity.setBeneficiary(beneficiary);
	    		parser.nextTag();
	    		
    		}else if(parser.getName().equals("dueDate")){
    			String dateStr = parser.nextText();
    			activity.setDueDate(dateStr);
    			
    		}else if(parser.getName().equals("task")){
    			
    			task.setProgStageName(parser.getAttributeValue(0));
    			task.setProgStageId(Integer.parseInt(parser.getAttributeValue(1)));
    			task.setProgStageInstId(Integer.parseInt(parser.getAttributeValue(2)));
    			task.setComplete(parser.getAttributeValue(3).equals("true")?true:false);
    			activity.setTask(task);
    			parser.nextTag();
    			
    		}    		
    	}
    	activitiesVector.addElement(activity);
    }
    
    //Parse from String get error
	private Date getDateFromString(String strDate) {
		Calendar cal = Calendar.getInstance();
		int day = Integer.parseInt(strDate.substring(8, 9));
		int month = Integer.parseInt(strDate.substring(5, 6));
		int year = Integer.parseInt(strDate.substring(0, 3));
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();		
	}
    
    
}
