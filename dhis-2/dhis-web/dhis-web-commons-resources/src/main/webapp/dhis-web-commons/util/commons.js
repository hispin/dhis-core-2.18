
// -----------------------------------------------------------------------------
// Global variables
// -----------------------------------------------------------------------------

var headerMessageTimeout = -1;

/**
 * Redirects to the translate GUI.
 * 
 * @param className the name of the object class.
 * @param objectId the identifier of the object.
 */
function translate( className, objectId )
{
    var url = "../dhis-web-commons/i18n.action?className=" + className + "&objectId=" + objectId + "&returnUrl=" + htmlEncode( window.location.href ); 
    
    window.location.href = url; 
}

/**
 * Gets help content for the given id. Opens the right bar and puts the content
 * inside. Reads data from an underlying docbook file.
 * 
 * @param id the content id, refers to the section id in the docbook file.
 */
function getHelpContent( id )
{
    $.get( 
       '../dhis-web-commons-about/getHelpContent.action',
       { "id": id },
       function( data )
       {
           $( "div#rightBar" ).fadeIn();
           $( "div#rightBarContents" ).html( data );
       } );
}

/**
 * Hides the help content.
 */
function hideHelpContent()
{
	$( "div#rightBar" ).fadeOut();
}

/**
 * Filters values in a html table with tbody id "list".
 * 
 * @param filter the filter.
 */
function filterValues( filter, columnIndex )
{
	if( columnIndex==undefined ) columnIndex = 0;
	
    var list = document.getElementById( 'list' );
    
    var rows = list.getElementsByTagName( 'tr' );
    
    for ( var i = 0; i < rows.length; ++i )
    {
        var cell = rows[i].getElementsByTagName( 'td' )[columnIndex - 1];
        
        var value = cell.firstChild.nodeValue;

        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            rows[i].style.display = 'table-row';
        }
        else
        {
            rows[i].style.display = 'none';
        }
    }
}

/**
 * Opens a help popup window.
 * 
 * @param id the key of the help property content.
 */
function openHelpForm( id )
{
    window.open( "../dhis-web-commons-help/viewDynamicHelp.action?id=" + id, "_blank", "width=800,height=600,scrollbars=yes" );
}   

/**
 * Returns the value of the selected option in the list with the given identifier.
 * 
 * @param listId the list identifier.
 */
function getListValue( listId )
{
    var list = document.getElementById( listId );
    var value = list.options[ list.selectedIndex ].value;
    
    return value;
}

/**
 * Hides the document element with the given identifier.
 * 
 * @param id the element identifier.
 */
function hideById( id )
{
  jQuery("#" + id).hide();
}

/**
 * Shows the document element with the given identifier.
 * 
 * @param id the element identifier.
 */
function showById( id )
{
  jQuery("#" + id).show();
}

/**
 * Returns true if the element with the given identifier has text, false if not.
 * 
 * @param inputId the identifier of the input element.
 */
function hasText( inputId )
{
    return trim( getFieldValue( inputId ) ) != "";
}

function trim( string )
{
	return jQuery.trim( string );
}


/**
 * Returns true if the element with the given identifier is checked, false if not
 * or if it does not exist.
 * 
 * @param checkboxId the identifier of the checkbox element.
 */
function isChecked( checkboxId )
{
	return jQuery( "#" + checkboxId ).attr("checked");   
}

/**
 * Checks the checkbox with the given identifier if the checkbox exists.
 */
function check( checkBoxId )
{
    jQuery( "#" + checkBoxId ).attr("checked", true );
}



/**
 * Unchecks the checkbox with the given identifier if the checkbox exists.
 */
function uncheck( checkBoxId )
{
    jQuery( "#" + checkBoxId ).attr("checked", false );
}

/**
 * Enables the element with the given identifier if the element exists.
 */
function enable( elementId )
{
    jQuery( "#" + elementId ).attr("disabled", false );
}

