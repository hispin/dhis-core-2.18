<%@ page contentType="text/html ; charset=UTF-8"%>


<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<jsp:useBean id="DBConnection" scope="session" class="org.hisp.gtool.action.DBConnection" />
<jsp:useBean id="LoginAction" scope="session" class="org.hisp.gtool.action.LoginAction" />

<% 
  
  String optToDisplay = request.getParameter("optToDisplay");
  
  Connection con = null;
  
  Statement st1 = null;
  Statement st2 = null;
  Statement st3 = null;
  Statement st4 = null;
  Statement st5 = null;
  Statement st6 = null;
  Statement st7 = null;
  Statement st8 = null;
    
  ResultSet rs1 = null;
  ResultSet rs2 = null;
  ResultSet rs3 = null;
  ResultSet rs4 = null;
  ResultSet rs5 = null;      
  ResultSet rs6 = null;      
  ResultSet rs7 = null;    
  ResultSet rs8 = null;    

  
	  
  String dsnames[];
  String dsshortnames[];
  int i=0;
  int noOfOrgUnits = 10000;

  int orgUnitLevelNo = 1;

  
%>
<%

   try
    {               
       
       con = DBConnection.openConnection();
            
       st1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
       st2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
       st3 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
       st4 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
       st5 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
       st6 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
       st7 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
       st8 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		     
	
       rs8 = st8.executeQuery("select  max(organisationunitid) from  organisationunit"); 
       if(rs8.next())
        {
          noOfOrgUnits = rs8.getInt(1) + 10;  
        }
       else
        {
          noOfOrgUnits = 10000;            
        }
    }

   catch(Exception e)  {out.println("some exception "+e.getMessage());}
   finally
      {
        try
         {
              if(rs8!=null) rs8.close();
              if(st8!=null) st8.close();                          
          }
           catch(Exception e) {out.println(e.getMessage());}
      } 


   	dsnames = new String[noOfOrgUnits];          
	dsshortnames = new String[noOfOrgUnits];
   try
    { 
      int optValue = 3;		
	  if(optToDisplay.equals("name")) optValue = 2;
	  else optValue = 3;
      rs5 = st5.executeQuery("select  organisationunitid,name,shortname from  organisationunit");     
      if(rs5.next())
	 	{
          do
	    	{
	     		dsnames[rs5.getInt(1)]=rs5.getString(optValue);
	     		dsshortnames[rs5.getInt(1)]=rs5.getString(3);
          	}
         while(rs5.next());
       }
     }
    catch(Exception e)  {out.println("some exception "+e.getMessage());}
    finally
         {
           try
            {             
             if(rs5!=null) rs5.close();
             if(st5!=null) st5.close();  
            }
           catch(Exception e) {out.println(e.getMessage());}
         } 
%>




<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <title>OrganisationUnit Tree</title>
    <link rel="stylesheet" type="text/css" href="css/DynamicTree.css" />
    <script type="text/javascript" src="javascript/ie5.js"></script>
    <script type="text/javascript" src="javascript/DynamicTree.js"></script>
    
    <style type="text/css">
    p { font-family: georgia, sans-serif; font-size: 11px; }
    </style>


<script>

   function fun1(OrgUnitName,OrgUnitID)
    {
		parent.chartPane.document.ChartGenerationForm.ouNameTB.value = OrgUnitName;
		parent.chartPane.document.ChartGenerationForm.ouIDTB.value = OrgUnitID;
    }

</script>



