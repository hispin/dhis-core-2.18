
<%@ page import="java.sql.*,java.util.*" %>
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

  
   // For finding datavalues 
    Statement st3=null;
    ResultSet rs3=null;

  
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/jh_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = 	Integer.parseInt( selectedId );
	
//	String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	int selectedDataPeriodID = Integer.parseInt( selectedPeriodId );

	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );
      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {  											
  											// Acute Diarrhoeal Diseases - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
  											"CD_DE1","CD_DE2","CD_DE37","CD_DE38","CD_DE73","CD_DE74",
  											
  											// Diphtheria - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
  											"CD_DE3","CD_DE4","CD_DE39","CD_DE40","CD_DE75","CD_DE76",
  											
  											// Acute Poliomyelitis - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
  											"CD_DE5","CD_DE6","CD_DE41","CD_DE42","CD_DE77","CD_DE78",
  											
  											// Tetanus other than Neonatal - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
  											"CD_DE7","CD_DE8","CD_DE43","CD_DE44","CD_DE79","CD_DE80",
  											
  											// Neo natal Tetanus - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
  											"CD_DE9","CD_DE10","CD_DE45","CD_DE46","CD_DE81","CD_DE82",
  											
  											// Whooping Cough - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
  											"CD_DE11","CD_DE12","CD_DE47","CD_DE48","CD_DE83","CD_DE84",
  											
  											// Measles - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
  											"CD_DE13","CD_DE14","CD_DE49","CD_DE50","CD_DE85","CD_DE86",
  											
  											// Acute Respiratory Infection - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE15","CD_DE16","CD_DE51","CD_DE52","CD_DE87","CD_DE88",
											
											// Pneumonia - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE17","CD_DE18","CD_DE53","CD_DE54","CD_DE89","CD_DE90",
											
											// Enteric Fever - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE19","CD_DE20","CD_DE55","CD_DE56","CD_DE91","CD_DE92",
											
											// Viral Hepatitis - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE21","CD_DE22","CD_DE57","CD_DE58","CD_DE93","CD_DE94",
											
											// Meningococcaol Meningitis - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE23","CD_DE24","CD_DE59","CD_DE60","CD_DE95","CD_DE96",
											
											// Japanese Encephalitis - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE25","CD_DE26","CD_DE61","CD_DE62","CD_DE97","CD_DE98",
											
											// Robies - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE27","CD_DE28","CD_DE63","CD_DE64","CD_DE99","CD_DE100",
											
											// Syphilis - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE29","CD_DE30","CD_DE65","CD_DE66","CD_DE101","CD_DE102",
											
											// Gonococcal Infection - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE31","CD_DE32","CD_DE67","CD_DE68","CD_DE103","CD_DE104",
											
											// Pulmonary Tuberculosis - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE33","CD_DE34","CD_DE69","CD_DE70","CD_DE105","CD_DE106",
											
											// All other diseases - OPD Male, OPD Female, IPD Male, IPD Female, IPD Deaths Male, IPD Deaths Female
											"CD_DE35","CD_DE36","CD_DE71","CD_DE72","CD_DE107","CD_DE108"
																						
  											
									};
	
	String nonCommDisNames[] = {
									"Acute Diarrhoeal Diseases <br><font face='arial' size='1'>(Including Gastro Entritis and Cholera)</font>",
									"Diphteria",
									"Acute Poliomyelitis",
									"Tetanus other than Neonatal",
									"Neo Natal Tetanus",
									"Whooping Cough",
									"Measels",
									"Acute Respiratory Infection<br><font face='arial' size='1'>(including Influenza and excluding Pneumonia)</font>",
									"Pneumonia",
									"Enteric Fever",
									"Viral Hepatits",
									"Meningococcaol Meningitis",
									"Japanese Encephalitis",
									"Robies (Dog Bite)",
									"Syphilis",
									"Gonococcal Infection",
									"Pulmonary Tuberculosis",
									"All other diseases treated in Institution excluding above mentioned diseases"
									
									
							};	
							
	String nonCommDisNo[] = {
								"1.",
								"2.",
								"3.",
								"4.",
								"5.",
								"6.",
								"7.",
								"8.",
								"9.",
								"10.",
								"11.",
								"12.",
								"13.",
								"14.",
								"15.",
								"16.",
								"17.",
								"18."
							};	
							
	
	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
  	int totPopulation = 0;
  	  	
	int tempval[] = new int[16];
	int total[] = new int[16];
