
<%@ page import="java.sql.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%
      Connection con=null;
      
      // for Performance in the Reporting Month
      Statement st=null;
      ResultSet rs=null;
      
      // for selected OrgUnit Name and Population
      Statement st1=null;
      ResultSet rs1=null;
      
      // for Performance in Corresponding month Last Year
      Statement st2=null;
      ResultSet rs2=null;
      
      // for Cumulative Performance till Current Month
      Statement st4=null;
      ResultSet rs4=null;
     
      // for Cumulative Performance till corresponding month of Last Year
      Statement st3=null;
      ResultSet rs3=null;
 
      // for Taluk Name and Id
      Statement st5=null;
      ResultSet rs5=null;

      // for District Name and Id
      Statement st6=null;
      ResultSet rs6=null;

     // for PHC Name and Id
      Statement st8=null;
      ResultSet rs8=null;

     // for CHC Name and Id
      Statement st9=null;
      ResultSet rs9=null;

     // for PHC Population Estimates
      Statement st10=null;
      ResultSet rs10=null;

     // for Data Period Start Date and End Date
      Statement st11=null;
      ResultSet rs11=null;
      
     // for DataElement ids based on DataElement code
      Statement st12=null;
      ResultSet rs12=null;
     
      
      String userName = "dhis";           
      String password = "";           
      String urlForConnection = "jdbc:mysql://localhost/jh_dhis2";
          
      int talukID = 0;
      String talukName = "";
      int districtID = 0; 
      String districtName = ""; 
      int CHCID = 0;
      String CHCName ="";
      int PHCID = 0;
      String PHCName ="";          
	  String stateName = "";
      int totPHCPopulation = -1;
      int totSCPopulation = -1;

	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID =   Integer.parseInt( selectedId );
	 
	  String startingDate  =   (String) stack.findValue( "startingPeriod" );
	  String endingDate  =    (String) stack.findValue( "endingPeriod" );
      
	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID = 	   Integer.parseInt( monthlyPeriodId );
	       
   	  int lastYear = 0;
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
   	  String selectedDataPeriodEndDate = "";
	  String lastDataPeriodStartDate = "";
	  
	   String dataElementCodes[] = {
										
										"'Form6_DE1'", "'Form6_DE3'",
										
										"' '",
										
										"'Form6_DE6'","'Form6_DE7'","'Form6_DE9'",
										
										"' '","' '",										
										
										"'Form7_DE1'","'Form7_DE1'","'Form7_DE1'",
										
										"' '","' '","' '","' '",
										
										"'Form7_DE5'",
										
										"' '",

										"'Form6_DE13'","'Form6_DE11','Form6_DE12'",

										"' '",

										"'Form6_DE15'","'Form7_DE3'","'Form7_DE3'",

										"' '","' '","' '","' '",

										"'Form6_DE16'","'Form6_DE35'","'Form6_DE36'","'Form6_DE37'",
										"'Form6_DE17','Form6_DE18'",
										"'Form6_DE19','Form6_DE20'",

										"' '",

										"'Form6_DE21','Form6_DE22'",
										"'Form6_DE23','Form6_DE24'",
										"'Form6_DE25','Form6_DE26'",

										"' '",

										"'Form6_DE27','Form6_DE28'",
										"'Form6_DE29','Form6_DE30'",

										"' '","' '","' '",

										"'Form6_DE33'",

										"' '",

										"'Form6_DE40'","'Form6_DE41'",
										
										"' '",
										
										"'Form6_DE42','Form6_DE43'",

										"' '","' '","' '",
	
										"'Form6_DE151'",
										
										"' '","' '","' '",

										"'Form6_DE44'","'Form6_DE45'",
										"'Form6_DE44','Form6_DE45'",

										"'Form6_DE46'","'Form6_DE47'",
										"'Form6_DE46','Form6_DE47'",
										
										"'Form6_DE48'","'Form6_DE49'",
										"'Form6_DE48','Form6_DE49'",
										
										"'Form6_DE50'","'Form6_DE51'",
										"'Form6_DE50','Form6_DE51'",
										
										"'Form6_DE52'","'Form6_DE53'",
										"'Form6_DE52','Form6_DE53'",
										
										"'Form6_DE54'","'Form6_DE55'",
										"'Form6_DE54','Form6_DE55'",
										
										"'Form6_DE56'","'Form6_DE57'",
										"'Form6_DE56','Form6_DE57'",
										
										"'Form6_DE58'","'Form6_DE59'",
										"'Form6_DE58','Form6_DE59'",
										
										"'Form6_DE60'","'Form6_DE61'",
										"'Form6_DE60','Form6_DE61'",

										"' '","' '","' '",

										"'Form6_DE64'","'Form6_DE65'",
										"'Form6_DE64','Form6_DE65'",
										
										"'Form6_DE66'","'Form6_DE67'",
										"'Form6_DE66','Form6_DE67'",
										
										"'Form6_DE68'","'Form6_DE69'",
										"'Form6_DE68','Form6_DE69'",

										"' '","' '","' '",

										"'Form6_DE70'","'Form6_DE71'",
										"'Form6_DE70','Form6_DE71'",

										"' '","' '","' '",

										"'Form6_DE72'","'Form6_DE73'",
										"'Form6_DE72','Form6_DE73'",

										"' '","' '","' '",

										"'Form6_DE74'","'Form6_DE75'",
										"'Form6_DE74','Form6_DE75'",
										
										"'Form6_DE76'","'Form6_DE77'",
										"'Form6_DE76','Form6_DE77'",
										
										"'Form6_DE78'","'Form6_DE79'",
										"'Form6_DE78','Form6_DE79'",
										
										"'Form6_DE80'","'Form6_DE81'",
										"'Form6_DE80','Form6_DE81'",
										
										"'Form6_DE82'","'Form6_DE83'",
										"'Form6_DE82','Form6_DE83'",

										"' '","' '",
										
										"'Form6_DE100','Form6_DE101'",
										"'Form6_DE106','Form6_DE107'",

										"' '",

										"'Form6_DE84','Form6_DE85'",
										"'Form6_DE90','Form6_DE91'",

										"' '",

										"'Form6_DE92','Form6_DE93'",
										"'Form6_DE98','Form6_DE99'",

										"' '",

										"'Form6_DE166','Form6_DE167'",
										"'Form6_DE168','Form6_DE169'",

										"' '",

										"'Form6_DE173','Form6_DE174'",
										"'Form6_DE179','Form6_DE180'",

										"' '",

										"'Form6_DE108','Form6_DE109'",
										"'Form6_DE114','Form6_DE115'",

										"' '",

										"'Form6_DE175','Form6_DE176'",
										"'Form6_DE116','Form6_DE117'",
										"'Form6_DE118','Form6_DE119'",
										"'Form6_DE120','Form6_DE121'",

										"' '",

										"'Form6_DE177','Form6_DE178'",
										"'Form6_DE122','Form6_DE123'",
										"'Form6_DE124','Form6_DE125'",
										"'Form6_DE126','Form6_DE127'",
										"'Form6_DE128','Form6_DE129'",
										"'Form6_DE130','Form6_DE131'",
										"'Form6_DE132','Form6_DE133'",
										"'Form6_DE134','Form6_DE135'",
										"'Form6_DE137','Form6_DE138'",
										"'Form6_DE139','Form6_DE140'",

										"' '",

										"'Form6_DE145','Form6_DE146'",
										"'Form6_DE149'",

										"' '","' '","' '","' '","' '",

										"'Form6_DE171'",

										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",										
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '"																																																			

	  	  							   };

                 
 	int dataElementIDs[] = new int[dataElementCodes.length+5];  
	int entryNumberValues[]=  new int[dataElementCodes.length+5];	  
   	int entryValuesForLastYear[]= new int[dataElementCodes.length+5];      
   	int cumentryValuesForCurYear[]= new int[dataElementCodes.length+5];      
   	int cumentryValuesForLastYear[]= new int[dataElementCodes.length+5];      
      	
   	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }; 	   
   
