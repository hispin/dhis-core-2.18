// ------------------------------------------------------------
//  Add New Form
// ------------------------------------------------------------
function validateForm(){
	var name = getFieldValue("name");
	var label = getFieldValue("label");
	var noRow = getFieldValue("noRow");
	var noColumn = getFieldValue("noColumn");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddFormCompleted );
	var url = 'validateForm.action?name=' + name;
	url += "&label=" + label;
	url += "&noRow=" + noRow;
	url += "&noColumn=" + noColumn;
 	request.send( url );    

}

function validateAddFormCompleted( xmlObject ){
	var type = xmlObject.getAttribute( 'type' );
	
    if(type =='error') setMessage(xmlObject.firstChild.nodeValue);
  
    if(type == 'success') {
		if(mode == "ADD") addForm();
		else updateForm();
	}
}

function addForm(){
	var name = getFieldValue("name");
	var label = getFieldValue("label");
	var noRow = getFieldValue("noRow");
	var noColumn = getFieldValue("noColumn");
	var noColumnLink = getFieldValue("noColumnLink");
	var icon = getFieldValue("icon");
	var visible = document.getElementById('visible').value;
	var attached =  document.getElementById('attached').value;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
	
    request.setCallbackSuccess( Completed );
	var url = 'addForm.action?name=' + name;
	url += "&label=" + label;
	url += "&noRow=" + noRow;
	url += "&noColumn=" + noColumn;
	url += "&noColumnLink=" + noColumnLink;
	url += "&icon=" + icon;
	url += "&visible=" + visible;
	url += "&attached=" + attached;
	
 	request.send( url );    
}

function Completed( xmlObject ){	
	window.location.reload();
}

// ------------------------------------------------------------
//  Update Form
// ------------------------------------------------------------

function updateForm(){
	
	var id = getFieldValue("id");
	var label = getFieldValue("label");
	var noRow = getFieldValue("noRow");
	var noColumn = getFieldValue("noColumn");
	var noColumnLink = getFieldValue("noColumnLink");
	var icon = getFieldValue("icon");
	var visible = document.getElementById('visible').value;
	var attached =  document.getElementById('attached').value;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
	
    request.setCallbackSuccess( Completed );
	var url = 'updateForm.action?name=' + name;
	url += "&id=" + id;
	url += "&label=" + label;
	url += "&noRow=" + noRow;
	url += "&noColumn=" + noColumn;
	url += "&noColumnLink=" + noColumnLink;
	url += "&icon=" + icon;
	url += "&visible=" + visible;
	url += "&attached=" + attached;
	
 	request.send( url );    
}

// ------------------------------------------------------------
//  Add New Egroup
// ------------------------------------------------------------
function validateEgroup(){
	
	var name = getFieldValue("name");
	var sortOrder = getFieldValue("sortOrder");
	var formID = getFieldValue("formID");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddEgroupCompleted );
	var url = 'validateEgroup.action?name=' + name;
	url += "&formID=" + formID;
	url += "&sortOrder=" + sortOrder;
	
	request.send( url );    

}

function validateAddEgroupCompleted( xmlObject ){

	var type = xmlObject.getAttribute( 'type' );
	
	if(type =='error') 
		alert(xmlObject.firstChild.nodeValue);
  
    if(type == 'success') {
		if(mode == "ADD") addEgroup();
		else updateEgroup();
	}
}

function addEgroup(){
	
	var formID = getFieldValue("formID");
	var name = getFieldValue("name");
	var sortOrder = getFieldValue("sortOrder");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( Completed );
	
	var url = 'addEgroup.action?name=' + name;
	url += "&formID=" + formID;
	url += "&sortOrder=" + sortOrder;
	
	request.send( url );    
}

// ------------------------------------------------------------
//  Update Egroup
// ------------------------------------------------------------

function updateEgroup(){
	
	var id = getFieldValue("id");
	var name = getFieldValue("name");
	var sortOrder = getFieldValue("sortOrder");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( Completed );
	
	var url = 'updateEgroup.action?name=' + name;
	url += "&id=" + id;
	url += "&sortOrder=" + sortOrder;
	
 	request.send( url );    
}

// --------------------------------------------------------------------------------------
//  Delete Egroup
// --------------------------------------------------------------------------------------
function deleteEgroup( id ){
	if(window.confirm(i18n_confirm_delete)){
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( Completed );			
		request.send( "deleteEgroup.action?id=" + id );  
	}		
}

