
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

      // for State Name
      Statement st12=null;
      ResultSet rs12=null;
      
      // for DataElement IDs
      Statement st13=null;
      ResultSet rs13=null;
            
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
      int totPHCPopulation = 0;
      int totSCPopulation = 0;

	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID = Integer.parseInt( selectedId );
	
//	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	  int selectedDataPeriodID =  Integer.parseInt( selectedPeriodId );

  	  String startingDate  = (String) stack.findValue( "startingPeriod" );
	  String endingDate  = (String) stack.findValue( "endingPeriod" );

      
	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID =   Integer.parseInt( monthlyPeriodId );
	       
      int lastYear = 0;
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
        String selectedDataPeriodEndDate = "";
	  String lastDataPeriodStartDate = "";

	  String dataElementCodes[] = {
	                                " "," ","Form6_DE1","Form6_DE2","Form6_DE3",
	                                "Form6_DE4"," ","Form6_DE5","Form6_DE6","Form6_DE7","Form6_DE8","Form6_DE9"," ","Form6_DE10"," ","Form6_DE11","Form6_DE12","Form6_DE13","Form6_DE14","Form6_DE15","Form6_DE16"," "," ","Form6_DE17","Form6_DE18","Form6_DE19","Form6_DE20"," "," ","Form6_DE21","Form6_DE22","Form6_DE23","Form6_DE24","Form6_DE25","Form6_DE26",
	                                " "," ","Form6_DE27","Form6_DE28","Form6_DE29","Form6_DE30","Form6_DE31","Form6_DE32"," ","Form6_DE33","Form6_DE34"," ","Form6_DE35","Form6_DE36","Form6_DE37"," "," "," "," ","Form6_DE38","Form6_DE39","Form6_DE40","Form6_DE41","Form6_DE42","Form6_DE43"," "," "," "," ","Form6_DE44","Form6_DE45","Form6_DE46","Form6_DE47","Form6_DE48","Form6_DE49","Form6_DE50","Form6_DE51","Form6_DE52","Form6_DE53","Form6_DE54","Form6_DE55",
	                                "Form6_DE56","Form6_DE57","Form6_DE58","Form6_DE59","Form6_DE60","Form6_DE61"," "," ","Form6_DE64","Form6_DE65","Form6_DE66","Form6_DE67","Form6_DE68","Form6_DE69","Form6_DE70","Form6_DE71","Form6_DE72","Form6_DE73","Form6_DE74","Form6_DE75","Form6_DE76","Form6_DE77"," "," ","Form6_DE78","Form6_DE79","Form6_DE80","Form6_DE81","Form6_DE82","Form6_DE83"," "," "," "," "," "," ","Form6_DE84","Form6_DE85","Form6_DE86","Form6_DE87","Form6_DE88","Form6_DE89","Form6_DE90","Form6_DE91",
	                                " "," ","Form6_DE92","Form6_DE93","Form6_DE94","Form6_DE95","Form6_DE96","Form6_DE97","Form6_DE98","Form6_DE99"," "," ","Form6_DE100","Form6_DE101","Form6_DE102","Form6_DE103","Form6_DE104","Form6_DE105","Form6_DE106","Form6_DE107"," "," ","Form6_DE108","Form6_DE109","Form6_DE110","Form6_DE111","Form6_DE112","Form6_DE113","Form6_DE114","Form6_DE115"," "," ","Form6_DE116","Form6_DE117","Form6_DE118","Form6_DE119","Form6_DE120","Form6_DE121"," "," ","Form6_DE122","Form6_DE123","Form6_DE124","Form6_DE125","Form6_DE126","Form6_DE127",
	                                " "," ","Form6_DE128","Form6_DE129","Form6_DE130","Form6_DE131","Form6_DE132","Form6_DE133","Form6_DE134","Form6_DE135"," ","Form6_DE136"," ","Form6_DE137","Form6_DE138"," ","Form6_DE139","Form6_DE140"," ","Form6_DE141","Form6_DE142"," ","Form6_DE143","Form6_DE144"," ","Form6_DE145","Form6_DE146","Form6_DE147","Form6_DE148","Form6_DE149",
	                                " ","Form6_DE150","Form6_DE151","Form6_DE152","Form6_DE153","Form6_DE154"," "," ","Form6_DE155","Form6_DE156","Form6_DE157","Form6_DE158","Form6_DE159","Form6_DE160","Form6_DE161"," "," ","Form6_DE162","Form6_DE163","Form6_DE164","Form6_DE165"                          	  
	  							   };

      int dataElementIDs[] = new int[250];        
	  int entryNumberValues[]=  new int[250];	  
      int entryValuesForLastYear[]= new int[250];      
      int cumentryValuesForCurYear[]= new int[250];      
      int cumentryValuesForLastYear[]= new int[250];       	      
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
        st13=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
     
        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())
          {
            selectedOrgUnitName = rs1.getString(1);
          }
        else 
         { 
           	selectedOrgUnitName = "";
            totSCPopulation = 0;            
		 }  

