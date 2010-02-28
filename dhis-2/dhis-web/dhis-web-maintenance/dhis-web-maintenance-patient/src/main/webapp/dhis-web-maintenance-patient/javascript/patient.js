
function organisationUnitSelected( orgUnits )
{		
    window.location.href = 'patient.action';    
}


selection.setListenerFunction( organisationUnitSelected );

//------------------------------------------------------------------------------
// Check for Integer
//------------------------------------------------------------------------------

function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {   	
        return false;
    }
    
    return true;
}

//------------------------------------------------------------------------------
// Validate EnrollmentDate
//------------------------------------------------------------------------------

function validateProgramEnrollment()
{	
	
	var url = 'validatePatientProgramEnrollment.action?' +
			'enrollmentDate=' + getFieldValue( 'enrollmentDate' ) +
			'&dateOfIncident=' + getFieldValue( 'dateOfIncident' ) ;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( programEnrollmentValidationCompleted );    
    request.send( url );        

    return false;
}

function programEnrollmentValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'programEnrollmentForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_program_enrollment_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

//-----------------------------------------------------------------------------
//Save
//-----------------------------------------------------------------------------

function saveDueDate( programStageInstanceId, programStageInstanceName )
{
	var field = document.getElementById( 'value[' + programStageInstanceId + '].date' );
	
	field.style.backgroundColor = '#ffffcc';
	
	var dateSaver = new DateSaver( programStageInstanceId, field.value, '#ccffcc' );
	dateSaver.save();
  
}


//-----------------------------------------------------------------------------
//Saver objects
//-----------------------------------------------------------------------------

function DateSaver( programStageInstanceId_, dueDate_, resultColor_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ccccff';
	
	var programStageInstanceId = programStageInstanceId_;	
	var dueDate = dueDate_;
	var resultColor = resultColor_;	

	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );
		request.send( 'saveDueDate.action?programStageInstanceId=' + programStageInstanceId + '&dueDate=' + dueDate );
	};

	function handleResponse( rootElement )
	{
		var codeElement = rootElement.getElementsByTagName( 'code' )[0];
		var code = parseInt( codeElement.firstChild.nodeValue );
   
		if ( code == 0 )
		{
			markValue( resultColor );                   
		}
		else
		{
			markValue( ERROR );
			window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
		}
	}

	function handleHttpError( errorCode )
	{
		markValue( ERROR );
		window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
	}   

	function markValue( color )
	{       
   
		var element = document.getElementById( 'value[' + programStageInstanceId + '].date' );	
           
		element.style.backgroundColor = color;
	}
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patient' );
    request.setCallbackSuccess( patientReceived );
    request.send( 'getPatient.action?id=' + patientId );
}

