/**
 * @author Jon Moen Drange
 *
 * Javascript file for outlier analysis web UI - update outlier value.
 * Included in outlierSearchResult: updates a outlier value by sending
 * AJAX requests to the server on-the-fly when an user has updated a
 * outlier value.
 *
 */

function editOutlierValue( outlierId )
{
	var field = document.getElementById( 'outlier[' + outlierId + '].value' );
	
	var dataElementId = document.getElementById( 'outlier[' + outlierId + '].dataElement' ).value;
    var categoryOptionComboId = document.getElementById( 'outlier[' + outlierId + '].categoryOptionCombo' ).value;
	var periodId = document.getElementById( 'outlier[' + outlierId + '].period' ).value;
	var sourceId = document.getElementById( 'outlier[' + outlierId + '].source' ).value;
	
	if ( field.value != '' )
	{
		if ( !isInt(field.value) )
		{
			alert( "Value must be an integer." );
			
			field.select();
	        field.focus(); 
	        
			return;   
		}
		else
		{
			var minString = document.getElementById('outlier[' + outlierId + '].min').value;
			var maxString = document.getElementById('outlier[' + outlierId + '].max').value;
			
			var min = new Number( minString );
			var max = new Number( maxString );
			var value = new Number( field.value );
			
			if ( value < min )
			{
				var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, outlierId, '#ffcccc' );
				valueSaver.save();
				
				alert( "Value is still lower than the lower boundary." );
				return;
			}
			
			if ( value > max )
			{
				var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, outlierId, '#ffcccc' );
				valueSaver.save();
				
				alert( "Value is still higher than the upper boundary." );
				return;
			}
		}
	}
	
    var valueSaver = new ValueSaver( dataElementId, periodId, sourceId, categoryOptionComboId, field.value, outlierId, '#ccffcc', '');
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


//-----------------------------------------------------------------------------
// Saver object (modified version of dataentry/javascript/general.js)
//-----------------------------------------------------------------------------

function ValueSaver( dataElementId_, periodId_, sourceId_, categoryOptionComboId_, value_, outlierId_, resultColor_, selectedOption_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var periodId = periodId_;
    var sourceId = sourceId_;
    var categoryOptionComboId = categoryOptionComboId_;
    var value = value_;
    var outlierId = outlierId_;
    var resultColor = resultColor_;
    var selecteOption = selectedOption_;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'editOutlier.action?'
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
            window.alert( "Failed saving value:\n" + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markValue( ERROR );
        window.alert( "Failed saving value. HTTP code: \n" + errorCode );
    }
    
    function markValue( color )
    {
        var element = document.getElementById( 'outlier[' + outlierId + '].value' );
        element.style.backgroundColor = color;
    }
}
