jQuery(document).ready(	function(){
	validation( 'updatePatientChartForm', function( form ){			
		form.submit();
	});			

	checkValueIsExist( "title", "validatePatientChart.action", {id:'$patientChart.id'});	
});		
