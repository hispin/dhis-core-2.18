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
	
	var width = 800;
    var height = 500;
    var left = parseInt( ( screen.availWidth/2 ) - ( width/2 ) );
    var top = parseInt( ( screen.availHeight/2 ) - ( height/2 ) );
    var windowFeatures = 'width=' + width + ',height=' + height + ',scrollbars=yes, resizable=yes,left=' + left + ',top=' + top + 'screenX=' + left + ',screenY=' + top;
    
    window.open( url, '_blank_', windowFeatures);
}
