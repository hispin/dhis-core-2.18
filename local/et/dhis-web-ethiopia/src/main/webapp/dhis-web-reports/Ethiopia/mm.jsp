<html>
<head>
<%@ page 
	import = "java.io.*"
	import = "java.lang.*"
	import = "java.sql.*" 
	import = "org.hisp.dhis.service.*"
	import = "com.opensymphony.xwork.util.OgnlValueStack" 
%>
<title>
Morbidity - Mortality Form
</title>
<script language="JavaScript1.2">

function submitForm(form,stype)
{
  form.SubmitType.value=stype;
  form.recType.value=frmRecType.rectype.value;
  form.orgUnit.value=form.orgUnit.value;
  form.dataPeriod.value=form.dataPeriod.value;
  form.submit();
}

</script>
<link href="styles.css" rel="stylesheet" type="text/css">
</head>
<body>
<h1 style="color:996633">Morbidity - Mortality </h1>
<table cellpadding="2" cellspacing="2" >
<tr>
<td class="header1">
<%
    //To fill recType from the database and automaticaly set the current value
      String orgUnit="aa Abo liyu Clinic";
      String dataPeriod="2005-12-01 00:00:00";
      
      
	  
    try{ 
	orgUnit = request.getParameter("orgUnit");
	dataPeriod = request.getParameter("dataPeriod");
    }catch(NumberFormatException e){
        
    
      int orgUnitId = 6;
      int periodId = 203;
	  	
	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  orgUnitId = Integer.parseInt( selectedId );
	  
	  String selectedPeriodId = (String) stack.findValue( "periodSelect" );
	  periodId = Integer.parseInt( selectedPeriodId );
	    
      Connection con = null;
	  Database d = new Database();
      ResultSet rs = null;
      String sql = "";  
          
         
      try{  
        con=d.mysqlConnect();
 
        sql= "select id,name from  organizationunit where ID=" + orgUnitId;    
        rs = d.getRecordset(con,sql);
        
         
        if (rs.next()){
			orgUnit=rs.getString("name");
			}
		
		sql= "select startdate,enddate from period where id=" + periodId;    
        rs = d.getRecordset(con,sql);
        
        if (rs.next()){
			dataPeriod=rs.getString("startdate") ;
		}
	   }catch(Exception err){out.println("Error selecting OrgUnit and DataPeriod!");}	
       
    }
    
    out.println("<form name=\"frmRecType\" method=\"post\" action=\"/dhis-web-reports/Ethiopia/mm.jsp\">");	
	MonthlyMMData m= new MonthlyMMData();
	String [][] MMType;
	int FormType=1;
    int RecType=1;
	int i=0;
	MMType = m.getMMType();

	try{ 
	RecType = java.lang.Integer.parseInt(request.getParameter("recType"));
    }catch(NumberFormatException e){}
	
    out.println("<p>MM Category(rec type)");   
	out.println("<select name='rectype'>");
		 for(i=0;i<MMType.length;i++){
		   String selectedMMType=""; 
    	   if(java.lang.Integer.parseInt(MMType[i][0])==RecType){selectedMMType="Selected";}
		   out.println("<option value='" + MMType[i][0] + "' " + selectedMMType + " >" + MMType[i][1] + "</option>");
		   }
	out.println("</select>");
	out.println("<a href=\"javascript: submitForm(frmRecType ,  0)\" >load</a></p>");
	out.println("<input type='hidden' name='SubmitType' value='0' >");
	out.println("<input type='hidden' name='recType' >");
	out.println("<input type='hidden' name='orgUnit' >");
	out.println("<input type='hidden' name='dataPeriod' >");
    out.println("</form>");
%>
</td>
</tr>
</table>
<table cellpadding="2" cellspacing="2" >
<tr>
<td rowspan="2" align="center" valign="center" class="tableheader">ICD</td>
<td colspan="6" align="center" class="tableheader" >Female</td>
<td colspan="6" align="center" class="tableheader">Male</td>
<%
if(RecType==1 || RecType==4)
{

out.print("<td colspan='2' align='center' class='tableheader' >Repeat</td>");
}
if(RecType==2)
 {  
out.print("<td colspan='2' align='center' class='tableheader'>Cured</td>");
out.print("<td colspan='2' align='center' class='tableheader' >TLS</td>");
out.print("<td colspan='2' align='center' class='tableheader'>Improved</td>");
out.print("<td colspan='2' align='center' class='tableheader'>Not Improved</td>");
out.print("<td colspan='2' align='center' class='tableheader'>Worse</td>");
out.print("<td colspan='2' align='center' class='tableheader'>Dead</td>");
}
%>
</tr>	
<tr >
  
  <td align="center" class="tableheader">&lt;1</td>
  <td align="center" class="tableheader">1-4</td>
  <td align="center" class="tableheader">5-14</td>
  <td align="center" class="tableheader">15-45</td>
  <td align="center" class="tableheader">45-64</td>
  <td align="center" class="tableheader">&gt;64</td>
  <td align="center" class="tableheader">&lt;1</td>
  <td align="center" class="tableheader">1-4</td>
  <td align="center" class="tableheader">5-14</td> 
  <td align="center" class="tableheader">15-45</td>
  <td align="center" class="tableheader">45-64</td>
  <td align="center" class="tableheader">&gt;60</td>
