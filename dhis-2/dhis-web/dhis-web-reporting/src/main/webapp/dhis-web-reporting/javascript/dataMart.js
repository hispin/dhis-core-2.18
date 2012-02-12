
$( document ).ready( function() {
	datePickerInRange( 'startDate' , 'endDate' );
} );

function startExport()
{
	var url = 'startExport.action';
	var startDate = $( '#startDate' ).val();
	var endDate = $( '#endDate' ).val();
	
	$.get( url, { startDate:startDate, endDate:endDate }, pingNotifications );
}

function pingNotifications()
{
	$( '#notificationDiv' ).load( '../dhis-web-commons-ajax/getNotifications.action?category=DATAMART' );
	
	setTimeout( "pingNotifications()", 2000 );
}