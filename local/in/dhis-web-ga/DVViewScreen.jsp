
<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="DataValidationAction" scope="session" class="org.hisp.gtool.action.DataValidationAction" />
<jsp:useBean id="GenerateHomePage" scope="session" class="org.hisp.gtool.action.GenerateHomePage" />
<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />

<%
// To Play Audio
String muteOpt = (String) session.getAttribute("muteOpt");
if(muteOpt != null && muteOpt.equals("OFF"))
{
	AudioAction.stopAudio();
	AudioAction.playAudio("t6.wav");
}	


 Hashtable htForDataValidations = DataValidationAction.getAllDataValidations();
 Enumeration keysFordataValidations = htForDataValidations.keys();

 String monthNames[]={"","Jan-","Feb-","Mar-","Apr-","May-","Jun-","Jul-","Aug-","Sep-","Oct-","Nov-","Dec-"};
%>

<html>
	<head>
		<title>Graphical Analyser</title>
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    	<meta http-equiv="description" content="this is my page">
    	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		
		<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
		<style fprolloverstyle>       		
				A:hover {background-color: silver;}
		</style>		
		<script>
			var ouName=""; 
			var dsName="";
			var sDateIndex=0;
			var eDateIndex=0;			
			var sDate="";
			var eDate="";
																											
			function formValidations()
		 	{
				ouName   = document.ChartGenerationForm.ouNameTB.value;
				dvName  = document.ChartGenerationForm.dataValidationListCB.selectedIndex;
				sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
				eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
			
				sDate = document.ChartGenerationForm.sDateLB.options[sDateIndex].value;
				eDate = document.ChartGenerationForm.eDateLB.options[eDateIndex].value;

				if(ouName=="" || ouName==null) {alert("Please Select OrganisationUnit");return false;}
				else if(dvName < 0) {alert("Please Select DataValidations(s)");return false;}
				else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
				else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
				else if(sDate > eDate) {alert("Starting Date is Greater");return false;}

				var sWidth = 850;
				var sHeight = 650;
				var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
				var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

				window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');

				return true;
		 	} // formValidations Function End		 
	</script>
	</head>
	<body onload="onloadFunction()" >
	<form id="ChartGenerationForm" name="ChartGenerationForm" action="DataValidation.jsp" method="post" onsubmit="return formValidations()" target="chartWindow1">	
		 <table style=" border-collapse: collapse; margin-top: 0;" cellpadding="0" cellspacing="0" width="100%" height="100%"  border=0 valign="top">
      		<tr>
        		<td width="64%" class="NormalB" valign="top" style="padding-left: 0; padding-top: 0">
        			<table cellpadding="15" cellspacing="2" border=0 bordercolor="#800000" width="100%" style="background-image: url('images/Ind_Box1.png'); background-repeat: no-repeat;">        				      				        																				  
						<tr><td class="NormalB" >											  																	  																	  
								DataValidation List : <br><select name="dataValidationListCB" id="dataValidationListCB" multiple size="20" style="width: 400">
								<%																	
									while(keysFordataValidations.hasMoreElements())
									{
										String dvID = (String) keysFordataValidations.nextElement();			
										ArrayList liForDataValidations = (ArrayList) htForDataValidations.get(dvID);
										String dvName  = (String) liForDataValidations.get(0);
										String dvleftdeids = (String) liForDataValidations.get(1);
										String dvrightdeids = (String) liForDataValidations.get(2);
										String dvoperator = (String) liForDataValidations.get(3);
										String dvtype = (String) liForDataValidations.get(4);
										String dvleftdesc = (String) liForDataValidations.get(5);
										String dvrightdesc = (String) liForDataValidations.get(6);
      									%>
										<option value='<%=dvID%>'><%=dvName%></option>
										<%																				
									}// while loop end																									
								%>			  		
								</select>
						</td></tr>
						<tr><td align="right" class="NormalB"><a href="DValidationScreen.jsp" style="text-decoration: none; color:#0000ff">&nbsp;Define&nbsp;</a>&nbsp;&nbsp;<br></br><br/></td></tr>
        			</table>		
        		</td>        		    		        			        	
        		<td width="31%" class="NormalB" style="padding-left: 0; padding-top: 0" valign="top">
        			<table border=0 width="100%" cellpadding="25" style="background-image: url('images/Period_Box1.png'); background-repeat: no-repeat; ">        					
        				<tr><td class="NormalB" width="100%">
        						From : <br><select id="sDateLB" name="sDateLB">
												<%
													List liForSDate = GenerateHomePage.getStartDate();
													Iterator iForSDate = liForSDate.iterator();
													while(iForSDate.hasNext())
													{
														String sdate = (String) iForSDate.next();
														String partsOfTempForSDate[] = sdate.split("-");
														String tempForSDate = monthNames[Integer.parseInt(partsOfTempForSDate[1])]+partsOfTempForSDate[0];%>
														<option value="<%=sdate%>"><%=tempForSDate%></option>
													<%}// while loop end									
													%>			  							
			  									</select>			  									
												<br><br>												
												To : <br><select id="eDateLB" name="eDateLB">
			  									<%
													List liForEDate = GenerateHomePage.getEndDate();
													Iterator iForEDate = liForEDate.iterator();
													while(iForEDate.hasNext())
													{
														String edate = (String) iForEDate.next();
														String partsOfTempForEDate[] = edate.split("-");
														String tempForEDate = monthNames[Integer.parseInt(partsOfTempForEDate[1])]+partsOfTempForEDate[0];
														%>
														<option value="<%=edate%>"><%=tempForEDate%></option>
														<%
													}// while loop end									
												%>
			  									</select>
			  									<br/><br/><br/>
			  					</td></tr>
			  								  					
			  				</table>			  				
			  				<table width="100%" cellpadding="15" border=0 bordercolor="#800000" style="background-image: url('images/Submit_Box1.png'); background-repeat: no-repeat;">
        						<tr><td class="NormalB">		
									<br/><br/>
									OrganisationUnit  : <br><input type="text" name="ouNameTB" id="ouNameTB" size="20">									  							
									<br/><br/><br/><br/>
									LocalLanguage : <input type="checkbox" name="LocalLangCB" id="LocalLangCB" checked/>
									<br/><br/><br/><br/>
									<input type="submit" name="ViewValidation" value="ViewValidation" style="width: 120; height: 25; font-family:Arial; font-weight:bold; color:#800080">
									<input type="hidden" name="ouIDTB" id="ouIDTB">
									<br/><br/><br/><br/>									
								</td></tr>
							</table>							  	
        				</td>
        				<td width="5%" valign="center" class="NormalB" style="background-image: url('images/SideTitle1.png'); background-repeat: no-repeat; "></td>
      				</tr>
    			</table>
			</form>	
	</body>
</html>