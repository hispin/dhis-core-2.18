
var currentPage = 0;
var pageLock = false;

$( document ).ready( function() {
	$( ".commentArea" ).autogrow();
	
	$( document ).scroll( function() {
		isNextPage();
	} );
	
	$( "#interpretationFeed" ).load( "getInterpretations.action" );
} );

function isNextPage()
{
	var fromTop = $( document ).scrollTop();
	var docHeight = $( document ).height();
	var windowHeight = $( window ).height();
	var threshold = parseInt( 350 );
	var remaining = parseInt( docHeight - ( fromTop + windowHeight ) );
	
	log("--");log(fromTop);log(docHeight);log(windowHeight);log(remaining);	
	
	if ( remaining < threshold )
	{
		loadNextPage();
	}
}

function loadNextPage()
{
	if ( pageLock == true )
	{
		return false;
	}
	
	pageLock = true;
	currentPage++;
	
	$.get( "getInterpretations.action", { page: currentPage }, function( data ) {
		$( "#interpretationFeed" ).append( data );
		
		if ( !isDefined ( data ) || $.trim( data ).length == 0 )
		{
			$( document ).off( "scroll" );
		}
		
		pageLock = false;
	} );
}

function showUserInfo( id )
{
	$( "#userInfo" ).load( "../dhis-web-commons-ajax-html/getUser.action?id=" + id, function() {
		$( "#userInfo" ).dialog( {
			modal : true,
			width : 350,
			height : 350,
			title : "User"
		} );
	} );
}

function postComment( uid )
{	
	var text = $( "#commentArea" + uid ).val();
	
	$( "#commentArea" + uid ).val( "" );
	
	var url = "../api/interpretations/" + uid + "/comment";
	
	var created = getCurrentDate();
	
	if ( text.length && $.trim( text ).length )
	{
		$.ajax( url, {
			type: "POST",
			contentType: "text/html",
			data: $.trim( text ),
			success: function() {			
				var template = 
					"<div><div class=\"interpretationName\"><span class=\"bold pointer\" " +
					"onclick=\"showUserInfo( \'${userId}\' )\">${userName}<\/span>&nbsp; " +
					"<span class=\"grey\">${created}<\/span><\/div><\/div>" +
					"<div class=\"interpretationText\">${text}<\/div>";
				
				$.tmpl( template, { "userId": currentUser.id, "userName": currentUser.name, created: created, text: text } ).appendTo( "#comments" + uid );
			}		
		} );
	}
}