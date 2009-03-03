<%@ page contentType="text/html ; charset=UTF-8"%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page session="true"%>

<jsp:useBean id="AudioAction" scope="session" class="org.hisp.gtool.action.AudioAction" />
<%
  // To Play Audio
	String muteOpt = (String) session.getAttribute("muteOpt");
	if(muteOpt != null && muteOpt.equals("OFF"))
	{
		AudioAction.stopAudio();
		AudioAction.playAudio("t3.wav");
	}	
  
  String indicatorList[] = request.getParameterValues("iListCB");
  String orgUnitName = request.getParameter("ouNameTB");

  String sDate = request.getParameter("sDateLB");  
  String eDate = request.getParameter("eDateLB");

//  String sDate[] = request.getParameterValues("sDateLB");  
//  String eDate[] = request.getParameterValues("eDateLB");
  String category = request.getParameter("categoryLB");
  String ideType = request.getParameter("riRadio");

  String temp = request.getParameter("ouIDTB");
  int orgUnitID = -1;
  try
  {
   orgUnitID = Integer.parseInt(request.getParameter("ouIDTB"));
   }
  catch(Exception e)
   {
   System.out.println(orgUnitName+" --Can not Convert Integer : "+ e.getMessage());
   }
             
%>  

<jsp:useBean id="ViewChartBean" scope="session" class="org.hisp.gtool.action.ViewChartBean" />

<jsp:setProperty name="ViewChartBean" property="indicatorList" value="<%=indicatorList%>"/>
<jsp:setProperty name="ViewChartBean" property="orgUnitName" value="<%=orgUnitName%>"/>
<jsp:setProperty name="ViewChartBean" property="s_Date" value="<%=sDate%>"/>
<jsp:setProperty name="ViewChartBean" property="e_Date" value="<%=eDate%>"/>
<jsp:setProperty name="ViewChartBean" property="i_category" value="<%=category%>"/>
<jsp:setProperty name="ViewChartBean" property="orgUnitID" value="<%=orgUnitID%>"/>
<jsp:setProperty name="ViewChartBean" property="ide_type" value="<%=ideType%>"/>

<html>
<head>
<title></title>
<link rel="stylesheet" type="text/css" href="css/StylesForTags.css" />
<script>
  var indexForChart = 0;
  var chartNamesToView = new Array();
	chartNamesToView[0] = new Array("WelCome.jsp");
	chartNamesToView[1] = new Array("overlaidbarchart1.jsp","linechart2.jsp","combinedcategoryplotchart1.jsp","barchart1.jsp","barchart2.jsp","barchart3d1.jsp","barchart3d2.jsp","overlaidbarchart2.jsp","dualaxischart1.jsp","piechart1.jsp","piechart3d1.jsp","areachart1.jsp");	
  	chartNamesToView[2] = new Array("overlaidbarchart1.jsp","linechart2.jsp","combinedcategoryplotchart1.jsp","barchart1.jsp","barchart2.jsp","barchart3d1.jsp","barchart3d2.jsp","overlaidbarchart2.jsp","dualaxischart1.jsp","areachart1.jsp");  							  						
  	
	chartNamesToView2[1] = new Array("combinedcategoryplotchart","barchart(Vertical)","barchart(Horizontal)","3D barchart(Vertical)","3D barchart(Horizantal)","overlaidbarchart(Vertical)","overlaidbarchart(Horizontal)","dualaxischart","piechart","piechart","linechart","areachart");	
  	chartNamesToView2[2] = new Array("combinedcategoryplotchart","barchart(Vertical)","barchart(Horizontal)","3D barchart(Vertical)","3D barchart(Horizantal)","overlaidbarchart(Vertical)","overlaidbarchart(Horizontal)","dualaxischart","linechart","areachart");  							  						
 

  function nextCharttoView()
   {      
   
     indexForChart++;
     var chartType = 0;
     var tempICount = <%=(indicatorList.length)%>;
     if(tempICount==0)  chartType=0;
     else if(tempICount==1) chartType=1;
     else chartType=2;
   
     if(indexForChart >= chartNamesToView[chartType].length) indexForChart=0;
     iframeForChart.location.href = chartNamesToView[chartType][indexForChart];
     
  //   document.form1.nextChartButton.title = chartNamesToView2[chartType][indexForChart];
  //   document.form1.prevChartButton.title = chartNamesToView2[chartType][indexForChart];
   }
   
   function prevCharttoView()
   {      
     indexForChart--;
     var chartType = 0;
     var tempICount = <%=(indicatorList.length)%>;
     if(tempICount==0)  chartType=0;
     else if(tempICount==1) chartType=1;
     else chartType=2;
   
     if(indexForChart < 0) indexForChart=chartNamesToView[chartType].length-1;

     iframeForChart.location.href = chartNamesToView[chartType][indexForChart];
     
//     document.form1.nextChartButton.title = chartNamesToView2[chartType][indexForChart];
 //    document.form1.prevChartButton.title = chartNamesToView2[chartType][indexForChart];
   }
   
