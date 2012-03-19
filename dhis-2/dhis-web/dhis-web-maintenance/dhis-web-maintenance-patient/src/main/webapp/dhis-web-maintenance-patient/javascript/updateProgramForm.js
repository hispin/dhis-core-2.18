jQuery(document).ready(	function(){

	validation( 'updateProgramForm', function( form ){ 
		enable('dateOfEnrollmentDescription');
		form.submit();			
	});	
	
	checkValueIsExist( "name", "validateProgram.action", {id:getFieldValue('id')});
});	
