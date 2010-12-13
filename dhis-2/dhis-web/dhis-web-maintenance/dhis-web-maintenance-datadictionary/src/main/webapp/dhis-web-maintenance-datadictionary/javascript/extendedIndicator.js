
// -----------------------------------------------------------------------------
// Add indicator
// -----------------------------------------------------------------------------

function validateAddExtendedIndicator()
{
	
	jQuery.postJSON("validateExtendedIndicator.action",{
		name: getFieldValue( 'name' ),
		shortName: getFieldValue( 'shortName' ),
		code: getFieldValue( 'code' ),
		description: getFieldValue( 'description' ),
		indicatorTypeId: getFieldValue( 'indicatorTypeId' ),
		numeratorDescription: getFieldValue( 'numeratorDescription' ),
		denominatorDescription: getFieldValue( 'denominatorDescription' ),
		mnemonic: getFieldValue( 'mnemonic' ),
		version: getFieldValue( 'version' ),
		keywords: getFieldValue( 'keywords' ),
		minimumSize: getFieldValue( 'minimumSize' ),
		maximumSize: getFieldValue( 'maximumSize' ),
		responsibleAuthority: getFieldValue( 'responsibleAuthority' ),
		location: getFieldValue( 'location' ),
		reportingMethods: getFieldValue( 'reportingMethods' ),
		versionStatus: getFieldValue( 'versionStatus' )
	},function( json ){
		if ( json.response == 'success' )
		{
			var form = document.getElementById( 'addExtendedIndicatorForm' );
			form.submit();
		}
		else if ( json.response == 'error' )
		{
			window.alert( i18n_adding_indicator_failed + ':' + '\n' + json.message );
		}
		else if ( json.response == 'input' )
		{
			document.getElementById( 'message' ).innerHTML = json.message;
			document.getElementById( 'message' ).style.display = 'block';
		}
	});
	
	
}

// -----------------------------------------------------------------------------
// Update data element
// -----------------------------------------------------------------------------

function validateUpdateExtendedIndicator()
{

	jQuery.postJSON("validateExtendedIndicator.action",{
		id: getFieldValue( 'id' ),
		name: getFieldValue( 'name' ),
		shortName: getFieldValue( 'shortName' ),
		code: getFieldValue( 'code' ),
		description: getFieldValue( 'description' ),
		indicatorTypeId: getFieldValue( 'indicatorTypeId' ),
		numeratorDescription: getFieldValue( 'numeratorDescription' ),
		denominatorDescription: getFieldValue( 'denominatorDescription' ),
		mnemonic: getFieldValue( 'mnemonic' ),
		version: getFieldValue( 'version' ),
		keywords: getFieldValue( 'keywords' ),
		minimumSize: getFieldValue( 'minimumSize' ),
		maximumSize: getFieldValue( 'maximumSize' ),
		responsibleAuthority: getFieldValue( 'responsibleAuthority' ),
		location: getFieldValue( 'location' ),
		reportingMethods: getFieldValue( 'reportingMethods' ),
		versionStatus: getFieldValue( 'versionStatus' )
	},function( json ){
		if ( json.response == 'success' )
		{
			var form = document.getElementById( 'updateExtendedIndicatorForm' );
			form.submit();
		}
		else if ( json.response == 'error' )
		{
			window.alert( i18n_adding_indicator_failed + ':' + '\n' + json.message );
		}
		else if ( json.response == 'input' )
		{
			document.getElementById( 'message' ).innerHTML = json.message;
			document.getElementById( 'message' ).style.display = 'block';
		}
	});	

}
