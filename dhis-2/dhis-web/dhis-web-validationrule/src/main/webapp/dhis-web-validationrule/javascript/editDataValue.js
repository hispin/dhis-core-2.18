
var currentFollowupId = null;

function editValue( valueId )
{
	var field = document.getElementById( 'value[' + valueId + '].value' );
	
	var dataElementId = document.getElementById( 'value[' + valueId + '].dataElement' ).value;
    var categoryOptionComboId = document.getElementById( 'value[' + valueId + '].categoryOptionCombo' ).value;
	var periodId = document.getElementById( 'value[' + valueId + '].period' ).value;
	var sourceId = document.getElementById( 'value[' + valueId + '].source' ).value;
	
	if ( field.value != '' )
	{
		if ( !isInt( field.value ) )
		{
			alert( i18n_value_must_be_a_number );
			
			field.select();
	        field.focus(); 
	        
			return;   
		}
		else
		{
			var minString = document.getElementById('value[' + valueId + '].min').value;
			var maxString = document.getElementById('value[' + valueId + '].max').value;
			
			var min = new Number( minString );
			var max = new Number( maxString );
			var value = new Number( field.value );
			
			if ( !( min == 0 && max == 0 ) ) // No min max found
			{
				if ( value < min )
				{
					var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, valueId, '#ffcccc' );
					valueSaver.save();
					
					alert( i18n_value_is_lower_than_min_value );
					return;
				}
				
				if ( value > max )
				{
					var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, valueId, '#ffcccc' );
					valueSaver.save();
					
					alert( i18n_value_is_higher_than_max_value );
					return;
				}
			}
		}
	}
	
    var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, valueId, '#ccffcc', '');
    valueSaver.save();

}

function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {
        return false;
    }
    
    return true;
}

function markValueForFollowup( valueId )
{	
	currentFollowupId = valueId;
	
    var dataElementId = document.getElementById( 'value[' + valueId + '].dataElement' ).value;
    var categoryOptionComboId = document.getElementById( 'value[' + valueId + '].categoryOptionCombo' ).value;
    var periodId = document.getElementById( 'value[' + valueId + '].period' ).value;
    var sourceId = document.getElementById( 'value[' + valueId + '].source' ).value;
    
    var url = 'markForFollowup.action?dataElementId=' + dataElementId + '&periodId=' + periodId +
        '&sourceId=' + sourceId + '&categoryOptionComboId=' + categoryOptionComboId;
    
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( markValueForFollowupReceived );    
    request.send( url );
}

function markValueForFollowupReceived( messageElement )
{   
    var message = messageElement.firstChild.nodeValue;
    var image = document.getElementById( 'value[' + currentFollowupId + '].followup' );
    
    if ( message == "marked" )
    {
        image.src = "../images/marked.png";
        image.alt = i18n_unmark_value_for_followup;
    }
    else if ( message = "unmarked" )
    {
        image.src = "../images/unmarked.png";
        image.alt = i18n_mark_value_for_followup;   
    }
}

// -----------------------------------------------------------------------------
// Saver object (modified version of dataentry/javascript/general.js)
// -----------------------------------------------------------------------------

function ValueSaver( dataElementId_, periodId_, sourceId_, categoryOptionComboId_, value_, valueId_, resultColor_, selectedOption_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var periodId = periodId_;
    var sourceId = sourceId_;
    var categoryOptionComboId = categoryOptionComboId_;
    var value = value_;
    var valueId = valueId_;
    var resultColor = resultColor_;
    var selecteOption = selectedOption_;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'editDataValue.action?'
        		+ 'dataElementId=' + dataElementId
        		+ '&periodId=' + periodId
        		+ '&organisationUnitId=' + sourceId
        		+ '&categoryOptionComboId=' + categoryOptionComboId
        		+ '&value=' + value );
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
            window.alert( "Failed saving value." );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( "Failed saving value." );
    }
    
    function markValue( color )
    {
        var element = document.getElementById( 'value[' + valueId + '].value' );
        element.style.backgroundColor = color;
    }
}
