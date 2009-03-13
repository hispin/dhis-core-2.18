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

function showHideLabel(){
	try{
		var element = document.embeds['map'].getSVGDocument().getElementById('label');
		if(element.getAttribute('visibility')=='hidden'){
			element.setAttributeNS(null,'visibility','visible');		
		}else{
			element.setAttributeNS(null,'visibility','hidden');
		}
	}catch( e ){
		alert( i18n_dont_have_label );
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
		var id = polygon.getAttribute("id");
		if(id==orgCode){
			polygon.setAttributeNS(null, "fill", color);	
			
		}
		
	}	
}

function getSVGFileByOrganisation(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getSVGFileByOrganisationReceived );
	request.send( "getSVGFileByOrganisation.action" );
}
function getSVGFileByOrganisationReceived( xmlObject ){
	var svg = xmlObject.firstChild.nodeValue;	
	document.getElementById('map').src = "map/" + svg;
	return false;
}

function previewMap( svg ){
	document.getElementById('map').src = "map/" + svg;
	currentSVG = svg;	
}