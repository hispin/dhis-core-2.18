<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>


<%
    Connection con=null;            
    Statement st1=null;
    ResultSet rs1=null; 
	    
  //initial value
  int orgUnitId = 6;
  int periodId = 203;
  String parentName= null;
    OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
  
    String userName = "root";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/dhis2";  
    
    String[][] orgUnit= new String[100][2];
	int[] pregTt1= new int[100];
	int[] pregTt2= new int[100];
	int[] pregTt3= new int[100];
	int[] pregTt4= new int[100];
	int[] pregTt5= new int[100];
	//int[] pregTt2Pl= new int[100];
	int[] nonPregTt1= new int[100];
	int[] nonPregTt2= new int[100];
	int[] nonPregTt3= new int[100];
	int[] nonPregTt4= new int[100];
	int[] nonPregTt5= new int[100];
	//int[] nonPregTt2Pl= new int[100];
	int totalPregTt1= 0;
	int totalPregTt2= 0;
	int totalPregTt3= 0;
	int totalPregTt4= 0;
	int totalPregTt5= 0;
	int totalPregTt2Pl=0;
	int totalNonPregTt1= 0;
	int totalNonPregTt2= 0;
	int totalNonPregTt3=0;
	int totalNonPregTt4= 0;
	int totalNonPregTt5= 0;
	int totalNonPregTt2Pl= 0;
	String orgUnitName="";
	String startDate="";
	String endDate="";
	boolean nextorg=true;
	
	//String region = new String;	
	//String subCity = new String;
	int i=0;
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
        rs1 = st1.executeQuery("select id,name from organizationunit where parent=" + orgUnitId); 
		String condition = "";
	    while(rs1.next())
          {         
	  			orgUnit[i][0]=rs1.getString("id");
	  			orgUnit[i][1]=rs1.getString("name");
				condition+= rs1.getInt("id")+",";
	  			i++;
           } 
		condition+= condition+("-1");
		
				
        rs1 = st1.executeQuery("select name from organizationunit where id=" + orgUnitId);
		if (rs1.next()){
			orgUnitName= rs1.getString("name");
		}
				
        rs1 = st1.executeQuery("select name from organizationunit where parent is null");
		if (rs1.next()){
			parentName= rs1.getString("name");
		}

		rs1 = st1.executeQuery("select startDate,endDate from period where id=" + periodId);
		if (rs1.next()){
			startDate= rs1.getString("startDate");
			endDate= rs1.getString("endDate");						
		}
		
		String sql="select value,source from datavalue where dataElement=613 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        int j=0;

		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					pregTt1[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=615 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					pregTt2[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=619 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					pregTt3[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=621 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					pregTt4[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=623 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					pregTt5[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		/*sql="select value,source from datavalue where dataElement=000 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					pregTt2Pl[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
          } */	   
	
		sql="select value,source from datavalue where dataElement=612 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					nonPregTt1[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=614 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					nonPregTt2[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=618 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					nonPregTt3[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=620 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					nonPregTt4[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		sql="select value,source from datavalue where dataElement=622 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					nonPregTt5[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
		  } 
	
		/*sql="select value,source from datavalue where dataElement=000 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
       	{
			rs1.beforeFirst();
			nextorg=true;
			while (rs1.next())        
			{
				if ((rs1.getString("source").equals(orgUnit[j][0])))
				{
					nonPregTt2pl[j]=rs1.getInt("value");
					nextorg=false;
				}
			}
          } 	  
          } */	   
		for (j=0;j<i;j++)
		{
			totalPregTt1+= pregTt1[j];
			totalPregTt2+= pregTt2[j];
			totalPregTt3+= pregTt3[j];
			totalPregTt4+= pregTt4[j];
			totalPregTt5+= pregTt5[j];
			totalNonPregTt1+= nonPregTt1[j];
			totalNonPregTt2+= nonPregTt2[j];
			totalNonPregTt3+= nonPregTt3[j];
			totalNonPregTt4+= nonPregTt4[j];
			totalNonPregTt5+= nonPregTt5[j];
		}	
		  
		 }//try
		 catch(Exception e)  { out.println("Loi cho ma gi day: "+e.getMessage());  }
		    	        
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>TT Vaccination Report for <%=orgUnitName%></title>
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
              <td width="869" height="93" valign="top" class="header">TT Vaccination Report for <%=orgUnitName%></td>
            </tr>
          </table>          </td>
        </tr>
        <tr> 
          <td height="49" valign="top">
          <table width="100%" border="0" cellpadding="0" cellspacing="0" bordercolor="#000066" class="header1" bgcolor="#008080" style="border-collapse: collapse" >
              <!--DWLayoutTable-->
              <tr class="header1"> 
                <td width="139" height="45" valign="top" class="header1" bordercolor="#000080">Region</td>
                <td width="127" bordercolor="#000080">&nbsp;</td>
                <td width="121" valign="top" class="header1" bordercolor="#000080" >Sub-City</td>
                <td width="200" bordercolor="#000080"><%=orgUnitName%></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="59" valign="top">
          <table width="100%" border="0" cellpadding="0" cellspacing="0" bordercolor="#000066" class="header1" bgcolor="#008080" style="border-collapse: collapse">
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
                <td width="89" rowspan="2" valign="top" class="tableheader">Site 
                  Name </td>
                <td colspan="5" valign="top" class="tableheader">Pregnant Women 
                </td>
                <td width="70" rowspan="2" valign="top" class="tableheader">Pregnant 
                  Women Total</td>
                <td height="41" colspan="5" valign="top" class="tableheader">Non-pregnant 
                  Women </td>
                <td width="109" rowspan="2" valign="top" class="tableheader">Total 
                  Non-pregnant Women</td>
                <td width="107" rowspan="2" valign="top" class="tableheader">Grand 
                  Total</td>
              </tr>
              <tr> 
                <td width="45" valign="top" class="tableheader">TT1</td>
                <td width="45" valign="top" class="tableheader">TT2</td>
                <td width="43" valign="top" class="tableheader">TT3</td>
                <td width="42" valign="top" class="tableheader">TT4</td>
                <td width="39" valign="top" class="tableheader">TT5</td>
                <td width="50" valign="top" class="tableheader">TT1</td>
                <td width="50" valign="top" class="tableheader">TT2</td>
                <td width="48" valign="top" class="tableheader">TT3</td>
                <td width="48" valign="top" class="tableheader">TT4</td>
                <td width="41" height="23" valign="top" class="tableheader">TT5</td>
              </tr>
              <% for (int k=0;k<i;k++)  { %>
              <tr> 
                <td height="10" class="tableitem"><%=orgUnit[k][1]%>&nbsp;</td>
                <td class="tableitem"><%=pregTt1[k]%>&nbsp;</td>
                <td class="tableitem"><%=pregTt2[k]%>&nbsp;</td>
                <td class="tableitem"><%=pregTt3[k]%>&nbsp;</td>
                <td class="tableitem"><%=pregTt4[k]%>&nbsp;</td>
                <td class="tableitem"><%=pregTt5[k]%>&nbsp;</td>
                <td class="tableheader"><%=pregTt1[k]+pregTt1[k]+pregTt2[k]+pregTt3[k]+pregTt4[k]+pregTt5[k]%>&nbsp;</td>
                <td class="tableitem"><%=nonPregTt1[k]%>&nbsp;</td>
                <td class="tableitem"><%=nonPregTt2[k]%>&nbsp;</td>
                <td class="tableitem"><%=nonPregTt3[k]%>&nbsp;</td>
                <td class="tableitem"><%=nonPregTt4[k]%>&nbsp;</td>
                <td class="tableitem"><%=nonPregTt5[k]%>&nbsp;</td>
                <td class="tableheader"><%=nonPregTt1[k]+nonPregTt2[k]+nonPregTt3[k]+nonPregTt4[k]+nonPregTt5[k]%>&nbsp;</td>
                <td class="tableheader"><%=pregTt1[k]+pregTt2[k]+pregTt3[k]+pregTt4[k]+pregTt5[k]+nonPregTt1[k]+nonPregTt2[k]+nonPregTt3[k]+nonPregTt4[k]+nonPregTt5[k]%>&nbsp;</td>
              </tr>
              <% } %>
              <tr> 
                <td height="23" valign="top" class="tableheader">Total</td>
                <td class="tableheader"><%=totalPregTt1%>&nbsp;</td>
                <td class="tableheader"><%=totalPregTt2%>&nbsp;</td>
                <td class="tableheader"><%=totalPregTt3%>&nbsp;</td>
                <td class="tableheader"><%=totalPregTt4%>&nbsp;</td>
                <td class="tableheader"><%=totalPregTt5%>&nbsp;</td>
                <td class="tableheader"><%=totalPregTt1+totalPregTt2+totalPregTt3+totalPregTt4+totalPregTt5%>&nbsp;</td>
                <td class="tableheader"><%=totalNonPregTt1%>&nbsp;</td>
                <td class="tableheader"><%=totalNonPregTt2%>&nbsp;</td>
                <td class="tableheader"><%=totalNonPregTt3%>&nbsp;</td>
                <td class="tableheader"><%=totalNonPregTt4%>&nbsp;</td>
                <td class="tableheader"><%=totalNonPregTt5%>&nbsp;</td>
                <td class="tableheader"><%=totalNonPregTt1+totalNonPregTt2+totalNonPregTt3+totalNonPregTt4+totalNonPregTt5%>&nbsp;</td>
                <td class="tableheader"><%=totalPregTt1+totalPregTt2+totalPregTt3+totalPregTt4+totalPregTt5+totalNonPregTt1+totalNonPregTt2+totalNonPregTt3+totalNonPregTt4+totalNonPregTt5%>&nbsp;</td>
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