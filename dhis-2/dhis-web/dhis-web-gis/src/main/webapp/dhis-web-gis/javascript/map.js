// JavaScript Document

var layer_actived;
var j = 0;

function exportRaster()
{

	var mapFileName = document.getElementById('mapFileName').value;
	window.open ("exportImage.action?mapFileName=" + mapFileName,"Exported Image", 'width=500,height=500,scrollbars=yes');
	 
}
function exportExcel()
{

	var mapFileName = document.getElementById('mapFileName').value;	
	var indicatorId = document.getElementById("indicatorId").value;
	var startDate = document.getElementById("startDate").value;
	var endDate = document.getElementById("endDate").value;
	
	var url = "exportExcel.action?mapFileName="+mapFileName+"&indicatorId="+indicatorId+"&startDate=" + startDate + "&endDate="+endDate+"";
	window.open (url,"Exported Excel", 'width=500,height=500,scrollbars=yes');
		 
}



/***********************************************************
* ************SELECT ACTION FUNCTION *****************
************************************************************/



function init(){
	setAction('polygon', 'click',getDataFromDHIS2);	
	setAction('polygon', 'mouseover',showInfo);
	setAction('polygon', 'mouseout',hiddenInfo);	
}

function getIndicators()
{
	var indicatorGroupList = document.getElementById( "indicatorGroupId" );
	var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
	
	if ( indicatorGroupId != null )
	{
		var url = "../dhis-web-commons-ajax/getIndicators.action?id=" + indicatorGroupId;
		
		var request = new Request();
	    request.setResponseTypeXML( 'indicator' );
	    request.setCallbackSuccess( getIndicatorsReceived );
	    request.send( url );	    
	}
}

function getIndicatorsReceived( xmlObject )
{    
    var indicatorList = document.getElementById( "indicatorId" );
    var indicators = xmlObject.getElementsByTagName( "indicator" );
    
    for ( var i = 0; i < indicators.length; i++ )
    {
        var id = indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        indicatorList.add(new Option(indicatorName, id), null);       
    }
}

function getPeriods()
{
	var periodTypeList = document.getElementById( "periodTypeId" );
	var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
	
	if ( periodTypeId != null )
	{		
		var url = "../dhis-web-commons-ajax/getPeriods.action?name=" + periodTypeId;
		
		var request = new Request();
	    request.setResponseTypeXML( 'period' );
	    request.setCallbackSuccess( getPeriodsReceived );
	    request.send( url );
	}
}

