
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
  int periodCount = 1;

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
	<head><title>DataStatus</title></head>
	<body>
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
			itForPeriods = liForPeriods.iterator();
			int periodID = 0;
			while(itForPeriods.hasNext())
			{
				List templi = (ArrayList) itForPeriods.next();
				periodID = ((Integer)templi.get(0)).intValue();
			}// Period While loop end							
		%>
		<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse; " bordercolor="#111111">		
			<tr>
				<%												
				int count1 = 0;						
				for(count1=selOrgUnitLevel+1;count1<=maxOrgUnitLevel;count1++)
				{ 
				%>
					<td><font face="arial" size="2"><b><%=levelNames[count1]%></b></font></td>
				<%
				}// for end
				keysFoDataSetsIter = keysFoDataSets.iterator();
				while(keysFoDataSetsIter.hasNext())
				{
					String strDataSetID = (String)keysFoDataSetsIter.next();							
					ArrayList liForDataSets = (ArrayList) htForDataSets.get(strDataSetID);
					dataSetName  = (String) liForDataSets.get(0);
					%>
					<td><font face="arial" size="1"><b><%=dataSetName%></b></font></td>
					<%
				}
				%>
					<td><font face="arial" size="1"><b>Over all %</b></font></td>
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
						<td><font face="arial" size="1"><%=tempOUName%></font></td>
						<%
					} // for end
					totDataStatusPer = 0.0;										
					String cellData = "";
					
					int count2 = 0;
					keysFoDataSetsIter = keysFoDataSets.iterator();
					while(keysFoDataSetsIter.hasNext())
					{
						String strDataSetID = (String)keysFoDataSetsIter.next();
						dataSetID = Integer.parseInt(strDataSetID);
			
						ArrayList liForDataSets = (ArrayList) htForDataSets.get(strDataSetID);
						dataSetName  = (String) liForDataSets.get(0);
						int totNoOfDSMemebers = ((Integer) liForDataSets.get(1)).intValue();			
													
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
						count2++;
					}// DataSet While loop end
						
					if(totDataStatusPer/count2 >= 75)  cellColor = "green";
					else if (totDataStatusPer/count2 >=40 && totDataStatusPer/count2 < 75) cellColor = "yellow";
					else cellColor = "red";

					if(childOULevel!=maxOrgUnitLevel) { cellColor = "white"; cellData="";}
					else cellData = ""+( Math.round( (totDataStatusPer/count2) *Math.pow(10,1)) / Math.pow(10,1));											
				%>
					<td bgcolor="<%=cellColor%>" align="center"><font face="Arial" size="1" color="#FF6633"><%=cellData%></font></td>				
				</tr>
				<%								
				}// ChildOrgUnit While loop end		
				%>
			</table>				