function organisationUnitSelected( orgUnits ){
	window.location = "getImportingParams.action";
}
selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// IMPORT DATA FROM EXCEL FILE INTO DATABASE
// -----------------------------------------------------------------------------

function importData(){
	
	var importReportId = byId('importReportId').value;	
	var periodId = byId('period').value;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( importDataCompleted );
	
	// URL
	var params = 'importReportId='+importReportId;
	// USER choose exportItem
	var preview = byId('showValue').style.display;
	
	if(preview == 'block'){
		var importItems = document.getElementsByName('importItems');
		for(var i=0;i<importItems.length;i++){
			if(importItems[i].checked ){
				params +='&importItemIds=' + importItems[i].value;
			}
		}
	}	
	params += '&periodId='+ periodId;
	request.sendAsPost(params);
	request.send('importData.action'); 
}

function importDataCompleted( xmlObject ){
	
	setMessage(xmlObject.firstChild.nodeValue);	
}

// -----------------------------------------------------------------------------
// PREVIEW DATA FLOW
// -----------------------------------------------------------------------------

function getPreviewImportData(){	
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getExportItemValuesReceived );
	request.send( "previewDataFlow.action?importReportId=" + byId("importReportId").value );
	
}

function getExportItemValuesReceived( xmlObject ){

	if(xmlObject.getElementsByTagName('importItemValueByOrgUnit').length > 0 ){
		previewOrganisation(xmlObject);
	}
	else if(xmlObject.getElementsByTagName('importItemValueByCategory').length > 0 ){
		previewCategory(xmlObject);
	}
	else{
		previewNormal(xmlObject);
	}
}


// -----------------------------------------------------------------------------
// PREVIEW DATA - NORMAL
// -----------------------------------------------------------------------------

function previewNormal( xmlObject ){
	
	byId('selectAll').checked = false;
	var availableDiv = byId('showValue');
	availableDiv.style.display = 'block';
	
	var availableObjectList = xmlObject.getElementsByTagName('importItemValue');
	
	var myTable = byId('showImportItemValues');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	
	for(var i = byId("showImportItemValues").rows.length; i > 1;i--)
	{
		byId("showImportItemValues").deleteRow(i -1);
	}

	for(var i=0;i<availableObjectList.length;i++){
		
		// get values
		var itemValue = availableObjectList.item(i);
		// add new row
		var newTR = document.createElement('tr');
		// create new column
		var newTD2 = document.createElement('td');
		newTD2.innerHTML = itemValue.getElementsByTagName('name')[0].firstChild.nodeValue;
		// create new column
		var newTD3 = document.createElement('td');
		var value = 0;
		if( itemValue.getElementsByTagName('value')[0].firstChild != null ) {
			value = itemValue.getElementsByTagName('value')[0].firstChild.nodeValue;
		}
		newTD3.innerHTML = value;
		// create new column
		var newTD1 = document.createElement('td');
		var id = itemValue.getElementsByTagName('id')[0].firstChild.nodeValue;
		if(value!=0){
			newTD1.innerHTML= "<input type='checkbox' name='importItems' onChange='javascript: checkAllSelect(this);' id='importItems' value='" + id + "'>" ;
		}
		
		newTR.appendChild (newTD1);
		newTR.appendChild (newTD2);
		newTR.appendChild (newTD3);
		// add row into the table
		tBody.appendChild(newTR);
	}
}

function checkAllSelect(checkBox){
	if(!checkBox.checked){
		byId('selectAll').checked = false;
	}
}

// -----------------------------------------------------------------------------
// PREVIEW DATA - ORGANISATION
// -----------------------------------------------------------------------------

function previewOrganisation( xmlObject ){
	
	// show preview table
	byId('selectAll').checked = false;
	var availableDiv = byId('showValue');
	availableDiv.style.display = 'block';
	
	var availableObjectList = xmlObject.getElementsByTagName('importItemValueByOrgUnit');
	var myTable = byId('showImportItemValues');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	
	for(var i = byId("showImportItemValues").rows.length; i > 1;i--)
	{
		myTable.deleteRow(i -1);
	}
	
	for(var i=0;i<availableObjectList.length;i++){
		
		// get item into XML
		var itemValue = availableObjectList.item(i);
		
		// Add new row which contains to Organisation's name
		var newTR = document.createElement('tr');
		var newTD = document.createElement('td');
		newTD.colSpan = 3;
		var nameOrgUnit= itemValue.getElementsByTagName('orgUnit')[0];
		newTD.innerHTML = "<b>" + nameOrgUnit.getElementsByTagName('name')[0].firstChild.nodeValue + "</b>";
		var orgunitId =  nameOrgUnit.getElementsByTagName('id')[0].firstChild.nodeValue;
		newTR.appendChild (newTD);
		// add row into the table
		tBody.appendChild(newTR);
		
		// get values
		var valueList = itemValue.getElementsByTagName('importItemValue');
		for(var j=0;j<valueList.length;j++) {
		
			// get itemValue into XML
			itemValue = valueList.item(j);
			// add new row which contains to value
			var newTR = document.createElement('tr');
			// create new column
			var newTD2 = document.createElement('td');
			newTD2.innerHTML = itemValue.getElementsByTagName('name')[0].firstChild.nodeValue;
			// create new column
			var newTD3 = document.createElement('td');
			var value = 0;
			if( itemValue.getElementsByTagName('value')[0].firstChild != null ) {
				value = itemValue.getElementsByTagName('value')[0].firstChild.nodeValue;
			}
			//var value = itemValue.getElementsByTagName('value')[0].firstChild.nodeValue;
			newTD3.innerHTML = value;
			// create new column
			var newTD1 = document.createElement('td');
			var id = itemValue.getElementsByTagName('id')[0].firstChild.nodeValue;
			if(value!=0){
				newTD1.innerHTML= "<input type='checkbox' name='importItems' id='importItems' value='" + orgunitId + "-" + i + "-" + id + "'>" ;
			}
			
			newTR.appendChild (newTD1);
			newTR.appendChild (newTD2);
			newTR.appendChild (newTD3);
			// add row into the table
			tBody.appendChild(newTR);
			
			
		}// end for Get values
			
	}// end for availableObjectList
}