function patientReceived( patientElement )
{   
    var identifiers = patientElement.getElementsByTagName( "identifier" );   
    
    var identifierText = '';
	
	for ( var i = 0; i < identifiers.length; i++ )
	{		
		identifierText = identifierText + identifiers[ i ].getElementsByTagName( "identifierText" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setFieldValue( 'identifierField', identifierText );
	
	var attributes = patientElement.getElementsByTagName( "attribute" );   
    
    var attributeValues = '';
	
	for ( var i = 0; i < attributes.length; i++ )
	{	
		attributeValues = attributeValues + '<strong>' + attributes[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue  + ':  </strong>' + attributes[ i ].getElementsByTagName( "value" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setFieldValue( 'attributeField', attributeValues );
    
    var programs = patientElement.getElementsByTagName( "program" );   
    
    var programName = '';
	
	for ( var i = 0; i < programs.length; i++ )
	{		
		programName = programName + programs[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setFieldValue( 'programField', programName );
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId, fullName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + fullName );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removePatientCompleted );
        window.location.href = 'removePatient.action?id=' + patientId;
    }
}

function removePatientCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'patientform.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function validateSearchPatient()
{	
	
	var params = 'searchText=' + getFieldValue( 'searchText' );	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
 	request.setCallbackSuccess( searchValidationCompleted );    
	request.sendAsPost( params );
 	request.send( "validateSearchPatient.action" );        

 	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if ( type == 'success' )
	{
		var form = document.getElementById( 'searchPatientForm' );        
		form.submit();
	}
	else if ( type == 'error' )
	{
		window.alert( i18n_searching_patient_failed + ':' + '\n' + message );
	}
	else if ( type == 'input' )
	{
		document.getElementById( 'message' ).innerHTML = message;
		document.getElementById( 'message' ).style.display = 'block';
	}
}


// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function validateAddPatient()
{
	
	var age = document.getElementById( 'age' );
		
	if( age.value != '' )
	{
		if( !isInt( age.value ) )
		{
			window.alert( i18n_age_must_integer );
			age.select();
			age.focus();
			
			return false;
		}
	}	
	
	var params = '&checkedDuplicate='+checkedDuplicate +
				'&firstName=' + getFieldValue( 'firstName' ) +
				'&middleName=' + getFieldValue( 'middleName' ) +
				'&lastName=' + getFieldValue( 'lastName' ) +
				'&gender=' + getFieldValue( 'gender' ) +
				'&birthDate=' + getFieldValue( 'birthDate' ) +	        
				'&age=' + getFieldValue( 'age' ) +
				'&genre=' + getFieldValue('gender')
				+ getIdParams();
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted ); 
	request.sendAsPost( params );	
    request.send( "validatePatient.action" );        

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	removeDisabledIdentifier();
    	document.getElementById('addPatientForm').submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
    else if( type == 'duplicate' )
    {
    	if( !checkedDuplicate )
    		showListPatientDuplicate(messageElement, true);
    }
}


// -----------------------------------------------------------------------------
// Update Patient
// -----------------------------------------------------------------------------

function validateUpdatePatient()
{
    var params = 'id=' + getFieldValue( 'id' ) +
				'&firstName=' + getFieldValue( 'firstName' ) +
				'&middleName=' + getFieldValue( 'middleName' ) +
				'&lastName=' + getFieldValue( 'lastName' ) +
				'&gender=' + getFieldValue( 'gender' ) +
				'&birthDate=' + getFieldValue( 'birthDate' ) 
				+ getIdParams();
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );   
    request.sendAsPost( params );
    request.send( "validatePatient.action" );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var form = document.getElementById( 'updatePatientForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
    else if( type == 'duplicate' )
    {
    	if( !checkedDuplicate )
    		showListPatientDuplicate(messageElement, true);
    }
}

function getIdParams()
{
	var params = "";
	jQuery("input.idfield").each(function(){
		if( jQuery(this).val() && !jQuery(this).is(":disabled") )
			params += "&" + jQuery(this).attr("name") +"="+ jQuery(this).val();
	});
	return params;
}

function removeDisabledIdentifier()
{
	jQuery(".idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).remove();
	});
}

//-----------------------------------------------------------------------------
//Move members
//-----------------------------------------------------------------------------
var selectedList;
var availableList;

function move( listId ) {
	
	var fromList = document.getElementById(listId);
	
	if ( fromList.selectedIndex == -1 ) { return; }
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById( 'availableList' );
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById( 'selectedList' );
	}
	
	var toList = ( fromList == availableList ? selectedList : availableList );
	
	while ( fromList.selectedIndex > -1 ) {
		
		option = fromList.options.item(fromList.selectedIndex);
		fromList.remove(fromList.selectedIndex);
		toList.add(option, null);
	}
}

function submitForm() {
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById('availableList');
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById('selectedList');
	}
	
	selectAll( selectedList );
	
	return false;
}

function selectAll( list ) 
{
	for ( var i = 0, option; option = list.options.item(i); i++ ) 
	{
		option.selected = true;
	}
}

function ageOnchange()
{
	jQuery("#birthDate").rules("remove","required");
	jQuery("#birthDate").val("");
	jQuery("#birthDate").removeClass("error");
	jQuery("#age").rules("add",{required:true});

}

function bdOnchange()
{
	jQuery("#age").rules("remove","required");
	jQuery("#age").val("")
	jQuery("#birthDate").rules("add",{required:true});
}

