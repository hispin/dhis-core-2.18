
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
    
    //For finding cumulative values
    Statement st4=null;
    ResultSet rs4=null;
       
    
    //For finding district name and id
    Statement st7=null;
    ResultSet rs7=null;
    
    //For finding state name and id
    Statement st8=null;
    ResultSet rs8=null;
    
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/jh_dhis2";
          

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
  											"TB_DE10","TB_DE11","TB_DE12","TB_DE13","TB_DE14",
  											
											// Medications  - INH-300 mg
											"TB_DE15","TB_DE16","TB_DE17","TB_DE18","TB_DE19",
											
											// Medications  - INH-100 mg
											"TB_DE20","TB_DE21","TB_DE22","TB_DE23","TB_DE24",
											
											// Medications  - Streptomycin 0.75 g
											"TB_DE25","TB_DE26","TB_DE27","TB_DE28","TB_DE29",
											
											// Medications  - Rifampicin 150 mg
											"TB_DE30","TB_DE31","TB_DE32","TB_DE33","TB_DE34",
											
											// Medications  - Pyrazinamide
											"TB_DE35","TB_DE36","TB_DE37","TB_DE38","TB_DE39",
											
											// Medications  - Ethambutol 800 mg	
											"TB_DE40","TB_DE41","TB_DE42","TB_DE43","TB_DE44",
											
											// Staff Position and Training - Medical Officer
											"TB_DE45","TB_DE46","TB_DE47",
											
											// Staff Position and Training - Laboratory Technician
											"TB_DE48","TB_DE49","TB_DE50",
											
											// Staff Position and Training - Pharmacist
											"TB_DE51","TB_DE52","TB_DE53",
											
											// Staff Position and Training - MPH Supervisors
											"TB_DE54","TB_DE55","TB_DE56",
											
											// Staff Position and Training - Multipurpose Health Workers
											"TB_DE57","TB_DE58","TB_DE59",
											
											// Staff Position and Training - TBHV
											"TB_DE60","TB_DE61","TB_DE62",
											
											// Staff Position and Training - STLS
											"TB_DE63","TB_DE64","TB_DE65",
											
											// Referral Activities
											"TB_DE66","TB_DE67",
											
											// Microscopy Activities
											"TB_DE68","TB_DE69","TB_DE70","TB_DE71", 
											
											// Treatment Initiation
											"TB_DE72","TB_DE73","TB_DE74","TB_DE75",
											
											"TB_DE76","TB_DE77",
											
											// Consumables - Sputum Containers
											"TB_DE78","TB_DE79","TB_DE80","TB_DE81","TB_DE82",
											
											// Consumables - Slides
											"TB_DE83","TB_DE84","TB_DE85","TB_DE86","TB_DE87",
											
											// Consumables - Carbon Fuchsin
											"TB_DE88","TB_DE89","TB_DE90","TB_DE91","TB_DE92",											
											
											// Consumables - Methylene Blue
											"TB_DE93","TB_DE94","TB_DE95","TB_DE96","TB_DE97",
											
											// Consumables - Sulphuric Acid
											"TB_DE98","TB_DE99","TB_DE100","TB_DE101","TB_DE102",
											
											// Consumables - Phenol
											"TB_DE103","TB_DE104","TB_DE105","TB_DE106","TB_DE107",
											
											// Consumables - Xylene
											"TB_DE108","TB_DE109","TB_DE110","TB_DE111","TB_DE112",
											
											// Consumables - Immersion Oil
											"TB_DE113","TB_DE114","TB_DE115","TB_DE116","TB_DE117",
											
											// Consumables - Methylated Spirit
											"TB_DE118","TB_DE119","TB_DE120","TB_DE121","TB_DE122",
											
											// Equipment in Place - Monocular Microscopes
											"TB_DE123","TB_DE124","TB_DE125",
											
											// Equipment in Place - Binocular Microscopes
											"TB_DE126","TB_DE127","TB_DE128"
											
											

											
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
 	
  	  String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September",
  									"October", "November", "December" };   	
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
        	st7=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        	st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);	
        	
        	//rs1 = st1.executeQuery("select shortname from organisationunit  where id = "+selectedOrgUnitID);
        	rs1 = st1.executeQuery("select shortname from organisationunit  where organisationunitid = "+selectedOrgUnitID);        
        	if(rs1.next())        {  selectedOrgUnitName = rs1.getString(1);      }
                
