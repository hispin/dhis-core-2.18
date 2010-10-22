
// -----------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------

function commentSelected( dataElementId, optionComboId )
{  
    var commentSelector = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );
    var commentField = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );

    var value = commentSelector.options[commentSelector.selectedIndex].value;
    
    if ( value == 'custom' )
    {
        commentSelector.style.display = 'none';
        commentField.style.display = 'inline';
        
        commentField.select();
        commentField.focus();
    }
    else
    {
        commentField.value = value;
        
        saveComment( dataElementId, optionComboId, value );
    }
}

function commentLeft( dataElementId, optionComboId )
{
    var commentField = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );
    var commentSelector = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );

    saveComment( dataElementId, optionComboId, commentField.value );

    var value = commentField.value;
    
    if ( value == '' )
    {
        commentField.style.display = 'none';
        commentSelector.style.display = 'inline';

        commentSelector.selectedIndex = 0;
    }
}

function saveComment( dataElementId, optionComboId, commentValue )
{
    var field = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );                
    var select = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );
    
    field.style.backgroundColor = '#ffffcc';
    select.style.backgroundColor = '#ffffcc';
    
    var commentSaver = new CommentSaver( dataElementId, optionComboId, commentValue );
    commentSaver.save();
}

function CommentSaver( dataElementId_, optionComboId_, value_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var optionComboId = optionComboId_
    var value = value_;
    
    this.save = function()
    {
        var request = new Request();
        request.setCallbackSuccess( handleResponse );
        request.setCallbackError( handleHttpError );
        request.setResponseTypeXML( 'status' );
        request.send( 'saveComment.action?dataElementId=' +
                dataElementId + '&optionComboId=' + optionComboId + '&comment=' + value );
    };
    
    function handleResponse( rootElement )
    {
        var codeElement = rootElement.getElementsByTagName( 'code' )[0];
        var code = parseInt( codeElement.firstChild.nodeValue );
        
        if ( code == 0 )
        {
            markComment( SUCCESS );           
        }
        else
        {
            markComment( ERROR );
            window.alert( i18n_saving_comment_failed_status_code + '\n\n' + code );
        }
    }
    
    function handleHttpError( errorCode )
    {
        markComment( ERROR );
        window.alert( i18n_saving_comment_failed_error_code + '\n\n' + errorCode );
    }
    
    function markComment( color )
    {
        var field = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );                
        var select = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );        

        field.style.backgroundColor = color;
        select.style.backgroundColor = color;
    }
}

function isInt(value){
	if(((value) == parseInt(value)) && !isNaN(parseInt(value))){
		return true;
	} else {
		  return false;
	} 
}

function saveMinLimit( organisationUnitId, dataElementId, optionComboId )
{
    var minLimitField = document.getElementById( "minLimit" );
	if(!isInt(minLimitField.value)){
		setInnerHTML('minSpan', i18n_enter_digits);
		return;
	}else{
		setInnerHTML('minSpan', "");
	}
	
	var maxLimitField = document.getElementById( "maxLimit" );
	if(!isInt(maxLimitField.value)){
		setInnerHTML('maxSpan', i18n_enter_digits);
		return;
	}else{
		setInnerHTML('maxSpan', "");
	}
    
    var request = new Request();
    request.setCallbackSuccess( refreshWindow );
    request.setCallbackError( refreshWindow );

    if ( minLimitField.value == '' )
    {
        request.send( 'removeMinMaxLimits.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' + dataElementId + '&optionComboId=' + optionComboId );
    }
    else
    {
        var minLimit = Number( minLimitField.value );
        var maxLimit = Number( maxLimitField.value );
        
        if ( minLimit )
        {
        	if ( minLimit < 0 )
        	{
        	    minLimit = 0;
        	}

            if ( !maxLimit || maxLimit <= minLimit )
            {
                maxLimit = minLimit + 1;
            }

            request.send( 'saveMinMaxLimits.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' + dataElementId + '&optionComboId=' + optionComboId + '&minLimit=' + minLimit + '&maxLimit=' + maxLimit );
        }
        else
        {
            refreshWindow();
        }
    }
}

function saveMaxLimit( organisationUnitId, dataElementId, optionComboId )
{	 
	var maxLimitField = document.getElementById( "maxLimit" );
	if(!isInt(maxLimitField.value)){
		setInnerHTML('maxSpan', i18n_enter_digits);
		return;
	}else{
		setInnerHTML('maxSpan', "");
	}
    
	var minLimitField = document.getElementById( "minLimit" );
	if(!isInt(minLimitField.value)){
		setInnerHTML('minSpan', i18n_enter_digits);
		return;
	}else{
		setInnerHTML('minSpan', "");
	}
	
    var request = new Request();
    
    request.setCallbackSuccess( refreshWindow );
    request.setCallbackError( refreshWindow );

    if ( maxLimitField.value == '' )
    {
       
    	request.send( 'removeMinMaxLimits.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' + dataElementId + '&optionComboId=' + optionComboId );
   
    }
    else
    {
    	var minLimit = Number( minLimitField.value );
        var maxLimit = Number( maxLimitField.value );
        
        if ( maxLimit )
        {
            if ( maxLimit < 1 )
            {
                maxLimit = 1;
            }

            if ( !minLimit )
            {
                minLimit = 0;
            }
            else if ( minLimit >= maxLimit )
            {
                minLimit = maxLimit - 1;
            }

          request.send( 'saveMinMaxLimits.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' + dataElementId + '&optionComboId=' + optionComboId + '&minLimit=' + minLimit + '&maxLimit=' + maxLimit );
            
        }
        else
        {
            refreshWindow();
        }
    }
}

function refreshWindow()
{
    window.location.reload();
}

function markValueForFollowup( dataElementId, periodId, sourceId, categoryOptionComboId )
{
    var url = "markValueForFollowup.action?dataElementId=" + dataElementId + "&periodId=" + periodId +
        "&sourceId=" + sourceId + "&categoryOptionComboId=" + categoryOptionComboId;
    
    var request = new Request();
    request.setResponseTypeXML( "message" );
    request.setCallbackSuccess( markValueForFollowupReceived );    
    request.send( url );
}

function markValueForFollowupReceived( messageElement )
{	
    var message = messageElement.firstChild.nodeValue;
    var image = document.getElementById( "followup" );
    
    if ( message == "marked" )
    {
    	image.src = "../images/marked_large.png";
        image.alt = i18n_unmark_value_for_followup;
    }
    else if ( message = "unmarked" )
    {
        image.src = "../images/unmarked_large.png";
        image.alt = i18n_mark_value_for_followup;  	
    }
}


