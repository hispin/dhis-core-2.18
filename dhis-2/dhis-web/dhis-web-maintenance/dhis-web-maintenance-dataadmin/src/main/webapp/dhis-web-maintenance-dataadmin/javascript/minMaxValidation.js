function generateMinMaxValue(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( minMaxValueCompleted );	

	var datasetIds = "";
	var datasetField = byId( 'dataSetIds' ); 
	for (var i = 0; i < datasetField.options.length; i++)
	{
		if (datasetField.options[ i ].selected)
		{
		  datasetIds+= "dataSets=" + datasetField.options[ i ].value + "&";
		}
	}
	
	request.sendAsPost( datasetIds );
	request.send( 'generateMinMaxValue.action' );
	
}

function minMaxValueCompleted( xmlObject ) {
    showSuccessMessage (xmlObject.firstChild.nodeValue);
	return false;
}
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

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( minMaxValueCompleted );	
	
	var datasetIds = "";
	var datasetField = byId( 'dataSetIds' ); 
	for (var i = 0; i < datasetField.options.length; i++)
	{
		if (datasetField.options[ i ].selected)
		{
		  datasetIds+= "dataSets=" + datasetField.options[ i ].value + "&";
		}
	}
	
	request.sendAsPost( datasetIds );
	request.send( 'removeMinMaxValue.action' );
	
}