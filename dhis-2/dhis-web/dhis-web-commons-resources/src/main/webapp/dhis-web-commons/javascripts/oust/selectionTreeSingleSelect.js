
var selectedOrganisationUnitListABCDEF;

jQuery(document).ready( function() {

});

function addSelectedOrganisationUnitABCDEF( id )
{
	selectedOrganisationUnitListABCDEF.append( '<option value="' + id + ' selected="selected">' + id + '</option>');
}	

function selectOrganisationUnitABCDEF( ids )
{
	selectedOrganisationUnitListABCDEF.empty();
	
	jQuery.each( ids, function(i, item ) {
		selectedOrganisationUnitListABCDEF.append( '<option value="' + item + ' selected="selected">' + item + '</option>');
	});
}
