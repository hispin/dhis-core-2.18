<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>


<%
    Connection con=null;
    Statement st1=null;
    ResultSet rs1=null; 
	    
  //initial value
  int orgUnitId = 253;
  int periodId = 203;
  String parentName = null;
    OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
  
    String userName = "root";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/dhis2";  
    
    
	int[] outM= new int[10];
	int[] outF= new int[10];
	int[] inM= new int[10];
	int[] inF= new int[10];
	int totalOutM= 0;
	int totalOutF= 0;
	int totalInM= 0;
	int totalInF=0;
	String orgUnitName="";
	String startDate="";
	String endDate="";
	boolean nextde=true;
	
	String [] services= {"X-Ray","Biopsy","Sonography"};
	
	int[] deint1= {1820,1827,1831};
	int[] deint2= {1821,1826,1829};
	int[] deint3= {1822,1825,1830};
	int[] deint4= {1823,1824,1828};
	
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
				
        rs1 = st1.executeQuery("select name from organizationunit where id=" + orgUnitId);
		if (rs1.next()){
			orgUnitName= rs1.getString("name");
		}
		
		rs1 = st1.executeQuery("select name from organizationunit where parent is null");
		if (rs1.next())
		{
			parentName=rs1.getString("name");
		}
		
		rs1 = st1.executeQuery("select startDate,endDate from period where id=" + periodId);
		if (rs1.next()){
			startDate= rs1.getString("startDate");
			endDate= rs1.getString("endDate");
		}
		
		String sql="select value,dataelement from datavalue where source="+orgUnitId+" and period=" +periodId+" and dataelement in (1820,1827,1831)";
		rs1 = st1.executeQuery(sql);
        int j=0;

		if (rs1.next())
		{
		for (j=0;j<3;j++)
          {
		  	rs1.beforeFirst();
			nextde=true;
				while (rs1.next()&& nextde)
				{         
					if (deint1[j]==(rs1.getInt("dataelement")))
					{         
						outM[j]=rs1.getInt("value");
						nextde=false;
					}
					
				}
				
          	}
		  } 	   
	
	
		sql="select value,dataelement from datavalue where source="+orgUnitId+" and period=" +periodId+" and dataelement in (1821,1826,1829)";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<3;j++)
          {
		  	rs1.beforeFirst();
			nextde=true;  
				while (rs1.next() && nextde)
				{       
					if (deint2[j]==(rs1.getInt("dataelement")))
					{         
						outF[j]=rs1.getInt("value");
						nextde=false;
					}
					
				}  
          	}
		  } 	   
	
		sql="select value,dataelement from datavalue where source="+orgUnitId+" and period=" +periodId+" and dataelement in (1822,1825,1830)";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<3;j++)
          {
		  	rs1.beforeFirst();
			nextde=true;
				while (rs1.next() && nextde)
				{         
					if (deint3[j]==(rs1.getInt("dataelement")))
					{         
						inM[j]=rs1.getInt("value");
						nextde=false;
					}
					
				}
          	}
		  } 	   
	
		sql="select value,dataelement from datavalue where source="+orgUnitId+" and period=" +periodId+" and dataelement in (1823,1824,1828)";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<3;j++)
          { 
		  	rs1.beforeFirst();
			nextde=true;
				while (rs1.next() && nextde)
				{        
	  				if (deint4[j]==(rs1.getInt("dataelement")))
					{         
						inF[j]=rs1.getInt("value");
						nextde=false;
					}
					
				}
			}
          } 	   
	
		  
		  for (j=0;j<3;j++)
		  {
		  	totalOutM+=outM[j];
		  	totalOutF+=outF[j];  
		  	totalInM+=inM[j];	   
		  	totalInF+=inF[j];	   
		}	   
		
		  
		 }//try
		 catch(Exception e)  { out.println("Loi cho ma gi day: "+e.getMessage());  }
		    	        
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>X-Ray, Biopsy and Sonography Report for <%=orgUnitName%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../../../../dhis-web-reports/src/main/webapp/dhis-web-reports/styles.css" rel="stylesheet" type="text/css">
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
              <td width="869" height="93" valign="top" class="header">X-Ray, Biopsy and Sonography Report for <%=orgUnitName%></td>
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
                <td width="114" rowspan="2" valign="top" class="tableheader">Services</td>
                <td colspan="3" valign="top" class="tableheader">Outpatient</td>
                <td height="41" colspan="3" valign="top" class="tableheader">Inpatient</td>
                <td height="41" colspan="3" valign="top" class="tableheader">Total</td>
              </tr>
              <tr> 
                <td width="62" valign="top" class="tableheader">Male</td>
                <td width="57" valign="top" class="tableheader">Female</td>
                <td width="59" valign="top" class="tableheader">Total</td>
                <td width="60" valign="top" class="tableheader">Male</td>
                <td width="56" valign="top" class="tableheader">Female</td>
                <td width="59" valign="top" class="tableheader">Total</td>
                <td width="55" valign="top" class="tableheader">Male</td>
                <td width="52" valign="top" class="tableheader">Female</td>
                <td width="64" valign="top" class="tableheader">Grand Total</td>
              </tr>
              <% for (int k=0;k<3;k++)  { %>
              <tr> 
                <td height="21" class="tableitem"><%=services[k]%></td>
                <td class="tableitem"><%=outM[k]%></td>
                <td class="tableitem"><%=outF[k]%></td>
                <td class="tableitem"><%=outM[k]+outF[k]%></td>
                <td class="tableitem"><%=inM[k]%></td>
                <td class="tableitem"><%=inF[k]%></td>
                <td class="tableitem"><%=inM[k]+inF[k]%></td>
                <td class="tableitem"><%=outM[k]+inM[k]%></td>
                <td class="tableitem"><%=outF[k]+inF[k]%></td>
                <td class="tableitem"><%=outM[k]+outF[k]+inM[k]+inF[k]%></td>
              </tr>
              <% } %>
              <tr> 
                <td height="23" valign="top" class="tableheader">Total</td>
                <td height="23" valign="top" class="tableheader"><%=totalOutM%></td>
                <td height="23" valign="top" class="tableheader"><%=totalOutF%></td>
                <td class="tableheader"><%=totalOutM+totalOutF%></td>
                <td class="tableheader"><%=totalInM%></td>
                <td class="tableheader"><%=totalInF%></td>
                <td class="tableheader"><%=totalInM+totalInF%></td>
                <td class="tableheader"><%=totalOutM+totalInM%></td>
                <td class="tableheader"><%=totalOutF+totalInF%></td>
                <td class="tableheader"><%=totalOutM+totalInM+totalOutF+totalInF%></td>
              </tr>
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