
var organisationUnitSelected = 0;

function treeSelected( unitIds )
{
	organisationUnitSelected = unitIds[0];
	
	if ( organisationUnitSelected != null && organisationUnitSelected != 0 )
	{
		$.getJSON( 
	        "../dhis-web-commons-ajax-json/getOrganisationUnit.action",
	        {
	            "id": organisationUnitSelected
	        },
	        function( json )
	        {
	            setInnerHTML( 'keepNameField', json.organisationUnit.name );
	            enable( 'pruneButton' );
				hideHeaderMessage( 'message' );
	        }
	    );
	}
	else
	{
		setInnerHTML( 'keepNameField', i18n_not_selected );
	    disable( 'pruneButton' );
	}
}

function pruneOrganisationUnit() 
{
	var result = window.confirm(i18n_confirmation);

	if ( result ) {
	
		setHeaderWaitMessage( i18n_pruning + "..." );

		$.getJSON(
			"pruneOrganisationUnit.action", {},
			function( json )
			{
				hideHeaderMessage( 'message' );
				
				if ( json.response == "success" )
				{	
					showSuccessMessage( i18n_pruning_done );
					setTimeout( "window.location.href='displayPruneOrganisationUnitForm.action'", 2500);
				}
				else
				{
					showErrorMessage( i18n_pruning_interrupted );
				}
			}
		);
	}
}
