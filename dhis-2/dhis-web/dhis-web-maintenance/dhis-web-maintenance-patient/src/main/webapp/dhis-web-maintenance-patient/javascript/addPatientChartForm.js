jQuery(document).ready(	function(){
	validation( 'addPatientChartForm', function( form ){			
		form.submit();
	});	
	checkValueIsExist( "title", "validatePatientChart.action");
});	