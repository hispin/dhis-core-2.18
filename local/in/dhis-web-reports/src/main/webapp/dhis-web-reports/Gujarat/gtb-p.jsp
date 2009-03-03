
<%@ page import="java.sql.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%@ page session="true"%>

<%
    Connection con=null;
            
    // For finding organisationunit name of selected Orgunit based on Orgunit id
    Statement st1=null;
    ResultSet rs1=null;
      
    // For finding start date of selected period based on period id
    Statement st2=null;
    ResultSet rs2=null;
    
    //For finding monthly values
    Statement st3=null;
    ResultSet rs3=null;
    
    //For finding taluk id
    Statement st4=null;
    ResultSet rs4=null;
       
    
    //For finding district name and id
    Statement st5=null;
    ResultSet rs5=null;
    
    //For finding state name and id
    Statement st6=null;
    ResultSet rs6=null;
    
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
          

	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	
//	String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	int selectedDataPeriodID = 	Integer.parseInt( selectedPeriodId );

  	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = "";
  	  	  	
  	String monthlyDataElements[] = {
  											// Medications  - Category- 1
  											"TB_DE1","TB_DE2","TB_DE3",

  											// Medications  - Category- 2
  											"TB_DE4","TB_DE5","TB_DE6",

											// Medications  - Category- 3  											
  											"TB_DE7","TB_DE8","TB_DE9",
  											
  											// Medications  - Pouches of Blister Strips
  											"TB_DE10","TB_DE11","TB_DE12",
  											
											// Medications  - INH-300 mg
											"TB_DE13","TB_DE14","TB_DE15",
											
											// Medications  - INH-100 mg
											"TB_DE16","TB_DE17","TB_DE18",
											
											// Medications  - Streptomycin 0.75 g
											"TB_DE19","TB_DE20","TB_DE21",
											
											// Medications  - Rifampicin 150 mg
											"TB_DE22","TB_DE23","TB_DE24",
											
											// Medications  - Pyrazinamide
											"TB_DE25","TB_DE26","TB_DE27",
											
											// Medications  - Ethambutol 800 mg	
											"TB_DE28","TB_DE29","TB_DE30",
											
											// Staff Position and Training - Medical Officer
											"TB_DE31","TB_DE32","TB_DE33",
											
											// Staff Position and Training - Laboratory Technician
											"TB_DE34","TB_DE35","TB_DE36",
											
											// Staff Position and Training - Pharmacist
											"TB_DE37","TB_DE38","TB_DE39",
											
											// Staff Position and Training - MPH Supervisors
											"TB_DE40","TB_DE41","TB_DE42",
											
											// Staff Position and Training - Multipurpose Health Workers
											"TB_DE43","TB_DE44","TB_DE45",
											
											// Staff Position and Training - TBHV
											"TB_DE46","TB_DE47","TB_DE48",
											
											// Staff Position and Training - STLS
											"TB_DE49","TB_DE50","TB_DE51",
											
											// Referral Activities
											"TB_DE52","TB_DE53",
											
											// Microscopy Activities
											"TB_DE54","TB_DE55","TB_DE56","TB_DE57", 
											
											// Treatment Initiation
											"TB_DE58","TB_DE59","TB_DE60","TB_DE61",
											
											"TB_DE62","TB_DE63",
											
											// Consumables - Sputum Containers
											"TB_DE64","TB_DE65","TB_DE66",
											
											// Consumables - Slides
											"TB_DE67","TB_DE68","TB_DE69",
											
											// Consumables - Carbon Fuchsin
											"TB_DE70","TB_DE71","TB_DE72",
											
											// Consumables - Methylene Blue
											"TB_DE73","TB_DE74","TB_DE75",
											
											// Consumables - Sulphuric Acid
											"TB_DE76","TB_DE77","TB_DE78",
											
											// Consumables - Phenol
											"TB_DE79","TB_DE80","TB_DE81",
											
											// Consumables - Xylene
											"TB_DE82","TB_DE83","TB_DE84",
											
											// Consumables - Immersion Oil
											"TB_DE85","TB_DE86","TB_DE87",
											
											// Consumables - Methylated Spirit
											"TB_DE88","TB_DE89","TB_DE90",
											
											// Equipment in Place - Monocular Microscopes
											"TB_DE91","TB_DE92","TB_DE93",
											
											// Equipment in Place - Binocular Microscopes
											"TB_DE94","TB_DE95","TB_DE96"																						
											
										};
					
 	  int monthlyValues[] = new int[monthlyDataElements.length+5]; 		
 	 
 	  
 	  int CHCID=0;
 	  int TalukID=0;
 	  int DistrictID=0;
 	  int StateID=0;
 	  
 	  String CHCName="";
 	  String TalukName = "";
 	  String  DistrictName = "";
 	  String StateName = "";

  	  int j=1;  	 
  	  int i=1;  	 
  	  int k=0;  	 
  	  int endcount = 0;
  	  int p = 0;  	 
  	  int q = 0;
 	
  	  String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };   	
