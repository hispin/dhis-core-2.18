
function verifiedOnchange( container ){

	var checked = byId( 'verified' ).checked;
	if( checked )
	{
		disable( 'age' );
	}
	else
	{
		enable( 'age' );
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
		contend += '<input type="button" value="-" onclick="removeAttributeOption(' + "'" + rowId + "'" + ');"></td>';
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
	var element = jQuery('#' + container+ ' [id=searchText]');
	var valueType = jQuery('#' + container+ ' [id=searchingAttributeId] option:selected').attr('valueType');
	
	if( attributeId == '-1' )
	{
		element.replaceWith( getDateField( container ) );
		datePickerValid( 'searchDateField-' + container + ' [id=searchText]' );
	}
	else if( attributeId == '0' )
	{
		element.replaceWith( programComboBox );
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
	var dateField = '<div id="searchDateField-' + container + '" > <input type="text" id="searchText" name="searchText" maxlength="30" style="width:18em" onkeyup="searchPatientsOnKeyUp( event );"></div>';
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
		searchAdvancedPatients()();
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
	jQuery("#" + patientDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
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
