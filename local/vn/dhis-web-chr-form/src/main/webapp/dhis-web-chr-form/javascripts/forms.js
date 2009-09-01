// ------------------------------------------------------------
//  Add New Form
// ------------------------------------------------------------
function validateForm(){
	var name = getFieldValue("name");
	var label = getFieldValue("label");
	var noColumn = getFieldValue("noColumn");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddFormCompleted );
	var url = 'validateForm.action?name=' + name;
	url += "&label=" + label;
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
	var noColumn = getFieldValue("noColumn");
	var noColumnLink = getFieldValue("noColumnLink");
	var icon = getFieldValue("icon");
	var visible = document.getElementById('visible').value;
	var visible = document.getElementById("visible").checked;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
	
    request.setCallbackSuccess( Completed );
	var url = 'addForm.action?name=' + name;
	url += "&label=" + label;
	url += "&noColumn=" + noColumn;
	url += "&noColumnLink=" + noColumnLink;
	url += "&icon=" + icon;
	url += "&visible=" + visible;
	
 	request.send( url );    
}

function Completed( xmlObject ){
	
	if(document.getElementById('message') != null){
		document.getElementById('message').style.display = 'block';
		document.getElementById('message').innerHTML = xmlObject.firstChild.nodeValue;
	}
	
	window.location.reload();
	
}

// ------------------------------------------------------------
//  Update Form
// ------------------------------------------------------------

function updateForm(){
	
	var id = getFieldValue("id");
	var label = getFieldValue("label");
	var noColumn = getFieldValue("noColumn");
	var noColumnLink = getFieldValue("noColumnLink");
	var icon = getFieldValue("icon");
	var visible = document.getElementById("visible").checked;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
	
    request.setCallbackSuccess( Completed );
	var url = 'updateForm.action?name=' + name;
	url += "&id=" + id;
	url += "&label=" + label;
	url += "&noColumn=" + noColumn;
	url += "&noColumnLink=" + noColumnLink;
	url += "&icon=" + icon;
	url += "&visible=" + visible;
	
 	request.send( url );    
}


//------------------------------------------------------------------------------
// Get Element By the Form
//------------------------------------------------------------------------------
function getElementsByForm(formName){
	
	var request = new Request();
	
	request.setResponseTypeXML( 'xmlObject' );
	
	request.setCallbackSuccess( getElementsByFormReceived );
	
	request.send( "getFormByName.action?name=" + formName );
}

function getElementsByFormReceived( xmlObject ){
	
		// Available Egroups
		var availableList = byId('availableElementsInForm');
		availableList.options.length = 0;
	
		var availableObjectList = xmlObject.getElementsByTagName('availabelElements')[0].getElementsByTagName('element');
		
		for(var i=0;i<availableObjectList.length;i++){
			var element = availableObjectList.item(i);
			//var id = element.getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = element.getElementsByTagName('name')[0].firstChild.nodeValue;
			var label = element.getElementsByTagName('label')[0].firstChild.nodeValue;
			availableList.add(new Option(label, name),null);
			//availableList.add(new Option(label, id),null);
		}
}

// ------------------------------------------------------------
//  Validate an Egroup
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


//------------------------------------------------------------------------------
// Add Egroup
//------------------------------------------------------------------------------

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
	var required = document.getElementById("required").checked;
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
	var required = document.getElementById("required").checked;
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
	var required = document.getElementById("required").checked;
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

// --------------------------------------------------------------------------------------
//  Add Object
// --------------------------------------------------------------------------------------

function validateDataObject(){

	// get formId
	var formid = getParamByURL('formId');
	var dataParam = '?formId='+ formid;
	
	var arrayOfElements = document.getElementsByName('data');
	for(var i=0;i<arrayOfElements.length;i++){
		dataParam += '&data='+ arrayOfElements[i].value;
	}
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateObjectCompleted );
	
	var url = 'validateObject.action' + dataParam;
	
	request.send( url );    

}

function validateObjectCompleted( xmlObject ){
	
	// get type into XML file ( The type is error or success)
	var type = xmlObject.getAttribute( 'type' );
	// type == error
    if(type =='error') {
		setMessage(xmlObject.firstChild.nodeValue);
	}
  	// type == success
    if(type == 'success') {
		if(mode == "ADD") 
			addObject();
		else 
			updateObject();
	}
}

