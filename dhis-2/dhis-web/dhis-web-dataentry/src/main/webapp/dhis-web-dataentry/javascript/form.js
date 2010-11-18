
window.onload = function () 
{
	var inputs = document.getElementsByName( "entryfield" ) 

	for ( var i = 0, input; input = inputs[i]; i++ )
	{
		input.addEventListener('focus', valueFocus, false);
	}

    var selects = document.getElementsByName( "entryselect" );

	for ( var i = 0, select; select = selects[i]; i++ )
	{
		select.addEventListener('focus', valueFocus, false);
	}
}

// -----------------------------------------------------------------------------
// Selection
// -----------------------------------------------------------------------------

function clearDataSetAndPeriod()
{
	clearList( document.getElementById( 'selectedDataSetId' ) );
	clearPeriod();
}

function clearPeriod()
{	
	clearList( document.getElementById( 'selectedPeriodIndex' ) );	
	$('#entryForm').html( '' );
}

function organisationUnitSelected( orgUnits )
{
    var selectedDataSetId = $( '#selectedDataSetId' ).val();
    
    var url = 'loadDataSets.action';
    
    var list = document.getElementById( 'selectedDataSetId' );
    
    clearList( list );
    addOptionToList( list, '-1', '[ Select ]' );
    
    $.getJSON( url, function( json ) {
    	$('#selectedOrganisationUnit').val( json.organisationUnit.name );
    	$('#currentOrganisationUnit').html( json.organisationUnit.name );
    	
    	for ( i in json.dataSets ) {
    		addOptionToList( list, json.dataSets[i].id, json.dataSets[i].name );
    	}
    	
    	if ( json.selectionValid && selectedDataSetId != null ) {
    		$( '#selectedDataSetId' ).val( selectedDataSetId );
    	} 
    } );
}

selection.setListenerFunction( organisationUnitSelected );

function nextPeriodsSelected()
{
	displayPeriodsInternal( true, false );
}

function previousPeriodsSelected()
{
	displayPeriodsInternal( false, true );
}

function dataSetSelected()
{
	displayPeriodsInternal( false, false );
}

function displayPeriodsInternal( next, previous )
{
	var dataSetId = $( '#selectedDataSetId' ).val();
	
	if ( dataSetId && dataSetId != -1 )
	{
		var url = 'loadPeriods.action?dataSetId=' + dataSetId + '&next=' + next + '&previous=' + previous;

		var list = document.getElementById( 'selectedPeriodIndex' );
		
	    clearList( list );
	    addOptionToList( list, '-1', '[ Select ]' );
		
	    $.getJSON( url, function( json ) {
	    	for ( i in json.periods ) {
	    		addOptionToList( list, i, json.periods[i].name );
	    	}
	    } );
	}
}

function displayEntryForm()
{
	displayEntryFormInternal( null );
}

function periodSelected()
{
	displayEntryFormInternal( setDisplayModes );
}

function displayEntryFormInternal( callback )
{
	var dataSetId = $('#selectedDataSetId').val();
	var periodIndex = $('#selectedPeriodIndex').val();
	
	if ( dataSetId && dataSetId != -1 && periodIndex && periodIndex != -1 )
	{
		var url = 'select.action?selectedDataSetId=' + dataSetId +
			'&selectedPeriodIndex=' + periodIndex +
			'&displayMode=' + $("input[name='displayMode']:checked").val();
		
		$('#entryForm').load( url, callback );
	}
}

function setDisplayModes()
{
	$.getJSON( 'loadDisplayModes.action', function( json ) {
		if ( json.customForm ) {
			$( '#displayModeCustom' ).removeAttr( 'disabled' );
		}
		else {
			$( '#displayModeCustom' ).attr( 'disabled', 'disabled' );
		}
		
		if ( json.sectionForm ) {
			$( '#displayModeSection' ).removeAttr( 'disabled' );
		}
		else {
			$( '#displayModeSection' ).attr( 'disabled', 'disabled' );
		}
		
		if ( json.displayMode == 'customform' ) {
			$( '#displayModeCustom' ).attr( 'checked', 'checked' );
			$( '#displayModeSection' ).removeAttr( 'checked' );
			$( '#displayModeDefault' ).removeAttr( 'checked' );
		}
		else if ( json.displayMode = 'sectionform' ) {
			$( '#displayModeCustom' ).removeAttr( 'checked' );
			$( '#displayModeSection' ).attr( 'checked', 'checked' );
			$( '#displayModeDefault' ).removeAttr( 'checked' );
		}
		else if ( json.displayMode = 'defaultform' ) {
			$( '#displayModeCustom' ).removeAttr( 'checked' );
			$( '#displayModeSection' ).removeAttr( 'checked' );
			$( '#displayModeDefault' ).attr( 'checked', 'checked' );
		}
	} );
}

function viewHistory( dataElementId, optionComboId, showComment )
{
    window.open( 'viewHistory.action?dataElementId=' + dataElementId + '&optionComboId=' + optionComboId + '&showComment=' + showComment, '_blank', 'width=580,height=710,scrollbars=yes' );
}

/**
 * Display data element name in selection display when a value field recieves
 * focus.
 */
var customDataEntryFormExists = "false";

