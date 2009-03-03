<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>


<html>
	<head>
		<title>Graphical Analyser</title>
		<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
	</head>
	<body>
		<form id="loginForm" name="loginForm" action="LoginValidation.jsp" method="post">
			<table align="center" valign="center" width="30%">
				<tr>
					<td class="DefaultFontStyles">UserName : </td>
					<td>
						<input id="unameTF" name="unameTF" type="text">
						<input id="nextPageTF" name="nextPageTF" type="hidden" value="GAnalysisScreen.jsp">
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="DefaultFontStyles">Password : </td>
					<td><input id="pwdTF" name="pwdTF" type="password"></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>				
				<tr>
					<td>&nbsp;</td>
					<td align="center"><input type="submit" value="Login">&nbsp;&nbsp;&nbsp;<input type="reset" value="Cancel"></td>
				</tr>			
			</table>
		</form>	
	</body>
</html>