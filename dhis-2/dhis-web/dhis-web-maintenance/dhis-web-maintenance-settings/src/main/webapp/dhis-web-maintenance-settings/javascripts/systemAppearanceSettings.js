
function changeLocale()
{	
	$.get( '../dhis-web-commons-ajax/systemAppearanceSettingsString.action?localeCode=' + $( '#localeSelect' ).val(), function( json ) {
		$( '#applicationTitle' ).val( json.applicationTitle );
		$( '#applicationIntro' ).val( json.keyApplicationIntro );
		$( '#applicationNotification' ).val( json.keyApplicationNotification );
		$( '#applicationFooter' ).val( json.keyApplicationFooter );
	} );	
}