/**
 * Disables the element with the given identifier if the element exists.
 */
function disable( elementId )
{
    jQuery( "#" + elementId ).attr("disabled", true );
}
/**
 * Enables the element with the given identifier if the element exists in parent window of frame.
 */
function enableParent( elementId )
{
    var element = parent.document.getElementById( elementId );
    
    if ( element )
    {
        element.disabled = false;
    }
}

/**
 * Disables the element with the given identifier if the element exists in parent window of frame.
 */
function disableParent( elementId )
{
    var element = parent.document.getElementById( elementId );
    
    if ( element )
    {
        element.disabled = true;
    }
}

/**
 * Enables the element with the given identifier if the element exists in parent window of frame.
 */
function enableParent( elementId )
{
    var element = parent.document.getElementById( elementId );
    
    if ( element )
    {
        element.disabled = false;
    }
}

/**
 * Disables the element with the given identifier if the element exists in parent window of frame.
 */
function disableParent( elementId )
{
    var element = parent.document.getElementById( elementId );
    
    if ( element )
    {
        element.disabled = true;
    }
}

/**
 * Returns true if the element with the given identifier has selected elements
 * associated with it, false if not.
 * 
 * @param listId the identifier of the list element.
 */
function hasElements( listId )
{
    return jQuery( "#" + listId ).children().length > 0;
}

/**
 * Returns true if the element with the given identifier exists, false if not.
 * 
 * @param elementId the identifier of the element.
 */
function isNotNull( elementId )
{
    return  jQuery("#" + elementId).length  == 1;
}

/**
 * HTML encodes the given string.
 * 
 * @param str the input string.
 * @return the HTML encoded string.
 */
