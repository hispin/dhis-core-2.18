<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<%
	session.setAttribute("loginStatus","0");
    session.setAttribute("loginUName",null); 
%>

<html>
	<head>
		<title>Graphical Analyser</title>
		<script>
			function onLoadFunction()
			{
			 parent.location.href="index.jsp";
			}
		</script>
	</head>
	<body onload="onLoadFunction()">
	</body>
</html>