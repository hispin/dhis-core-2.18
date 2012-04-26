// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showInventoryTypeDetails( inventoryTypeId )
{
	jQuery.getJSON( 'getInventoryType.action', { id: inventoryTypeId },
		function ( json ) {
			setInnerHTML( 'nameField', json.inventoryType.name );	
			setInnerHTML( 'descriptionField', json.inventoryType.description );
			
			var tracking = ( json.inventoryType.tracking == 'true') ? i18n_yes : i18n_no;
			setInnerHTML( 'trackingField', tracking );
			
			setInnerHTML( 'catalogTypeField', json.inventoryType.catalogType );    
	   
			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove InvenotryType
// -----------------------------------------------------------------------------
function removeInventoryType( invenotryTypeId, name )
{
	removeItem( invenotryTypeId, name, i18n_confirm_delete, 'removeInventoryType.action' );	
}

