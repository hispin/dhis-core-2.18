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