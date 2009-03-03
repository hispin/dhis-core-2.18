
<%@ page import="java.sql.*,java.util.*,java.math.*" %>
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

    // For finding child orgunit ids and names based on selected orgunit id
    Statement st3=null;
    ResultSet rs3=null;

   // For finding child orgunit datavalues
    Statement st4=null;
    ResultSet rs4=null;
    
    // For finding phcs based on taluka ids
    Statement st5=null;
    ResultSet rs5=null;
    
   // For finding BirthRate
    Statement st6=null;
    ResultSet rs6=null;

  
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
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String DECodes[] = {
										
						// Population
    					"'SD_DE0007'",

   						// Live Births
   						"'Form6_DE0081','Form6_DE0082'",    					

						//ANC Reg.
						"'Form6_DE0009'",
						
						//Early Reg.
						"'Form6_DE0012'",
						
						//ANC 3 Checkup
						"'Form6_DE0021'",
						
						//Del. Reg
						"'Form6_DE0057','Form6_DE0060','Form6_DE0063','Form6_DE0066','Form6_DE0069','Form6_DE0072','Form6_DE0075','Form6_DE0078'",
						
						//Inst. Del
						"'Form6_DE0072','Form7_DE0001','Form8_DE0001','Form8_DE0004','Form8_DE0010','Form8_DE0013','Form8_DE0016'",
						
						//PNC3 Checkup
						"'Form6_DE0207','Form6_DE0207'",
						
						//TT (Mo)
						"'Form6_DE0042'",
						
						//BCG
						"'Form6_DE0277','Form6_DE0278'",
						
						//DPT/Polio
						"'Form6_DE0283','Form6_DE0284'",
						
						//Measles
						"'Form6_DE0361','Form6_DE0362'",
						
						//Fully Imm
						"'Form6_DE0367','Form6_DE0368'",
						
						//Sterilisation
						"'Form6_DE0564','Form6_DE0570'",
						
						//IUD
						"'Form6_DE0579'",
						
    					
    					// Birth Rate
   						"'SD_DE0008'"
					
						};
	
	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
  	int totPopulation = 0;
  	  	

  	double PHCdataValues[] = new double[DECodes.length];
  	double maxMarks[] = {
  							10.0,8.0,7.0,8.0,10.0,7.0,5.0,5.0,5.0,5.0,5.0,13.0,12.0
  						}; 
  	

  	String query = "";
  	
  	double birthRate = 24.3;
  	int population = 0;
//  	double r= 0.0;


	String startingYear = "";
	String endingYear = "";
	
	String partsOfStartDate[] = startingDate.split("-");
	String partsOfEndDate[] = endingDate.split("-");

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
		st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);


        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";       }  
                
//        rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		selectedDataPeriodStartDate = startingDate;

				
	    //rs3 =  st3.executeQuery("select id,shortname from organisationunit where parent = "+selectedOrgUnitID);
	    rs3 =  st3.executeQuery("select organisationunitid,shortname from organisationunit where parentid = "+selectedOrgUnitID);
	    while(rs3.next())
		 {
		  	Integer tempInt = new Integer(rs3.getInt(1));
		 	childOrgUnitIDs.add(childOrgUnitCount,tempInt);		 	
		 	childOrgUnitNames.add(childOrgUnitCount,rs3.getString(2));		 	
		 	childOrgUnitCount++;
		 } 		 	  	 

		//rs6 = st6.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodType=3) AND datavalue.source ="+selectedOrgUnitID+"  AND dataelement.code in ("+DECodes[15]+")");
		rs6 = st6.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodtypeid=3) AND datavalue.sourceid ="+selectedOrgUnitID+"  AND dataelement.code in ("+DECodes[15]+")");
		if(rs6.next()) { birthRate = rs6.getInt(1); }
		if(birthRate==0) birthRate=24.3;

	 } // try block end
    catch(Exception e)  { out.println(e.getMessage());  }

    String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");	
    int lyear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;			     
	
	int gradesTalukWise[][] = new int[childOrgUnitCount+5][6];
	int grades[] = new int[6];	
%>


