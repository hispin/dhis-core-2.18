
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%@ page session="true" %>

<%

   Connection con=null;            
    Statement st1=null;
	Statement st2=null;
    ResultSet rs1=null;
	ResultSet rs2=null;      
  
    String userName = "root";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/dhis2";  
    
	int orgUnitId = 16;
	String orgUnitName="";
	String parentName="";
	int parentId=0;	
	int periodId = 204;
	String startDate="";
	String endDate="";   
OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
  

	
    int[] hfM= new int[100];
	int[] hfF= new int[100];
	int totHFM=0;
	int totHFF=0;
	
	
	int[] sM= new int[100];
	int[] sF= new int[100];
	int totSM=0;
	int totSF=0;
	
	int[] faF= new int[100];
	int totFAF=0;
	
	int[] yaM= new int[100];
	int[] yaF= new int[100];
	int totYAM=0;
	int totYAF=0;

	int[] fraM= new int[100];
	int[] fraF= new int[100];
	int totFRAM=0;
	int totFRAF=0;
	
	int[] psM= new int[100];
	int[] psF= new int[100];
	int totPSM=0;
	int totPSF=0;
	
	int[] oM= new int[100];
	int[] oF= new int[100];
	int totOM=0;
	int totOF=0;
	
	int[] um= new int[100];
	
	
	

	
	String[] topic= {"Malaria","HIV/AIDS","CDD","TB and Leprosy","Parasites","Nutrition","Use Of medicines","Personal Hygiene","Environmental Health","MCH","EPI","FP","Harmful Traditions","Meningitis","Others"};
	
	String[] sn= {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};

	boolean nextde=true;
	
	String[] destring1={"872","754","893","852","914","934","954","974","701","994","729","1014","1041","1054","816"};
	String[] destring2={"873","747","892","846","913","933","953","973","694","993","722","1013","1040","1053","808"};
	String[] destring3={"875","759","895","857","916","936","956","976","706","995","733","1016","1042","1056","820"};
	String[] destring4={"874","751","894","850","915","935","955","975","699","996","726","1015","1043","1055","814"};
	String[] destring5={"876","753","896","853","917","937","957","977","704","997","727","1017","1044","1057","811"};
	String[] destring6={"877","746","897","845","918","938","958","978","693","998","723","1018","1045","1058","807"};
	String[] destring7={"878","752","898","851","919","939","959","979","700","999","730","1019","1046","1059","815"};
	String[] destring8={"880","756","900","854","921","940","961","980","702","1000","728","1020","1047","1060","817"};
	String[] destring9={"879","748","899","847","920","941","960","981","695","1001","721","1021","1048","1061","809"};
	String[] destring10={"881","758","902","856","922","943","963","983","705","1003","732","1023","1049","1063","819"};
	String[] destring11={"882","750","901","849","923","942","962","982","698","1002","725","1022","1050","1062","813"};
	String[] destring12={"884","755","903","855","925","945","965","985","703","1004","731","1025","1052","1064","818"};
	String[] destring13={"883","749","904","848","924","944","964","984","696","1005","724","1024","1051","1065","810"};
	String[] destring14={"772","777","773","782","778","780","781","783","774","784","775","776","785","786","787"};
	

	int i=0;
	
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
		st2=con.createStatement();
  		String sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (872,754,893,852,914,934,954,974,701,994,729,1014,1041,1054,816)";		
        rs1 = st1.executeQuery(sql);  
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring1[i].equals(rs1.getString("dataelement")))
			{
            	
            	hfM[i]=rs1.getInt("value");				
				//System.out.print("YEAH");
				//if(!rs1.next()) break;
				nextde=false;
            
            }
			}
			
         
        }
		}
		
		sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (873,747,892,846,913,933,953,973,694,993,722,1013,1040,1053,808)";		
        rs1 = st1.executeQuery(sql); 

          
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring2[i].equals(rs1.getString("dataelement")))
			{
            	
            	hfF[i]=rs1.getInt("value");				
				//System.out.println("YEAH");
				//if(!rs1.next()) break;
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
		
		
		sql = "select name from organizationunit where id ="+parentId ;
		rs1=st1.executeQuery(sql);
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
		sql+=" and dataelement in (875,759,895,857,916,936,956,976,706,995,733,1016,1042,1056,820)";		
        rs1 = st1.executeQuery(sql);    	  			
         
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring3[i].equals(rs1.getString("dataelement")))
			{
            	
            	sM[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
   
		   
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (874,751,894,850,915,935,955,975,699,996,726,1015,1043,1055,814)";		
        rs1 = st1.executeQuery(sql);    	  			
         
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring4[i].equals(rs1.getString("dataelement")))
			{
            	
            	sF[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
   
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (876,753,896,853,917,937,957,977,704,997,727,1017,1044,1057,811)";		
        rs1 = st1.executeQuery(sql);    	  			
       
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring5[i].equals(rs1.getString("dataelement")))
			{
            	
            	faF[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
     
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (877,746,897,845,918,938,958,978,693,998,723,1018,1045,1058,807)";		
        rs1 = st1.executeQuery(sql);    	  			
          
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring6[i].equals(rs1.getString("dataelement")))
			{
            	
            	yaF[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
  
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		   sql+=" and dataelement in (878,752,898,851,919,939,959,979,700,999,730,1019,1046,1059,815)";		
        rs1 = st1.executeQuery(sql);    	  			
         
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring7[i].equals(rs1.getString("dataelement")))
			{
            	
            	yaM[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
   
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (880,756,900,854,921,940,961,980,702,1000,728,1020,1047,1060,817)";		
        rs1 = st1.executeQuery(sql);    	  			
          
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring8[i].equals(rs1.getString("dataelement")))
			{
            	
            	fraM[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
  
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;

		sql+=" and dataelement in (879,748,899,847,920,941,960,981,695,1001,721,1021,1048,1061,809)";		
        rs1 = st1.executeQuery(sql);    	  			
          
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring9[i].equals(rs1.getString("dataelement")))
			{
            	
            	fraF[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
  
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (881,758,902,856,922,943,963,983,705,1003,732,1023,1049,1063,819)";		
        rs1 = st1.executeQuery(sql);    	  			
         
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring10[i].equals(rs1.getString("dataelement")))
			{
            	
            	psM[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
   
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (882,750,901,849,923,942,962,982,698,1002,725,1022,1050,1062,813)";		
        rs1 = st1.executeQuery(sql);    	  			
       
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring11[i].equals(rs1.getString("dataelement")))
			{
            	
            	psF[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
     
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (884,755,903,855,925,945,965,985,703,1004,731,1025,1052,1064,818)";		
        rs1 = st1.executeQuery(sql);    	  			
         
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring12[i].equals(rs1.getString("dataelement")))
			{
            	
            	oM[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
   
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (883,749,904,948,924,944,964,984,696,1005,724,1024,1051,1065,810)";		
        rs1 = st1.executeQuery(sql);    	  			
         
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring13[i].equals(rs1.getString("dataelement")))
			{
            	
            	oF[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
  
		   sql = "select dataelement,value from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (772,777,773,782,778,780,781,783,774,784,775,776,785,786,787)";		
        rs1 = st1.executeQuery(sql);    	  			
          
    if (rs1.next())
        {
            for(i=0;i<15;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring14[i].equals(rs1.getString("dataelement")))
			{
            	
            	um[i]=rs1.getInt("value");				
				//System.out.print("have value");
				//if(!rs1.next()) break;
				nextde=false;
				}
				}
            
            }
         }
  
           
	for (i=0;i<15;i++)
	{
	totHFM+=hfM[i];
	totHFF+=hfF[i];
	totSM+=sM[i];
	totSF+=sF[i];
	totFAF+=faF[i];
	totYAM+=yaM[i];
	totYAF+=yaF[i];
	totFRAM+=fraM[i];
	totFRAF+=fraF[i];
	totPSM+=psM[i];
	totPSF+=psF[i];
	totOM+=oM[i];
	totOF+=oF[i];
	
	}
	
		  
		   
		   
       } 
      catch(Exception e)  { out.println("Loi cho ma gi day: "+e.getMessage());  }
      
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Health Education Report for <%=orgUnitName%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="styles.css" rel="stylesheet" type="text/css">

<!-- <style type="text/css">
<!--
.style10 {font-family: Geneva, Arial, Helvetica, sans-serif}

</style> -->
</head>

<body>
<table width="740" border="0" cellpadding="1" cellspacing="1" height="202">
  <!--DWLayoutTable-->
  <tr> 
    <td colspan="2" rowspan="2" valign="top" height="375" width="744">
    <table width="914" border="0" cellpadding="1" cellspacing="1" height="1">
        <!--DWLayoutTable-->
        <tr> 
          <td width="910" height="79" valign="top">
          <table width="743" border="0" cellspacing="0" height="78">
              <!--DWLayoutTable-->
              <tr class="header"> 
                <td width="910" height="76" valign="top"  >Monthly Activity 
                Report on Health Education for <%=orgUnitName%></td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="47" valign="top" width="910">
          <table width="743" border="1" cellpadding="0" cellspacing="0" bordercolor="#FFFFFF" class="header1" style="border-collapse: collapse" bgcolor="#008080" >
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC" class="header1"> 
                <td width="126" height="47" valign="top" class="header1" bgcolor="#008080" bordercolor="#000099"><div align="left">
                  District</div>                </td>
                <td width="124" class="header1" bgcolor="#008080" bordercolor="#000099"><%=parentName%>&nbsp;</td>
                <td width="126" valign="top" class="header1" bgcolor="#008080" bordercolor="#000099" >
                Health Institution</td>
                <td width="456" bgcolor="#008080" bordercolor="#000099"><%=orgUnitName%>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="55" valign="top" width="910">
          <table width="743" border="1" cellpadding="0" cellspacing="0" class="header1" style="border-collapse: collapse" bordercolor="#FFFFFF" bgcolor="#008080" height="52">
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC"> 
                <td width="110" height="52" valign="top" class="header1" bgcolor="#008080" bordercolor="#0000FF"><div align="left">Period</div></td>
                <td width="108" valign="top" class="header1" bgcolor="#008080" bordercolor="#0000FF" height="52">From</td>
                <td width="99" bgcolor="#008080" bordercolor="#0000FF" height="52"><%=startDate%>&nbsp;</td>
                <td width="104" valign="top" class="header1" bgcolor="#008080" bordercolor="#0000FF" height="52">To</td>
                <td width="408" valign="top" class="header1" bgcolor="#008080" bordercolor="#0000FF" height="52">
                <%=endDate%>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
                  
      
        <table width="89%" border="0" cellpadding="0" cellspacing="0" height="122">
              <!--DWLayoutTable-->
              <tr> 
                <td width="555" height="143" valign="top">
                
          <table width="743" height="60" border="1" cellpadding="0" cellspacing="0"  bgcolor="#000066">
            <!--DWLayoutTable-->
            <tr class="tableheader">
                                            </td> 
                    </tr>
                    <tr>
                      <td width="24" height="30" rowspan="2" valign="top" class="tableheader" bordercolor="#000080">
                      S.N</td>
                      <td width="65" height="30" rowspan="2" valign="top" class="tableheader" bordercolor="#000080">
                      Topic of Education</td>
                      <td width="55" height="1" valign="top" class="tableheader" colspan="3" bordercolor="#000080">
                      Health Facility<p>&nbsp;</td>                    
                      
                      <td width="54" valign="top" class="tableheader" height="1" colspan="3" bordercolor="#000080">
                      School</td>
                      
                      <td width="76" valign="top" class="tableheader" height="24" rowspan="2" bordercolor="#000080">
                      Females Association</td>
                      
                      <td width="76" valign="top" class="tableheader" height="1" colspan="3" bordercolor="#000080">
                      Youth Association</td>
                      
                      <td width="72" valign="top" class="tableheader" height="1" colspan="3" bordercolor="#000080">
                      Farmers<p>Association</td>
                      
                      <td width="52" valign="top" class="tableheader" height="1" colspan="3" bordercolor="#000080">
                      Prayer Sites</td>
					  
                      <td width="59" valign="top" class="tableheader" height="1" colspan="3" bordercolor="#000080">
                      Others</td>
					  
					  
                      <td width="54" valign="top" class="tableheader" height="12" colspan="3" bordercolor="#000080">
                      Total</td>
					  
					  
                      <td width="81" valign="top" class="tableheader" height="24" rowspan="2" bordercolor="#000080">
                      Used<p>Methodology<p>&nbsp;</td>
					  
                    </tr>
                    <tr>
                      <td width="20" height="23" valign="top" class="tableheader" bordercolor="#000080">
                      M</td>                    
                      
                      <td width="16" height="23" valign="top" class="tableheader" bordercolor="#000080">
                      F</td>                    
                      
                      <td width="17" height="23" valign="top" class="tableheader" bordercolor="#000080">
                      Total</td>                    
                      
                      <td width="18" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      M</td>
                      
                      <td width="17" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      F</td>
                      
                      <td width="17" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      Total</td>
                      
                      <td width="34" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      M</td>
                      
                      <td width="20" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      F</td>
                      
                      <td width="20" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      Total</td>
                      
                      <td width="37" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      M</td>
                      
                      <td width="18" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      F</td>
                      
                      <td width="15" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      Total</td>
                      
                      <td width="23" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      M</td>
                      
                      <td width="13" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      F</td>
                      
                      <td width="14" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      Total</td>
                      
                      <td width="20" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      M</td>
                      
                      <td width="19" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      F</td>
                      
                      <td width="18" valign="top" class="tableheader" height="23" bordercolor="#000080">
                      Total</td>
                      
					  
                      <td width="17" valign="top" class="tableheader" height="12" bordercolor="#000080">
                      M</td>
					  
					  
                      <td width="15" valign="top" class="tableheader" height="12" bordercolor="#000080">
                      F</td>
					  
					  
                      <td width="20" valign="top" class="tableheader" height="12" bordercolor="#000080">
                      Grand Total</td>
					  
                    </tr>
                  <% for (int k=0;k<15;k++){%>
                  <tr bordercolor="#000000" class="tableitem">
                      <td width="22" height="30" valign="top" class="tableitem" bordercolor="#000080"><%=sn[k]%>&nbsp;</td>
					  <td width="63" height="30" valign="top" class="tableitem" bordercolor="#000080"><%=topic[k]%>&nbsp;</td>
                      <td width="18" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=hfM[k]%>&nbsp;</td>
                      <td width="14" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=hfF[k]%>&nbsp;</td>
                      <td width="15" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=hfM[k]+hfF[k]%>&nbsp;</td>
                      <td width="16" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=sM[k]%>&nbsp;</td>
                      <td width="15" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=sF[k]%>&nbsp;</td>
                      <td width="15" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=sM[k]+sF[k]%>&nbsp;</td>
                      <td width="74" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=faF[k]%></td>
                      <td width="32" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=yaM[k]%>&nbsp;</td>
                      <td width="18" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=yaF[k]%>&nbsp;</td>
                      <td width="18" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=yaM[k]+yaF[k]%>&nbsp;</td>
                      <td width="35" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=fraM[k]%>&nbsp;</td>
                      <td width="16" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=fraF[k]%>&nbsp;</td>
                      <td width="13" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=fraM[k]+fraF[k]%>&nbsp;</td>
                      <td width="21" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=psM[k]%>&nbsp;</td>
                      <td width="11" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=psF[k]%>&nbsp;</td>
                      <td width="12" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=psM[k]+psF[k]%>&nbsp;</td>
                      <td width="18" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=oM[k]%>&nbsp;</td>
                      <td width="17" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=oF[k]%>&nbsp;</td>
               		  <td width="16" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=oM[k]+oF[k]%>&nbsp;</td>
					  <td width="15" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=hfM[k]+sM[k]+yaM[k]+fraM[k]+psM[k]+oM[k]%>&nbsp;</td>
					  <td width="13" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=hfF[k]+sF[k]+faF[k]+yaF[k]+fraF[k]+psF[k]+oF[k]%>&nbsp;</td>
					  
              <td width="18" valign="top" class="tableheader" height="30" bordercolor="#000080"><%=hfM[k]+hfF[k]+sM[k]+sF[k]+faF[k]+yaM[k]+yaF[k]+fraM[k]+fraF[k]+psM[k]+psF[k]+oM[k]+oF[k]%>&nbsp;</td>
                      <td width="59" valign="top" class="tableitem" height="30" bordercolor="#000080"><%=um[k]%></td>
                    </tr>

					<%}%>
              <tr> 
                <td class="none"></td>
				<td class="tableheader" width="65" >Total</td>
                <td class="tableheader" width="20" style="background-color: #4040FF" bordercolor="#000080"><%=totHFM%>&nbsp;</td>
                <td class="tableheader" width="16" style="background-color: #4040FF" bordercolor="#000080"><%=totHFF%>&nbsp;</td>
                <td class="tableheader" width="17" style="background-color: #4040FF" bordercolor="#000080"><%=totHFM + totHFF%>&nbsp;</td>
                <td class="tableheader" width="18" style="background-color: #4040FF" bordercolor="#000080"><%=totSM%>&nbsp;</td>
                <td class="tableheader" width="17" style="background-color: #4040FF" bordercolor="#000080"><%=totSF%>&nbsp;</td>
                <td class="tableheader" width="17" style="background-color: #4040FF" bordercolor="#000080"><%=totSM + totSF%>&nbsp;</td>
                
              <td class="tableheader" width="76" style="background-color: #4040FF" bordercolor="#000080"><%=totFAF%>&nbsp;</td>
                <td class="tableheader" width="34" style="background-color: #4040FF" bordercolor="#000080"><%=totYAM%>&nbsp;</td>
                <td class="tableheader" width="20" style="background-color: #4040FF" bordercolor="#000080"><%=totYAF%>&nbsp;</td>
                <td class="tableheader" width="20" style="background-color: #4040FF" bordercolor="#000080"><%=totYAM + totYAF%>&nbsp;</td>
                <td class="tableheader" width="37" style="background-color: #4040FF" bordercolor="#000080"><%=totFRAM%>&nbsp;</td>
                <td class="tableheader" width="18" style="background-color: #4040FF" bordercolor="#000080"><%=totFRAF%>&nbsp;</td>
                <td class="tableheader" width="15" style="background-color: #4040FF" bordercolor="#000080"><%=totFRAM + totFRAF%>&nbsp;</td>
                <td class="tableheader" width="23" style="background-color: #4040FF" bordercolor="#000080"><%=totPSM%>&nbsp;</td>
                <td class="tableheader" width="13" style="background-color: #4040FF" bordercolor="#000080"><%=totPSF%>&nbsp;</td>
                <td class="tableheader" width="14" style="background-color: #4040FF" bordercolor="#000080"><%=totPSM + totPSF%>&nbsp;</td>
                <td class="tableheader" width="20" style="background-color: #4040FF" bordercolor="#000080"><%=totOM%>&nbsp;</td>
                <td class="tableheader" width="19" style="background-color: #4040FF" bordercolor="#000080"><%=totOF%>&nbsp;</td>
                <td class="tableheader" width="18" style="background-color: #4040FF" bordercolor="#000080"><%=totOM + totOF%>&nbsp;</td>
				<td class="tableheader" width="17" style="background-color: #4040FF" bordercolor="#000080"><%=totHFM+totSM+totYAM+totFRAM+totPSM+totOM%>&nbsp;</td>
				<td class="tableheader" width="15" style="background-color: #4040FF" bordercolor="#000080"><%=totHFF+totSF+totFAF+totYAF+totFRAF+totPSF+totOF%>&nbsp;</td>
				<td class="tableheader" width="20" style="background-color: #4040FF" bordercolor="#000080"><%=totHFM+totHFF+totSM+totSF+totFAF+totYAM+totYAF+totFRAM+totFRAF+totPSM+totPSF+totOM + totOF%>&nbsp;</td>
                <td  class="none">&nbsp;</td>
				
              </tr>
                  </table>
              </tr>
              <tr> 
                <td height="1" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#000066">
                    <!--DWLayoutTable-->
					
                  </table></td>
              </tr>
              </table></td>
        </tr>
      </table></td>
    <td height="352" width="1"></td>
  </tr>
  <tr> 
    <td height="20" width="1"></td>
  </tr>
  <tr> 
    <td width="716" height="1"></td>
    <td height="1" width="25"></td>
    <td height="1" width="1"></td>
  </tr>
</table>
</body>
</html>