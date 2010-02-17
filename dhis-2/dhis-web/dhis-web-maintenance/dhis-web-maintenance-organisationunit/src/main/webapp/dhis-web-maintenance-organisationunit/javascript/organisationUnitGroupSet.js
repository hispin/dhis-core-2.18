/*
 * Depends on dhis-web-commons/lists/lists.js for List functionality
 */

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitGroupSetDetails( groupSetId )
{
	var request = new Request();
    request.setResponseTypeXML( 'organisationUnitGroupSet' );
    request.setCallbackSuccess( organisationUnitGroupSetReceived );
    request.send( 'getOrganisationUnitGroupSet.action?id=' + groupSetId );
}

function organisationUnitGroupSetReceived( unitElement )
{
	setFieldValue( 'nameField', getElementValue( unitElement, 'name' ) );
    setFieldValue( 'descriptionField', getElementValue( unitElement, 'description' ) );
    
    var compulsory = getElementValue( unitElement, 'compulsory' );
        
    if ( compulsory == "true" )
    {
    	setFieldValue( 'compulsoryField', i18n_yes );
    }
    else
    {
    	setFieldValue( 'compulsoryField', i18n_no );
    }
        
    setFieldValue( 'memberCountField', getElementValue( unitElement, 'memberCount' ) );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove organisation unit group set
// -----------------------------------------------------------------------------

function removeOrganisationUnitGroupSet( groupSetId, groupSetName )
{
	removeItem( groupSetId, groupSetName, confirm_to_delete_org_unit_group_set, 'removeOrganisationUnitGroupSet.action' );
}

// -----------------------------------------------------------------------------
// Add organisation unit group set
// -----------------------------------------------------------------------------

function validateAddOrganisationUnitGroupSet()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    
    var params = 'name=' + getFieldValue( 'name' ) +
        '&description=' + getFieldValue( 'description' ) +
        '&compulsory=' + getFieldValue( 'compulsory' ) + '&';
        
    var selectedGroups = document.getElementById( 'selectedGroups' );
    
    for ( var i = 0; i < selectedGroups.options.length; i++ )
    {
    	params += 'selectedGroups=' + selectedGroups.options[i].value + '&';
    }
    
    var url = 'validateOrganisationUnitGroupSet.action';
    
    request.sendAsPost( params );    
    request.send( url );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	selectAllById( 'selectedGroups' );
    	
        var form = document.getElementById( 'addOrganisationUnitGroupSetForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( adding_the_org_unit_group_set_failed + ':\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update organisation unit group set
// -----------------------------------------------------------------------------

function validateUpdateOrganisationUnitGroupSet()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    
    var params = 'id=' + getFieldValue( 'id' ) +
    	'&name=' + getFieldValue( 'name' ) +
        '&description=' + getFieldValue( 'description' ) +
        '&compulsory=' + getFieldValue( 'compulsory' ) + '&';
    
    var selectedGroups = document.getElementById( 'selectedGroups' );
    
    for ( var i = 0; i < selectedGroups.options.length; i++ )
    {
    	params += 'selectedGroups=' + selectedGroups.options[i].value + '&';
    }
    
    var url = 'validateOrganisationUnitGroupSet.action';
    
    request.sendAsPost( params );    
    request.send( url );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	selectAllById( 'selectedGroups' );
    	
        var form = document.getElementById( 'updateOrganisationUnitGroupSetForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( saving_the_org_unit_group_set_failed + ':\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
