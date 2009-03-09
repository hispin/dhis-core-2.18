// JavaScript Document

/***********************************************************
* ************SELECT ACTION FUNCTION *****************
************************************************************/

/*  Left select organisation unit list */

function selectOrgunit(id,name){
	document.getElementById('orgname').value = name;
	document.getElementById('orgUnit').value = id;
	
	var listButton = document.getElementsByName('orgunitlink');
	for(var i=0;i<listButton.length;i++){		
		var button = listButton.item(i);
		button.style.color="#0000CC";
	}
	
	document.getElementById(id).style.color="red";	
	
	var request = new Request();
	request.setResponseTypeXML( 'features' );
	request.setCallbackSuccess( selectOrgunitSuccess);
	request.send( "selectOrgUnitAjax.action?id=" + id );
}

function selectOrgunitSuccess(feature){
	var type = 	feature.getAttribute( 'type' );	
	
	refreshColor('#FFFFCC');
	
	document.getElementById( 'message' ).innerHTML = "";
	
	if(type=='success'){
		var id = feature.getElementsByTagName('id')[0].firstChild.nodeValue;
		var featureCode = feature.getElementsByTagName('featureCode')[0].firstChild.nodeValue;
		var orgunitName = feature.getElementsByTagName('orgunitName')[0].firstChild.nodeValue;
		document.getElementById('organisationUnitCode').value = featureCode;
		document.getElementById('featureid').value = id;
		document.getElementById('button_delete').disabled = false;
		document.getElementById('button_assign').disabled = true;		
		selectOneFeature(featureCode);	
	}else{
		document.getElementById('organisationUnitCode').value = '';
		document.getElementById('featureid').value = '';
		document.getElementById('button_delete').disabled = true;
		document.getElementById('button_assign').disabled = false;
}

function selectOneFeature(featureCode){
	
	if(featureCode!=""){		
		var nodeList = domSVG('polygon');
			for(var i=0;i<nodeList.length;i++){
				g_element = nodeList.item(i);
				var id = g_element.getAttribute("id");
				
				if(featureCode==id){
					
					lastClickFill = g_element.getAttribute("fill");
					g_element.setAttributeNS(null, "fill", "red");
					lastFearureChoise = featureCode;				
				}
				
			}
	}
	}	
}


var lastFearureChoise;
var lastClickFill;
function selectedFeature(featureCode){
	if(featureCode!=""){		
		var nodeList = domSVG('polygon');
			for(var i=0;i<nodeList.length;i++){
				g_element = nodeList.item(i);
				var id = g_element.getAttribute("id");
				if(featureCode==id){
					lastClickFill = g_element.getAttribute("fill");
					g_element.setAttributeNS(null, "fill", "red");
					lastFearureChoise = featureCode;
					
				}
				
			}
	}
	
}

function changeAction(element_id){
	
	var nodeList = domSVG('polygon');
	for(var i=0;i<nodeList.length;i++){
		g_element = nodeList.item(i);
		g_element.removeEventListener("mouseover", showInfo, false);
		g_element.removeEventListener("mouseout", hiddenInfo, false);
		g_element.addEventListener("click", getInfo, false);
	}
	this.layer_actived=element_id;
}

function nonSelect(element_id){	
	var nodeList = domSVG('polygon');
	for(var i=0;i<nodeList.length;i++){
		g_element = nodeList.item(i);	
		
		var id = g_element.getAttribute("id");
		if(lastFearureChoise==id){
			g_element.setAttributeNS(null, "fill", lastClickFill);
		}
		
	}
}

function getInfo(evt){
	var target = evt.target;	
	if(lastFearureChoise){
		nonSelect(layer_actived);
	}
	
	if(layer_actived=="polyline"){
		  target.setAttributeNS(null, "stroke", stroke);
		  target.setAttributeNS(null, "stroke-width",stroke_width);
	}else{
		lastClickFill = target.getAttribute("fill");
		target.setAttributeNS(null, "fill", "red");
		var orgCode = target.getAttribute("id");
		lastFearureChoise = orgCode;
		document.getElementById('organisationUnitCode').value = orgCode;
		
	}
	
}



function validateAddFeature(){
	var organisationUnitCode = document.getElementById('organisationUnitCode').value;
	var orgUnit = document.getElementById('orgUnit').value;	
	
	var request = new Request();
 	request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( responseValidateAddFeature);
    request.send( "validateAssignMap.action?orgUnitId="+orgUnit+"&organisationUnitCode=" + organisationUnitCode);
}

function responseValidateAddFeature(message){
	var type = message.getAttribute( 'type' );  
	 var message = message.firstChild.nodeValue;
	 if ( type == 'success' ) {	
		 document.getElementById('button_assign').disabled = true;		
		 assignUnit();
     
  	 }	
	document.getElementById( 'message' ).innerHTML = message;
   	
	 
}

function assignUnit(){
	var organisationUnitCode = document.getElementById('organisationUnitCode').value;
	var orgUnit = document.getElementById('orgUnit').value;
	var comment = document.getElementById('comment').value;
	
	var request = new Request(); 	
    request.send( "assignUnitSaveAction.action?orgUnitId="+orgUnit+"&organisationUnitCode=" + organisationUnitCode + "&comment='"+ comment +"'");
}

function assignUnitSuccess(message){
 	var message = message.firstChild.nodeValue;	
	document.getElementById( 'message' ).innerHTML = message;
	
}

function deleteFeature(){
	if(confirm("Do you want delete ?")){
		var featureid = document.getElementById('featureid').value;
		var request = new Request();
		request.setResponseTypeXML( 'message' );
		request.setCallbackSuccess( deleteFeatureSuccess);
		request.send( "deleteFeature.action?featureId=" + featureid);		
	}else{
		return false;
	}
	
}
function deleteFeatureSuccess(message){
	var message = message.firstChild.nodeValue;
	document.getElementById( 'message' ).innerHTML = message;
}

function deleteAllFeature(){
	if(confirm("Do you want delete all ?")){		
		var request = new Request();
		request.setResponseTypeXML( 'message' );
		request.setCallbackSuccess( deleteFeatureSuccess);
		request.send( "deleteAllFeature.action");		
	}else{
		return false;
	}
}

function showAllFeature(){
	var request = new Request();
	request.setResponseTypeXML( 'features' );
	request.setCallbackSuccess( showAllSuccess);
	request.send( "showAllFeature.action");
}
function showAllSuccess(features){
	var featureList = features.getElementsByTagName("feature");
	for(var i=0;i<featureList.length;i++){
		var feature = featureList.item(i);
		var featureCode = feature.getElementsByTagName('featureCode')[0].firstChild.nodeValue;
		fillColor(featureCode,"RED");	
	}
	var message = features.getElementsByTagName("message")[0].firstChild.nodeValue;
	document.getElementById( 'showAllAssigned' ).style.display='block';
	document.getElementById( 'showAllAssignedData' ).innerHTML =  message;
	
}
function closeDiv(id){
	document.getElementById( id ).style.display='none';
}