function showReportTableGroupDetails( reportTableGroupId )
{
    jQuery.post( 'getReportTableGroup.action', { id: reportTableGroupId },
		function ( json ) {
			setInnerHTML( 'nameField', json.reportTableGroup.name );
			setInnerHTML( 'memberCountField', json.reportTableGroup.memberCount );

			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove report table group
// -----------------------------------------------------------------------------

function removeReportTableGroup( reportTableGroupId, reportTableGroupName )
{
    removeItem( reportTableGroupId, reportTableGroupName, i18n_confirm_delete, 'removeReportTableGroup.action' );
}
