isAjax = true;

function organisationUnitSelected( orgUnits )
{
    showLoader();
	setInnerHTML( 'contentDiv','' );
	jQuery.postJSON( "getPrograms.action",
	{
	}, 
	function( json ) 
	{    
		setFieldValue( 'orgunitname', json.organisationUnit );
		
		clearListById('programId');
		if( json.programs.length == 0)
		{
			disable('programId');
			disable('startDate');
			disable('endDate');
			disable('endDate');
			disable('generateBtn');
		}
		else
		{
			addOptionById( 'programId', "", i18n_select );
			
			for ( var i in json.programs ) 
			{
				addOptionById( 'programId', json.programs[i].id, json.programs[i].name );
			} 
			enable('programId');
			enable('startDate');
			enable('endDate');
			enable('endDate');
			enable('generateBtn');
		}
		
		hideLoader();
	});
}

selection.setListenerFunction( organisationUnitSelected );

function validateAndGenerateReport()
{
	var url = 'validateReportParameters.action?' +
			'startDate=' + getFieldValue( 'startDate' ) +
			'&endDate=' + getFieldValue( 'endDate' ) ;
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( reportValidationCompleted );    
	request.send( url );
	
	return false;
}

function reportValidationCompleted( messageElement )
{   
    var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	hideById( 'contentDiv' );
	
	if ( type == 'success' )
	{
		loadGeneratedReport();
	}
	else if ( type == 'error' )
	{
		window.alert( i18n_report_generation_failed + ':' + '\n' + message );
	}
	else if ( type == 'input' )
	{
		setMessage( message );
	}
}

function loadGeneratedReport()
{
	lockScreen();

	jQuery( "#contentDiv" ).load( "generateReport.action",
	{
		programId: getFieldValue( 'programId' ),
		startDate: getFieldValue( 'startDate' ),
		endDate: getFieldValue( 'endDate' )
	}, function() { unLockScreen();hideById( 'message' );showById( 'contentDiv' );});
}

function viewRecords( programStageInstanceId ) 
{
	var url = 'viewRecords.action?id=' + programStageInstanceId;
	
	var width = 800
    var height = 500;
    var left = parseInt( ( screen.availWidth/2 ) - ( width/2 ) );
    var top = parseInt( ( screen.availHeight/2 ) - ( height/2 ) );
    var windowFeatures = 'width=' + width + ',height=' + height + ',scrollbars=yes, resizable=yes,left=' + left + ',top=' + top + 'screenX=' + left + ',screenY=' + top;
    
    window.open( url, '_blank_', windowFeatures);
}

//-----------------------------------------------------------------------------
//View details
//-----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
 var request = new Request();
 request.setResponseTypeXML( 'patient' );
 request.setCallbackSuccess( patientReceived );
 request.send( 'getPatientDetails.action?id=' + patientId );
}

function patientReceived( patientElement )
{   
 var identifiers = patientElement.getElementsByTagName( "identifier" );   
 
 var identifierText = '';
	
	for ( var i = 0; i < identifiers.length; i++ )
	{		
		identifierText = identifierText + identifiers[ i ].getElementsByTagName( "identifierText" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setInnerHTML( 'identifierField', identifierText );
	
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
	//if ( top < 0 ) top = 100; 
    top = 100;
	var left = screen.width - detailArea.width() - 100;
    detailArea.css({"left":left+"px","top":top+"px"});
    detailArea.show('fast');
    
}


function hideDetails()
{
	var detailArea = $("#detailsArea");
	detailArea.hide();
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