<%
if(RecType==1 || RecType==4)
{
  out.print("<td class='tableheader'>F</td>");
  out.print("<td class='tableheader'>M</td>");
}
if(RecType==2)
 {  
out.print("  <td align='center' class='tableheader'>F</td>");
out.print("  <td align='center' class='tableheader'>M</td>");
out.print("  <td align='center' class='tableheader'>F</td>");
out.print("  <td align='center' class='tableheader'>M</td>");
out.print("  <td align='center' class='tableheader'>F</td>");
out.print("  <td align='center' class='tableheader'>M</td>");
out.print("  <td align='center' class='tableheader'>F</td>");
out.print("  <td align='center' class='tableheader'>M</td>");
out.print("  <td align='center' class='tableheader'>F</td>");
out.print("  <td align='center' class='tableheader'>M</td>");
out.print("  <td align='center' class='tableheader'>F</td>");
out.print("  <td align='center' class='tableheader'>M</td>");
 }
%>
  <td align="center" class="tableheader">Save</td>
  <td align="center" class="tableheader">Delete</td>

</tr>

<% 
	String [][] data;
	String [][] ICD;
	
	String ICDCode="";
	int [] input=new int[26];
	int count=0;
	int SubmitType=0;
	long Lastupdated=36888;
	String LastUser="sisay";
	int ysnCheck=0;
	
	for(i=0;i<12;i++)
	   input[i]=0;

	try{   
    SubmitType=	java.lang.Integer.parseInt(request.getParameter("SubmitType"));
	}catch(NumberFormatException e){SubmitType=0;}
	
	
	
	if(SubmitType!=0){
	try{
		ICDCode=request.getParameter("ICD");
	
	if (!(request.getParameter("FL1").trim().equals("")))		
		input[1]=java.lang.Integer.parseInt(request.getParameter("FL1"));
	if (!(request.getParameter("F1To4").equals("")))		
		input[2]=java.lang.Integer.parseInt(request.getParameter("F1To4"));	
	if (!request.getParameter("F5To14").equals(""))		
		input[3]=java.lang.Integer.parseInt(request.getParameter("F5To14"));
	if (!request.getParameter("F15To44").equals(""))
		input[4]=java.lang.Integer.parseInt(request.getParameter("F15To44"));	
	if (!request.getParameter("F45To64").equals(""))		
		input[5]=java.lang.Integer.parseInt(request.getParameter("F45To64"));
	if (!request.getParameter("FG65").equals(""))		
		input[6]=java.lang.Integer.parseInt(request.getParameter("FG65"));
	if (!request.getParameter("ML1").equals(""))		
		input[7]=java.lang.Integer.parseInt(request.getParameter("ML1"));
	if (!request.getParameter("M1To4").equals(""))		
		input[8]=java.lang.Integer.parseInt(request.getParameter("M1To4"));
	if (!request.getParameter("M5To14").equals(""))		
		input[9]=java.lang.Integer.parseInt(request.getParameter("M5To14"));
	if (!request.getParameter("M15To44").equals(""))		
		input[10]=java.lang.Integer.parseInt(request.getParameter("M15To44"));
	if (!request.getParameter("M45To64").equals(""))		
		input[11]=java.lang.Integer.parseInt(request.getParameter("M45To64"));
	if (!request.getParameter("MG65").equals(""))		
		input[0]=java.lang.Integer.parseInt(request.getParameter("MG65"));
    if(RecType==1 || RecType==4)
     {
	if (!(request.getParameter("FRepeat").trim().equals("")))		
		input[12]=java.lang.Integer.parseInt(request.getParameter("FRepeat"));
	if (!(request.getParameter("MRepeat").equals("")))		
		input[13]=java.lang.Integer.parseInt(request.getParameter("MRepeat"));	
     }
    if(RecType==2)
     {  
	if (!(request.getParameter("FTLS").trim().equals("")))		
		input[14]=java.lang.Integer.parseInt(request.getParameter("FTLS"));
	if (!(request.getParameter("MTLS").equals("")))		
		input[15]=java.lang.Integer.parseInt(request.getParameter("MTLS"));	
	if (!request.getParameter("FCured").equals(""))		
		input[16]=java.lang.Integer.parseInt(request.getParameter("FCured"));
	if (!request.getParameter("MCured").equals(""))
		input[17]=java.lang.Integer.parseInt(request.getParameter("MCured"));	
	if (!request.getParameter("FImproved").equals(""))		
		input[18]=java.lang.Integer.parseInt(request.getParameter("FImproved"));
	if (!request.getParameter("MImproved").equals(""))		
		input[19]=java.lang.Integer.parseInt(request.getParameter("MImproved"));
	if (!request.getParameter("FNotImprovement").equals(""))		
		input[20]=java.lang.Integer.parseInt(request.getParameter("FNotImprovement"));
	if (!request.getParameter("MNotImprovement").equals(""))		
		input[21]=java.lang.Integer.parseInt(request.getParameter("MNotImprovement"));
	if (!request.getParameter("FWorse").equals(""))		
		input[22]=java.lang.Integer.parseInt(request.getParameter("FWorse"));
	if (!request.getParameter("MWorse").equals(""))		
		input[23]=java.lang.Integer.parseInt(request.getParameter("MWorse"));
	if (!request.getParameter("FDead").equals(""))		
		input[24]=java.lang.Integer.parseInt(request.getParameter("FDead"));
	if (!request.getParameter("MDead").equals(""))		
		input[25]=java.lang.Integer.parseInt(request.getParameter("MDead"));
      }		
	}catch(NumberFormatException e){
		out.println("Error on getting NumberFormatException:" + e.getMessage());
	}
    }		
	try
		{
			
	 
     		
			
			
	
			ICD = m.getICDData();

			
			if(SubmitType==1)
			{
			 m.deleteMMData(orgUnit,dataPeriod,ICDCode,RecType);
			}
			else if(SubmitType==2)
			{
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","<1",input[1],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","1-4",input[2],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","5-14",input[3],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","15-44",input[4],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","45-64",input[5],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","65+",input[6],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","<1",input[7],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","1-4",input[8],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","5-14",input[9],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","15-44",input[10],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","45-64",input[11],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","65+",input[0],Lastupdated,LastUser,ysnCheck);
   			 if(RecType==1 || RecType==4)
     		 {
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Repeat",input[12],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Repeat",input[13],Lastupdated,LastUser,ysnCheck);
     		 }
		    if(RecType==2)
     		 {  
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","TLS",input[14],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","TLS",input[15],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Cured",input[16],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Cured",input[17],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Improved",input[18],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Improved",input[19],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Not Improvement",input[20],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Not Improvement",input[21],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Worse",input[22],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Worse",input[23],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Dead",input[24],Lastupdated,LastUser,ysnCheck);
			  m.addMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Dead",input[25],Lastupdated,LastUser,ysnCheck);
	         }		  
			}
			else if(SubmitType==3)
			{
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","<1",input[1]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","1-4",input[2]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","5-14",input[3]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","15-44",input[4]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","45-64",input[5]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","65+",input[6]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","<1",input[7]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","1-4",input[8]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","5-14",input[9]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","15-44",input[10]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","45-64",input[11]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","65+",input[0]);

		     if(RecType==1 || RecType==4)
     		 {
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Repeat",input[12]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Repeat",input[13]);
     		 }
		    if(RecType==2)
    		 {	   
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","TLS",input[14]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","TLS",input[15]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Cured",input[16]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Cured",input[17]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Improved",input[18]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Improved",input[19]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Not Improvement",input[20]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Not Improvement",input[21]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Worse",input[22]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Worse",input[23]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"F","Dead",input[24]);
			  m.updateMMData(orgUnit,dataPeriod,ICDCode,RecType,"M","Dead",input[25]);
     		 }    
			} 
			
	data=m.getMMData(orgUnit,dataPeriod,RecType);
			
	for(count=0;count<data.length;count++){		
		out.println("<tr>");
		out.println("<form name='frmMM" + count + "' action='mm.jsp' method='post'>");	
		out.println("<td class='tableitem' height='26'>");
	 
	    out.println("<select name='ICD'>");
		 for(i=0;i<ICD.length;i++){
		   String selectedICD=""; 
    	   if(ICD[i][0].equalsIgnoreCase(data[count][0])){selectedICD="Selected";}
		   out.println("<option value='" + ICD[i][0] +"' " + selectedICD +" >" + ICD[i][0] + " " + ICD[i][1] + "</option>");
		   }
    	out.println("</select>");
    	out.println("</td>");
		
		out.println("<td class='tableitem'><input type='text' name='FL1' size='3' value=" + data[count][1] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='F1To4' size='3' value=" + data[count][2] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='F5To14' size='3' value=" + data[count][3] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='F15To44' size='3' value=" + data[count][4] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='F45To64' size='3' value=" + data[count][5] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='FG65' size='3' value=" + data[count][6] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='ML1' size='3' value=" + data[count][7] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='M1To4' size='3' value=" + data[count][8] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='M5To14' size='3' value=" + data[count][9] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='M15To44' size='3' value=" + data[count][10] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='M45To64' size='3' value=" + data[count][11] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MG65' size='3' value=" + data[count][12] + "></td>");
    if(RecType==1 || RecType==4)
     {
		out.println("<td class='tableitem'><input type='text' name='FRepeat' size='3' value=" + data[count][13] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MRepeat' size='3' value=" + data[count][14] + "></td>");
     }
    if(RecType==2)
     {   
		out.println("<td class='tableitem'><input type='text' name='FTLS' size='3' value=" + data[count][15] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MTLS' size='3' value=" + data[count][16] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='FCured' size='3' value=" + data[count][17] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MCured' size='3' value=" + data[count][18] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='FImproved' size='3' value=" + data[count][19] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MImproved' size='3' value=" + data[count][20] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='FNotImprovement' size='3' value=" + data[count][21] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MNotImprovement' size='3' value=" + data[count][22] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='FWorse' size='3' value=" + data[count][23] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MWorse' size='3' value=" + data[count][24] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='FDead' size='3' value=" + data[count][25] + "></td>");
		out.println("<td class='tableitem'><input type='text' name='MDead' size='3' value=" + data[count][26] + "></td>");
      } 

		out.println("<td class='tableitem'><a href=\"javascript: submitForm(frmMM" + count +" ,  3)\" >update</a></td>");
		out.println("<td class='tableitem'><a href=\"javascript: submitForm(frmMM" + count +" ,  1)\" >delete</a></td>");
		out.println("<input type='hidden' name='SubmitType' value='1' >");
		out.println("<input type='hidden' name='recType' >");
		out.println("<input type='hidden' name='orgUnit' >");
		out.println("<input type='hidden' name='dataPeriod' >");
        out.println("</form>");
		out.println("</tr>");
	 } 
	    //Add new record
		out.println("<tr>");
		out.println("<form name=\"frmMM\" action='mm.jsp' method='post'>");	
		out.println("<td class='tableitem' height='26'>");
	 
	    out.println("<select name='ICD'>");
		 for(i=0;i<ICD.length;i++){
		   out.println("<option value='" + ICD[i][0] + "'>" + ICD[i][0] + " " + ICD[i][1] + "</option>");
    	   }
    	out.println("</select>");
    	out.println("</td>");
		
		out.println("<td class='tableitem' width='5'><input type='text' name='FL1' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='F1To4' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='F5To14' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='F15To44' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='F45To64' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='FG65' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='ML1' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='M1To4' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='M5To14' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='M15To44' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='M45To64' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MG65' size='3' value=''></td>");
    if(RecType==1 || RecType==4)
     {
		out.println("<td class='tableitem'><input type='text' name='FRepeat' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MRepeat' size='3' value=''></td>");
     }
    if(RecType==2)
     {  
		out.println("<td class='tableitem'><input type='text' name='FTLS' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MTLS' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='FCured' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MCured' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='FImproved' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MImproved' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='FNotImprovement' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MNotImprovement' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='FWorse' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MWorse' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='FDead' size='3' value=''></td>");
		out.println("<td class='tableitem'><input type='text' name='MDead' size='3' value=''></td>");
      }
		out.println("<td class='tableitem'><a href=\"javascript: submitForm(frmMM , 2)\" >save</a></td>");
		out.println("<input type='hidden' name='SubmitType' value='2' >");
		out.println("<input type='hidden' name='recType' >");
		out.println("<input type='hidden' name='orgUnit' >");
		out.println("<input type='hidden' name='dataPeriod' >");
        out.println("</form>");
		out.println("<tr>");
	}		
	catch (Exception err)
	{
		out.println("Class loading error" + err.getMessage());
    }
%>
</body> 
</html>