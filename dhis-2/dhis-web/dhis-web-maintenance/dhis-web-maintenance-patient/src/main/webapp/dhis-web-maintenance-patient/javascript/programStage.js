function isInt( value )
{
    var number = new Number( value );
    
    if ( isNaN( number ))
    {   	
        return false;
    }
    
    return true;
}

function getStageByProgram( programId )
{
	window.location = "programStage.action?id=" + programId;
}

function addProgramStage()
{
	var programId = document.getElementById('id').value;
	
	if( programId == "null"  || programId == "" )
	{
		window.alert( i18n_please_select_program );
	}
	else
	{
		window.location.href="showAddProgramStageForm.action?id=" + programId;
	}
}

function showSortProgramStage()
{
	var programId = document.getElementById('id').value;
	
	if( programId == "null"  || programId == "" )
	{
		window.alert( i18n_please_select_program );
	}
	else
	{
		window.location.href="showSortProgramStageForm.action?id=" + programId;
	}
}


//-----------------------------------------------------------------------------
// Move members
//-----------------------------------------------------------------------------


var selectedList;
var availableList;

function move( listId ) {
	
	var fromList = document.getElementById(listId);
	
	if ( fromList.selectedIndex == -1 ) { return; }
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById( 'availableList' );
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById( 'selectedList' );
	}
	
	var toList = ( fromList == availableList ? selectedList : availableList );
	
	while ( fromList.selectedIndex > -1 ) {
		
		option = fromList.options.item(fromList.selectedIndex);
		fromList.remove(fromList.selectedIndex);
		toList.add(option, null);
	}
}

function submitForm() {
	
	if ( ! availableList ) 
	{
		availableList = document.getElementById('availableList');
	}
	
	if ( ! selectedList ) 
	{
		selectedList = document.getElementById('selectedList');
	}
	
	selectAll( selectedList );
	
	return false;
}

function selectAll( list ) 
{
	for ( var i = 0, option; option = list.options.item(i); i++ ) 
	{
		option.selected = true;
	}
}


function moveUp( listId ) 
{
	
	var withInList = document.getElementById( listId ); 
	  
	var index = withInList.selectedIndex;
	  
	if ( index == -1 ) { return; } 
	  
	if( index - 1 < 0 ) { return; }//window.alert( 'Item cant be moved up');        
	  
	var option = new Option( withInList.options[index].text, withInList.options[index].value); 
	var temp = new Option( withInList.options[index-1].text, withInList.options[index-1].value);
	  
	withInList.options[index-1] = option;
	withInList.options[index-1].selected = true;
	withInList.options[index] = temp;  

}

function moveDown( listId ) 
{
	var withInList = document.getElementById( listId ); 
	  
	var index = withInList.selectedIndex;
	  
	if ( index == -1 ) { return; } 
	  
	if( index + 1 == withInList.options.length ) { return; }//window.alert( 'Item cant be moved down');   
	    
	var option = new Option( withInList.options[index].text, withInList.options[index].value); 
	var temp = new Option( withInList.options[index+1].text, withInList.options[index+1].value);
	  
	withInList.options[index+1] = option;
	withInList.options[index+1].selected = true;
	withInList.options[index] = temp; 
	  
}


// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramStageDetails( programStageId )
{
    var request = new Request();
    request.setResponseTypeXML( 'programStage' );
    request.setCallbackSuccess( programStageReceived );
    request.send( 'getProgramStage.action?id=' + programStageId );
}

function programStageReceived( programStageElement )
{
	setFieldValue( 'idField', getElementValue( programStageElement, 'id' ) );
	setFieldValue( 'nameField', getElementValue( programStageElement, 'name' ) );	
    setFieldValue( 'descriptionField', getElementValue( programStageElement, 'description' ) );
    setFieldValue( 'stageInProgramField', getElementValue( programStageElement, 'stageInProgram' ) );   
    setFieldValue( 'minDaysFromStartField', getElementValue( programStageElement, 'minDaysFromStart' ) );    
    setFieldValue( 'dataElementCountField', getElementValue( programStageElement, 'dataElementCount' ) );   
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove ProgramStage
// -----------------------------------------------------------------------------

function removeProgramStage( programStageId, name )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeProgramStageCompleted );
        window.location.href = 'removeProgramStage.action?id=' + programStageId;
    }
}

function removeProgramStageCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'programStage.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add ProgramStage
// -----------------------------------------------------------------------------

function validateAddProgramStage()
{	
	var minDaysFromStartField = document.getElementById( 'minDaysFromStart' );
	
	if( !isInt( minDaysFromStartField.value ) )
	{
		window.alert( i18n_value_must_integer );
		minDaysFromStartField.select();
		minDaysFromStartField.focus();
		
		return false;
	}
	
	if( isInt( minDaysFromStartField.value ) )
	{
		if( minDaysFromStartField.value < 0 )
		{
			window.alert( i18n_value_must_positive );
			minDaysFromStartField.select();
			minDaysFromStartField.focus();
			
			return false;
		}		
	}
	
	
	var url = 'validateProgramStage.action?' +
		'nameField=' + getFieldValue( 'nameField' ) +			
		'&description=' + getFieldValue( 'description' ) +		
		'&minDaysFromStart=' + getFieldValue( 'minDaysFromStart' );

	var request = new Request();
		request.setResponseTypeXML( 'message' );
		request.setCallbackSuccess( addValidationCompleted );    
		request.send( url );        

	return false;
    
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addProgramStageForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_program_stage_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
// -----------------------------------------------------------------------------
// Update ProgramStage
// -----------------------------------------------------------------------------

function validateUpdateProgramStage()
{
	
	var minDaysFromStartField = document.getElementById( 'minDaysFromStart' );
	
	if( !isInt( minDaysFromStartField.value ) )
	{
		window.alert( i18n_value_must_integer );
		minDaysFromStartField.select();
		minDaysFromStartField.focus();
		
		return false;
	}
	
	if( isInt( minDaysFromStartField.value ) )
	{
		if( minDaysFromStartField.value < 0 )
		{
			window.alert( i18n_value_must_positive );
			minDaysFromStartField.select();
			minDaysFromStartField.focus();
			
			return false;
		}		
	}
	
    var url = 'validateProgramStage.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' ) +	        
	        '&minDaysFromStart=' + getFieldValue( 'minDaysFromStart' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );   
    
    request.send( url );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var form = document.getElementById( 'updateProgramStageForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_updating_program_stage_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}