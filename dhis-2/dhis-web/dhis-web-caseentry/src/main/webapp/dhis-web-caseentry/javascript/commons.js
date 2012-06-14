
function dobTypeOnChange( container ){

	var type = jQuery('#' + container + ' [id=dobType]').val();
	if(type == 'V' || type == 'D'){
		jQuery('#' + container + ' [id=age]').rules("remove");
		jQuery('#' + container + ' [id=age]').css("display","none");
		
		jQuery('#' + container + ' [id=birthDate]').rules("add",{required:true});
		datePickerValid( container + ' [id=birthDate]' );
		jQuery('#' + container + ' [id=birthDate]').css("display","");
	}else if(type == 'A'){
		jQuery('#' + container + ' [id=age]').rules("add",{required:true, number: true});
		jQuery('#' + container + ' [id=age]').css("display","");
		
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
		$('#' + container+ ' [id=birthDate]').datepicker("destroy");
		jQuery('#' + container + ' [id=birthDate]').css("display","none");
	}else {
		jQuery('#' + container + ' [id=age]').rules("remove");
		jQuery('#' + container + ' [id=age]').css("display","");
		
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
		$('#' + container+ ' [id=birthDate]').datepicker("destroy");
		jQuery('#' + container + ' [id=birthDate]').css("display","none");
	}
}

// ----------------------------------------------------------------------------
// Search patients by name
// ----------------------------------------------------------------------------

function getPatientsByName( divname )
{	
	var fullName = jQuery('#' + divname + ' [id=fullName]').val().replace(/^\s+|\s+$/g,"");
	if( fullName.length > 0) 
	{
		contentDiv = 'resultSearchDiv';
		$('#resultSearchDiv' ).load("getPatientsByName.action",
			{
				fullName: fullName
			}).dialog({
				title: i18n_search_result,
				maximize: true, 
				closable: true,
				modal:true,
				overlay:{ background:'#000000', opacity: 0.8},
				width: 800,
				height: 400
		});
	}
	else
	{
		alert( i18n_no_patients_found );
	}
}

// -----------------------------------------------------------------------------
// Advanced search
// -----------------------------------------------------------------------------

function addAttributeOption()
{
	var rowId = 'advSearchBox' + jQuery('#advancedSearchTB select[name=searchingAttributeId]').length + 1;
	var contend  = '<td>' + getInnerHTML('searchingAttributeIdTD') + '</td>';
		contend += '<td>' + searchTextBox ;
		contend += '<input type="button" class="small-button" value="-" onclick="removeAttributeOption(' + "'" + rowId + "'" + ');"></td>';
		contend = '<tr id="' + rowId + '">' + contend + '</tr>';

	jQuery('#advancedSearchTB > tbody:last').append( contend );
}	

function removeAttributeOption( rowId )
{
	jQuery( '#' + rowId ).remove();
}	

//------------------------------------------------------------------------------
// Search patients by selected attribute
//------------------------------------------------------------------------------

function searchingAttributeOnChange( this_ )
{	
	var container = jQuery(this_).parent().parent().attr('id');
	var attributeId = jQuery('#' + container+ ' [id=searchingAttributeId]').val(); 
	var element = jQuery('#' + container + ' [id=searchText]');
	var valueType = jQuery('#' + container+ ' [id=searchingAttributeId] option:selected').attr('valueType');
	
	if( attributeId == '-1' )
	{
		element.replaceWith( getDateField( container ) );
		datePickerValid( container + ' [id=searchText]' );
		return;
	}
	
	$('#' + container+ ' [id=searchText]').datepicker("destroy");
	$('#' + container+ ' [id=dateOperator]').replaceWith("");
	if( attributeId == '0' )
	{
		element.replaceWith( programComboBox );
	}
	else if ( attributeId=='-2' )
	{
		element.replaceWith( genderSelector );
	}
	else if ( valueType=='YES/NO' )
	{
		element.replaceWith( trueFalseBox );
	}
	else
	{
		element.replaceWith( searchTextBox );
	}
}

