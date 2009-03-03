
<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="DataValidationAction" scope="session" class="org.hisp.gtool.action.DataValidationAction" />
<jsp:useBean id="DataStausAction" scope="session" class="org.hisp.gtool.action.DataStatusAction" />
<jsp:useBean id="GAUtilities" scope="session" class="org.hisp.gtool.utilities.GAUtilities" />

<%
 Hashtable htForDataValidations = DataValidationAction.getAllDataValidations();
 Enumeration keysFordataValidations = htForDataValidations.keys();
%>
 
<html>
	<head>
		<title>Graphical Analyser</title>
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    	<meta http-equiv="description" content="this is my page">
    	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		
		<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
		<script>						
			function DVRemoveFunction(dvID)
			{				
				location.href="DValidationDel.jsp?dvid="+dvID;
			}//DVRemoveFunction end
			
			function DVEditFunction(dvID)
			{				
				parent.location.href="DValidationEdit.jsp?dvid="+dvID;
			}//DVEditFunction end
			
			function DVInfoDetailsFunction(dvname,dvope,dvtype,dvleftdesc,dvrightdesc,leftPercent,rightPercent)
			{
				parent.infoBox.innerHTML = "<table width='100%' bgcolor='#9999CC'>"+
												"<tr><td><div align='right'><a href='javascript:infoBoxCloseFunction()'>"+
												"<img src='images/close.png' border='0'></a></div>"+
												"<font face='Arial' size='2'><b>Name : </b><br>"+dvname+
												"<br><br><b>Operator : </b><br>"+dvope+
												"<br><br><b>Type : </b><br>"+dvtype+
												"<br><br><b>LeftSide Description : </b><br>"+dvleftdesc+
												"<br><br><b>RightSide Description : </b><br>"+dvrightdesc+
												"<br><br><b>Left Percentage : </b><br>"+leftPercent+
												"<br><br><b>Right Percentage : </b><br>"+rightPercent+
												"</font></td></tr></table>";			
			}//DVInfoDetailsFunction
		</script>
	</head>
	<body onload="onloadFunction()" >
		<table width="100%">
			<tr style="background-color:#dddddd"><th width="79%" align="left"><font face="Arial" size="2">Name</font></th><th width="21%" colspan="3" ><font face="Arial" size="2">Operations</font></th></tr>
      			<%
      			String rowColor="#dddddd";
      			while(keysFordataValidations.hasMoreElements())
				{
					String dvID = (String) keysFordataValidations.nextElement();			
					ArrayList liForDataValidations = (ArrayList) htForDataValidations.get(dvID);
					String dvName  = (String) liForDataValidations.get(0);
					String dvleftdeids = (String) liForDataValidations.get(1);
					String dvrightdeids = (String) liForDataValidations.get(2);
					String dvoperator = (String) liForDataValidations.get(3);
					String dvtype = (String) liForDataValidations.get(4);
					String dvleftdesc = (String) liForDataValidations.get(5);
					String dvrightdesc = (String) liForDataValidations.get(6);
      				String deleftdes = (String) liForDataValidations.get(7);
      				String derightdes = (String) liForDataValidations.get(8);
      				String leftPercent = (String) liForDataValidations.get(9);
      				String rightPercent = (String) liForDataValidations.get(10);
      				
      				if(rowColor.equals("#dddddd")) rowColor="#ffffff";
      				else rowColor="#dddddd";
      				%>
      				<tr style="background-color:<%=rowColor%>" class="DefaultFontStylesNoBold">
      					<td width="79%" ><font face="Arial" size="2"><%=dvName%></font></td>
      					<td width="7%" align="center"><a href="javascript:DVEditFunction('<%=dvID%>')"><img src="images/edit.png" border="0"></a></td>
      					<td width="7%" align="center"><a href="javascript:DVRemoveFunction('<%=dvID%>')"><img src="images/delete.png" border="0"></a></td>
      					<td width="7%" align="center"><a href="javascript:DVInfoDetailsFunction('<%=dvName%>','<%=dvoperator%>','<%=dvtype%>','<%=dvleftdesc%>','<%=dvrightdesc%>','<%=leftPercent%>','<%=rightPercent%>')"><img src="images/information.png" border="0"></a></td>
      				</tr>
      				<%
      			}
      			%>	
    	  		</table>      	  		
	</body>
</html>