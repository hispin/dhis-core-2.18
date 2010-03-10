
// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------

function initLists()
{
    var id;
	
	var list = document.getElementById( 'selectedDataSets' );
	
    for ( id in selectedDataSets )
    {
        list.add( new Option( selectedDataSets[id], id ), null );
    }	
	
    list = document.getElementById( 'availableDataSets' );
    
    for ( id in availableDataSets )
    {
        list.add( new Option( availableDataSets[id], id ), null );
    }
}
