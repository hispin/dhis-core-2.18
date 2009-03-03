<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<%
  
  // Input Parameters
  int selOrgUnitID = Integer.parseInt(request.getParameter("ouIDTB"));
  String startingDate = request.getParameter("sDateLB");
  String endingDate = request.getParameter("eDateLB");
  String ide_type = request.getParameter("riRadio");  
  String indicatorNamesList[] = request.getParameterValues("iListCB");
  
  //String envVarValuesForDM1 = request.getParameter("Action1");
  //String envVarValuesForDM2 = request.getParameter("Action2");
  
  //String partsOfEnvVarValuesForDM1[] = envVarValuesForDM1.split("@@");
  //String indicatorNamesList[] = envVarValuesForDM2.split("@@");
  
  // These are the input parameters
  //int selOrgUnitID = Integer.parseInt(partsOfEnvVarValuesForDM1[0]);
  //String startingDate = partsOfEnvVarValuesForDM1[1];
  //String endingDate = partsOfEnvVarValuesForDM1[2];  
  //String ide_type = partsOfEnvVarValuesForDM1[3];
%>

<jsp:useBean id="DataMartAction" scope="session" class="org.hisp.gtool.action.DataMartAction" />

<%
	out.println("<font face='arial'>");
	
	int orgUnitLevel = DataMartAction.getOrgUnitLevel(selOrgUnitID);
	out.println("<h3><u><center>DataMart For </u></center></h3><br/>");
	
	out.println("<h5>Orgunit Level : "+orgUnitLevel+"</h5>");
	out.println("<h5>Starting Period : "+startingDate+"</h5>");
	out.println("<h5>Ending Period : "+endingDate+"</h5>");
	out.println("<h5>Indicator/ DataElement List : </h5>");
	for(int i=0;i<indicatorNamesList.length;i++)
		{
			out.println("<h6><li>"+indicatorNamesList[i]+"</li></h6>"); 
		}	
	
	out.println("<h5>Staring Time : "+new java.util.Date()+"</h5>");
	
	int totRecCount = DataMartAction.aggregateIndicatorValues(selOrgUnitID,startingDate,endingDate,ide_type,indicatorNamesList);
	
	out.println("<h5>Ending Time : "+new java.util.Date()+"</h5>");
	if(totRecCount ==-1) out.println("<h5>Some Exception Occured</h5>");
	else out.println("<h5>"+totRecCount+" Records are inserted successfully</h5>");
		
	out.println("</font>");
%>
