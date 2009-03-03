<%@page import="java.sql.*"%>

<jsp:useBean id="DBConnection" scope="session" class="org.hisp.gtool.action.DBConnection" />

<%!
     public String[] getAllPeriods(String fromdate,String todate)
    {
       
        String[] monthnames = {" ","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String[] fromvalues = fromdate.split("-");
        String[] tovalues = todate.split("-");
        String[] periodarray = new String[100];
        
        int fromyear = Integer.parseInt(fromvalues[0]);
        int frommonth = Integer.parseInt(fromvalues[1]);
        
        int toyear = Integer.parseInt(tovalues[0]);
        int tomonth = Integer.parseInt(tovalues[1]);
        int count = 0;
        int j = frommonth;
        for(int i=fromyear; i <= toyear; i++)
        {
            for(;j<=12;)
            {
                periodarray[count] = monthnames[j]+" "+i;
                if (j == 12)
                {
                    j = 1;
                    count++;
                    break;                    
                }
                if ((j > tomonth) && (i == toyear))
                {
                    count++;
                    break;
                }
                j++;
                count++;
            }
        }
        String[] actualarray = new String[count];
        for(j=0;j<count;j++)
        {
            actualarray[j] = periodarray[j];
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
//    out.println("<h1>"+urlForSR+"</h1>");
//   String orgunit = "Anklach SC";
//   String fromdate = "2006-01-01";
//   String todate = "2006-07-31";

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    int cnt,count2,count3,i,j;
 //   float targetvalue = 100.0f;
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
    String[] monthnames = getAllPeriods(fromdate,todate);       
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
    }
    float[][] actualvalues = new float[indicatornames.length][compdatearr.length];
	
    try{
       
        i = 0; j= 0;
        while (i < indicatornames.length){
            j=0;
            while (j < compdatearr.length){
                stmt = con.createStatement();
               // query3 = "SELECT * FROM aggregatedindicatorvalue WHERE source LIKE (SELECT name FROM organisationunit WHERE shortname LIKE '"+orgunit+"') AND indicatorName LIKE '"+indicatornames[i]+"' AND periodName LIKE '"+compdatearr[j]+"'";
               //  query3 = "SELECT * FROM aggregatedindicatorvalue WHERE organisationUnitId in (SELECT id FROM organisationunit WHERE shortname LIKE '"+orgunit+"') AND (indicatorId in (SELECT id from indicator where name LIKE '"+indicatornames[i]+"')) AND (periodId in (Select id from period where startDate like '"+compdatearr[j]+"'))";
                query3 = "SELECT SUM(value) FROM aggregatedindicatorvalue WHERE organisationunitid in (SELECT organisationunitid FROM organisationunit WHERE shortname LIKE '"+orgunit+"') AND (indicatorid IN (SELECT indicatorid FROM indicator WHERE name LIKE '"+indicatornames[i]+"')) AND (periodid IN (SELECT periodid FROM period WHERE startdate BETWEEN '"+compdatearr[j]+"' AND '"+compdatearr[j]+"'))";
                rs = stmt.executeQuery(query3);
                if (rs.next()){
                    actualvalues[i][j] = (float)rs.getFloat(1);
					System.out.println(actualvalues[i][j]);                    
                }
                else{
                    actualvalues[i][j] = 0.0f;
                }
                rs.close();
                stmt.close();
                j++;
            }
			//targetvalue[i] = 100.0f;
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
                    while (count < (monthnames.length)-1){
                    %><td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><%=monthnames[count]%></td>
                    <%count++;}%>
            </tr>
            <%count=0;
            while (count < indicatornames.length){ i=0;%>
            <tr>
                <td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><%=indicatornames[count]%></td>
                <% while (i < (monthnames.length)-1){if(actualvalues[count][i]>=targetvalue[count]){%>
                         <td class="TableDataCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><font color="RED"><%=actualvalues[count][i]%></font></td>
                <% }else{%><td class="TableDataCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><%=actualvalues[count][i]%></td><%}i++; }%>
            </tr>
            <% count++; }}catch(Exception e){} %>       
          
        </table>
            
    </body>
</html>