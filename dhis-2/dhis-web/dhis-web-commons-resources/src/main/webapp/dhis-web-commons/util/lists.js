
/**
 * Moves selected options in the source list to the target list.
 * 
 * @param fromList the id of the source list.
 * @param targetList the id of the target list.
 */
function moveSelectedById( fromListId, targetListId )
{
    var fromList = document.getElementById( fromListId );
    var targetList = document.getElementById( targetListId );

    moveSelected( fromList, targetList );
}

/**
 * Moves selected options in the source list to the target list.
 * 
 * @param fromList the source list.
 * @param targetList the target list.
 */
function moveSelected( fromList, targetList )
{
    if ( fromList.selectedIndex == -1 )
    {
        return;
    }

    while ( fromList.selectedIndex > -1 )
    {
        option = fromList.options[ fromList.selectedIndex ];
        fromList.remove( fromList.selectedIndex );
        targetList.add(option, null);
        option.selected = true;
    }
}

/**
 * Moves all elements in a list to the target list
 * 
 * @param fromListId the id of the source list.
 * @param targetListId the id of target list.
 */
function moveAllById( fromListId, targetListId )
{
    var fromList = document.getElementById( fromListId );
    var targetList = document.getElementById( targetListId );

    moveAll( fromList, targetList );
}

/**
 * Moves all elements in a list to the target list
 * 
 * @param fromList the source list.
 * @param targetList the target list.
 */
function moveAll( fromList, targetList )
{
    for ( var i = fromList.options.length - 1; i >= 0; i-- )
    {
        option = fromList.options[i];
        fromList.remove( i );
        targetList.add( option, null );
        option.selected = true;
    }
}

/**
 * Clears the list.
 * 
 * @param listId the id of the list.
 */
function clearListById( listId )
{
    var list = document.getElementById( listId );
    
    clearList( list );
}

/**
 * Clears the list.
 * 
 * @param list the list.
 */
function clearList( list )
{
    list.options.length = 0;
}

/**
 * Tests whether the list contains the value.
 * 
 * @param listId the id of the list.
 * @param value the value.
 */
function listContainsById( listId, value )
{
    var list = document.getElementById( listId );
    
    return listContains( list, value );
}

/**
 * Tests whether the list contains the value.
 * 
 * @param list the list.
 * @param value the value.
 */
function listContains( list, value )
{
    for ( var i = 0; i < list.options.length; i++ )
    {
        if ( list.options[i].value == value )
        {
            return true;
        }
    }

    return false;
}

/**
 * Marks all elements in a list as selected.
 * 
 * @param listId the id of the list.
 */
function selectAllById( listId )
{
	var list = document.getElementById( listId );
	
	selectAll( list );
}

/**
 * Marks all elements in a list as selected.
 * 
 * @param list the list.
 */
function selectAll( list )
{
	for ( var i = 0; i < list.options.length; i++ )
	{
		list.options[i].selected = true;
	}
}

/**
 * Marks all elements in a list as not selected.
 * 
 * @param listId the id of the list.
 */
function deselectAllById( listId )
{
	var list = document.getElementById( listId );
	
	deselectAll( list );
}

/**
 * Marks all elements in a list as not selected.
 * 
 * @param list the list.
 */
function deselectAll( list )
{
	for ( var i = 0; i < list.options.length; i++ )
	{
		list.options[i].selected = false;
	}
}

/**
 * Adds an option to a select list.
 * 
 * @param listId the id of the list.
 * @param text the text of the option.
 * @param value the value of the option.
 */
function addOption( listId, text, value )
{
	var list = document.getElementById( listId );
	
	var option = new Option( text, value );
	
	list.add( option, null );
}

/**
 * Removes the selected option from a select list.
 * 
 * @param listId the id of the list.
 */
function removeSelectedOption( listId )
{
	var list = document.getElementById( "levelNames" );
	
	for ( var i = list.length - 1; i >= 0; i-- )
	{
		if ( list.options[ i ].selected )
		{
			list.remove( i );
		}
	}
}

/**
 * Moves the selected option in a select list up one position.
 * 
 * @param listId the id of the list.
 */
function moveUpSelectedOption( listId )
{
	var list = document.getElementById( listId );
	
	for ( var i = 0; i < list.length; i++ )
	{
		if ( list.options[ i ].selected )
		{
			if ( i > 0 )
			{
				var precedingOption = new Option( list.options[ i - 1 ].text, list.options[ i - 1 ].value );
				var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );
				
				list.options[ i - 1 ] = currentOption;
				list.options[ i - 1 ].selected = true;
				list.options[ i ] = precedingOption;
			}
		}
	}
}