%>

<%
     try
      {
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
 

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit where organisationunit.id = "+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";           }  
                
//        rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  					    		 	  	 

	   selectedDataPeriodStartDate = startingDate;		
	  } // try block end
    catch(Exception e)  { out.println(e.getMessage());  }

    String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");			     
%>

<html>
	<head>
		<title>MONTHLY STATEMENT SHOWING INSTITUTIONAL CASES AND DEATHS DUE TO PRINCIPAL COMMUNICABLE DISEASES</title>
	</head>
	<body>		
		<center>
			<font face="arial" size="3"><b>
 				<u>MONTHLY STATEMENT SHOWING INSTITUTIONAL CASES AND DEATHS DUE TO PRINCIPAL COMMUNICABLE DISEASES</u>
 			</b></font>  			
		</center>
		<BR>
		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2">1.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Name of the District : &nbsp;&nbsp;</font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2"><%=selectedOrgUnitName%></font>
    			</td>   
  			</tr>
  			<tr>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2">2.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month</font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2"><%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%></font>
    			</td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2">3.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Year</font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2"><%=partsOfDataPeriodStartDate[0]%></font>
    			</td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2">4.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total No. of existing Institutions in the District</font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">&nbsp;</td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">
       				<font face="Arial" size="2">5.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total No. of Reporting Institutions For the month in the District :</font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">&nbsp;</td>   
  			</tr>
 		<tr>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="25">
       				<font face="Arial" size="2">6.&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Total No. of defaulting Institutions For the month in the District:</font>
    			</td>
    			<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="25">&nbsp;</td>
  		</tr>  		  		
  		</table>  
		<br>
		<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
  			<tr>
    				<td width="3%"  align="center" rowspan="3"><font face="arial" size="2"><b>&#2325;&#2381;&#2352;&#2350; &#2360;&#2306;.</b></font></td>
    				<td width="40%" align="center"  rowspan="3"><b><font face="arial" size="2">Name of Disease</font></b></td>
    				<td width="38%" align="center"  colspan="6"><b><font face="arial" size="2">Patients Treated</font></b></td>
    				<td width="19%" align="center"  colspan="3"><b><font face="arial" size="2">Deaths</font></b></td>
  			</tr>
  			<tr>
    				<td width="12%" align="center"  colspan="2"><b><font face="arial" size="2">OPD</font></b></td>
    				<td width="12%" align="center"  colspan="2"><b><font face="arial" size="2">IPD</font></b></td>
    				<td width="14%" align="center"  colspan="2"><b><font face="arial" size="2">TOTAL</font></b></td>
    				<td width="19%" align="center"  colspan="3"><b><font face="arial" size="2">(IPD Only)</font></b></td>
  			</tr>
  			<tr>
    				<td width="6%" align="center"><b><font face="arial" size="2">M</font></b></td>
    				<td width="6%" align="center"><b><font face="arial" size="2">F</font></b></td>
    				<td width="6%" align="center"><b><font face="arial" size="2">M</font></b></td>
    				<td width="6%" align="center"><b><font face="arial" size="2">F</font></b></td>
    				<td width="7%" align="center"><b><font face="arial" size="2">M</font></b></td>
    				<td width="7%" align="center"><b><font face="arial" size="2">F</font></b></td>
    				<td width="6%" align="center"><b><font face="arial" size="2">M</font></b></td>
    				<td width="6%" align="center"><b><font face="arial" size="2">F</font></b></td>
    				<td width="7%" align="center"><b><font face="arial" size="2">T</font></b></td>
  			</tr>
  			<%
			
				try
			  	{
				  count = 0;
				  int k=0;
				  while(count < 18)
				   { 				   
				   
				   
				     		for(i=0;i<6;i++)
					 	 	{			
								//rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"')  AND datavalue.source in (select id from organisationunit where parent ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[k]+"'");
								rs3 = st3.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"')  AND datavalue.sourceid in (select organisationunitid from organisationunit where parentid ="+selectedOrgUnitID+") AND dataelement.code like '"+monthlyDataElements[k]+"'");
								//SELECT datavalue.value FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period ="+selectedDataPeriodID+" AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code like '"+monthlyDataElements[k]+"'");
																		
								if(!rs3.next())  {  tempval[i] = 0;	 }
								else   {  tempval[i] = rs3.getInt(1);  }
								total[i] += tempval[i];
								k++;
					 	 	} // for loop end	
				     						     						     
				     	%>
				     
				     	<tr>
    						<td width="3%" align="center"><font face="arial" size="2"><%=nonCommDisNo[count]%></font>&nbsp;</td>
   	 						<td width="40%" align="center"><font face="arial" size="2"><%=nonCommDisNames[count]%></font>&nbsp;</td>
    						<td width="6%" align="center"><font face="arial" size="2"><%=tempval[0]%></font>&nbsp;</td>
    						<td width="6%" align="center"><font face="arial" size="2"><%=tempval[1]%></font>&nbsp;</td>
    						<td width="6%" align="center"><font face="arial" size="2"><%=tempval[2]%></font>&nbsp;</td>
    						<td width="6%" align="center"><font face="arial" size="2"><%=tempval[3]%></font>&nbsp;</td>
    						<td width="7%" align="center"><font face="arial" size="2"><%=(tempval[0]+tempval[2])%></font>&nbsp;</td>
    						<td width="7%" align="center"><font face="arial" size="2"><%=(tempval[1]+tempval[3])%></font>&nbsp;</td>
    						<td width="6%" align="center"><font face="arial" size="2"><%=tempval[4]%></font>&nbsp;</td>
    						<td width="6%" align="center"><font face="arial" size="2"><%=tempval[5]%></font>&nbsp;</td>
    						<td width="7%" align="center"><font face="arial" size="2"><%=(tempval[4]+tempval[5])%></font>&nbsp;</td>
  						</tr>				     
				     
				        
				     <%		    				 					
    				 count++;
  				   } // while loop end
			  } // try block end
  			catch(Exception e)  { out.println(e.getMessage());  }
     		finally
       		 {
				try
				 {
  					if(rs1!=null)  rs1.close();		if(st1!=null)  st1.close();
  					if(rs2!=null)  rs2.close();		if(st2!=null)  st2.close();
  					if(rs3!=null)  rs3.close();		if(st3!=null)  st3.close();  					
  					
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>
  		
	     	<tr>
				<td width="43%" align="center" colspan="2"><font face="arial" size="2">Total</font></td>
				<td width="6%" align="center" ><font face="arial" size="2"><%=total[0]%></font>&nbsp;</td>
				<td width="6%" align="center" ><font face="arial" size="2"><%=total[1]%></font>&nbsp;</td>
				<td width="6%" align="center" ><font face="arial" size="2"><%=total[2]%></font>&nbsp;</td>
				<td width="6%" align="center" ><font face="arial" size="2"><%=total[3]%></font>&nbsp;</td>
				<td width="7%" align="center" ><font face="arial" size="2"><%=(total[0]+total[2])%></font>&nbsp;</td>
				<td width="7%" align="center" ><font face="arial" size="2"><%=(total[1]+total[3])%></font>&nbsp;</td>
				<td width="6%" align="center" ><font face="arial" size="2"><%=total[4]%></font>&nbsp;</td>
				<td width="6%" align="center" ><font face="arial" size="2"><%=total[5]%></font>&nbsp;</td>
				<td width="7%" align="center" ><font face="arial" size="2"><%=(total[4]+total[5])%></font>&nbsp;</td>
			</tr>				     				       		
		</table>
	</body>
</html>