<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<jsp:useBean id="GenerateHomePage" scope="session" class="org.hisp.gtool.action.GenerateHomePage" />
<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />

<%

  session.setAttribute("loginStatus","0");
  session.setAttribute("muteOpt","OFF");
  
  // To Play Audio
  String muteOpt = (String) session.getAttribute("muteOpt");
  if(muteOpt != null && muteOpt.equals("OFF"))
  {
	AudioAction.stopAudio();
	AudioAction.playAudio("t1.wav");
  }	
    
%>

<html>
	<head>
		<title>Graphical Analyser</title>
		<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
		<style fprolloverstyle>       		
				A:hover {background-color: #CCCCCC;}
		</style>		
		<script>
			var request;
			var ouName="";
			var ouID=0; 
			var indName="";
			var sDateIndex=0;
			var eDateIndex=0;
			var category="";
			var categoryName="";
			var ide_type="";
			
			var sDate="";
			var eDate="";
			
			var envVarValuesForDM1 = "";
			var envVarValuesForDM2 = "";
			
			var muteOpt = 1;
			
			function createRequestObject()
			{
				if(XMLHttpRequest)		request = new XMLHttpRequest();
				else request = new ActiveXObject("Microsoft.XMLHTTP");
			}
			  
			function soundControlFunction()
			{																										
				if(muteOpt == 1) { muteOpt = 2; muteImage.src = "images/mute_off.jpg"}
			  	else { muteOpt = 1; muteImage.src = "images/mute_on.jpg"}
							  
				
				createRequestObject();
				request.onreadystatechange = displayResult;
				request.open("GET","tempAudio.jsp?muteOpt="+muteOpt,true);
				request.send(null);					
			}//soundControlFunction end
    		
    		function displayResult()
			{										
				if(request.readyState ==4)
				{
					if(request.status ==200)
					{														
						var response = request.responseXML;							
						var resultValues = response.getElementsByTagName("option");																												
						var statusValue = resultValues[0].firstChild.data;							
					}// status
				} // readyState
			}// displayResult	
    		
			function formValidations()
		 	{
				ouID = parent.chartPane.document.ChartGenerationForm.ouIDTB.value;
				ouName   = parent.chartPane.document.ChartGenerationForm.ouNameTB.value;
				sDateIndex    = parent.chartPane.document.ChartGenerationForm.sDateLB.selectedIndex;
				eDateIndex    = parent.chartPane.document.ChartGenerationForm.eDateLB.selectedIndex;
				category = parent.chartPane.document.ChartGenerationForm.categoryLB.selectedIndex;
			
				sDate = parent.chartPane.document.ChartGenerationForm.sDateLB.options[sDateIndex].value;
				eDate = parent.chartPane.document.ChartGenerationForm.eDateLB.options[eDateIndex].value;
				categoryName = parent.chartPane.document.ChartGenerationForm.categoryLB.options[category].text;

				if(ouName=="" || ouName==null) {alert("Please Select OrganisationUnit");return false;}
				else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
				else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
				else if(category < 0) {alert("Please Select Category");return false;}
				else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
				
				return true;
		 	} // formValidations Function End
		 
		 	function summaryReportFunction()
		 	{
	 			if(formValidations())
	 			{	 		
	 				var urlValuesForSR = ouName+"@@"+sDate+"@@"+eDate;
	 				
	 				var sWidth = 850;
					var sHeight = 650;
					var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
					var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

					// window.open("summaryReportFW.jsp?Action="+urlValuesForSR,'chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=no, resizable=no');
	 					 				
	 				if(categoryName=="Period") window.open("summaryReportPW.jsp?Action="+urlValuesForSR,'chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
	 				else if(categoryName=="Facility") window.open("summaryReportFW.jsp?Action="+urlValuesForSR,'chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
	 			}		 			 		
	 		}// summaryReportFunction end	 
	
			function dataStatusFunction()
		 	{
	 			if(formValidations())
	 			{	 		
	 				
	 				
	 				var urlValuesForSR = ouID+"@@"+sDate+"@@"+eDate;
	 				
	 				var sWidth = 850;
					var sHeight = 650;
					var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
					var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;
					
					var selSDate = parent.chartPane.document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
					var selEDate = parent.chartPane.document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
					var fileNameToOpen = "";
					if(selSDate == selEDate)	fileNameToOpen = "DataStatusDSW.jsp?Action="+urlValuesForSR;
					else fileNameToOpen = "DataStatusPW.jsp?Action="+urlValuesForSR;
	 				
	 				window.open(fileNameToOpen,'chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');	 				
	 			}		 			 		
	 		}// dataStatusFunction end	 


			function dataMartFunction(envVarValuesForDM1,envVarValuesForDM2)
		 	{	 						 			 	
				var fileNameToOpen = "DataMart.jsp?Action1="+envVarValuesForDM1+"&Action2="+envVarValuesForDM2;					
				window.open(fileNameToOpen,'chartWindow1','');
	 		}// dataMartFunction end	 

			function fun1()
			{	 		
				if(formValidations())
	 			{	 		
					ide_type = parent.chartPane.document.ChartGenerationForm.riRadio.value;
					
	 				envVarValuesForDM1 = ouID+"@@"+sDate+"@@"+eDate+"@@"+ide_type;	 		
	 				envVarValuesForDM2 = "";
	 				
	 				var sWidth = 850;
					var sHeight = 650;
					var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
					var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;
					
					var selSDate = parent.chartPane.document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
					var selEDate = parent.chartPane.document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
					
					for(count1=0;count1 < parent.chartPane.document.ChartGenerationForm.iListCB.length; count1++)
					{
						if(parent.chartPane.document.ChartGenerationForm.iListCB[count1].selected)
						{								
							envVarValuesForDM2  += parent.chartPane.document.ChartGenerationForm.iListCB.options[count1].text + "@@";
						}	
					} // for end	
					
					if (envVarValuesForDM2 == null || envVarValuesForDM2 == "") {alert("Please Select Indicator(s)");return ;}
					window.open('loadingPage.jsp','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
					setTimeout("dataMartFunction(envVarValuesForDM1,envVarValuesForDM2)", 1000);																				 				 				
				}// if block end
			}// fun1 end
			
			
			function dataValidationFunction()
		 	{
		 		
	 			if(formValidations())
	 			{	 		
	 				var urlValuesForSR = ouID+"@@"+sDate+"@@"+eDate;
	 				
	 				var sWidth = 850;
					var sHeight = 650;
					var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
					var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;
					
					var selSDate = parent.chartPane.document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
					var selEDate = parent.chartPane.document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
					var fileNameToOpen = "";
					
					fileNameToOpen = "DataValidation.jsp?Action="+urlValuesForSR;	 				
	 				window.open(fileNameToOpen,'chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');	 				
	 			}		 			 		
	 		}// dataValidationFunction end	 
			
			
		</script>
	</head>
	<body bgcolor="#c0c0c0">
	<div align="center" >
		<table border=0 style="border-collapse: collapse" bordercolor="#111111" cellpadding="0" cellspacing="0" width="1010" height="100%" bgcolor="#FfFfFf">
			<tr>
				<td colspan="2" width="1000" height="30" valign="top" style="background-image: url('images/GA_Banner_Title.jpg'); background-repeat: no-repeat; margin-left: 20px;">
				  	<div align="right" style="font-family: Arial; font-size: 10px"><a target="chartPane" style="text-decoration: none; color: #FF0000; font-family: Arial; font-size: 10px" href="LogOut.jsp">&nbsp;Logout&nbsp;</a>&nbsp;&nbsp;&nbsp;</div>
				  	<div align="right" class="DefaultFontStyles">														            						
      						<a href="javascript:soundControlFunction()"><img id="muteImage" name="muteImage" src="images/mute_on.jpg" border="0" height="40" width="40"></a>
      						<br/><br/><br/>
      						<!-- <a href="#" onclick="javascript:summaryReportFunction()" style="text-decoration: none; color:#0000ff">Summary Report</a>&nbsp;&nbsp; -->
      						<a href="LoginValidation.jsp?nextPageTF=DVViewScreen.jsp"      target="chartPane" style="text-decoration: none; color:#ff0000">&nbsp;DataValidation&nbsp;</a>&nbsp;
							<!-- <a href="LoginValidation.jsp?nextPageTF=DMartScreen.jsp"       target="chartPane" style="text-decoration: none; color:#ff0000">&nbsp;DataMart&nbsp;</a>&nbsp; -->
      						<a href="LoginValidation.jsp?nextPageTF=DStatusScreen.jsp"     target="chartPane" style="text-decoration: none; color:#ff0000">&nbsp;DataStatus&nbsp;</a>&nbsp;
      						<a href="LoginValidation.jsp?nextPageTF=GAnalysisScreen.jsp"   target="chartPane" style="text-decoration: none; color:#ff0000">&nbsp;GraphicalAnalysis&nbsp;</a>&nbsp;
				      		<a href="LoginValidation.jsp?nextPageTF=orgUnitTree.jsp?optToDisplay=shortname" target="ouTreeFrame" style="text-decoration: none; color:#ff0000">&nbsp;English&nbsp;</a>&nbsp;
				    	  	<a href="LoginValidation.jsp?nextPageTF=orgUnitTree.jsp?optToDisplay=name" target="ouTreeFrame" style="text-decoration: none; color:#ff0000">&nbsp;Gujarati&nbsp;</a>			    	  
			    	  <hr color="#FF0000"></hr>			  		
			    	</div>  
			  	</td>
			</tr>	
			<tr>
				<td width="240" height="480" valign="top">
					<iframe name="ouTreeFrame" id="ouTreeFrame" src="" scrolling=auto frameborder="0" width="100%" height="100%"></iframe>
				</td>
				<td width="760" height="480" valign="top">
					<iframe name="chartPane" id="chartPane" src="LoginScreen.jsp" scrolling=no frameborder="0" width="100%" height="100%"></iframe>
				</td>
			</tr>
		</table>      	
	</div>					
	</body>
</html>