//        	rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	   		if(rs2.next()) 	  {  selectedDataPeriodStartDate =  rs2.getDate(1).toString();	} 		  
	   		   			
			selectedDataPeriodStartDate = startingDate;
			

			//rs8=st8.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
			rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");	
			if(rs8.next())  { StateID = rs8.getInt(1);StateName = rs8.getString(2);  } 
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
				//rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[i]+"'");
				rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[i]+"'"); 
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
			
				if(rs7!=null)  rs7.close(); 		if(st7!=null)  st7.close();
				if(rs8!=null)  rs8.close(); 		if(st8!=null)  st8.close();
				
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
       				<font face="Arial" size="2">DISTRICT :&nbsp;&nbsp;<%=selectedOrgUnitName%></font>
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
          <td width="13%" align="center"><FONT face="Arial" size="2"><b>Stock on last day of month<br>(d) = a+b+c</b></font></td>
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
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[12]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[13]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">INH 300 mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[14]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[15]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[16]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[17]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[18]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">INH 100 mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[19]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[20]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[21]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[22]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[23]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Streptomycin 0.75g</font></td>
          <td width="15%"><FONT face="Arial" size="2">Vials</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[24]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[25]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[26]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[27]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[28]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Rifampicin 150 mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Capsules</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[29]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[30]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[31]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[32]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[33]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Pyrazinamide 500mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[34]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[35]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[36]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[37]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[38]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Ethambutol 800mg</font></td>
          <td width="15%"><FONT face="Arial" size="2">Tablets</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[39]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[40]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[41]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[42]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[43]%></font>&nbsp;</td>
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
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[44]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[45]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[46]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">Laboratory Techician</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[47]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[48]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[49]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">Pharmacist</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[50]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[51]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[52]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">MPH Supervisors</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[53]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[54]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[55]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">Multipurpose Health Workers</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[56]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[57]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[58]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">TBHV</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[59]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[60]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[61]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><FONT face="Arial" size="2">STLS*</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[62]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[63]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[64]%></font>&nbsp;</td>
        </tr>
      </table>
      <FONT face="Arial" size="2">*STLS to be reported by medical colleges only</font>
      <br><br>
      <FONT face="Arial" size="2"><b>Referral Activities (To be filled in by all PHIs fom OPD Register)</b></font>
      
	  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">a. Number of new adult outpatient visits</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[65]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">b. Out of (a), number of chest symptomatic patients referred for sputum examination</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[66]%></font>&nbsp;</td>
        </tr>
      </table>
      <br><br><br>
      <FONT face="Arial" size="2"><b>Microscopy Activities (To be filled in by only PHIs which are a DMC from Laboratory Register)</b></font>            
	  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">c. Number of TB suspects whose sputum was examined for diagnosis</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[67]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">d. Out of (c) number of smear positive patients diagnosed</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[68]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">e. Number of TB suspects subjected to repeat sputum examination for diagnosis</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[69]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">f. Out of (e), number of sputum smear positive patients diagnosed</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[70]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">g. Total number of sputum smear positive patients diagnosed (d + f)</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=(monthlyValues[68]+monthlyValues[70])%></font>&nbsp;</td>
        </tr>
      </table>
	  
	  <br>	      
      <FONT face="Arial" size="2"><b>Treatment Initiation (To be filled in by only PHIs which are a DMC from Laboratory Register and Referral for Treatment</b></font>            
	  <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">h. Of the smear-positive patients diagnosed (g), number put on DOTS</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[71]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">i. Of the number of smear-positive patients diagnosed (g), number put on RNTCP Non-DOTS (ND1 and ND2)</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[72]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">j. Of the smear-positive patients diagnosed (g), the number referred for treatment to other TUs within the district</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[73]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">k. Of the smear-positive patients diagnosed (g), the number referred for treatment outside the district</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[74]%></font>&nbsp;</td>
        </tr>
      </table>
      <br>
      <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
        <tr>
          <td width="80%"><FONT face="Arial" size="2">Number of smear negative patients residing within the district and put on treatment other than DOTS</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[75]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="80%"><FONT face="Arial" size="2">Number of extrapulmonary patients residing within the district and put on treatment other than DOTS</font></td>
          <td width="20%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[76]%></font>&nbsp;</td>
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
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[77]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[78]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[79]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[80]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[81]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Slides</font></td>
          <td width="15%"><FONT face="Arial" size="2">Nos.</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[82]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[83]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[84]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[85]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[86]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Carbon Fuchsin</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[87]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[88]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[89]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[90]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[91]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Methylene Blue</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[92]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[93]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[94]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[95]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[96]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Sulphuric Acid</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[97]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[98]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[99]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[100]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[101]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Phenol</font></td>
          <td width="15%"><FONT face="Arial" size="2">Grams</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[102]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[103]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[104]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[105]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[106]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Xylene</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[107]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[108]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[109]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[110]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[111]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Immersion Oil</font></td>
          <td width="15%"><FONT face="Arial" size="2">ml</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[112]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[113]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[114]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[115]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[116]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="20%"><FONT face="Arial" size="2">Methylated Spirit</font></td>
          <td width="15%"><FONT face="Arial" size="2">Litres</font></td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[117]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[118]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[119]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[120]%></font>&nbsp;</td>
          <td width="13%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[121]%></font>&nbsp;</td>
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
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[122]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[123]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[124]%></font>&nbsp;</td>
        </tr>
        <tr>
          <td width="25%"><font face="Arial" size="2">Binocular microscopes</font></td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[125]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[126]%></font>&nbsp;</td>
          <td width="25%" align="center"><FONT face="Arial" size="2"><%=monthlyValues[127]%></font>&nbsp;</td>
        </tr>
	  </table>
	  <FONT face="Arial" size="2">*PHIs that are not a DMC, but have been supplied with sputum ontainers, should complete this row.
	  <br><br><br><b>Name of officer reporting (in Capital Letters):
	  <br><br>Signature:
	  <br><br>Date:
	  </b></font>
	</body>
</html>