%>

<%

     try
      {
        	Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        	con = DriverManager.getConnection (urlForConnection, userName, password);
        
        	st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);        	
        	st5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);	

        	
        	//rs1 = st1.executeQuery("select shortname from organisationunit  where id = "+selectedOrgUnitID);
        	rs1 = st1.executeQuery("select shortname from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        	if(rs1.next())        {  selectedOrgUnitName = rs1.getString(1);      }
                
//        	rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   		if(rs2.next()) 	  {  selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 		  

			selectedDataPeriodStartDate = startingDate;


			//rs4=st4.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
			rs4=st4.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs4.next())  { TalukID = rs4.getInt(1);  } 
			else  {  TalukID = 0;   } 
	   		   			
			//rs5=st5.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+TalukID+")");
			rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+TalukID+")");	
			if(rs5.next())  { DistrictID = rs5.getInt(1); DistrictName = rs5.getString(2);  } 
			else  {  DistrictID = 0; DistrictName = "";  } 

			//rs6=st6.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+DistrictID+")");
			rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+DistrictID+")");	
			if(rs6.next())  { StateID = rs6.getInt(1); StateName = rs6.getString(2);  } 
			else  {  StateID = 0; StateName = "";  } 

      } //try block end
      catch(Exception e)  { out.println(e.getMessage());  }
      
     String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");
     int lastYear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
     int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
     int tempForYear = 0;
     if(tempForMonth1 < 4){   tempForYear = lastYear;  }
     else  {   tempForYear = lastYear + 1;   	}
     String curYearStart=tempForYear+"-04-01";
      	 	
    try
     {     
     	int count = monthlyDataElements.length;
     	for(i=0;i<count;i++)	  
			{	  
				//rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
				rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");	
        		if(rs3.next()) 		{  monthlyValues[i] =  rs3.getInt(1);	}  		  							
	  		}
  	} // try block end
    catch(Exception e)  { out.println(e.getMessage());  }
    finally
       {
			try
			{
  				if(rs1!=null)  rs1.close(); 		if(st1!=null)  st1.close();
  				if(rs2!=null)  rs2.close(); 		if(st2!=null)  st2.close();  				
  				if(rs3!=null)  rs3.close(); 		if(st3!=null)  st3.close();				
				if(rs4!=null)  rs4.close(); 		if(st4!=null)  st4.close();			
				if(rs5!=null)  rs5.close(); 		if(st5!=null)  st5.close();
				if(rs6!=null)  rs6.close(); 		if(st6!=null)  st6.close();
				
				if(con!=null)  con.close(); 
			}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end	
       
  %>


<html>
	<head>
		<title>REVISED NATIONAL TUBERCULOSIS CONTROL PROGRAMME</title>		
	</head>
	<body >
	  <center> 
	    <FONT face="Arial" size="3"><b><u>REVISED NATIONAL TUBERCULOSIS CONTROL PROGRAMME</u>
	    	<br>Monthly Report on Logistics and Microscopy
	    	<br><br>Peripheral Health Institution Level</b></font>
	 </center>   
	    <br><FONT face="Arial" size="2"><i>Note: All PHCs/CHCs/Referral Hospitals Major Hospitals/Specially Clinics/TB Hospitals/Medical Colleges to submit their monthly reports in this format.</i>
	    <br><br>Name of Peripheral Health Institution :&nbsp;&nbsp;&nbsp;<%=selectedOrgUnitName%>
	    
	    <table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="left" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="50%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2">TU :&nbsp;&nbsp;</font>
    				</td>
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2">DISTRICT :&nbsp;&nbsp;<%=DistrictName%></font>
    				</td>   
  			</tr>
 			<tr>
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">Month : &nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%></font>
    				</td>
    				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				<font face="Arial" size="2">Year : &nbsp;&nbsp;<%=partsOfDataPeriodStartDate[0]%></font>
    				</td>
  		</tr>  		  		
  		</table>  
	    
		<br><br><br><br><br>
		<b>Medications</b></font><table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="20%" align="center"><FONT face="Arial" size="2"><b>Item</b></font></td>
          <td width="15%" align="center"><FONT face="Arial" size="2"><b>Unit of Measurement</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock on first day of month<br>(a)</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock received during month<br>(b)</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Patients initiated on treatment<br>(c)</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock on last day of month<br>(d) = a+b-c</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Quantity Requested<p>(e) = (cx2)-d</b></font></td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Category I</font></td>
          <td width="15%"><FONT face="Arial" size="2">Boxes</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[0]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[1]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[2]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[0]+monthlyValues[1]-monthlyValues[2])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[2]*2)-(monthlyValues[0]+monthlyValues[1]-monthlyValues[2]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Category II</font></td>
          <td width="15%"><FONT face="Arial" size="2">Boxes</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[3]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[4]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[5]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[3]+monthlyValues[4]-monthlyValues[5])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[5]*2)-(monthlyValues[3]+monthlyValues[4]-monthlyValues[5]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Category III</font></td>
          <td width="15%"><FONT face="Arial" size="2">Boxes</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[6]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[7]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[8]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[6]+monthlyValues[7]-monthlyValues[8])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[8]*2)-(monthlyValues[6]+monthlyValues[7]-monthlyValues[8]))%></font>&nbsp;</td>
        </tr>
      </table>
      
      <br>
      <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="20%" align="center"><FONT face="Arial" size="2"><b>Item</b></font></td>
          <td width="15%" align="center"><FONT face="Arial" size="2"><b>Unit of Measurement</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock on first day of month</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock received during month</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Consumption during month</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock on last day of month</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Quantity Requested</b></font></td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Pouches of blister strips for prolongation of intensive phase</font></td>
          <td width="15%"><FONT face="Arial" size="2">Pouches each with 12 blister strips</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[9]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[10]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[11]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[9]+monthlyValues[10]-monthlyValues[11])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[11]*2)-(monthlyValues[9]+monthlyValues[10]-monthlyValues[11]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">INH 300 mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[12]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[13]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[14]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[12]+monthlyValues[13]-monthlyValues[14])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[14]*2)-(monthlyValues[12]+monthlyValues[13]-monthlyValues[14]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">INH 100 mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[15]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[16]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[17]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[15]+monthlyValues[16]-monthlyValues[17])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[17]*2)-(monthlyValues[15]+monthlyValues[16]-monthlyValues[17]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Streptomycin 0.75g</font></td>
          <td width="15%"><FONT face="Arial" size="2">Vials</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[18]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[19]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[20]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[18]+monthlyValues[19]-monthlyValues[20])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[20]*2)-(monthlyValues[18]+monthlyValues[19]-monthlyValues[20]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Rifampicin 150 mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Capsules</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[21]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[22]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[23]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[21]+monthlyValues[22]-monthlyValues[23])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[23]*2)-(monthlyValues[21]+monthlyValues[22]-monthlyValues[23]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Pyrazinamide 500mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[24]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[25]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[26]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[24]+monthlyValues[25]-monthlyValues[26])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[26]*2)-(monthlyValues[24]+monthlyValues[25]-monthlyValues[26]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Ethambutol 800mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[27]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[28]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[29]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[27]+monthlyValues[28]-monthlyValues[29])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[29]*2)-(monthlyValues[27]+monthlyValues[28]-monthlyValues[29]))%></font>&nbsp;</td>
        </tr>
      </table>
      
      <br>
      <font face="Arial" size="2"><b>Staff Position and Training</b></font>
      <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="25%" align="center"><FONT face="Arial" size="2"><b>Category of staff</b></font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><b>Sanctioned</b></font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><b>In Place</b></font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><b>Trained in RNTCP</b></font></td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">Medical Officer</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[30]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[31]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[32]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">Laboratory Techician</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[33]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[34]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[35]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">Pharmacist</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[36]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[37]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[38]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">MPH Supervisors</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[39]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[40]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[41]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">Multipurpose Health Workers</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[42]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[43]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[44]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">TBHV</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[45]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[46]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[47]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">STLS*</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[48]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[49]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[50]%></font>&nbsp;</td>
        </tr>
      </table>
      <FONT face="Arial" size="2">*STLS to be reported by medical colleges only</font>
      <br><br>
      <FONT face="Arial" size="2"><b>Referral Activities (To be filled in by all PHIs fom OPD Register)</b></font>
      
	  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">a. Number of new adult outpatient visits</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[51]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">b. Out of (a), number of chest symptomatic patients referred for sputum examination</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[52]%></font>&nbsp;</td>
        </tr>
      </table>
      <br><br><br>
      <FONT face="Arial" size="2"><b>Microscopy Activities (To be filled in by only PHIs which are a DMC from Laboratory Register)</b></font>            
	  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">c. Number of TB suspects whose sputum was examined for diagnosis</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[53]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">d. Out of (c) number of smear positive patients diagnosed</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[54]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">e. Number of TB suspects subjected to repeat sputum examination for diagnosis</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[55]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">f. Out of (e), number of sputum smear positive patients diagnosed</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[56]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">g. Total number of sputum smear positive patients diagnosed (d + f)</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[54]+monthlyValues[56])%></font>&nbsp;</td>
        </tr>
      </table>
	  
	  <br>	      
      <FONT face="Arial" size="2"><b>Treatment Initiation (To be filled in by only PHIs which are a DMC from Laboratory Register and Referral for Treatment</b></font>            
	  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">h. Of the smear-positive patients diagnosed (g), number put on DOTS</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[57]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">i. Of the number of smear-positive patients diagnosed (g), number put on RNTCP Non-DOTS (ND1 and ND2)</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[58]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">j. Of the smear-positive patients diagnosed (g), the number referred for treatment to other TUs within the district</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[59]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">k. Of the smear-positive patients diagnosed (g), the number referred for treatment outside the district</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[60]%></font>&nbsp;</td>
        </tr>
      </table>
      <br>
      <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">Number of smear negative patients residing within the district and put on treatment other than DOTS</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[61]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">Number of extrapulmonary patients residing within the district and put on treatment other than DOTS</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[62]%></font>&nbsp;</td>
        </tr>
	  </table>
	  <br>
      <FONT face="Arial" size="2"><b>Consumables (To be filled in by only PHIs which are a DMC)</b></font>
      <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="20%" align="center"><FONT face="Arial" size="2"><b>Item</b></font></td>
          <td width="15%" align="center"><FONT face="Arial" size="2"><b>Unit of Measurement</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock on first day of Month</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock received during Month</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Consumption during Month*</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock on last day of Month</b></font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Quantity Requested</b></font></td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Sputum containers</font></td>
          <td width="15%"><FONT face="Arial" size="2">Nos.</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[63]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[64]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[65]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[63]+monthlyValues[64]-monthlyValues[65])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[65]*2)-(monthlyValues[63]+monthlyValues[64]-monthlyValues[65]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Slides</font></td>
          <td width="15%"><FONT face="Arial" size="2">Nos.</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[66]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[67]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[68]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[66]+monthlyValues[67]-monthlyValues[68])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[68]*2)-(monthlyValues[66]+monthlyValues[67]-monthlyValues[68]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Carbon Fuchsin</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[69]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[70]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[71]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[69]+monthlyValues[70]+monthlyValues[71])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[71]*2)-(monthlyValues[69]+monthlyValues[70]-monthlyValues[71]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Methylene Blue</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[72]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[73]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[74]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[72]+monthlyValues[73]+monthlyValues[74])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[74]*2)-(monthlyValues[72]+monthlyValues[73]-monthlyValues[74]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Sulphuric Acid</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[75]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[76]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[77]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[75]+monthlyValues[76]+monthlyValues[77])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[77]*2)-(monthlyValues[75]+monthlyValues[76]-monthlyValues[77]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Phenol</font></td>
          <td width="15%"><FONT face="Arial" size="2">Grams</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[78]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[79]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[80]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[78]+monthlyValues[79]+monthlyValues[80])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[80]*2)-(monthlyValues[78]+monthlyValues[79]-monthlyValues[80]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Xylene</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[81]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[82]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[83]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[81]+monthlyValues[82]+monthlyValues[83])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[83]*2)-(monthlyValues[81]+monthlyValues[82]-monthlyValues[83]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Immersion Oil</font></td>
          <td width="15%"><FONT face="Arial" size="2">ml</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[84]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[85]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[86]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[84]+monthlyValues[85]+monthlyValues[86])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[86]*2)-(monthlyValues[84]+monthlyValues[85]-monthlyValues[86]))%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Methylated Spirit</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[87]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[88]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[89]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[87]+monthlyValues[88]+monthlyValues[89])%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=((monthlyValues[89]*2)-(monthlyValues[87]+monthlyValues[88]-monthlyValues[89]))%></font>&nbsp;</td>
        </tr>
	  </table>
	  <br>
	  <FONT face="Arial" size="2"><b>Equipment in place</b></font>
	  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="25%" align="center"><b><font face="Arial" size="2">Item</font></b></td>
          <td width="25%" align="center"><b><font face="Arial" size="2">Number in place</font></b></td>
          <td width="25%" align="center"><b><font face="Arial" size="2">In working condition</font></b></td>
          <td width="25%" align="center"><b><font face="Arial" size="2">Not in working condition</font></b></td>
        </tr>
        <tr>
          <td width="25%"><font face="Arial" size="2">Monocular microscopes</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[90]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[91]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[92]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><font face="Arial" size="2">Binocular microscopes</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[93]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[94]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[95]%></font>&nbsp;</td>
        </tr>
	  </table>
	  <FONT face="Arial" size="2">*PHIs that are not a DMC, but have been supplied with sputum ontainers, should complete this row.
	  <br><br><br><b>Name of officer reporting (in Capital Letters):
	  <br><br>Signature:
	  <br><br>Date:
	  </b></font>
	</body>
</html>