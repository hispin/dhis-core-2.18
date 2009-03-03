<Validation-Values>
<%@ page contentType="text/xml ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="DataValidationAction" scope="session" class="org.hisp.gtool.action.DataValidationAction" />

<% 
  	// Input Parameters
	int deId   = Integer.parseInt(request.getParameter("deid"));
	int dValue = Integer.parseInt(request.getParameter("dvalue"));
	int ouId   = Integer.parseInt(request.getParameter("ouid"));
	int periodId = Integer.parseInt(request.getParameter("periodid"));
	
	int status = DataValidationAction.correctDVDataValues(ouId,deId,periodId,dValue);
%>
	<option value='<%=status%>'><%=status%></option>
</Validation-Values>