<HTML>
	<HEAD>
		<TITLE>Grading Points and Grade received by Reporting Units</TITLE>
	</HEAD>
	<BODY>					
										
		<%
			
			try
			  {
				  count = 0;
				  int count1 = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%16==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<tr>
  							<TD align="center" width="100%" colspan="18"><FONT face="Arial" size="3"><b>
                            Grading Points and Grade received by Reporting Units - <%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%>
                            <BR><div align=right><%=selectedOrgUnitName%></div></b></font></TD>
   						</tr>
  						<TR>
  							<TD align="center" width="3%" rowSpan=2><B><FONT face="Arial" size="2">#</FONT></B></TD>
   							<TD align="center" width="30%" rowSpan=2><b><font face="Arial" size="2">Taluka / Block</font></b></TD>
   							<TD align="center" width="32%" rowSpan=2><b><font face="Arial" size="2">Name of PHC/ CHC/ PPU/ FRU</font></b></TD>
   							<TD align="center" width="18%" colspan="6"><b><font face="Arial" size="2">MCH</font></b></TD>
   							<TD align="center" width="15%" colspan="5"><b><font face="Arial" size="2">Immunisation</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">FP</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" size="2">Total</font></b></TD>
   							<TD align="center" width="3%" rowspan="2"><b><font face="Arial" size="2">Grade</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">ANC Reg.</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">Early Reg</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">ANC 3 Check up</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">Del. Reg</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">Inst. Del</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">PNC3 Check up</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">TT (Mo)</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">BCG</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">DPT / Polio</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">Measles</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">Fully Imm</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">Sterilisation</font></b></TD>
   							<TD align="center" width="3%"><b><font face="Arial" size="2">IUD</font></b></TD>
    					</TR>
						<TR>
   							<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"></font></TD>
    						<TD width="30%" HEIGHT="30" align="center"><FONT face="Arial" size="2"></font></TD>
    						<TD width="32%" HEIGHT="30" align="center"><FONT face="Arial" size="2">Max Marks</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">10</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">8</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">7</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">8</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">10</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">7</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">5</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">5</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">5</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">5</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">5</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">13</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">12</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">100</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"></font></TD>
    					</TR>
						<TR>
   							<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2">1</font></TD>
    						<TD width="30%" HEIGHT="30" align="center"><FONT face="Arial" size="2">2</font></TD>
    						<TD width="32%" HEIGHT="30" align="center"><FONT face="Arial" size="2">3</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">4</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">5</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">6</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">7</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">8</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">9</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">10</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">11</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">12</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">13</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">14</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">15</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">16</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">17</font></TD>
    						<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2">18</font></TD>
    					</TR>

				     <%}
				     
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 		

					int flag = 0;
					
					
					//rs5 =  st5.executeQuery("select id,shortname from organisationunit where parent = "+currentChildID);
					rs5 =  st5.executeQuery("select organisationunitid,shortname from organisationunit where parentid = "+currentChildID);
	    			while(rs5.next())
		 				{
							int tempID = rs5.getInt(1);
		  					String tempName = rs5.getString(2);
		  					int total = 0;

		 					for(i=0;i<DECodes.length;i++)
					 			{			
			   						//if(i==0) query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodType=3) AND datavalue.source in (select id from organisationunit where parent ="+tempID+" OR id ="+tempID+")  AND dataelement.code in ("+DECodes[i]+")";			   						
			   						//else query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+endingDate+"') AND datavalue.source in (select id from organisationunit where parent ="+tempID+" OR id ="+tempID+")  AND dataelement.code in ("+DECodes[i]+")";	
			   						if(i==0) query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate like '"+startingYear+"' and enddate like '"+endingYear+"' and periodtypeid=3) AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+tempID+" OR organisationunitid ="+tempID+")  AND dataelement.code in ("+DECodes[i]+")";
			   						else query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+endingDate+"') AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+tempID+" OR organisationunitid ="+tempID+")  AND dataelement.code in ("+DECodes[i]+")";

									rs4 = st4.executeQuery(query);			   
			   						if(rs4.next())  
			    					{
										try
						 				{
			   								if(i==0) // Target1
			   									{ 	
			   										population = rs4.getInt(1);
			   				      					double r = ((population   * birthRate)/1000 ) * 1.1;
													r = (r / 12) * noOfMonths;	
													int decimalPlace = 2;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		

			   				        				PHCdataValues[i] = r;			   				      
			   									}  
			   								else if(i==1) // Target2
			   									{ 	
			   										
			   				      					double r = ( rs4.getInt(1) * 94 ) / 100;
														
													int decimalPlace = 2;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		

			   				        				PHCdataValues[i] = r;			   				      								  			   				      			   									
			   									}  

			   								else if(i==2) // ANC Reg
			   									{ 
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[0]) * 100;	
													r = r * 0.10;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}
			   								else if(i==3) // Early Reg
			   									{	
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[0]) * 100;	
													r = r * 0.08;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==4) // ANC 3 Chekup
			   									{	
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[0]) * 100;	
													r = r * 0.07;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==5) // Delivery
			   									{	
							   				    	double r = ( rs4.getInt(1) / (PHCdataValues[0]/1.1)) * 100;	
													r = r * 0.08;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==6) // Institutional Delivery
			   									{	
							   				    	double r = ( rs4.getInt(1) / (PHCdataValues[0]/1.1)) * 100;	
													r = (r * 10) / (57.3 + 10);
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==7) // PNC 3 Check up
			   									{	
							   				    	double r = ( rs4.getInt(1) / (PHCdataValues[0]/1.1)) * 100;	
													r = r * 0.07;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==8) // TT-Mother
			   									{	
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[1]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==9) // BCG
			   									{	
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[1]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==10) // DPT
			   									{	
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[1]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==11) // Measles
			   									{	
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[1]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==12) //Fully Immunization
			   									{	
							   				    	double r = ( rs4.getInt(1) / PHCdataValues[1]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	
			   									}   
			   								else if(i==13) // Sterilisation
			   									{	
			   									double r = ( rs4.getInt(1) / PHCdataValues[0]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	

			   									}   
			   								else if(i==14) //IUD Insertion
			   									{	
			   									double r = ( rs4.getInt(1) / PHCdataValues[0]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	

			   									}   			   								
			   								else 
			   									{	
			   									double r = ( rs4.getInt(1) / PHCdataValues[1]) * 100;	
													r = r * 0.05;
													int decimalPlace = 1;
													BigDecimal bd = new BigDecimal(r);
													bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);
													r = bd.doubleValue();		
			   				    					
			   				    					PHCdataValues[i] = r;	

			   									} 
			   				

											if(PHCdataValues[i] > maxMarks[i])  PHCdataValues[i] = maxMarks[i];
			   								
			   								if(i>=2) total += PHCdataValues[i];
			   								
			   								
			   			 				}
			   						  catch(Exception e) { PHCdataValues[i] = 0.0; } 				   						   			
			   						} // if block end										
									
					 			} // for loop end 	
					 			
					 			String curGrade = "";
					 			if(total >= 90) { curGrade="A"; grades[0]++; gradesTalukWise[count][0]++;}
			   					else if(total >= 80 && total < 90) { curGrade="B"; grades[1]++; gradesTalukWise[count][1]++;}
			   					else if(total >= 70 && total < 80) { curGrade="C"; grades[2]++; gradesTalukWise[count][2]++;}
			   					else if(total < 70) { curGrade="D"; grades[3]++; gradesTalukWise[count][3]++;}
			   								
			   					gradesTalukWise[count][4]++;
					 			
					 			count1++;
								
								String tcount = "";
								String tname = "";
					 			if(flag==0) { flag=1; tcount = ""+(count+1); tname = ""+(String) childOrgUnitNames.get(count);}
					 			 
					 			%>			     				  		             		
  							<TR>
    							<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=count1%></font></TD>
    							<TD width="30%" HEIGHT="30" ><FONT face="Arial" size="2"><%=tname%></font></TD>
    							<TD width="32%" HEIGHT="30" ><FONT face="Arial" size="2"><%=tempName%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[2]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[3]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[4]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[5]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[6]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[7]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[8]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[9]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[10]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[11]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[12]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[13]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=PHCdataValues[14]%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=total%></font></TD>
    							<TD width="3%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=curGrade%></font></TD>
    						</TR>
					 			
					 		<%						 								 			
			   			} // while loop end	
			   			
			   			%>
						
									   
				 
    				<% 					
    					count++;
  				   }	// while loop end
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
  					if(rs6!=null)  rs6.close();		if(st6!=null)  st6.close();				
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>
            
		</TABLE>
		
		<br><br>
		
 	    <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="50%" id="AutoNumber1" align="right">
          <tr>
            <td width="10%" align="center"><FONT FACE="ARIAL" SIZE="2">Grade</FONT></td>
            <td width="45%"><FONT FACE="ARIAL" SIZE="2">IF Marks Obtained</FONT></td>
            <td width="45%" align="center"><FONT FACE="ARIAL" SIZE="2"># of PHCS / CHC / PPU / FRU</FONT></td>
          </tr>
          <tr>
            <td width="10%" align="center"><FONT FACE="ARIAL" SIZE="2">A</FONT></td>
            <td width="45%"><FONT FACE="ARIAL" SIZE="2">More than 90</FONT></td>
            <td width="45%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=grades[0]%></FONT>&nbsp;</td>
          </tr>
          <tr>
            <td width="10%" align="center"><FONT FACE="ARIAL" SIZE="2">B</FONT></td>
            <td width="45%"><FONT FACE="ARIAL" SIZE="2">Between 80 to 90</FONT></td>
            <td width="45%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=grades[1]%></FONT>&nbsp;</td>
          </tr>
          <tr>
            <td width="10%" align="center"><FONT FACE="ARIAL" SIZE="2">C</FONT></td>
            <td width="45%"><FONT FACE="ARIAL" SIZE="2">Between 70 to 80</FONT></td>
            <td width="45%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=grades[2]%></FONT>&nbsp;</td>
          </tr>
          <tr>
            <td width="10%" align="center"><FONT FACE="ARIAL" SIZE="2">D</FONT></td>
            <td width="45%"><FONT FACE="ARIAL" SIZE="2">Less than 70</FONT></td>
            <td width="45%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=grades[3]%></FONT>&nbsp;</td>
          </tr>
        </table>
		
		<br><br><br><br><br><br><br>
 	    <table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="50%" id="AutoNumber1" align="right">
          <tr>
            <td width="10%" align="center"><font face="ARIAL" size="2">#</font></td>
            <td width="45%"><FONT FACE="ARIAL" SIZE="2">&nbsp;Taluka Name</FONT></td>
            <td width="9%" align="center"><font face="ARIAL" size="2">A</font></td>
            <td width="9%" align="center"><font face="ARIAL" size="2">B</font></td>
            <td width="9%" align="center"><font face="ARIAL" size="2">C</font></td>
            <td width="9%" align="center"><font face="ARIAL" size="2">D</font></td>
            <td width="9%" align="center"><font face="ARIAL" size="2">Total</font></td>
          </tr>
<%

	  count = 0;
	  while(count < childOrgUnitCount)
	   { 				   
			String tname = ""+(String) childOrgUnitNames.get(count);
			
%>	
          <tr>
            <td width="10%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=count+1%></FONT>&nbsp;</td>
            <td width="45%"><FONT FACE="ARIAL" SIZE="2"><%=tname%></FONT>&nbsp;</td>
            <td width="9%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=gradesTalukWise[count][0]%></FONT>&nbsp;</td>
            <td width="9%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=gradesTalukWise[count][1]%></FONT>&nbsp;</td>
            <td width="9%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=gradesTalukWise[count][2]%></FONT>&nbsp;</td>
            <td width="9%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=gradesTalukWise[count][3]%></FONT>&nbsp;</td>
            <td width="9%" align="center"><FONT FACE="ARIAL" SIZE="2"><%=gradesTalukWise[count][4]%></FONT>&nbsp;</td>
          </tr>

<%          
		count++;
		}
%>		
        </table>
		
 	</BODY>
</HTML>