%>
				
<%
     try
      {
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st9=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st10=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st11=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st12=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

     
        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())  {   selectedOrgUnitName = rs1.getString(1);     }
        else   {     	selectedOrgUnitName = "";                   }  

        //rs11 = st11.executeQuery("select startDate,endDate from period where id = "+selectedDataPeriodID);
		//if(rs11.next())
		//  {
		//	selectedDataPeriodStartDate =  rs11.getDate(1).toString();
		//	selectedDataPeriodEndDate   =  rs11.getDate(2).toString();
		//  }
		
		selectedDataPeriodStartDate = startingDate;
		
      } // try block end
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
			try
				{
  					if(rs1!=null)  rs1.close();			if(st1!=null)  st1.close();
					if(rs11!=null)  rs11.close();		if(st11!=null)  st11.close();
				}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end		

	String partsOfDataPeriodStartDate[]  =  selectedDataPeriodStartDate.split("-");
	lastYear  = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
	lastDataPeriodStartDate = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
     	
	int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
	int tempForYear = 0;
	 	
	if(tempForMonth1 < 4)   	{   tempForYear = lastYear;  }
 	else  {   tempForYear = lastYear + 1;   	}
 	 
 	 String curYearStart = tempForYear+"-04-01";
 	 String lastYearStart = (tempForYear-1)+"-04-01";
 	 String lastYearEnd = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
	 
     //for district, taluk, CHC names
     try
      {
//	        rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");        
//			if(rs8.next())  { PHCID = rs8.getInt(1);PHCName = rs8.getString(2);  } 
//			else  {  PHCID = 0; PHCName = "";  } 

//			rs9=st9.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+PHCID+")");	
//			if(rs9.next())  { CHCID = rs9.getInt(1);CHCName = rs9.getString(2);  } 
//			else  {  CHCID = 0; CHCName = "";  } 

//			rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");	
//			if(rs5.next())  { talukID = rs5.getInt(1); talukName = rs5.getString(2);  } 
//			else  {  talukID = 0; talukName = "";  } 
        
		    //rs6=st6.executeQuery("select organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
		    rs6=st6.executeQuery("select organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")"); 
		    if(rs6.next()) {  stateName = rs6.getString(1);}
			else { stateName = "";}      

			//rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.id = datavalue.dataElement) ON organisationunit.id = datavalue.source WHERE organisationunit.parent = "+PHCID+" AND dataelement.name like 'Total Population'");
			rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.dataelementid = datavalue.dataelementid) ON organisationunit.organisationunitid = datavalue.sourceid WHERE organisationunit.parentid = "+PHCID+" AND dataelement.name like 'Total Population'");
			if(rs10.next()) { totPHCPopulation = rs10.getInt(1);}
			else {totPHCPopulation = 0;}      
       
       }   // try block end		 
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
		 try
			  {
			    if(rs5!=null)  rs5.close();			if(st5!=null)  st5.close();
			    if(rs6!=null)  rs6.close();			if(st6!=null)  st6.close();
			    if(rs8!=null)  rs8.close();			if(st8!=null)  st8.close();   
			    if(rs9!=null)  rs9.close();			if(st9!=null)  st9.close();
			    if(rs10!=null)  rs10.close();		if(st10!=null)  st10.close();                                                                                                                
			  }
		catch(Exception e)   {  out.println(e.getMessage());   }
       }  // finally block end
    
     try
      {
		int i=0;    
		int j= dataElementCodes.length;
		String query = "";
		int in = 0;
			
		while(i!=j)
			{		
				entryNumberValues[i]	 = -1;
				entryValuesForLastYear[i] = -1;
				cumentryValuesForCurYear[i] = -1;
				cumentryValuesForLastYear[i] = -1;
																
				// for Performance in the reporting month
				//query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source in (select id from organisationunit where id = "+selectedOrgUnitID+" OR id  in (select id from organisationunit where parent = "+selectedOrgUnitID+") OR parent in (select id from organisationunit where parent = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";
				query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+" OR organisationunitid  in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";
				rs=st.executeQuery(query);
				if(rs.next())  {  entryNumberValues[i] = rs.getInt(1);  } 
				else  {  entryNumberValues[i] = 0;  } 
												
		        // for Cumulative Performance till Current Month
			    //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")  AND datavalue.source in (select id from organisationunit where id = "+selectedOrgUnitID+" OR id  in (select id from organisationunit where parent = "+selectedOrgUnitID+") OR parent in (select id from organisationunit where parent = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";
			    query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")  AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+" OR organisationunitid  in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";	
			    rs4=st4.executeQuery(query);
     			if(rs4.next())  {  cumentryValuesForCurYear[i]= rs4.getInt(1);  } 
			    else  {  cumentryValuesForCurYear[i] = 0;  } 
			            
				i++;
			}  // while loop end
      }  // try block end   		 
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
			 try
				{
				    if(rs!=null)  rs.close();		if(st!=null)  st.close();      							
					if(rs2!=null)  rs2.close();		if(st2!=null)  st2.close();    
					if(rs3!=null)  rs3.close();		if(st3!=null)  st3.close();					
					if(rs4!=null)  rs4.close();		if(st4!=null)  st4.close();
					if(rs12!=null)  rs12.close();	if(st12!=null)  st12.close();
										
					if(con!=null) con.close();					
		        }	 
			catch(Exception e)   {  out.println(e.getMessage());   }
       }  // finally block end	                     	        
