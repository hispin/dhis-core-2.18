
function validateRunValidation()
{
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( runValidationCompleted );
    
    request.send( 'validateRunValidation.action?startDate=' + getFieldValue( 'startDate' ) +
        '&endDate=' + getFieldValue( 'endDate' ) );
        
    return false;
}

function runValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	setWaitMessage( "Analysing data, please wait..." );
    	
        var url = 'runValidationAction.action?startDate=' + getFieldValue( 'startDate' ) +
        	'&endDate=' + getFieldValue( 'endDate' ) + '&validationRuleGroupId=' + $( "#validationRuleGroupId" ).val();
        	        
		$.get( url, function( data ) {
			$( "div#analysisInput" ).hide();
			$( "div#analysisResult" ).show();
			$( "div#analysisResult" ).html( data );
			pageInit();
		} );
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_validation_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

function viewValidationResultDetails( validationRuleId, sourceId, periodId )
{
	var url = "viewValidationResultDetails.action?validationRuleId=" + validationRuleId +
		"&sourceId=" + sourceId + "&periodId=" + periodId;
	
	var dialog = window.open( url, "_blank", "directories=no, \
    		 height=550, width=500, location=no, menubar=no, status=no, \
    		 toolbar=no, resizable=yes");
}
