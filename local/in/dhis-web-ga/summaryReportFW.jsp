<%@page import="java.sql.*"%>

<jsp:useBean id="DBConnection" scope="session" class="org.hisp.gtool.action.DBConnection" />

<%!
     public String[] getAllFacilities(String orgunit,Connection con)
    {
       
        Statement st1 = null;
		ResultSet rs1 = null;
		//String query = "Select shortname from organisationunit where parent = (select id from Organisationunit where shortname like'"+orgunit+"') order by shortname";
		String query = "SELECT shortname FROM organisationunit WHERE parentid = (SELECT organisationunitid FROM organisationunit WHERE shortname LIKE'"+orgunit+"') ORDER BY shortname";
		String[] str = new String[30];
		int cnt = 0;
		try{
			st1= con.createStatement();
			rs1 = st1.executeQuery(query);
			while (rs1.next())
			{
				str[cnt] = rs1.getString("shortname");
				cnt++;
			}
			rs1.close();
			st1.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String actualarray[] = new String[cnt];
        for(int j=0;j<cnt;j++)
        {
            actualarray[j] = str[j];
        }    
        return(actualarray);
  }
%>     

<% 

	String urlForSR = request.getParameter("Action");
	String partsOfUrl[] = urlForSR.split("@@");
	
   String orgunit = partsOfUrl[0];
   String fromdate = partsOfUrl[1];
   String todate = partsOfUrl[2];
 //  String orgunit = "Anklach PHC";
  // String fromdate = "2006-01-01";
  // String todate = "2006-07-31";

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int cnt,count2,count3,i,j;
   // float targetvalue = 100.0f;
    try{
        //Class.forName("com.mysql.jdbc.Driver");
        //con = DriverManager.getConnection("jdbc:mysql://localhost/gj_dhis2","dhis","");
        con = DBConnection.openConnection();
    }
    catch(Exception e){
        e.printStackTrace();
    }
    String[] temparray = new String[100];
    Date[] temparray1 = new Date[100];
    String[] monarray = new String[100];
    
    double[] targetvalue = 	new double[200];
    
    String query1 = "SELECT name,Target FROM indicator ORDER BY name"; 
    String query2 = "SELECT * FROM period WHERE enddate BETWEEN '" + fromdate +"' AND '" +todate+"' ORDER BY enddate";
    String query3 = null;
	int monthcount=0;
    String[] facilitynames = getAllFacilities(orgunit,con);       
    cnt = 0;
    try{
        stmt = con.createStatement();
        rs = stmt.executeQuery(query1);
        cnt = 0;
        while (rs.next())
        {
            temparray[cnt] = rs.getString(1);
            targetvalue[cnt] = rs.getDouble(2);
            
            cnt++;
        }
        rs.close();
        stmt.close();
    }
    catch(Exception e){
        e.printStackTrace();
    }
    String[] indicatornames = new String[cnt];

    
    i = 0;
    while (i < cnt)
    {
        indicatornames[i] = temparray[i];
        i++;
    }
	try{
		stmt = con.createStatement();
		rs = stmt.executeQuery("SELECT count(*) FROM period WHERE enddate BETWEEN '" + fromdate +"' AND '" +todate+"' ORDER BY enddate");
		if (rs.next())
			monthcount = rs.getInt(1);
		rs.close();
		stmt.close();
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
  /*  try{
        stmt = con.createStatement();
        rs = stmt.executeQuery(query2);
        cnt = 0;
        while (rs.next()){
           // temparray[cnt] = rs.getDate("startDate")+" - "+rs.getDate("endDate");
            temparray1[cnt] = rs.getDate("startDate");
            cnt++;
        }
        rs.close();
        stmt.close();
    }
    catch(Exception e){
        e.printStackTrace();
    }
   // String[] compdatearr = new String[cnt];
    Date[] compdatearr = new Date[cnt];
    i = 0;
    while (i < cnt){
        compdatearr[i] = temparray1[i];
        i++;
    }*/
    float[][] actualvalues = new float[indicatornames.length][facilitynames.length];
	
    try{
       
        i = 0; j= 0;
        while (i < indicatornames.length){
            j=0;
            while (j < facilitynames.length){
                stmt = con.createStatement();
               // query3 = "SELECT * FROM aggregatedindicatorvalue WHERE source LIKE (SELECT name FROM organisationunit WHERE shortname LIKE '"+orgunit+"') AND indicatorName LIKE '"+indicatornames[i]+"' AND periodName LIKE '"+compdatearr[j]+"'";
               //  query3 = "SELECT sum(value) FROM aggregatedindicatorvalue WHERE organisationUnitId in (SELECT id FROM organisationunit WHERE shortname LIKE '"+facilitynames[j]+"') AND (indicatorId in (SELECT id from indicator where name LIKE '"+indicatornames[i]+"')) AND (periodId in (Select id from period where endDate between  '"+fromdate+"' and '"+todate+"')) order by organisationUnitId";
               query3 = "SELECT sum(value) FROM aggregatedindicatorvalue WHERE organisationunitid IN (SELECT organisationunitid FROM organisationunit WHERE shortname LIKE '"+facilitynames[j]+"') AND (indicatorid IN (SELECT indicatorid FROM indicator WHERE name LIKE '"+indicatornames[i]+"')) AND (periodid IN (SELECT periodid FROM period WHERE endDate BETWEEN  '"+fromdate+"' AND '"+todate+"')) ORDER BY organisationunitid";
                rs = stmt.executeQuery(query3);
                if (rs.next()){
                    actualvalues[i][j] = (float)rs.getFloat(1)/monthcount;                    
                }
                else{
                    actualvalues[i][j] = 0.0f;
                }
                rs.close();
                stmt.close();
                j++;
            }
            //targetvalue[i] = 60.0f;
            i++;
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    String str = "List of Indicator Values for "+orgunit+" From "+fromdate+" to "+todate;
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Summary Report</title>
        <link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
    </head>
    <body>
        <font face="arial"><h5><%=str%></h5></font>
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-style: dotted" bordercolor="#111111" width="100%" bgcolor="#FFFFFF">
            <tr>
                <td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"></td>
                <% int count = 0;try{count = 0;
                    while (count < (facilitynames.length)-1){
                    %><td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><%=facilitynames[count]%></td>
                    <%count++;}%>
            </tr>
            <%count=0;
            while (count < indicatornames.length){ i=0;%>
            <tr>
                <td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><%=indicatornames[count]%></td>
                <% while (i < (facilitynames.length)-1){if(actualvalues[count][i]>targetvalue[count]){%>
                         <td class="TableDataCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><font color="RED"><%=actualvalues[count][i]%></font></td>
                <% }else{%><td class="TableDataCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><%=actualvalues[count][i]%></td><%}i++; }%>
            </tr>
            <% count++; }}catch(Exception e){} %>       
          
        </table>
            
    </body>
</html>