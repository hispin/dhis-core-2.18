jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			alphanumericwithbasicpuncspaces : r.dataSet.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.dataSet.name.firstletteralphabet,
			rangelength : r.dataSet.name.rangelength
		},
		shortName : {
			required : true,
			alphanumericwithbasicpuncspaces : r.dataSet.shortName.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.dataSet.shortName.firstletteralphabet,
			rangelength : r.dataSet.shortName.rangelength
		},
		code : {
			alphanumericwithbasicpuncspaces : r.dataSet.code.alphanumericwithbasicpuncspaces,
			notOnlyDigits : r.dataSet.code.notOnlyDigits,
			rangelength : r.dataSet.code.rangelength
		},
		frequencySelect : {
			required : true
		}
	};

	validation2( 'editDataSetForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			selectAllById( 'selectedList' )
		},
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.dataSet.name.rangelength[1] );
	jQuery( "#shortName" ).attr( "maxlength", r.dataSet.shortName.rangelength[1] );
	jQuery( "#code" ).attr( "maxlength", r.dataSet.code.rangelength[1] );

	checkValueIsExist( "name", "validateDataSet.action", {
		dataSetId : function()
		{
			return jQuery( "#dataSetId" ).val();
		}
	} );

	checkValueIsExist( "shortName", "validateDataSet.action", {
		dataSetId : function()
		{
			return jQuery( "#dataSetId" ).val();
		}
	} );

	checkValueIsExist( "code", "validateDataSet.action", {
		dataSetId : function()
		{
			return jQuery( "#dataSetId" ).val();
		}
	} );
} );
