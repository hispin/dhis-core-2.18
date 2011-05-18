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
			propertyName == 'dobType' || 
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
	hideById('dobTypeDiv');
	hideById('bloodGroupDiv');		
}

function fillValue( value ){
	byId('value').value = value;
}

// -----------------------------------------------------------------------------
// Add Validation Criteria
// -----------------------------------------------------------------------------

function showAddValidationCriteriaForm()
{
	hideById('validationCriteriaList');
	jQuery('#loaderDiv').show();
	jQuery('#editValidationCriteriaForm').load('showAddValidationCriteriaForm.action',
	{
	}, function()
	{
		showById('editValidationCriteriaForm');
		jQuery('#loaderDiv').hide();
	});
}

function addValidationCriteria()
{	
	$.ajax({
		type: "POST",
		url: 'addValidationCriteria.action',
		data: getParamsForDiv('validationCriteriaForm'),
		success: function( json ) {
			if( json.response == 'success')
			{
				onClickBackToCriteriaBtn();
			}
		}
	});
	
    return false;
}

// -----------------------------------------------------------------------------
// Update Validation Criteria
// -----------------------------------------------------------------------------

function showUpdateValidationCriteriaForm( criteriaId )
{
	hideById('validationCriteriaList');
	jQuery('#loaderDiv').show();
	jQuery('#editValidationCriteriaForm').load('showUpdateValidationCriteriaForm.action',
	{
		id:criteriaId
	}, function()
	{
		showById('editValidationCriteriaForm');
		jQuery('#loaderDiv').hide();
	});
}

function updateValidationCriteria()
{	
	$.ajax({
		type: "POST",
		url: 'updateValidationCriteria.action',
		data: getParamsForDiv('validationCriteriaForm'),
		success: function( json ) {
			if( json.response == 'success')
			{
				onClickBackToCriteriaBtn();
			}
		}
	});
	
    return false;
}

// ------------------------------------------------------------------
// Assign Validation Criteria for programs
// ------------------------------------------------------------------

function showProgramsForValidationCriteriaForm( validationId )
{
	hideById('validationCriteriaList');
	jQuery('#loaderDiv').show();
	jQuery('#editValidationCriteriaForm').load('showProgramsForValidationCriteriaForm.action',
	{
		criteriaId:validationId
	}, function()
	{
		showById('editValidationCriteriaForm');
		jQuery('#loaderDiv').hide();
	});
}

function defineProgramsForValidationCriteria()
{	
	$.ajax({
		type: "POST",
		url: 'defineProgramsForValidationCriteria.action',
		data: getParamsForDiv('assignValidationCriteriaForm'),
		success: function( json ) {
			if( json.response == 'success')
			{
				onClickBackToCriteriaBtn();
			}
		}
	});
	
    return false;
}


// ------------------------------------------------------------------
// Click Back button
// ------------------------------------------------------------------

function onClickBackToCriteriaBtn()
{
	hideById('editValidationCriteriaForm');	
	jQuery('#loaderDiv').show();
	jQuery('#validationCriteriaList').load('validationCriteriaList.action',
	{
	}, function()
	{
		showById('validationCriteriaList');
		jQuery('#loaderDiv').hide();
	});
}	
