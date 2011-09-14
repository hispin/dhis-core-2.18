// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRelationshipTypeDetails( relationshipTypeId )
{
  	$.ajax({
		url: 'getRelationshipType.action?id=' + relationshipTypeId,
		cache: false,
		dataType: "xml",
		success: relationshipTypeReceived
	});
}

function relationshipTypeReceived( relationshipTypeElement )
{
	setInnerHTML( 'idField', getElementValue( relationshipTypeElement, 'id' ) );
	setInnerHTML( 'aIsToBField', getElementValue( relationshipTypeElement, 'aIsToB' ) );	
	setInnerHTML( 'bIsToAField', getElementValue( relationshipTypeElement, 'bIsToA' ) );       
	setInnerHTML( 'descriptionField', getElementValue( relationshipTypeElement, 'description' ) );
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Add RelationshipType
// -----------------------------------------------------------------------------

function validateAddRelationshipType()
{
	$.postJSON(
    	    'validateRelationshipType.action',
    	    {
    	        "aIsToB": getFieldValue( 'aIsToB' ),
				"bIsToA": getFieldValue( 'bIsToA' )
    	    },
    	    function( json )
    	    {
    	    	if ( json.response == "success" )
    	    	{
					var form = document.getElementById( 'addRelationshipTypeForm' );        
					form.submit();
    	    	}else if ( json.response == "input" )
    	    	{
    	    		setHeaderMessage( json.message );
    	    	}
    	    	else if ( json.response == "error" )
    	    	{
    	    		setHeaderMessage( "i18n_adding_patient_atttibute_failed + ':' + '\n'" +json.message );
    	    	}
    	    }
    	);
}

// -----------------------------------------------------------------------------
// Update RelationshipType
// -----------------------------------------------------------------------------

function validateUpdateRelationshipType()
{
	$.postJSON(
    	    'validateRelationshipType.action',
    	    {
				"id": getFieldValue( 'id' ),
    	        "aIsToB": getFieldValue( 'aIsToB' ),
				"bIsToA": getFieldValue( 'bIsToA' )
    	    },
    	    function( json )
    	    {
    	    	if ( json.response == "success" )
    	    	{
					var form = document.getElementById( 'updateRelationshipTypeForm' );        
					form.submit();
    	    	}else if ( json.response == "input" )
    	    	{
    	    		setHeaderMessage( json.message );
    	    	}
    	    	else if ( json.response == "error" )
    	    	{
    	    		setHeaderMessage( "i18n_adding_patient_atttibute_failed + ':' + '\n'" +json.message );
    	    	}
    	    }
    	);
}

// -----------------------------------------------------------------------------
// Remove RelationshipType
// -----------------------------------------------------------------------------	

function removeRelationshipType( relationshipTypeId, aIsToB, bIsToA )
{
    removeItem( relationshipTypeId, aIsToB + "/" + bIsToA, i18n_confirm_delete, 'removeRelationshipType.action' );
}
