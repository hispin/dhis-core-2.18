
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
	
	boolean nextde=true;
	
	
	//int[] sn= {1,2,3,4,5,6,7};
	String[] lab1= {"Stool and Other Parasite Test","Bacteriology"," ","Urinalysis","Hematology"," "," "," ","Serology"," "," "," "," ","Clinical Chemistry","Others"};
	String[] lab2= {" ","Sputum for AFB","Others"," ","Blood Film","Malaria","Relapsing Fever","Others","HIV Screened","VDRL","WIDAL","WELIFELX","Others"," "," ","Total"};
	
	
	//String[] no={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17"};

	//String[] type_of_test= {"Stool and Other parasite test","Bacteriology sputum for AFB"," Bacteriology sputum for Others","Urinalysis","Pregnancy Test","Hematology Blood Film","Hematology Malaria","Hematolgy Relapsing Fever","Hematology Others","Serology VDRL","Serology WIDAL","Serology WEILFELX","Serology Others","Chemistry","HIV screened","Others","Total"};
	
	int [] deint1={1361,1328,1375,1387,1283,2619,1324,1399,2643,1348,1357,1353,1311,1412,1424};
	int [] deint2={1362,1326,1378,1388,1282,2620,1322,1401,2644,1346,1358,1354,1312,1414,1425};
	int [] deint3={1363,1332,1376,1389,1269,2622,2631,1400,2645,1352,1359,1355,1313,1415,1428};
	int [] deint4={1364,1330,1377,1390,1284,2621,2632,1402,2646,1350,1360,1356,1314,1413,1429};
	int [] deint5={1365,2618,1379,1391,2639,2623,2635,1403,2647,2655,2659,2667,2675,1418,1426};
	int [] deint6={1367,2617,1380,1392,2640,2624,2636,1404,2648,2656,2660,2668,2676,1416,1427};
	int [] deint7={1370,1327,1381,1393,1279,2625,1323,1405,2649,1347,2661,2669,2677,1420,1430};
	int [] deint8={1369,1325,1382,1394,1278,2626,1321,1406,2650,1345,2662,2670,2678,1422,1431};
	int [] deint9={1371,1331,1385,1395,1281,2627,2633,1408,2651,1351,2663,2671,2679,1419,1432};
	int [] deint10={1372,1329,1386,1396,1290,2630,2634,1409,2652,1349,2664,2672,2680,1421,1433};
	int [] deint11={1373,2616,1383,1398,2641,2628,2637,1407,2653,2657,2665,2673,2681,1423,1434};
	int [] deint12={1374,2615,1384,1397,2642,2629,2638,1410,2654,2658,2666,2674,2682,1624,1435};
	
	
	int i=0;
	String sql;
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
          
 		
  		sql = "select dataelement, value from datavalue where period="+periodId+" and source="+orgUnitId ;
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
	   
		sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1362,1326,1378,1388,1282,2620,1322,1401,2644,1346,1358,1354,1312,1414,1425)";		
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
		    
		   sql = "select dataelement, value from datavalue where period="+periodId+" and source="+orgUnitId ;
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

        
        
		   
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1364,1330,1377,1390,1284,2621,2632,1402,2646,1350,1360,1356,1314,1413,1429)";		
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

		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1365,2618,1379,1391,2639,2623,2635,1403,2647,2655,2659,2667,2675,1418,1426)";		
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
  	  			
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1367,2617,1380,1392,2640,2624,2636,1404,2648,2656,2660,2668,2676,1416,1427)";		
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
   	  			
          
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
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
  	  			
          
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
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
         
         
         

           sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
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
     
  sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
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
     
  sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (1373,2616,1383,1398,2641,2628,2637,1407,2653,2657,2665,2673,2681,1423,1434)";		
        rs1 = st1.executeQuery(sql);    	  			
     
          
       if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint11[i]==(rs1.getInt("dataelement")))
			{
            	iotherM[i]=rs1.getInt("value");
				nextde=false;
			}
			}
			}				
         }
     
  sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
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
<title>Laboratory Test Report for <%=orgUnitName%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../../../../dhis-web-reports/src/main/webapp/dhis-web-reports/styles.css" rel="stylesheet" type="text/css">
<link href="styles.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#99FFFF">
<p class="lefttableheader">&nbsp;</p>
<table width="1000" border="0" cellpadding="0" cellspacing="0">
  <!--DWLayoutTable-->
  <tr> 
    <td colspan="2" rowspan="2" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1">
        <!--DWLayoutTable-->
        <tr> 
          <td width="869" height="95" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
            <!--DWLayoutTable-->
              <tr> 
                <td width="869" height="93" valign="top" class="header">Laboratory 
                  Test Report for <%=orgUnitName%></td>
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
          <td height="84" valign="top" class="tableheader"> 
            <table width="925" border="0" bordercolor="#000066" class="tableheader">
              <!--DWLayoutTable-->
              <tr> 
                    <tr class="tableheader">
                      <td width="214" height="98" rowspan="4" valign="top" class="lefttableheader" colspan="2">
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
                  <% for (int k=0;k<16;k++){%>
                    <tr>
				<% if (k==15){%><td><!--  --></td><%}%>
                <% if (k==3) {%><td width="11" height="30" valign="top" class="lefttableitem"><%=lab1[k]%></td><%}%>
                <% if (k==1 || k==4 || k==8) {%><td width="11" height="30" valign="top" class="bottomopen"><%=lab1[k]%></td><%}%>
                <% if (k==0) {%><td width="11" height="30" valign="top" ><%=lab1[k]%></td><%}%>
				<% if (k==13 || k==14) {%><td class="lefttableheader"><%=lab1[k]%></td><%}%>
				<% if (!(k==0 || k==3 || k==13 || k==14|| k==8 || k==4 || k==15 || k==1)) {%><td width="11" height="30" valign="top" class="lefttableheader"></td><%}%>
                <% if (k==1 || k==4 || k==8){%><td width="11" height="30" valign="top" class="leftopen"><%=lab2[k]%></td><%}%>
                <% if (k==2 || k==7 || k==12){%><td width="11" height="30" valign="top" class="lefttableitem"><%=lab2[k]%></td><%}%>
                <% if (k==5 || k==6 || k==9 || k==10 || k==11){%><td width="11" height="30" valign="top" class="lefttableitem"><%=lab2[k]%></td><%}%>
                <% if (k==15){%><td width="11" height="30" valign="top" class="boxed"><%=lab2[k]%></td><%}%>
                <% if ((k==0 || k==3 || k==13 || k==14)) {%> <td width="11" height="30" valign="top" class="leftopen"><!--  --></td>  <%}%>
                <% if (k==15) {%><td width="11" height="30" valign="top" class="tableheader"><%=noutpM[k]%></td><%}%>
                <% if (k!=15) {%><td width="11" height="30" valign="top" class="tableitem"><%=noutpM[k]%></td><%}%>
                <% if (k==15) {%><td width="11" height="30" valign="top" class="tableheader"><%=noutpF[k]%></td><%}%>
                <% if (k!=15) {%><td width="11" height="30" valign="top" class="tableitem"><%=noutpF[k]%></td><%}%>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutpF[k]%></td>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutnM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutnM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutnF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutnF[k]%></td><%}%>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutnM[k]+noutnF[k]%></td>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ootherM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ootherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ootherF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ootherF[k]%></td><%}%>
                <td width="13" height="30" valign="top" class="tableheader"><%=ootherM[k]+ootherF[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutpF[k]+noutnF[k]+ootherF[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+noutpF[k]+noutnF[k]+ootherF[k]%></td>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninpM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninpF[k]%></td><%}%>
                <td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]+ninpF[k]%></td>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninnM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninnM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninnF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninnF[k]%></td><%}%>
                <td width="13" height="30" valign="top" class="tableheader"><%=ninnM[k]+ninnF[k]%></td>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=iotherM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=iotherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=iotherF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=iotherF[k]%></td><%}%>
                <td width="13" height="30" valign="top" class="tableheader"><%=iotherM[k]+iotherF[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]+ninnM[k]+iotherM[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=ninpF[k]+ninnF[k]+iotherF[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]+ninpF[k]+ninnM[k]+ninnF[k]+iotherM[k]+iotherF[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+ninpM[k]+ninnM[k]+iotherM[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutpF[k]+noutnF[k]+ootherF[k]+ninpF[k]+ninnF[k]+iotherF[k]%></td>
                <td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+noutpF[k]+noutnF[k]+ootherF[k]+ninpM[k]+ninpF[k]+ninnM[k]+ninnF[k]+iotherM[k]+iotherF[k]%></td>
                
					</tr>
					<%}%>
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