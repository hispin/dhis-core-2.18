
<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />

<%
// To Play Audio
String muteOpt = (String) session.getAttribute("muteOpt");
if(muteOpt != null && muteOpt.equals("OFF"))
{
	AudioAction.stopAudio();
	AudioAction.playAudio("t9.wav");
}	
%> 
<html>
	<head>
		<title>Graphical Analyser</title>
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    	<meta http-equiv="description" content="this is my page">
    	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		
		<script>
		    function DVAddFunction()
		    {
		    	location.href="DValidationAdd.jsp";
		    } // DVAddFunction end
		    
		    function infoBoxCloseFunction()
			{
			  	infoBox.innerHTML="";
			}//infoBoxCloseFunction
			
		  	function DVViewFunction()
		  	{
		  		location.href="DVViewScreen.jsp";
		  	} // DVViewFunction end
		  	
		  	function onloadFunction()
		  	{
		  		infoBox.innerHTML = "";	
		  	}
		</script>
	</head>
	<body onload="onloadFunction()" >
		<table width="100%" height="100%">
			<tr>
				<td width="70%">
					<div align="right">
						<input type="button" id="AddValiation" name="AddValiation" value="Add" onclick="DVAddFunction()" />
						<input type="button" id="ViewValiation" name="ViewValiation" value="View" onclick="DVViewFunction()" />
					</div>					
   	  			</td>
   	  			<td width="30%" style="vertical-align:top">
   	  			</td>
   	  		</tr>
			<tr>
				<td width="70%">
					<iframe name="DVListPane" id="DVListPane" src="DValidationList.jsp" scrolling=auto frameborder="0" width="100%" height="100%"></iframe>
   	  			</td>
   	  			<td width="30%" style="vertical-align:top"><p id="infoBox" name="infoBox">&nbsp;</p>
   	  			</td>
   	  		</tr>
   	  	</table>	
	</body>
</html>