function checkDuplicate()
{
	var params = 
				'&firstName=' + getFieldValue( 'firstName' ) +
				'&middleName=' + getFieldValue( 'middleName' ) +
				'&lastName=' + getFieldValue( 'lastName' ) +
				'&gender=' + getFieldValue( 'gender' ) +
				'&birthDate=' + getFieldValue( 'birthDate' ) +	        
				'&age=' + getFieldValue( 'age' ) ;
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( checkDuplicateCompleted ); 
	request.sendAsPost( params );	
    request.send( "validatePatient.action" );        

    return false;
}
function checkDuplicateCompleted( messageElement )
{
	checkedDuplicate = true;    
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if( type == 'success')
    {
    	alert(i18n_no_duplicate_found);
    }
    if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
    else if( type == 'duplicate' )
    {
    	showListPatientDuplicate(messageElement, false);
    }
}
/**
 * Show list patient duplicate  by jQuery thickbox plugin
 * @param rootElement : root element of the response xml
 * @param validate  :  is TRUE if this method is called in validation method  
 */
function showListPatientDuplicate(rootElement, validate)
{
	var message = rootElement.firstChild.nodeValue;
	var patients = rootElement.getElementsByTagName('patient');
	var sPatient = "";
	if( patients && patients.length > 0 )
	{
		for( var i = 0; i < patients.length ; i++ )
		{
			sPatient += "<hr style='margin:5px 0px;'><table>";
			sPatient += "<tr><td><strong>"+i18n_patient_system_id+"</strong></td><td>"+ getElementValue( patients[i], 'systemIdentifier' )+"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_fullName+"</strong></td><td>"+ getElementValue( patients[i], 'fullName' )+"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_gender+"</strong></td><td>"+ getElementValue(  patients[i], 'gender' )+"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_date_of_birth+"</strong></td><td>"+getElementValue(  patients[i], 'dateOfBirth' ) +"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_age+"</strong></td><td>"+ getElementValue(  patients[i], 'age' ) +"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_blood_group+"</strong></td><td>"+ getElementValue(  patients[i], 'bloodGroup' ) +"</td></tr>";
        	var identifiers =  patients[i].getElementsByTagName('identifier');
        	if( identifiers && identifiers.length > 0 )
        	{
        		for( var j = 0; j < identifiers.length ; j++ )
        		{
        			sPatient +="<tr class='identifierRow'>"
        				+"<td><strong>"+getElementValue( identifiers[j], 'name' )+"</strong></td>"
        				+"<td>"+getElementValue( identifiers[j], 'value' )+"</td>	"	
        				+"</tr>";
        		}
        	}
        	var attributes =  patients[i].getElementsByTagName('attribute');
        	if( attributes && attributes.length > 0 )
        	{
        		for( var k = 0; k < attributes.length ; k++ )
        		{
        			sPatient +="<tr class='attributeRow'>"
        				+"<td><strong>"+getElementValue( attributes[k], 'name' )+"</strong></td>"
        				+"<td>"+getElementValue( attributes[k], 'value' )+"</td>	"	
        				+"</tr>";
        		}
        	}
        	sPatient += "<tr><td colspan='2'><input type='button' id='"+getElementValue(  patients[i], 'id' )+"' value='"+i18n_edit_this_patient+"' onclick='edit(this)'/></td></tr>";
        	sPatient += "</table>";
		}
		jQuery("#thickboxContainer","#hiddenModalContent").html("").append(sPatient);
		if( !validate ) jQuery("#btnCreateNew","#hiddenModalContent").click(function(){window.parent.tb_remove();});
		else jQuery("#btnCreateNew","#hiddenModalContent").click(function(){window.parent.tb_remove();window.parent.checkedDuplicate = true; window.parent.validatePatient();});
		tb_show( message, "#TB_inline?height=500&width=500&inlineId=hiddenModalContent", null);
	}
}
function validatePatient()
{
	if( jQuery("#id").val() )
		validateUpdatePatient();
	else validateAddPatient();
}

function toggleUnderAge(this_)
{
	if( jQuery(this_).is(":checked")){
		tb_show(i18n_child_representative,"showAddRepresentative.action?TB_iframe=true&height=500&width=500",null);
	}
}