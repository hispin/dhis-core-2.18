package org.hisp.dhis.mobile.reporting.connection;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.rms.RecordStoreException;

import org.hisp.dhis.mobile.reporting.db.SettingsRecordStore;
import org.hisp.dhis.mobile.reporting.gui.DHISMIDlet;
import org.hisp.dhis.mobile.reporting.model.AbstractModel;
import org.hisp.dhis.mobile.reporting.model.AbstractModelList;
import org.hisp.dhis.mobile.reporting.model.ActivityPlan;
import org.hisp.dhis.mobile.reporting.model.ActivityValue;
import org.hisp.dhis.mobile.reporting.model.DataSet;
import org.hisp.dhis.mobile.reporting.model.DataSetValue;
import org.hisp.dhis.mobile.reporting.model.Program;
import org.hisp.dhis.mobile.reporting.util.AlertUtil;

import com.jcraft.jzlib.ZInputStream;

public class ConnectionManager extends Thread {

	public static final String UPLOAD_DATASET_VALUES = "uploaddatasetvalue";
	public static final String BROWSE_DATASETS = "datasets";
	public static final String DOWNLOAD_DATASET = "dataset";
	public static final String BROWSE_PROGRAMS = "programs";
	public static final String DOWNLOAD_PROGRAM = "program";
	public static final String DOWNLOAD_ACTIVITYPLAN = "activityplan";
	public static final String UPLOAD_ACTIVITY_VALUES = "uploadactivityvalue";
	public static final String AUTHENTICATE = "authenticate";

	Vector abstractModelListVector = new Vector();
	private DataSet dataSet = null;
	private Program program = null;
	private ActivityPlan activityPlan = null;
	private AbstractModelList abstractModelList = new AbstractModelList();

	private String ua;
	private String locale;
	private DHISMIDlet dhisMIDlet;
	private String rootUrl;
	private String userName;
	private String password;
	private String task;
	private int param;
	private DataSetValue dataSetValue;
	private ActivityValue activityValue;

	public ConnectionManager() {
	}

	public ConnectionManager(DHISMIDlet dhisMIDlet, String rootUrl,
			String userName, String password, String locale, String task) {

		this.dhisMIDlet = dhisMIDlet;
		this.rootUrl = rootUrl;
		this.userName = userName;
		this.password = password;
		this.task = task;
		this.locale = locale;

		ua = "Profile/" + System.getProperty("microedition.profiles")
				+ " Configuration/"
				+ System.getProperty("microedition.configuration");
	}

	public ConnectionManager(DHISMIDlet dhisMIDlet, String rootUrl,
			String userName, String password, String locale, String task,
			int param) {

		this.dhisMIDlet = dhisMIDlet;
		this.rootUrl = rootUrl;
		this.userName = userName;
		this.password = password;
		this.task = task;
		this.param = param;
		this.locale = locale;

		ua = "Profile/" + System.getProperty("microedition.profiles")
				+ " Configuration/"
				+ System.getProperty("microedition.configuration");
	}

	public ConnectionManager(DHISMIDlet dhisMIDlet, String rootUrl,
			String userName, String password, String locale, String task,
			DataSetValue dataSetValue) {

		this.dhisMIDlet = dhisMIDlet;
		this.rootUrl = rootUrl;
		this.userName = userName;
		this.password = password;
		this.task = task;
		this.dataSetValue = dataSetValue;
		this.locale = locale;

		ua = "Profile/" + System.getProperty("microedition.profiles")
				+ " Configuration/"
				+ System.getProperty("microedition.configuration");
	}
	
	public ConnectionManager(DHISMIDlet dhisMIDlet, String rootUrl,
			String userName, String password, String locale, String task,
			ActivityValue activityValue) {

		this.dhisMIDlet = dhisMIDlet;
		this.rootUrl = rootUrl;
		this.userName = userName;
		this.password = password;
		this.task = task;
		this.activityValue = activityValue;
		this.locale = locale;

		ua = "Profile/" + System.getProperty("microedition.profiles")
				+ " Configuration/"
				+ System.getProperty("microedition.configuration");
	}