function getPeriodsReceived( xmlObject )
{		
	var selectedPeriods = document.getElementById( "periodId" );
	
	clearList( selectedPeriods );
	
	var periods = xmlObject.getElementsByTagName( "period" );
	
	for ( var i = 0; i < periods.length; i++ )
	{
		var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var periodName = periods[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		if ( listContains( selectedPeriods, id ) == false )
		{						
			var option = document.createElement( "option" );
			option.value = id;
			option.text = periodName;
			selectedPeriods.add( option, null );
		}			
	}
}


/***********************************************************
* ************COMMON FUNCTION ************************
************************************************************/

var mousex=0;
var mousey=0;
var ie=document.all;
var ns6=document.getElementById && !document.all;
document.onmousemove =  function updateMousePosition(){
    var e = arguments[0] || event;
    mousex=(ns6)?e.pageX : event.clientX+ietruebody().scrollLeft;
    mousey=(ns6)?e.pageY : event.clientY+ietruebody().scrollTop;
}

function setPosition(id,x,y){
    var a = document.getElementById(id);
    a.style.left = x + "px";
    a.style.top = y + "px";
}

/***********************************************************
* ************ MAPP FUNCTION ****************************  
************************************************************/


function zoomIn(){
	 var svg = document.embeds['map'].getSVGDocument().getElementsByTagName('svg');
	 svg = svg.item(0);	
	 var currenZoom = new Number(svg.getAttribute('width').replace("%",""));	
	 currenZoom+=10;
	 svg.setAttributeNS(null, "width", currenZoom+"%");
	 svg.setAttributeNS(null, "height", currenZoom+"%");
}
function zoomOut(){
	 var svg = document.embeds['map'].getSVGDocument().getElementsByTagName('svg');
	 svg = svg.item(0);	
	 var currenZoom = new Number(svg.getAttribute('width').replace("%",""));	
	 currenZoom-=10;
	 svg.setAttributeNS(null, "width", currenZoom+"%");
	 svg.setAttributeNS(null, "height", currenZoom+"%");
}

function retoreZoom(){
	 var svg = document.embeds['map'].getSVGDocument().getElementsByTagName('svg');	
	 svg = svg.item(0);	
	 svg.setAttributeNS(null, "width", "100%");
	 svg.setAttributeNS(null, "height", "100%");
}

function setAction(layerId, event, action){	
	var nodeList = domSVG( layerId );
	for(var i=0;i<nodeList.length;i++){
		g_element = nodeList.item(i);
		g_element.addEventListener(event , action, false);			
	}	
}
function domSVG( layerId ){
	var element = document.embeds['map'].getSVGDocument().getElementById(layerId);
	try{
		return element.getElementsByTagName('polygon');	
	}catch(e){
		return false;
	}
	
	
}

function showInfo(evt){
	var target = evt.target;
	var name = target.getAttribute("attrib:district");	
	var orgCode = target.getAttribute("id");	
	fill = target.getAttribute("fill");
	stroke = target.getAttribute("stroke");
	stroke_width = target.getAttribute("stroke-width");
	if(layer_actived=="polyline"){
		  target.setAttributeNS(null, "stroke", "red");
		  target.setAttributeNS(null, "stroke-width", "200");
	}else{
		target.setAttributeNS(null, "fill", "#67F906");		
	}	
}

var fill, stroke, stroke_width;
function hiddenInfo(evt){
	var target = evt.target;
	if(layer_actived=="polyline"){
		  target.setAttributeNS(null, "stroke", stroke);
		  target.setAttributeNS(null, "stroke-width",stroke_width);
	}else{
		target.setAttributeNS(null, "fill", fill);
	}
	
}

function refreshColor(color){
	
	var nodeList = domSVG('polygon');
	for(var i=0;i<nodeList.length;i++){
		polygon = nodeList.item(i);		
		polygon.setAttributeNS(null, "fill", color);
	}
	
}

function fillColor(orgCode,color){
	var nodeList = domSVG('polygon');
	for(var i=0;i<nodeList.length;i++){
		polygon = nodeList.item(i);		
		//polygon.addEventListener("click", showIndicatorValue, false);
		var id = polygon.getAttribute("id");
		if(id==orgCode){
			polygon.setAttributeNS(null, "fill", color);	
			
		}
		
	}
	
}

/***********************************************************
* ************ DATA FUNCTION ****************************  
************************************************************/

function getDataFromDHIS2( evt ){
	var target = evt.target;
	var orgCode = target.getAttribute("id");
	setFieldValue("informationDetails","<img src=\"../images/ajax-loader.gif\"/>");	
	showHideDiv('information');
	
}
// Get data from user and render map

function renderMap(){
	var indicatorId = byId('indicatorId').value;	
	var url = "fillMapByIndicator.action?indicatorId=" + indicatorId ;
	if(indicatorValueFrom == 'aggregation_service'){
		var startdate = byId('startDate').value;
		var enddate = byId('endDate').value;
		if(startdate==''){
		alert(i18n_startdate_null);
		}else if(enddate==''){
			alert(i18n_enddate_null);
		}else{		
			
			url+= "&startDate=" + startdate + "&endDate=" + enddate + "&mode=" + indicatorValueFrom;
			var request = new Request();
			request.setResponseTypeXML( 'features' );
			request.setCallbackSuccess( renderMapRecieved);
			request.send( url);
			
			showDivEffect();
		}
	}else{
	
		var periodId = byId('periodId').value;
		url+= "&periodId=" + periodId;
		var request = new Request();
		request.setResponseTypeXML( 'features' );
		request.setCallbackSuccess( renderMapRecieved);
		request.send( url);
		
		showDivEffect();
		
	}
									
	
	
		
	
}

function renderMapRecieved(features){	
	//  Render map -------------------------------------------------------------------------------
	var featureList = features.getElementsByTagName("feature");		
	for(var i=0;i<featureList.length;i++){
		var feature = featureList.item(i);
		var orgCode = feature.getElementsByTagName('orgCode')[0].firstChild.nodeValue;
		var color =  feature.getElementsByTagName('color')[0].firstChild.nodeValue;	
		var value = feature.getElementsByTagName('value')[0].firstChild.nodeValue;		
		fillColor(orgCode,color);
		showLabel(orgCode, value);
	}
	//  Create Legend -------------------------------------------------------------------------------
	var legendList = features.getElementsByTagName("legend");
	var innerHTML ="<legend>Legend</legend>";
		innerHTML+="<table width='100%' border='0'>";	
		innerHTML+="<tr align='center'>";	
		innerHTML+="<td>Color</td>"
		innerHTML+="<td></td>"
		innerHTML+="<td>Min</td>"
		innerHTML+="<td></td>"
		innerHTML+="<td>Max</td>"
		innerHTML+="</tr>";	
		for(var i=0;i<legendList.length;i++){	
			var legend = legendList.item(i);
			innerHTML+="<tr align='center'>";
			var color = legend.getElementsByTagName('color')[0].firstChild.nodeValue;
			var minValue = legend.getElementsByTagName('min')[0].firstChild.nodeValue;
			var maxValue = legend.getElementsByTagName('max')[0].firstChild.nodeValue;
			innerHTML+="<td width='50%' bgcolor='#"+color+"'>&nbsp;&nbsp;</td>";
			innerHTML+="<td width='10%'>&nbsp;</td>";
			innerHTML+="<td width='19%'>" + minValue + "</td>";
			innerHTML+="<td width='2%'>-</td>";
			innerHTML+="<td width='19%'>" + maxValue + "</td>";
			innerHTML+="</tr>";
		}			
	innerHTML+="</table>";
	setFieldValue('legendDetails',innerHTML);
	byId('legend').style.display='block';
	enable('image');
	enable('excel');
	deleteDivEffect();
	
	
}


function showLabel(orgCode,value){
	var element =document.embeds['map'].getSVGDocument().getElementById('label');	
	
		var nodeList = element.getElementsByTagName('text');
		for(var i=0;i<nodeList.length;i++){
			text = nodeList.item(i);
			var id = text.getAttribute("id");
			if(id==orgCode){
				var lable = text.firstChild.nodeValue.split(":");	
				var newText = "";
				if(lable.length==1){
					newText = text.firstChild.nodeValue + ":" + value;
				}else{
					newText = lable[0] + ":" + value;
				}
				
				text.firstChild.nodeValue = newText;
			}
		}
	
	
}





function addMapFile(){
	
	var mapFileName = document.getElementById("mapFileName").value;
	var request = new Request();
	request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addMapFileSuccess);
    request.send( "addMapFile.action?mapFileName=" + mapFileName );	
	
}

function addMapFileSuccess(message){
 	var message = message.firstChild.nodeValue;	
	document.getElementById( 'message' ).innerHTML = message;
	
}

