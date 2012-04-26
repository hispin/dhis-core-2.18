
function CategoryLib()
{
	var categoryMap = new Array();
	var categoryOptionMap = new Array();
	
	this.loadCategories = function( elementId, id )
	{
		var target = jQuery( "#" + elementId );
		target.children().remove();

		if ( categoryMap.length == 0 )
		{	
			jQuery.getJSON( '../dhis-web-commons-ajax-json/getDataElementCategories.action', function( json )
			{
				categoryMap.push( new Category( -1, '[ ' + i18n_label + ' ]' ) );
				target.append( '<option value="-1">[ ' + i18n_label + ' ]</option>' );
				
				jQuery.each( json.dataElementCategories, function( i, item )
				{
					if ( id && item.id == id ) {
						target.append( '<option value="' + item.id + '" selected="true">' + item.name + '</option>' );
					}
					else {
						target.append( '<option value="' + item.id + '">' + item.name + '</option>' );
					}
					
					categoryMap.push( new Category( item.id, item.name ) );
				} );
			} );
		}
		else
		{
			jQuery.each( categoryMap, function( i, item )
			{
				if ( id && item.id == id ) {
					target.append( '<option value="' + item.id + '" selected="true">' + item.name + '</option>' );
				}
				else {
					target.append( '<option value="' + item.id + '">' + item.name + '</option>' );
				}
			} );
		}
	};

	this.loadCategoryOptionsByCategory = function( id, curItems, sourceList, destList, isFirstLoad )
	{
		var source = jQuery( "#" + sourceList );
		var dest = jQuery( "#" + destList );
		
		if ( source )
		{
			source.empty();
		}

		if ( dest && !isFirstLoad )
		{
			dest.empty();
		}

		var valueList = categoryOptionMap[ id ];

		if ( valueList == null )
		{
			valueList = new Array();

			jQuery.getJSON( 'getCategoryOptionsByCategory.action', {
				categoryId : id
			}, function( json )
			{
				jQuery.each( json.categoryOptions, function( i, item )
				{
					valueList.push( new CategoryOption( item.id, item.name ) );
					source.append( '<option value="' + item.id + '">' + item.name + '</option>' );
				} );
				
				categoryOptionMap[ id ] = valueList;
			} );
		}
		else
		{
			jQuery.each( valueList, function( i, item )
			{
				source.append( '<option value="' + item.id + '">' + item.name + '</option>' );
			} );
		}

		if ( curItems )
		{
			jQuery.each( curItems, function( i, item )
			{
				dest.append( '<option value="' + item.value + '">' + item.value + '</option>' );
			} );
		}
		
		this.removeDuplicatedItem( sourceList, destList );
	};
	
	this.removeDuplicatedItem = function( availableList, selectedList )
	{
		var $list1 = jQuery('#' + availableList);
		var $list2 = jQuery('#' + selectedList);
		
		if ( $list1 && $list2 )
		{
			jQuery.each( $list2.children(), function( i, item )
			{
				$list1.find( "option[value='" + item.value + "']" ).remove();
			} );
		}		
	};

	this.resetParams = function()
	{
		this.categoryMap = new Array();
		this.categoryOptionMap = new Array();
	}
}


function Category( _id, _name )
{
	this.id = _id;
	this.name = _name;
}

function CategoryOption( _id, _name )
{
	this.id = _id;
	this.name = _name;
}

var categoryLib = new CategoryLib();