	private void configureConnection(HttpConnection conn) throws IOException {

		conn.setRequestProperty("User-Agent", ua);
		conn.setRequestProperty("Accept-Language", locale);
		conn.setRequestProperty("Content-Type", "application/xml");
		conn.setRequestProperty("Accept", "application/xml");

		// set HTTP basic authentication
		if (userName != null && password != null) {
		    byte[] auth = (userName+":"+password).getBytes();
		    conn.setRequestProperty( "Authorization", "Basic " + Base64.encode( auth, 0, auth.length ));
		}
	}

	public void run() {
		if (task.equals(ConnectionManager.BROWSE_DATASETS)) 
		{			
			downloadAbstractModelList(rootUrl + "mobile-datasets");
			dhisMIDlet.displayDataSetsForDownload(abstractModelListVector);			
		} 
		else if (task.equals(ConnectionManager.DOWNLOAD_DATASET)) 
		{			
			downloadDataSet(rootUrl + "mobile-datasets/" + param);			
			dhisMIDlet.saveDataSet(dataSet);			
		} 
		else if (task.equals((ConnectionManager.UPLOAD_DATASET_VALUES))) 
		{			
			byte[] request_body;
			try {
				
				request_body = dataSetValue.serialize();
				
				String result = upload(rootUrl + "mobile-datasets/values", "application/vnd.org.dhis2.datasetvalue+serialized", request_body);
				dhisMIDlet.getSuccessAlert().setTitle("Upload Status");
				dhisMIDlet.getSuccessAlert().setString(result);
				dhisMIDlet.switchDisplayable(dhisMIDlet.getSuccessAlert(),
				dhisMIDlet.getDataSetDisplayList());
				
			} catch (IOException e) {
				e.printStackTrace();
			}						
		}
		else if (task.equals(ConnectionManager.BROWSE_PROGRAMS)) 
		{
			downloadAbstractModelList(rootUrl + "programs");
			dhisMIDlet.displayProgramsForDownload(abstractModelListVector);
		}
		else if (task.equals(ConnectionManager.DOWNLOAD_PROGRAM)) 
		{			
			downloadProgram(rootUrl + "programs/" + param);			
			dhisMIDlet.saveProgram(program);			
		}		
		else if (task.equals(ConnectionManager.DOWNLOAD_ACTIVITYPLAN)) 
		{			
			downloadActivityPlan(rootUrl + "activityplan/current");			
			dhisMIDlet.saveActivityPlan(activityPlan);			
		}
		else if( task.equals(ConnectionManager.UPLOAD_ACTIVITY_VALUES))
		{
			byte[] request_body;
			try {
				request_body = activityValue.serialize();
				
				String result = upload(rootUrl + "activityplan/values", "application/vnd.org.dhis2.activityvaluelist+serialized", request_body);
				dhisMIDlet.getSuccessAlert().setTitle("Upload Status");
				dhisMIDlet.getSuccessAlert().setString(result);
				dhisMIDlet.switchDisplayable(dhisMIDlet.getSuccessAlert(), dhisMIDlet.getActivityPlanList());
				
			} catch (IOException e) {
				e.printStackTrace();
			}					
		} else if (task.equals(ConnectionManager.AUTHENTICATE)) {
			authenticate(rootUrl + "user");
		}
	}
	
