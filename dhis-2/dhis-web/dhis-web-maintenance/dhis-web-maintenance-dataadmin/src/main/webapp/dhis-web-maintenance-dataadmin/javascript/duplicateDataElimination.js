
function initLists()
{	
	$.getJSON( 
        "../dhis-web-commons-ajax-json/getDataElements.action",
        {},
        function( json )
        {
        	var dataElementList = document.getElementById( "dataElementList" );
        	
        	var elements = json.dataElements;
        	
        	for ( var i = 0; i < elements.length; i++ )
        	{
        	   $( "#dataElementList" ).append( "<option value='" + 
        	       elements[i].id + "'>" + elements[i].name + "</option>" );
        	}
        }
    );
}