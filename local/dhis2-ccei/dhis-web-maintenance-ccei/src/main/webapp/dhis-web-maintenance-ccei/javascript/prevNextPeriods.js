

//next and pre periods
function getAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	var availableList = document.getElementById( availablePeriodsId );
	var selectedList = document.getElementById( selectedPeriodsId );
	
	clearList( selectedList );
	
	addOptionToList( selectedList, '-1', '[ Select ]' );
	
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"year": year },
		function( json ) {
			
			for ( i in json.periods ) {
	    		addOptionToList( selectedList, json.periods[i].isoDate, json.periods[i].name );
	    	}
			
		} );
}