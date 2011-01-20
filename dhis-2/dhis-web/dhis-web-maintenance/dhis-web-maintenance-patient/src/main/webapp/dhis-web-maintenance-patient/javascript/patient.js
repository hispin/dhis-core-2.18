
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
	var field = document.getElementById( 'value_' + programStageInstanceId + '_date' );
	
	var dateOfIncident = new Date(byId('dateOfIncident').value);
	var dueDate = new Date(field.value);
	
	if( !isDate(dueDate,dateFormat) ){
		field.style.backgroundColor = '#FFCC00';
		alert(i18n_date_invalid);
		return;
	}
	
	if(dueDate < dateOfIncident){
		field.style.backgroundColor = '#FFCC00';
		alert( i18n_date_less_incident );
		return;
	}
	
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
   
		var element = document.getElementById( 'value_' + programStageInstanceId + '_date' );	
           
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
	// ----------------------------------------------------------------------------
	// Get common-information
    // ----------------------------------------------------------------------------
	
	var id = patientElement.getElementsByTagName( "id" )[0].firstChild.nodeValue;
	var fullName = patientElement.getElementsByTagName( "fullName" )[0].firstChild.nodeValue;   
	var gender = patientElement.getElementsByTagName( "gender" )[0].firstChild.nodeValue;   
	var dobType = patientElement.getElementsByTagName( "dobType" )[0].firstChild.nodeValue;   
	var birthDate = patientElement.getElementsByTagName( "dateOfBirth" )[0].firstChild.nodeValue;   
	var bloodGroup= patientElement.getElementsByTagName( "bloodGroup" )[0].firstChild.nodeValue;   
    
	var commonInfo =  '<strong>'  + i18n_id + ':</strong> ' + id + "<br>" 
					+ '<strong>' + i18n_full_name + ':</strong> ' + fullName + "<br>" 
					+ '<strong>' + i18n_gender + ':</strong> ' + gender+ "<br>" 
					+ '<strong>' + i18n_dob_type + ':</strong> ' + dobType+ "<br>" 
					+ '<strong>' + i18n_date_of_birth + ':</strong> ' + birthDate+ "<br>" 
					+ '<strong>' + i18n_blood_group  + ':</strong> ' + bloodGroup;
	
	setInnerHTML( 'commonInfoField', commonInfo );
	
	// ----------------------------------------------------------------------------
	// Get identifier
    // ----------------------------------------------------------------------------
	
	var identifiers = patientElement.getElementsByTagName( "identifier" );   
    
    var identifierText = '';
	
	for ( var i = 0; i < identifiers.length; i++ )
	{		
		identifierText = identifierText + identifiers[ i ].getElementsByTagName( "identifierText" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setInnerHTML( 'identifierField', identifierText );
	
	
	
	// ----------------------------------------------------------------------------
	// Get attribute
    // ----------------------------------------------------------------------------
	
	var attributes = patientElement.getElementsByTagName( "attribute" );   
    
    var attributeValues = '';
	
	for ( var i = 0; i < attributes.length; i++ )
	{	
		attributeValues = attributeValues + '<strong>' + attributes[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue  + ':  </strong>' + attributes[ i ].getElementsByTagName( "value" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setInnerHTML( 'attributeField', attributeValues );
    
    var programs = patientElement.getElementsByTagName( "program" );   
    
    var programName = '';
	
	for ( var i = 0; i < programs.length; i++ )
	{		
		programName = programName + programs[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setInnerHTML( 'programField', programName );
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------
function removePatient( patientId, fullName )
{
	removeItem( patientId, fullName, i18n_confirm_delete, 'removePatient.action' );
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
// Disable form
// -----------------------------------------------------------------------------

function disableForm()
{
    $('#fullName').attr("disabled", true);
}
// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function validateAddPatient()
{	
	var age = document.getElementById( 'age' );
	var orgunitcode = document.getElementById('orgunitcode');
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

	var params = '&checkedDuplicate='+checkedDuplicate 
				+'&fullName=' + getFieldValue( 'fullName' ) 
				+'&gender=' + getFieldValue( 'gender' ) 
				+'&dobType=' + getFieldValue( 'dobType' ) 
				+'&birthDate=' + getFieldValue( 'birthDate' ) 
				+'&ageType=' + getFieldValue( 'ageType' )
				+'&age=' + getFieldValue( 'age' ) 
				+'&genre=' + getFieldValue('gender') 
				+'&underAge=' + jQuery("#underAge").is(":checked")
				+'&representativeId=' + getFieldValue('representativeId')
				+'&relationshipTypeId=' + getFieldValue('relationshipTypeId')
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
    var params = 'id=' + getFieldValue( 'id' ) 
				+'&fullName=' + getFieldValue( 'fullName' )
				+'&gender=' + getFieldValue( 'gender' ) 
				+'&birthDate=' + getFieldValue( 'birthDate' ) 
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
    	removeDisabledIdentifier();
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
// get and build a param String of all the identifierType id and its value
// excluding inherited identifiers
function getIdParams()
{
	var params = "";
	jQuery("input.idfield").each(function(){
		if( jQuery(this).val() && !jQuery(this).is(":disabled") )
			params += "&" + jQuery(this).attr("name") +"="+ jQuery(this).val();
	});
	return params;
}

// remove value of all the disabled identifier fields
// an identifier field is disabled when its value is inherited from another person ( underAge is true ) 
// we don't save inherited identifiers. Only save the representative id.
function removeDisabledIdentifier()
{
	jQuery("input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}

//-----------------------------------------------------------------------------
//Move members
//-----------------------------------------------------------------------------
var selectedList;
var availableList;

function move( listId ) {
	
	var fromList = document.getElementById(listId);
	
	if ( fromList.selectedIndex == -1 ) {return;}
	
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
	//jQuery("#birthDate").val("").removeClass("error").rules("remove","required");
	//jQuery("#age").rules("add",{required:true});
	jQuery("#birthDate").val("");

}

function bdOnchange()
{
	//jQuery("#age").rules("remove","required");
	//jQuery("#age").val("");
	//jQuery("#birthDate").rules("add",{required:true});
	jQuery("#age").val("");
}


// check duplicate patient
function checkDuplicate()
{
	var params = 
				'&fullName=' + getFieldValue( 'fullName' ) +
				'&dobType=' + getFieldValue( 'dobType' ) +
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
    	showSuccessMessage(i18n_no_duplicate_found);
    }
    if ( type == 'input' )
    {
        showWarningMessage(message);
    }
    else if( type == 'duplicate' )
    {
    	showListPatientDuplicate(messageElement, false);
    }
}

/**
 * Show list patient duplicate  by jQuery thickbox plugin
 * @param rootElement : root element of the response xml
 * @param validate  :  is TRUE if this method is called from validation method  
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
        		sPatient += "<tr><td colspan='2'><strong>"+i18n_patient_identifiers+"</strong></td></tr>"
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
        		sPatient += "<tr><td colspan='2'><strong>"+i18n_patient_attributes+"</strong></td></tr>"
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
		else jQuery("#btnCreateNew","#hiddenModalContent").click(function(){window.parent.tb_remove();window.parent.checkedDuplicate = true;window.parent.validatePatient();});
		tb_show( message, "#TB_inline?height=500&width=500&inlineId=hiddenModalContent", null);
	}
}

function validatePatient()
{
	if( jQuery("#id").val() )
		validateUpdatePatient();
	else validateAddPatient();
}

//  ------------------------------
//  checked = TRUE  : show pop up
//  ------------------------------
//  checked = FALSE : find all identifier which is disabled, remove its value and enable it
//  This happen when user chose a representative, but now they dont want this patient to be under age anymore
//  TODO : if user created a new person, should we delete that person in this case ?
function toggleUnderAge(this_)
{
	if( jQuery(this_).is(":checked"))
	{
		tb_show(i18n_child_representative,"showAddRepresentative.action?TB_iframe=true&height=400&width=500",null);
	}else
	{
		jQuery("input.idfield").each(function(){
			if( jQuery(this).is(":disabled"))
			{
				jQuery(this).removeAttr("disabled").val("");
			}
		});
		jQuery("#representativeId").val("");
		jQuery("#relationshipTypeId").val("");
	}
}

function changePageSize( baseLink )
{
	var pageSize = jQuery("#sizeOfPage").val();
	window.location.href = baseLink +"pageSize=" + pageSize ;
}
function jumpToPage( baseLink )
{
	var pageSize = jQuery("#sizeOfPage").val();
	var currentPage = jQuery("#jumpToPage").val();
	window.location.href = baseLink +"pageSize=" + pageSize +"&currentPage=" +currentPage;
}

/**
 * Overwrite showDetails() of common.js
 * This method will show details div on a pop up instead of show the div in the main table's column.
 * So we will have more place for the main column of the table.
 * @return
 */

function showDetails()
{
	var detailArea = $("#detailsArea");
	var top = (f_clientHeight() / 2) - 200;	
	if ( top < 0 ) top = 0; 
    var left = screen.width - detailArea.width() - 100;
    detailArea.css({"left":left+"px","top":top+"px"});
    detailArea.show('fast');
    
}

/**
 *  Get document width, hieght, scroll positions
 *  Work with all browsers
 * @return
 */

function f_clientWidth() {
	return f_filterResults (
		window.innerWidth ? window.innerWidth : 0,
		document.documentElement ? document.documentElement.clientWidth : 0,
		document.body ? document.body.clientWidth : 0
	);
}
function f_clientHeight() {
	return f_filterResults (
		window.innerHeight ? window.innerHeight : 0,
		document.documentElement ? document.documentElement.clientHeight : 0,
		document.body ? document.body.clientHeight : 0
	);
}
function f_scrollLeft() {
	return f_filterResults (
		window.pageXOffset ? window.pageXOffset : 0,
		document.documentElement ? document.documentElement.scrollLeft : 0,
		document.body ? document.body.scrollLeft : 0
	);
}
function f_scrollTop() {
	return f_filterResults (
		window.pageYOffset ? window.pageYOffset : 0,
		document.documentElement ? document.documentElement.scrollTop : 0,
		document.body ? document.body.scrollTop : 0
	);
}
function f_filterResults(n_win, n_docel, n_body) {
	var n_result = n_win ? n_win : 0;
	if (n_docel && (!n_result || (n_result > n_docel)))
		n_result = n_docel;
	return n_body && (!n_result || (n_result > n_body)) ? n_body : n_result;
}

function removeEnrollment(){
	byId('programEnrollmentForm').action = "removeEnrollment.action";
	byId('programEnrollmentForm').submit();
}