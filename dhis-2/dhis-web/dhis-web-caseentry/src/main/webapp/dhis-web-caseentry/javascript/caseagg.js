function dataSetSelected()
{
	var dataSetId = $( '#dataSetId' ).val();	
	var periodFrom = $( '#sDateLB' ).val();
	var periodTo = $( '#eDateLB' ).val();
	
	if ( dataSetId && dataSetId != 0 )
	{
		var url = 'loadPeriods.action?dataSetId=' + dataSetId;

		var listStartPeriod = document.getElementById( 'sDateLB' );
		var listEndPeriod = document.getElementById( 'eDateLB' );
		
	    clearList( listStartPeriod );
		clearList( listEndPeriod );
	    
	    addOptionToList( listStartPeriod, '', '[' + i18n_please_select + ']' );
		addOptionToList( listEndPeriod, '', '[' + i18n_please_select + ']' );
		
	    $.getJSON( url, function( json ) {
			
	    	for ( i in json.periods ) {
	    		addOptionToList( listStartPeriod, i, json.periods[i].name );
				addOptionToList( listEndPeriod, i, json.periods[i].name );
	    	}
	    	
	    } );
	}
}