
<%@ page import="java.sql.*,java.util.*,java.math.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%
    Connection con=null;
            

    // For finding organisationunit name of selected Orgunit based on Orgunit id
    Statement st1=null;
    ResultSet rs1=null;

    // For finding child orgunit ids and names based on selected orgunit id
    Statement st2=null;
    ResultSet rs2=null;
    
    // For finding datavalues
    Statement st3=null;
    ResultSet rs3=null;

    // For finding PHC datavalues
    Statement st4=null;
    ResultSet rs4=null;
    
    // For finding BirthRate
    Statement st5=null;
    ResultSet rs5=null;


    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
    

  	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
  	
	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = Integer.parseInt( selectedId );
	
  
	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = Integer.parseInt( monthlyPeriodId );
	       

      
	String selectedOrgUnitName = "";
	String selectedDataPeriodStartDate = "";
    String selectedDataPeriodEndDate = "";
	String lastDataPeriodStartDate = "";


    List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	

	int childOrgUnitID = 0;
//	String childOrgUnitName = "";			
  	int childOrgUnitCount = 0; 
  	
	double maxValue[] = new double[10];
	double minValue[] = new double[10]; 	
	int i = 0;
	
	double const1 = 1.1;
	
	String maxOrgUnitName[] = new String[10];	
	String minOrgUnitName[] = new String[10];

    
    String DECodes[] = {
    						// Population
    						"'SD_DE0007'",
    						
    						// Delivieries
    						"'Form6_DE0057','Form6_DE0060','Form6_DE0063','Form6_DE0066','Form6_DE0069','Form6_DE0072','Form6_DE0075','Form6_DE0078'",
    						
    						// Institutional Deliveries
    						"'Form6_DE0072','Form6_DE0075','Form6_DE0078','Form6_DE0069'",
    						
    						// Deliveries by Health Professional
    						"'Form6_DE0057','Form6_DE0060','Form6_DE0063','Form6_DE0069','Form6_DE0072','Form6_DE0075','Form6_DE0078'",
    						
    						// 3 PostNatal Visits
    						"'Form6_DE0204','Form6_DE0207'",
    						
    						// Birth Rate
    						"'SD_DE0008'"
    						
    					};

  	
  	double dataValues[][] = new double[25][DECodes.length];
  	double sumOfValues[] = new double[DECodes.length]; 
  	double PHCdataValues[] = new double[DECodes.length]; 
  	double PHCsumOfValues[] = new double[DECodes.length]; 

  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }; 	   
	String partsOfStartDate[] = startingDate.split("-");
	String partsOfEndDate[] = endingDate.split("-");

  	String query = "";
  	
  	double birthRate = 24.3;
  	int population = 0;

	String startingYear = "";
	String endingYear = "";
	
	
     int lastYear = Integer.parseInt(partsOfStartDate[0]) - 1;
     int tempForMonth1 = Integer.parseInt(partsOfStartDate[1]);
     int tempForYear = 0;
     if(tempForMonth1 < 4)
        {   
        	tempForYear = lastYear;
        	startingYear = ""+tempForYear+"-04-01";
			endingYear = ""+(tempForYear+1)+"-03-20";        	        
        }
     else  
       {   
       		tempForYear = lastYear + 1;   	
       		startingYear = ""+tempForYear+"-04-01";
			endingYear = ""+(tempForYear+1)+"-03-20";       	
       	}

	int noOfMonths = ( 12 * ( Integer.parseInt(partsOfEndDate[1]) - Integer.parseInt(partsOfStartDate[1]) ) ) - Integer.parseInt(partsOfStartDate[1]) + Integer.parseInt(partsOfEndDate[1])+1;


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

        
        //rs1 = st1.executeQuery("select shortname from organisationunit where id = "+selectedOrgUnitID);
        rs1 = st1.executeQuery("select shortname from organisationunit where organisationunitid = "+selectedOrgUnitID);
        if(rs1.next()) { selectedOrgUnitName = rs1.getString(1); }
        
	    //rs2 =  st2.executeQuery("select id,shortname from organisationunit where parent = "+selectedOrgUnitID);
	    rs2 =  st2.executeQuery("select organisationunitid,shortname from organisationunit where parentid = "+selectedOrgUnitID);
	    while(rs2.next())
		 {
		  	Integer tempInt = new Integer(rs2.getInt(1));
		 	childOrgUnitIDs.add(childOrgUnitCount,tempInt);		 	
		 	childOrgUnitNames.add(childOrgUnitCount,rs2.getString(2));
		 	
		 	childOrgUnitCount++;
		 } // while loop end			 	
		 	
		//rs5 = st5.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodType=3) AND datavalue.source in ( select parent from organisationunit where id ="+selectedOrgUnitID+")  AND dataelement.code in ("+DECodes[5]+")");
		rs5 = st5.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodtypeid=3) AND datavalue.sourceid in ( select parentid from organisationunit where organisationunitid ="+selectedOrgUnitID+")  AND dataelement.code in ("+DECodes[5]+")");
		if(rs5.next()) { birthRate = rs5.getInt(1); }
		if(birthRate==0) birthRate=24.3;

		int count = 0;
		while(count < childOrgUnitCount)
		 { 	
			Integer temp1 = (Integer) childOrgUnitIDs.get(count);
			int currentChildID = temp1.intValue(); 			
									
			for(i=0;i<DECodes.length;i++)
			 {
			   	//if(i==0) query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodType=3) AND datavalue.source in (select id from organisationunit where id = "+currentChildID+" OR id  in (select id from organisationunit where parent = "+currentChildID+") OR parent in (select id from organisationunit where parent = "+currentChildID+")) AND dataelement.code in ("+DECodes[i]+")";
			   	//else query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+endingDate+"') AND datavalue.source in (select id from organisationunit where id = "+currentChildID+" OR id  in (select id from organisationunit where parent = "+currentChildID+") OR parent in (select id from organisationunit where parent = "+currentChildID+"))  AND dataelement.code in ("+DECodes[i]+")";	
			   if(i==0) query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodtypeid=3) AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+currentChildID+" OR organisationunitid  in (select organisationunitid from organisationunit where parentid = "+currentChildID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+currentChildID+")) AND dataelement.code in ("+DECodes[i]+")";
			   else query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+endingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+currentChildID+" OR organisationunitid  in (select organisationunitid from organisationunit where parentid = "+currentChildID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+currentChildID+"))  AND dataelement.code in ("+DECodes[i]+")";
			   rs3 = st3.executeQuery(query);
			   
			   if(rs3.next())  
			    	{
						try
						 {
			   				if(i==0) 
			   				   { 
			   				      		double r = ((rs3.getInt(1)  * birthRate)/1000 ) ;	
										r = (r / 12) * noOfMonths;
										int decimalPlace = 2;
										BigDecimal bd = new BigDecimal(r);
										bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
										r = bd.doubleValue();		
			   				      		
			   				      		dataValues[count][i] = r;		   				      								  			   				      
			   				    }  
			   				else 
			   				  {
			   				    		double r = ( rs3.getInt(1) / dataValues[count][0]) * 100;
										
										int decimalPlace = 2;
										BigDecimal bd = new BigDecimal(r);
										bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
										r = bd.doubleValue();		
			   				    			   				    
			   				    		dataValues[count][i] = r;	
			   				   } 
			   			 }
			   			catch(Exception e) { dataValues[count][i] = 0.0; } 	
			   			
			   		//	sumOfValues[i] +=  dataValues[count][i];			   			
			   		}	
			   		
			 } // for loop end		 	
			 		 
			count++;			 			 	
		 } // while loop end



		count = 1;
		int flag1 = 0;
		int flag2 = 0;
		
		while(count < DECodes.length)
		 { 	
	        
			minValue[count] = dataValues[0][count];
			maxValue[count] = dataValues[0][count];	
			
			minOrgUnitName[count] = "";
			maxOrgUnitName[count] = "";
									
			for(i=0;i<childOrgUnitCount;i++)
			 {
			 
				String  childOrgUnitName = (String) childOrgUnitNames.get(i);
							 					
			   	if( dataValues[i][count] > maxValue[count] ) { maxValue[count] = dataValues[i][count]; maxOrgUnitName[count] = childOrgUnitName; }
			   	else if( dataValues[i][count] == maxValue[count] ) 
			   	        { maxValue[count] = dataValues[i][count]; 
			   	          if (flag1==0) { maxOrgUnitName[count] = childOrgUnitName;flag1=1;} 
			   	          else { maxOrgUnitName[count] += "<br>"+ childOrgUnitName; }
			   	        }  
			  

			   	if( dataValues[i][count] < minValue[count] ) { minValue[count] = dataValues[i][count]; minOrgUnitName[count] = childOrgUnitName;}
			   	else if( dataValues[i][count] == minValue[count])  
			   	        { minValue[count] = dataValues[i][count]; 
			   	          if (flag2==0) { minOrgUnitName[count] = childOrgUnitName;flag2=1;}
			   	          else { minOrgUnitName[count] += "<br>"+ childOrgUnitName;}
						}

								
			 } // for loop end		 	
			count++;			 			 	
		 } // while loop end


		for(i=0;i<DECodes.length;i++)
			 {
			   	//if(i==0) query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodType=3) AND datavalue.source in (select id from organisationunit where id = "+selectedOrgUnitID+" OR id  in (select id from organisationunit where parent = "+selectedOrgUnitID+") OR parent in (select id from organisationunit where parent = "+selectedOrgUnitID+")) AND dataelement.code in ("+DECodes[i]+")";
			   	//else query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+endingDate+"') AND datavalue.source in (select id from organisationunit where id = "+selectedOrgUnitID+" OR id  in (select id from organisationunit where parent = "+selectedOrgUnitID+") OR parent in (select id from organisationunit where parent = "+selectedOrgUnitID+"))  AND dataelement.code in ("+DECodes[i]+")";	
			    if(i==0) query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodtypeid=3) AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+" OR organisationunitid  in (select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")) AND dataelement.code in ("+DECodes[i]+")";
			    else query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+endingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+" OR organisationunitid  in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+"))  AND dataelement.code in ("+DECodes[i]+")";
			    rs4 = st4.executeQuery(query);
			   
			   if(rs4.next())  
			    	{
						try
						 {
			   				if(i==0) 
			   				   { 				   				      
			   				      	population = rs4.getInt(1);									
			   				      	double r = ((population * birthRate)/1000 ) ;	
									r = (r / 12) * noOfMonths;
									int decimalPlace = 2;
									BigDecimal bd = new BigDecimal(r);
									bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
									r = bd.doubleValue();		

			   				        PHCdataValues[i] = r;			   				      								  			   				      
			   				    }  
			   				else 
			   				  {
			   				    	double r = ( rs4.getInt(1) / PHCdataValues[0]) * 100;	
									int decimalPlace = 2;
									BigDecimal bd = new BigDecimal(r);
									bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
									r = bd.doubleValue();		

			   				    	PHCdataValues[i] = r;	
			   				   } 
			   			 }
			   			catch(Exception e) { PHCdataValues[i] = 0.0; } 	
			   			
			   			PHCsumOfValues[i] +=  PHCdataValues[i];
			   			
			   		}	
			 } // for loop end		 	

        						
	 } // try block end
    catch(Exception e)  { out.println(e.getMessage());  }
	finally
   		 {
			try
			 {
				if(rs1!=null)  rs1.close();		if(st1!=null)  st1.close();
				if(rs2!=null)  rs2.close();		if(st2!=null)  st2.close();
				if(rs3!=null)  rs3.close();		if(st3!=null)  st3.close();
				if(rs4!=null)  rs4.close();		if(st4!=null)  st4.close();
				if(rs5!=null)  rs5.close();		if(st5!=null)  st5.close();


				if(con!=null)  con.close();
			 }
			catch(Exception e)   {  out.println(e.getMessage());   }
   		 } // finally block end		          		

