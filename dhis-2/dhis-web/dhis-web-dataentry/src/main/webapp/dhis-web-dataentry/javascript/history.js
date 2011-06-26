
// -----------------------------------------------------------------------------
// Comments
// -----------------------------------------------------------------------------

function commentSelected()
{  
    var commentSelector = document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].comments' );
    var commentField = document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].comment' );

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
        
        saveComment( value );
    }
}

function commentLeft()
{
    var commentField = document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].comment' );
    var commentSelector = document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].comments' );

    saveComment( dataElementId, optionComboId, commentField.value );

    var value = commentField.value;
    
    if ( value == '' )
    {
        commentField.style.display = 'none';
        commentSelector.style.display = 'inline';

        commentSelector.selectedIndex = 0;
    }
}

function saveComment( commentValue )
{
    var field = document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].comment' );                
    var select = document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].comments' );

    field.style.backgroundColor = '#ffffcc';
    select.style.backgroundColor = '#ffffcc';
    
    var commentSaver = new CommentSaver( currentDataElementId, currentOptionComboId, currentOrganisationUnitId, commentValue );
    commentSaver.save();
}

function CommentSaver( dataElementId_, optionComboId_, organisationUnitId_, value_ )
{
    var SUCCESS = '#ccffcc';
    var ERROR = '#ccccff';

    var dataElementId = dataElementId_;
    var optionComboId = optionComboId_;
    var organisationUnitId = organisationUnitId_;
    var value = value_;
    
    this.save = function()
    {
        var url = 'saveComment.action?organisationUnitId=' + organisationUnitId + '&dataElementId=' +
                dataElementId + '&optionComboId=' + optionComboId + '&comment=' + value;
        
        $.ajax( { url: url, dataType: 'json', success: handleResponse, error: handleError } );
    };
    
    function handleResponse( json )
    {
    	var code = json.code;
    	
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
    
    function handleError( jqXHR, textStatus, errorThrown )
    {
        markComment( ERROR );
        window.alert( i18n_saving_comment_failed_error_code + '\n\n' + textStatus );
    }
    
    function markComment( color )
    {
        var field = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comment' );                
        var select = document.getElementById( 'value[' + dataElementId + ':' + optionComboId + '].comments' );        

        field.style.backgroundColor = color;
        select.style.backgroundColor = color;
    }
}

function removeMinMaxLimit()
{
	$( '#minLimit' ).val( '' );
	$( '#maxLimit' ).val( '' );
	
	var url = 'removeMinMaxLimits.action?organisationUnitId=' + currentOrganisationUnitId + '&dataElementId=' + currentDataElementId + '&optionComboId=' + currentOptionComboId;
	
	$.get( url, refreshWindow );
}

function saveMinMaxLimit()
{
	var minValue = $( '#minLimit' ).val();
	var maxValue = $( '#maxLimit' ).val();
	
	if ( !minValue || minValue == '' ) {
		return;
	}
	else if ( !isInt( minValue ) ) {
		$( '#minSpan' ).html( i18n_enter_digits );
		return;
	}
	else {
		$( '#minSpan' ).html( '' );
	}
	
	if ( !maxValue || maxValue == '' ) {
		return;
	}
	else if ( !isInt( maxValue ) ) {
		$( '#maxSpan' ).html( i18n_enter_digits );
		return;
	}
	else {
		$( '#maxSpan' ).html( '' );
	}
	
	if ( eval(minValue) > eval(maxValue) ) {
		$( '#maxSpan' ).html( i18n_max_must_be_greater_than_min );
		return;
	}
	else {
		$( '#maxSpan' ).html( '' );
	}
	
	if ( window.opener && window.opener.document ) {
		window.opener.document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].min' ).innerHTML = minValue;
    	window.opener.document.getElementById( 'value[' + currentDataElementId + ':' + currentOptionComboId + '].max' ).innerHTML = maxValue;
	}
	
    var url = 'saveMinMaxLimits.action?organisationUnitId=' + currentOrganisationUnitId + '&dataElementId=' + currentDataElementId + 
    	'&optionComboId=' + currentOptionComboId + '&minLimit=' + minValue + '&maxLimit=' + maxValue;
    
    $.get( url, refreshWindow );
}

function refreshWindow()
{
	var source = 'getHistoryChart.action?dataElementId=' + currentDataElementId + '&categoryOptionComboId=' + currentOptionComboId + '&r=' + Math.random();
	
	$( '#historyChart' ).attr( 'src', source );
}

function markValueForFollowup( dataElementId, periodId, sourceId, categoryOptionComboId )
{
    var url = 'markValueForFollowup.action?dataElementId=' + dataElementId + '&periodId=' + periodId +
        '&sourceId=' + sourceId + '&categoryOptionComboId=' + categoryOptionComboId;
    
    $.getJSON( url, function( json ) {
    	
    	if ( json.message == 'marked' ) {
    		$( '#followup' ).attr( 'src', '../images/marked_large.png' );
    		$( '#followup' ).attr( 'alt', i18n_unmark_value_for_followup );
    	}
    	else if ( json.message == 'unmarked' ) {
    		$( '#followup' ).attr( 'src', '../images/unmarked_large.png' );
    		$( '#followup' ).attr( 'alt', i18n_mark_value_for_followup );
    	}
    } );
}
