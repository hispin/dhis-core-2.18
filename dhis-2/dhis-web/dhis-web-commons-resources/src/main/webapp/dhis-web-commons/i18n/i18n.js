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

function getReference()
{
	clearFields( ' Ref' );

    var loc = getFieldValue( 'referenceLoc' );

    if ( loc != "heading" )
    {
		jQuery.postJSON( 'getTranslations.action', {
			id: getFieldValue ( 'objectId' ),
			className: getFieldValue ( 'className' ),
			loc: loc
		}, function ( json )
		{
			var translations = json.translations;

			for ( var i = 0; i < translations.length; i++ )
			{
				var field = document.getElementById( translations[i].key + ' Ref' );

				if ( field != null ) field.innerHTML = translations[i].value;
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

function addLocale()
{
    var loc = document.getElementById("loc");

    var language = document.getElementById("language").value;
    var country = document.getElementById("country").value;
    
    if ( language == null || language.length != 2 )
    {
    	setMessage( language_must_be_two_chars );
    	return;
    }
    
    if ( country == null || country.length != 2 )
    {
    	setMessage( country_must_be_two_chars );
    	return;
    }
    
	var toAdd = language + "_" + country;
	
	if ( listContains( loc, toAdd ) == true )
	{
		setMessage( locale_already_exists );
		return;
	}
	
    var option = document.createElement("option");

    option.value = toAdd;
    option.text = toAdd;

    loc.add(option, null);

    setMessage( locale_added + " " + toAdd );
}