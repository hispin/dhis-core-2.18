//-----------------------------------------------------------------------------
// Add Criteria
//-----------------------------------------------------------------------------

function validateCriteria()
{
	var params = 'name=' + getFieldValue( 'name' );
	if(byId('id') != null)
	{
		params += '&id=' + getFieldValue( 'id' );
	}
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validationCompleted ); 
	request.sendAsPost( params );
	request.send( "validateValidationCriteria.action");
}

function validationCompleted( xmlObject )
{
	 var type = xmlObject.getAttribute( 'type' );
	 var message = xmlObject.firstChild.nodeValue;
	 
	 if ( type == 'input' )
	 {
	     setMessage(message);
		 return;
	 }
	 document.forms['validationCriteriaForm'].submit();

}

// -----------------------------------------------------------------------------
// Remove Criteria
// -----------------------------------------------------------------------------

function removeCriteria( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'removeValidationCriteria.action' );
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showValidationCriteriaDetails( criteriaId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElement' );
    request.setCallbackSuccess( validationCriteriaReceived );
    request.send( 'getValidationCriteria.action?id=' + criteriaId );
}

function validationCriteriaReceived( validationCriteria )
{
    setInnerHTML( 'nameField', getElementValue( validationCriteria, 'name' ) );
    setInnerHTML( 'descriptionField', getElementValue( validationCriteria, 'description' ) );
	
	var property = getElementValue( validationCriteria, 'property' )
	var operator = getElementValue( validationCriteria, 'operator' )
	var	value = getElementValue( validationCriteria, 'value' )
	
	// get operator
	if(operator == 0 ){
		operator = '=';
	}else if(operator == -1 ){
		operator = '<';
	}else {
		operator = '>';
	}
	
	setInnerHTML('criteriaField', property + " " + operator + " " + value );
    showDetails();
}

// ----------------------------------------------------------------------------------------
// Show div to Add or Update Validation-Criteria
// ----------------------------------------------------------------------------------------
function showDivValue(){
	var propertyName = byId('property').value;
	hideDiv();
	if(propertyName != ''){
		 var div = byId(propertyName + 'Div');
		 div.style.display = 'block';
		 
		 if(propertyName == 'gender' || 
			propertyName == 'birthDateEstimated' || 
			propertyName == 'bloodGroup'){
				
			byId('operator').selectedIndex = 1;
			disable('operator');
		 }else{
			enable('operator');
		 }
	 }
}

function hideDiv(){
	hideById('genderDiv');
	hideById('integerValueOfAgeDiv');
	hideById('birthDateDiv');
	hideById('birthDateEstimatedDiv');
	hideById('bloodGroupDiv');		
}

function fillValue( value ){
	byId('value').value = value;
}