// --------------------------------------------------------------------------------------
//  Get Egroup
// --------------------------------------------------------------------------------------
function getEgroup( id ){
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( getEgroupReceived );			
		request.send( "getEgroupById.action?id=" + id );  		
}

function getEgroupReceived( xmlObject ){
	
		// Available Egroups
		var selectedList = byId('selectedElements');
		selectedList.options.length = 0;
	
		var selectedElements = xmlObject.getElementsByTagName('elements')[0].getElementsByTagName('element');
		
		for(var i=0;i<selectedElements.length;i++){
			var element = selectedElements.item(i);
			var id = element.getElementsByTagName('id')[0].firstChild.nodeValue;
			var label = element.getElementsByTagName('label')[0].firstChild.nodeValue;
			selectedList.add(new Option(label, id),null);
		}
}

// ------------------------------------------------------------
//  Add New Element
// ------------------------------------------------------------
function validateElement(){
	
	var name = getFieldValue("name");
	var label = getFieldValue("label");
	var type = getFieldValue("type");
	var controlType = getFieldValue("controlType");
	var initialValue = getFieldValue("initialValue");
	var formLink = getFieldValue("formLink");
	var required = getFieldValue("required");
	var sortOrder = getFieldValue("sortOrder");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddElementCompleted );
	var url = 'validateEgroup.action?name=' + name;
	url += "&label=" + label;
	url += "&type=" + type;
	url += "&controlType=" + controlType;
	url += "&initialValue=" + initialValue;
	url += "&formLink=" + formLink;
	url += "&required=" + required;
	url += "&sortOrder=" + sortOrder;
	
	request.send( url );    

}

function validateAddElementCompleted( xmlObject ){

	var type = xmlObject.getAttribute( 'type' );
	
	if(type =='error') setMessage(xmlObject.firstChild.nodeValue);
  
    if(type == 'success') {
		if(mode == "ADD") addElement();
		else updateElement();
	}
}


function addElement(){
	
	var name = getFieldValue("name");
	var label = getFieldValue("label");
	var type = getFieldValue("type");
	var controlType = getFieldValue("controlType");
	var initialValue = getFieldValue("initialValue");
	var formLink = getFieldValue("formLink");
	var required = getFieldValue("required");
	var sortOrder = getFieldValue("sortOrder");
	var formid = getFieldValue("formid");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( Completed );
	
	var url = 'addElement.action?name=' + name;
	url += "&label=" + label;
	url += "&type=" + type;
	url += "&controlType=" + controlType;
	url += "&initialValue=" + initialValue;
	url += "&formLink=" + formLink;
	url += "&required=" + required;
	url += "&sortOrder=" + sortOrder;
	url += "&formID=" + formid;
	
	request.send( url );    
}

// ------------------------------------------------------------
//  Update Element
// ------------------------------------------------------------

function updateElement(){
	
	var id = getFieldValue("id");
	var name = getFieldValue("name");
	var label = getFieldValue("label");
	var type = getFieldValue("type");
	var controlType = getFieldValue("controlType");
	var initialValue = getFieldValue("initialValue");
	var formLink = getFieldValue("formLink");
	var required = getFieldValue("required");
	var sortOrder = getFieldValue("sortOrder");
	var formid = getFieldValue("formid");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( Completed );
	
	var url = 'updateElement.action?name=' + name;
	url += "&id=" + id;
	url += "&label=" + label;
	url += "&type=" + type;
	url += "&controlType=" + controlType;
	url += "&initialValue=" + initialValue;
	url += "&formLink=" + formLink;
	url += "&required=" + required;
	url += "&sortOrder=" + sortOrder;
	url += "&formID=" + formid;
	
 	request.send( url );    
}

// --------------------------------------------------------------------------------------
//  Delete Element
// --------------------------------------------------------------------------------------
function deleteElement( id ){
	if(window.confirm(i18n_confirm_delete)){
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( Completed );			
		request.send( "deleteElement.action?id=" + id );  
	}		
}

// --------------------------------------------------------------------------------------
//  Associate Elements Form Egroup
// --------------------------------------------------------------------------------------

function openAssociateElementsForEgroupForm( id ){  	 	 	
	
	//document.getElementById('association').value = id;
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( openAssociateElementsForEgroupReceived );
	request.send( "openAssociateElementsForEgroup.action?id=" + id );
}

