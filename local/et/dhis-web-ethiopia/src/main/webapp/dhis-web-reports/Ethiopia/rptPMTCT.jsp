<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>


<%
    Connection con=null;
    Statement st1=null;
	Statement st2=null;
    ResultSet rs1=null; 
	ResultSet rs2=null;
	    
  //initial value
  int orgUnitId = 253;
  int periodId = 203;
  int parentId = 0;
  String parentName = null;
    OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
  
    String userName = "root";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/dhis2";  
    
    Integer g=0;
    
    String[][] orgUnit= new String[100][2];
	//int [] element = new [12];
	int total=0;
	int [] element = new int [12];
	String orgUnitName="";
	String startDate="";
	String endDate="";
	boolean nextorg=true;
	
	String [] pmtct= {"ANC First Visit","Pre-test Counseled","Tested","Post-test Counseled and Results Received","Positive Cases","Pregnanat Women Received NVP","Babies Received NVP","HIV-Positive Pregnant Women Counseled on Infant Feeding","HIV-Positive Pregnant Women Referred or Provided FP for Post-partum Contraception","HIV-Positive Pregnant Women Referred or Provided Long-term Care and Support","HIV-Positive Pregnant Women Completed Full Course of ARV Prophylaxis","Total"};
	
	int i=0;
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
        st2=con.createStatement();

        /*rs1 = st1.executeQuery("select id,name from organizationunit where parent=" + orgUnitId); 
		String condition = "";
	    while(rs1.next())
          {         
	  			orgUnit[i][0]=rs1.getString("id");
	  			orgUnit[i][1]=rs1.getString("name");
				condition+= rs1.getInt("id")+",";
	  			i++;			
           } 
		condition+= condition+("-1");*/
		
				
        rs1 = st1.executeQuery("select name,parent from organizationunit where id=" + orgUnitId);
		if (rs1.next()){
			orgUnitName= rs1.getString("name");
			parentId= rs1.getInt("parent");
		}
		
		rs1 = st1.executeQuery("select name from organizationunit where id="+parentId);
		if (rs1.next())
		{
			parentName= rs1.getString("name");
		}
		
		rs1 = st1.executeQuery("select startDate,endDate from period where id=" + periodId);
		if (rs1.next()){
			startDate= rs1.getString("startDate");
			endDate= rs1.getString("endDate");
		}
		
		String sql="select value,source from datavalue where dataElement=2803 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        int j=0;
		
		if (rs1.next())
		{
			element[0]=rs1.getInt("value");
		} 	   
	
	
		sql="select value,source from datavalue where dataElement=2804 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[1]=rs1.getInt("value");
		} 	   
	 	   
	
	
		sql="select value,source from datavalue where dataElement=2805 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[2]=rs1.getInt("value");
		} 	   
	
		sql="select value,source from datavalue where dataElement=2806 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[3]=rs1.getInt("value");
		} 	   
	
		sql="select value,source from datavalue where dataElement=2807 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[4]=rs1.getInt("value");
		} 	   
	 	   
	
		sql="select value,source from datavalue where dataElement=2808 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[5]=rs1.getInt("value");
		} 	   
	 	   
	
		sql="select value,source from datavalue where dataElement=2809 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[6]=rs1.getInt("value");
		} 	   
	 	   
	
		sql="select value,source from datavalue where dataElement=2810 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[7]=rs1.getInt("value");
		} 	   
	 	   
	
		sql="select value,source from datavalue where dataElement=2811 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[8]=rs1.getInt("value");
		} 	   
	 	   
	
		sql="select value,source from datavalue where dataElement=2812 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[9]=rs1.getInt("value");
		} 	   
	 	   
	
		sql="select value,source from datavalue where dataElement=2813 and period=" +periodId+" and source="+orgUnitId;
		rs1 = st1.executeQuery(sql);
        j=0;
		
		if (rs1.next())
		{
			element[10]=rs1.getInt("value");
		} 	   
	 	   
		
		for (i=0;i<12;i++)
		{
			total+=element[i];
		}		
		  
		 }//try
		 catch(Exception e)  { out.println("Loi cho ma gi day: "+e.getMessage());  }
		    	        
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>DHIS 2 - TT Vaccination Report  by Health Facility</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../../../../dhis-web-reports/src/main/webapp/dhis-web-reports/styles.css" rel="stylesheet" type="text/css">
<link href="styles.css" rel="stylesheet" type="text/css">
<link href="styles.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#99FFFF">
<table width="1000" border="0" cellpadding="0" cellspacing="0">
  <!--DWLayoutTable-->
  <tr> 
    <td colspan="2" rowspan="2" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1">
        <!--DWLayoutTable-->
        <tr> 
          <td width="869" height="95" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
            <!--DWLayoutTable-->
            <tr>
                <td width="869" height="93" valign="top" class="header">Prevention 
                  of Mother-to-Child Transmission (PMTCT)<%=orgUnitName%></td>
            </tr>
          </table>          </td>
        </tr>
        <tr> 
          <td height="49" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1" bordercolor="#000066" bgcolor="#008080" class="header1" >
              <!--DWLayoutTable-->
              <tr class="header1"> 
                <td width="139" height="45" valign="top" class="header1">Region</td>
                <td width="127"><%=parentName%></td>
                <td width="121" valign="top" class="header1" >Wereda</td>
                <td width="200"><%=orgUnitName%></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="59" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1" bordercolor="#000066" bgcolor="#008080" class="header1">
              <!--DWLayoutTable-->
              <tr> 
                <td width="85" height="55" valign="top" class="header1">Period</td>
                <td width="91" valign="top" class="header1">From</td>
                <td width="151"><%=startDate%></td>
                <td width="78" valign="top" class="header1">To</td>
                <td width="179"><%=endDate%></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="84" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1" bordercolor="#000066">
              <!--DWLayoutTable-->
              <tr> 
                <td width="62%" height="41" valign="top" class="tableheader">HIV Test for 
                  Pregnant Women</td>
                <td colspan="11" valign="top" class="tableheader">Number</td>
              </tr>
              <% for (int k=0;k<12;k++)  { %>
              <tr> 
                <% if (k==11) {%><td class="tableheader"><%=pmtct[k]%></td><%}%>
                <% if (k!=11) {%><td class="lefttableitem"><%=pmtct[k]%></td><%}%>
                <% if (k==11) {%><td class="tableheader"><%=total%></td><%}%>
                <% if (k!=11) {%><td class="tableitem"><%=element[k]%></td><%}%>
              </tr>
              <% } %>
            </table>
          </td>
        </tr>
      </table>
      <span class="tableitemnumber"></span></td>
    <td width="127" height="275"></td>
  </tr>
  <tr> 
    <td height="2"></td>
  </tr>
  <tr> 
    <td width="503" height="2"></td>
    <td width="370"></td>
    <td></td>
  </tr>
</table>
</body>
</html>