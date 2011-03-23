jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			alphanumericwithbasicpuncspaces : r.dataSet.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.dataSet.name.firstletteralphabet,
			rangelength : r.dataSet.name.length
		},
		shortName : {
			required : true,
			alphanumericwithbasicpuncspaces : r.dataSet.shortName.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.dataSet.shortName.firstletteralphabet,
			rangelength : r.dataSet.shortName.length
		},
		code : {
			alphanumericwithbasicpuncspaces : r.dataSet.code.alphanumericwithbasicpuncspaces,
			notOnlyDigits : r.dataSet.code.notOnlyDigits,
			rangelength : r.dataSet.code.length
		},
		frequencySelect : {
			required : true
		}
	};

	validation2( 'addDataSetForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			selectAllById( 'selectedList' )
		},
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.dataSet.name.length[1] );
	jQuery( "#shortName" ).attr( "maxlength", r.dataSet.shortName.length[1] );
	jQuery( "#code" ).attr( "maxlength", r.dataSet.code.length[1] );

	checkValueIsExist( "name", "validateDataSet.action" );
	checkValueIsExist( "shortName", "validateDataSet.action" );
	checkValueIsExist( "code", "validateDataSet.action" );
} );
