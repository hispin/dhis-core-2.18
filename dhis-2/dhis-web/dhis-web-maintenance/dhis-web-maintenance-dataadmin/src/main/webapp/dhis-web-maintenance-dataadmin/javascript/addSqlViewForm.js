jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name: {
			required: true,
			rangelength: r.sqlView.name.rangelength
		},
		description: {
			required: true,
			rangelength: r.sqlView.description.rangelength
		},
		sqlquery: {
			required: true,
			rangelength: r.sqlView.sqlquery.rangelength
		}
	};

	validation2( 'addSqlViewForm', function() {
		validateAddUpdateSqlView( 'add' );
	}, {
		'rules': rules
	});

	jQuery("#name").attr("maxlength", r.sqlView.name.rangelength[1]); 
	jQuery("#description").attr("maxlength", r.sqlView.description.rangelength[1]); 
	jQuery("#sqlquery").attr("maxlength", r.sqlView.sqlquery.rangelength[1]); 
});
