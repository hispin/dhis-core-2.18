
function organisationUnitSelected( orgUnits )
{
    window.location.href = 'dataEntrySelect.action';
}

selection.setListenerFunction( organisationUnitSelected );

//-----------------------------------------------------------------------------
//Search Patient
//-----------------------------------------------------------------------------

function validateSearch()
{	
	
	var url = 'validateSearch.action?' +
			'searchText=' + getFieldValue( 'searchText' );	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( searchValidationCompleted );    
	request.send( url );        

	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if ( type == 'success' )
	{
		var form = document.getElementById( 'searchForm' );        
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

//-----------------------------------------------------------------------------
//View details
//-----------------------------------------------------------------------------

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

//------------------------------------------------------------------------------
// Save Execution Date
//------------------------------------------------------------------------------

function saveExecutionDate( programStageInstanceId, programStageInstanceName )
{
	var field = document.getElementById( 'executionDate' );
	
	field.style.backgroundColor = '#ffffcc';
	
	var executionDateSaver = new ExecutionDateSaver( programStageInstanceId, field.value, '#ccffcc' );
	executionDateSaver.save();
  
}

//-----------------------------------------------------------------------------
// Date Saver objects
//-----------------------------------------------------------------------------

function ExecutionDateSaver( programStageInstanceId_, executionDate_, resultColor_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ffcc00';
	
	var programStageInstanceId = programStageInstanceId_;	
	var executionDate = executionDate_;
	var resultColor = resultColor_;	

	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );
		request.send( 'saveExecutionDate.action?programStageInstanceId=' + programStageInstanceId + '&executionDate=' + executionDate );
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
			window.alert( i18n_invalid_date );
		}
	}

	function handleHttpError( errorCode )
	{
		markValue( ERROR );
		window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
	}   

	function markValue( color )
	{       
   
		var element = document.getElementById( 'executionDate' );	
           
		element.style.backgroundColor = color;
	}
}

//------------------------------------------------------------------------------
//Save Execution Date
//------------------------------------------------------------------------------

function saveDateValue( dataElementId, dataElementName )
{
	var field = document.getElementById( 'value[' + dataElementId + '].date' );    
    var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
 
	var dateSaver = new DateSaver( dataElementId, field.value, providedByAnotherFacility, '#ccffcc' );
	dateSaver.save();
	
}

//-----------------------------------------------------------------------------
//Date Saver objects
//-----------------------------------------------------------------------------

function DateSaver( dataElementId_, value_, providedByAnotherFacility_, resultColor_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ffcc00';
	
	var dataElementId = dataElementId_;	
	var value = value_;
	var providedByAnotherFacility = providedByAnotherFacility_;
	var resultColor = resultColor_;		

	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );		
		request.send( 'saveDateValue.action?dataElementId=' + dataElementId + '&value=' + value + '&providedByAnotherFacility=' + providedByAnotherFacility );
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
			window.alert( i18n_invalid_date );
		}
	}

	function handleHttpError( errorCode )
	{
		markValue( ERROR );
		window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
	}   

	function markValue( color )
	{
		var element = document.getElementById( 'value[' + dataElementId + '].date' );
        
		element.style.backgroundColor = color;
	}
}

//------------------------------------------------------------------------------
//Save providing facility
//------------------------------------------------------------------------------

function updateProvidingFacility( dataElementId, checkedBox )
{
	checkedBox.style.backgroundColor = '#ffffcc';	
	var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
	
    var checkBoxSaver = new CheckBoxSaver( dataElementId, providedByAnotherFacility, '#ccffcc' );
    checkBoxSaver.save();
    
}

//-----------------------------------------------------------------------------
//Saver objects - checkbox
//-----------------------------------------------------------------------------

function CheckBoxSaver( dataElementId_, providedByAnotherFacility_, resultColor_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ccccff';
	
	var dataElementId = dataElementId_;	
	var providedByAnotherFacility = providedByAnotherFacility_;
	var resultColor = resultColor_;	

	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponseCheckBox );
		request.setCallbackError( handleHttpErrorCheckBox );
		request.setResponseTypeXML( 'status' );
		request.send( 'saveProvidingFacility.action?dataElementId=' + dataElementId + '&providedByAnotherFacility=' + providedByAnotherFacility );
	};

	function handleResponseCheckBox( rootElement )
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

	function handleHttpErrorCheckBox( errorCode )
	{
		markValue( ERROR );
		window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
	}   

	function markValue( color )
	{	
		var element = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' );		
		element.style.backgroundColor = color; //need to find another option as it is difficult to set background color for checkbox		
	}
}

//------------------------------------------------------------------------------
//Save
//------------------------------------------------------------------------------

