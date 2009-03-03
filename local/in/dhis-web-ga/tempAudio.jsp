<Audio-Format>
<%@ page contentType="text/xml ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />

<% 
  	// Input Parameters
	int muteOpt   = Integer.parseInt(request.getParameter("muteOpt"));
	if(muteOpt==2) 
	{	
		AudioAction.stopAudio();
		session.setAttribute("muteOpt","ON");
	}	
	else 
	{	
		//AudioAction.playAudio(" ");	
		session.setAttribute("muteOpt","OFF");
	}	
%>
	<option value='1'>1</option>
</Audio-Format>
