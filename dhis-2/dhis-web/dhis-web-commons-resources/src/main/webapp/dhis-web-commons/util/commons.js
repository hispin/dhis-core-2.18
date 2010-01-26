
/**
 * Redirects to the translate GUI.
 * 
 * @param className the name of the object class.
 * @param objectId the identifier of the object.
 */
function translate( className, objectId )
{
    var url = "../dhis-web-commons/i18n.action?className=" + className + "&objectId=" + objectId + "&returnUrl=" + window.location.href; 
    
    window.location.href = url; 
}

/**
 * Filters values in a html table with tbody id "list".
 * 
 * @param filter the filter.
 */
function filterValues( filter )
{
    var list = document.getElementById( 'list' );
    
    var rows = list.getElementsByTagName( 'tr' );
    
    for ( var i = 0; i < rows.length; ++i )
    {
        var cell = rows[i].getElementsByTagName( 'td' )[0];
        
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
 * @param elementId the element identifier.
 */
function hideById( elementId )
{
  document.getElementById( elementId ).style.display = "none";
}

/**
 * Shows the document element with the given identifier.
 * 
 * @param elementId the element identifier.
 */
function showById( elementId )
{
  document.getElementById( elementId ).style.display = "block";
}

/**
 * Returns true if the element with the given identifier has text, false if not.
 * 
 * @param inputId the identifier of the input element.
 */
function hasText( inputId )
{
    return document.getElementById( inputId ).value != "";
}

/**
 * Returns true if the element with the given identifier is checked, false if not
 * or if it does not exist.
 * 
 * @param checkboxId the identifier of the checkbox element.
 */
function isChecked( checkboxId )
{
    var checkBox = document.getElementById( checkboxId );
    
    if ( checkBox )
    {
        return checkBox.checked;
    }
    
    return false;
}

/**
 * Checks the checkbox with the given identifier if the checkbox exists.
 */
function check( checkBoxId )
{
    var checkBox = document.getElementById( checkBoxId );
    
    if ( checkBox )
    {
        checkBox.checked = true;
    }
}

/**
 * Unchecks the checkbox with the given identifier if the checkbox exists.
 */
function uncheck( checkBoxId )
{
    var checkBox = document.getElementById( checkBoxId );
    
    if ( checkBox )
    {
        checkBox.checked = false;
    }
}

/**
 * Enables the element with the given identifier if the element exists.
 */
function enable( elementId )
{
    var element = document.getElementById( elementId );
    
    if ( element )
    {
        element.disabled = false;
    }
}

/**
 * Disables the element with the given identifier if the element exists.
 */
function disable( elementId )
{
    var element = document.getElementById( elementId );
    
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
    return document.getElementById( listId ).options.length > 0;
}

/**
 * Returns true if the element with the given identifier exists, false if not.
 * 
 * @param elementId the identifier of the element.
 */
function isNotNull( elementId )
{
    return document.getElementById( elementId ) != null ? true : false;
}

/**
 * HTML encodes the given string.
 * 
 * @param str the input string.
 * @return the HTML encoded string.
 */
function htmlEncode( str )
{
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
function getElementValue( parentElement, childElementName )
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
 * Gets a value from the given element.
 * 
 * @param fieldId the identifier of the element.
 * @return a value corresponding to the given identifier.
 */
function getFieldValue( fieldId )
{
    return document.getElementById( fieldId ).value;
}

/**
 * Sets a message in the "message" span and makes the span visible.
 * 
 * @param message the message. 
 */
function setMessage( message )
{
    document.getElementById( 'message' ).innerHTML = message;   
    document.getElementById( 'message' ).style.display = 'block';
}

/**
 * Makes the "message" span invisible.
 */
function hideMessage()
{
    document.getElementById( 'message' ).style.display = 'none';
}

function setInfo( message )
{
    document.getElementById( 'info' ).innerHTML = message;
    document.getElementById( 'info' ).style.display = 'block';
}

function hideInfo()
{
    document.getElementById( 'info' ).style.display = 'block';
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
	
	if(divEffect!=null){	
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
    $( "#loaderDiv" ).css({ display: "block" });
    $( "#contentDiv" ).css({ display: "none" });
}

/**
 * Hides loader div and shows content div.
 */
function hideLoader()
{
    $( "#loaderDiv" ).css({ display: "none" });
    $( "#contentDiv" ).css({ display: "block" });
}
