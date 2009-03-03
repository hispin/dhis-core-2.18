


<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>


<jsp:useBean id="GenerateHomePage" scope="session" class="org.hisp.gtool.action.GenerateHomePage" />
<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />


<%

// To Play Audio
String muteOpt = (String) session.getAttribute("muteOpt");
if(muteOpt != null && muteOpt.equals("OFF"))
{
	AudioAction.stopAudio();
	AudioAction.playAudio("t2.wav");
}	


Hashtable htForIGMembers = GenerateHomePage.getIndicatorGroups();
//Enumeration keysForigMembers = htForIGMembers.keys();
Vector keysForigMembers = new Vector(htForIGMembers.keySet());
Collections.sort(keysForigMembers);
Iterator keysForigMembersIter = keysForigMembers.iterator();

	
Hashtable htForDEGMembers = GenerateHomePage.getDEGroups();
//Enumeration keysFordegMembers = htForDEGMembers.keys();
Vector keysFordegMembers = new Vector(htForDEGMembers.keySet());
Collections.sort(keysFordegMembers);
Iterator keysFordegMembersIter = keysFordegMembers.iterator();


String monthNames[]={"","Jan-","Feb-","Mar-","Apr-","May-","Jun-","Jul-","Aug-","Sep-","Oct-","Nov-","Dec-"};
%>

<html>
	<head>
		<title>Graphical Analyser</title>
		<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
		<style fprolloverstyle>       		
				A:hover {background-color: silver;}
		</style>		
		<script>
			var ouName=""; 
			var indName="";
			var sDateIndex=0;
			var eDateIndex=0;
			var category="";
			var categoryName="";
			
			var sDate="";
			var eDate="";
			
			var igMembers = new Array(); // Indicator Groups and its Corresponding Indicators
			var countForIGMembers1 = 0;
			var countForIGMembers2 = 0;
			
			var selriRadioButton="indicatorsRadio";

			// Indicator Group Members
			<%
		//	keysForigMembers = htForIGMembers.keys();
			
