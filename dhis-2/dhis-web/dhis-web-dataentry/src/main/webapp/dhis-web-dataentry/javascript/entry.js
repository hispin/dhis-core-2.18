
// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';

var FORMULA_PATTERN = /\[.+?\]/g;
var SEPARATOR = '.';

/**
 * Updates all indicator input fields with the calculated value based on the
 * values in the input entry fields in the form.
 */
function updateIndicators()
{
	var entryFieldValues = getEntryFieldValues();

	$( 'input[name="indicator"]' ).each( function( index ) {
		var indicatorId = $( this ).attr( 'indicatorid' );
		
		var formula = indicatorFormulas[ indicatorId ];
		
		var expression = generateExpression( formula );
		
		var value = eval( expression );
		
		value = isNaN( value ) ? '-' : Math.round( value );
		
		$( this ).attr( 'value', value );
	} );
}

/**
 * Returns an associative array with an entry for each entry input field in the
 * form where the key is the input field id and the value is the input field value.
 */
function getEntryFieldValues()
{
	var entryFieldValues = new Array();
	
	$( 'input[name="entryfield"]' ).each( function( index ) {
		entryFieldValues[ $( this ).attr( 'id' ) ] = $( this ).attr( 'value' );
	} );
	
	return entryFieldValues;
}

/**
 * Parses the expression and substitues the operand identifiers with the value of
 * the corresponding input entry field.
 */
function generateExpression( expression )
{
	var matcher = expression.match( FORMULA_PATTERN );
	
	for ( k in matcher )
	{
		var match = matcher[k];
		var operand = match.replace( /[\[\]]/g, '' ); // Remove brackets from expression to simplify extraction of identifiers
		
		var dataElementId = operand.substring( 0, operand.indexOf( SEPARATOR ) );
		var categoryOptionComboId = operand.substring( operand.indexOf( SEPARATOR ) + 1, operand.length );
		
		var entryFieldId = 'value[' + dataElementId + '].value:value[' + categoryOptionComboId + '].value';		
		var entryField = document.getElementById( entryFieldId );
		
		var value = entryField && entryField.value ? entryField.value : '0';
		
		expression = expression.replace( match, value ); // TODO signed numbers
	}
	
	return expression;
}

/**
/* Used by default and section forms.
*/
function saveVal( dataElementId, optionComboId )
{
	var dataElementName = document.getElementById( 'value[' + dataElementId + '].name' ).innerHTML;
	
	saveValueInternal( dataElementId, optionComboId, dataElementName, null );
}

/**
/* Used by custom forms.
*/
function saveValue( dataElementId, optionComboId, dataElementName )
{
	saveValueInternal( dataElementId, optionComboId, dataElementName );
	updateIndicators();
}

function saveValueInternal( dataElementId, optionComboId, dataElementName )
{
    var field = document.getElementById( 'value[' + dataElementId + '].value' + ':' +  'value[' + optionComboId + '].value');
    var type = document.getElementById( 'value[' + dataElementId + '].type' ).innerHTML;
	var organisationUnitId = getFieldValue( 'organisationUnitId' );
    
    field.style.backgroundColor = COLOR_YELLOW;
    
    if ( field.value && field.value != '' )
    {
        if ( type == 'int' || type == 'number' || type == 'positiveNumber' || type == 'negativeNumber' )
        {
			if ( isValidZeroNumber( field.value ) )
            {
                // If value is 0 and zero is not significant for data element, then skip value				
				if ( significantZeros.indexOf( dataElementId ) == -1 )
				{
					field.style.backgroundColor = COLOR_GREEN;
					field.value = '';
					field.select();
					field.focus();
					return false;
				}
				
				field.value = (field.value.indexOf(".") == -1) ? "0" : "0.0";
            }
			else if ( type == 'int' && ( !isInt( field.value ) || ( field.value.length > 255 ) ) )
            {
            	window.alert( i18n_value_must_integer + '\n\n' + dataElementName );
                return alertField( field );
            }  
            else if ( type == 'number' && ( !isRealNumber( field.value ) || ( field.value.length > 255 ) ) )
            {
                window.alert( i18n_value_must_number + '\n\n' + dataElementName );
                return alertField( field );
            } 
			else if ( type == 'positiveNumber' && ( !isPositiveInt( field.value ) || ( field.value.length > 255 ) ) )
            {
                window.alert( i18n_value_must_positive_integer + '\n\n' + dataElementName );
                return alertField( field );
            } 
			else if ( type == 'negativeNumber' && ( !isNegativeInt( field.value ) || ( field.value.length > 255 ) ) )
            {
                window.alert( i18n_value_must_negative_integer + '\n\n' + dataElementName );
                return alertField( field );
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
        var url = 'saveValue.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' +
                dataElementId + '&optionComboId=' + optionComboId + '&value=' + value;
                
        $.ajax( { url: url, dataType: 'json', success: handleResponse, error: handleError } );
    };
    
    function handleResponse( json )
    {
        var code = json.code;
        
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
    
    function handleError( jqXHR, textStatus, errorThrown )
    {
        markValue( COLOR_RED );
        window.alert( i18n_saving_value_failed_status_code + '\n\n' + textStatus );
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