flag = false;

function openAssociateElementsForEgroupReceived( xmlObject ){
	
	// process when the function is called first
	if(!flag){
		// Available Egroups
		var availableList = byId('availableEgroups');
		availableList.options.length = 0;
	
		if( xmlObject.getElementsByTagName('availabelEgroups').length == 1){
				
			var availableEgroups = xmlObject.getElementsByTagName('availabelEgroups')[0].getElementsByTagName('egroup');
			availableList.add(new Option("", 0),null);
			
			for(var i=0;i<availableEgroups.length;i++){
				var egroup = availableEgroups.item(i);
				var id = egroup.getElementsByTagName('id')[0].firstChild.nodeValue;
				var name = egroup.getElementsByTagName('name')[0].firstChild.nodeValue;
				availableList.add(new Option(name, id),null);
			}//end for
		}

		// Selected Elements
		var selectedElementslist = document.getElementById('selectedElements');
		selectedElementslist.options.length = 0;
	
		// show form
		setPositionCenter( 'association' );
		showDivEffect();
		showById( 'association' );
		flag = true;
	}
	
	// Avaulable Elements
	var availableElementsList = byId('availableElements');
	availableElementsList.options.length = 0;
			
	if( xmlObject.getElementsByTagName('availabelElements').length == 1){
			
		var availableElements = xmlObject.getElementsByTagName('availabelElements')[0].getElementsByTagName('element');
			
		for(var i=0;i<availableElements.length;i++){
			var element = availableElements.item(i);
			var id = element.getElementsByTagName('id')[0].firstChild.nodeValue;
			var label = element.getElementsByTagName('label')[0].firstChild.nodeValue;
			availableElementsList.add(new Option(label, id),null);
				
		}//end for
	}
}

// -----------------------------------------------------------------------------
//  Update Element for Egroup
// -----------------------------------------------------------------------------
function updateElementsForEgroup(){

	var list = document.getElementById('selectedElements').options;
	var selectedElements = '';	
	for(var i=0; i< list.length; i++){
		selectedElements = selectedElements + "&selectedElements=" + ( list[i].value );
	}
	
	var id = document.getElementById('availableEgroups').value;
	var url = 'updateElementsForEgroup.action?id='+id + selectedElements;
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.send(url);	
}

// -----------------------------------------------------------------------------
//  Update Element for Egroup
// -----------------------------------------------------------------------------
function createTableByForm(){
	
	var id = document.getElementById('formID').value;
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( Completed );
	request.send( "createTableByForm.action?id=" + id );
}

// --------------------------------------------------------------------------------------
//  Load add object form
// --------------------------------------------------------------------------------------

function getParamByURL(param){
	var name = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );

	return ( results == null ) ? "" : results[1];
}

function addObjectForm(){
	var url = 'addObjectForm.action?formId=' + getParamByURL('formId');
	var objectId = getParamByURL('objectId');
	if(objectId != '')
		url += '&objectId=' + objectId;
	window.location = url;
}
// --------------------------------------------------------------------------------------
//  Delete Object
// --------------------------------------------------------------------------------------
function deleteObject( id ){
	
	if(window.confirm(i18n_confirm_delete)){
		
		var result = getParamByURL('formId');
			
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( Completed );			
		request.send( "deleteObject.action?formId="+ result + "&id=" + id );  
	}		
}

// ------------------------------------------------------------
//  Update Object
// ------------------------------------------------------------

function updateForm(){
	
	var id = getFieldValue("id");
	var label = getFieldValue("label");
	var noRow = getFieldValue("noRow");
	var noColumn = getFieldValue("noColumn");
	var noColumnLink = getFieldValue("noColumnLink");
	var icon = getFieldValue("icon");
	var visible = document.getElementById('visible').value;
	var attached =  document.getElementById('attached').value;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
	
    request.setCallbackSuccess( Completed );
	var url = 'updateForm.action?name=' + name;
	url += "&id=" + id;
	url += "&label=" + label;
	url += "&noRow=" + noRow;
	url += "&noColumn=" + noColumn;
	url += "&noColumnLink=" + noColumnLink;
	url += "&icon=" + icon;
	url += "&visible=" + visible;
	url += "&attached=" + attached;
	
 	request.send( url );    
}

