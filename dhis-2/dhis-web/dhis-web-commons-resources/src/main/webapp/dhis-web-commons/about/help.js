
$( document ).ready( function()
{
    $.get( 
       '../dhis-web-commons-help/getHelpItems.action',
       function( data )
       {
           $( "div#helpMenu" ).html( data );
       } );
} );

function getHelpItemContent( id )
{
	$.get( 
       '../dhis-web-commons-help/getHelpContent.action',
       { "id": id },
       function( data )
       {
           $( "div#helpContent" ).html( data );
       } );
}
