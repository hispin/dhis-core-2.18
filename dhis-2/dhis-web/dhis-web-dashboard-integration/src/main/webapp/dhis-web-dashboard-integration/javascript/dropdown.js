
$( document ).ready( function()
{
	var viewportWidth = parseInt( $( window ).width() );
	var linkWidth = parseInt( 338 );
	var chartWidth = parseInt( 325 );
	
	if ( viewportWidth == undefined )
	{
		viewportWidth = parseInt( 1366 );
	}
	
	var noCharts = 2 * Math.floor( ( viewportWidth - linkWidth ) / chartWidth ); 
		
	$( "#contentDiv" ).load( "provideContent.action?noCharts=" + noCharts + "&_dc=" + getRandomNumber() );
} );

function setAreaItem( area, item )
{
    $.get( "setAreaItem.action", {
        'area' : area,
        'item' : item
    }, function()
    {
        window.location.href = "index.action";
    } );
}

function clearArea( area )
{
    $.get( "clearArea.action", {
        'area' : area
    }, function()
    {
        window.location.href = "index.action";
    } );
}

function viewChart( url )
{
    var width = 820;
    var height = 520;

    $( "#chartImage" ).attr( "src", url );
    $( "#chartView" ).dialog( {
        autoOpen : true,
        modal : true,
        height : height + 65,
        width : width + 25,
        resizable : false,
        title : "Viewing Chart"
    } );
}

function explore( uid )
{
	window.location.href = "../dhis-web-visualizer/app/index.html?id=" + uid;
}