</head>
<body >

    <div class="DynamicTree">
        
        <div class="wrap" id="tree">
        
 <% 
 	
 	String loginUName = (String) session.getAttribute("loginUName");
 	if(loginUName==null) {out.println("Not Logged");return;}  
    List loginDetails = (ArrayList) LoginAction.getLoginDetails(loginUName);
	int curUserID = Integer.parseInt((String)loginDetails.get(0));    
    
    int selOUID = 1;
    String query = "";
    try
    {
    	selOUID = Integer.parseInt((String)loginDetails.get(1));
    	if(selOUID<=0) query = "SELECT organisationunitid,shortname FROM organisationunit WHERE parentid IS NULL";
    	else
    		query = "SELECT organisationunitid,shortname FROM organisationunit WHERE organisationunitid ="+selOUID;
    }  
    catch(Exception e)
    {
		query = "SELECT organisationunitid,shortname FROM organisationunit WHERE parentid IS NULL";    	
    }
    System.out.println("selouid : "+selOUID+"CurUSerID : "+curUserID);
	//int selOUID = 3;

    try
     {
       rs1 = st1.executeQuery(query);
       if(!rs1.next())  {out.println("No records");}
       else
	{
	 do
	 {
           int parent1 = rs1.getInt(1); %>  
           <div class="folder"><a href="javascript:fun1('<%=dsshortnames[parent1]%>',<%=parent1%>)"> <%=dsnames[parent1]%> </a>
           <%rs2 = st2.executeQuery("select organisationunitid from organisationunit where parentid = "+parent1+" order by shortname"); 
	   if(rs2.next())
	    {
	      do
	       {
                 int parent2 = rs2.getInt(1);  
		 if(parent2==parent1) continue;%>
                 <div class="folder"><a href="javascript:fun1('<%=dsshortnames[parent2]%>',<%=parent2%>)" ><%=dsnames[parent2]%></a>
                 <%rs3 = st3.executeQuery("select organisationunitid from organisationunit where parentid = "+parent2+" order by shortname"); 
                 if(rs3.next())
	          {
        	   do
		    {
		      int parent3 = rs3.getInt(1); %>
                      <div class="folder"><a href="javascript:fun1('<%=dsshortnames[parent3]%>',<%=parent3%>)"><%=dsnames[parent3]%></a> 
                      <%rs4 = st4.executeQuery("select organisationunitid from organisationunit where parentid = "+parent3+" order by shortname");  
		      if(rs4.next())
		       {
		        do
		         {
                          int parent4 = rs4.getInt(1); %>               
                          <div class="folder"><a href="javascript:fun1('<%=dsshortnames[parent4]%>',<%=parent4%>) "><%=dsnames[parent4]%></a>
			  <%rs6 = st6.executeQuery("select organisationunitid from organisationunit where parentid = "+parent4+" order by shortname");  
		          if(rs6.next())
		           {
		             do
		              {
                                int parent5 = rs6.getInt(1); %>               
                                <div class="folder"><a href="javascript:fun1('<%=dsshortnames[parent5]%>',<%=parent5%>)"><%=dsnames[parent5]%></a>
			        <%rs7 = st7.executeQuery("select organisationunitid from organisationunit where parentid = "+parent5+" order by shortname");  
		                if(rs7.next())
		                  {
		                    do
		                     {
                                       int parent6 = rs7.getInt(1); %>               
                                       <div class="folder"><a href="javascript:fun1('<%=dsshortnames[parent6]%>',<%=parent6%>)"><%=dsnames[parent6]%></a></div>
				       <%
                                      }
                                     while(rs7.next());
                          	   } %>
				</div>
                                <%   
		               }
                             while(rs6.next());
	                    } %>
			  </div>
                          <%   
		         }
                        while(rs4.next());
	               } %>
                       </div>
                       <%
                    }
                   while(rs3.next());
		  } %>
                  </div>
                  <%
                }
               while(rs2.next()); 
             } %>
             </div>
             <% 
            }
	   while(rs1.next());
	  } 
        }
       catch(Exception e) {out.println(e.getMessage());}
       finally
         {
           try
            {
             	if(rs1!=null) rs1.close(); 
   	     		if(rs2!=null) rs2.close();
	     		if(rs3!=null) rs3.close();
             	if(rs4!=null) rs4.close();
	     		if(rs6!=null) rs6.close();
	     		if(rs7!=null) rs7.close();

         		if(st1!=null) st1.close();
	     		if(st2!=null) st2.close();
	     		if(st3!=null) st3.close();
	     		if(st4!=null) st4.close();
	     		if(st6!=null) st6.close();
	     		if(st7!=null) st7.close();

	     		
            }
           catch(Exception e) {out.println(e.getMessage());}
         } 
%>
        
    </div>
   
    </div>

    <script type="text/javascript">

    var tree = new DynamicTree("tree");
    tree.init();
   
    </script>

</body>
</html>