// Validate data inputted into a control
function validateObject( required, element, strerror ){
	
	if (required == 'true' && element.value =='') {
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
		//start=1;
	}
	
	var arrayOfElements = document.getElementsByName('data');
	
	for(var i=start;i<arrayOfElements.length;i++){
		
		var data = null;
		
		// if(data=='on'){
		if(arrayOfElements[i].type =='checkbox' ){
			dataParam += '&data='+ arrayOfElements[i].checked;
		}else{
			dataParam += '&data='+ arrayOfElements[i].value;
		}
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
function openUpdateObjectForm( id ){
	
		var formid = getParamByURL('formId');
		var url = 'updateObjectForm.action?id=' + id;
		url += "&formId=" + formid;
		window.location = url;
}

// ------------------------------------------------------------
// Update Object
// ------------------------------------------------------------
function updateObject(){
	
	// Get Form's Id
	var formid = getParamByURL('formId');
	var dataParam = '?formId='+ formid;
	
	dataParam += '&data='+ getParamByURL('id')
	var start = 0;
	
	// Get Object's data, not have id of the Object
	/* var arrayOfElements = document.getElementsByName('data');
	for(var i=0;i<arrayOfElements.length;i++){
		dataParam += '&data='+ arrayOfElements[i].value;
	}*/
	
	var arrayOfElements = document.getElementsByName('data');
	for(var i=start;i<arrayOfElements.length;i++){
		
		var data = null;
		
		// if(data=='on'){
		if(arrayOfElements[i].type =='checkbox' ){
			dataParam += '&data='+ arrayOfElements[i].checked;
		}else{
			dataParam += '&data='+ arrayOfElements[i].value;
		}
	}
	
	//// Get Object's Id
	//var id = getParamByURL('id');
	//dataParam += '&data='+ id;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
 	request.setCallbackSuccess( Completed );
	var url  = 'updateObject.action' + dataParam;
	
	request.send( url ); 
}

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
	return (type.toLowerCase()=='integer' || type.toLowerCase()=='numeric') ? true : false;
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

// Configuration - Set Image directory on server
function saveImageDirectoryOnServer(value){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( Completed );
	var url = 'setImageDirectoryOnServer.action?imageDirectoryOnServer=' + value;
 	request.send( url );    
}

// Configuration - Set number of records showed
function saveNumberOfRecords(value){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( Completed );
	var url = 'setNumberOfRecords.action?numberOfRecords=' + value;
 	request.send( url );    
}


// --------------------------------------------------------------------------------------
//  Associate DataElements For an Element
// --------------------------------------------------------------------------------------

/* function openAssociateFormulasForElementForm( ){  	 	 	
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( openAssociateFormulasForElementFormReceived );
	request.send( "getDataSetsAndForms.action");
}

function openAssociateFormulasForElementFormReceived( xmlObject ){
	
	// process when the function is called first
	//if(!flag){
		// Available Dataset
		var availableList = byId('availableDataSet');
		availableList.options.length = 0;

		// Input name of datasets into combo box
		if( xmlObject.getElementsByTagName('dataSet').length > 0){
				
			var availableDataSet = xmlObject.getElementsByTagName('dataSet');
			availableList.add(new Option("", 0),null);

			for(var i=0;i<availableDataSet.length;i++){
				var dataset = availableDataSet.item(i);
				var id = dataset.getElementsByTagName('id')[0].firstChild.nodeValue;
				var name = dataset.getElementsByTagName('name')[0].firstChild.nodeValue;
				availableList.add(new Option(name, id),null);
			}//end for
		}


	// -------------------------------------------------------------------------
	// show forms's list into comboBox
	// -------------------------------------------------------------------------
	var availableFormList = byId('availableForms');
	availableFormList.options.length = 0;
	// Input name of datasets into combo box
	if( xmlObject.getElementsByTagName('form').length > 0){
				
		var availableDataSet = xmlObject.getElementsByTagName('form');
		availableFormList.add(new Option("", 0),null);

		for(var i=0;i<availableDataSet.length;i++){
			var form =xmlObject.getElementsByTagName('form').item(i);
			//var id = form.getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = form.getElementsByTagName('name')[0].firstChild.nodeValue;
			var label = form.getElementsByTagName('label')[0].firstChild.nodeValue;
			//availableFormList.add(new Option(name, id),null);
			//availableFormList.add(new Option(label, id),null);
			availableFormList.add(new Option(label, name),null);

		}//end for
	}// end if


		// Selected Dataelements 
		////var selectedElementslist = document.getElementById('selectedDataelements');
		////selectedElementslist.options.length = 0;
		document.getElementById('formula').value = '';
	
		// show form
		setPositionCenter( 'associationDataelement' );
		showDivEffect();
		showById( 'associationDataelement' );
		//flag = true;
	//}
	
	}
	*/
// --------------------------------------------------------------------------------------
//  Get DataElements Of the Chosen Dataset
// --------------------------------------------------------------------------------------

function getDataElements( dataSetId ){  	 	 	
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getDataElementsReceived );
	request.send( "getDataElementsOfDataSet.action?dataSetId=" + dataSetId);
}

function getDataElementsReceived( xmlObject ){
	
	var operandList = byId( "availableOperands" );
			
	operandList.options.length = 0;
	
	var operands = xmlObject.getElementsByTagName( "operand" );
	
	for ( var i = 0; i < operands.length; i++)
	{
		var id = operands[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var elementName = operands[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		var option = document.createElement( "option" );
		option.value = "[" + id + "]";
		option.text = elementName;
		
		operandList.add( option, null );	
	}

}

// --------------------------------------------------------------------------------------
//  Insert Formula Text
// --------------------------------------------------------------------------------------

function insertFormulaText(value){
	
	//document.getElementById('formular').value += ' ' + value;
	setFieldValue('formula', getFieldValue('formula') + ' ' + value);
}

function associateFormula(){
	
	var selectedElements = '';

	var url = "addFormReport.action?chosenOperand="+ document.getElementById('operand').value;
	
	url += "&formula=" + document.getElementById('formula').value ;
	url += "&mainForm=" + document.getElementById('mainForm').value;
	url += "&name=" + document.getElementById('name').value;
	
	var request = new Request();
	
    request.setResponseTypeXML( 'xmlObject' );
	
	request.setCallbackSuccess( Completed );
	
    request.send(url);
	
}

//------------------------------------------------------------------------------
// update formula
//------------------------------------------------------------------------------
function updateFormula(){
	
	var selectedElements = '';

	var url = "updateFormReport.action?chosenOperand="+ document.getElementById('operand').value;
	
	url += "&id=" + getParamByURL('id') ;
	url += "&formula=" + document.getElementById('formula').value ;
	url += "&mainForm=" + document.getElementById('mainForm').value;
	url += "&name=" + document.getElementById('name').value;

	var request = new Request();
	
    request.setResponseTypeXML( 'xmlObject' );
	
	request.setCallbackSuccess( Completed );
	
    request.send(url);
	
	window.location = 'listFormReports.action';
	
}

//------------------------------------------------------------------------------
// Show list of an element's formular
//------------------------------------------------------------------------------
function formularsInForm(){
	
	/* var request = new Request();
	
	request.setResponseTypeXML( 'xmlObject' );
	
	request.setCallbackSuccess( formularsInElementReceive );
	
	request.send( "listFormReports.action?id=" + elementId ); */
	
	
	window.location = "listFormReports.action?id=" + formId;
}
	
/* function formularsInElementReceive(xmlObject ){
	
	var div_formulars = byId( "listFormularsInElementDetails" );
	div_formulars.innerHTML = "<table width='100%'>"+
								"<tr>" +
									"<th width='30%'>Operand</th>" +
									"<th width='30%'>formular</th>" +
									"<th width='30%'>Operator</th>" +
								"</tr>";
			
	var elementValues = xmlObject.getElementsByTagName("elementvalue");
	
	alert(" elementValues.length : " + elementValues.length);
	
	for ( var i = 0; i < elementValues.length; i++)
	{
		var elementValueId = elementValues[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var formular = elementValues[ i ].getElementsByTagName( "formular" )[0].firstChild.nodeValue;
		var operand = elementValues[ i ].getElementsByTagName( "operand" )[0].firstChild.nodeValue;
		
		div_formulars.innerHTML+= "<tr>" + 
									"<td>" + operand + "</td>" + 
									"<td>" + formular + "</td>" +
									"<td>" + "<a href='javascript: deleteElementValue(elementValueId);' title='$i18n.getString('delete')'> "+
												"<img src='../images/delete.png' alt='$i18n.getString('delete')'></a>" + "</td>" +
								"</tr>";
		
	}
	
	div_formulars.innerHTML +="</table>" ;
	
	// show form
	setPositionCenter( 'listFormularsInElement' );
	showDivEffect();
	showById( 'listFormularsInElement' );
} */

// --------------------------------------------------------------------------------------
//  Delete ElementValue
// --------------------------------------------------------------------------------------
function deleteFormReport( id ){
	if(window.confirm(i18n_confirm_delete)){
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( Completed );			
		request.send( "deleteFormReport.action?id=" + id );  
	}		
}