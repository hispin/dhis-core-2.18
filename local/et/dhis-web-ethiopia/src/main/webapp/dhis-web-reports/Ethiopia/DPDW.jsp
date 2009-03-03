
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%@ page session="true" %>

<%

   Connection con=null;            
    Statement st1=null;
    ResultSet rs1=null;      
  
    String userName = "root";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/dhis2";  
    
	int orgUnitId = 16;
	String orgUnitName="";	
	int periodId = 204;
	String startDate="";
	String endDate="";
OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
  
	
	
    int[] fC= new int[100];
	int[] fD= new int[100];
	int[] sC= new int[100];
	int[] sD= new int[100];
	int[] tC= new int[100];
	int[] tD= new int[100];
	
	String no_value="";
	String[] dis= {"Cholera","Plague","Yellow Fever","M.Meningitis","Measles","A.L.P(Polio)","NNT","W.Cough"};

	int i=0;
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
  		String sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2481,2487,2493,2499,2506,2512,2518,2524)";		
        rs1 = st1.executeQuery(sql); 
         if (rs1.next())
        {
            for(i=0;i<8;i++)
            {
            	fC[i]=rs1.getInt("value");				
				System.out.print("have value");
				if(!rs1.next()) break;
          
            }
         }
   	  			
          
        
		sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2482,2488,2494,2500,2507,2513,2519,2525)";		
        rs1 = st1.executeQuery(sql); 
         if (rs1.next())
        {
            for(i=0;i<8;i++)
            {
            	fD[i]=rs1.getInt("value");				
				System.out.print("have value");
				if(!rs1.next()) break;
            
            }
         }
 
        
         //geting orguntname
		sql = "select name from organizationunit where id ="+orgUnitId ;
	    rs1 = st1.executeQuery(sql);  
         if (rs1.next())
        {
           orgUnitName=rs1.getString("name");				
	    }
	    sql = "select startDate,endDate from period where id ="+periodId ;
	    rs1 = st1.executeQuery(sql);  
         if (rs1.next())
        {
           startDate=rs1.getString("startDate");				
           endDate=rs1.getString("endDate");				

	    }
	    //end getting 

		    
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2483,1154,2494,2500,2505,2511,2517,2523)";		
        rs1 = st1.executeQuery(sql);    	  			
         
	 if (rs1.next())
        {
            for(i=0;i<8;i++)
            {
            	sC[i]=rs1.getInt("value");				
				System.out.print("have value");
				if(!rs1.next()) break;
          
            }
         }
 
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2483,2489,2495,2502,2508,2514,2520,2526)";		
        rs1 = st1.executeQuery(sql);    	  			
          
 	if (rs1.next())
        {
            for(i=0;i<8;i++)
            {
            	sD[i]=rs1.getInt("value");				
				System.out.print("have value");
				if(!rs1.next()) break;
            
            }
         }
  
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2484,2490,2496,2503,2509,2515,2521,2527)";		
        rs1 = st1.executeQuery(sql);    	  			
       
	 if (rs1.next())
        {
            for(i=0;i<8;i++)
            {
            	tC[i]=rs1.getInt("value");				
				System.out.print("have value");
				if(!rs1.next()) break;
           
            }
         }
 
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2485,2491,2497,2504,2510,2516,2522,2528)";		
        rs1 = st1.executeQuery(sql);    	  			
         
 	if (rs1.next())
        {
            for(i=0;i<8;i++)
            {
            	tD[i]=rs1.getInt("value");				
				System.out.print("have value");
				if(!rs1.next()) break;
            
            }
         }
  
		   
       } 
      catch(Exception e)  { out.println("Loi cho ma gi day: "+e.getMessage());  }
      
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="styles.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
.style10 {font-family: Geneva, Arial, Helvetica, sans-serif}
-->
</style>
</head>

