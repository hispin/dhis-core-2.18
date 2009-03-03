<%@ page contentType="text/html; charset=" language="java" import="java.sql.*" errorPage="" %>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>

</body>
</html>



                  <% for (int k=0;k<16;k++){%>
                    <tr>
				<% if (k==15){%><td><!--  --></td><%}%>
                <% if (k==1 || k==3 || k==4 || k==8) {%><td width="11" height="30" valign="top" class="lefttableitem"><%=lab1[k]%></td><%}%>
                <% if (k==0) {%><td width="11" height="30" valign="top" ><%=lab1[k]%></td><%}%>
				<% if (k==13 || k==14) {%><td class="lefttableheader"><%=lab1[k]%></td><%}%>
				<% if (!(k==0 || k==3 || k==13 || k==14|| k==8 || k==4 || k==15 || k==1)) {%><td width="11" height="30" valign="top" class="lefttableheader"></td><%}%>
                <% if (k==1 || k==4 || k==8){%><td width="11" height="30" valign="top" class="leftopen"><%=lab2[k]%></td><%}%>
                <% if (k==2 || k==7 || k==12){%><td width="11" height="30" valign="top" class="upopen"><%=lab2[k]%></td><%}%>
                <% if (k==5 || k==6 || k==9 || k==10 || k==11){%><td width="11" height="30" valign="top" class="bothopen"><%=lab2[k]%></td><%}%>
                <% if (k==15){%><td width="11" height="30" valign="top" class="boxed"><%=lab2[k]%></td><%}%>
                <% if ((k==0 || k==3 || k==13 || k==14)) {%> <td width="11" height="30" valign="top" class="leftopen"><!--  --></td>  <%}%>
                <% if (k==15) {%><td width="11" height="30" valign="top" class="lefttableheader"><%=noutpM[k]%></td><%}%>
                <% if (k!=15) {%><td width="11" height="30" valign="top" class="tableitem"><%=noutpM[k]%></td><%}%>
                <% if (k==15) {%><td width="11" height="30" valign="top" class="lefttableheader"><%=noutpF[k]%></td><%}%>
                <% if (k!=15) {%><td width="11" height="30" valign="top" class="tableitem"><%=noutpF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutpM[k]+noutpF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutpF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutnM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutnM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutnF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=noutnF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutnM[k]+noutnF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutnM[k]+noutnF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ootherM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ootherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ootherF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ootherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ootherM[k]+ootherF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ootherM[k]+ootherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutpF[k]+noutnF[k]+ootherF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpF[k]+noutnF[k]+ootherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+noutpF[k]+noutnF[k]+ootherF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+noutpF[k]+noutnF[k]+ootherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninpM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninpM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninpF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninpF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninpM[k]+ninpF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]+ninpF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninnM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninnM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninnF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=ninnF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninnM[k]+ninnF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninnM[k]+ninnF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=iotherM[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=iotherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=iotherF[k]%></td><%}%>
                <% if (k!=15) {%><td width="13" height="30" valign="top" class="tableitem"><%=iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=iotherM[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=iotherM[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninpM[k]+ninnM[k]+iotherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]+ninnM[k]+iotherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninpF[k]+ninnF[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpF[k]+ninnF[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=ninpM[k]+ninpF[k]+ninnM[k]+ninnF[k]+iotherM[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=ninpM[k]+ninpF[k]+ninnM[k]+ninnF[k]+iotherM[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+ninpM[k]+ninnM[k]+iotherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+ninpM[k]+ninnM[k]+iotherM[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutpF[k]+noutnF[k]+ootherF[k]+ninpF[k]+ninnF[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpF[k]+noutnF[k]+ootherF[k]+ninpF[k]+ninnF[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="lefttableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+noutpF[k]+noutnF[k]+ootherF[k]+ninpM[k]+ninpF[k]+ninnM[k]+ninnF[k]+iotherM[k]+iotherF[k]%></td><%}%>
                <% if (k==15) {%><td width="13" height="30" valign="top" class="tableheader"><%=noutpM[k]+noutnM[k]+ootherM[k]+noutpF[k]+noutnF[k]+ootherF[k]+ninpM[k]+ninpF[k]+ninnM[k]+ninnF[k]+iotherM[k]+iotherF[k]%></td><%}%>
                
					</tr>
					<%}%>
