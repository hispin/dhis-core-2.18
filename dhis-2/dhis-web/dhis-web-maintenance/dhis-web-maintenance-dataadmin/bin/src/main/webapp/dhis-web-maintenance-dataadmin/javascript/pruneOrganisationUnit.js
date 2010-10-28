
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
	            document.getElementById( "keepNameField" ).innerHTML = json.organisationUnit.name;
	            document.getElementById( "pruneButton" ).disabled = false; 
				hideById('message');
	        }
	    );
	}
}

function pruneOrganisationUnit() 
{
	var result = window.confirm(i18n_confirmation);

	if (result) {
		setWaitMessage(i18n_pruning + "...");

		$.ajax( {
			"url" : "pruneOrganisationUnit.action",
			"data" : {
				"organisationUnitSelected" : organisationUnitSelected
			},
			"success" : function() {
				setMessage(i18n_pruning_done);
			}
		});
	}
}
