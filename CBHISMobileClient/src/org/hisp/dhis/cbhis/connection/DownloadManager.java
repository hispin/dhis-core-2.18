package org.hisp.dhis.cbhis.connection;

import java.io.*;
import java.util.Vector;
import javax.microedition.io.*;
import org.hisp.dhis.cbhis.gui.CBHISMIDlet;
import org.hisp.dhis.cbhis.model.AbstractModel;
import org.hisp.dhis.cbhis.model.DataElement;
import org.hisp.dhis.cbhis.model.ProgramStageForm;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DownloadManager extends Thread {

    public static final String DOWNLOAD_FORMS = "forms";
    public static final String DOWNLOAD_FORM = "form";
    public static final String DOWNLOAD_ACTIVITYPLAN = "activityplan";
    public static final String FORMS_TAG = "iProgramStages";
    public static final String FORM_TAG = "form";
    public static final String DATAELEMENTS_TAG = "des";
    public static final String ACTIVITYPLAN_TAG = "activityplan";

    Vector programStagesVector = new Vector();
    Vector programStageDataElementsVctr = new Vector();
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
        else if( task.equals( DOWNLOAD_ACTIVITYPLAN)){
            download( rootUrl + "cbhis-webservice/forms",  DOWNLOAD_ACTIVITYPLAN );
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
                configureConnection(hcon);                
                
                inStream = hcon.openInputStream();

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
	conn.setRequestProperty("Accept-Language", locale);
	conn.setRequestProperty("Content-Type", "text/xml");        
	conn.setRequestProperty("Accept", "text/xml");

        // set HTTP basic authentification
	if (userName != null && password != null) {
		conn.setRequestProperty("Authorization", "Basic " + BasicAuth.encode(userName, password));
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
        if( tag.equals(ACTIVITYPLAN_TAG) )
        {
            while (parser.nextTag () != XmlPullParser.END_TAG)
                parseActivityPlan( parser );
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

    private void parseActivityPlan(KXmlParser parser)
        throws IOException, XmlPullParserException {
        
    }
}