<body>
<table width="744" border="0" cellpadding="1" cellspacing="1">
  <!--DWLayoutTable-->
  <tr> 
    <td colspan="2" rowspan="2" valign="top"><table width="90%" border="0" cellpadding="1" cellspacing="1">
        <!--DWLayoutTable-->
        <tr> 
          <td width="555" height="54" valign="top">
          <table width="590" border="0" cellpadding="1" cellspacing="0">
              <!--DWLayoutTable-->
              <tr class="header"> 
                <td width="588" height="54" valign="top">Weekly Reportable 
                Surveillance Communicable Diseases </td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="45" valign="top">
          <table width="592" border="0" cellpadding="1" cellspacing="1" bordercolor="#660000" class="header1" >
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC" class="header1"> 
                <td width="123" height="47" valign="top" class="header1" bgcolor="#008080"><div align="left">Region
                    
                </div>                </td>
                <td width="119" class="header1" bgcolor="#008080"></td>
                <td width="122" valign="top" class="header1" bgcolor="#008080" >
                Health Institution</td>
                <td width="215" bgcolor="#008080"><%=orgUnitName%>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="55" valign="top">
          <table width="591" border="0" cellpadding="1" cellspacing="1" class="header1">
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC"> 
                <td width="108" height="55" valign="top" class="header1" bgcolor="#008080"><div align="left">Period</div></td>
                <td width="106" valign="top" class="header1" bgcolor="#008080">From</td>
                <td width="94" bgcolor="#008080"><%=startDate%>&nbsp;</td>
                <td width="101" valign="top" class="header1" bgcolor="#008080">To</td>
                <td width="166" valign="top" class="header1" bgcolor="#008080"><!--DWLayoutEmptyCell-->
                <%=endDate%>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="515" valign="top">
          <table width="89%" border="0" cellpadding="0" cellspacing="0" height="227">
              <!--DWLayoutTable-->
              <tr> 
                <td width="555" height="144" valign="top">
                <table width="591" height="61" border="0" cellpadding="1" cellspacing="1" bgcolor="#000066">
                    <!--DWLayoutTable-->
                    <tr>
                      <td width="32" height="100" valign="top" class="tableheader" rowspan="2">
                      Disease<p>&nbsp;</p>
                      <p>&nbsp;</td>
                      <td height="50" valign="top" class="tableheader" width="33" colspan="2">
                      0-4 Year</td>
                      <td height="50" valign="top" class="tableheader" width="150" colspan="2">
                      5-14 Year<p>&nbsp;</td>

                      <td valign="top" class="tableheader" width="90" height="50" colspan="2">
                      15+ Year</td> 
                      <td valign="top" class="tableheader" width="123" height="50" colspan="2">
                      Total</td>
                      <tr>
                      <td height="50" valign="top" class="tableheader" width="1">
                      Case </td>
                      <td height="50" valign="top" class="tableheader" width="32">
                      Death </td>
                      <td height="50" valign="top" class="tableheader" width="32">
                      Case </td>

                      <td height="50" valign="top" class="tableheader" width="79">
                      Death</td>

                      <td valign="top" class="tableheader" width="54" height="50">
                      Case </td> 

                      <td valign="top" class="tableheader" width="36" height="50">
                      Death</td> 
                      <td valign="top" class="tableheader" width="59" height="50">
                      Case </td>
                      <td valign="top" class="tableheader" width="64" height="50">
                      Death</td>
                   <% for (int k=0;k<8;k++){%>
                     
                      <tr bordercolor="#000000" class="tableitem">
                      <td width="2" height="32" valign="top" class="tableitem"><%=dis[k]%>&nbsp;</td>
                      
                      <td width="33" valign="top" class="tableitem"><%=fC[k]%>&nbsp;</td>
                     
                      <td width="55" valign="top" class="tableitem"><%=fD[k]%>&nbsp;</td>

                      <td width="62" valign="top" class="tableitem"><%=sC[k]%>&nbsp;</td> 
                      <td width="22" valign="top" class="tableitem"><%=sD[k]%>&nbsp;</td>
                
                      <td width="20" valign="top" class="tableitem"><%=tC[k]%>&nbsp;</td>
                      <td width="7" valign="top" class="tableitem"><%=tD[k]%>&nbsp;</td>
                      <td width="35" valign="top" class="tableitem"><%=fC[k]+sC[k]+tC[k]%>&nbsp;</td>
                      <td width="40" valign="top" class="tableitem"><%=fD[k]+sD[k]+tD[k]%>&nbsp;</td>
                      
                    </tr>

					<%}%>

                  </table>
              </tr>
              <tr> 
                <td height="83" valign="top"><i>This is format of the weekly 
                reportable disease report .If any of the above cases is seen in 
                any OPD of any Health Facility or in the community ,a case based 
                investigation hat to be done for further analysis.</i></td>
              </tr>
              <tr> 
                <td height="83" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#000066">
                    <!--DWLayoutTable-->
					
                  </table></td>
              </tr>
              </table></td>
        </tr>
      </table></td
    <td></td>
  </tr>
  <tr> 
    <td></td>
  </tr>
  <tr> 
    <td width="452"></td>
    <td></td>
    <td></td>
  </tr>
</table>
</body>
</html>