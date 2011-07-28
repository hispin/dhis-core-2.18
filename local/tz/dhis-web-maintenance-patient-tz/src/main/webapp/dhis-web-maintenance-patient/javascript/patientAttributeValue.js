//-----------------------------------------------------------------------------
//Save
//-----------------------------------------------------------------------------

function saveAttributeValue( patientId, patientAttributeId, patientAttributeName )
{
	var field = document.getElementById( 'value[' + patientAttributeId + '].value' );
    var type = document.getElementById( 'value[' + patientAttributeId + '].valueType' ).innerHTML;
    
    field.style.backgroundColor = '#ffffcc';
    
    if( field.value != '' )
    {
    	if( type == 'int' )
    	{
    		if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + patientAttributeName );

                field.select();
                field.focus();

                return;
            }
    	}  	
    	
    }
    
    var valueSaver = new ValueSaver( patientId, patientAttributeId, field.value, '#ccffcc', '' );
    valueSaver.save();
    
}

function saveDate( patientId, patientAttributeId, selectedOption )
{
	var field = document.getElementById( 'value[' + patientAttributeId + '].date' );
 
	var dateSaver = new DateSaver( patientId, patientAttributeId, field.value, '#ccffcc', '' );
	dateSaver.save();
}

function saveBoolean( patientId, patientAttributeId, selectedOption )
{
	selectedOption.style.backgroundColor = '#ffffcc';
 
	var valueSaver = new ValueSaver( patientId, patientAttributeId, selectedOption.options[selectedOption.selectedIndex].value, '#ccffcc', selectedOption );
	valueSaver.save();
}
function saveCombo( patientId, patientAttributeId, selectedOption )
{
	selectedOption.style.backgroundColor = '#ffffcc';
	var valueSaver = new ValueSaver( patientId, patientAttributeId, selectedOption.options[selectedOption.selectedIndex].value, '#ccffcc', selectedOption );
	valueSaver.save();
}

// ------------------------------------------------------------------------------
// Saver objects
// ------------------------------------------------------------------------------

function DateSaver( patientId_, patientAttributeId_, value_, resultColor_, selectedOption_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ffcc00';
	
	var patientId = patientId_;
	var patientAttributeId = patientAttributeId_;	
	var value = value_;
	var resultColor = resultColor_;
	var selectedOption = selectedOption_;
 
	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );
		request.send( 'savePatientAttributeDateValue.action?patientId=' + patientId + '&patientAttributeId=' + patientAttributeId + '&value=' + value );		
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
     
		var element = document.getElementById( 'value[' + patientAttributeId + '].date' );	
             
		element.style.backgroundColor = color;
	}
}


function ValueSaver( patientId_, patientAttributeId_, value_, resultColor_, selectedOption_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ffcc00';
	
	var patientId = patientId_;
	var patientAttributeId = patientAttributeId_;	
	var value = value_;
	var resultColor = resultColor_;
	var selectedOption = selectedOption_;
 
	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );
		request.send( 'savePatientAttributeValue.action?patientId=' + patientId + '&patientAttributeId=' + patientAttributeId + '&value=' + value );		
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
		var type = document.getElementById( 'value[' + patientAttributeId + '].valueType' ).innerHTML;       
     
		var element;
     
		if ( type == 'YES/NO' )
		{
			element = document.getElementById( 'value[' + patientAttributeId + '].boolean' );
		}	
		else if ( type == "COMBO")
		{           
			element = document.getElementById( 'value[' + patientAttributeId + '].combo' );                      
		}
		else
		{           
			element = document.getElementById( 'value[' + patientAttributeId + '].value' );                      
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
 * Display patient attribute name in selection display when a value field
 * recieves focus. XXX May want to move this to a separate function, called by
 * valueFocus.
 * 
 * @param e
 *            focus event
 * @author Hans S. Tommerholt
 */
function valueFocus(e) 
{
	// Retrieve the data element id from the id of the field
	var str = e.target.id;
	
	var match = /.*\[(.*)\]/.exec( str ); 
	
	if ( ! match )
	{				
		return;
	}

	var attrId = match[1];	
	
	// Get the data element name
	var nameContainer = document.getElementById('value[' + attrId + '].name');	
	
	if ( ! nameContainer )
	{		
		return;
	}

	var name = '';
	
	
	var as = nameContainer.getElementsByTagName('a');

	if ( as.length > 0 )	// Admin rights: Name is in a link
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
    	key = event.charCode; /*
								 * Safari2 (Mac) (and probably Konqueror on
								 * Linux, untested)
								 */
    }
    else
    {
		if ( event.keyCode )
		{
			key = event.keyCode; /*
									 * Firefox1.5 (Mac/Win), Opera9 (Mac/Win),
									 * IE6, IE7Beta2, Netscape7.2 (Mac)
									 */
		}
		else
		{
			if ( event.which )
			{
				key = event.which; /*
									 * Older Netscape? (No browsers triggered
									 * yet)
									 */
			}
	    }
	}
    
    if ( key == 13 ) /* CR */
    {
		nextField = getNextEntryField( field );
        if ( nextField )
        {
            nextField.focus(); /*
								 * Does not seem to actually work in Safari,
								 * unless you also have an Alert in between
								 */
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
        // return inputs[0];
    }
    else
    {
        return inputs[i + 1];
    }
}

// Validate
// Input datetime
function isDate( value ) {
	var re = /^(\d{2,4})(\/|-)(\d{1,2})(\/|-)(\d{1,2})$/;
	return (re.test(value)) ? true : false;
}

function validateAttributeValue(  patientAttributeId, patientAttributeName, type)
{ 
	var field = byId( patientAttributeId );
    
    if( field.value != '' )
    {
    	if( type == 'int' )
    	{
    		if ( !isInt( field.value ))
            {
                field.style.backgroundColor = '#ffcc00';

                window.alert( i18n_value_must_integer + '\n\n' + patientAttributeName );
				field.value = '';
                
				field.select();
                field.focus();

                return;
            }
    	} else if(type == 'date'){
			if(!isDate(field.value ))
			{
				field.style.backgroundColor = '#ffcc00';
			
				window.alert( i18n_value_must_date + '\n\n' + patientAttributeName );
				field.value = '';
                
				field.select();
                field.focus();

                return;
			}
		}
		field.style.backgroundColor = '';
    }
}


