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
	setInnerHTML( 'nameField', getElementValue( unitElement, 'name' ) );
    setInnerHTML( 'descriptionField', getElementValue( unitElement, 'description' ) );
    
    var compulsory = getElementValue( unitElement, 'compulsory' );
        
    if ( compulsory == "true" )
    {
    	setInnerHTML( 'compulsoryField', i18n_yes );
    }
    else
    {
    	setInnerHTML( 'compulsoryField', i18n_no );
    }
        
    setInnerHTML( 'memberCountField', getElementValue( unitElement, 'memberCount' ) );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove organisation unit group set
// -----------------------------------------------------------------------------

function removeOrganisationUnitGroupSet( groupSetId, groupSetName )
{
	removeItem( groupSetId, groupSetName, confirm_to_delete_org_unit_group_set, 'removeOrganisationUnitGroupSet.action' );
}

function changeCompulsory( value )
{
	if( value == 'true' ){
		addValidatorRulesById( 'selectedGroups', {required:true} );
	}else{
		removeValidatorRulesById( 'selectedGroups' );
	}
}

function validateAddOrganisationGroupSet( form )
{
	var url = "validateOrganisationUnitGroupSet.action?";
		url += getParamString( 'selectedGroups', 'selectedGroups' );

	jQuery.postJSON( url, function( json )
	{
		if( json.response == 'success' ){
			markValid( 'selectedGroups' );
			selectAllById( 'selectedGroups' );
			form.submit();
		}else{
			markInvalid( 'selectedGroups', json.message );				
		}
	});		
}