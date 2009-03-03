<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="GAUtilities" scope="session" class="org.hisp.gtool.utilities.GAUtilities" />
<jsp:useBean id="DataValidationAction" scope="session" class="org.hisp.gtool.action.DataValidationAction" />

<%
	// Input Parameters
	String dvName = request.getParameter("VNameTB");
	String leftDesc = request.getParameter("leftDescTB");
	String rightDesc = request.getParameter("rightDescTB"); 
	String dvOperator = request.getParameter("VOperatorLB"); 
	String dvType = request.getParameter("VTypeLB"); 
	String leftSelDEList[] = request.getParameterValues("LeftSelDEList");
	String rightSelDEList[] = request.getParameterValues("RightSelDEList");
	String leftPercent = request.getParameter("leftPercent");
	String rightPercent = request.getParameter("rightPercent");
	
	if(leftPercent == null || leftPercent.equals("")) leftPercent = "0";
	if(rightPercent == null || rightPercent.equals("")) rightPercent = "0";
		
	String leftSelDEs = GAUtilities.getConcatedString(leftSelDEList, ",");
	String rightSelDEs = GAUtilities.getConcatedString(rightSelDEList, ",");  

	Hashtable htForDataValidations = DataValidationAction.getAllDataValidations();
	Vector DataValidationIds = new Vector(htForDataValidations.keySet());
	String maxDVId = (String) Collections.max(DataValidationIds);
	String curDVId = "" + (Integer.parseInt(maxDVId)+1);
	
	List li = new ArrayList();
	li.add(0,curDVId);
	li.add(1,dvName);
	li.add(2,leftDesc);
	li.add(3,rightDesc);
	li.add(4,dvOperator);
	li.add(5,dvType);
	li.add(6,leftSelDEs);
	li.add(7,rightSelDEs);
	li.add(8,leftPercent);
	li.add(9,rightPercent);
		
	boolean addDVStatus = DataValidationAction.addDataValidation(li);
%>
<html>
	<head>
		<title>Graphical Analyser</title>
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    	<meta http-equiv="description" content="this is my page">
    	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    	
		<script>
			function onLoadFunction()
			{
			  if(<%=addDVStatus%>) 
			  	{
			  		alert("Validation is Successfully Added");
			  		location.href="DValidationScreen.jsp";
			  	}
			  else
			  	{
			  		alert("Failure while Adding...Try Again");
			  		location.href="DValidationAdd.jsp";
			  	}		
			}
		</script>
	</head>
	<body onload="onLoadFunction()">
	<p align="left">	
		<font face="arial" size="2">
			<li>Validation Id : <%=curDVId%><br>
			<li>Validation Name : <%=dvName%><br>
			<li>Left Description : <%=leftDesc%><br/>
			<li>Right Description : <%=rightDesc%><br/>
			<li>Validation Operator : <%=dvOperator%><br/>
			<li>Validation Type : <%=dvType%><br/>			
			<li>Left Sel DEList : <%=leftSelDEs%><br/>
			<li>Right Sel DEList : <%=rightSelDEs%><br/>
			<li>Left PerCentage : <%=leftPercent%><br/>
			<li>Right PerCentage : <%=rightPercent%><br/>
		</font>
	</p>	
	</body>
</html>