jQuery(document).ready( function() {
	var r = getValidationRules();
	
	var rules = {
		selectedPeriods: {
			required: true
		},
		selectedDataSets: {
			required: true
		}
	};

	validation2( 'lockingForm', function( form )
	{
		validateCollectiveDataLockingForm( form );
	}, {
		'rules': rules
	});
});