</script>
</head>
<body >
<div align="right">
	<form name="form1" id="form1">
		<input type="button" name="prevChartButton" id="prevChartButton" style="width: 40; height: 35; background-image: url('images/moveLeft.gif'); background-repeat: no-repeat; background-position: center; " onClick="prevCharttoView()" title="Previous">
		<input type="button" name="nextChartButton" id="nextChartButton" style="width: 40; height: 35; background-image: url('images/moveRight.gif'); background-repeat: no-repeat; background-position: center;" onClick="nextCharttoView()" title="Next">
	</form>	
</div>
<iframe name="iframeForChart" id="iframeForChart" src="overlaidbarchart1.jsp" width="100%" height="100%" scrolling=auto frameborder="0"></iframe>

<%
if(ideType.equals("indicatorsRadio"))
{

%>

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-style: dotted" bordercolor="#111111" width="100%">
<tr>
		<td class="TableHeadingCellStyles" style="border-left-style:dotted; border-left-width:1; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><font color="maroon">Indicator Names</font></td>
		<td class="TableHeadingCellStyles" colspan="2" style="border-left-style:dotted; border-left-width:1; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><font color="maroon">Formula</font></td>
		<td class="TableHeadingCellStyles" style="border-left-style:dotted; border-left-width:1; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><font color="maroon">Numerator DataElements</font></td>
		<td class="TableHeadingCellStyles" style="border-left-style:dotted; border-left-width:1; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><font color="maroon">Denominator DataElements</font></td>
	</tr>


<!--
<table border="1" cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-style: dotted" bordercolor="#111111" width="100%">
	<tr>
		<td class="TableHeadingCellStyles"><font color="maroon">Indicator</font></td>
		<td class="TableHeadingCellStyles"><font color="maroon">Formula</font></td>
		<td class="TableHeadingCellStyles"><font color="maroon">Numerator DataElements</font></td>
		<td class="TableHeadingCellStyles"><font color="maroon">Denominator DataElements</font></td>
	</tr>
-->	
	<%
		Hashtable htForIndNumerator  	 = ViewChartBean.getIndNumeratorFormula();
		Hashtable htForIndDenominator 	 = ViewChartBean.getIndDenominatorFormula();
		Hashtable htForIndFactor 		 = ViewChartBean.getIndFactor();
		Hashtable htForIndNumeratorDEs 	 = ViewChartBean.getIndNumeratorDEs();
		Hashtable htForIndDenominatorDEs = ViewChartBean.getIndDenominatorDEs();
		
		if(htForIndNumerator == null) { out.println("<h3>No Numerator</h3>"); return;}
		if(htForIndDenominator == null) { out.println("<h3>No Denominator</h3>"); return;}
		if(htForIndFactor == null) { out.println("<h3>No Factor</h3>"); return;}
		if(htForIndNumeratorDEs == null) { out.println("<h3>No Numerator DataElements</h3>"); return;}
		if(htForIndDenominatorDEs == null) { out.println("<h3>No Denominator DataElements</h3>"); return;}
		
		Enumeration keysForIndicators = htForIndFactor.keys();
		while(keysForIndicators.hasMoreElements())
		{
			String indName = (String) keysForIndicators.nextElement();
			%>
			<!--
			<tr>
				<td class="TableHeadingCellStyles"><font color="maroon">Sample Indicator1</font></td>
				<td class="TableHeadingCellStyles" >
        			<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%" id="AutoNumber1">
			          	<tr>
            				<td class="TableHeadingCellStyles" width="20%" style="border-left-width: 1; border-right-width: 1; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1" bordercolor="#000000"><%=htForIndNumerator.get(indName)%></td>
			            	<td class="TableHeadingCellStyles" rowspan="2" width="80%">&nbsp;&nbsp;X &nbsp;<%=htForIndFactor.get(indName)%></td>
			          	</tr>
			          	<tr>
            				<td class="TableHeadingCellStyles" width="20%"><%=htForIndDenominator.get(indName)%></td>
				    	</tr>
			        </table>
        		</td>
				<td class="TableHeadingCellStyles"><%=htForIndNumeratorDEs.get(indName)%></td>
				<td class="TableHeadingCellStyles"><%=htForIndDenominatorDEs.get(indName)%></td>
			</tr>						
			-->
			
			<tr>
				<td class="TableHeadingCellStyles" style="border-left-style:dotted; border-left-width:1; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><%=indName%></td>
				<td class="TableHeadingCellStyles" align="center" style="border-left-style:dotted; border-left-width:1; border-right-width:0; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><u><%=htForIndNumerator.get(indName)%></u><br><%=htForIndDenominator.get(indName)%></td>
				<td class="TableHeadingCellStyles" align="left" style="border-left-style:dotted; border-left-width:0; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1">&nbsp;X&nbsp;<%=htForIndFactor.get(indName)%></td>
				<td class="TableHeadingCellStyles" style="border-left-style:dotted; border-left-width:1; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><%=htForIndNumeratorDEs.get(indName)%></td>
				<td class="TableHeadingCellStyles" style="border-left-style:dotted; border-left-width:1; border-right-width:1; border-top-style:dotted; border-top-width:1; border-bottom-style:dotted; border-bottom-width:1"><%=htForIndDenominatorDEs.get(indName)%></td>
			</tr>			
		<%	
		}
	%>
		
</table>
<%
} // if end
%>
</body>
</html>
