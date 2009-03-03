<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="DataValidationAction" scope="session" class="org.hisp.gtool.action.DataValidationAction" />

<%
	String dvID = request.getParameter("dvid");	
	boolean delStatus = DataValidationAction.removeDataValidation(dvID);
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
				location.href="DValidationList.jsp";
			}
		</script>
	</head>
	<body onload="onLoadFunction()"></body>
</html>