function getTranslation()
{
	clearFields();

    var loc = getFieldValue( 'loc' );
	
	if ( loc != "heading" )
	{
		jQuery.postJSON( 'getTranslations.action', {
			id: getFieldValue( 'objectId' ),
			className: getFieldValue( 'className' ),
			loc: loc
		}, function ( json ) {
			
			var translations = json.translations;

			for ( var i = 0; i < translations.length; i++ )
			{
				var field = byId( translations[i].key );

				if ( field != null ) field.value = translations[i].value;
			}
		});
	}
}

function clearFields( prefix )
{
	prefix = prefix ? prefix : '';
	
    for ( var i = 0; i < propNames.length; i++ )
    {
        byId( propNames[i] + prefix ).innerHTML = "";
    }
}
