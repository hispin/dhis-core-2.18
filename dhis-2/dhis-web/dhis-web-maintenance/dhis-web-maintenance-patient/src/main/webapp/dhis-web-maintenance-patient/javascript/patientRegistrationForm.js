
function addNewForm(){
	window.location.href='viewPatientRegistrationForm.action?programId=' + getFieldValue('programId');
}

function updateNewForm( registrationFormId, programId ){
	window.location.href='viewPatientRegistrationForm.action?programId=' + programId + '&id=' + registrationFormId;
}

function removeRegistrationForm( registrationFormId, name )
{
	removeItem( registrationFormId, name, i18n_confirm_delete, 'delRegistrationFormAction.action' );	
}