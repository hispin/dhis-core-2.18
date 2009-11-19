
function organisationUnitSelected( orgUnits )
{
    window.location.href = 'reportSelect.action';
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
	
	if ( type == 'success' )
	{
		window.location.href='generateReport.action?' +			
			'startDate=' + getFieldValue( 'startDate' ) +
			'&endDate=' + getFieldValue( 'endDate' ) ;
	}
	else if ( type == 'error' )
	{
		window.alert( i18n_report_generation_failed + ':' + '\n' + message );
	}
	else if ( type == 'input' )
	{
		document.getElementById( 'message' ).innerHTML = message;
		document.getElementById( 'message' ).style.display = 'block';
	}
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