%>

<HTML>
<HEAD>
   <TITLE> Form - 9</TITLE>
   <script src="../dhis-web-reports/Jharkhand/JDENamesForForm9.js" type="text/javascript" language="Javascript"></script> 	
   <script>
   		function fun1()
        	{ 
        	    
        		var start=0;
        		var end = 28;    
        		var j=1;
        		var k=0;             	
        		var id="";        		
        	
        		while(j<=5)
        		 {        		    
        		   	
        		   	if(j==1) end = 30;
					else if(j==2) end = 45;
					else if(j==3) end = 43;
					else if(j==4) end = 44;
					else if(j==5) end = 23;
        		   	
	   	 			for(start=0;start<=end;start++)
	   	 			 {	
					  	 id="cell1"+k;   	 			  
	   	 			  	 document.getElementById(id).innerHTML = slnoForForm9[k];
					  	 id="cell2"+k;   	 			  
	   	 			  	 document.getElementById(id).innerHTML = servicesForForm9[k];
					  	 id="cell3"+k;   	 			  
	   	 			  	 document.getElementById(id).innerHTML = denamesForForm9[k];
	   	 			  	 
	   	 			  	 k++;
	   	 			 }
	   	 			j++; 
	   	 		 }	   	 			   	 		
	  		}
  	</script>   		    
