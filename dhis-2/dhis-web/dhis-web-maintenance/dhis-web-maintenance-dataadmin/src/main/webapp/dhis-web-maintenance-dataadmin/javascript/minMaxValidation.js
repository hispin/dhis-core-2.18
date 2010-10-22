//-----------------------------------------------------------------------------------
// Default Min/Max values
//-----------------------------------------------------------------------------------

function saveDefaultValues(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( saveDefaultValuesCompleted );	
	var params = 'minValue=' + getFieldValue('minValue');
		params += '&maxValue=' + getFieldValue('maxValue');
	request.send( 'saveDefaultMinMaxValues.action?' + params );	
}

function saveDefaultValuesCompleted(xmlObject){
	setMessage(xmlObject.firstChild.nodeValue);
}

//-----------------------------------------------------------------------------------
// Organisation Tree
//-----------------------------------------------------------------------------------
function treeClicked()
{
	numberOfSelects++;

	setMessage( i18n_loading );

	document.getElementById( "submitButton" ).disabled = true;
}

function selectCompleted( selectedUnits )
{
	numberOfSelects--;

	if ( numberOfSelects <= 0 )
	{
		hideMessage();
			
		document.getElementById( "submitButton" ).disabled = false;
	}
}

function unselectAllAtLevel( id ){
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
	request.send( 'unselectLevelMinMax.action?level=' + getFieldValue('levelList'));
}

function selectAllAtLevel( id ){
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'selectLevelMinMax.action?level=' + getFieldValue('levelList'));
}

function unselectGroup()
{
    var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectOrganisationUnitGroup.action?organisationUnitGroupId=' + getListValue( 'groupList' ) );
}

function selectGroup()
{
    var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'selectOrganisationUnitGroup.action?organisationUnitGroupId=' + getListValue( 'groupList' ) );
}

function unselectAll()
{
    var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectAllMinMax.action' );
}

function selectReceived()
{
    selectionTree.buildSelectionTree();
}

function validateForm(){
	if(byId('dataSetIds').value=='')
	{
		setMessage(i18n_not_choose_dataset);
		return false;
	}
	
	document.getElementById( 'minMaxGeneratingForm' ).submit();
}

//------------------------------------------------------------------------------
// Save factor
//------------------------------------------------------------------------------

function saveFactor(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( saveFactorSuccess );
    request.send( 'saveFactor.action?factor='+ getFieldValue('factor') );
}

function saveFactorSuccess(){
	setMessage( i18n_save_factory_success );
}

//-----------------------------------------------------------------------------------
// Organisation Tree
//-----------------------------------------------------------------------------------

function removeMinMaxValue(){
	var form = byId("minMaxGeneratingForm");
	form.action = "removeMinMaxValue.action"
	form.submit();
}