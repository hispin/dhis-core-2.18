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
	jQuery.post( 'getValidationCriteria.action', { id: criteriaId }, function ( json ) {
		setInnerHTML( 'idField', json.validationCriteria.id );
		setInnerHTML( 'nameField', json.validationCriteria.name );
		setInnerHTML( 'descriptionField', json.validationCriteria.description );
		
		var property = json.validationCriteria.property;
		var operator = json.validationCriteria.operator;
		var	value = json.validationCriteria.value;
		
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
	});
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