/**
 * Moves the selected option in a list down one position.
 * 
 * @param listId the id of the list.
 */
function moveDownSelectedOption( listId )
{
	var list = document.getElementById( listId );
	
	for ( var i = list.options.length - 1; i >= 0; i-- )
	{
		if ( list.options[ i ].selected )
		{
			if ( i <= list.options.length - 1 )
			{
				var subsequentOption = new Option( list.options[ i + 1 ].text, list.options[ i + 1 ].value );
				var currentOption = new Option( list.options[ i ].text, list.options[ i ].value );
				
				list.options[ i + 1 ] = currentOption;
				list.options[ i + 1 ].selected = true;
				list.options[ i ] = subsequentOption;
			}
		}
	}
}

/**
 * Moves the selected option to the top of the list.
 * 
 * @param listId the id of the list.
 */
function moveSelectedOptionToTop( listId )
{
    var list = document.getElementById( listId );
    
    var moveOption = null;
            
    for ( var i = list.options.length - 1; i >= 0; i-- )
    {
        if ( list.options[ i ].selected )
        {
            moveOption = new Option( list.options[ i ].text, list.options[ i ].value );                
        }
        
        if ( moveOption != null && i > 0 )
        {
            var nextOption = new Option( list.options[ i - 1 ].text, list.options[ i - 1 ].value );
            list.options[ i ] = nextOption;
        }
    }
    
    list.options[ 0 ] = moveOption;       
}

/**
 * Moves the selected option to the bottom of the list.
 * 
 * @param listId the id of the list.
 */
function moveSelectedOptionToBottom( listId )
{
    var list = document.getElementById( listId );
    
    var moveOption = null;
  
    for ( var i = 0; i < list.options.length; i++ )
    {
        if ( list.options[ i ].selected )
        {
            moveOption = new Option( list.options[ i ].text, list.options[ i ].value );                
        }
      
        if ( moveOption != null && i < ( list.options.length - 1 ) )
        {
            var nextOption = new Option( list.options[ i + 1 ].text, list.options[ i + 1 ].value );
            list.options[ i ] = nextOption;
        }
    }
    
    list.options[ list.options.length - 1 ] = moveOption;
}

/**
 * Filters out options in a select list that don't match the filter string by 
 * hiding them.
 * 
 * @param filter the filter string.
 * @param listId the id of the list to filter.
 */
function filterList( filter, listId )
{
    var list = document.getElementById( listId );
  
    for ( var i=0; i<list.options.length; i++ )
    {
        var value = list.options[i].text;
     
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.options[i].style.display = "block";
        }
        else
        {
            list.options[i].style.display = "none";
        }
    }
}

/**
 * Clears a filter and resets the filtered select list.
 * 
 * @param filterId the id of the filter input box.
 * @param listId the id of the list reset after being filtered.
 */
function clearFilter( filterId, listId )
{
    document.getElementById( filterId ).value = "";
  
    filterList( "", listId );
}

/**
 * Check if list has option.
 * 
 * @param obj is list object 
 */
function hasOptions( obj ) 
{
	if ( obj != null && obj.options != null ) 
	{
	    return true;
	}
	
	return false;
}

/**
 * Sort list by name.
 * 
 * @param id is id of list
 * @param type is type for sort ASC:ascending && DES: desending
 */
function sortList( id, type ) 
{
	var obj = document.getElementById( id );
	var o = new Array();
	if (!hasOptions(obj)) { return; }
	for (var i=0; i<obj.options.length; i++) {
		o[o.length] = new Option( obj.options[i].text, obj.options[i].value ) ;
		}
	if (o.length==0) { return; }
	if(type=='ASC'){
		o = o.sort(		
				function(a,b) { 
					if ((a.text+"") < (b.text+"")) { return -1; }
					if ((a.text+"") > (b.text+"")) { return 1; }
					return 0;
				} 		
		);
	}
	if(type=='DES'){
		o = o.sort(		
				function(a,b) { 
					if ((a.text+"") < (b.text+"")) { return 1; }
					if ((a.text+"") > (b.text+"")) { return -1; }
					return 0;
				} 		
		);
	}
	for (var i=0; i<o.length; i++) {
		obj.options[i] = new Option(o[i].text, o[i].value);
	}
}
