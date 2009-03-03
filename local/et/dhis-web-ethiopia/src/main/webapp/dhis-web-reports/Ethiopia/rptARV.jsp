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
<title>VCT Report for <%=orgUnitName%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="styles.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
.style10 {font-family: Geneva, Arial, Helvetica, sans-serif}
-->
</style>
</head>

<body>
<table width="559" border="0" cellpadding="1" cellspacing="1" height="680">
  <!--DWLayoutTable-->
  <tr> 
    <td colspan="2" valign="top" height="590" width="561">
    <table width="544" border="0" cellpadding="1" cellspacing="1" height="554">
        <!--DWLayoutTable-->
        <tr> 
          <td width="560" height="60" valign="top">
          <table width="556" border="0" cellpadding="1" cellspacing="0">
              <!--DWLayoutTable-->
              <tr class="header"> 
                <td width="554" height="54" valign="top"  >Voluntary Counseling and Testing (VCT) Service by Health Facility </td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="47" valign="top" width="560">
          <table width="556" border="1" cellpadding="0" cellspacing="0" bordercolor="#FFFFFF" class="header1" style="border-collapse: collapse" >
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC" class="header1"> 
                <td width="137" height="47" valign="top" class="header1" bgcolor="#008080" bordercolor="#FFFFFF"><div align="left">
                  Region
                    
                </div>                </td>
                <td width="137" class="header1" bgcolor="#008080" bordercolor="#FFFFFF"><%=parentName%>&nbsp;</td>
                <td width="137" valign="top" class="header1" bgcolor="#008080" bordercolor="#FFFFFF" >
                OrgUnit</td>
                <td width="130" bgcolor="#008080" bordercolor="#FFFFFF"><%=orgUnitName%>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="55" valign="top" width="560">
          <table width="556" border="1" cellpadding="0" cellspacing="0" class="header1" style="border-collapse: collapse" bordercolor="#FFFFFF">
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC"> 
                <td width="118" height="55" valign="top" class="header1" bgcolor="#008080"><div align="left">Period</div></td>
                <td width="117" valign="top" class="header1" bgcolor="#008080">From</td>
                <td width="111" bgcolor="#008080"><%=startDate%>&nbsp;</td>
                <td width="114" valign="top" class="header1" bgcolor="#008080">To</td>
                <td width="77" valign="top" class="header1" bgcolor="#008080">
                <%=endDate%>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="380" valign="top" width="560">
          <table width="89%" border="0" cellpadding="0" cellspacing="0" height="75">
              <!--DWLayoutTable-->
              <tr> 
                <td width="555" height="93" valign="top">
                <table width="556" height="92" border="1" cellpadding="0" cellspacing="0" bgcolor="#000066" style="border-collapse: collapse" bordercolor="#000099">
                    <!--DWLayoutTable-->
                    <tr class="tableheader">
                      <td width="58" height="37" rowspan="2" valign="top" class="tableheader">Age</td>
                      <td height="33" colspan="3" valign="top" class="tableheader" width="82">pretest Counseled </td>
                      <td colspan="3" valign="top" class="tableheader" width="83">Tested </td> 
                      <td colspan="3" valign="top" class="tableheader" width="81">Positive Case </td>
                      <td colspan="3" valign="top" class="tableheader" width="83">Post test counseled</td>
                      <td colspan="3" valign="top" class="tableheader" width="86">HIV positive Referred</td>
                      </td> 
                      <td colspan="3" valign="top" class="tableheader" width="61">
                      Total</td>
                    </tr>
                    <tr>
                      <td width="19" height="23" valign="top" class="tableheader">M</td>                    
                      
                      <td width="23" valign="top" class="tableheader">F </td>                     
                      <td width="34" valign="top" class="tableheader">Total</td>                       
                      <td width="20" valign="top" class="tableheader">M</td>
                      <td width="23" valign="top" class="tableheader">F</td>
                      <td width="34" valign="top" class="tableheader">Total</td>
                      <td width="24" valign="top" class="tableheader">M</td>
                      <td width="18" valign="top" class="tableheader">F </td>
                      <td width="33" valign="top" class="tableheader">Total</td>
                      <td width="23" valign="top" class="tableheader">M</td>
                      <td width="21" valign="top" class="tableheader">F</td>
                      <td width="33" valign="top" class="tableheader">Total</td>
                      <td width="23" valign="top" class="tableheader">M</td>
                      <td width="19" valign="top" class="tableheader">F</td>
                      <td width="17" valign="top" class="tableheader">Total</td>
                      <td width="23" valign="top" class="tableheader">M</td>
                      <td width="22" valign="top" class="tableheader">F</td>
                      <td width="37" valign="top" class="tableheader">GT</td>
                    </tr>
                  <% for (int k=0;k<10;k++){%>
                    <tr bordercolor="#000000" class="tableitem">
                      <%if (k==10) {%><td width="58" height="32" valign="top" class="tableheader"><%=age[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="58" height="32" valign="top" class="tableitem"><%=age[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="19" valign="top" class="tableheader"><%=pretestM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="19" valign="top" class="tableitem"><%=pretestM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="23" valign="top" class="tableheader"><%=pretestF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="23" valign="top" class="tableitem"><%=pretestF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="34" valign="top" class="tableheader"><%=pretestM[k]+pretestF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="34" valign="top" class="tableitem"><%=pretestM[k]+pretestF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="20" valign="top" class="tableheader"><%=testM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="20" valign="top" class="tableitem"><%=testM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="23" valign="top" class="tableheader"><%=testF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="23" valign="top" class="tableitem"><%=testF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="34" valign="top" class="tableheader"><%=testM[k]+testF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="34" valign="top" class="tableitem"><%=testM[k]+testF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="24" valign="top" class="tableheader"><%=positiveM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="24" valign="top" class="tableitem"><%=positiveM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="18" valign="top" class="tableheader"><%=positiveF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="18" valign="top" class="tableitem"><%=positiveF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="33" valign="top" class="tableheader"><%=positiveM[k]+positiveF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="33" valign="top" class="tableitem"><%=positiveM[k]+positiveF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="23" valign="top" class="tableheader"><%=posttestM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="23" valign="top" class="tableitem"><%=posttestM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="21" valign="top" class="tableheader"><%=posttestF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="21" valign="top" class="tableitem"><%=posttestF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="33" valign="top" class="tableheader"><%=posttestM[k]+posttestF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="33" valign="top" class="tableitem"><%=posttestM[k]+posttestF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="23" valign="top" class="tableheader"><%=HIVprM[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="23" valign="top" class="tableitem"><%=HIVprM[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="19" valign="top" class="tableheader"><%=HIVprF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="19" valign="top" class="tableitem"><%=HIVprF[k]%>&nbsp;</td><%}%>
                      <%if (k==10) {%><td width="17" valign="top" class="tableheader"><%=HIVprM[k]+HIVprF[k]%>&nbsp;</td><%}%>
                      <%if (k!=10) {%><td width="17" valign="top" class="tableitem"><%=HIVprM[k]+HIVprF[k]%>&nbsp;</td><%}%>
                      <td width="23" valign="top" class="tableheader"><%=pretestM[k]+testM[k]+positiveM[k]+posttestM[k]+HIVprM[k]%></td>
                      <td width="22" valign="top" class="tableheader"><%=pretestF[k]+testF[k]+positiveF[k]+posttestF[k]+HIVprF[k]%></td>
                      <td width="37" valign="top" class="tableheader"><%=pretestM[k]+pretestF[k]+testM[k]+testF[k]+positiveM[k]+positiveF[k]+posttestM[k]+posttestF[k]+HIVprM[k]+HIVprF[k]%>&nbsp;</td>
                    </tr>
					<%}%>
                  </table>
              </tr>
              <tr> 
                <td height="1" valign="top"><table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#000066">
                    <!--DWLayoutTable-->
					
                
              </tr>
            </table></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
                  </table></td>
              </tr>
              <tr>
                <td height="19"><p style="line-height: 100%"><b><i>
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
    <td width="539" height="1"></td>
    <td height="1" width="19"></td>
  </tr>
</table>
</body>
</html>