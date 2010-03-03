
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

function filterSelectedDataSets()
{
	var filter = document.getElementById( 'selectedDataSetsFilter' ).value;
    var list = document.getElementById( 'selectedDataSets' );
    
    list.options.length = 0;
    
    for ( var id in selectedDataSets )
    {
        var value = selectedDataSets[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableDataSets()
{
	var filter = document.getElementById( 'availableDataSetsFilter' ).value;
    var list = document.getElementById( 'availableDataSets' );
    
    list.options.length = 0;
    
    for ( var id in availableDataSets )
    {
        var value = availableDataSets[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addSelectedDataSets()
{
	var list = document.getElementById( 'availableDataSets' );

    while ( list.selectedIndex != -1 )
    {
		var selectedList = byId( 'selectedDataSets' );
        var id = list.options[list.selectedIndex].value;
		
		selectedDataSets[id] = availableDataSets[id];
		
		addOptionToList( selectedList, id, selectedDataSets[id] );
		
        list.remove( list.selectedIndex );
        
        delete availableDataSets[id];        
    }
    
	//Not filter anymore
    //filterDataSetMembers();
    //filterAvailableDataElements();
}

function removeDataSetMembers()
{
	var list = document.getElementById( 'selectedDataSets' );

    while ( list.selectedIndex != -1 )
    {
		var availableList = byId( 'availableDataSets' );
        var id = list.options[list.selectedIndex].value;

        availableDataSets[id] = selectedDataSets[id];
		
		addOptionToList( availableList, id, availableDataSets[id] );
		
		list.remove( list.selectedIndex );
        
        delete selectedDataSets[id];        
    }
    
	//Not filter anymore
    //filterDataSetMembers();
    //filterAvailableDataElements();
}
