
<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="DataStausAction" scope="session" class="org.hisp.gtool.action.DataStatusAction" />
<jsp:useBean id="GAUtilities" scope="session" class="org.hisp.gtool.utilities.GAUtilities" />

<% 
  String urlForSR = request.getParameter("Action");
  String partsOfUrl[] = urlForSR.split("@@");
	                	  
  int i=0;

  int selOrgUnitLevel = 1;
  int maxOrgUnitLevel = 1;
  
  // these are the input parameters
  int selOrgUnitID = Integer.parseInt(partsOfUrl[0]);
  String startingDate = partsOfUrl[1];
  String endingDate = partsOfUrl[2];
  

  String selOrgUnitName = "";

  double dataStatusPer  = 0.0;
  double totDataStatusPer =0.0;
  double selOrgUnitDataStatus[];

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
	

  Hashtable htForDataSets = DataStausAction.getAllDataSetMemebers();
  if(htForDataSets == null) {out.println("<h3>No DataSets</h3>");return ; }
  Vector keysFoDataSets = new Vector(htForDataSets.keySet());
  Collections.sort(keysFoDataSets);
  Iterator keysFoDataSetsIter = keysFoDataSets.iterator();
  
  List liForSelOU = (ArrayList) DataStausAction.getSelectedOUDetails(selOrgUnitID);
  selOrgUnitName = (String) liForSelOU.get(0);
  selOrgUnitLevel = ((Integer) liForSelOU.get(1)).intValue();
    
  List liForPeriods = (ArrayList) DataStausAction.getPeriodDetails(startingDate, endingDate, 1);
  Iterator itForPeriods = liForPeriods.iterator();
%>

<html>
	<head>   
    	<title>DataStatus</title>   
	</head>
	<body >
		<center>
			<font face="arial" size="3"><b><%=selOrgUnitName%> Data Entry Status</b></font>
		</center>
		<div align="right">
			<table width="30%">
				<tr><td bgcolor="green">&nbsp;</td><td><font face="arial" size="1"> &nbsp;Completed (75+)</font></td></tr>
				<tr><td bgcolor="yellow">&nbsp;</td><td><font face="arial" size="1"> &nbsp;Partially Completed (40 - 75)</font></td></tr>
				<tr><td bgcolor="red">&nbsp;</td><td><font face="arial" size="1"> &nbsp;Not Completed (< 40)</font></td></tr>
			</table>
		</div>
	
<%		
		int count1 = 0;
		//This is to get Datasets - id,name,totalnumber of dataelements that are members of dataset				
		while(keysFoDataSetsIter.hasNext())
		{
			String strDataSetID = (String)keysFoDataSetsIter.next();
			dataSetID = Integer.parseInt(strDataSetID);
			
			ArrayList liForDataSets = (ArrayList) htForDataSets.get(strDataSetID);
			dataSetName  = (String) liForDataSets.get(0);
			int totNoOfDSMemebers = ((Integer) liForDataSets.get(1)).intValue();
			
			%>
			<br><br>
			<div align="left"><font face="arial" size="3"><b>DataSet : <%=dataSetName%></b></font></div>
			<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse" width="100%">		
				<tr>
					<%
					for(count1=selOrgUnitLevel+1;count1<=maxOrgUnitLevel;count1++)
					{ 
					%>
						<td><font face="arial" size="2"><b><%=levelNames[count1]%></b></font></td>
					<%
					}// for end

					// strPeriodIDs = "-1";
					periodCount = 0;
					itForPeriods = liForPeriods.iterator();
					while(itForPeriods.hasNext())
					{
						List templi = (ArrayList) itForPeriods.next();
						int periodID = ((Integer)templi.get(0)).intValue();
						String startDate = (String)templi.get(1);
						String monYearFormat = GAUtilities.getMonYearFormat(startDate);
						periodCount++;
						// strPeriodIDs += ","+periodID;
						%>
						<td><font face="arial" size="2"><b><%=monYearFormat%></b></font></td>		
						<%
					}// Period While loop end
					%>
					<td><font face="arial" size="2"><b>Overall %</b></font></td>
				</tr>
				<%
					Iterator itForChildOUIDs = liForChildOUIDs.iterator();
					Iterator itForChildOUNames = liForChildOUNames.iterator();
					Iterator itForChildOULevels = liForChildOULevles.iterator();
		
					while(itForChildOUIDs.hasNext())
					{
						%>
						<tr>
						<%
						int childOUID = ((Integer)itForChildOUIDs.next()).intValue();
						String childOUName = (String) itForChildOUNames.next();
						int childOULevel = ((Integer) itForChildOULevels.next()).intValue();
						
						String tempOUName ="";
						for(count1=selOrgUnitLevel+1;count1<=maxOrgUnitLevel;count1++)
						{ 
							if(count1==childOULevel) tempOUName = childOUName;
							else tempOUName = "";
							%>
							<td><font face="arial" size="2"><%=tempOUName%></font></td>
							<%
						} // for end
						totDataStatusPer = 0.0;										
						String cellData = "";
						
						itForPeriods = liForPeriods.iterator();
						while(itForPeriods.hasNext())
						{
							List templi = (ArrayList) itForPeriods.next();
							int periodID = ((Integer)templi.get(0)).intValue();
							String startDate = (String)templi.get(1);
							
							dataStatus = DataStausAction.getStatusByPeriod(childOUID,dataSetID,periodID);
							dataStatusPer =  ((double)dataStatus / (double)totNoOfDSMemebers) * 100.0;					
							totDataStatusPer += dataStatusPer;
							
							if(dataStatusPer >= 75)  cellColor = "green";
							else if (dataStatusPer >=40 && dataStatusPer < 75) cellColor = "yellow";
							else cellColor = "red";

							if(childOULevel!=maxOrgUnitLevel) { cellColor = "white"; cellData="";}
							else cellData = ""+( Math.round(dataStatusPer*Math.pow(10,1)) / Math.pow(10,1));
							%>
							<td bgcolor="<%=cellColor%>" align="center"><font face="Arial" size="1" color="#FF6633"><%=cellData%></font></td>
							<%
						}// Period While loop end
						
						if(totDataStatusPer/periodCount >= 75)  cellColor = "green";
						else if (totDataStatusPer/periodCount >=40 && totDataStatusPer/periodCount < 75) cellColor = "yellow";
						else cellColor = "red";

						if(childOULevel!=maxOrgUnitLevel) { cellColor = "white"; cellData="";}
						else cellData = ""+( Math.round( (totDataStatusPer/periodCount) *Math.pow(10,1)) / Math.pow(10,1));											
						%>
							<td bgcolor="<%=cellColor%>" align="center"><font face="Arial" size="1" color="#FF6633"><%=cellData%></font></td>				
						</tr>
						<%								
					}// ChildOrgUnit While loop end		
				%>
			</table>			
			<%
		}// DataSet While loop end							
%>	

</table>
