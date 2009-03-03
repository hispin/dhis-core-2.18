
<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="DataValidationAction" scope="session" class="org.hisp.gtool.action.DataValidationAction" />
<jsp:useBean id="DataStausAction" scope="session" class="org.hisp.gtool.action.DataStatusAction" />
<jsp:useBean id="GAUtilities" scope="session" class="org.hisp.gtool.utilities.GAUtilities" />
<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />

<% 
  // To Play Audio
	String muteOpt = (String) session.getAttribute("muteOpt");
	if(muteOpt != null && muteOpt.equals("OFF"))
	{
		AudioAction.stopAudio();
		AudioAction.playAudio("t10.wav");
	}	
  
                  	  
  // Input Parameters
  int selOrgUnitID = Integer.parseInt(request.getParameter("ouIDTB"));
  String startingDate = request.getParameter("sDateLB");
  String endingDate = request.getParameter("eDateLB");
  String dVIdsList[] = request.getParameterValues("dataValidationListCB");
  String localLang = request.getParameter("LocalLangCB");	 
   	   
  int i=0;

  int selOrgUnitLevel = 1;
  int maxOrgUnitLevel = 1;
    
  String selOrgUnitName = "";

// These 2 are hard coded, we need to change these
  int periodIDs[] = new int[200]; 
  String periodNames[] = new String[200];

  int totDataSetMem = 1;
  int periodCount = 0;

  int dataSetID = -1;
  String dataSetName = "";

  String monthNames[] = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
  
  // if we want to use this Program to another state we need to give the level names of that state
  String levelNames[] = {"","State","District","Taluk","PHC","Subcentre"};

  int dataStatus =0;
  String query = "";
  String cellColor = "";
  
//  String strPeriodIDs = "-1";
  
  Hashtable htForChildOUTree = DataStausAction.getChildOrgUnitTree(selOrgUnitID);
  if(htForChildOUTree == null) {out.println("<h3>No Children</h3>");return ; }
  
  List liForChildOUIDs = (ArrayList) htForChildOUTree.get("childOUIDsList");
  List liForChildOUNames = (ArrayList) htForChildOUTree.get("childOUNamesList");
  List liForChildOULevles = (ArrayList) htForChildOUTree.get("childOULevelsList");
  maxOrgUnitLevel = ((Integer) Collections.max(liForChildOULevles)).intValue();
  List listForChildShortNames = (ArrayList) htForChildOUTree.get("childOUShortNamesList");
	
  //Hashtable htForDataSets = DataStausAction.getSelectedDataSetMemebers(dataSetIdsList);
  //if(htForDataSets == null) {out.println("<h3>No DataSets</h3>");return ; }
  //Vector keysFoDataSets = new Vector(htForDataSets.keySet());
  //Collections.sort(keysFoDataSets);
  //Iterator keysFoDataSetsIter = keysFoDataSets.iterator();
  
  List liForSelOU = (ArrayList) DataStausAction.getSelectedOUDetails(selOrgUnitID);
  selOrgUnitName = (String) liForSelOU.get(0);
  selOrgUnitLevel = ((Integer) liForSelOU.get(1)).intValue();
    
  List liForPeriods = (ArrayList) DataStausAction.getPeriodDetails(startingDate, endingDate, 1);
  Iterator itForPeriods = liForPeriods.iterator();
  
  Hashtable htForDataValidations = DataValidationAction.getAllDataValidations();
  Enumeration keysFordataValidations = htForDataValidations.keys();
  
%>

