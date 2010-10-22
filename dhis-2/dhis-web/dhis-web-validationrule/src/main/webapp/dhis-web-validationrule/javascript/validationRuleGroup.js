
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showValidationRuleGroupDetails( id )
{
    var request = new Request();
    request.setResponseTypeXML( 'validationRuleGroup' );
    request.setCallbackSuccess( validationRuleGroupReceived );
    request.send( 'getValidationRuleGroup.action?id=' + id );
}

function validationRuleGroupReceived( xmlObject )
{
    setInnerHTML( 'nameField', getElementValue( xmlObject, 'name' ) );
    setInnerHTML( 'descriptionField', getElementValue( xmlObject, 'description' ) );
    setInnerHTML( 'memberCountField', getElementValue( xmlObject, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove data element group
// -----------------------------------------------------------------------------

function removeValidationRuleGroup( validationRuleGroupId, validationRuleGroupName )
{
	removeItem( validationRuleGroupId, validationRuleGroupName, i18n_confirm_delete, 'removeValidationRuleGroup.action' );
}

// -----------------------------------------------------------------------------
// Add data element group
// -----------------------------------------------------------------------------

function validateAddValidationRuleGroup()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateValidationRuleGroup.action?name=' + 
        getFieldValue( 'name' ) + "&description=" + getFieldValue( 'description' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var availableValidationRules = document.getElementById( 'availableValidationRules' );
        availableValidationRules.selectedIndex = -1;
        
        var groupMembers = document.getElementById( 'groupMembers' );
        for ( var i = 0; i < groupMembers.options.length; ++i )
        {
            groupMembers.options[i].selected = true;
        }
        
        var form = document.getElementById( 'addValidationRuleGroupForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_validation_rule_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

function getFieldValue( fieldId )
{
    return document.getElementById( fieldId ).value;
}

// -----------------------------------------------------------------------------
// Update data element group
// -----------------------------------------------------------------------------

function validateUpdateValidationRuleGroup()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateValidationRuleGroup.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) + '&description=' + getFieldValue( 'description' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var availableValidationRules = document.getElementById( 'availableValidationRules' );
        availableValidationRules.selectedIndex = -1;
        
        var groupMembers = document.getElementById( 'groupMembers' );
        for ( var i = 0; i < groupMembers.options.length; ++i )
        {
            groupMembers.options[i].selected = true;
        }

        var form = document.getElementById( 'updateValidationRuleGroupForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_validation_rule_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Select lists
// -----------------------------------------------------------------------------

function initLists()
{
    var id;
    for ( id in groupMembers )
    {
        $("#groupMembers").append( $( "<option></option>" ).attr( "value",id ).text( groupMembers[id] )) ;
    }

    for ( id in availableValidationRules )
    {
        $("#availableValidationRules").append( $( "<option></option>" ).attr( "value",id ).text( availableValidationRules[id] )) ;
    }
}

function filterGroupMembers()
{
    var filter = document.getElementById( 'groupMembersFilter' ).value;
    var list = document.getElementById( 'groupMembers' );
    
    list.options.length = 0;
    
    for ( var id in groupMembers )
    {
        var value = groupMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableValidationRules()
{
    var filter = document.getElementById( 'availableValidationRulesFilter' ).value;
    var list = document.getElementById( 'availableValidationRules' );
    
    list.options.length = 0;
    
    for ( var id in availableValidationRules )
    {
        var value = availableValidationRules[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addGroupMembers()
{
    var list = document.getElementById( 'availableValidationRules' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        groupMembers[id] = availableValidationRules[id];
        
        delete availableValidationRules[id];        
    }
    
    filterGroupMembers();
    filterAvailableValidationRules();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'groupMembers' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableValidationRules[id] = groupMembers[id];
        
        delete groupMembers[id];        
    }
    
    filterGroupMembers();
    filterAvailableValidationRules();
}
