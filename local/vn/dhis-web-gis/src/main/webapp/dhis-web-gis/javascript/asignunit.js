/***********************************************************
* ************SELECT ACTION FUNCTION *****************
************************************************************/
var currentOrganisationId;
var currentFeatureCode;
var featureId;
/*  Left select organisation unit list */

function cleanCurrentValue(){
	featureId = '';
	currentFeatureCode = '';
	featureId = '';
}
function selectOrgunit(id,name){
	
	currentOrganisationId = id;
	hideById('message');
	
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
	
	if(type=='success'){
		featureId = feature.getElementsByTagName('id')[0].firstChild.nodeValue;
		currentFeatureCode = feature.getElementsByTagName('featureCode')[0].firstChild.nodeValue;
		var orgunitName = feature.getElementsByTagName('orgunitName')[0].firstChild.nodeValue;	
		selectOneFeature(currentFeatureCode);	
	}else{
	}
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
			}				
		}
	}		
}

function getFeatureCode(evt){
	var target = evt.target;
	refreshColor('#FFFFCC');		
	target.setAttributeNS(null, "fill", "red");
	currentFeatureCode = target.getAttribute("id");	
}



function validateAddFeature(){	
	var request = new Request();
 	request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( responseValidateAddFeature);
    request.send( "validateAssignMap.action?orgUnitId=" + currentOrganisationId + "&organisationUnitCode=" + currentFeatureCode );
}

function responseValidateAddFeature(message){
	var type = message.getAttribute( 'type' );  
	var message = message.firstChild.nodeValue;
	if ( type == 'success' ) {			
		assignUnit();     
	}else{
		setMessage( message );
	}	 
}

function assignUnit(){		
	var request = new Request(); 
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( assignUnitSuccess);	
	request.send( "assignUnitSaveAction.action?orgUnitId=" + currentOrganisationId + "&organisationUnitCode=" + currentFeatureCode);
}

function assignUnitSuccess(message){
	var type = message.getAttribute( 'type' ); 
 	var message = message.firstChild.nodeValue;	
	if ( type == 'success' ) {
		setMessage( message );
	}	
}

function deleteFeature(){
	if(confirm(i18n_confirm_delete)){		
		var request = new Request();
		request.setResponseTypeXML( 'message' );
		request.setCallbackSuccess( deleteFeatureSuccess);
		request.send( "deleteFeature.action?featureId=" + featureId);		
	}else{
		return false;
	}	
}
function deleteFeatureSuccess(message){
	var message = message.firstChild.nodeValue;
	setMessage( message );
	refreshColor('#FFFFCC');
}

function deleteAllFeature(){
	if(confirm(i18n_confirm_delete)){		
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
	setPositionCenter( 'showAllAssigned' );
	showById( 'showAllAssigned' );
	document.getElementById( 'showAllAssigned' ).innerHTML =  message;
	
}
