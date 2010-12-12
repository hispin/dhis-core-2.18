
function initOrgUnitDetails()
{
	$( "#organisationUnitDetails" ).dialog( {
		modal:true,
		autoOpen:false,
		width:600,
		height:600
	} );
}

function showOrgUnitDetails( id )
{
	$( "#organisationUnitDetails" ).load( "getOrganisationUnitDetails.action?id=" + id, function() {
		$( "#organisationUnitDetails" ).dialog( "open" );
	} );
}
