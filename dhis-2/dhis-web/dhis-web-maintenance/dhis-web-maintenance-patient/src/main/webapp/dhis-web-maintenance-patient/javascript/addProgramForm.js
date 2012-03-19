jQuery(document).ready(	function(){
	validation( 'addProgramForm', function( form ){		
		enable('dateOfEnrollmentDescription');
		form.submit();
	});				
	
	checkValueIsExist( "name", "validateProgram.action");
});	