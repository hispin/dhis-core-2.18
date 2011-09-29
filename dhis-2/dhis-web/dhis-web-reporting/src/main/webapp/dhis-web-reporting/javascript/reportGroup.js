function showReportGroupDetails( reportGroupId )
{
    jQuery.post( 'getReportGroup.action', { id: reportGroupId }, function ( json ) {
		setInnerHTML( 'nameField', json.reportGroup.name );
		setInnerHTML( 'memberCountField', json.reportGroup.memberCount );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove report group
// -----------------------------------------------------------------------------

function removeReportGroup( reportGroupId, reportGroupName )
{
    removeItem( reportGroupId, reportGroupName, i18n_confirm_delete, 'removeReportGroup.action' );
}
