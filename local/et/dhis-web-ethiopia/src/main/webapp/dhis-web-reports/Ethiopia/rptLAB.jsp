
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
	int parentId=0;
	String parentName=null;
	String startDate="";
	String endDate="";
OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
  
    int[] noutpM= new int[100];
	int[] noutpF= new int[100];
	
	int[] noutnM= new int[100];
	int[] noutnF= new int[100];
	
	int[] ninpM= new int[100];
	int[] ninpF= new int[100];
	
	int[] ninnM= new int[100];
	int[] ninnF= new int[100];
	
	int[] ootherM=new int[100];
	int[] ootherF=new int[100];
	
	int[] iotherM=new int[100];
	int[] iotherF=new int[100];
	
	
	
	//int[] sn= {1,2,3,4,5,6,7};
	String[] lab1= {"Stool and Other Parasite Test","Bacteriology"," ","Urinalysis","Hematology"," "," "," ","Serology"," "," "," "," ","Clinical Chemistry","Others","Total"};
	String[] lab2= {" ","Sputum for AFB","Others"," ","Blood Film","Malaria","Relapsing Fever","Others","HIV Screened","VDRL","WIDAL","WELIFELX","Others"," "," "," "};
	
	
	//String[] no={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17"};

	//String[] type_of_test= {"Stool and Other parasite test","Bacteriology sputum for AFB"," Bacteriology sputum for Others","Urinalysis","Pregnancy Test","Hematology Blood Film","Hematology Malaria","Hematolgy Relapsing Fever","Hematology Others","Serology VDRL","Serology WIDAL","Serology WEILFELX","Serology Others","Chemistry","HIV screened","Others","Total"};
	
	int [] deint1={1361,1328,1375,1387,1283,2619,1324,1399,2643,1348,1357,1353,1311,1412,1424};
	int [] deint2={1362,1326,1378,1388,1282,2620,1322,1307,2644,1346,1358,1354,1312,1414,1425};
	int [] deint3={1363,1332,1376,1389,1269,2622,2631,1400,2645,1352,1359,1355,1313,1415,1428};
	int [] deint4={1364,1330,1377,1390,1284,2621,2632,1308,2646,1350,1360,1356,1314,1413,1429};
	int [] deint5={1365,2618,1379,1391,2639,2623,2635,1309,2647,2655,2459,2667,2675,1418,1426};
	int [] deint6={1367,2617,1380,1392,2640,2624,2636,1310,2648,2656,2660,2668,2676,1416,1427};
	int [] deint7={1370,1327,1381,1393,1279,2625,1323,1405,2649,1347,2661,2669,2677,1420,1430};
	int [] deint8={1369,1325,1382,1394,1278,2626,1321,1406,2650,1345,2662,2670,2678,1422,1431};
	int [] deint9={1371,1331,1385,1395,1281,2627,2633,1408,2651,1351,2663,2671,2679,1419,1432};
	int [] deint10={1372,1329,1386,1396,1290,2630,2634,1409,2652,1349,2664,2672,2680,1421,1433};
	int [] deint11={1373,2616,1383,1398,2641,2628,2637,1407,2653,2657,2665,2673,2651,1423,1434};
	int [] deint12={1374,2615,1384,1397,2642,2629,2638,1410,2654,2658,2666,2674,2682,1624,1435};
	
	
	int i=0;
	String sql;
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
          
 		
  		sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1361,1328,1375,1387,1283,2619,1324,1399,2643,1348,1357,1353,1311,1412,1424)";		
        rs1 = st1.executeQuery(sql);    	  			
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint1[i]==(rs1.getInt("dataelement")))
			{
            	noutpM[i]=rs1.getInt("value");				
            	nextde=false;
            }
			}
			}
         }
		//geting orguntname
		sql = "select name,parent from organizationunit where id ="+orgUnitId ;
	    rs1 = st1.executeQuery(sql);  
         if (rs1.next())
        {
           orgUnitName=rs1.getString("name");
		   parentId=rs1.getInt("parent");				
	    }
		
		sql = "select name from organizationunit where id="+parentId;
		rs1 = st1.executeQuery(sql);
		if (rs1.next())
		{
			parentName=rs1.getString("name");
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
		sql+=" and dataelement in (1362,1326,1378,1388,1282,2620,1322,1307,2644,1346,1358,1354,1312,1414,1425)";		
        rs1 = st1.executeQuery(sql);  
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint2[i]==(rs1.getInt("dataelement")))
			{
            	noutpF[i]=rs1.getInt("value");				
            	nextde=false;
            }
			}
			}
         }
		    
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1363,1332,1376,1389,1269,2622,2631,1400,2645,1352,1359,1355,1313,1415,1428)";		
        rs1 = st1.executeQuery(sql);
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint3[i]==(rs1.getInt("dataelement")))
			{
            	noutnM[i]=rs1.getInt("value");				
            	nextde=false;
            }
			}
			}
         }

        
        
		   
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1364,1330,1377,1390,1284,2621,2632,1308,2646,1350,1360,1356,1314,1413,1429)";		
        rs1 = st1.executeQuery(sql);    	  			
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint4[i]==(rs1.getInt("dataelement")))
			{
            	noutnF[i]=rs1.getInt("value");				
            	nextde=false;
			}
			}
            }
         }

		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1365,2618,1379,1391,2639,2623,2635,1309,2647,2655,2459,2667,2675,1418,1426)";		
        rs1 = st1.executeQuery(sql);  
          
       	if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint5[i]==(rs1.getInt("dataelement")))
			{
            	ootherM[i]=rs1.getInt("value");				
            	nextde=false;
			}
			}
            }
         }
  	  			
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1367,2617,1380,1392,2640,2624,2636,1310,2648,2656,2660,2668,2676,1416,1427)";		
        rs1 = st1.executeQuery(sql); 
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint6[i]==(rs1.getInt("dataelement")))
			{
            	ootherF[i]=rs1.getInt("value");				
            	nextde=false;
			}
			}
            }
         }
   	  			
          
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		   sql+=" and dataelement in (1370,1327,1381,1393,1279,2625,1323,1405,2649,1347,2661,2669,2677,1420,1430)";		
        rs1 = st1.executeQuery(sql);  
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint7[i]==(rs1.getInt("dataelement")))
			{
            	ninpM[i]=rs1.getInt("value");				
            	nextde=false;
			}
			}
            }
         }
  	  			
          
		   sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1369,1325,1382,1394,1278,2626,1321,1406,2650,1345,2662,2670,2678,1422,1431)";		
        rs1 = st1.executeQuery(sql);    	  			
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint8[i]==(rs1.getInt("dataelement")))
			{
            	ninpF[i]=rs1.getInt("value");				
            	nextde=false;
			}
			}
            }
         }
         
         
         

           sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1371,1331,1385,1395,1281,2627,2633,1408,2651,1351,2663,2671,2679,1419,1432)";		
        rs1 = st1.executeQuery(sql);    	  			
     
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint9[i]==(rs1.getInt("dataelement")))
			{
            	ninnM[i]=rs1.getInt("value");
				nextde=false;
			}
			}
			}        
            
         }
     
  sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1372,1329,1386,1396,1290,2630,2634,1409,2652,1349,2664,2672,2680,1421,1433)";		
        rs1 = st1.executeQuery(sql);    	  			
     
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint10[i]==(rs1.getInt("dataelement")))
			{
            	ninnF[i]=rs1.getInt("value");
				nextde=false;
			}
			}
			}				
         }
     
  sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1373,2616,1383,1398,2641,2628,2637,1407,2653,2657,2665,2673,2651,1423,1434)";		
        rs1 = st1.executeQuery(sql);    	  			
     
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint10[i]==(rs1.getInt("dataelement")))
			{
            	iotherM[i]=rs1.getInt("value");
				nextde=false;
			}
			}
			}				
         }
     
  sql = "select value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1374,2615,1384,1397,2642,2629,2638,1410,2654,2658,2666,2674,2682,1624,1435)";		
        rs1 = st1.executeQuery(sql);    	  			
     
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint12[i]==(rs1.getInt("dataelement")))
			{
            	iotherF[i]=rs1.getInt("value");
				nextde=false;
			}
			}
			}				
         }
      
		for(int z=0;z<=14;z++){
				noutpM[15]+=noutpM[z];
				noutpF[15]+=noutpF[z];
				noutnM[15]+=noutnM[z];
				noutnF[15]+=noutnF[z];
				ninpM[15]+=ninpM[z];
				ninpF[15]+=ninpF[z];
				ninnM[15]+=ninnM[z];
				ninnF[15]+=ninnF[z];
				ootherM[15]+=ootherM[z];
				ootherF[15]+=ootherF[z];
				iotherM[15]+=iotherM[z];
				iotherF[15]+=iotherF[z];
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
    <td colspan="2" valign="top">
    <table width="651" border="0" cellpadding="1" cellspacing="1">
        <!--DWLayoutTable-->
        <tr> 
          <td width="665" height="54" valign="top">
          <table width="649" border="0" cellpadding="1" cellspacing="0">
              <!--DWLayoutTable-->
              <tr class="header"> 
                <td width="647" height="54" valign="top"  >&nbsp; Laboratory Test Report for <%=orgUnitName%></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="45" valign="top" width="665">
          <table width="650" border="0" cellpadding="1" cellspacing="1" bordercolor="#660000" class="header1" >
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC" class="header1"> 
                <td width="124" height="47" valign="top" class="header1" bgcolor="#008080"><div align="left">Region
                    
                </div>                </td>
               
                <td width="119" class="header1" bordercolor="#FF00FF" bgcolor="#008080"><%=parentName%></td>
                <td width="123" valign="top" class="header1" bgcolor="#008080" >OrgUnit</td>
                <td width="271" bgcolor="#008080"><%=orgUnitName %>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="55" valign="top" width="665">
          <table width="648" border="0" cellpadding="1" cellspacing="1" class="header1">
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC"> 
                <td width="108" height="55" valign="top" class="header1" bgcolor="#008080"><div align="left">Period</div></td>
                <td width="106" valign="top" class="header1" bgcolor="#008080">From</td>
                <td width="94" bgcolor="#008080"><%=startDate %>&nbsp;</td>
                <td width="101" valign="top" class="header1" bgcolor="#008080">To</td>
                <td width="223" valign="top" class="header1" bgcolor="#008080"><%=endDate %>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="515" valign="top" width="665">
          <table width="89%" border="0" cellpadding="0" cellspacing="0" height="141">
              <!--DWLayoutTable-->
              <tr> 
                <td width="555" height="123" valign="top">
                <table width="648" height="1" border="0" cellpadding="1" cellspacing="1" bgcolor="#000066">
                    <!--DWLayoutTable-->
                    <tr class="tableheader">
                      <td width="22" height="98" rowspan="4" valign="top" class="tableheader">
                     NO.</td>
                      <td width="214" height="98" rowspan="4" valign="top" class="tableheader" colspan="2">
                     Type Of Test</td>
                      <td height="1" colspan="27" valign="top" class="tableheader" width="526">
                      Number Of Test<p>&nbsp;</td>
                    </tr>
                    <tr class="tableheader">
                      <td height="1" colspan="12" valign="top" class="tableheader" width="138">
                      Out Patient</td>
                      <td height="1" colspan="15" valign="top" class="tableheader" width="388">
                      In Patient</td>
                    </tr>
                    <tr class="tableheader">
                      <td height="13" colspan="2" valign="top" class="tableheader" width="1">
                      Positive</td>
                      <td height="14" valign="top" class="tableheader" width="0" rowspan="2">
                      Total</td>
                      <td height="13" colspan="2" valign="top" class="tableheader" width="80">
                      Negative</td>
                      <td height="14" valign="top" class="tableheader" width="40" rowspan="2">
                      Total</td>
                      <td height="13" colspan="2" valign="top" class="tableheader" width="68">
                      Other</td>
                      <td height="14" valign="top" class="tableheader" width="34" rowspan="2">
                      Total</td>
                      <td height="13" colspan="3" valign="top" class="tableheader" width="50">
                      Outpatient Total</td>
                      <td height="13" colspan="2" valign="top" class="tableheader" width="104">
                      Positive</td>
                      <td height="14" valign="top" class="tableheader" width="52" rowspan="2">
                      Total</td>
                      <td height="13" colspan="2" valign="top" class="tableheader" width="64">
                      Negative</td>
                      <td height="14" valign="top" class="tableheader" width="16" rowspan="2">
                      Total</td>
                      <td height="7" colspan="2" valign="top" class="tableheader" width="12">
                      Other</td>
                      <td height="14" valign="top" class="tableheader" width="3" rowspan="2">
                      Total</td>
                      <td height="13" colspan="3" valign="top" class="tableheader" width="46">
                      Inpatient Total</td>
                      <td height="13" colspan="3" valign="top" class="tableheader" width="46">
                      Grand Total</td>
                    </tr>
                    <tr class="tableheader">
                      <td height="1" valign="top" class="tableheader" width="1">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="21">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="57">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="32">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="62">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="23">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="23">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="11">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="11">
                      T</td>
                      <td height="1" valign="top" class="tableheader" width="83">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="37">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="52">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="22">
                      F</td>
                      <td height="7" valign="top" class="tableheader" width="8">
                      M</td>
                      <td height="7" valign="top" class="tableheader" width="4">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="32">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="32">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="31">
                      T</td>
                      <td height="1" valign="top" class="tableheader" width="7">
                      M</td>
                      <td height="1" valign="top" class="tableheader" width="7">
                      F</td>
                      <td height="1" valign="top" class="tableheader" width="7">
                      GT</td>
                    </tr>
                    <tr class="tableheader">
                      </td> 
                    </tr>
                  <% for (int k=0;k<15;k++){%>
                    <tr bordercolor="#000000" class="tableitem">
                	<td width="22" height="30" valign="top" class="tableitem"><%=sn[k]%></td>
                    <% if (k==0 || k==1 || k==3 || k==4 || k==8 || k==13 || k==14 || k==15) {%><td width="11" height="30" valign="top" class="tableitem"><%=lab1[k]%></td><%}%>
                    <% if (k!=0 && k!=1 && k!=3 && k!=4 && k!=8 && k!=13 && k!=14 && k==15) {%><!-- Just to Try --><%}%>
					<% if (k ==1 || k== 2 || k==4 || k==5 || k==6 || k==7 || k==8 || k==9 || k==10 || k==11 || k==12) { %>
                    <td width="11" height="30" valign="top" class="tableitem">&nbsp;</td> <td width="26" height="30" valign="top" class="tableitem"><%=lab2[k]%></td> <%}%>
					<% if (k !=1 && k!= 2 && k!=4 && k!=5 && k!=6 && k!=7 && k!=8 && k!=9 && k!=10 && k!=11 && k!=12) { %> <!-- And Again Just to Try --> <%}%>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutpM[k]%></td> <%}%>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpF[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutpF[k]%></td> <%}%>
                    <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutpF[k]%></td>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutnM[k]%></td> <%}%>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutnM[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutnF[k]%></td> <%}%>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutnF[k]%></td> <%}%>
                    <td width="13" height="30" valign="top" class="tableheader"><%=noutnM[k]+noutnF[k]%></td>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ootherM[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ootherM[k]%></td> <%}%>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ootherF[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ootherF[k]%></td> <%}%>
                    <td width="13" height="30" valign="top" class="tableheader"><%=ootherM[k]+ootherF[k]%></td>
                    <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]%></td>
                    <td width="13" height="30" valign="top" class="tableheader"><%=noutpF[k]+noutnF[k]+ootherF[k]%></td>
                    <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+noutpF[k]+noutnF[k]+ootherF[k]%></td>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninpM[k]%></td> <%}%>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpF[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninpF[k]%></td> <%}%>
                    <td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]+ninF[k]%></td>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninnM[k]%></td> <%}%>
                    <% if (k==15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninnM[k]%></td> <%}%>
                    <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninnF[k]%></td> <%}%>
                   // 
					<%}%>
                  </table>
              </tr>
              <tr> 
                <td height="1" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#000066">
                    <!--DWLayoutTable-->
					
                  </table></td>
              </tr>
              <tr>
                <td height="19">&nbsp;<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#000080" width="100%" id="AutoNumber1" bgcolor="#000080">
                  <tr>
                    <td width="100%" colspan="2" bgcolor="#00CCFF" bordercolor="#000099">Number of tested persons</td>
                  </tr>
                  <tr>
                    <td width="50%" bgcolor="#00CCFF" bordercolor="#000099">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
                    Male </td>
                    <td width="50%" bgcolor="#00CCFF" bordercolor="#000099">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
                    Female</td>
                  </tr>
                  <tr>
                    <td width="50%" bgcolor="#00CCFF" bordercolor="#000099"><%=tpM%>&nbsp;</td>
                    <td width="50%" bgcolor="#00CCFF" bordercolor="#000099"><%=tpF%>&nbsp;</td>
                  </tr>
                </table>
                </td>
              </tr>
              </table></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td width="452"></td>
    <td></td>
  </tr>
</table>
</body>
</html><%@ page contentType="text/html; charset=" language="java" import="java.sql.*" errorPage="" %>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>

</body>
</html>