function valueFocus(e) 
{
	//Retrieve the data element id from the id of the field
	var baseId = e.target.id;	
	
	var opId = baseId;
	var str = baseId;
	
	if(	baseId.indexOf(':') != -1 )
	{
		opId = baseId.substr( baseId.indexOf(':')+1, baseId.length );
		str = baseId.substr( 0, baseId.indexOf(':') );
	}
	
	var match1 = /.*\[(.*)\]/.exec(str); //value[-dataElementId-]	
	var match2 = /.*\[(.*)\]/.exec(opId); //value[-optionComboId-]
	
	if ( ! match1 )
	{				
		return;
	}

	deId = match1[1];
	ocId = match2[1];		
	
	var nameContainer = document.getElementById('value[' + deId + '].name');
	var opCbContainer = document.getElementById('value[option' + ocId + '].name');
	var minContainer = document.getElementById('value[' + deId + ':' + ocId +'].min');	
	var maxContainer = document.getElementById('value[' + deId + ':' + ocId +'].max');
	
	if ( ! nameContainer )
	{		
		return;
	}

	var name = '';
	var optionName = '';
	
	var as = nameContainer.getElementsByTagName('a');

	if ( as.length > 0 )	//Admin rights: Name is in a link
	{
		name = as[0].firstChild.nodeValue;
	} 
	else 
	{
		name = nameContainer.firstChild.nodeValue;
	}
	
	if( opCbContainer )
	{	    
		if( opCbContainer.firstChild )
		{
		    optionName = opCbContainer.firstChild.nodeValue;
		}			
	}
	
	if( minContainer )
	{
	    	    
	    if( minContainer.firstChild )
	    {        
	        optionName += " - "+minContainer.firstChild.nodeValue; 
	    }	    
	}
	
	if( maxContainer )
	{
	    if( maxContainer.firstChild )
	    {
	        optionName += " - "+maxContainer.firstChild.nodeValue;
	    }
	}
	    
    var curDeSpan = document.getElementById('currentDataElement');
     
    curDeSpan.firstChild.nodeValue = name;
    
    document.getElementById("currentOptionCombo").innerHTML  = optionName;
	
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

// -----------------------------------------------------------------------------
// View history
// -----------------------------------------------------------------------------

/**
 * Set min/max limits for dataelements that has one or more values, and no 
 * manually entred min/max limits.
 */
function SetGeneratedMinMaxValues()
{
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'minmax' );
        request.send( 'minMaxGeneration.action' );
    };
    
    function handleResponse( rootElement )
    {
        var dataElements = rootElement.getElementsByTagName( 'dataelement' );
        
        for( i = 0; i < dataElements.length; i++ )
        {
            var deId = getElementValue( dataElements[i], 'dataelementId' );
            var ocId = getElementValue( dataElements[i], 'optionComboId' );
            
            setInnerHTML('value[' + deId + ':' + ocId + '].min', getElementValue( dataElements[i], 'minLimit'));
            setInnerHTML('value[' + deId + ':' + ocId + '].max', getElementValue( dataElements[i], 'maxLimit'));
        }
        
    }
    
    function handleHttpError( errorCode )
    {
        window.alert( i18n_saving_minmax_failed_error_code + '\n\n' + errorCode );
    }   
   
    
    function getElementValue( parentElement, childElementName )
    {
        var textNode = parentElement.getElementsByTagName( childElementName )[0].firstChild;
        
        if ( textNode )
        {
            return textNode.nodeValue;
        }
        else
        {
            return null;
        }
    }    
}

function generateMinMaxValues()
{    
	lockScreen();
	var setGeneratedMinMaxValues = new SetGeneratedMinMaxValues();
    setGeneratedMinMaxValues.save();
	
	unLockScreen();
	setHeaderDelayMessage(i18n_generate_min_max_success);
}

// -----------------------------------------------------------------------------
// Data completeness
// -----------------------------------------------------------------------------

function validateCompleteDataSet()
{
	var confirmed = confirm( i18n_confirm_complete );
	
	if ( confirmed )
	{
	    var url = "getValidationViolations.action";
    
        var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( registerCompleteDataSet );
        request.send( url );        
	}
}

function registerCompleteDataSet( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    
    if ( type == "none" )
    {
        var date = document.getElementById( "dateField" ).value;
    
        var url = "registerCompleteDataSet.action?date=" + date;
    
        var request = new Request();
		request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( registerReceived );
        request.send( url );
    }
    else
    {
        window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
    }
}

function registerReceived( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	
    if ( type=='input' )
    {
		setHeaderDelayMessage( messageElement.firstChild.nodeValue );
		return;
	}
	
	document.getElementById( "completeButton" ).disabled = true;
	document.getElementById( "undoButton" ).disabled = false;
    document.getElementById( "dateField" ).disabled = true;
    document.getElementById( "dateDiv" ).style.display = "none";
}

function undoCompleteDataSet()
{
	var confirmed = confirm( i18n_confirm_undo );
	
	if ( confirmed )
	{
        var url = "undoCompleteDataSet.action";
    
        var request = new Request();
        request.setCallbackSuccess( undoReceived );
        request.send( url );
	}
}

function undoReceived( messageElement )
{
    document.getElementById( "completeButton" ).disabled = false;
    document.getElementById( "undoButton" ).disabled = true;
    document.getElementById( "dateField" ).disabled = false;
    document.getElementById( "dateDiv" ).style.display = "inline";
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validate()
{
    window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
}
