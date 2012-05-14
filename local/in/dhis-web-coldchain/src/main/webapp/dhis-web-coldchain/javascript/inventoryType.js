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

//-----------------------------------------------------------------------------
//Move Table Row Up and Down
//-----------------------------------------------------------------------------

/**
* Moves the selected option in a select list up one position.
* 
* @param listId the id of the list.
*/
function moveUpSelectedOption( listId ){
  var list = document.getElementById( listId );
  for ( var i = 0; i < list.length; i++ ) {
      if ( list.options[ i ].selected ) {
          if ( i > 0 ) {	// Cannot move up the option at the top
              var precedingOption = new Option( list.options[ i - 1 ].text, list.options[ i - 1 ].value );
              var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );

              list.options[ i - 1 ] = currentOption; // Swapping place in the
                                                      // list
              list.options[ i - 1 ].selected = true;
              list.options[ i ] = precedingOption;
          }
      }
  }
}
/**
* Moves the selected option in a list down one position.
* 
* @param listId the id of the list.
*/
function moveDownSelectedOption( listId ) {
  var list = document.getElementById( listId );

  for ( var i = list.options.length - 1; i >= 0; i-- ) {
      if ( list.options[ i ].selected ) {
          if ( i < list.options.length - 1 ) { 	// Cannot move down the
                                                  // option at the bottom
              var subsequentOption = new Option( list.options[ i + 1 ].text, list.options[ i + 1 ].value );
              var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );

              list.options[ i + 1 ] = currentOption; // Swapping place in the
                                                      // list
              list.options[ i + 1 ].selected = true;
              list.options[ i ] = subsequentOption;
          }
      }
  }
}