//			Vector keysForigMembers = new Vector(htForIGMembers.keySet());
//			Collections.sort(keysForigMembers);
			keysForigMembersIter = keysForigMembers.iterator();
			
			while(keysForigMembersIter.hasNext())
			{
				String igName = (String) keysForigMembersIter.next();
				List liForIGMembers = (ArrayList) htForIGMembers.get(igName);
				Iterator itForIGMembers = liForIGMembers.iterator();
				%>
				igMembers[countForIGMembers1] = new Array();
				countForIGMembers2 = 0;
				<%
				while(itForIGMembers.hasNext())
				{		
					String iName = (String) itForIGMembers.next();
					%>
					igMembers[countForIGMembers1][countForIGMembers2++]= '<%=iName%>';
					<%
				} // inner while loop
				%>
				countForIGMembers1++;
				<%
			} // outer while loop	
			%>
			
			
			var degMembers = new Array(); // DEGroups and its Dataelemets
			var countForDEGMembers1 = 0;
			var countForDEGMembers2 = 0;

			// DEGroup Members
			<%
			keysFordegMembersIter = keysFordegMembers.iterator();
			while(keysFordegMembersIter.hasNext())
			{
				String degName = (String) keysFordegMembersIter.next();
				List liForDEGMembers = (ArrayList) htForDEGMembers.get(degName);
				Iterator itForDEGMembers = liForDEGMembers.iterator();
				%>
				degMembers[countForDEGMembers1] = new Array();
				countForDEGMembers2 = 0;
				<%
				while(itForDEGMembers.hasNext())
				{		
					String deName = (String) itForDEGMembers.next();
					%>
					degMembers[countForDEGMembers1][countForDEGMembers2++]= '<%=deName%>';
					<%
				} // inner while loop
				%>
				countForDEGMembers1++;
				<%
			} // outer while loop	
			%>
			
			
			
			var igNames = new Array(); // IndicatorGroup Names
			var countForIGNames = 0;

			// IndicatorGroup Names			
			<%
				//keysForigMembers = htForIGMembers.keys();
				keysForigMembersIter = keysForigMembers.iterator();
				while(keysForigMembersIter.hasNext())
				{
					String igName = (String) keysForigMembersIter.next(); 
					%>
					igNames[countForIGNames++]= '<%=igName%>';
					<%					
				} // while loop		
			%>



			var degNames = new Array(); // DataElementGroup Names
			var countForDEGNames = 0;
			
			// DEGroup Names			
			<%
				//keysFordegMembers = htForDEGMembers.keys();
				keysFordegMembersIter = keysFordegMembers.iterator();
				while(keysFordegMembersIter.hasNext())
				{
					String degName = (String) keysFordegMembersIter.next(); 
					%>
					degNames[countForDEGNames++]= '<%=degName%>';
					<%					
				} // while loop		
			%>
			
			
			
			function riradioSelection(evt)
			{
				selriRadioButton = evt.target.value;
				var iGroupsCBLength = document.ChartGenerationForm.iGroupsCB.options.length;
    			var k = 0;
    			// For loop to clear the Indicator/DE Group ListBox
    			for(k=0;k<iGroupsCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.iGroupsCB.options[0] = null;    	     
             	} // for loop end
             	
             	// For Loop to clear the Indicators /Data Elements ListBox
             	var iListCBLength = document.ChartGenerationForm.iListCB.options.length;
    			for(k=0;k<iListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.iListCB.options[0] = null;    	     
             	} // for loop end			  
             				  
			  	var ideGroupIndex = 0;			  	
			  	if(selriRadioButton == "dataElementsRadio")
			  	 {			  	 
				  	for(k=0;k<degNames.length;k++)
				  	{
				  	 document.ChartGenerationForm.iGroupsCB.options[k] = new Option(degNames[k],degNames[k],false,false);			  	 
				  	}// for loop end
				  	
				  	for(k=0;k<degMembers[ideGroupIndex].length;k++)
				  	{
				  	 document.ChartGenerationForm.iListCB.options[k] = new Option(degMembers[ideGroupIndex][k],degMembers[ideGroupIndex][k],false,false);			  	 
				  	}// for loop end
				  	
				  }// if block end
				 else
				 {
				 	for(k=0;k<igNames.length;k++)
				  	{
				  	 document.ChartGenerationForm.iGroupsCB.options[k] = new Option(igNames[k],igNames[k],false,false);			  	 
				  	}// for loop end	
				  	
			  		
			  		for(k=0;k<igMembers[ideGroupIndex].length;k++)
			  		{
			  	 	document.ChartGenerationForm.iListCB.options[k] = new Option(igMembers[ideGroupIndex][k],igMembers[ideGroupIndex][k],false,false);			  	 
			  		}// for loop end			 
				 }// else end 				
			}// function riradioSelection end
			
			
			function onloadFunction()
			{
			  	var iGroupsCBLength = document.ChartGenerationForm.iGroupsCB.options.length;
    			var k = 0;
    			for(k=0;k<iGroupsCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.iGroupsCB.options[0] = null;    	     
             	} // for loop end			  
			 	for(k=0;k<igNames.length;k++)
			  	{
				  	document.ChartGenerationForm.iGroupsCB.options[k] = new Option(igNames[k],igNames[k],false,false);			  	 
			  	}// for loop end				 
			  				  	
			  				  	
			  	var iListCBLength = document.ChartGenerationForm.iListCB.options.length;
    			for(k=0;k<iListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.iListCB.options[0] = null;    	     
             	} // for loop end			  
			  	var iGroupIndex = 0;
			  	for(k=0;k<igMembers[iGroupIndex].length;k++)
			  	{
			  	 document.ChartGenerationForm.iListCB.options[k] = new Option(igMembers[iGroupIndex][k],igMembers[iGroupIndex][k],false,false);			  	 
			  	}
			} // onloadFuntion end
			

			function iGroupsCBChange()
			{
			  	var iListCBLength = document.ChartGenerationForm.iListCB.options.length;
    			var k = 0;
    			for(k=0;k<iListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.iListCB.options[0] = null;    	     
             	} // for loop end			  
			  	var iGroupIndex = document.ChartGenerationForm.iGroupsCB.selectedIndex;
			  	
			  	if(selriRadioButton == "dataElementsRadio")
			  	 {			  	 
				  	for(k=0;k<degMembers[iGroupIndex].length;k++)
				  	{
				  	 document.ChartGenerationForm.iListCB.options[k] = new Option(degMembers[iGroupIndex][k],degMembers[iGroupIndex][k],false,false);			  	 
				  	}// for loop end
				  }// if block end
				 else
				 {
				 	for(k=0;k<igMembers[iGroupIndex].length;k++)
				  	{
				  	 document.ChartGenerationForm.iListCB.options[k] = new Option(igMembers[iGroupIndex][k],igMembers[iGroupIndex][k],false,false);			  	 
				  	}// for loop end				 
				 }// else end 	
			}// function iGroupsCBChange end
			
			
			function formValidations()
		 	{
				ouName   = document.ChartGenerationForm.ouNameTB.value;
				indName  = document.ChartGenerationForm.iListCB.selectedIndex;
				sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
				eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
				category = document.ChartGenerationForm.categoryLB.selectedIndex;
			
				sDate = document.ChartGenerationForm.sDateLB.options[sDateIndex].value;
				eDate = document.ChartGenerationForm.eDateLB.options[eDateIndex].value;
				categoryName = document.ChartGenerationForm.categoryLB.options[category].text;


				if(ouName=="" || ouName==null) {alert("Please Select OrganisationUnit");return false;}
				else if(indName < 0) {alert("Please Select Indicator(s)");return false;}
				else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
				else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
				else if(category < 0) {alert("Please Select Category");return false;}
				else if(sDate > eDate) {alert("Starting Date is Greater");return false;}

				var sWidth = 850;
				var sHeight = 650;
				var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
				var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

				window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');

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
	</script>
	</head>
	<body onload="onloadFunction()" >
	<form id="ChartGenerationForm" name="ChartGenerationForm" action="tempForViewChart.jsp" method="post" onsubmit="return formValidations()" target="chartWindow1">	
		 <table style=" border-collapse: collapse; margin-top: 0;" cellpadding="0" cellspacing="0" width="100%" height="100%"  border=0 valign="top">
      		<tr>
        		<td width="64%" class="NormalB" valign="top" style="padding-left: 0; padding-top: 0">
        			<table cellpadding="15" cellspacing="2" border=0 bordercolor="#800000" width="100%" style="background-image: url('images/Ind_Box1.png'); background-repeat: no-repeat;">        				
        				<tr><td class="NormalB">
        					<input type="radio" id="riRadio" name="riRadio" value="dataElementsRadio"    onclick="riradioSelection(event)"> DataElements
							&nbsp;&nbsp;&nbsp;
        					<input type="radio" id="riRadio" name="riRadio" value="indicatorsRadio" onclick="riradioSelection(event)" checked> Indicators
        				</td></tr>        				
        				<tr><td class="NormalB">
        						Indicator/DataElement Group : <br><select name="iGroupsCB" id="iGroupsCB" onchange="iGroupsCBChange()" style="width: 400">
																		<%
																		/*
																		keysForigMembers = htForIGMembers.keys();
																		while(keysForigMembers.hasMoreElements())
																		{
																			String igName = (String) keysForigMembers.nextElement(); %>
																			<option><%=igName%></option>
																		<%} // while loop	
																		*/	
																		%>																	
																	  </select>
						</td></tr>																	  
						<tr><td class="NormalB" >											  																	  																	  
																	 Indicator/DataElement List : <br><select name="iListCB" id="iListCB" multiple size="15" style="width: 400">
											  						 <%
																		/*
																		List liForIName = GenerateHomePage.getIndicatorName();
																		Iterator iForIName = liForIName.iterator();
																		while(iForIName.hasNext())
																			{
																				String iname = (String) iForIName.next();%>
																				<option><%=iname%></option>
																			<%}// while loop end									
																		*/	
																	%>			  		
			  														</select>
						</td></tr>
						<tr><td>&nbsp;<br></br><br/></td></tr>
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
												String tempForEDate = monthNames[Integer.parseInt(partsOfTempForEDate[1])]+partsOfTempForEDate[0];%>
												<option value="<%=edate%>"><%=tempForEDate%></option>
											<%}// while loop end									
										%>
			  						</select>
			  						<br/><br/><br/>
			  					</td></tr>			  					
			  				</table>			  				
			  				<table width="100%" cellpadding="15" border=0 bordercolor="#800000" style="background-image: url('images/Submit_Box1.png'); background-repeat: no-repeat; " >
        						<tr><td class="NormalB">		
									OrganisationUnit  : <br><input type="text" name="ouNameTB" id="ouNameTB" size="20">									  							
									<br/><br/><br/>
									View by : <br><select id="categoryLB" name="categoryLB">
			  									<option>Period</option>
			  									<option>Facility</option>
			  									
			  								</select>
			  						<br/><br/><br/>					  						
									<input type="submit" name="ViewChart" value="ViewChart" style="width: 120; height: 25; font-family:Arial; font-weight:bold; color:#800080">
									<br/><br/><br/>
									<input type="button" name="ViewSummary" value="ViewSummary" style="width: 120; height: 25; font-family:Arial; font-weight:bold; color:#800080" onclick="javascript:summaryReportFunction()">
									<input type="hidden" name="ouIDTB" id="ouIDTB">
									<br></br><br/><br/><br/><br/>
								</td></tr>
							</table>							  	
        				</td>
        				<td width="5%" valign="center" class="NormalB" style="background-image: url('images/SideTitle1.png'); background-repeat: no-repeat; "></td>
      				</tr>
    			</table>
			</form>	
	</body>
</html>