//        rs11 = st11.executeQuery("select startDate,endDate from period where id = "+selectedDataPeriodID);
//	    if(rs11.next())
//		  {
//		    selectedDataPeriodStartDate =  rs11.getDate(1).toString();
//			selectedDataPeriodEndDate   =  rs11.getDate(2).toString();
//		  }
		
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
	        //rs8=st8.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
	        rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");        
			if(rs8.next())  { PHCID = rs8.getInt(1);PHCName = rs8.getString(2);  } 
			else  {  PHCID = 0; PHCName = "";  } 

			
        
		    //rs6=st6.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+PHCID+")");
		    rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+PHCID+")"); 
		    if(rs6.next()) {  districtID = rs6.getInt(1); districtName = rs6.getString(2);}
			else {districtID = 0; districtName = "";}      

			//rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.id = datavalue.dataElement) ON organisationunit.id = datavalue.source WHERE organisationunit.parent = "+PHCID+" AND dataelement.name like 'Total Population'");
			rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.dataelementid = datavalue.dataelementid) ON organisationunit.organisationunitid = datavalue.sourceid WHERE organisationunit.parentid = "+PHCID+" AND dataelement.name like 'Total Population'");
			if(rs10.next()) { totPHCPopulation = rs10.getInt(1);}
			else {totPHCPopulation = 0;}      
			
			 //rs12=st12.executeQuery("select organisationunit.id, organisationunit.shortname FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+districtID+")");
			 rs12=st12.executeQuery("select organisationunit.organisationunitid, organisationunit.shortname FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+districtID+")"); 
		     if(rs12.next()) {  stateName = rs12.getString(2);}
			 else { stateName = "";}  
       
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
			    if(rs12!=null)  rs12.close();		if(st12!=null)  st12.close();
			  }
		catch(Exception e)   {  out.println(e.getMessage());   }
       }  // finally block end
    
     try
      {
		int i=0;    
		int j= dataElementCodes.length;
		while(i!=j)
			{			  	
			  	//rs13 = st13.executeQuery("select id from dataelement where code like '"+dataElementCodes[i]+"'");
			  	rs13 = st13.executeQuery("select dataelementid from dataelement where code like '"+dataElementCodes[i]+"'");
	    	  	if(rs13.next())  	{   dataElementIDs[i] = rs13.getInt(1); }
	    	  	else { dataElementIDs[i] = 0; }
				
			
				entryNumberValues[i]	 = -1;
				entryValuesForLastYear[i] = -1;
				 cumentryValuesForCurYear[i] = -1;
				 cumentryValuesForLastYear[i] = -1;
	
				
				
				if(dataElementIDs[i]!=0)
					{
						// for Performance in the reporting month
						//rs=st.executeQuery("select sum(value) from datavalue where source = "+selectedOrgUnitID+" and period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') and dataElement = "+dataElementIDs[i]);
						rs=st.executeQuery("select sum(value) from datavalue where sourceid = "+selectedOrgUnitID+" and periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') and dataelementid = "+dataElementIDs[i]);
					    if(rs.next())  {  entryNumberValues[i] = rs.getInt(1);  } 
						else  {  entryNumberValues[i] = 0;  } 
						
						// for Performance in Corresponding month Last Year
			        	//rs2=st2.executeQuery("select sum(value) from datavalue where dataElement ="+dataElementIDs[i]+" and source= "+selectedOrgUnitID+" and period in (select id from period where startdate like '"+lastYearEnd+"' and periodType = "+periodTypeID+" )");
			        	rs2=st2.executeQuery("select sum(value) from datavalue where dataelementid ="+dataElementIDs[i]+" and sourceid= "+selectedOrgUnitID+" and periodid in (select periodid from period where startdate like '"+lastYearEnd+"' and periodtypeid = "+periodTypeID+" )"); 
	        	        if(rs2.next())  {  entryValuesForLastYear[i] = rs2.getInt(1);  } 
			            else  {  entryValuesForLastYear[i] = 0;  } 
			                
			            // for Cumulative Performance till Current Month
			            //rs4=st4.executeQuery("select sum(value) from datavalue where dataElement ="+dataElementIDs[i]+" and source= "+selectedOrgUnitID+" and period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+") ");
			            rs4=st4.executeQuery("select sum(value) from datavalue where dataelementid ="+dataElementIDs[i]+" and sourceid= "+selectedOrgUnitID+" and periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+") ");
     					if(rs4.next())  {  cumentryValuesForCurYear[i]= rs4.getInt(1);  } 
			            else  {  cumentryValuesForCurYear[i] = 0;  } 
			            
			            // for Cumulative Performance till corresponding month of Last Year
			            //rs3=st3.executeQuery("select sum(value) from datavalue where dataElement ="+dataElementIDs[i]+" and source= "+selectedOrgUnitID+" and period in (select id from period where startdate between '"+lastYearStart+"' and '"+lastYearEnd+"' and periodType = "+periodTypeID+") ");
			            rs3=st3.executeQuery("select sum(value) from datavalue where dataelementid ="+dataElementIDs[i]+" and sourceid= "+selectedOrgUnitID+" and periodid in (select periodid from period where startdate between '"+lastYearStart+"' and '"+lastYearEnd+"' and periodtypeid = "+periodTypeID+") ");
					    if(rs3.next())  {  cumentryValuesForLastYear[i]= rs3.getInt(1);  } 
				    	else  {  cumentryValuesForLastYear[i] = 0;  } 					    					    	
					} // if block end
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
					if(rs13!=null)  rs13.close();	if(st13!=null)  st13.close();
										
					if(con!=null) con.close();					
		        }	 
			catch(Exception e)   {  out.println(e.getMessage());   }
       }  // finally block end	                     	        
