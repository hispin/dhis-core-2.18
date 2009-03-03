<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<jsp:useBean id="ViewChartBean" scope="session" class="org.hisp.gtool.action.ViewChartBean" />

<html>
<head>
		<title>Graphical Analyser</title>
		<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
	</head>
	<body>
	
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-style: dotted" bordercolor="#111111" width="100%">


     <%
     String categoryType = ViewChartBean.getI_category();
     String ideType = ViewChartBean.getIde_type();
     
     String monthNames[]={"","Jan-","Feb-","Mar-","Apr-","May-","Jun-","Jul-","Aug-","Sep-","Oct-","Nov-","Dec-"};
     String tempForSDate = "";

	 String chartTitle = "Indicator : ";	
	 String xAxisTitle = "";
	 String yAxisTitle = "";
	 if(categoryType.equals("Period"))  xAxisTitle = "Time Line";
	 else xAxisTitle = "Facilities";
	 
	 if(ideType.equals("indicatorsRadio"))  yAxisTitle="Percentage/Rate";
	 else yAxisTitle="Value";

    // Indicator IDs

	
	
	Hashtable htForIndicator = ViewChartBean.setIndicatorIDs();
	Enumeration keysForIndicator = htForIndicator.keys();
	
	int count = 0;
	String[] series1 = new String[htForIndicator.size()];
	String[] series2 = new String[htForIndicator.size()];
	
	while(keysForIndicator.hasMoreElements())
	{
		String keyI = (String) keysForIndicator.nextElement();
		int iID = ((Integer)htForIndicator.get(keyI)).intValue();
		series1[count] = keyI;	
		series2[count++]=keyI;	
		
		chartTitle+=keyI+",";			
	}// while loop end
	
		chartTitle += "\n Facility : "+ViewChartBean.getOrgUnitName()+"\nPeriod : "+ViewChartBean.getS_Date()+" To "+ViewChartBean.getE_Date();
	
	//Perid IDs
	count = 0;
	Hashtable htForPeriods = ViewChartBean.setPeriodIDs();
	//Enumeration keysForPeriod = htForPeriods.keys();
	
	Vector vForPeriods = new Vector(htForPeriods.keySet());
	Collections.sort(vForPeriods);
	Iterator iteratorForPeriod = vForPeriods.iterator();

    String[] categories1 = new String[htForPeriods.size()];
    String[] categories2 = new String[htForPeriods.size()];
	%>
	<tr><td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111">Indicator Name</td>
		<td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><font color="Maroon">Target</font></td>
	<%
	while(iteratorForPeriod.hasNext())
	{				
		String keyP = (String) iteratorForPeriod.next();
		int pID = ((Integer)htForPeriods.get(keyP)).intValue();
		if(categoryType.equals("Period"))
				{
					String partsOfTempForSDate[] = keyP.split("-");
					tempForSDate = monthNames[Integer.parseInt(partsOfTempForSDate[1])]+partsOfTempForSDate[0];
				}	
		else tempForSDate =	keyP;
		
		categories1[count]=tempForSDate;
		categories2[count++]=tempForSDate;
		%>
		<td class="TableHeadingCellStyles"><%=tempForSDate%></td>
		<%
	}// while loop end
	%>
	</tr>
	<%
	//Indicator Values & Target
	
	Hashtable htForIndicatorValues = ViewChartBean.getValuesByPeriod();
	if(htForIndicatorValues==null) {out.println("<h3>Select any Indicator</h3>");return;}
	Enumeration keysForIndicatorValues = htForIndicatorValues.keys();
	
	Hashtable htForTargets = ViewChartBean.getTargetValues();
	
	int count1=0;
	int count2=0;
				
	double data1[][]= new double[htForIndicatorValues.size()][];

		
	while(keysForIndicatorValues.hasMoreElements())
	{
		String keyIV = (String) keysForIndicatorValues.nextElement();						
		List liForValues = (ArrayList) htForIndicatorValues.get(keyIV);
		data1[count1] = new double[liForValues.size()];
		Iterator itForValues = liForValues.iterator();
		count2 = 0;
		%>
		<tr><td class="TableHeadingCellStyles" style="border-style: dotted; border-width: 1" bordercolor="#111111"><%=keyIV%></td>
			<td class="TableDataCellStyles"><font color="Maroon"><%=((Double)htForTargets.get(keyIV)).doubleValue()%></font></td>
		<%
		while(itForValues.hasNext())
		{		
			double iValue = ((Double) itForValues.next()).doubleValue();
			data1[count1][count2] = Math.round(iValue*Math.pow(10,2))/Math.pow(10,2);
					
			%>
			<td class="TableDataCellStyles"><%=Math.round(iValue*Math.pow(10,2))/Math.pow(10,2)%></td>
			<%
			count2++;
		}// list while loop end
		%>
		</tr>
		<%
		count1++;
	}// indihashtable		
	
/*	Hashtable htForTargets = new Hashtable();
	htForTargets.put("Percentage of first trimester ANC registration",new Double(60.0));
	htForTargets.put("Percentage of Institutional Delivery",new Double(90.0));
	htForTargets.put("Percentage of Delivery by SBA",new Double(95.0));
	htForTargets.put("Percentage of Still Births",new Double(100.0));
	htForTargets.put("Percentage of Female Live Birth",new Double(50.0));
	htForTargets.put("Early Breast Feeding Rate",new Double(90.0));
	htForTargets.put("Percentage of Fully Immunised Children",new Double(90.0));
	htForTargets.put("Percentage of drop outs in BCG to Measles",new Double(10.0));
	htForTargets.put("Percentage of LBW Children at Birth",new Double(10.0));
	htForTargets.put("Infant Mortality Rate",new Double(50.0));
	htForTargets.put("Percentage of Neonatal Deaths",new Double(25.0));
	htForTargets.put("Total Sterilization Rate",new Double(65.0));
	
	double data2[][]= new double[count1][count2];
	count1=0;
	
	while(count1<data2.length)
	  {	
	    count2=0;    
	    
	    while(count2<data2[count1].length)
	     {
	      data2[count1][count2++]= ((Double)htForTargets.get(series1[count1])).doubleValue();
	     }
	     count1++;
	    }  
*/	     
%>
	
</table>
<br>
<jsp:useBean id="myChart" scope="request" class='org.hisp.gtool.charts.PieChart3D1' />

<jsp:setProperty name="myChart" property="section" value="<%=categories1%>"/>
<jsp:setProperty name="myChart" property="data" value="<%=data1[0]%>"/>
<jsp:setProperty name="myChart" property="chartTitle" value="<%=chartTitle%>"/>

<%
	myChart.setXAxis_Title(xAxisTitle);
	myChart.setYAxis_Title(yAxisTitle);
%>

<%String chartViewer = myChart.getChartViewer(request, response);%>
<center>
<img src="<%=chartViewer%>" border=0 usemap="#imageMap">
</center>