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
	$.ajax({
		url: 'getValidationCriteria.action?id=' + criteriaId,
		cache: false,
		dataType: "xml",
		success: validationCriteriaReceived
	});
}

function validationCriteriaReceived( validationCriteria )
{
    setInnerHTML( 'nameField', $(validationCriteria).find('name').text() );
    setInnerHTML( 'descriptionField', $(validationCriteria).find('description' ).text() );
	
	var property = $(validationCriteria).find('property').text()
	var operator = $(validationCriteria).find('operator').text()
	var	value = $(validationCriteria).find('value').text()
	
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
	
	if(propertyName != '')
	{
		 var div = byId(propertyName + 'Div');
		 div.style.display = 'block';
		 
		 if( propertyName == 'gender' || 
			propertyName == 'dobType' || 
			propertyName == 'bloodGroup' ){
				
			byId('operator').selectedIndex = 1;
			disable('operator');
		 }else{
			enable('operator');
		 }
	 }
}

function hideDiv()
{
	hideById('genderDiv');
	hideById('integerValueOfAgeDiv');
	hideById('birthDateDiv');
	hideById('dobTypeDiv');
	hideById('bloodGroupDiv');		
}

function fillValue( value ){
	byId('value').value = value;
}