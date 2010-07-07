//----------------------------------------------------------
// Regular Expression using for checking shortname' value
//----------------------------------------------------------

regexShortName = /^[\w][\w\d]+$/;

//----------------------------------------------------------


function updateTranslation()
{
    var id = document.getElementById("objectId").value;

    var className = document.getElementById("className").value;

    var box = document.getElementById("loc");
    var loc = box.options[box.selectedIndex].value;

    var url = "getTranslations.action?id=" + id + "&className=" + className + "&loc=" + loc ;

    var request = new Request();

    request.setResponseTypeXML('translation');

    request.setCallbackSuccess(updateTranslationReceived);

    /* Clear fields */
    for ( var i = 0; i < propNames.length; i++ )
    {
        document.getElementById(propNames[i]).value = "";
    }

    if ( loc != "heading" )
    {
        request.send(url);
    }
}

function updateTranslationReceived( xmlObject )
{
    var translations = xmlObject.getElementsByTagName("translation");

    for ( var i = 0; i < translations.length; i++ )
    {
        var key = translations[ i ].getElementsByTagName("key")[0].firstChild.nodeValue;

        var value = translations[ i ].getElementsByTagName("value")[0].firstChild.nodeValue;

        var field = document.getElementById(key);

        if ( field != null )
        {
            field.value = value;
        }
    }
}

function updateReference()
{
    var id = document.getElementById("objectId").value;

    var className = document.getElementById("className").value;

    var box = document.getElementById("referenceLoc");
    var loc = box.options[box.selectedIndex].value;

    var url = "getTranslations.action?id=" + id + "&className=" + className + "&loc=" + loc ;

    var request = new Request();

    request.setResponseTypeXML('translation');

    request.setCallbackSuccess(updateReferenceReceived);

    /* Clear fields */
    for ( var i = 0; i < propNames.length; i++ )
    {
        document.getElementById(propNames[i] + " Ref").innerHTML = "";
    }

    if ( loc != "heading" )
    {
        request.send(url);
    }
}

function updateReferenceReceived( xmlObject )
{
    var translations = xmlObject.getElementsByTagName("translation");

    for ( var i = 0; i < translations.length; i++ )
    {
        var key = translations[ i ].getElementsByTagName("key")[0].firstChild.nodeValue;

        var value = translations[ i ].getElementsByTagName("value")[0].firstChild.nodeValue;

        var field = document.getElementById(key + " Ref");

        if ( field != null )
        {
            field.innerHTML = value;
        }
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