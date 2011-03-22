jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			alphanumericwithbasicpuncspaces : r.dataSet.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.dataSet.name.firstletteralphabet,
			range : r.dataSet.name.range
		},
		shortName : {
			required : true,
			alphanumericwithbasicpuncspaces : r.dataSet.shortName.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.dataSet.shortName.firstletteralphabet,
			range : r.dataSet.shortName.range
		},
		code : {
			alphanumericwithbasicpuncspaces : r.dataSet.code.alphanumericwithbasicpuncspaces,
			notOnlyDigits : r.dataSet.code.notOnlyDigits,
			range : r.dataSet.code.range
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

	jQuery( "#name" ).attr( "maxlength", r.dataSet.name.range[1] );
	jQuery( "#shortName" ).attr( "maxlength", r.dataSet.shortName.range[1] );
	jQuery( "#code" ).attr( "maxlength", r.dataSet.code.range[1] );

	checkValueIsExist( "name", "validateDataSet.action" );
	checkValueIsExist( "shortName", "validateDataSet.action" );
	checkValueIsExist( "code", "validateDataSet.action" );
} );
