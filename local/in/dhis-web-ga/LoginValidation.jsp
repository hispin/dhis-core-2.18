<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<!-- Bean Specification -->
<jsp:useBean id="LoginAction" scope="session" class="org.hisp.gtool.action.LoginAction" />


<% 
  String userName = request.getParameter("unameTF");
  String userPwd = request.getParameter("pwdTF");
  String dbState = request.getParameter("stateCB");
  String nextPage = request.getParameter("nextPageTF");	
  int loginStatus = 0;
  
  int flag = 1;
  if(userName==null && userPwd ==null)  flag=2;
  
  try
  {	
  	loginStatus = Integer.parseInt((String) session.getAttribute("loginStatus"));
  }
  catch(Exception e)
  {
    loginStatus = 0;
    userName = " ";
    userPwd = " ";
  }	
    
  boolean loginValid = true;

  if(loginStatus==0)
  {
	loginValid = LoginAction.loginValidation(userName,userPwd);
    if(loginValid) 
       {        
        session.setAttribute("loginStatus","1");
        session.setAttribute("loginUName",userName); 
        loginStatus = 1;       
       } 
  }
%>
<html>
	<head>
		<title></title>
		<script>
 			var loginStatus = <%=loginStatus%>;
 			var nextPage = '<%=nextPage%>';
 			var flag = <%=flag%>;

 			function fun1()
 			{ 						
	  			if(loginStatus==0)	{parent.chartPane.location.href="LoginScreen.jsp";	}
	 			else 	
	 			{
	 				if(nextPage=="orgUnitTree.jsp?optToDisplay=shortname" || nextPage=="orgUnitTree.jsp?optToDisplay=name")
	 					parent.ouTreeFrame.location.href=nextPage;	 
	 				else
	 					{
	 						if(flag==1) parent.ouTreeFrame.location.href="orgUnitTree.jsp?optToDisplay=shortname";	 
	 						parent.chartPane.location.href=nextPage;
	 					}
	 			}
	 		}	  	   
		</script>
	</head>
	<body onload="fun1()">
	</body>
</html>	         