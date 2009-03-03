
<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="GenerateHomePage" scope="session" class="org.hisp.gtool.action.GenerateHomePage" />
<jsp:useBean id="CommonResourcesAction" scope="session" class="org.hisp.gtool.action.CommonResourcesAction" />
<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />

<%
// To Play Audio
String muteOpt = (String) session.getAttribute("muteOpt");
if(muteOpt != null && muteOpt.equals("OFF"))
{
	AudioAction.stopAudio();
	AudioAction.playAudio("t7.wav");
}	


Hashtable htForDEGMembers = GenerateHomePage.getDEGroups();
if(htForDEGMembers == null) {out.println("No DEGroups"); return;}
//Enumeration keysFordegMembers = htForDEGMembers.keys();
Vector keysFordegMembers = new Vector(htForDEGMembers.keySet());
Collections.sort(keysFordegMembers);
Iterator keysFordegMembersIter = keysFordegMembers.iterator();

Hashtable htForDEDetails = CommonResourcesAction.getDEDetailsByAlternativeName();
if(htForDEDetails==null) {out.println("No DataElements In the DataBase"); return;}

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
		<script type="text/javascript" src="javascript/hashtable.js"></script>
		<script>												
			var degMembers = new HashTable(); // DEGroups and its Dataelemets
			var leftDEGMemStatus = new HashTable(); //DEGroup Members Status - A for Available, S for Selected
			var rightDEGMemStatus = new HashTable(); //DEGroup Members Status - A for Available, S for Selected
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
				var tempdegMembers = new Array();

				countForDEGMembers2 = 0;
				<%
				while(itForDEGMembers.hasNext())
				{		
					String deName = (String) itForDEGMembers.next();
					List liForDEDetails = (ArrayList) htForDEDetails.get(deName);
					String deID = (String) liForDEDetails.get(0);
					String deShortName = (String) liForDEDetails.get(1);
					System.out.println("ShortName: "+deShortName);
					%>
					tempdegMembers[countForDEGMembers2++]= trimAll('<%=deShortName%>');
					var tempdegMembersStatusL =  new Array();
					var tempdegMembersStatusR =  new Array(); 
					tempdegMembersStatusL[0] = '<%=deShortName%>';
					
					tempdegMembersStatusL[1] = "A";
					tempdegMembersStatusL[2] = '<%=deID%>';
					
					tempdegMembersStatusR[0] = '<%=deShortName%>';
					tempdegMembersStatusR[1] = "A";
					tempdegMembersStatusR[2] = '<%=deID%>';
					
					leftDEGMemStatus.put(trimAll('<%=deShortName%>'),tempdegMembersStatusL);
					rightDEGMemStatus.put(trimAll('<%=deShortName%>'),tempdegMembersStatusR);					
					<%
				} // inner while loop
				%>
				degMembers.put('<%=degName%>',tempdegMembers);				
				countForDEGMembers1++;
				<%
			} // outer while loop	
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
												
			function trimAll(s1)
			{
  				while(s1.substring(0,1)== ' ')
   				{
     				s1 = s1.substring(1,s1.length);
   				}
  				while(s1.substring(s1.length-1,s1.length)== ' ')
   				{
     				s1 = s1.substring(0,s1.length-1);
   				}
  				return s1; 
			}// trimAll End 
			
			
			function onloadFunction()
			{
			 //For LeftSide DataElement Group List
			  	var leftDEGroupsCBLength = document.ChartGenerationForm.LeftDEGroupsCB.options.length;
    			var k = 0;
    			for(k=0;k<leftDEGroupsCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.LeftDEGroupsCB.options[0] = null;    	     
             	} // for loop end			  
			 	for(k=0;k<degNames.length;k++)
			  	{
				  	document.ChartGenerationForm.LeftDEGroupsCB.options[k] = new Option(degNames[k],degNames[k],false,false);
			  	}// for loop end				 
			  				  	
			  //For LeftSide AvailableDataElement List				  	
			  	var LeftAvailDEListCBLength = document.ChartGenerationForm.LeftAvailDEListCB.options.length;
    			var k = 0;
    			for(k=0;k<LeftAvailDEListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.LeftAvailDEListCB.options[0] = null;    	     
             	} // for loop end			  
			  	var leftAvailDEList = new Array();
			  	leftAvailDEList = degMembers.get(degNames[0]);
			  				  	
			  	for(k=0;k<leftAvailDEList.length;k++)
			  	{
					document.ChartGenerationForm.LeftAvailDEListCB.options[k] = new Option(leftAvailDEList[k],k,false,false);			  	 
			  	}// for loop end
			  	
			  	//For RightSide DataElement Group List
			  	var rightDEGroupsCBLength = document.ChartGenerationForm.RightDEGroupsCB.options.length;
    			var k = 0;
    			for(k=0;k<rightDEGroupsCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.RightDEGroupsCB.options[0] = null;    	     
             	} // for loop end			  
			 	for(k=0;k<degNames.length;k++)
			  	{
				  	document.ChartGenerationForm.RightDEGroupsCB.options[k] = new Option(degNames[k],degNames[k],false,false);
			  	}// for loop end
			  	
			  	//For RightSide Available DataElement List
			  	var RightAvailDEListCBLength = document.ChartGenerationForm.RightAvailDEListCB.options.length;
    			var k = 0;
    			for(k=0;k<RightAvailDEListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.RightAvailDEListCB.options[0] = null;    	     
             	} // for loop end			  
			  	var rightAvailDEList = new Array();
			  	rightAvailDEList = degMembers.get(degNames[0]);
			  	
			  	for(k=0;k<rightAvailDEList.length;k++)
			  	{
					document.ChartGenerationForm.RightAvailDEListCB.options[k] = new Option(rightAvailDEList[k],k,false,false);			  	 
			  	}// for loop end
			} // onloadFuntion end
			
			function leftDEGroupsCBChange()
			{	
				//For LeftSide AvailableDataElement List				  	
			  	var LeftAvailDEListCBLength = document.ChartGenerationForm.LeftAvailDEListCB.options.length;
    			var k = 0;
    			for(k=0;k<LeftAvailDEListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.LeftAvailDEListCB.options[0] = null;    	     
             	} // for loop end			  

			  	var leftDEGroupIndex = document.ChartGenerationForm.LeftDEGroupsCB.selectedIndex;
			  	var leftAvailDEList = new Array();
			  	leftAvailDEList = degMembers.get(degNames[leftDEGroupIndex]);
			  	var j=0;			  	
			  	for(k=0;k<leftAvailDEList.length;k++)
			  	{
					var degMemStatus = new Array();
					degMemStatus = leftDEGMemStatus.get(leftAvailDEList[k]);
					if(degMemStatus[1] == "A")
						document.ChartGenerationForm.LeftAvailDEListCB.options[j++] = new Option(leftAvailDEList[k],k,false,false);			  	 
			  	}// for loop end		  				  	
			}// function leftDEGroupsCBChange end
						
			function rightDEGroupsCBChange()
			{			  	
			  	//For RightSide Available DataElement List
			  	var RightAvailDEListCBLength = document.ChartGenerationForm.RightAvailDEListCB.options.length;
    			var k = 0;
    			for(k=0;k<RightAvailDEListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.RightAvailDEListCB.options[0] = null;    	     
             	} // for loop end			  

			  	var rightDEGroupIndex = document.ChartGenerationForm.RightDEGroupsCB.selectedIndex;
			  	var rightAvailDEList = new Array();
			  	rightAvailDEList = degMembers.get(degNames[rightDEGroupIndex]);
			  	var j=0;
			  	for(k=0;k<rightAvailDEList.length;k++)
			  	{
			  		var degMemStatus = new Array();
			  		degMemStatus = rightDEGMemStatus.get(rightAvailDEList[k]);
			  		if(degMemStatus[1] == "A")
						document.ChartGenerationForm.RightAvailDEListCB.options[j++] = new Option(rightAvailDEList[k],k,false,false);			  	 
			  	}// for loop end			  	
			}// function rightDEGroupsCBChange end
			
			function leftGFunction()
			{			
			  	var LeftAvailDEListCBLength = document.ChartGenerationForm.LeftAvailDEListCB.options.length;
			  	var LeftSelDEListCBLength = document.ChartGenerationForm.LeftSelDEList.options.length;
    			var k = 0;
    			for(k=0;k<LeftAvailDEListCBLength;k++)
    	     	{	
    	     		if(document.ChartGenerationForm.LeftAvailDEListCB.options[k].selected)
    	     			{
    	       				var degMemStatus = new Array();
							degMemStatus = leftDEGMemStatus.get(document.ChartGenerationForm.LeftAvailDEListCB.options[k].text);
							degMemStatus[1] = "S";
							leftDEGMemStatus.put(document.ChartGenerationForm.LeftAvailDEListCB.options[k].text,degMemStatus);    	       				
							
							document.ChartGenerationForm.LeftSelDEList.options[LeftSelDEListCBLength++] = new Option(document.ChartGenerationForm.LeftAvailDEListCB.options[k].text,degMemStatus[2],false,false);
    	       			}	
             	} // for loop end
             	
             	leftDEGroupsCBChange();
			}//leftGFunction end
			
			function leftLFunction()
			{				
			  	var LeftSelDEListCBLength = document.ChartGenerationForm.LeftSelDEList.options.length;
    			var k = 0;
    			var j=0;
    			var LeftSelDEList = new Array();
    			
    			for(k=0;k<LeftSelDEListCBLength;k++)
    	     	{	
    	     		if(document.ChartGenerationForm.LeftSelDEList.options[k].selected)
    	     			{
    	       				var degMemStatus = new Array();
							degMemStatus = leftDEGMemStatus.get(document.ChartGenerationForm.LeftSelDEList.options[k].text);
							degMemStatus[1] = "A";
							leftDEGMemStatus.put(document.ChartGenerationForm.LeftSelDEList.options[k].text,degMemStatus); 														
    	       			}
    	       		else
    	       		{
    	       			LeftSelDEList[j++] = document.ChartGenerationForm.LeftSelDEList.options[k].text;    	       			
    	       		}		
             	} // for loop end
             	
             	leftDEGroupsCBChange();
             	leftSelDEListFunction(LeftSelDEList)
			}//leftLFunction
			
			function leftSelDEListFunction(LeftSelDEList)			
			{
				var k=0;
				var LeftSelDEListCBLength = document.ChartGenerationForm.LeftSelDEList.options.length;
				for(k=0;k<LeftSelDEListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.LeftSelDEList.options[0] = null;    	     
             	} // for loop end			  
				
				for(k=0;k<LeftSelDEList.length;k++)
				 {
				 	var degMemStatus = new Array();
					degMemStatus = leftDEGMemStatus.get(LeftSelDEList[k]);
				 	document.ChartGenerationForm.LeftSelDEList.options[k] = new Option(LeftSelDEList[k],degMemStatus[2],false,false);
				 }
			}//leftSelDEList
			
			function rightGFunction()
			{			
			  	var RightAvailDEListCBLength = document.ChartGenerationForm.RightAvailDEListCB.options.length;
			  	var RightSelDEListCBLength = document.ChartGenerationForm.RightSelDEList.options.length;
    			var k = 0;
    			for(k=0;k<RightAvailDEListCBLength;k++)
    	     	{	
    	     		if(document.ChartGenerationForm.RightAvailDEListCB.options[k].selected)
    	     			{
    	       				var degMemStatus = new Array();
							degMemStatus = rightDEGMemStatus.get(document.ChartGenerationForm.RightAvailDEListCB.options[k].text);
							degMemStatus[1] = "S";
							rightDEGMemStatus.put(document.ChartGenerationForm.RightAvailDEListCB.options[k].text,degMemStatus);    	       				
							
							document.ChartGenerationForm.RightSelDEList.options[RightSelDEListCBLength++] = new Option(document.ChartGenerationForm.RightAvailDEListCB.options[k].text,degMemStatus[2],false,false);
    	       			}	
             	} // for loop end
             	
             	rightDEGroupsCBChange();
			}//rightGFunction end
			
			
			function rightLFunction()
			{				
			  	var RightSelDEListCBLength = document.ChartGenerationForm.RightSelDEList.options.length;
    			var k = 0;
    			var j=0;
    			var RightSelDEList = new Array();
    			
    			for(k=0;k<RightSelDEListCBLength;k++)
    	     	{	
    	     		if(document.ChartGenerationForm.RightSelDEList.options[k].selected)
    	     			{
    	       				var degMemStatus = new Array();
							degMemStatus = rightDEGMemStatus.get(document.ChartGenerationForm.RightSelDEList.options[k].text);
							degMemStatus[1] = "A";
							rightDEGMemStatus.put(document.ChartGenerationForm.RightSelDEList.options[k].text,degMemStatus); 														
    	       			}
    	       		else
    	       		{
    	       			RightSelDEList[j++] = document.ChartGenerationForm.RightSelDEList.options[k].text;    	       			
    	       		}		
             	} // for loop end
             	
             	rightDEGroupsCBChange();
             	rightSelDEListFunction(RightSelDEList)
			}//rightLFunction
			
			function rightSelDEListFunction(RightSelDEList)			
			{
				var k=0;
				var RightSelDEListCBLength = document.ChartGenerationForm.RightSelDEList.options.length;
				for(k=0;k<RightSelDEListCBLength;k++)
    	     	{
    	       		document.ChartGenerationForm.RightSelDEList.options[0] = null;    	     
             	} // for loop end			  
				
				for(k=0;k<RightSelDEList.length;k++)
				 {
				 	var degMemStatus = new Array();
					degMemStatus = rightDEGMemStatus.get(RightSelDEList[k]);
				 	document.ChartGenerationForm.RightSelDEList.options[k] = new Option(RightSelDEList[k],degMemStatus[2],false,false);
				 }
			}//rightSelDEList
			
			
			function VTypeLBChangeFunction(selVal)
			{
				document.ChartGenerationForm.leftPercent.value = "";
				document.ChartGenerationForm.rightPercent.value = "";
					
				if(selVal == "E") 
				{
					document.ChartGenerationForm.leftPercent.disabled = false;
					document.ChartGenerationForm.rightPercent.disabled = false;	
				}	
				else 
				{
					document.ChartGenerationForm.leftPercent.disabled = true;
					document.ChartGenerationForm.rightPercent.disabled = true;
				}
			}//VTypeLBChangeFunction
			
			
			function formValidations()
		 	{
				DVName   = document.ChartGenerationForm.VNameTB.value;
				LeftDesc = document.ChartGenerationForm.leftDescTB.value;
				RightDesc = document.ChartGenerationForm.rightDescTB.value;
				LeftSelDEListIndex  = document.ChartGenerationForm.LeftSelDEList.options.length;
				RightSelDEListIndex = document.ChartGenerationForm.RightSelDEList.options.length;
											
				if(DVName=="" || DVName==null) {alert("Please Enter ValidationRule Name");return false;}
				else if(LeftDesc=="" || LeftDesc==null) {alert("Please Enter LeftSide Description");return false;}
				else if(RightDesc=="" || RightDesc==null) {alert("Please Enter RightSide Description");return false;}
				else if(LeftSelDEListIndex <= 0) {alert("Please Select DataElements For LeftSide Validation");return false;}
				else if(RightSelDEListIndex <= 0) {alert("Please Select DataElements For RightSide Validation");return false;}
				
				var k=0;
				for(k=0;k<LeftSelDEListIndex;k++)
    	     	{    	       		
    	       		document.ChartGenerationForm.LeftSelDEList.options[k].selected = true;    	     
             	} // for loop end
             	
				for(k=0;k<RightSelDEListIndex;k++)
    	     	{    	       		
    	       		document.ChartGenerationForm.RightSelDEList.options[k].selected = true;    	     
             	} // for loop end
             	
				return true;
		 	} // formValidations Function End
		 	
		 	function backButtonFunction()
		  	{
		  		location.href="DValidationScreen.jsp";
		  	} // backButtonFunction end		 
	</script>
	</head>
	<body onload="onloadFunction()" >
	<form id="ChartGenerationForm" name="ChartGenerationForm" action="tempDValidationAdd.jsp" method="post" onsubmit="return formValidations()">	
		<table style=" border-collapse: collapse; margin-top: 0;" cellpadding="0" cellspacing="0" width="100%" height="100%"  border=0 valign="top">
  			<tr>
    			<td class="NormalB">FilteredBY Group : <br>
    				<select name="LeftDEGroupsCB" id="LeftDEGroupsCB" onchange="leftDEGroupsCBChange()" style="width: 330">
					</select>
				</td>
    			<td class="NormalB">&nbsp;</td>
    			<td class="NormalB">Validation Name : <br><input type="text" name="VNameTB" id="VNameTB" size="50"></td>
  			</tr>
  			<tr>
    			<td class="NormalB">LeftSide Available DataElement List : <br>
    				<select name="LeftAvailDEListCB" id="LeftAvailDEListCB" multiple size="6" style="width: 330">
		  			</select>														 
				</td>
    			<td class="NormalB"><input type="button" name="LeftG" id="LeftG" value=">" onclick="leftGFunction()"><br>
    			 					<input type="button" name="LeftL" id="LeftL" value="<" onclick="leftLFunction()">
    			</td>
    			<td class="NormalB">LeftSide Selected DataElement List : <br>
    				<select name="LeftSelDEList" id="LeftSelDEList" multiple size="6" style="width: 330">
    				</select>
    			</td>
  			</tr>
  			<tr>
    			<td class="NormalB">FilteredBY Group :  <br>
    				<select name="RightDEGroupsCB" id="RightDEGroupsCB" onchange="rightDEGroupsCBChange()" style="width: 330">
    				</select>	
				</td>
    			<td class="NormalB">&nbsp;</td>
    			<td class="NormalB">Validation Operator : <select id="VOperatorLB" name="VOperatorLB" style="width: 60">
													<option value="Equal">=</option>
													<option value="Less"><</option>
													<option value="LessOrEqual"><=</option>
													<option value="Greater">></option>
													<option value="GreaterOrEqual">>=</option>													
												</select>
									LeftCriteria &nbsp;&nbsp;: <input type="text" name="leftPercent" id="leftPercent" size="2" disabled/> %
								<br>Validation Type : <select id="VTypeLB" name="VTypeLB" onchange="VTypeLBChangeFunction(this.value)">
														<option value="A" selected>Absolute</option>
														<option Value="E">Expert</option>
													  </select>
									RightCriteria : <input type="text" name="rightPercent" id="rightPercent" size="2" disabled/> %				  
				</td>
  			</tr>
  			<tr>
    			<td class="NormalB">RightSide Available DataElement List : <br>
    				<select name="RightAvailDEListCB" id="RightAvailDEListCB" multiple size="6" style="width: 330">
					</select>														 
				</td>
    			<td class="NormalB"><input type="button" name="RightG" id="RightG" value=">" onclick="rightGFunction()"> <br> 
    				<input type="button" name="RightL" id="RightL" value="<" onclick="rightLFunction()">
    			</td>
    			<td class="NormalB">RightSide Selected DataElement List : <br>
    				<select name="RightSelDEList" id="RightSelDEList" multiple size="6" style="width: 330">
    				</select>
    			</td>
  			</tr>
   			<tr>
    			<td class="NormalB">Leftside Description &nbsp;&nbsp;: <input name="leftDescTB" id="leftDescTB" type="text" size="27"></td>
    			<td class="NormalB">&nbsp;</td>
    			<td class="NormalB"><input name="AddValidation" id="AddValidation" value="AddValidation" type="submit" style="width: 120; height: 25; font-family:Arial; font-weight:bold; color:#800080"></td>
  			</tr>
  			<tr>
    			<td class="NormalB">Rightside Description : <input name="rightDescTB" id="rightDescTB" type="text" size="27"></td>
    			<td class="NormalB">&nbsp;</td>
    			<td class="NormalB"><input name="BackButton" id="BackButton" value="Back" type="button" onclick="backButtonFunction()" style="width: 120; height: 25; font-family:Arial; font-weight:bold; color:#800080"></td>
  			</tr>
		</table>
	</form>	
	</body>
</html>