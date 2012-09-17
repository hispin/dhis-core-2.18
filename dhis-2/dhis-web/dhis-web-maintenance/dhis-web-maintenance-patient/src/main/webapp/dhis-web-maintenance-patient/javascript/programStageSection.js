
function programStageSectionList( programStageId )
{
	window.location.href = "programStage.action?id=" + programId;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showSectionDetails( sectionId )
{
	jQuery.getJSON( 'getProgramStageSection.action', { id: sectionId }, function ( json ) {
		setInnerHTML( 'nameField', json.programStageSection.name );	
		setInnerHTML( 'dataElementCountField', json.programStageSection.dataElementCount ); 
		showDetails();
	});
}

function removeSection( programStageId, sectionId, name )
{
	var result = window.confirm( i18n_confirm_delete + "\n" + name );
    if ( result )
    {
		jQuery.getJSON( "removeProgramStageSection.action",
			{
				programStageId:programStageId,
				id:sectionId
			}, 
			function( json ) 
			{   
				jQuery( "tr#tr" + sectionId ).remove();
				jQuery( "table.listTable tbody tr" ).removeClass( "listRow listAlternateRow" );
				jQuery( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
				jQuery( "table.listTable tbody tr:even" ).addClass( "listRow" );
				jQuery( "table.listTable tbody" ).trigger("update");
				
				showSuccessMessage( i18n_delete_success );
			});
	}
}
