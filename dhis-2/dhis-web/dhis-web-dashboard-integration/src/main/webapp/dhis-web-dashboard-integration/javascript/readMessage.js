
var dialog = null;

$( document ).ready( function() {
	
	dialog = $( "#senderInfo" ).dialog( {
		modal: true,
		autoOpen: false,
		width: 300,
		height: 250,
		title: "Sender" } );
} );

function showSenderInfo( id )
{
	$( "#senderInfo" ).load( "getUserInfo.action", { id:id }, function() {
		dialog.dialog( "open" );
	} );
}
