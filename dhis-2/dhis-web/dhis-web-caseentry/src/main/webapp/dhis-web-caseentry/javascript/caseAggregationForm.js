jQuery(document).ready(	function() {
	var rules = {
		dataSetId: {
			required:true
		},
		sDateLB: {
			required:true
		},
		eDateLB: {
			required:true
		}
	};
	
	validation2( 'caseAggregationForm', function(form) {
		if(isSubmit) form.submit();
	}, {
		'beforeValidateHandler': function() {
			var periodFrom = jQuery( '#sDateLB' ).val();
			var periodTo = jQuery( '#eDateLB' ).val();
			
			if(periodFrom > periodTo){
				byId('warningMessage').innerHTML = i18n_greater_then_from_date;
				isSubmit = false;
			} else {
				isSubmit = true;
			}
		},
		'rules': rules
	})
}); 