function htmlEncode( str )
{
    str = str.replace( /\%/g, "%25" ); //This line must come first so the % doesn't get overwritten later
    str = str.replace( /\ /g, "%20" );
    str = str.replace( /\!/g, "%21" );
    str = str.replace( /\"/g, "%22" );
    str = str.replace( /\#/g, "%23" );
    str = str.replace( /\$/g, "%24" );
    str = str.replace( /\&/g, "%26" );
    str = str.replace( /\'/g, "%27" );
    str = str.replace( /\(/g, "%28" );
    str = str.replace( /\)/g, "%29" );
    str = str.replace( /\*/g, "%2a" );
    str = str.replace( /\+/g, "%2b" );
    str = str.replace( /\,/g, "%2c" );
    str = str.replace( /\-/g, "%2d" );
    str = str.replace( /\./g, "%2e" );
    str = str.replace( /\//g, "%2f" );
    str = str.replace( /\:/g, "%3a" );
    str = str.replace( /\;/g, "%3b" );
    str = str.replace( /\</g, "%3c" );
    str = str.replace( /\=/g, "%3d" );
    str = str.replace( /\>/g, "%3e" );
    str = str.replace( /\?/g, "%3f" );
    str = str.replace( /\@/g, "%40" );
    
    return str;
}

/**
 * Gets the value for the element with the given name from the DOM object.
 * 
 * @param parentElement the DOM object.
 * @param childElementName the name of the element.
 */
function getElementaValue( parentElement, childElementName )
{
    var textNode = parentElement.getElementsByTagName( childElementName )[0].firstChild;
    
    return textNode ? textNode.nodeValue : null;
}

/**
 * Gets the attribute value from the given DOM element.
 * 
 * @param parentElement the DOM object.
 * @param attributeName the name of the attribute.
 */
function getElementAttribute( parentElement, childElementName, childAttributeName )
{
	var textNode = parentElement.getElementsByTagName( childElementName )[0];
    
    return textNode ? textNode.getAttribute( childAttributeName ) : null; 
}

/**
 * Gets the value from the given DOM element.
 * 
 * @param rootElement the DOM object.
 */
function getRootElementValue( rootElement )
{
    var textNode = rootElement.firstChild;
    
    return textNode ? textNode.nodeValue : null;
}

/**
 * Gets the value of the attribute with the given name from the given DOM element.
 * 
 * @param rootElement the DOM object.
 * @param attributeName the name of the attribute.
 */
function getRootElementAttribute( rootElement, attributeName )
{
   return rootElement.getAttribute( attributeName );
}

/**
 * Sets a value on the given element.
 * 
 * @param fieldId the identifier of the element.
 * @param value the value to set.
 */
function setFieldValue( fieldId, value )
{
    document.getElementById( fieldId ).innerHTML = value;
}

/**
 * Gets a value from the given element and HTML encodes it.
 * 
 * @param fieldId the identifier of the element.
 * @return the HTML encoded value of the element with the given identifier.
 */
function getFieldValue( fieldId )
{
    return jQuery("#" + fieldId).val();
}

// -----------------------------------------------------------------------------
// Message, warning, info, headerMessage
// -----------------------------------------------------------------------------

/**
 * Shows the message span and sets the message as text.
 * 
 * @param message the message. 
 */
function setMessage( message )
{
	if ( (message != "") && (message != null) )
	{
		$( '#message' ).html( message );
		$( '#message' ).slideDown( 'fast' );
	}
}

/**
 * Shows the message span and sets the message as text together with a wait animation.
 * 
 * @param message the message.
 */
function setWaitMessage( message )
{
	setMessage( message + "&nbsp;&nbsp;&nbsp;<img src='../images/ajax-loader-bar-small.gif'>" );
}

/**
 * Makes the "message" span invisible.
 */
function hideMessage()
{
	$( '#message' ).slideUp( 'fast' );
}

/**
 * Slides down the header message div and sets the message as text.
 * 
 * @param message the message.
 */
function setHeaderMessage( message )
{
    $( 'div#headerMessage' ).html( message );
    $( 'div#headerMessage' ).slideDown( 'fast' );
}

/**
 * Updates the text in the header message div with the message.
 * 
 * @param message the message.
 */
function updateHeaderMessage( message )
{
	$( 'div#headerMessage' ).html( message );
}

/**
 * Slides down the header message div and sets the message as text together with a wait animation.
 * 
 * @param message the message.
 */
function setHeaderWaitMessage( message )
{
	$( 'div#headerMessage' ).html( message + "&nbsp;&nbsp;&nbsp;<img src='../images/ajax-loader-bar-small.gif'>" );
    $( 'div#headerMessage' ).slideDown( 'fast' );
}

/**
 * Updates the text in the header message div with the message.
 * 
 * @param message the message.
 */
function updateHeaderWaitMessage( message )
{
	$( 'div#headerMessage' ).html( message + "&nbsp;&nbsp;&nbsp;<img src='../images/ajax-loader-bar-small.gif'>" );
}

/**
 * Sets the header message and hides it after 3 seconds.
 */
function setHeaderDelayMessage( message )
{
	setHeaderMessage( message );
	
	window.clearTimeout( headerMessageTimeout ); // Clear waiting invocations
	
	headerMessageTimeout = window.setTimeout( "hideHeaderMessage();", 3000 );
}

/**
 * Hides the header message div.
 */
function hideHeaderMessage()
{
    $( 'div#headerMessage' ).slideUp( 'fast' );
}   

/**
 * Slides down the info message div and sets the message as text.
 * 
 * @param message the message.
 */
function setInfo( message )
{
    $( '#info' ).html( message );
    $( '#info' ).slideDown( 'fast' );
}

/**
 * Hides the info div.
 */
function hideInfo()
{
    $( '#info' ).slideUp( 'fast' );
}

/**
 * Makes the "detailsArea" span visible.
 */
function showDetails()
{
    $( '#detailsArea' ).show( "fast" );
}

/**
 * Makes the "detailsArea" span invisible.
 */
function hideDetails()
{
    $( '#detailsArea' ).hide( "fast" );
}

/**
 * Makes the "warningArea" span visible.
 */
function showWarning()
{
    $( '#warningArea' ).show( "fast" );
}

/**
 * Makes the "warningArea" span invisible.
 */
function hideWarning()
{
    $( '#warningArea' ).hide( "fast" );
}

/**
 * Convenience method for getting a document element.
 * 
 * @param id id of the element to get.
 */
function byId( elementId )
{
  return document.getElementById( elementId );
}

/**
 * Toggles visibility for an element.
 * 
 * @param id the identifier of the element.
 * @param display boolean indicator.
 */
function toggleByIdAndFlag( id, display )
{
    var node = byId( id );
    
    if ( ! node )
    {
        return;
    }
    
    node.style.display = ( display ? 'block' : 'none' );
}

/**
 * Toggles visibility for an element.
 * 
 * @param id the identifier of the element.
 */
function toggleById( id )
{
    var node = byId( id );

    if ( ! node )
    {
        return;
    }
    
    var display = node.style.display;
    
    node.style.display = ( display == 'none' || display == '' ? 'block' : 'none' );
}

/**
 * Show div at center of screen.
 */
function setPositionCenter( id )
{
	var div = document.getElementById(id);
	
	var width = div.style.width;
	var height = div.style.height;
	
	var x = (document.documentElement.clientHeight / 2) - new Number(height.replace('px',''))/2;
	var y = (document.documentElement.clientWidth / 2) - new Number(width.replace('px',''))/2;	
	div.style.top = x +"px";
	div.style.left  = y +"px";		
}

/**
 * Toggles visibility of document element.
 */
function showHideDiv( elementId )
{	
	if ( document.getElementById( elementId ).style.display == "none" )
	{
		document.getElementById( elementId ).style.display = "block";
	}
	else
	{
		document.getElementById( elementId ).style.display = "none";
	}	
}

/**
 * Adds a div with 50 % opacity on the document.
 */
function showDivEffect()
{
	var width = document.documentElement.clientWidth;
	var height = document.documentElement.clientHeight;	
	var divEffect = document.createElement( 'div' );
	
	divEffect.id = "divEffect";
	divEffect.style.position = "fixed";
	divEffect.style.top = 0;
	divEffect.style.width = width + "px";
	divEffect.style.height = height + "px";
	divEffect.style.background = "#000000";
	divEffect.style.opacity = 0.5;
	divEffect.style.zIndex = 10;
	//divEffect.innerHTML = "<div style='background-color:#EFEFEF;position:absolute;top:300px;width:100%;text-align:center'><img src=\"../images/ajax-loader.gif\"/></div>";	
	document.body.appendChild( divEffect );	
}

/**
 * Removes the opacity div from the document.
function deleteDivEffect()
 */
function deleteDivEffect()
{
	var divEffect = document.getElementById( 'divEffect' );
	
	if( divEffect!=null )
	{	
		document.body.removeChild(divEffect);
	}
}

/**
 * Returns a query string with all element values in the select list with
 * the specified identifier.
 */
function getParamString( elementId )
{
    var list = document.getElementById( elementId );
    
    var params = "";
    
    for ( var i = 0; i < list.options.length; i++ )
    {
        params += elementId + "=" + list.options[i].value + "&";
    }
    
    return params;
}

/**
 * Creates an option and adds it to the list.
 * 
 * @param list the list.
 * @param optionValue the option value.
 * @param optionText the option text.
 */
function addOptionToList( list, optionValue, optionText )
{
    var option = document.createElement( "option" );
    option.value = optionValue;
    option.text = optionText;
    list.add( option, null );
}

/**
 * Returns a query string on the form <paramName>=<listValue> based on the options
 * in the list with the given identifier.
 * 
 * @param listId the list identifier.
 * @param paramName the name of the query param.
 * @return a query string.
 */
function getQueryStringFromList( listId, paramName )
{
	var list = document.getElementById( listId );
	
	var params = "";
	
	for ( var i = 0; i < list.options.length; i++ )
	{
		params += paramName + "=" + list.options[i].value + "&";
	}
	
	return params;
}

/**
 * Shows loader div and hides content div.
 */
function showLoader()
{
    $( "div#loaderDiv" ).show();
    $( "div#contentDiv" ).hide();
}

/**
 * Hides loader div and shows content div.
 */
function hideLoader()
{
    $( "div#loaderDiv" ).hide();
    $( "div#contentDiv" ).show();
}

/**
 * Deletes and removes an item from a table. The table row to be removed must have
 * an identifier on the form "tr[itemId]".
 * 
 * @param itemId the item identifier.
 * @param itemName the item name.
 * @param message the confirmation message.
 * @param action the server action url for deleting the item.
 */
function removeItem( itemId, itemName, confirmation, action )
{                
    var result = window.confirm( confirmation + "\n\n" + itemName );
    
    if ( result )
    {
    	$.getJSON(
    	    action,
    	    {
    	        "id": itemId   
    	    },
    	    function( json )
    	    {
    	    	if ( json.response == "success" )
    	    	{
    	    		$( "tr#tr" + itemId ).remove();
                
	                $( "table.listTable tbody tr" ).removeClass( "listRow listAlternateRow" );
	                $( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
	                $( "table.listTable tbody tr:even" ).addClass( "listRow" );
    	    	}
    	    	else if ( json.response == "error" )
    	    	{
    	    		setFieldValue( 'warningArea', json.message );
        
                    showWarning();
    	    	}
    	    }
    	);
    }
}

/**
 * Create jQuery datepicker for input text with id * * 
 * @param id the id of input filed which you want enter date *
 */
function datePicker( id )
{
	$("#" + id).datepicker(
	{
		dateFormat:dateFormat,
		changeMonth: true,
		changeYear: true,			
		monthNamesShort: monthNames,
		dayNamesMin: dayNamesMin,
		showOn: 'both',
		buttonImage: '../images/calendar.png',
		buttonImageOnly: true		
	});
	s = jQuery("#" + id );		
	if( s.val()=='' ) s.val( getCurrentDate() );	
	s.keypress(function(event) {return;});
}

/**
 * Create jQuery datepicker for input text with id * * 
 * @param id the id of input filed which you want enter date *
 */
function datePickerValid( id )
{
	jQuery("#" + id).datepicker(
	{
		dateFormat:dateFormat,
		changeMonth: true,
		changeYear: true,			
		monthNamesShort: monthNames,
		dayNamesMin: dayNamesMin,
		showOn: 'both',
		buttonImage: '../images/calendar.png',
		buttonImageOnly: true,
		maxDate: '+0d +0w'
	});
	
	s = jQuery("#" + id );		
	if( s.val()=='' ) s.val( getCurrentDate() );	
	s.keypress(function(event) {return;});
}


/**
 * Create jQuery datepicker for start date and end ate text with id * * 
 * @param startdate the id of input filed which you want enter start date *
 * @param enddate the id of input filed which you want enter end date *
 */

function datePickerInRange ( startdate, enddate )
{
	var dates = $('#'+startdate+', #' + enddate).datepicker(
	{
		dateFormat:dateFormat,
		defaultDate: "+1w",
		changeMonth: true,
		changeYear: true,
		numberOfMonths: 1,
		monthNamesShort: monthNames,
		dayNamesMin: dayNamesMin,
		showAnim:'',
		showOn: 'both',
		buttonImage: '../images/calendar.png',
		buttonImageOnly: true,	
		onSelect: function(selectedDate)
		{
			var option = this.id == startdate ? "minDate" : "maxDate";
			var instance = $(this).data("datepicker");
			var date = $.datepicker.parseDate(instance.settings.dateFormat || $.datepicker._defaults.dateFormat, selectedDate, instance.settings);
			dates.not(this).datepicker("option", option, date);
		}
	});
	s = jQuery("#" + startdate );
	e = jQuery("#" + enddate );
	
	if( s.val()=='' ) s.val( getCurrentDate() );
	if( e.val()=='' ) e.val( getCurrentDate() );	
	
	e.keypress(function(event) {return;});
	s.keypress(function(event) {return;});

}

function getCurrentDate()
{	
	return jQuery.datepicker.formatDate( dateFormat , new Date() ) ;
}

/**
 * Create input table id become sortable table * * 
 * @param tableId the id of table you want to sort * * 
 */

function tableSorter( tableId, sortList )
{
	if(sortList==undefined) sortList = [[0,0]];
	
	$("#" + tableId ).tablesorter({sortList:sortList}); 
}

function setSelectionRange( input, selectionStart, selectionEnd ) 
{
	if ( input.setSelectionRange ) 
	{
		input.focus();
	    input.setSelectionRange( selectionStart, selectionEnd );
	}
	else if ( input.createTextRange ) 
	{
	    var range = input.createTextRange();
	    range.collapse( true );
	    range.moveEnd( 'character', selectionEnd );
	    range.moveStart( 'character', selectionStart );
	    range.select();
	}
}

function setCaretToPos ( input, pos ) 
{
	setSelectionRange( input, pos, pos );
}

function insertTextCommon( inputAreaName, inputText )
{
	var inputArea = document.getElementById( inputAreaName );
	
	// IE support
	if ( document.selection ) 
	{
		inputArea.focus();
        sel = document.selection.createRange();
        sel.text = inputText;
        inputArea.focus();
	}
	// MOZILLA/NETSCAPE support
	else if ( inputArea.selectionStart || inputArea.selectionStart == '0' ) 
	{
	
		var startPos = inputArea.selectionStart;
		var endPos = inputArea.selectionEnd;
		
		var existingText = inputArea.value;
		var textBefore = existingText.substring( 0, startPos );
		var textAfter = existingText.substring( endPos, existingText.length );

		inputArea.value = textBefore + inputText + textAfter;
	}
	else 
	{
		inputArea.value += inputText;
		inputArea.focus();
	}

	setCaretToPos( inputArea, inputArea.value.length );
}

/**
 * Clock screen by mask  * 
 */
function lockScreen()
{
	jQuery.blockUI({ message: 'Please wait ... ', css: { 
		border: 'none', 
		padding: '15px', 
		backgroundColor: '#000', 
		'-webkit-border-radius': '10px', 
		'-moz-border-radius': '10px', 
		opacity: .5, 
		color: '#fff'			
	} }); 
}
/**
 * unClock screen   * 
 */
function unLockScreen()
{
	jQuery.unblockUI();
}

function showErrorMessage( message, time )
{
	jQuery.growlUI( i18n_error, message, 'error', time ); 	
}

function showSuccessMessage( message, time )
{
	jQuery.growlUI( i18n_success, message, 'success', time ); 	
}

function showWarningMessage( message, time )
{
	jQuery.growlUI( i18n_warning, message, 'warning', time ); 	
}

/**
 * Create validator for fileds in form  * 
 */
 
function validation( formId, submitHandler, beforeValidateHandler )
{
	var nameField = jQuery('#' + formId + ' input[type=text]')[0];

	var validator = jQuery("#" + formId ).validate({
		 meta:"validate"
		,errorElement:"span"
		,beforeValidateHandler:beforeValidateHandler
		,submitHandler: submitHandler
	});
	
	if ( nameField )
	{
		nameField.focus();
	}
	
	return validator;
}
/**
 * Add validation rule remote for input field
* @param inputId is id for input field
* @param url is ajax request url
* @param params is array of param will send to server by ajax request
 */
function checkValueIsExist( inputId, url, params )
{
	jQuery("#" + inputId).rules("add",{
		remote: {
			url:url,
			type:'post',
			data:params
		}
	});
}
