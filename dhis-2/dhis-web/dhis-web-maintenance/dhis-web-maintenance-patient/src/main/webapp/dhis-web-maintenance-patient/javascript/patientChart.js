
function showPatientChartDetails( patientChartId )
{
	jQuery.postJSON( "getPatientChart.action", {
			id:patientChartId 
		}, function(json){
			setInnerHTML( 'titleField', json.title );

			var typeMap = {
				'bar' : i18n_bar_chart,
				'bar3d' : i18n_bar3d_chart,
				'line' : i18n_line_chart,
				'line3d' : i18n_line3d_chart
			};
			var type = json.type;
			setInnerHTML( 'typeField', typeMap[type] );

			var sizeMap = {
				'normal' : i18n_normal,
				'wide' : i18n_wide,
				'tall' : i18n_tall
			};
			var size = json.size;
			setInnerHTML( 'sizeField', sizeMap[size] );
			
			var regression = ( json.regression == 'true') ? i18n_yes : i18n_no;
		
			setInnerHTML( 'regressionField', regression );
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