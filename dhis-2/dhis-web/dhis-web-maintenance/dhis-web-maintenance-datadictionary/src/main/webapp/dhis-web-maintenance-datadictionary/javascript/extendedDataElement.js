
// -----------------------------------------------------------------------------
// Add data element
// -----------------------------------------------------------------------------

function validateAddExtendedDataElement()
{
	jQuery.postJSON("validateExtendedDataElement.action",{
		name: getFieldValue( 'name' ),
		shortName: getFieldValue( 'shortName' ),
		alternativeName: getFieldValue( 'alternativeName' ),
		code: getFieldValue( 'code' ),
		description: getFieldValue( 'description' ),
		mnemonic: getFieldValue( 'mnemonic' ),
		version: getFieldValue( 'version' ),
		keywords: getFieldValue( 'keywords' ),
		dataElementType: getFieldValue( 'dataElementType' ),
		dataElementType: getFieldValue( 'dataElementType' ),
		minimumSize: getFieldValue( 'minimumSize' ),
		maximumSize: getFieldValue( 'maximumSize' ),
		responsibleAuthority: getFieldValue( 'responsibleAuthority' ),
		location: getFieldValue( 'location' ),
		reportingMethods: getFieldValue( 'reportingMethods' ),
		versionStatus: getFieldValue( 'versionStatus' )		
	}, function( json ){		
		if ( json.response == 'success' )
		{
			var form = document.getElementById( 'addExtendedDataElementForm' );
			form.submit();
		}
		else if ( json.response == 'error' )
		{
			window.alert( i18n_adding_data_element_failed + ':' + '\n' + json.message );
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

function validateUpdateExtendedDataElement()
{
	
	jQuery.postJSON("validateExtendedDataElement.action",{
		id: getFieldValue( 'id' ),
		name: getFieldValue( 'name' ),
		shortName: getFieldValue( 'shortName' ),
		alternativeName: getFieldValue( 'alternativeName' ),
		code: getFieldValue( 'code' ),
		description: getFieldValue( 'description' ),
		mnemonic: getFieldValue( 'mnemonic' ),
		version: getFieldValue( 'version' ),
		keywords: getFieldValue( 'keywords' ),
		dataElementType: getFieldValue( 'dataElementType' ),
		dataElementType: getFieldValue( 'dataElementType' ),
		minimumSize: getFieldValue( 'minimumSize' ),
		maximumSize: getFieldValue( 'maximumSize' ),
		responsibleAuthority: getFieldValue( 'responsibleAuthority' ),
		location: getFieldValue( 'location' ),
		reportingMethods: getFieldValue( 'reportingMethods' ),
		versionStatus: getFieldValue( 'versionStatus' )		
	}, function( json ){		
		if ( json.response == 'success' )
		{
			var form = document.getElementById( 'updateExtendedDataElementForm' );
			form.submit();
		}
		else if ( json.response == 'error' )
		{
			window.alert( i18n_adding_data_element_failed + ':' + '\n' + json.message );
		}
		else if ( json.response == 'input' )
		{
			document.getElementById( 'message' ).innerHTML = json.message;
			document.getElementById( 'message' ).style.display = 'block';
		}
	});

}