<html>
	<head>   
    	<title>DataValidation</title>
    	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    	<meta http-equiv="description" content="this is my page">
    	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    	
    	<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />   
    	<script>
    		var request;
    		var OrgID;
    		var PID;
    		var oldLdvtbValue = 0;
    		var oldRdvtbValue = 0;
    		var leftDVVal = new Array();
    		var rightDVVal =  new Array();
    		var curIndex = 0;
    		var curLength = 0;
    					
			function createRequestObject()
			{
				if(XMLHttpRequest)		request = new XMLHttpRequest();
				else request = new ActiveXObject("Microsoft.XMLHTTP");
			}
												
			function correctionValidationFunction(evt)
				{	
				
					var deid = evt.target.name;
					var dvalue = evt.target.value;
					//evt.target.style.background = "green";
					curdvtbHB.value = evt.target.id;
																					
					createRequestObject();
					request.onreadystatechange = displayResult;
					request.open("GET","correctDVValues.jsp?deid="+deid+"&dvalue="+dvalue+"&ouid="+OrgID+"&periodid="+PID,true);
					request.send(null);					
				}
    		
    		function displayResult()
				{
										
					if(request.readyState ==4)
					{
						if(request.status ==200)
						{														
							var response = request.responseXML;							
							var resultValues = response.getElementsByTagName("option");																												
							var statusValue = resultValues[0].firstChild.data;
							
							var leftdvTB = dvLeftValHB.value;
							var rightdvTB = dvRightValHB.value;
							var leftdvTBValue = document.getElementById(leftdvTB).innerHTML;
							var rightdvTBValue = document.getElementById(rightdvTB).innerHTML;
							var curdvtb = curdvtbHB.value;
							var curdvtbValue = document.getElementById(curdvtb).value;
									
							
							if(statusValue < 0) {document.getElementById(curdvtb).style.background = "red";}
							else 
								{																		
									if(curdvtb.substring(-1,3)=="LTB")
									{
										//alert(leftdvTBValue+"   "+oldLdvtbValue+"   "+curdvtbValue);
										//leftdvTBValue = parseInt(leftdvTBValue) - parseInt(oldLdvtbValue) + parseInt(curdvtbValue);
										if(leftDVVal[curIndex] == curdvtbValue) return;
										leftDVVal[curIndex] = curdvtbValue;
										var k;
										leftdvTBValue = 0;
										for(k=0;k<curLength;k++)
										{
										leftdvTBValue = parseInt(leftdvTBValue) + parseInt(leftDVVal[k]);
										}
										document.getElementById(leftdvTB).style.background = "yellow";
										document.getElementById(leftdvTB).innerHTML = leftdvTBValue;
									}	
									else
									{
										//alert(rightdvTBValue+"   "+oldRdvtbValue+"   "+curdvtbValue);
										//rightdvTBValue = parseInt(rightdvTBValue) - parseInt(oldRdvtbValue) + parseInt(curdvtbValue);
										if(rightDVVal[curIndex] == curdvtbValue) return;
										rightDVVal[curIndex] = curdvtbValue;
										var k;
										rightdvTBValue = 0;
										for(k=0;k<curLength;k++)
										{
										rightdvTBValue = parseInt(rightdvTBValue) + parseInt(rightDVVal[k]);
										}
										document.getElementById(rightdvTB).style.background = "yellow";
										document.getElementById(rightdvTB).innerHTML = rightdvTBValue;
									}
									document.getElementById(curdvtb).style.background = "yellow";
								}	
						}// status
					} // readyState
				}// displayIList	
    		
    		
    		function DVDetailsFunction(leftdenames,rightdenames,leftPercent,rightPercent)
    		{
    		  alert("Left % : "+leftPercent+"\nRight % : "+rightPercent+"\nLV: "+leftdenames+"\nRV: "+rightdenames);
    		}
			
			function infoBoxCloseFunction()
			{
			  	infoBox.innerHTML="";
			}//infoBoxCloseFunction
    		
    		function DVUpdateFunction(noOfRows,ouName,period,leftdeids,leftdenames,leftdeval,rightdeids,rightdenames,rightdeval,ouID,periodID,dvleftvalHB,dvrightvalHB)
			{
				OrgID = ouID;
				PID = periodID;
				
				var ldeidList = leftdeids.split(",");
				var ldenameList = leftdenames.split(",");
				var ldevalList = leftdeval.split(",");				
				var rdeidList =	rightdeids.split(",");			
				var rdenameList = rightdenames.split(",");	
				var rdevalList =  rightdeval.split(",");
				
				var tempLeftDEList = "";
				var tempRightDEList = "";
				var noOfBreaks = "";		
				
				var tbColor = "red";		
				
				dvLeftValHB.value = dvleftvalHB;
				dvRightValHB.value = dvrightvalHB;
				var i=0;
				for(i=0;i<ldenameList.length-1;i++)
				{
				 tempLeftDEList += "<br/>"+ldenameList[i]+" : <input type='text' size='4' id='LTB"+ldeidList[i]+"' name='"+ldeidList[i]+"' onblur='javascript:curIndex = "+parseInt(i)+"; javascript:curLength = "+parseInt(ldenameList.length-1)+";correctionValidationFunction(event)' value='"+ldevalList[i]+"'>";
				 leftDVVal[i] = ldevalList[i];
				 //noOfRows--;
				}
				for(i=0;i<rdenameList.length-1;i++)
				{
				 tempRightDEList += "<br/>"+rdenameList[i]+" : <input type='text' size='4' id='RTB"+rdeidList[i]+"' name='"+rdeidList[i]+"' onblur='javascript:curIndex = "+parseInt(i)+"; javascript:curLength = "+parseInt(rdenameList.length-1)+";correctionValidationFunction(event)' value='"+rdevalList[i]+"'>";
				 rightDVVal[i] = rdevalList[i];
				 //noOfRows--;
				}
				for(i=1;i<noOfRows;i++)
				{
				noOfBreaks += "<tr height='25' class='TableHeadingCellStyles'><td class='TableHeadingCellStyles'>&nbsp;</td></tr>";
				}
				
				infoBox.innerHTML = "<br><br><div align='left'><font face='arial' size='2'><b>&nbsp;</b></font></div>"+
									"<table border='0' cellpadding='0' cellspacing='0' style='border-collapse: collapse' width='100%'>	"+noOfBreaks+										
										"<tr bgcolor='#9999CC'><td align='right' class='TableHeadingCellStyles'><a href='javascript:infoBoxCloseFunction()'><img src='images/close.png' border='0'></a>&nbsp;&nbsp;</td></tr>"+
										"<tr  bgcolor='#9999CC'><td class='TableHeadingCellStyles'>"+
												 "<font face='arial' size='2'><b>"+
												 "OrgUnit : </b><br>"+ouName+"<br><br><b>Period : </b><br>"+period+"<br/><br><b>LeftValues : </b>"+
												 tempLeftDEList+"<br><br><b>RightValues : </b>"+tempRightDEList+												 
											 "</font></td></tr><tr bgcolor='#9999CC'><td align='right' class='TableHeadingCellStyles'>&nbsp;</td></tr>"+
											 "<tr bgcolor='#9999CC'><td align='right' class='TableHeadingCellStyles'>&nbsp;</td></tr></table>";			
			}//DVUpdateFunction
    	</script>
	</head>
	<body >
		<center>
			<font face="arial" size="3"><b>Data Validations</b></font>
		</center>
		<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" width="100%">
			<tr><td class="TableHeadingCellStyles" width="68%" valign="top">
