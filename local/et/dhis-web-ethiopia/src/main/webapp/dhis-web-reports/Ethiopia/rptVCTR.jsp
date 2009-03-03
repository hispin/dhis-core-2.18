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
  

    int[] pretestM= new int[100];
	int[] pretestF= new int[100];
	int[] testM= new int[100];
	int[] testF= new int[100];
	int[] positiveM= new int[100];
	int[] positiveF= new int[100];
	int[] posttestM= new int[100];
	int[] posttestF= new int[100];
	int[] HIVprM= new int[100];
	int[] HIVprF= new int[100];
	
	boolean nextde=true;
	
	String[] destring1={"2703","2705","2707","2709","2711","2713","2715","2717","2719","2721"};
	String[] destring2={"2704","2706","2708","2710","2712","2714","2716","2718","2720","2722"};
	String[] destring3={"2723","2725","2727","2729","2731","2733","2735","2737","2739","2741"};
	String[] destring4={"2724","2726","2728","2730","2732","2734","2736","2738","2740","2742"};
	String[] destring5={"2743","2745","2747","2749","2751","2753","2755","2757","2759","2761"};
	String[] destring6={"2744","2746","2748","2750","2752","2754","2756","2758","2760","2762"};
	String[] destring7={"2763","2765","2767","2769","2771","2773","2775","2777","2779","2781"};
	String[] destring8={"2764","2766","2768","2770","2772","2774","2776","2778","2780","2782"};
	String[] destring9={"2783","2785","2787","2789","2791","2793","2795","2797","2799","2801"};
	String[] destring10={"2784","2786","2788","2790","2792","2794","2796","2798","2800","2802"};
	
	String[] age= {"<15","15-19","20-24","25-29","30-34","35-39","40-44","45-49","50+","Unrecorded", "Total",};
	
	int i=0;
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
  		String sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2703,2705,2707,2709,2711,2713,2715,2717,2719,2721)";		
        rs1 = st1.executeQuery(sql);
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring1[i].equals(rs1.getString("dataelement")))
			{
            	pretestM[i]=rs1.getInt("value");
				nextde=false;
			}
			}				
            
            }
         }    	  			
           
		sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2704,2706,2708,2710,2712,2714,2716,2718,2720,2722)";		
        rs1 = st1.executeQuery(sql);  
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring2[i].equals(rs1.getString("dataelement")))
			{
            	pretestF[i]=rs1.getInt("value");				
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
 
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2723,2725,2727,2729,2731,2733,2735,2737,2739,2741)";		
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring3[i].equals(rs1.getString("dataelement")))
			{
            	testM[i]=rs1.getInt("value");				
				nextde=false;
			}
			}            
            }
         }  	  			
            
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2724,2726,2728,2730,2732,2734,2736,2738,2740,2742)";		
        rs1 = st1.executeQuery(sql); 
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring4[i].equals(rs1.getString("dataelement")))
			{
            	testF[i]=rs1.getInt("value");				
				nextde=false;    
			}
			}       
            }
         }   	  			
           
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2743,2745,2747,2749,2751,2753,2755,2757,2759,2761)";		
        rs1 = st1.executeQuery(sql); 
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring5[i].equals(rs1.getString("dataelement")))
			{
            	positiveM[i]=rs1.getInt("value");				
				nextde=false;
			}
			}            
            }
         }   	  			
            
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2744,2746,2748,2750,2752,2754,2756,2758,2760,2762)";		
        rs1 = st1.executeQuery(sql);  
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring6[i].equals(rs1.getString("dataelement")))
			{
            	positiveF[i]=rs1.getInt("value");				
				nextde=false;
			}
			}            
            }
         }  	  			
            
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		   sql+=" and dataelement in (2763,2765,2767,2769,2771,2773,2775,2777,2779,2781)";		
        rs1 = st1.executeQuery(sql);    	  			
          
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring7[i].equals(rs1.getString("dataelement")))
			{
            	posttestM[i]=rs1.getInt("value");				
				nextde=false;
			}
			}            
            }
         }  
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2764,2766,2768,2770,2772,2774,2776,2778,2780,2782)";		
        rs1 = st1.executeQuery(sql); 
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring8[i].equals(rs1.getString("dataelement")))
			{
            	posttestF[i]=rs1.getInt("value");				
				nextde=false;
			}
			}            
            }
         }   	  			
            
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2783,2785,2787,2789,2791,2793,2795,2797,2799,2801)";		
        rs1 = st1.executeQuery(sql); 
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring9[i].equals(rs1.getString("dataelement")))
			{
            	HIVprM[i]=rs1.getInt("value");				
				nextde=false;  
			}
			}          
            }
         }   	  			
            
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (2784,2786,2788,2790,2792,2794,2796,2798,2800,2802)";		
        rs1 = st1.executeQuery(sql);    	  			
          
          if (rs1.next())
        {
            for(i=0;i<10;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (destring10[i].equals(rs1.getString("dataelement")))
			{
            	HIVprF[i]=rs1.getInt("value");				
				nextde=false;
			}
			}
            }
         }  
         
		   for(int z=0;z<=9;z++){
				pretestM[10]+=pretestM[z];
				pretestF[10]+=pretestF[z];
				testM[10]+=testM[z];
				testF[10]+=testF[z];
				positiveM[10]+=positiveM[z];
				positiveF[10]+=positiveF[z];
				posttestM[10]+=posttestM[z];
				posttestF[10]+=posttestF[z];
				HIVprM[10]+=HIVprM[z];
				HIVprF[10]+=HIVprF[z];
		   }
		   
       } //try block end
      catch(Exception e)  { out.println("Loi cho ma gi day: "+e.getMessage());  }
      
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Voluntary Counseling and Testing (VCT) Report for <%=orgUnitName%></title>
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
                <td width="869" height="93" valign="top" class="header">Voluntary Counseling and Testing (VCT) Report for <%=orgUnitName%></td>
              </tr>
            </table></td>
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
          <td height="111" valign="top"> 
            <table width="100%" border="0" cellpadding="1" cellspacing="1" bordercolor="#000066">
              <!--DWLayoutTable-->
              <tr> 
                <td width="39" rowspan="2" valign="top" class="tableheader">Age </td>
                <td colspan="3" valign="top" class="tableheader">Pre-test counseled</td>
                <td height="41" colspan="3" valign="top" class="tableheader">Tested</td>
                <td height="41" colspan="3" valign="top" class="tableheader">Positive Cases</td>
                <td colspan="3" valign="top" class="tableheader">Post-test counseled (results received)</td>
                <td height="41" colspan="3" valign="top" class="tableheader">HIV Positive Referred</td>
                <td height="41" colspan="3" valign="top" class="tableheader">Total</td>
              </tr>
                    <tr>
                      <td width="21" height="23" valign="top" class="tableheader">M</td>                    
                      
                      <td width="23" valign="top" class="tableheader">F </td>                     
                      <td width="42" valign="top" class="tableheader">Total</td>                       
                      <td width="18" valign="top" class="tableheader">M</td>
                      <td width="19" valign="top" class="tableheader">F</td>
                      <td width="36" valign="top" class="tableheader">Total</td>
                      <td width="18" valign="top" class="tableheader">M</td>
                      <td width="20" valign="top" class="tableheader">F </td>
                      <td width="36" valign="top" class="tableheader">Total</td>
                      <td width="47" valign="top" class="tableheader">M</td>
                      <td width="35" valign="top" class="tableheader">F</td>
                      <td width="59" valign="top" class="tableheader">Total</td>
                      <td width="25" valign="top" class="tableheader">M</td>
                      <td width="24" valign="top" class="tableheader">F</td>
                      <td width="45" valign="top" class="tableheader">Total</td>
                      <td width="18" valign="top" class="tableheader">M</td>
                      <td width="18" valign="top" class="tableheader">F</td>
                      <td width="28" valign="top" class="tableheader">GT</td>
                    </tr>
                  <% for (int k=0;k<11;k++){%>
                    <tr bordercolor="#000000" class="tableitem">
                      <td width="39" height="32" valign="top" class="tableheader"><%=age[k]%>&nbsp;</td>
                      <%if (k==10) {%><td width="21" valign="top" class="tableheader"><%=pretestM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="23" valign="top" class="tableitem"><%=pretestM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="42" valign="top" class="tableheader"><%=pretestF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="18" valign="top" class="tableitem"><%=pretestF[k]%>&nbsp;</td><%}%>
                      <td width="19" valign="top" class="tableheader"><%=pretestM[k]+pretestF[k]%>&nbsp;</td>
                      <%if (k==10) {%><td width="36" valign="top" class="tableheader"><%=testM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="18" valign="top" class="tableitem"><%=testM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="20" valign="top" class="tableheader"><%=testF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="36" valign="top" class="tableitem"><%=testF[k]%>&nbsp;</td><%}%>
                      
                <td width="47" valign="top" class="lefttableheader"><%=testM[k]+testF[k]%></td>
                      <%if (k==10) {%>
                <td width="35" valign="top" class="tableheader"><%=positiveM[k]%></td>
                <%}%>
                      <%if (k!=10) {%><td width="59" valign="top" class="tableitem"><%=positiveM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="25" valign="top" class="tableheader"><%=positiveF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="24" valign="top" class="tableitem"><%=positiveF[k]%>&nbsp;</td><%}%>
                      <td width="45" valign="top" class="tableheader"><%=positiveM[k]+positiveF[k]%>&nbsp;</td>
                      <%if (k==10) {%><td width="18" valign="top" class="tableheader"><%=posttestM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="18" valign="top" class="tableitem"><%=posttestM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="28" valign="top" class="tableheader"><%=posttestF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="18" valign="top" class="tableitem"><%=posttestF[k]%>&nbsp;</td><%}%>
                      <td width="23" valign="top" class="tableheader"><%=posttestM[k]+posttestF[k]%>&nbsp;</td>
                      <%if (k==10) {%><td width="19" valign="top" class="tableheader"><%=HIVprM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="19" valign="top" class="tableitem"><%=HIVprM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="18" valign="top" class="tableheader"><%=HIVprF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="18" valign="top" class="tableitem"><%=HIVprF[k]%>&nbsp;</td><%}%>
                      <td width="18" valign="top" class="tableheader"><%=HIVprM[k]+HIVprF[k]%>&nbsp;</td>
                      <td width="19" valign="top" class="tableheader"><%=pretestM[k]+testM[k]+positiveM[k]+posttestM[k]+HIVprM[k]%></td>
                      <td width="18" valign="top" class="tableheader"><%=pretestF[k]+testF[k]+positiveF[k]+posttestF[k]+HIVprF[k]%></td>
                      <td width="40" valign="top" class="tableheader"><%=pretestM[k]+pretestF[k]+testM[k]+testF[k]+positiveM[k]+positiveF[k]+posttestM[k]+posttestF[k]+HIVprM[k]+HIVprF[k]%>&nbsp;</td>
                    </tr>
					<%}%>
            </table></td>
			
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
    <td height="78"> <table width="100%">
              <tr>
                <td height="19" width="325"><p style="line-height: 100%"><b><i>
                <font face="Tahoma" size="1">Referrals for:-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </font>
                </i>
                </b>
                </p>
                <p style="line-height: 100%"><b><i>
                <font face="Tahoma" size="1">&nbsp;&nbsp;&nbsp;clinical Care&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
                Follow-up Counseling&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ANC</font></i></b></p>
                <p style="line-height: 100%"><b><i>
                <font size="1" face="Tahoma">&nbsp;&nbsp;TB Follow-up&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Spiritual Counseling&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </font></i>
                </b><i>
                <font face="Tahoma"><b><font size="1" face="Tahoma">Orphanage&nbsp;
                </font> </b></font></i></p>
                <p style="line-height: 100%"><b><i>
                <font face="Tahoma" size="1"><font face="Tahoma">&nbsp; </font>
                <font face="Tahoma">STI</font><font face="Tahoma"> Follow-up&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Home based Care&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
                Other (specify)</font></font></i></b></p>
                <p style="line-height: 100%"><b><i>
                <font size="1" face="Tahoma">&nbsp;&nbsp;Family Planning&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Financial Support&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </font></i>
                </b><i>
                <b>
                <font size="1" face="Tahoma">Total</font></b></i></p>
                <p style="line-height: 100%"><b><i>
                <font face="Tahoma">&nbsp;&nbsp;</font></i></b></p>
    <td width="310" height="1"></td>
    <td height="1" width="3"></td>
  </tr>
      </table></td>

</table>
</body>
</html>