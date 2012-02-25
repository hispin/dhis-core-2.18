
function dobTypeOnChange( container ){

	var type = jQuery('#' + container + ' [id=dobType]').val();
	
	if(type == 'V' || type == 'D'){
		jQuery('#' + container + ' [id=age]').rules("remove","required");
		jQuery('#' + container + ' [id=birthDate]').rules("add",{required:true});
		
		showById(container + ' [id=birthdaySpan]');
		hideById(container + ' [id=ageSpan]');
	}else if(type == 'A'){
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
		jQuery('#' + container + ' [id=age]').rules("add",{required:true});
		
		hideById(container + ' [id=birthdaySpan]');
		showById(container + ' [id=ageSpan]');
	}else {
		hideById(container + ' [id=birthdaySpan]');
		hideById(container + ' [id=ageSpan]');
		jQuery('#' + container + ' [id=age]').rules("remove","required");
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
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
				params += elementId + "="+ jQuery(this).val() + "&";
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
			height: 400
		});;
}

// -----------------------------------------------------------------------------
// Show Details
// -----------------------------------------------------------------------------

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
