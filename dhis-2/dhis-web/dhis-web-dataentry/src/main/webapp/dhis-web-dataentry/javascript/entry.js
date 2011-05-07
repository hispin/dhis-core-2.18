
// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';

function saveVal( dataElementId, optionComboId )
{
	var dataElementName = document.getElementById( 'value[' + dataElementId + '].name' ).innerHTML;
	
	saveValue( dataElementId, optionComboId, dataElementName, null );
}

function saveValue( dataElementId, optionComboId, dataElementName )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value' + ':' +  'value[' + optionComboId + '].value');
    var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;   
	var organisationUnitId = getFieldValue( 'organisationUnitId' );
    
    field.style.backgroundColor = COLOR_YELLOW;
    
    if ( field.value && field.value != '' )
    {
        if ( type == 'int' || type == 'number' || type == 'positiveNumber' || type == 'negativeNumber' )
        {
            if ( type == 'int' && !isInt( field.value ) )
            {
            	window.alert( i18n_value_must_integer + '\n\n' + dataElementName );
                return alertField( field );
            }  
            else if ( type == 'number' && !isNumber( field.value ) )
            {
                window.alert( i18n_value_must_number + '\n\n' + dataElementName );
                return alertField( field );
            } 
			else if ( type == 'positiveNumber' && !isPositiveNumber( field.value ) )
            {
                window.alert( i18n_value_must_positive_number + '\n\n' + dataElementName );
                return alertField( field );
            } 
			else if ( type == 'negativeNumber' && !isNegativeNumber( field.value ) )
            {
                window.alert( i18n_value_must_negative_number + '\n\n' + dataElementName );
                return alertField( field );
            }
            else if ( isZeroNumber( field.value ) && significantZeros.indexOf( dataElementId ) == -1 )
            {
                // If value is 0 and zero is not significant for data element, then skip value
                
                field.style.backgroundColor = COLOR_GREEN;
                return;
            }
            else
            {
                var minString = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].min' ).innerHTML;
                var maxString = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].max' ).innerHTML;

                if ( minString.length != 0 && maxString.length != 0 )
                {
                    var value = new Number( field.value );
                    var min = new Number( minString );
                    var max = new Number( maxString );

                    if ( value < min )
                    {
                        var valueSaver = new ValueSaver( dataElementId, optionComboId, organisationUnitId, field.value, COLOR_RED );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_less + '\n\n' + dataElementName );
                        
                        return;
                    }

                    if ( value > max )
                    {
                        var valueSaver = new ValueSaver( dataElementId, optionComboId, organisationUnitId, field.value, COLOR_RED );
                        valueSaver.save();
                        
                        window.alert( i18n_value_of_data_element_greater + '\n\n' + dataElementName);
                        
                        return;
                    }
                }
            }       
        }
    }

    var valueSaver = new ValueSaver( dataElementId, optionComboId, organisationUnitId, field.value, COLOR_GREEN, '' );
    valueSaver.save();
}

function saveBoolean( dataElementId, optionComboId, selectedOption  )
{	
	var select = selectedOption.options[selectedOption.selectedIndex].value 
	var organisationUnitId = getFieldValue( 'organisationUnitId' );
	
   	selectedOption.style.backgroundColor = COLOR_YELLOW;
    
    var valueSaver = new ValueSaver( dataElementId, optionComboId, organisationUnitId, select, COLOR_GREEN, selectedOption );
    valueSaver.save();
}

function saveDate( dataElementId, dataElementName )
{
	var field = document.getElementById( 'value[' + dataElementId + '].date' );
    var type = document.getElementById( 'value[' + dataElementId + '].valueType' ).innerHTML;
	var organisationUnitId = getFieldValue( 'organisationUnitId' );
    
    field.style.backgroundColor = COLOR_YELLOW;
    
    var valueSaver = new ValueSaver( dataElementId, '', organisationUnitId, field.value, COLOR_GREEN, '' );
    valueSaver.save();
}

function saveComment( dataElementId, optionComboId, commentValue )
{
    var field = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );                
    var select = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );
	var organisationUnitId = getFieldValue( 'organisationUnitId' );
    
    field.style.backgroundColor = COLOR_YELLOW;
    select.style.backgroundColor = COLOR_YELLOW;
    
    var commentSaver = new CommentSaver( dataElementId, optionComboId, organisationUnitId, commentValue );
    commentSaver.save();
}

/**
 * Supportive method.
 */
function alertField( field )
{
	field.style.backgroundColor = COLOR_YELLOW;
    field.select();
    field.focus();
    return false;
}

// -----------------------------------------------------------------------------
// Saver objects
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId_, optionComboId_, organisationUnitId_, value_, resultColor_, selectedOption_ )
{
    var dataElementId = dataElementId_;
    var optionComboId = optionComboId_;
    var value = value_;
    var resultColor = resultColor_;
    var selectedOption = selectedOption_;
    var organisationUnitId = organisationUnitId_;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );        
        request.send( 'saveValue.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' +
                dataElementId + '&optionComboId=' + optionComboId + '&value=' + value );
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
            markValue( COLOR_RED );
            window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markValue( COLOR_RED );
        window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
    }   
    
    function markValue( color )
    {
        var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerText;
        var element;
        
        if ( type == 'bool' )
        {
            element = document.getElementById( 'value[' + dataElementId + '].boolean' );
        }
        else if ( type == 'date' )
        {
        	element = document.getElementById( 'value[' + dataElementId + '].date' );
        }
        else if ( selectedOption )
        {
        	element = selectedOption;    
        }
        else
        {            
            element = document.getElementById( 'value[' + dataElementId + '].value' + ':' +  'value[' + optionComboId + '].value');            
        }

        element.style.backgroundColor = color;
    }
}

// -----------------------------------------------------------------------------
// Section
// -----------------------------------------------------------------------------

function openCloseSection( sectionId )
{
	var divSection = document.getElementById( sectionId );
	var sectionLabel = document.getElementById( sectionId + ":name" );	
	
	if( divSection.style.display == 'none' )
	{			
		divSection.style.display = ('block');
		sectionLabel.style.textAlign = 'center';
	}
	else
	{			
		divSection.style.display = ('none');
		sectionLabel.style.textAlign = 'left';
	}
}
