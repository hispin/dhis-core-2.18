
function showPatientChartDetails( patientChartId )
{
	jQuery.postJSON( "getPatientChart.action", {
			id:patientChartId 
		}, function(json){
			setInnerHTML( 'idField', json.id );
			setInnerHTML( 'titleField', json.title );	
			setInnerHTML( 'typeField', json.type );
			setInnerHTML( 'sizeField', json.size );
			setInnerHTML( 'regressionField', json.regression );
			setInnerHTML( 'dataElementField', json.dataElement );
			showDetails();
		});
}

// -----------------------------------------------------------------------------
// Remove Patient Identifier Type
// -----------------------------------------------------------------------------

function removePatientChart( patientChartId, name )
{
    removeItem( patientChartId, name, i18n_confirm_delete, 'removePatientChart.action' );
}