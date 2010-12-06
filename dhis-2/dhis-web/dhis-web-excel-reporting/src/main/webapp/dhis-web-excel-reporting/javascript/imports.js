function organisationUnitSelected( orgUnits ){
	window.location = "getImportingParams.action";
}
selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// IMPORT DATA FROM EXCEL FILE INTO DATABASE
// -----------------------------------------------------------------------------

function importData(){
	
	var excelItemGroupId = byId('excelItemGroupId').value;	
	var periodId = byId('period').value;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( importDataCompleted );
	
	// URL
	var params = 'excelItemGroupId='+excelItemGroupId;
	// USER choose reportItem
	var preview = byId('showValue').style.display;
	
	if(preview == 'block'){
		var excelItems = document.getElementsByName('excelItems');
		for(var i=0;i<excelItems.length;i++){
			if(excelItems[i].checked ){
				params +='&excelItemIds=' + excelItems[i].value;
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
	request.setCallbackSuccess( getReportItemValuesReceived );
	request.send( "previewDataFlow.action?excelItemGroupId=" + byId("excelItemGroupId").value );
	
}

function getReportItemValuesReceived( xmlObject ){
	
	if(xmlObject.getElementsByTagName('excelItemValueByOrgUnit').length > 0 ){
		previewOrganisation(xmlObject);
	}
	else if(xmlObject.getElementsByTagName('excelItemValueByCategory').length > 0 ){
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
	
	var availableObjectList = xmlObject.getElementsByTagName('excelItemValue');
	
	var myTable = byId('showExcelItemValues');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	
	for(var i = byId("showExcelItemValues").rows.length; i > 1;i--)
	{
		byId("showExcelItemValues").deleteRow(i -1);
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
			newTD1.innerHTML= "<input type='checkbox' name='excelItems' onChange='javascript: checkAllSelect(this);' id='excelItems' value='" + id + "'>" ;
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
	
	var availableObjectList = xmlObject.getElementsByTagName('excelItemValueByOrgUnit');
	var myTable = byId('showExcelItemValues');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	
	for(var i = byId("showExcelItemValues").rows.length; i > 1;i--)
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
		var valueList = itemValue.getElementsByTagName('excelItemValue');
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
				newTD1.innerHTML= "<input type='checkbox' name='excelItems' id='excelItems' value='" + orgunitId + "-" + i + "-" + id + "'>" ;
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
	
	var availableObjectList = xmlObject.getElementsByTagName('excelItemValueByCategory');
	
	var myTable = byId('showExcelItemValues');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	
	for(var i = byId("showExcelItemValues").rows.length; i > 1;i--)
	{
		byId("showExcelItemValues").deleteRow(i -1);
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
			newTD1.innerHTML= "<input type='checkbox' name='excelItems' id='excelItems' value='" + id + "-" + row + "-" + expression + "'>" ;
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
	
	var reportItems = document.getElementsByName('excelItems');
	
	for(var i=0;i<reportItems.length;i++){
		reportItems[i].checked = select;
	 }
 }

// --------------------------------------------------------------------
// PERIOD TYPE
// --------------------------------------------------------------------

function getPeriodsByExcelItemGroup(excelItemGroupId) {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'getPeriodsByExcelItemGroup.action?excelItemGroupId=' + excelItemGroupId);
}

function responseListPeriodReceived( xmlObject ) {

	clearListById('period');
	var list = xmlObject.getElementsByTagName('period');
	for ( var i = 0; i < list.length; i++ )
    {
        item = list[i];
        var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
        
		addOption('period', name, i);
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

function validateUploadExcelImport ( fileName, columnIndex ) {

    var list = byId( 'list' );
    
    var rows = list.getElementsByTagName( 'tr' );
    
    for ( var i = 0; i < rows.length; i++ )
    {
        var cell = rows[i].getElementsByTagName( 'td' )[columnIndex-1];
        var value = cell.firstChild.nodeValue;
		
        if ( value.toLowerCase().indexOf( fileName.toLowerCase() ) != -1 )
        {
            // file is existsing
			return window.confirm( i18n_confirm_override );
        }
    }
      
	// normally upload
	return true;
}

function validateUploadExcelImport(){

	$.ajaxFileUpload
	(
		{
			url:'validateUploadExcelImport.action',
			secureuri:false,
			fileElementId:'upload',
			dataType: 'json',
			success: function (data, status)
			{
				if ( typeof( data.response ) != 'undefined' )
				{
					if( data.response != 'success' )
					{
						setMessage( data.message );
					}
					else
					{
						uploadExcelImport();
					}
				}
			},
			error: function (data, status, e)
			{
				alert(e);
			}
		}
	);
}
	
function uploadExcelImport(){

	$.ajaxFileUpload
	(
		{
			url:'uploadExcelImport.action',
			secureuri:false,
			fileElementId:'upload',
			dataType: 'xml',
			success: function (data, status)
			{
				window.location.reload();
			},
			error: function (data, status, e)
			{
				alert(e);
			}
		}
	);
}