// --------------------------------------------------------------------------------------
//  Add Object
// --------------------------------------------------------------------------------------
function validateObject( required, element, strerror ){
	
	if (required == 'Yes' && element.value =='') {
		document.getElementById('info').style.display = 'block';
		document.getElementById('info').innerHTML = strerror;
		element.focus();
	}
	
}

function addObject () {	
	

	var formid = getParamByURL('formId');
	var dataParam = '?formId='+ formid;
	
	var start = 0;
	var objectId = getParamByURL('objectId');
	if(objectId != ''){
		dataParam += '&data='+ objectId;
		start=1;
	}
	
	var arrayOfElements = document.getElementsByName('data');
	for(var i=start;i<arrayOfElements.length;i++){
		dataParam += '&data='+ arrayOfElements[i].value;
	}
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
 
 	request.setCallbackSuccess( Completed );
	
	var url  = 'addObject.action' + dataParam;
	
	request.send( url ); 
}

// ------------------------------------------------------------
//  Fillup Initial values into control
// ------------------------------------------------------------
function fillup (strSelect, str, div_name){
	if(str==null && str=='')
		return;
	var arr = str.split(",");
	var result = strSelect;
	for(var i=0; i<arr.length; i++){
		result += "<option value='" + arr[i].replace(/^\s*|\s*$/g, "") + "'>" + arr[i] + "</option>";
	}
	result += '</select>';
	document.getElementById(div_name).innerHTML = result;
}

// ------------------------------------------------------------
// Open Update Object Form - Load data of Object into control
// ------------------------------------------------------------
/*function openUpdateObjectForm( id ){
		var request = new Request();
		request.setResponseTypeXML( 'object' );
		request.setCallbackSuccess( openUpdateObjectFormReceived );
		
		var formid = getParamByURL('formId');
		var url = 'getObject.action?data=' + data;
		url += "&id=" + id;
		url += "&formId=" + formid;
	
		request.send( url );		
}

function openUpdateObjectFormReceived( object ){		
		
		var data = getElementValue(object, 'id').split(";");
		
		var arrayOfElements = document.getElementsByName('data');
		for(var i=start;i<arrayOfElements.length;i++){
			arrayOfElements[i].value = data[i];
		}
	
		var request = new Request();
    	request.setResponseTypeXML( 'xmlObject' );
 
 		request.setCallbackSuccess( Completed );
	
		var url  = 'addObjectForm.action' + dataParam;
	
		request.send( url ); 
}

// ------------------------------------------------------------
// Update Object
// ------------------------------------------------------------
function updateObject(data){
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( Completed );
	
	var formid = getParamByURL('formId');
	
	var url = 'updateObjectForm.action?data=' + data;
	url += "&id=" + id;
	url += "&formId=" + formid;
	
 	request.send( url );    
}*/

// ------------------------------------------------------------
// Search Objects
// ------------------------------------------------------------
function searchObject(search_div){
	
	var keyword = document.getElementById(search_div).value;
	var formid = getParamByURL('formId');
	var url = 'searchObject.action?keyword=' + keyword;
	url += "&formId=" + formid;
	window.location = url;
}

// ------------------------------------------------------------
// Validate date value
// ------------------------------------------------------------

// ------------------------------------------------------------
// Check data in textfield
// Copyright@http://www.codeproject.com/KB/scripting/keyboard_restrict_spanish.aspx
// validchars : chars valid, user can input the chars
// ------------------------------------------------------------
function keyRestrict(e, validchars) {
	var key='', keychar='';
	 key = getKeyCode(e);
	 if (key == null) 
	 	return true;
	 keychar = String.fromCharCode(key);
	 keychar = keychar.toLowerCase();
	 validchars = validchars.toLowerCase();
	 if (validchars.indexOf(keychar) != -1)
	 	 return true;
	 if ( key==null || key==0 || key==8 || key==9 || key==13 || key==27 )
	  	return true;
	 return false;
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}

// Input Number
function isNumber(type){
	return (type.toLowerCase()=='number' || type.toLowerCase()=='double') ? true : false;
}

// Input datetime
function isDate(element, strError) {
	var re = /^(\d{2,4})(\/|-)(\d{1,2})(\/|-)(\d{1,2})$/;
	var result = (re.test(element.value)) ? true : false;
	if(!result){
		document.getElementById('info').style.display = 'block';
		document.getElementById('info').innerHTML = strError;
		//element.focus();
	}
}

// Input Tree