	private void authenticate(String url) {
		HttpConnection hcon = null;

		try {
			int redirectTimes = 0;
			boolean redirect;
			do {
				redirect = false;
				hcon = (HttpConnection) Connector.open(url);
				configureConnection(hcon);

				int status = hcon.getResponseCode();
				switch (status) {
				case HttpConnection.HTTP_OK:
					dhisMIDlet.setLogin(true);
					saveInitSetting();
					dhisMIDlet.switchDisplayable(null, dhisMIDlet.getPinForm());
					break;
                case HttpConnection.HTTP_SEE_OTHER:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
					url = hcon.getHeaderField("location");
					if (hcon != null)
						hcon.close();

					hcon = null;
					redirectTimes++;
					redirect = true;
					break;
				default:
					hcon.close();
					throw new IOException("Response status not OK:" + status);
				}

			} while (redirect == true && redirectTimes < 5);
			if (redirectTimes == 5) {
				throw new IOException("Too much redirects");
			}
		} catch (SecurityException e) {
			dhisMIDlet.switchDisplayable(AlertUtil.getErrorAlert("Error", e.getMessage()), dhisMIDlet.getLoginForm());
			e.printStackTrace();
		} catch (Exception e) {
			dhisMIDlet.switchDisplayable(AlertUtil.getErrorAlert("Error", e.getMessage()), dhisMIDlet.getLoginForm());
			e.printStackTrace();
		} finally {
			try {
				if (hcon != null)
					hcon.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}
	
	private void saveInitSetting() {
		SettingsRecordStore settingsRecord = null;
		try {
			settingsRecord = new SettingsRecordStore(
					SettingsRecordStore.SETTINGS_DB);
			settingsRecord.put("url", dhisMIDlet.getServerUrl().getString());
			settingsRecord.put("username", dhisMIDlet.getUserName().getString());
			settingsRecord.put("password", dhisMIDlet.getPassword().getString());
			settingsRecord.save();
		} catch (RecordStoreException e) {
			e.printStackTrace();
		}
	}

	private void downloadDataSet(String url) {
		HttpConnection hcon = null;
		DataInputStream dis = null;

		try {
			int redirectTimes = 0;
			boolean redirect;
			do {
				redirect = false;				
				hcon = (HttpConnection) Connector.open(url);
				configureConnection(hcon);

				hcon.setRequestProperty("Accept",
						"application/vnd.org.dhis2.dataset+serialized");

				dis = new DataInputStream(hcon.openInputStream());
				dis = getDecompressedStream(dis);

				if (dis != null) {
					dataSet = new DataSet();
					dataSet.deSerialize(dis);
				}

				int status = hcon.getResponseCode();
				switch (status) {
				case HttpConnection.HTTP_OK:
					break;
				case HttpConnection.HTTP_TEMP_REDIRECT:
				case HttpConnection.HTTP_MOVED_TEMP:
				case HttpConnection.HTTP_MOVED_PERM:

					url = hcon.getHeaderField("location");

					if (dis != null)
						dis.close();
					if (hcon != null)
						hcon.close();

					hcon = null;
					redirectTimes++;
					redirect = true;
					break;
				default:
					hcon.close();
					throw new IOException("Response status not OK:" + status);
				}

			} while (redirect == true && redirectTimes < 5);

			if (redirectTimes == 5) {
				throw new IOException("Too much redirects");
			}
		}catch (SecurityException e){	
			//e.printStackTrace();
		}catch (Exception e) {		
			//e.printStackTrace();
		} finally {
			try {
				if (hcon != null)
					hcon.close();
				if (dis != null)
					dis.close();

			} catch (IOException ioe) {
			}
		}		
	}
	
	private void downloadProgram(String url) {
		HttpConnection hcon = null;
		DataInputStream dis = null;

		try {
			int redirectTimes = 0;
			boolean redirect;
			do {
				redirect = false;				
				hcon = (HttpConnection) Connector.open(url);
				configureConnection(hcon);

				hcon.setRequestProperty("Accept",
						"application/vnd.org.dhis2.program+serialized");

				dis = new DataInputStream(hcon.openInputStream());
				dis = getDecompressedStream(dis);

				if (dis != null) {
					program = new Program();
					program.deSerialize(dis);
				}

				int status = hcon.getResponseCode();
				switch (status) {
				case HttpConnection.HTTP_OK:
					break;
				case HttpConnection.HTTP_TEMP_REDIRECT:
				case HttpConnection.HTTP_MOVED_TEMP:
				case HttpConnection.HTTP_MOVED_PERM:

					url = hcon.getHeaderField("location");

					if (dis != null)
						dis.close();
					if (hcon != null)
						hcon.close();

					hcon = null;
					redirectTimes++;
					redirect = true;
					break;
				default:
					hcon.close();
					throw new IOException("Response status not OK:" + status);
				}

			} while (redirect == true && redirectTimes < 5);

			if (redirectTimes == 5) {
				throw new IOException("Too much redirects");
			}
		}catch (SecurityException e){	
			//e.printStackTrace();
		}catch (Exception e) {		
			//e.printStackTrace();
		} finally {
			try {
				if (hcon != null)
					hcon.close();
				if (dis != null)
					dis.close();

			} catch (IOException ioe) {
			}
		}		
	}

	private void downloadActivityPlan(String url) {
		HttpConnection hcon = null;
		DataInputStream dis = null;

		try {
			int redirectTimes = 0;
			boolean redirect;
			do {
				redirect = false;				
				hcon = (HttpConnection) Connector.open(url);
				configureConnection(hcon);

				hcon.setRequestProperty("Accept",
						"application/vnd.org.dhis2.activityplan+serialized");

				dis = new DataInputStream(hcon.openInputStream());
				dis = getDecompressedStream(dis);

				if (dis != null) {
					activityPlan = new ActivityPlan();
					activityPlan.deSerialize(dis);
				}

				int status = hcon.getResponseCode();
				switch (status) {
				case HttpConnection.HTTP_OK:
					break;
				case HttpConnection.HTTP_TEMP_REDIRECT:
				case HttpConnection.HTTP_MOVED_TEMP:
				case HttpConnection.HTTP_MOVED_PERM:

					url = hcon.getHeaderField("location");

					if (dis != null)
						dis.close();
					if (hcon != null)
						hcon.close();

					hcon = null;
					redirectTimes++;
					redirect = true;
					break;
				default:
					hcon.close();
					throw new IOException("Response status not OK:" + status);
				}

			} while (redirect == true && redirectTimes < 5);

			if (redirectTimes == 5) {
				throw new IOException("Too much redirects");
			}
		}catch (SecurityException e){	
			//e.printStackTrace();
		}catch (Exception e) {		
			//e.printStackTrace();
		} finally {
			try {
				if (hcon != null)
					hcon.close();
				if (dis != null)
					dis.close();

			} catch (IOException ioe) {
			}
		}		
	}

	private void downloadAbstractModelList(String url) {
		HttpConnection hcon = null;
		DataInputStream dis = null;

		try {
			int redirectTimes = 0;
			boolean redirect;
			do {
				redirect = false;				
				hcon = (HttpConnection) Connector.open(url);				
				configureConnection(hcon);
				
				hcon.setRequestProperty("Accept",
						"application/vnd.org.dhis2.abstractmodellist+serialized");

				dis = new DataInputStream(hcon.openInputStream());
				dis = getDecompressedStream(dis);

				if (dis != null) {
					abstractModelList.deSerialize(dis);

					if (abstractModelList != null) {
						for (int i = 0; i < abstractModelList
								.getAbstractModels().size(); i++) {
							AbstractModel ds = (AbstractModel) abstractModelList
									.getAbstractModels().elementAt(i);
							abstractModelListVector.addElement(ds);
						}
					}
				}

				int status = hcon.getResponseCode();
				switch (status) {
				case HttpConnection.HTTP_OK:
					break;
				case HttpConnection.HTTP_TEMP_REDIRECT:
				case HttpConnection.HTTP_MOVED_TEMP:
				case HttpConnection.HTTP_MOVED_PERM:

					url = hcon.getHeaderField("location");

					if (dis != null)
						dis.close();
					if (hcon != null)
						hcon.close();

					hcon = null;
					redirectTimes++;
					redirect = true;
					break;
				default:
					hcon.close();
					throw new IOException("Response status not OK:" + status);
				}

			} while (redirect == true && redirectTimes < 5);

			if (redirectTimes == 5) {
				throw new IOException("Too much redirects");
			}
		}catch (SecurityException e){
			//e.printStackTrace();
		}catch (Exception e) {
			//e.printStackTrace();
		} finally {			
			try {				
				if (hcon != null)
				{					
					hcon.close();
				}					
				if (dis != null)
				{					
					dis.close();
				}					
			} catch (IOException ioe) {
			}
		}	
	}	
	
	private String upload(String url, String contentType, byte[] request_body ) {
		HttpConnection hcon = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		StringBuffer responseMessage = new StringBuffer();

		try {
			int redirectTimes = 0;
			boolean redirect;
			do {
				redirect = false;

				hcon = (HttpConnection) Connector.open(url);
				configureConnection(hcon);

				hcon.setRequestProperty("Content-Type",contentType);
				hcon.setRequestMethod(HttpConnection.POST);

				hcon.setRequestProperty("Content-Length", ""+request_body.toString().length());
				dos = hcon.openDataOutputStream();
				
				for (int i = 0; i < request_body.length; i++) {
					dos.writeByte(request_body[i]);
				}
				dos.flush();

				dis = new DataInputStream(hcon.openInputStream());

				int ch;
				while ((ch = dis.read()) != -1) {
					responseMessage.append((char) ch);
				}

				int status = hcon.getResponseCode();

				switch (status) {
				case HttpConnection.HTTP_OK:
					break;
				case HttpConnection.HTTP_TEMP_REDIRECT:
				case HttpConnection.HTTP_MOVED_TEMP:
				case HttpConnection.HTTP_MOVED_PERM:

					url = hcon.getHeaderField("location");

					if (dis != null)
						dis.close();
					if (hcon != null)
						hcon.close();

					hcon = null;
					redirectTimes++;
					redirect = true;
					break;
				default:
					hcon.close();
					throw new IOException("Response status not OK:" + status);
				}

			} while (redirect == true && redirectTimes < 5);

			if (redirectTimes == 5) {
				throw new IOException("Too much redirects");
			}
		}catch (SecurityException e){
			responseMessage.append("FAILURE");
			//e.printStackTrace();
		}catch (Exception e) {
			//e.printStackTrace();
		} finally {
			try {
				if (hcon != null)
					hcon.close();
				if (dis != null)
					dis.close();
			} catch (IOException ioe) {
			}
		}
		
		return responseMessage.toString();		
	}


	/*private String upload(String url) {
		HttpConnection hcon = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		StringBuffer responseMessage = new StringBuffer();

		try {
			int redirectTimes = 0;
			boolean redirect;
			do {
				redirect = false;

				hcon = (HttpConnection) Connector.open(url);
				configureConnection(hcon);

				hcon.setRequestProperty("Content-Type",
						"application/vnd.org.dhis2.datasetvalue+serialized");
				hcon.setRequestMethod(HttpConnection.POST);

				if (dataSetValue != null) {
					hcon.setRequestProperty("Content-Length", ""
							+ dataSetValue.serialize().toString().length());
				}

				if (dataSetValue != null) {
					dos = hcon.openDataOutputStream();
					byte[] request_body = dataSetValue.serialize();

					for (int i = 0; i < request_body.length; i++) {
						dos.writeByte(request_body[i]);
					}
					dos.flush();
				}

				dis = new DataInputStream(hcon.openInputStream());

				int ch;
				while ((ch = dis.read()) != -1) {
					responseMessage.append((char) ch);
				}

				int status = hcon.getResponseCode();

				switch (status) {
				case HttpConnection.HTTP_OK:
					break;
				case HttpConnection.HTTP_TEMP_REDIRECT:
				case HttpConnection.HTTP_MOVED_TEMP:
				case HttpConnection.HTTP_MOVED_PERM:

					url = hcon.getHeaderField("location");

					if (dis != null)
						dis.close();
					if (hcon != null)
						hcon.close();

					hcon = null;
					redirectTimes++;
					redirect = true;
					break;
				default:
					hcon.close();
					throw new IOException("Response status not OK:" + status);
				}

			} while (redirect == true && redirectTimes < 5);

			if (redirectTimes == 5) {
				throw new IOException("Too much redirects");
			}
		}catch (SecurityException e){
			responseMessage.append("FAILURE");
			//e.printStackTrace();
		}catch (Exception e) {
			//e.printStackTrace();
		} finally {
			try {
				if (hcon != null)
					hcon.close();
				if (dis != null)
					dis.close();
			} catch (IOException ioe) {
			}
		}
		
		return responseMessage.toString();		
	}*/

	private DataInputStream getDecompressedStream(DataInputStream dis)
			throws IOException {
		// GZIPInputStream gzip = new GZIPInputStream(dis);
		ZInputStream gzip = new ZInputStream(dis);
		return new DataInputStream(gzip);
	}

}