function saveValue( dataElementId, dataElementName )
{
	var field = document.getElementById( 'value[' + dataElementId + '].value' );
    var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;    
    var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
    
    field.style.backgroundColor = '#ffffcc';
    
    if( field.value != '' )
    {
    	if( type == 'int' )
    	{
    		if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + dataElementName );

                field.select();
                field.focus();

                return;
            }
    	}  	
    	
    }
    
    var valueSaver = new ValueSaver( dataElementId, field.value, providedByAnotherFacility, '#ccffcc', '' );
    valueSaver.save();
    
}

function saveChoice( dataElementId, selectedOption )
{
	selectedOption.style.backgroundColor = '#ffffcc';
	
	var providedByAnotherFacility = document.getElementById( 'value[' + dataElementId + '].providedByAnotherFacility' ).checked;
 
	var valueSaver = new ValueSaver( dataElementId, selectedOption.options[selectedOption.selectedIndex].value, providedByAnotherFacility, '#ccffcc', selectedOption );
	valueSaver.save();
}


//-----------------------------------------------------------------------------
//Saver objects
//-----------------------------------------------------------------------------

function ValueSaver( dataElementId_, value_, providedByAnotherFacility_, resultColor_, selectedOption_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ccccff';
	
	var dataElementId = dataElementId_;	
	var value = value_;
	var providedByAnotherFacility = providedByAnotherFacility_;
	var resultColor = resultColor_;
	var selectedOption = selectedOption_;
 
	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );
		request.send( 'saveValue.action?dataElementId=' + dataElementId + '&value=' + value + '&providedByAnotherFacility=' + providedByAnotherFacility );
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
		var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;       
     
		var element;
     
		if ( type == 'bool' )
		{
			element = document.getElementById( 'value[' + dataElementId + '].boolean' );
		}		
		else if( type == 'date' )
		{
			element = document.getElementById( 'value[' + dataElementId + '].date' );
		}		
		else if( selectedOption )
		{
			element = selectedOption;    
		}
		else
		{           
			element = document.getElementById( 'value[' + dataElementId + '].value' );                      
		}
             
		element.style.backgroundColor = color;
	}
}


function isInt( value )
{
	var number = new Number( value );
 
	if ( isNaN( number ) )
	{
		return false;
	}
 
	return true;
}

/**
 * Display data element name in selection display when a value field recieves
 * focus.
 * XXX May want to move this to a separate function, called by valueFocus.
 * @param e focus event
 * @author Hans S. Tommerholt
 */
function valueFocus(e) 
{
	//Retrieve the data element id from the id of the field
	var str = e.target.id;
	
	var match = /.*\[(.*)\]/.exec( str ); //value[-dataElementId-]
	
	if ( ! match )
	{				
		return;
	}

	var deId = match[1];	
	
	//Get the data element name
	var nameContainer = document.getElementById('value[' + deId + '].name');	
	
	if ( ! nameContainer )
	{		
		return;
	}

	var name = '';
	
	
	var as = nameContainer.getElementsByTagName('a');

	if ( as.length > 0 )	//Admin rights: Name is in a link
	{
		name = as[0].firstChild.nodeValue;
	} 
	else 
	{
		name = nameContainer.firstChild.nodeValue;
	}
	
}

function keyPress( event, field )
{
    var key = 0;
    if ( event.charCode )
    {
    	key = event.charCode; /* Safari2 (Mac) (and probably Konqueror on Linux, untested) */
    }
    else
    {
		if ( event.keyCode )
		{
			key = event.keyCode; /* Firefox1.5 (Mac/Win), Opera9 (Mac/Win), IE6, IE7Beta2, Netscape7.2 (Mac) */
		}
		else
		{
			if ( event.which )
			{
				key = event.which; /* Older Netscape? (No browsers triggered yet) */
			}
	    }
	}
    
    if ( key == 13 ) /* CR */
    {
		nextField = getNextEntryField( field );
        if ( nextField )
        {
            nextField.focus(); /* Does not seem to actually work in Safari, unless you also have an Alert in between */
        }
        return true;
    }
    
    /* Illegal characters can be removed with a new if-block and return false */
    return true;
}

function getNextEntryField( field )
{
    var inputs = document.getElementsByName( "entryfield" );
    
    // Simple bubble sort
    for ( i = 0; i < inputs.length - 1; ++i )
    {
        for ( j = i + 1; j < inputs.length; ++j )
        {
            if ( inputs[i].tabIndex > inputs[j].tabIndex )
            {
                tmp = inputs[i];
                inputs[i] = inputs[j];
                inputs[j] = tmp;
            }
        }
    }
    
    i = 0;
    for ( ; i < inputs.length; ++i )
    {
        if ( inputs[i] == field )
        {
            break;
        }
    }
    
    if ( i == inputs.length - 1 )
    {
    	// No more fields after this:
    	return false;
    	// First field:
        //return inputs[0];
    }
    else
    {
        return inputs[i + 1];
    }
}

