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

function submitSortOderForm()
{
    selectAllById( 'sortedList' );
    document.getElementById( 'sortProgramStageForm' ).submit();
}

//-----------------------------------------------------------------------------
// Move members
//-----------------------------------------------------------------------------

function moveFromAvailableList( ) {
	
	var fromList = document.getElementById('availableList');
	
	while ( fromList.selectedIndex > -1 ) {
		option = fromList.options.item(fromList.selectedIndex);
		jQuery('#selectedList').append("<li onClick='selectItem(this)' ondblclick='moveFromSelectedList(this);'><input type='checkbox' value='"+jQuery(option).val()+"'  title='"+i18n_compulsory_checkbox_title+"'/>"+jQuery(option).text()+"</li>");
		fromList.remove(fromList.selectedIndex);
	}
}
function selectItem(this_)
{
	if( jQuery(this_).hasClass('selected') )
		jQuery(this_).removeClass('selected');
	else
		jQuery(this_).addClass('selected');
}
function moveItemsFromSelectedList()
{
	jQuery("#selectedList .selected").each(function(){
		moveFromSelectedList(jQuery(this));
	});
}

function moveFromSelectedList(this_)
{
	var input = jQuery(this_).children("input")[0];
	var option = new Option( jQuery(this_).text(), jQuery(input).val());
	var fromList = document.getElementById('availableList');
	fromList.add(option,null);
	jQuery(this_).remove();
	input = null;
}

function submitForm(this_) {
	
	
	var selectedList = [];
	var input ;
	jQuery("#selectedList li").each(function(){
		input =  jQuery(this).children('input')[0];
		selectedList.push({"id":jQuery(input).val(),"check":jQuery(input).attr('checked')});
	});
	
	jQuery("#selectedListSubmit").val(JSON.stringify(selectedList));
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
	setInnerHTML( 'idField', getElementValue( programStageElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( programStageElement, 'name' ) );	
    setInnerHTML( 'descriptionField', getElementValue( programStageElement, 'description' ) );
    setInnerHTML( 'stageInProgramField', getElementValue( programStageElement, 'stageInProgram' ) );   
    setInnerHTML( 'minDaysFromStartField', getElementValue( programStageElement, 'minDaysFromStart' ) );    
    setInnerHTML( 'dataElementCountField', getElementValue( programStageElement, 'dataElementCount' ) );   
   
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
        setInnerHTML( 'warningField', message );
        
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

function viewDataEntryForm( associationId )
{
    window.location.href = 'viewDataEntryForm.action?associationId=' + associationId;
}