%>

<HTML>
<HEAD>
   <TITLE> Report of ANM </TITLE>
   <script src="../dhis-web-reports/Jharkhand/JDENamesForForm6.js" type="text/javascript" language="Javascript"></script> 	
   <script>
   		function fun1()
        	{         	    
        		var start=0;
        		var end = 28;    
        		var j=1;
        		var k=0;             	
        		var id="";        		
        	
        		while(j<=7)
        		 {        		    
        		   	
        		   	if(j==1) end = 4;
        		   	else if(j==2) end = 23;
        		   	else if(j==3) end = 23;
        		   	else if(j==4) end = 21;
        		   	else if(j==5) end = 22;
        		   	else if(j==6) end = 24;
        		   	else if(j==7) end = 21;
	   	 			for(start=0;start<=end;start++)
	   	 			 {	
					  	id="cell1"+k;   	 			  
	   	 			  	document.getElementById(id).innerHTML = slnoForForm6[k];
					  	id="cell2"+k;   	 			  
	   	 			  	document.getElementById(id).innerHTML = denamesForForm6[k];
	   	 			  	k++;
	   	 			 }
	   	 			j++; 
	   	 		 }	   	 			   	 		
	  		}
  	</script>   		    
</HEAD>
<BODY BGCOLOR="#FFFFFF" onload="fun1()">
	<br>
	<font face="Arial" size="2">
	        (&#2346;&#2381;&#2352;&#2366;.&#2360;&#2381;&#2357;&#2366;.&#2325;&#2375;&#2306; &#2325;&#2379; &#2309;&#2344;&#2369;&#2357;&#2352;&#2381;&#2340;&#2368; &#2350;&#2361;&#2368;&#2344;&#2375; &#2325;&#2368; 15 &#2340;&#2366;&#2352;&#2368;&#2326; &#2340;&#2325; &#2346;&#2381;&#2352;&#2360;&#2381;&#2340;&#2369;&#2340; &#2325;&#2367;&#2351;&#2366; &#2332;&#2366;&#2319;)
	</font>
	<br><br>        
	<center>
		<font face="Arial" size="3">
			<b>&#2347;&#2366;&#2352;&#2381;&#2350; 6<BR>
			&#2313;&#2346;&#2325;&#2375;&#2306;&#2342;&#2381;&#2352;/&#2358;&#2361;&#2352;&#2368; &#2360;&#2381;&#2357;&#2360;&#2381;&#2341;&#2381;&#2351; &#2330;&#2380;&#2325;&#2368;/&#2346;&#2369;&#2344;&#2307; &#2344;&#2357;&#2368;&#2351;&#2344; (&#2352;&#2367;&#2357;&#2376;&#2350;&#2381;&#2346;&#2367;&#2306;&#2327;) &#2325;&#2375;&#2306;&#2342;&#2381;&#2352; &#2325;&#2375; &#2354;&#2367;&#2319; &#2350;&#2366;&#2360;&#2367;&#2325; &#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335;<BR>
			(&#2319; &#2319;&#2344; &#2319;&#2350;/&#2319;&#2350; &#2346;&#2368; &#2337;&#2348;&#2381;&#2354;&#2370; (&#2346;&#2369;&#2352;&#2369;&#2359;) &#2325;&#2368; &#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335;)
		
	</center>
	<br>
I.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2360;&#2366;&#2350;&#2366;&#2344;&#2381;&#2351; &#2360;&#2370;&#2330;&#2344;&#2366;</b>
</font>	
	<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  		<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="2">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2352;&#2366;&#2332;&#2381;&#2351; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp; &nbsp;&nbsp;&nbsp;<b><%=stateName%></b> </font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="2">7.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2350;&#2361;&#2368;&#2344;&#2375; &#2325;&#2368; &#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335; :&nbsp;&nbsp;<%=selectedDataPeriodStartDate%>&nbsp;&nbsp; </font>
    		</td>   
  		</tr>
 		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       			<font face="Arial" size="2">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2332;&#2367;&#2354;&#2366; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp; &nbsp;&nbsp;&nbsp;<b><%=districtName%></b></font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       			<font face="Arial" size="2">8.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2346;&#2366;&#2340;&#2381;&#2352; &#2342;&#2350;&#2381;&#2346;&#2340;&#2367;&#2351;&#2366;&#2306; (&#2357;&#2352;&#2381;&#2359; &#2325;&#2368; &#2346;&#2361;&#2354;&#2368; &#2309;&#2346;&#2381;&#2352;&#2376;&#2354; &#2325;&#2379;) :</font>
    		</td>
  		</tr>
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       			<font face="Arial" size="2">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2346;&#2368; &#2319;&#2330; &#2360;&#2368; (&#2346;&#2381;&#2352;.&#2360;&#2381;&#2357;&#2366;.&#2325;&#2375;&#2306;.) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b><%=PHCName%></b></font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19"></td>
  		</tr>
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="2">4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2313;&#2346;&#2325;&#2375;&#2306;&#2342;&#2381;&#2352;&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b><%=selectedOrgUnitName%></b> </font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" height="23"></td>
  		</tr>
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" height="19">
       			<font face="Arial" size="2">5.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2346;&#2381;&#2352;.&#2360;&#2381;&#2357;&#2366;.&#2325;&#2375;&#2306;. &#2325;&#2368; &#2332;&#2344;&#2360;&#2306;&#2325;&#2381;&#2351;&#2366; :&nbsp;&nbsp;&nbsp;&nbsp;<%=totPHCPopulation%></font>
    		</td>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" height="19"></td>
  		</tr> 
  		<tr>
    		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" height="1" colspan="2">
            	<font face="Arial" size="2">6.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2313;&#2346;&#2325;&#2375;&#2306;&#2342;&#2381;&#2352; &#2325;&#2368; &#2332;&#2344;&#2360;&#2306;&#2325;&#2381;&#2351;&#2366; :&nbsp;&nbsp;&nbsp;&nbsp;<%=totSCPopulation%></font>
	       	</td>
      		<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" height="19"> </td>
  		</tr>
  </table>  
<br><br>
<font face="Arial" size="3"><b>II.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#2360;&#2375;&#2357;&#2366;&#2319;&#2306;</b></font><br>
<br>
<%
 	int i;
 	int j = 1;
 	int k = 0;
 	int l = 0;
 	int endcount = 28;
 	int flag = 0;
 	String tempForentryNumberValues1 = "";
 	String tempForentryNumberValues2 = "";
 	String tempForentryValuesForLastYear1 = "";
 	String tempForentryValuesForLastYear2 = "";
 	String tempForcumentryValuesForCurYear1 = "";
 	String tempForcumentryValuesForCurYear2 = "";
 	String tempForcumentryValuesForLastYear1 = "";
 	String tempForcumentryValuesForLastYear2 = "";
 	 	 	
 	String temp1 = "";
 	
 	String temp2 = "";
 	
 	int temp = 0;
 	while(j<=7)
 	 { 
 	    if(flag == 0) 
 	      { %>
 	       	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
	 			<tr>
    				<td width="3%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2">&#2325;&#2381;&#2352;. &#2360;&#2306;.</font></td>
    				<td width="47%"  valign="top" style="border-style:solid; border-width:1; border-collapse: collapse; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2">&#2360;&#2375;&#2357;&#2366;&#2319;&#2306;</font></td>
    				<td width="10%"  valign="top" style="border-style:solid; border-width:1; border-collapse: collapse; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"  colspan="2"><font face="Arial" size="2">&#2327;&#2340; &#2357;&#2352;&#2381;&#2359; &#2311;&#2360;&#2368; &#2350;&#2361;&#2368;&#2344;&#2375; &#2325;&#2375; &#2342;&#2380;&#2352;&#2366;&#2344; &#2344;&#2367;&#2359;&#2381;&#2346;&#2366;&#2342;&#2344;</font></td>
    				<td width="10%"  valign="top" style="border-style:solid; border-width:1; border-collapse: collapse; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"  colspan="2"><font face="Arial" size="2">&#2352;&#2367;&#2346;&#2379;&#2352;&#2381;&#2335;&#2366;&#2343;&#2368;&#2344; &#2350;&#2361;&#2368;&#2344;&#2375; &#2350;&#2375;&#2306; &#2344;&#2367;&#2359;&#2381;&#2346;&#2366;&#2342;&#2344;</font></td>
    				<td width="10%"  valign="top" style="border-style:solid; border-width:1; border-collapse: collapse; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"  colspan="2"><font face="Arial" size="2">&#2327;&#2340; &#2357;&#2352;&#2381;&#2359; &#2325;&#2375; &#2311;&#2360;&#2368; &#2350;&#2361;&#2368;&#2344;&#2375; &#2340;&#2325; &#2360;&#2306;&#2330;&#2351;&#2368; &#2344;&#2367;&#2359;&#2381;&#2346;&#2366;&#2342;&#2344;</font></td>
    				<td width="10%"  valign="top" style="border-style:solid; border-width:1; border-collapse: collapse; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"  colspan="2"><font face="Arial" size="2">&#2330;&#2366;&#2354;&#2370; &#2357;&#2352;&#2381;&#2359; &#2350;&#2375;&#2306; &#2357;&#2352;&#2381;&#2340;&#2350;&#2366;&#2344; &#2350;&#2361;&#2368;&#2344;&#2375; &#2340;&#2325; &#2360;&#2306;&#2330;&#2351;&#2368; &#2344;&#2367;&#2359;&#2381;&#2346;&#2366;&#2342;&#2344;</font></td>
    				<td width="10%"  valign="top" style="border-style:solid; border-width:1; border-collapse: collapse; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2">&#2330;&#2366;&#2354;&#2370; &#2357;&#2352;&#2381;&#2359; &#2325;&#2375; &#2354;&#2367;&#2319; &#2351;&#2379;&#2332;&#2344;&#2366;&#2348;&#2342;&#2381;&#2342; &#2310;&#2357;&#2358;&#2381;&#2351;&#2325;&#2340;&#2366;</font></td>
  				</tr>
		   <%	
 	       flag = 1;
 	       }
 	    else
 	      { %>
 	      	
 	      	<div align="right"><font face="Arial" size="1"><i>
 	      	(&#2398;&#2366;&#2352;&#2381;&#2350; 6 &#2325;&#2381;&#2352;&#2350;&#2358;&#2307;)</i></font></div>
 			<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">  
 	      <%}   
 	       		
		if(j==1) endcount = 4;
		else if(j==2) endcount = 23;
		else if(j==3) endcount = 23;
		else if(j==4) endcount = 21; 
		else if(j==5) endcount = 22;
		else if(j==6) endcount = 24;
		else if(j==7) endcount = 21;
 		for(i=0;i<=endcount;i++)
 	 	{  	
 	     String id1="cell1"+k;
 	     String id2="cell2"+k;
 	    
 	     if((k>=21 && k<=32) || (k>=40&&k<=102))
 	      { 
 	        // if(k==33 || k==45 || k==46 || k==47 || k==113 || k==117)
 	        //   {
			//	temp = entryNumberValues[l] + entryNumberValues[l+1];
			//	if(temp==0) tempForentryNumberValues1 = "";
			//	else tempForentryNumberValues1 = ""+temp;
				
			//	temp = entryValuesForLastYear[l] + entryValuesForLastYear[l+1];
			//	if(temp==0) tempForentryValuesForLastYear1 = "";
			//	else tempForentryValuesForLastYear1 = ""+temp;
								
			//	temp = cumentryValuesForLastYear[l] + cumentryValuesForLastYear[l+1];
			//	if(temp==0) tempForcumentryValuesForLastYear1 = "";
			//	else tempForcumentryValuesForLastYear1 = ""+temp;

			//	temp = cumentryValuesForCurYear[l] + cumentryValuesForCurYear[l+1];
			//	if(temp==0) tempForcumentryValuesForCurYear1 = "";
			//	else tempForcumentryValuesForCurYear1 = ""+temp;

				//temp = dataElementIDs[l] + dataElementIDs[l+1];
				//if(temp==0) temp1 = "";
				//else temp1 = ""+temp;
				
			//	l = l+2;
				
			//	temp = entryNumberValues[l] + entryNumberValues[l+1];
 	          //  if(temp==0) tempForentryNumberValues2 = "";
 	           // else tempForentryNumberValues2 = ""+temp;
 	            
 	            //temp = entryValuesForLastYear[l] + entryValuesForLastYear[l+1];
 	            //if(temp==0) tempForentryValuesForLastYear2 = "";
 	            //else tempForentryValuesForLastYear2 = ""+temp; 	            

				//temp = cumentryValuesForLastYear[l] + cumentryValuesForLastYear[l+1];
				//if(temp==0) tempForcumentryValuesForLastYear1 = "";
				//else tempForcumentryValuesForLastYear1 = ""+temp;

				//temp = cumentryValuesForCurYear[l] + cumentryValuesForCurYear[l+1];
				//if(temp==0) tempForcumentryValuesForCurYear2 = "";
				//else tempForcumentryValuesForCurYear2 = ""+temp;

				//temp = dataElementIDs[l] + dataElementIDs[l+1];
				//if(temp==0) temp2 = "";
				//else temp2 = ""+temp;
				
				//l = l+2; 	             	            
 	          // } // if block end
 	          
 	          if(k==21) 
 	            {
 	              tempForentryNumberValues1= "&#2346;&#2369;.";
 	              tempForentryValuesForLastYear1 = "&#2346;&#2369;.";
 	              tempForcumentryValuesForLastYear1 = "&#2346;&#2369;.";
 	              tempForcumentryValuesForCurYear1 = "&#2346;&#2369;.";
 	              
 	              tempForentryNumberValues2 = "&#2350;.";
 	              tempForentryValuesForLastYear2 = "&#2350;.";
 	              tempForcumentryValuesForLastYear2 = "&#2350;.";
 	              tempForcumentryValuesForCurYear2 = "&#2350;."; 	               	               	              
 	            }
 	          else
 	            {
 	             temp = entryNumberValues[l];
				 if(temp==-1) tempForentryNumberValues1 = "";
 	             else tempForentryNumberValues1 = ""+temp;
 	              
 	             temp = entryValuesForLastYear[l];
				 if(temp==-1) tempForentryValuesForLastYear1 = "";
 	             else tempForentryValuesForLastYear1 = ""+temp;

				 temp = cumentryValuesForLastYear[l];
				 if(temp==-1) tempForcumentryValuesForLastYear1 = "";
				 else tempForcumentryValuesForLastYear1 = ""+temp;
 	             
  				 temp = cumentryValuesForCurYear[l];
				 if(temp==-1) tempForcumentryValuesForCurYear1 = "";
				 else tempForcumentryValuesForCurYear1 = ""+temp;

 	             temp = dataElementIDs[l];
 	             if(temp==-1) temp1 = "";
				 else temp1 = ""+temp;

				 l++;	
 	             
				 temp = entryNumberValues[l];
				 if(temp==-1) tempForentryNumberValues2 = "";
 	             else tempForentryNumberValues2 = ""+temp;
 	             
 	             temp = entryValuesForLastYear[l];
				 if(temp==-1) tempForentryValuesForLastYear2 = "";
 	             else tempForentryValuesForLastYear2 = ""+temp;
 	             
				 temp = cumentryValuesForLastYear[l];
				 if(temp==-1) tempForcumentryValuesForLastYear2 = "";
				 else tempForcumentryValuesForLastYear2 = ""+temp;

				 temp = cumentryValuesForCurYear[l];
				 if(temp==-1) tempForcumentryValuesForCurYear2 = "";
				 else tempForcumentryValuesForCurYear2 = ""+temp; 	             
 	             
 	             temp = dataElementIDs[l];
 	             if(temp==-1) temp2 = "";
				 else temp2 = ""+temp;
				 
				 l++;

 	            }  // else block end
 	        %>
 	        <tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="3%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding: 0" bordercolor="#111111" width="100%" align="left"></td>
    			<td id="<%=id2%>" name="<%=id2%>" width="47%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%"></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryValuesForLastYear1%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryValuesForLastYear2%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues1%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues2%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForLastYear1%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForLastYear2%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear1%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear2%></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"></font></td>
    			<td width="5%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:3; padding-top:0; padding-bottom:0" bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"></font></td>
  			</tr>
 	   <%   }
 	     else
 	      {  
 	      // if(k==21 || k==36)
 	        // {
 	         // temp = entryNumberValues[l] + entryNumberValues[l+1];
 	          //if(temp==0) tempForentryNumberValues1 = "";
 	          //else tempForentryNumberValues1 = ""+temp;
 	          
              //temp = entryValuesForLastYear[l] + entryValuesForLastYear[l+1];
 	          //if(temp==0) tempForentryValuesForLastYear1 = "";
 	          //else tempForentryValuesForLastYear1 = ""+temp;
 	          
			  //temp = cumentryValuesForLastYear[l] + cumentryValuesForLastYear[l+1];
			  //if(temp==0) tempForcumentryValuesForLastYear1 = "";
			  //else tempForcumentryValuesForLastYear1 = ""+temp;
 	          
			  //temp = cumentryValuesForCurYear[l] + cumentryValuesForCurYear[l+1];
			  //if(temp==0) tempForcumentryValuesForCurYear1 = "";
			  //else tempForcumentryValuesForCurYear1 = ""+temp;
 	          
              //temp = dataElementIDs[l] + dataElementIDs[l+1];
              //if(temp==0) temp1 = "";
 			  //else temp1 = ""+temp;
 			  
 			  //l = l+2; 	           	          
 	         //} 
 	       //else 
 	        //{
 	         temp = entryNumberValues[l];
 	         if(temp==-1) tempForentryNumberValues1 = "";
 	         else tempForentryNumberValues1 = ""+temp;
 	         
 	         temp = entryValuesForLastYear[l];
 	         if(temp==-1) tempForentryValuesForLastYear1 = "";
 	         else tempForentryValuesForLastYear1 = ""+temp;
 	         
			 temp = cumentryValuesForLastYear[l];
			 if(temp==-1) tempForcumentryValuesForLastYear1 = "";
			 else tempForcumentryValuesForLastYear1 = ""+temp;

 			 temp = cumentryValuesForCurYear[l];
			 if(temp==-1) tempForcumentryValuesForCurYear1 = "";
			 else tempForcumentryValuesForCurYear1 = ""+temp;
 	         
 	         temp = dataElementIDs[l];
             if(temp==-1) temp1 = "";
 			 else temp1 = ""+temp;

			 l++;
 	        //}  
 	       %>
 	       	<tr>
    			<td id="<%=id1%>" name="<%=id1%>"  valign="top" width="3%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; " bordercolor="#111111" width="100%" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>"  valign="top" width="47%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:15; padding-right:1; " bordercolor="#111111" width="100%"></td>
    			<td width="10%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryValuesForLastYear1%></font></td>
    			<td width="10%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues1%></font></td>
    			<td width="10%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForLastYear1%></font></td>
    			<td width="10%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear1%></font></td>
	 			<td width="10%"  valign="top" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><font face="Arial" size="2"></font></td>
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