function getDateField( container )
{
	var dateField = '<select id="dateOperator" name="dateOperator" ><option value=">"> > </option><option value="="> = </option><option value="<"> < </option></select>';
	dateField += '<input type="text" id="searchText" name="searchText" maxlength="30" style="width:18em" onkeyup="searchPatientsOnKeyUp( event );">';
	return dateField;
}
	
//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function searchPatientsOnKeyUp( event )
{
	var key = getKeyCode( event );
	
	if ( key==13 )// Enter
	{
		searchAdvancedPatients();
	}
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}

function searchAdvancedPatients()
{
	hideById( 'listPatientDiv' );
	var searchTextFields = jQuery('[name=searchText]');
	var flag = true;
	jQuery( searchTextFields ).each( function( i, item )
    {
		if( jQuery( item ).val() == '' )
		{
			showWarningMessage( i18n_specify_search_criteria );
			flag = false;
		}
	});
	
	if(!flag) return;
	
	contentDiv = 'listPatientDiv';
	jQuery( "#loaderDiv" ).show();
	searchPatient();
	
}

// ----------------------------------------------------------------------------
// Show patients
// ----------------------------------------------------------------------------

function isDeathOnChange()
{
	var isDeath = byId('isDead').checked;
	if(isDeath)
	{
		showById('deathDateTR');
	}
	else
	{
		hideById('deathDateTR');
	}
}

// ----------------------------------------------------------------
// Get Params form Div
// ----------------------------------------------------------------

function getParamsForDiv( patientDiv)
{
	var params = '';
	var dateOperator = '';
	jQuery("#" + patientDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( elementId =='dateOperator' )
			{
				dateOperator = jQuery(this).val();
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val()!= null && jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				if( dateOperator != '' )
				{
					value = dateOperator + "'" + value + "'";
					dateOperator = "";
				}
				params += elementId + "="+ value + "&";
			}
		});
		
	return params;
}

// -----------------------------------------------------------------------------
// View patient details
// -----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
    $('#detailsInfo').load("getPatientDetails.action", 
		{
			id:patientId
		}
		, function( ){
		}).dialog({
			title: i18n_patient_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 450,
			height: 300
		});
}

function showPatientHistory( patientId )
{
	$('#detailsInfo').load("getPatientHistory.action", 
		{
			patientId:patientId
		}
		, function( ){
			
		}).dialog({
			title: i18n_patient_details_and_history,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 520
		});
}

function exportPatientHistory( patientId, type )
{
	var url = "getPatientHistory.action?patientId=" + patientId + "&type=" + type;
	window.location.href = url;
}

var prefixStageId = 'ps_';
var COLOR_RED = "#fb4754";
var COLOR_GREEN = "#8ffe8f";
var COLOR_YELLOW = "#f9f95a";
var COLOR_LIGHTRED = "#fb6bfb";
var COLOR_LIGHT_RED = "#ff7676";
var COLOR_LIGHT_YELLOW = "#ffff99";
var COLOR_LIGHT_GREEN = "#ccffcc";
var COLOR_LIGHT_LIGHTRED = "#ff99ff";

function setEventColorStatus( elementId, status )
{
	status = eval(status);
	switch(status)
	{
		case 1:
			jQuery('#' + elementId ).css('border-color', COLOR_GREEN);
			jQuery('#' + elementId ).css('background-color', COLOR_LIGHT_GREEN);
			return;
		case 2:
		  jQuery('#' + elementId ).css('border-color', COLOR_LIGHTRED);
			jQuery('#' + elementId ).css('background-color', COLOR_LIGHT_LIGHTRED);
			return;
		case 3:
			jQuery('#' + elementId ).css('border-color', COLOR_YELLOW);
			jQuery('#' + elementId ).css('background-color', COLOR_LIGHT_YELLOW);
			return;
		case 4:
			jQuery('#' + elementId ).css('border-color', COLOR_RED);
			jQuery('#' + elementId ).css('background-color', COLOR_LIGHT_RED);
			return;
		default:
		  return;
	}
}