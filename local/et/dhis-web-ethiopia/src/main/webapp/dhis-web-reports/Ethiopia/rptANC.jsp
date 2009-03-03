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
  

    int[] cARVM= new int[100];
	int[] cARVF= new int[100];
	int[] sARVM= new int[100];
	int[] sARVF= new int[100];
	int[] dARVM= new int[100];
	int[] dARVF= new int[100];
	int total=0;
	
	boolean nextde=true;
	
	/*String[] destring1={"2703","2705","2707","2709","2711","2713","2715","2717","2719","2721"};
	String[] destring2={"2704","2706","2708","2710","2712","2714","2716","2718","2720","2722"};
	String[] destring3={"2723","2725","2727","2729","2731","2733","2735","2737","2739","2741"};
	String[] destring4={"2724","2726","2728","2730","2732","2734","2736","2738","2740","2742"};
	String[] destring5={"2743","2745","2747","2749","2751","2753","2755","2757","2759","2761"};
	String[] destring6={"2744","2746","2748","2750","2752","2754","2756","2758","2760","2762"};
	String[] destring7={"2763","2765","2767","2769","2771","2773","2775","2777","2779","2781"};
	String[] destring8={"2764","2766","2768","2770","2772","2774","2776","2778","2780","2782"};
	String[] destring9={"2783","2785","2787","2789","2791","2793","2795","2797","2799","2801"};
	String[] destring10={"2784","2786","2788","2790","2792","2794","2796","2798","2800","2802"};*/
	int [] deint1={140,158,142,144,146,148,150,152,154,156,160};
	int [] deint2={139,157,141,143,145,147,149,151,153,155,159};
	int [] deint3={162,180,164,166,168,170,172,174,176,178,182};
	int [] deint4={161,179,163,165,167,169,171,173,175,177,181};
	int [] deint5={184,202,186,188,190,192,194,196,198,200,204};
	int [] deint6={183,201,185,187,189,191,193,195,197,199,203};
	
	String[] age= {"0-4","5-14","15-19","20-24","25-29","30-34","35-39","40-44","45-49","50+","Age Unspecified", "Total",};
	
	int i=0;
     try
      {
        Class.forName ("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (urlForConnection, userName, password);
        st1=con.createStatement();
  		String sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (140,158,142,144,146,148,150,152,154,156,160)";		
        rs1 = st1.executeQuery(sql);
          if (rs1.next())
        {
            for(i=0;i<11;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint1[i]==(rs1.getInt("dataelement")))
			{
            	cARVM[i]=rs1.getInt("value");
				nextde=false;
			}
			}				
            
            }
         }    	  			
           
		sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (139,157,141,143,145,147,149,151,153,155,159)";		
        rs1 = st1.executeQuery(sql);  
          if (rs1.next())
        {
            for(i=0;i<11;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint2[i]==(rs1.getInt("dataelement")))
			{
            	cARVF[i]=rs1.getInt("value");				
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
		sql+=" and dataelement in (162,180,164,166,168,170,172,174,176,178,182)";		
        rs1 = st1.executeQuery(sql); 
          if (rs1.next())
        {
            for(i=0;i<11;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint3[i]==(rs1.getInt("dataelement")))
			{
            	sARVM[i]=rs1.getInt("value");				
				nextde=false;    
			}
			}       
            }
         }   	  			
           
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (161,179,163,165,167,169,171,173,175,177,181)";		
        rs1 = st1.executeQuery(sql); 
          if (rs1.next())
        {
            for(i=0;i<11;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint4[i]==(rs1.getInt("dataelement")))
			{
            	sARVF[i]=rs1.getInt("value");				
				nextde=false;    
			}
			}       
            }
         }   	  			
            
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (184,202,186,188,190,192,194,196,198,200,204)";		
        rs1 = st1.executeQuery(sql); 
          if (rs1.next())
        {
            for(i=0;i<11;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint5[i]==(rs1.getInt("dataelement")))
			{
            	dARVM[i]=rs1.getInt("value");				
				nextde=false;
			}
			}            
            }
         }   	  			
            
		   sql = "select value,dataelement from datavalue where period="+periodId+" and source="+orgUnitId ;
		sql+=" and dataelement in (183,201,185,187,189,191,193,195,197,199,203)";		
        rs1 = st1.executeQuery(sql);  
          if (rs1.next())
        {
            for(i=0;i<11;i++)
            {
			rs1.beforeFirst();
			nextde=true;
			while (rs1.next() && nextde)
			{
			if (deint6[i]==(rs1.getInt("dataelement")))
			{
            	dARVF[i]=rs1.getInt("value");				
				nextde=false;
			}
			}            
            }
         }  	  			
            
         
		   for(int z=0;z<=10;z++){
				cARVM[11]+=cARVM[z];
				cARVF[11]+=cARVF[z];
				sARVM[11]+=sARVM[z];
				sARVF[11]+=sARVF[z];
				dARVM[11]+=dARVM[z];
				dARVF[11]+=dARVF[z];
		   }
		   
		   total=cARVM[11]+cARVF[11]+sARVM[11]+sARVF[11]+dARVM[11]+dARVF[11];
		   
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
          <table width="568" border="0" cellpadding="1" cellspacing="0">
              <!--DWLayoutTable-->
              <tr class="header"> 
                <td width="566" height="54" valign="top"  >Anti-Retroviral (ARV) Therapy for <%=orgUnitName%> </td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="47" valign="top" width="560">
          <table width="569" border="1" cellpadding="0" cellspacing="0" bordercolor="#FFFFFF" class="header1" style="border-collapse: collapse" >
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC" class="header1"> 
                <td width="137" height="47" valign="top" class="header1" bgcolor="#008080" bordercolor="#FFFFFF"><div align="left">
                  Region
                    
                </div>                </td>
                <td width="137" class="header1" bgcolor="#008080" bordercolor="#FFFFFF"><%=parentName%>&nbsp;</td>
                <td width="137" valign="top" class="header1" bgcolor="#008080" bordercolor="#FFFFFF" >
                OrgUnit</td>
                <td width="143" bgcolor="#008080" bordercolor="#FFFFFF"><%=orgUnitName%>&nbsp;</td>
              </tr>
            </table></td>
        </tr>
        <tr> 
          <td height="55" valign="top" width="560">
          <table width="569" border="1" cellpadding="0" cellspacing="0" class="header1" style="border-collapse: collapse" bordercolor="#FFFFFF">
              <!--DWLayoutTable-->
              <tr bgcolor="#FFCCCC"> 
                <td width="118" height="55" valign="top" class="header1" bgcolor="#008080"><div align="left">Period</div></td>
                <td width="117" valign="top" class="header1" bgcolor="#008080">From</td>
                <td width="111" bgcolor="#008080"><%=startDate%>&nbsp;</td>
                <td width="114" valign="top" class="header1" bgcolor="#008080">To</td>
                <td width="90" valign="top" class="header1" bgcolor="#008080">
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
                <table width="570" height="92" border="1" cellpadding="0" cellspacing="0" bgcolor="#000066" style="border-collapse: collapse" bordercolor="#000099">
                    <!--DWLayoutTable-->
                    <tr class="tableheader">
                      <td width="58" height="62" rowspan="2" valign="top" class="tableheader">Age</td>
                      <td height="65" colspan="3" valign="top" class="tableheader" width="82">
                      Clients on ARV Treatment </td>
                      <td colspan="3" valign="top" class="tableheader" width="83" height="65">
                      Clients Starting ARV Treatment </td> 
                      <td colspan="3" valign="top" class="tableheader" width="81" height="65">
                      Clients Discontinued ARV Treatment</td>
                      </td> 
                      <td colspan="3" valign="top" class="tableheader" width="61" height="65">
                      Total</td>
                    </tr>
                    <tr>
                      <td width="19" height="13" valign="top" class="tableheader">Male</td>                    
                      <td width="23" valign="top" class="tableheader" height="13">Female </td>                     
                      <td width="34" valign="top" class="tableheader" height="13">Total</td>                       
                      <td width="20" valign="top" class="tableheader" height="13">Male</td>
                      <td width="23" valign="top" class="tableheader" height="13">Female</td>
                      <td width="34" valign="top" class="tableheader" height="13">Total</td>
                      <td width="24" valign="top" class="tableheader" height="13">Male</td>
                      <td width="18" valign="top" class="tableheader" height="13">Female </td>
                      <td width="33" valign="top" class="tableheader" height="13">Total</td>
                      <td width="38" valign="top" class="tableheader" height="13">Male Total</td>
                      <td width="18" valign="top" class="tableheader" height="13">Female Total</td>
                      <td width="33" valign="top" class="tableheader" height="13">Grand Total</td>
					
					<% for (int k=0;k<12;k++) {%>
					<tr>
                      <td width="19" height="13" valign="top" class="tableheader"><%=age[k]%></td>
					  <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=cARVM[k]%></td><%}%>
                      <%if (k!=11) {%><td width="19" height="13" valign="top" class="tableitem"><%=cARVM[k]%></td><%}%>
                      <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=cARVF[k]%></td><%}%>
                      <%if (k!=11) {%><td width="19" height="13" valign="top" class="tableitem"><%=cARVF[k]%></td><%}%>
                      <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=cARVM[k]+cARVF[k]%></td><%}%>
                      <%if (k!=11) {%><td width="18" valign="top" class="tableheader" height="13"><%=cARVM[k]+cARVF[k]%></td><%}%>
                      <%if (k==11) {%><td width="18" valign="top" class="tableheader" height="13"><%=sARVM[k]%></td><%}%>
                      <%if (k!=11) {%><td width="33" valign="top" class="tableitem" height="13"><%=sARVM[k]%></td><%}%>
                      <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=sARVF[k]%></td><%}%>
                      <%if (k!=11) {%><td width="33" height="13" valign="top" class="tableitem"><%=sARVF[k]%></td><%}%>
                      <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=sARVM[k]+sARVF[k]%></td><%}%>
                      <%if (k!=11) {%><td width="19" height="13" valign="top" class="tableheader"><%=sARVM[k]+sARVF[k]%></td><%}%>
                      <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=dARVM[k]%></td><%}%>
                      <%if (k!=11) {%><td width="19" height="13" valign="top" class="tableitem"><%=dARVM[k]%></td><%}%>
                      <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=dARVF[k]%></td><%}%>
                      <%if (k!=11) {%><td width="19" height="13" valign="top" class="tableitem"><%=dARVF[k]%></td><%}%>
                      <%if (k==11) {%><td width="19" height="13" valign="top" class="tableheader"><%=dARVM[k]+dARVF[k]%></td><%}%>
                      <%if (k!=11) {%><td width="19" height="13" valign="top" class="tableheader"><%=dARVM[k]+dARVF[k]%></td><%}%>
                      <td width="19" height="13" valign="top" class="tableheader"><%=cARVM[k]+sARVM[k]+dARVM[k]%></td>
                      <td width="19" height="13" valign="top" class="tableheader"><%=cARVF[k]+sARVF[k]+dARVF[k]%></td>
                      <td width="19" height="13" valign="top" class="tableheader"><%=cARVM[k]+sARVM[k]+dARVM[k]+cARVF[k]+sARVF[k]+dARVF[k]%></td>
					 </tr>
					 <% } %>
                    
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