</HEAD>
<BODY BGCOLOR="#FFFFFF" onload="fun1()">  
	<font face="Arial" size="2">(&#2344;&#2367;&#2325;&#2344;&#2375;&#2335; &#2325;&#2375; &#2350;&#2366;&#2343;&#2381;&#2351;&#2350; &#2360;&#2375; &#2352;&#2366;&#2332;&#2381;&#2351; &#2360;&#2352;&#2325;&#2366;&#2352; &#2340;&#2341;&#2366; &#2346;&#2352;&#2367;&#2357;&#2366;&#2352; &#2325;&#2354;&#2381;&#2351;&#2366;&#2339; &#2357;&#2367;&#2349;&#2366;&#2327;, &#2349;&#2366;&#2352;&#2340; &#2360;&#2352;&#2325;&#2366;&#2352; &#2325;&#2379; &#2309;&#2357;&#2369;&#2357;&#2352;&#2381;&#2340;&#2368; &#2350;&#2361;&#2368;&#2344;&#2375; &#2325;&#2368; 25 &#2340;&#2366;&#2352;&#2368;&#2326; &#2340;&#2325; &#2346;&#2381;&#2352;&#2360;&#2381;&#2340;&#2369;&#2340; &#2325;&#2367;&#2351;&#2366; &#2332;&#2366;&#2319;)</font>
	<center>
		<font face="Arial" size="3">
			<b>&#2347;&#2366;&#2352;&#2381;&#2350; 9 <br> &#2352;&#2366;&#2332;&#2381;&#2351;/&#2325;&#2375;&#2306;&#2342;&#2381;&#2352; &#2325;&#2375; &#2354;&#2367;&#2319; &#2332;&#2367;&#2354;&#2375; &#2325;&#2368; &#2360;&#2350;&#2375;&#2325;&#2367;&#2340; &#2350;&#2366;&#2360;&#2367;&#2325; &#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335;	
	</center>
	&#2360;&#2366;&#2350;&#2366;&#2344;&#2381;&#2351;
	</b></font>
		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2">1. &#2352;&#2366;&#2332;&#2381;&#2351; : &nbsp;&nbsp;&nbsp;<%=stateName%></font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       				<font face="Arial" size="2">4. &#2350;&#2366;&#2360; &#2325;&#2368; &#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335; :  &nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%></font>
    		</td>   
  		</tr>
 		<tr>
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">2. &#2332;&#2367;&#2354;&#2366; : &nbsp;&nbsp;<%=selectedOrgUnitName%></font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				<font face="Arial" size="2">5. &#2346;&#2366;&#2340;&#2381;&#2352; &#2342;&#2350;&#2381;&#2346;&#2340;&#2367;&#2351;&#2366;&#2306; ( &#2357;&#2352;&#2381;&#2359; &#2325;&#2368; &#2346;&#2361;&#2354;&#2368; &#2309;&#2346;&#2381;&#2352;&#2376;&#2354; &#2325;&#2379;) </font>
    		</td>
  		</tr>		
 		<tr>
    			<td width="60%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       				<font face="Arial" size="2">3. &#2332;&#2367;&#2354;&#2375; &#2325;&#2368; &#2332;&#2344;&#2360;&#2306;&#2326;&#2381;&#2351;&#2366;</font>
    		</td>
    			<td width="40%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       				<font face="Arial" size="2"></font>
    		</td>
  		</tr>		
	</table>  
<br>

<%
 	int i;
 	int j = 1;
 	int k = 0;
 	int l = 0;
 	int endcount = 28;
 	int flag = 0;
	String tempForentryNumberValues[] = new String[12];
	String tempForcumentryValuesForCurYear[] = new String[12];
	
 	 	 	
 	String temp1 = "";
 	
 	String temp2 = "";
 	
 	int temp = 0;
int bt =0; 	
 	while(j<=5)
 	 { 
 	   
 	    if(j==1) 
 	      { %>
 	       	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
	 	    	<tr>
		    			<td width="3%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left"><font face="Arial" size="2"><b>&#2325;&#2381;&#2352;. &#2360;&#2306;.</b></font></td>
    					<td width="36%" align="center" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ><font face="Arial" size="2"><b>&#2360;&#2375;&#2357;&#2366;</b></font></td>
    					<td width="45%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr"><font face="Arial" size="2"></font></td>
    					<td width="16%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"></font></td>
				</tr>
		   <%	 	      
 	       }
 	    else if(j==2)
 	      { %>
			<br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
        else if(j==3)
 	      { %>
			<br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 		else if(j==4)
 	      { %>
			<br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 		else if(j==5)
 	      { %>
			<br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 	    else
 	      { %>
			<br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}

		if(j==1) endcount = 30; 
		else if(j==2) endcount = 45; 
		else if(j==3) endcount = 43;
		else if(j==4) endcount = 44;
		else if(j==5) endcount = 23;
												
 		for(i=0;i<=endcount;i++)
 	 	  {  	
 	     	String id1="cell1"+k;
 	     	String id2="cell2"+k;
 	     	String id3="cell3"+k;
 	     	 	    
 	     	if((k>=53 && k<=76))
 	      	{ 
               if(k==53) 
 	            {
 	              tempForentryNumberValues[0]= "<b>M</b>";
 	              tempForentryNumberValues[1]= "<b>F</b>";
				  tempForentryNumberValues[2]= "<b>T</b>";
 	              
 	              l=l+3;
 	            }
 	           else if(k==2 || k==6 || k==7 || k==11 || k==12 || k==13 || k==14 || k==16 || k==19 || k==23 || k==24 || k==25 || k==26 || k==33 || k==37 || k==40 || k==41 || k==44 || k==49 || k==50 || k==51 || k==63 || k==67 || k==69 || k==71 || k==77 || k==78 || k==81 || k==84 || k==87 || k==90 || k==93 || k==96 || k==101 || k==112 || k==115)
 				{
 	              tempForentryNumberValues[0]= " ";
 	              tempForentryNumberValues[1]= " ";
				  tempForentryNumberValues[2]= " ";
  	              
 	              l=l+3;
 	            }
 	            
 	           else 
 	            {
   					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
// tempForentryNumberValues[0] = dataElementCodes[l];
  					
					l++;
						         
 	         		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;
 //tempForentryNumberValues[1] = dataElementCodes[l];
  	         							
 	         		l++;
 	         		
			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[2] = "";
 	         		else tempForentryNumberValues[2] = ""+temp;
 	         		
					l++;
 	            } 
 	        %> 	        
 	   
  	    	<tr>
	    			<td id="<%=id1%>" name="<%=id1%>" width="3%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    				<td id="<%=id2%>" name="<%=id2%>" width="36%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    				<td id="<%=id3%>" name="<%=id3%>" width="45%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr"></td>
    				<td width="5%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    				<td width="5%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    				<td width="6%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
			</tr>
 	        
 	   <%   }
 	     else 
 	      {   	       
			 if(k==2 || k==6 || k==7 || k==11 || k==12 || k==13 || k==14 || k==16 || k==19 || k==23 || k==24 || k==25 || k==26 || k==33 || k==37 || k==40 || k==41 || k==44 || k==49 || k==50 || k==51 || k==63 || k==67 || k==69 || k==71 || k==77 || k==78 || k==81 || k==84 || k==87 || k==90 || k==93 || k==96 || k==101 || k==112 || k==115) 
 	            {
 	              tempForentryNumberValues[0]= "";
 
	              l++;
 	             }
		 	 else 
 	          {
 					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 //tempForentryNumberValues[0] = dataElementCodes[l];
 	         		 					
					l++;
 	          }	
 	       %>
 	    	<tr>
	    			<td id="<%=id1%>" name="<%=id1%>" width="3%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    				<td id="<%=id2%>" name="<%=id2%>" width="36%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    				<td id="<%=id3%>" name="<%=id3%>" width="45%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" dir="ltr"><font face="Arial" size="2"></font></td>
    				<td width="16%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
			</tr>
 	     	<% 
 	     }    	  	   
		 k++;
		}		
		 j++;
		%>
		</table>
		<%
	 }	
 %>
</BODY>
</HTML>