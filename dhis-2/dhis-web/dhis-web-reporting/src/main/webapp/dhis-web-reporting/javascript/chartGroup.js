function showChartGroupDetails( chartGroupId )
{
    jQuery.post( 'getChartGroup.action', { id: chartGroupId }, function ( json ) {
		setInnerHTML( 'nameField', json.chartGroup.name );
		setInnerHTML( 'memberCountField', json.chartGroup.memberCount );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove chart group
// -----------------------------------------------------------------------------

function removeChartGroup( chartGroupId, chartGroupName )
{
    removeItem( chartGroupId, chartGroupName, i18n_confirm_delete, 'removeChartGroup.action' );
}
