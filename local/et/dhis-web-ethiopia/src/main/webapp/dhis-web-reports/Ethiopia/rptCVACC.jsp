<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>


<%
    Connection con=null;            
    Statement st1=null;
	Statement st2=null;
	Statement st3=null;
    ResultSet rs1=null; 
	ResultSet rs2=null;
	ResultSet rs3=null;
	    
  //initial value
  int orgUnitId = 253;
  int periodId = 203;
  int parentId = 0;
  String parentName = "";
    OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
  
    String userName = "root";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/dhis2";  
    
    String[][] orgUnit= new String[100][2];
	int[] bcgM= new int[100];
	int[] bcgF= new int[100];
	int[] polio0M= new int[100];
	int[] polio0F= new int[100];
	int[] polio1M= new int[100];
	int[] polio1F= new int[100];
	int[] polio2M= new int[100];
	int[] polio2F= new int[100];
	int[] polio3M= new int[100];
	int[] polio3F= new int[100];
	int[] dpt1M= new int[100];
	int[] dpt1F= new int[100];
	int[] dpt2M= new int[100];
	int[] dpt2F= new int[100];
	int[] dpt3M= new int[100];
	int[] dpt3F= new int[100];
	int[] measlesM= new int[100];
	int[] measlesF= new int[100];
	int[] fullyVaccM= new int[100];
	int[] fullyVaccF= new int[100];
	int[] vitA1259FirstM= new int[100];
	int[] vitA1259FirstF= new int[100];
	int[] vitA1259SecondM= new int[100];
	int[] vitA1259SecondF= new int[100];
	int[] vitA911M= new int[100];
	int[] vitA911F= new int[100];
	int[] vitAMothers= new int[100];
	int totalBcgM= 0;
	int totalBcgF= 0;
	int totalPolio0M= 0;
	int totalPolio0F= 0;
	int totalPolio1M= 0;
	int totalPolio1F= 0;
	int totalPolio2M= 0;
	int totalPolio2F= 0;
	int totalPolio3M= 0;
	int totalPolio3F= 0;
	int totalDpt1M= 0;
	int totalDpt1F= 0;
	int totalDpt2M=0;
	int totalDpt2F=0;
	int totalDpt3M= 0;
	int totalDpt3F= 0;
	int totalMeaslesM= 0;
	int totalMeaslesF= 0;
	int totalFullyVaccM=0;
	int totalFullyVaccF=0;
	int totalVitA911M= 0;
	int totalVitA911F= 0;
	int totalVitAMothers= 0;
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
		st2=con.createStatement();
		st3=con.createStatement();
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
		
		
        rs1 = st1.executeQuery("select name,parent from organizationunit where id=" + orgUnitId);
		if (rs1.next()){
			orgUnitName= rs1.getString("name");
			//parentId=rs1.getInt("parent");
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
				
		String sql="select value,source from datavalue where dataElement=593 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        int j=0;
	
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
		{
			rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{
						bcgM[j]=rs1.getInt("value");
						nextorg=false;
					}
					
				}					
			}
		}
		
		sql="select value,source from datavalue where dataElement=2691 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        j=0;
	
		
		if (rs1.next())
		{
		for (j=0;j<i;j++)
		{
			rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{
						bcgF[j]=rs1.getInt("value");
						nextorg=false;
					}
					
				}					
			}
		}
		
		sql="select value,source from datavalue where dataElement=595 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						dpt1M[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	} 	 
		  }  
		
		sql="select value,source from datavalue where dataElement=2694 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						dpt1F[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	} 	 
		  }  
	
		sql="select value,source from datavalue where dataElement=596 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						dpt2M[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	} 	 
		  }  
	
		sql="select value,source from datavalue where dataElement=2695 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						dpt2F[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	} 	 
		  }  
	
		sql="select value,source from datavalue where dataElement=597 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						dpt3M[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	}
		  } 	   
	
		sql="select value,source from datavalue where dataElement=2696 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						dpt3F[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	}
		  } 	   
	
		sql="select value,source from datavalue where dataElement=603 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						measlesM[j]=rs1.getInt("value");
						nextorg=false;
					}
					
				}
          	} 	   
		  }
	
		sql="select value,source from datavalue where dataElement=2697 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						measlesF[j]=rs1.getInt("value");
						nextorg=false;
					}
					
				}
          	} 	   
		  }

		sql="select value,source from datavalue where dataElement=605 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          { 
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{      
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio0M[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	}
		  } 	   

		sql="select value,source from datavalue where dataElement=2698 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          { 
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{      
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio0F[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	}
		  } 	   
	
		sql="select value,source from datavalue where dataElement=607 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{         
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio1M[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	}
		  } 	   
	
		sql="select value,source from datavalue where dataElement=2699 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{         
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio1F[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	}
		  } 	   
	
		sql="select value,source from datavalue where dataElement=609 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          { 
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio2M[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	} 	 
		  }  
	
		sql="select value,source from datavalue where dataElement=2700 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          { 
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio2F[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	} 	 
		  }  
	
		sql="select value,source from datavalue where dataElement=611 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio3M[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}

          	} 	 
		  }  
	
	
		sql="select value,source from datavalue where dataElement=2701 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						polio3F[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}

          	} 	 
		  }  
	
		sql="select value,source from datavalue where dataElement=601 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						fullyVaccM[j]=rs1.getInt("value");
						nextorg=false;
					}
					
				}
          	} 
		  }	   
	
		sql="select value,source from datavalue where dataElement=2702 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						fullyVaccF[j]=rs1.getInt("value");
						nextorg=false;
					}
					
				}
          	} 
		  }	   
	
		rs1 = st1.executeQuery("select value,source from datavalue where dataElement=2692 and period=" +periodId+" and source in ("+condition+")");
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						vitA911M[j]=rs1.getInt("value");					
						nextorg=false;
					}
				}

          	} 	 
		  }  
	
		rs1 = st1.executeQuery("select value,source from datavalue where dataElement=2693 and period=" +periodId+" and source in ("+condition+")");
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						vitA911F[j]=rs1.getInt("value");					
						nextorg=false;
					}
				}

          	} 	 
		  }  
	
		sql="select value,source from datavalue where dataElement=624 and period=" +periodId+" and source in ("+condition+")";
		rs1 = st1.executeQuery(sql);
        
		if (rs1.next())
		{
		for (j=0;j<i;j++)
          {         
		  	rs1.beforeFirst();
			nextorg=true;
				while (rs1.next() && nextorg)
				{        
					if (rs1.getString("source").equals(orgUnit[j][0]))
					{         
						vitAMothers[j]=rs1.getInt("value");
						nextorg=false;
					}
				
				}
          	} 	 
		  }
		  
		  for (j=0;j<i;j++)
		  {
		  	totalBcgM+=bcgM[j];
			totalBcgF+=bcgF[j];
			totalDpt1M+=dpt1M[j];
			totalDpt1F+=dpt1F[j];
			totalDpt2M+=dpt2M[j];
			totalDpt2F+=dpt2F[j];
			totalDpt3M+=dpt3M[j];
			totalDpt3F+=dpt3F[j];
			totalPolio0M+=polio0M[j];
			totalPolio0F+=polio0F[j];
			totalPolio1M+=polio1M[j];
			totalPolio1F+=polio1F[j];
			totalPolio2M+=polio2M[j];
			totalPolio2F+=polio2F[j];
			totalPolio3M+=polio3M[j];
			totalPolio3F+=polio3F[j];
			totalMeaslesM+=measlesM[j];
			totalMeaslesF+=measlesF[j];
			totalFullyVaccM+=fullyVaccM[j];
			totalFullyVaccF+=fullyVaccF[j];
			totalVitA911M+=vitA911M[j];
			totalVitA911F+=vitA911F[j];
			totalVitAMothers+=vitAMothers[j];
		}
		  
		 }//try
		 catch(Exception e)  { out.println("Loi cho ma gi day: "+e.getMessage());  }
		    	        
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Children Vaccination Report for <%=orgUnitName%></title>
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
              <td width="869" height="93" valign="top" class="header">Children Vaccination for <%=orgUnitName%> </td>
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
          <td height="84" valign="top">
          <table width="925" border="0" cellpadding="1" cellspacing="1" bordercolor="#000066">
              <!--DWLayoutTable-->
              <tr> 
                <td width="42" rowspan="3" valign="top" class="tableheader">Site 
                  Name </td>
                <td rowspan="2" valign="top" class="tableheader" colspan="2">BCG</td>
                <td rowspan="2" valign="top" class="tableheader" colspan="2">DPT1</td>
                <td rowspan="2" valign="top" class="tableheader" colspan="2">DPT2</td>
                <td rowspan="2" valign="top" class="tableheader" colspan="2">DPT3</td>
                <td rowspan="2" valign="top" class="tableheader" colspan="2">Measles</td>
                <td colspan="8" valign="top" class="tableheader">Polio</td>
                <td rowspan="2" valign="top" class="tableheader" colspan="2">Fully 
                  Immunized</td>
                <td rowspan="2" valign="top" class="tableheader" colspan="2">Children 
                  &lt;5 got Vit. A </td>
                <td width="65" rowspan="3" valign="top" class="tableheader">Lactating mothers 
                  got Vit A</td>
                <td colspan="3" rowspan="2" valign="top" class="tableheader">Total</td>
              </tr>
              <tr> 
                <td valign="top" class="tableheader" colspan="2"> 0 </td>
                <td valign="top" class="tableheader" colspan="2"> 1 </td>
                <td valign="top" class="tableheader" colspan="2"> 2 </td>
                <td valign="top" class="tableheader" colspan="2"> 3 </td>
              </tr>
              <tr> 
                <td width="17" height="18" valign="top" class="tableheader">M</td>
                <td width="17" valign="top" class="tableheader">F</td>
                <td width="17" valign="top" class="tableheader">M</td>
                <td width="17" valign="top" class="tableheader">F</td>
                <td width="17" valign="top" class="tableheader">M</td>
                <td width="17" valign="top" class="tableheader">F</td>
                <td width="17" valign="top" class="tableheader">M</td>
                <td width="17" valign="top" class="tableheader">F</td>
                <td width="31" valign="top" class="tableheader">M</td>
                <td width="19" valign="top" class="tableheader">F</td>
                <td width="17" valign="top" class="tableheader">M</td>
                <td width="17" valign="top" class="tableheader">F</td>
                <td width="19" valign="top" class="tableheader">M</td>
                <td width="17" valign="top" class="tableheader">F</td>
                <td width="23" valign="top" class="tableheader">M</td>
                <td width="52" valign="top" class="tableheader">F</td>
                <td width="26" valign="top" class="tableheader">M</td>
                <td width="17" valign="top" class="tableheader">F</td>
                <td width="30" valign="top" class="tableheader">M</td>
                <td width="73" valign="top" class="tableheader">F</td>
                <td width="41" valign="top" class="tableheader">M</td>
                <td width="98" valign="top" class="tableheader">F</td>
                <td width="30" valign="top" class="tableheader">M</td>
                <td width="25" valign="top" class="tableheader">F</td>
                <td width="65" valign="top" class="tableheader">GT</td>
              </tr>
              <% for (int k=0;k<i;k++)  { %>
              <tr> 
                <td height="10" class="tableitem" width="42"><%=orgUnit[k][1]%></td>
                <td class="tableitem" width="17"><%=bcgM[k]%></td>
                <td class="tableitem" width="17"><%=bcgF[k]%></td>
                <td class="tableitem" width="17"><%=dpt1M[k]%></td>
                <td class="tableitem" width="17"><%=dpt1F[k]%></td>
                <td class="tableitem" width="17"><%=dpt2M[k]%></td>
                <td class="tableitem" width="17"><%=dpt2F[k]%></td>
                <td class="tableitem" width="17"><%=dpt3M[k]%></td>
                <td class="tableitem" width="17"><%=dpt3F[k]%></td>
                <td class="tableitem" width="31"><%=measlesM[k]%></td>
                <td class="tableitem" width="19"><%=measlesF[k]%></td>
                <td class="tableitem" width="17"><%=polio0M[k]%></td>
                <td class="tableitem" width="17"><%=polio0F[k]%></td>
                <td class="tableitem" width="19"><%=polio1M[k]%></td>
                <td class="tableitem" width="17"><%=polio1F[k]%></td>
                <td class="tableitem" width="23"><%=polio2M[k]%></td>
                <td class="tableitem" width="52"><%=polio2F[k]%></td>
                <td class="tableitem" width="26"><%=polio3M[k]%></td>
                <td class="tableitem" width="17"><%=polio3F[k]%></td>
                <td class="tableitem" width="30"><%=fullyVaccM[k]%></td>
                <td class="tableitem" width="73"><%=fullyVaccF[k]%></td>
                <td class="tableitem" width="41"><%=vitA911M[k]%></td>
                <td class="tableitem" width="98"><%=vitA911F[k]%></td>
                <td class="tableitem"><%=vitAMothers[k]%></td>
                <td class="tableheader" width="30"><%=bcgM[k]+dpt1M[k]+dpt2M[k]+dpt3M[k]+measlesM[k]+polio0M[k]+polio1M[k]+polio2M[k]+polio3M[k]+fullyVaccM[k]+vitA911M[k]%></td>
                <td class="tableheader"><%=bcgF[k]+dpt1F[k]+dpt2F[k]+dpt3F[k]+measlesF[k]+polio0F[k]+polio1F[k]+polio2F[k]+polio3F[k]+fullyVaccF[k]+vitA911F[k]%></td>
                <td class="tableheader"><%=bcgM[k]+bcgM[k]+dpt1M[k]+dpt1F[k]+dpt2M[k]+dpt2F[k]+dpt3M[k]+dpt3F[k]+measlesF[k]+polio0M[k]+polio0F[k]+polio1M[k]+polio1F[k]+polio2M[k]+polio2F[k]+polio3M[k]+polio3F[k]+fullyVaccM[k]+fullyVaccF[k]+vitA911M[k]+vitA911F[k]+vitAMothers[k]%></td>
              </tr>
              <% } %>
              <tr> 
                <td height="23" valign="top" class="tableheader" width="42">Total</td>
                <td class="tableheader" width="17"><%=totalBcgM%></td>
                <td class="tableheader" width="17"><%=totalBcgF%></td>
                <td class="tableheader" width="17"><%=totalDpt1M%></td>
                <td class="tableheader" width="17"><%=totalDpt1F%></td>
                <td class="tableheader" width="17"><%=totalDpt2M%></td>
                <td class="tableheader" width="17"><%=totalDpt2F%></td>
                <td class="tableheader" width="17"><%=totalDpt3M%></td>
                <td class="tableheader" width="17"><%=totalDpt3F%></td>
                <td class="tableheader" width="31"><%=totalMeaslesM%></td>
                <td class="tableheader" width="19"><%=totalMeaslesF%></td>
                <td class="tableheader" width="17"><%=totalPolio0M%></td>
                <td class="tableheader" width="17"><%=totalPolio0F%></td>
                <td class="tableheader" width="19"><%=totalPolio1M%></td>
                <td class="tableheader" width="17"><%=totalPolio1F%></td>
                <td class="tableheader" width="23"><%=totalPolio2M%></td>
                <td class="tableheader" width="52"><%=totalPolio2F%></td>
                <td class="tableheader" width="26"><%=totalPolio3M%></td>
                <td class="tableheader" width="17"><%=totalPolio3F%></td>
                <td class="tableheader" width="30"><%=totalFullyVaccM%></td>
                <td class="tableheader" width="73"><%=totalFullyVaccF%></td>
                <td class="tableheader" width="41"><%=totalVitA911M%></td>
                <td class="tableheader" width="98"><%=totalVitA911F%></td>
                <td class="tableheader"><%=totalVitAMothers%></td>
                <td class="tableheader" width="30"><%=totalBcgM+totalDpt1M+totalDpt2M+totalDpt3M+totalMeaslesM+totalPolio0M+totalPolio1M+totalPolio2M+totalPolio3M+totalFullyVaccM+totalVitA911M%></td>
                <td class="tableheader"><%=totalBcgF+totalDpt1F+totalDpt2F+totalDpt3F+totalMeaslesF+totalPolio0F+totalPolio1F+totalPolio2F+totalPolio3F+totalFullyVaccF+totalVitA911F%></td>
                <td class="tableheader"><%=totalBcgM+totalDpt1M+totalDpt2M+totalDpt3M+totalMeaslesM+totalPolio0M+totalPolio1M+totalPolio2M+totalPolio3M+totalFullyVaccM+totalVitA911M+totalBcgF+totalDpt1F+totalDpt2F+totalDpt3F+totalMeaslesF+totalPolio0F+totalPolio1F+totalPolio2F+totalPolio3F+totalFullyVaccF+totalVitA911F+totalVitAMothers%></td>
              </tr>
            </table>          </td>
        </tr>
      </table>
      <span class="tableitemnumber"></span></td>
    <td width="127" height="275"><!--DWLayoutEmptyCell-->&nbsp;</td>
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