// -----------------------------------------------------------------------------
// PREVIEW DATA - CATEGORY
// -----------------------------------------------------------------------------

function previewCategory( xmlObject ){
	
	byId('selectAll').checked = false;
	var availableDiv = byId('showValue');
	availableDiv.style.display = 'block';
	
	var availableObjectList = xmlObject.getElementsByTagName('importItemValueByCategory');
	
	var myTable = byId('showImportItemValues');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	
	for(var i = byId("showImportItemValues").rows.length; i > 1;i--)
	{
		byId("showImportItemValues").deleteRow(i -1);
	}

	for(var i=0;i<availableObjectList.length;i++){
		
		// get values
		var itemValue = availableObjectList.item(i);
		// add new row
		var newTR = document.createElement('tr');
		// create new column
		var newTD2 = document.createElement('td');
		newTD2.innerHTML = itemValue.getElementsByTagName('name')[0].firstChild.nodeValue;
		// create new column
		var newTD3 = document.createElement('td');
		var value = 0;
		if( itemValue.getElementsByTagName('value')[0].firstChild != null ) {
			value = itemValue.getElementsByTagName('value')[0].firstChild.nodeValue;
		}

		newTD3.innerHTML = value;
		
		// create new column
		var newTD1 = document.createElement('td');
		var id = itemValue.getElementsByTagName('id')[0].firstChild.nodeValue;
		var row = itemValue.getElementsByTagName('row')[0].firstChild.nodeValue;
		var expression = itemValue.getElementsByTagName('expression')[0].firstChild.nodeValue
		if(value!=0){
			newTD1.innerHTML= "<input type='checkbox' name='importItems' id='importItems' value='" + id + "-" + row + "-" + expression + "'>" ;
		}
		
		newTR.appendChild (newTD1);
		newTR.appendChild (newTD2);
		newTR.appendChild (newTD3);
		// add row into the table
		tBody.appendChild(newTR);
	}
}


function selectAll(){
	 
	var select = byId('selectAll').checked;
	
	var exportItems = document.getElementsByName('importItems');
	
	for(var i=0;i<exportItems.length;i++){
		exportItems[i].checked = select;
	 }
 }

// --------------------------------------------------------------------
// PERIOD TYPE
// --------------------------------------------------------------------

function getPeriodsByImportReport( importReportId ) {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'getPeriodsByImportReport.action?importReportId=' + importReportId);
}

function responseListPeriodReceived( xmlObject ) {

	clearListById('period');
	
	var type = xmlObject.getAttribute( 'type' );

	if ( (type != undefined) && (type == 'error') )
	{
		setHeaderDelayMessage( xmlObject.firstChild.nodeValue );
	}
	else
	{
		var list = xmlObject.getElementsByTagName('period');
		
		for ( var i = 0; i < list.length; i++ )
		{
			item = list[i];
			var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
			
			addOption('period', name, i);
		}
	}
}

function lastPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'previousPeriodsGeneric.action' ); 
}

function nextPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'nextPeriodsGeneric.action' ); 
}

function validateUploadExcelImportByJSON(){

	jQuery( "#upload" ).upload( 'validateUploadExcelImport.action',
		{ 'draft': true },
		function ( data )
		{
			if ( data.response == 'error' )
			{              
				setMessage( data.message );
			}
			else
			{
				uploadExcelImport();
			}
		}, 'json'
	);
}

function validateUploadExcelImportByXML(){

	jQuery( "#upload" ).upload( 'validateUploadExcelImport.action',
		{ 'draft': true },
		function ( data )
		{
			data = data.getElementsByTagName('message')[0];
			var type = data.getAttribute("type");
			
			if ( type == 'error' )
			{              
				setMessage( data.firstChild.nodeValue );
			}
			else
			{
				uploadExcelImport();
			}
		}, 'xml'
	);
}
	
function uploadExcelImport(){
	
	jQuery( "#upload" ).upload( 'uploadExcelImport.action',
		{ 'draft': true },
		function( data, e ) {
			try {
				window.location.reload();
			}
			catch(e) {
				alert(e);
			}
		}
	);
}