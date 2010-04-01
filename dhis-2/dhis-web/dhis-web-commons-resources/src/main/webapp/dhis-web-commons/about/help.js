
$( document ).ready( function()
{	
	$( "div#helpBack" ).click(
		function()
		{
			window.location.href="/dhis/dhis-web-portal/intro.action";
		});

    $.get( 
       'getHelpItems.action',
       function( data )
       {
           $( "div#helpMenu" ).html( data );
           $( "div#helpMenu" ).accordion();
       } );
} );

function getHelpItemContent( id )
{
	$.get( 
       'getHelpContent.action',
       { "id": id },
       function( data )
       {
           $( "div#helpContent" ).html( data );
       } );
}