<%		
		int count1 = 0;
		int count = 0;
		int noOfRows = 2;
		//This is to get DataValidations - 				
		while(count < dVIdsList.length)
		{
			String dvID = dVIdsList[count];
			ArrayList liForDataValidations = (ArrayList) htForDataValidations.get(dvID);
			String dvName  = (String) liForDataValidations.get(0);
			String dvleftdeids = (String) liForDataValidations.get(1);
			String dvrightdeids = (String) liForDataValidations.get(2);
			String dvoperator = (String) liForDataValidations.get(3);
			String dvtype = (String) liForDataValidations.get(4);
			String dvleftdesc = (String) liForDataValidations.get(5);
			String dvrightdesc = (String) liForDataValidations.get(6);
			String dvleftdes = (String) liForDataValidations.get(7);
			String dvrightdes = (String) liForDataValidations.get(8);
			double leftPercent = Double.parseDouble((String) liForDataValidations.get(9));
			double rightPercent = Double.parseDouble((String) liForDataValidations.get(10));
			%>
			<br><br>
			<div align="left"><font face="arial" size="2"><b>DataValidation : <a href="javascript:DVDetailsFunction('<%=dvleftdes%>','<%=dvrightdes%>',<%=leftPercent%>,<%=rightPercent%>)"><%=dvName%></a></b></font></div>
			<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" width="100%">		
				<tr style="background-color:#dddddd" class="TableHeadingCellStyles"  height="25" >
					<%
					for(count1=selOrgUnitLevel+1;count1<=maxOrgUnitLevel;count1++)
					{ 
					%>
						<th class="TableHeadingCellStyles" align="center"><%=levelNames[count1]%></th>
					<%
					}// for end
					%>
					<th class="TableHeadingCellStyles" align="center" >Period</th>
					<th class="TableHeadingCellStyles" align="center" ><%=dvleftdesc%></th>
					<th class="TableHeadingCellStyles" align="center" >Ope</th>
					<th class="TableHeadingCellStyles" align="center" ><%=dvrightdesc%></th>
					<th class="TableHeadingCellStyles" align="center" >&nbsp;</th>
				</tr>				
				<%
					Iterator itForChildOUIDs = liForChildOUIDs.iterator();
					Iterator itForChildOUNames = liForChildOUNames.iterator();
					Iterator itForChildOULevels = liForChildOULevles.iterator();
					Iterator itForChildOUSNames = listForChildShortNames.iterator();
		
					while(itForChildOUIDs.hasNext())
					{						
						int childOUID = ((Integer)itForChildOUIDs.next()).intValue();
						String childOUName = (String) itForChildOUNames.next();
						int childOULevel = ((Integer) itForChildOULevels.next()).intValue();
						List parentList = (ArrayList) DataStausAction.getParentOrgUnitTree(childOUID,localLang);
						
						if(localLang != null)   childOUName = (String) itForChildOUSNames.next();
						
						itForPeriods = liForPeriods.iterator();
						while(itForPeriods.hasNext())
						{
							List templi = (ArrayList) itForPeriods.next();
							int periodID = ((Integer)templi.get(0)).intValue();
							String startDate = (String)templi.get(1);
							String monYearFormat = GAUtilities.getMonYearFormat(startDate);
								
							List li = (ArrayList) DataValidationAction.getDataValidationStatus(childOUID,dvleftdeids,dvrightdeids,dvoperator,periodID,localLang,leftPercent,rightPercent);
							String dvLeftValue = (String) li.get(0);
							String dvRightValue = (String) li.get(1);
							String dvStatus = (String) li.get(2);
							String dvOpe = (String) li.get(3);
							String strLeftDEIDs = (String) li.get(4);
							String strLeftDENames = (String) li.get(5);
							String strLeftDEValues = (String) li.get(6);
							String strRightDEIDs = (String) li.get(7);
							String strRightDENames = (String) li.get(8);
							String strRightDEValues = (String) li.get(9);

							if(dvStatus.equals("T"))  continue;
							else 
								{ 
									String tempOUName ="";
									String curdvLeftValID = "dvLeftValHB"+noOfRows;
									String curdvRightValID = "dvRightValHB"+noOfRows;
									
									%>
									<tr height="25">
									<%
									int flag=0;
									for(count1=selOrgUnitLevel+1;count1<=maxOrgUnitLevel;count1++)
									{ 
										if(count1==childOULevel) {tempOUName = childOUName; flag=1;}										
										else if(flag==1) tempOUName=" ";
										else tempOUName = (String) parentList.get(count1);										
										%>
										<td class="TableHeadingCellStyles"><%=tempOUName%></td>
										<%
									} // for end									
									%>
										<td class="TableHeadingCellStyles" align="center"><%=monYearFormat%></td>
										<td class="TableHeadingCellStyles" align="center"><p id="<%=curdvLeftValID%>" name="<%=curdvLeftValID%>"><%=dvLeftValue%></p></td>
										<td class="TableHeadingCellStyles" align="center"><%=dvOpe%></td>
										<td class="TableHeadingCellStyles" align="center"><p id="<%=curdvRightValID%>" name="<%=curdvRightValID%>"><%=dvRightValue%></p></td>
										<td class="TableHeadingCellStyles" align="center">
											<input class="TableHeadingCellStyles" align="center" type="button" value='Update' onclick="DVUpdateFunction(<%=noOfRows%>,'<%=childOUName%>','<%=monYearFormat%>','<%=strLeftDEIDs%>','<%=strLeftDENames%>','<%=strLeftDEValues%>','<%=strRightDEIDs%>','<%=strRightDENames%>','<%=strRightDEValues%>',<%=childOUID%>,<%=periodID%>,'<%=curdvLeftValID%>','<%=curdvRightValID%>')"/>
										</td>
									</tr>
									<%
									noOfRows++;
								}// else end							
						}// Period While loop end												
					}// ChildOrgUnit While loop end		
				%>
			</table>			
			<%
			count++;
		}// DataValidation While loop end							
%>	
</td>
<td class="TableHeadingCellStyles" width="2%" valign="top">&nbsp;</td>
<td class="TableHeadingCellStyles" width="30%" valign="top"><p id="infoBox" name="infoBox">&nbsp;</p></td>
</tr>
</table>
<input type="hidden" name="dvLeftValHB" id="dvLeftValHB" value="dvleft" />
<input type="hidden" name="dvRightValHB" id="dvRightValHB" value="dvright" />
<input type="hidden" name="curdvtbHB" id="curdvtbHB" value="curdvtb" />