%>


<html>
<head>
	<title>Delivery Care</title>
</head>
<body>
	<br><br>
	<font face="arial" size="4"><b>Delivery Care</b></font>
	<br><br>
	 <table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:0; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><b><font face="Arial" size="2">Name of the Taluk&nbsp;:&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>Starting Period :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfStartDate[1])]%> - <%=partsOfStartDate[0]%> </b></font></td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:0; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><b><font face="Arial" size="2">Population&nbsp;:&nbsp;&nbsp;<%=population%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>Ending Period :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfEndDate[1])]%> - <%=partsOfEndDate[0]%> </b></font></td>   
  			</tr>

		</table>
                               		
		<br>
   		<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
     		<tr>
       			<td width="14%" rowspan="2" align="center" height="50"><font face="arial" size="2"><b>Indicators</b></font></td>
       			<td width="10%" rowspan="2" align="center"><font face="arial" size="2"><b>Expected (In Thousands)</b></font></td>
       			<td width="10%" rowspan="2" align="center"><font face="arial" size="2"><b>Workload Estimated (as % of Expected)</b></font></td>
       			<td width="10%" rowspan="2" align="center"><font face="arial" size="2"><b>% Workload Achieved</b></font></td>
       			<td width="56%" colspan="2" align="center"><font face="arial" size="2"><b>PHC Performance Range</b></font></td>
     		</tr>
     		<tr>
       			<td width="28%" align="center"><font face="arial" size="2"><b>Maximum</b></font></td>
       			<td width="28%" align="center"><font face="arial" size="2"><b>Minimum</b></font></td>
     		</tr>
     		<tr>
       			<td width="14%" height="50"><font face="arial" size="2"><b>
                Deliveries</b></font></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[0]%></b></font>&nbsp;</td>
       			<td width="10%" align="center"><font face="arial" size="2"><b>100.0</b></font></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[1]%></b></font>&nbsp;</td>
       			<td width="28%" align="center"><font face="arial" size="2"><b><%=maxOrgUnitName[1]%><br>(<%=maxValue[1]%>)</b></font>&nbsp;</td>
       			<td width="28%" align="center"><font face="arial" size="2"><b><%=minOrgUnitName[1]%><br>(<%=minValue[1]%>)</b></font>&nbsp;</td>
     		</tr>
     		<tr>
       			<td width="14%" height="50"><b><font face="arial" size="2">
                Institutional Deliveries</font></b></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[0]%></b></font>&nbsp;</td>
       			<td width="10%" align="center"><font face="arial" size="2"><b>100.0</b></font></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[2]%></b></font>&nbsp;</td>
       			<td width="28%" align="center"><font face="arial" size="2"><b><%=maxOrgUnitName[2]%><br>(<%=maxValue[2]%>)</b></font>&nbsp;</td>
       			<td width="28%" align="center"><font face="arial" size="2"><b><%=minOrgUnitName[2]%><br>(<%=minValue[2]%>)</b></font>&nbsp;</td>
     		</tr>
     		<tr>
       			<td width="20%" height="50"><b><font face="arial" size="2">
                Deliveries by Health Professional</font></b></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[0]%></b></font>&nbsp;</td>
       			<td width="10%" align="center"><font face="arial" size="2"><b>100.0</b></font></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[3]%></b></font>&nbsp;</td>
       			<td width="25%" align="center"><font face="arial" size="2"><b><%=maxOrgUnitName[3]%><br>(<%=maxValue[3]%>)</b></font>&nbsp;</td>
       			<td width="25%" align="center"><font face="arial" size="2"><b><%=minOrgUnitName[3]%><br>(<%=minValue[3]%>)</b></font>&nbsp;</td>
     		</tr>
     		<tr>
       			<td width="20%" height="50"><b><font face="arial" size="2">3 
                Postnatal Visits</font></b></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[0]%></b></font>&nbsp;</td>
       			<td width="10%" align="center"><font face="arial" size="2"><b>100.0</b></font></td>
       			<td width="10%" align="center"><font face="arial" size="2"><b><%=PHCdataValues[4]%></b></font>&nbsp;</td>
       			<td width="25%" align="center"><font face="arial" size="2"><b><%=maxOrgUnitName[4]%><br>(<%=maxValue[4]%>)</b></font>&nbsp;</td>
       			<td width="25%" align="center"><font face="arial" size="2"><b><%=minOrgUnitName[4]%><br>(<%=minValue[4]%>)</b></font>&nbsp;</td>
     		</tr>
   		</table>   
   		
   		
   		<br><br>
   		<font face="arial" size="4"><b>Formulae</b></font>
   		<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
     		<tr>
       			<td width="20%" align="center" height="50"><font face="arial" size="2"><b>Indicators</b></font></td>
       			<td width="30%" align="center"><font face="arial" size="2"><b>Expected (In Thousands)</b></font></td>
       			<td width="50%" align="center"><font face="arial" size="2"><b>% Workload Achieved</b></font></td>
     		</tr>
     		<tr>
       			<td width="20%" height="50"><b><font face="arial" size="2">
                Deliveries</font></b></td>
       			<td width="30%" align="center"><font face="arial" size="2"><b>  (Population * Birth Rate) / 1000   </b></font>&nbsp;</td>
       			<td width="50%" align="center"><font face="arial" size="2"><b>(DataElement For Deliveries / Expected) * 100.0</b></font>&nbsp;</td>
     		</tr>
     		<tr>
       			<td width="20%"  height="50"><b><font face="arial" size="2">
                Institutional Deliveries</font></b></td>
       			<td width="30%" align="center"><font face="arial" size="2"><b>  (Population * Birth Rate) / 1000  </b></font>&nbsp;</td>
       			<td width="50%" align="center"><font face="arial" size="2"><b>(DataElement For Institutional Deliveries / Expected) * 100.0</b></font>&nbsp;</td>
     		</tr>
     		<tr>
       			<td width="20%"  height="50"><b><font face="arial" size="2">
                Deliveries by Health Professional</font></b></td>
       			<td width="30%" align="center"><font face="arial" size="2"><b>  (Population * Birth Rate) / 1000  </b></font>&nbsp;</td>
       			<td width="50%" align="center"><font face="arial" size="2"><b>(DataElement For Deliveries by Health Professional / Expected) * 100.0</b></font>&nbsp;</td>
     		</tr>
     		<tr>
       			<td width="20%"  height="50"><b><font face="arial" size="2">3 
                Postnatal Visits</font></b></td>
       			<td width="30%" align="center"><font face="arial" size="2"><b>   (Population * Birth Rate) / 1000   </b></font>&nbsp;</td>
       			<td width="50%" align="center"><font face="arial" size="2"><b>(DataElement For 3 PostNatal Visits / Expected) * 100.0</b></font>&nbsp;</td>
     		</tr>
   		</table>   

		<br>
   		<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
     		<tr>
       			<td width="20%" height="50" ><font face="arial" size="2"><b>Numerator For Deliveries : ( Home Deliveries by Doctor + ANM/Nurses/Health Worker + Trained Birth Attendant + Untrained Dai) <br>
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+<br>
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
       			( Deliveries at Public Institutions + Sub-Centre + Facilities covered under Public Private Partnership + Other Private/NGO and Charitable Hospitals)</b></font></td>
     		</tr>
     		<tr>
       			<td width="20%"  height="50"><font face="arial" size="2"><b>Numerator for Institutional Deliveries : Deliveries at Public Institutions + Sub-Centre + Facilities covered under Public Private Partnership + Other Private/NGO and Charitable Hospitals</b></font></td>
     		</tr>
     		<tr>
       			<td width="20%"  height="50"><font face="arial" size="2"><b>Numerator for Deliveries by Health Professional : ( Home Deliveries by Doctor + ANM/Nurses/Health Worker + Trained Birth Attendant ) <br>
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+<br>
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
       			( Deliveries at Public Institutions + Sub-Centre + Facilities covered under Public Private Partnership + Other Private/NGO and Charitable Hospitals)</b></font></td>
     		</tr>
     		<tr>
       			<td width="20%"  height="50"><font face="arial" size="2"><b>Numerator for 3 PostNatal Visits : Third PostNatal Visit between 11 to 14 days Total</b></font></td>
     		</tr>
   		</table>   


	</body>
</html>