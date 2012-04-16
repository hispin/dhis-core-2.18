var selectedAttributeValueMap = new Array();

function addOptionToListWithToolTip( list, optionValue, optionText )
{
    var option = document.createElement( "option" );
    option.value = optionValue;
    option.text = optionText;
	option.onmousemove = function(e) {
		showToolTip(e, optionText);
	}
    list.add( option, null );
}

function showAttributeValueGroupOrderDetails( id )
{
	jQuery.post( 'getAttributeValueGroupOrder.action',  { id: id }, function( json ) {
		
		setInnerHTML( 'nameField', json.attributeValueGroupOrder.name );
		setInnerHTML( 'memberCountField', json.attributeValueGroupOrder.memberCount );

		showDetails();
	});
}

function resetForm()
{
	setFieldValue( "name", "" );
	setFieldValue( "attributeValueGroupOrderId", "" );

	var availableList = jQuery( '#availableAttributeValues' );
	availableList.empty();
	var selectedList = jQuery( '#attributeValues' );
	selectedList.empty();
}

/*
* 	Open Add Attribute Value Group Order 
*/
function openAddAttributeValueGroupOrder()
{
	resetForm();
	validator.resetForm();

	attributeLib.loadAttributes( "attributeId" );

	dialog.dialog("open");
	
	jQuery( "#attributeValueGroupsForm" ).attr( "action", "addAttributeValueGroupOrderFor" + clazzName + ".action?clazzName=" + clazzName );
}

/*
* 	Open Update Data Element Order
*/

function openUpdateAttributeValueGroupOrder( id )
{
	validator.resetForm();
	setFieldValue("attributeValueGroupOrderId", id );
	
	jQuery.post( 'getAttributeValueGroupOrder.action', { id: id }, function( json )
	{
		var attributeId = json.attributeValueGroupOrder.attributeId;
		var values = json.attributeValueGroupOrder.attributeValues;
		var list = jQuery( "#attributeValues" );
		list.empty();
		selectedAttributeValueMap = [];
		var items = [];
		
		setFieldValue( "name", json.attributeValueGroupOrder.name );
		attributeLib.loadAttributes( "attributeId", attributeId );
		attributeLib.loadAttributeValuesByAttribute( attributeId, items, "availableAttributeValues", "attributeValues", true );
		
		for ( var i = 0 ; i < values.length ; i++ )
		{
			items.push( new AttributeValue( values[ i ].value ) );
			list.append( '<option value="' + values[ i ].value + '">' + values[ i ].value + '</option>' );
		}

		selectedAttributeValueMap[ id + "-" + attributeId ] = items;

		attributeLib.removeDuplicatedItem( "availableAttributeValues", "attributeValues" );

		jQuery( "#attributeValueGroupsForm" ).attr( "action", "updateAttributeValueGroupOrderFor" + clazzName + ".action" );
		dialog.dialog( "open" );
	} );
}

function validateAttributeValueGroupOrder( _form )
{
	var attributeId = getFieldValue( "attributeId" );

	if ( attributeId && attributeId != -1 )
	{
		jQuery.postUTF8( "validateAttributeValueGroupOrder.action", {
			name: getFieldValue( 'name' ),
			id: getFieldValue( 'attributeValueGroupOrderId' ),
			reportId: reportId,
			clazzName: clazzName
		}, function( json )
		{
			if ( json.response == "success" )
			{
				if ( hasElements( 'attributeValues' ) )
				{
					selectAllById( 'attributeValues' );
					_form.submit();
				}
				else { markInvalid( "attributeValues", i18n_selected_list_empty ); }
			}
			else { markInvalid( "name", json.message ); }
		} );
	} else { markInvalid( "attributeId", i18n_verify_attribute ); }
}

/*
* 	Delete Attribute Value Group Order
*/
function deleteAttributeValueGroupOrder( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'deleteAttributeValueGroupOrder.action', function(){ window.location.reload(); } );
}

/*
*	Update Attribute Value Group Order
*/
function updateSortAttributeValueGroupOrder()
{
	var attributeValueGroups = document.getElementsByName( 'attributeValueGroupOrder' );
	var url = "updateSortAttributeValueGroupOrder.action?reportId=" + reportId;
	url += "&clazzName=" + clazzName;
	
	for ( var i = 0 ; i < attributeValueGroups.length ; i++ )
	{
		url += "&attributeValueGroupOrderId=" + attributeValueGroups.item(i).value;
	}
	
	jQuery.postJSON( url, {}, function( json ) {
		showSuccessMessage( json.message );
	});
}

function openSortAttributeValueForGroupOrder( id )
{
	window.location = "openSortAttributeValue.action?id="+id+"&reportId="+reportId+"&clazzName="+clazzName;
}

/*
* 	Update Sorted Attribute Value
*/
function updateSortedAttributeValue()
{	
	moveAllById( 'availableList', 'selectedList' );
	selectAllById( 'selectedList' );
	document.forms[0].submit();
}

/*
*	Tooltip
*/
function showToolTip( e, value)
{	
	var tooltipDiv = byId( 'tooltip' );
	tooltipDiv.style.display = 'block';
	
	var posx = 0;
    var posy = 0;
	
    if (!e) var e = window.event;
    if (e.pageX || e.pageY)
    {
        posx = e.pageX;
        posy = e.pageY;
    }
    else if (e.clientX || e.clientY)
    {
        posx = e.clientX;
        posy = e.clientY;
    }
	
	tooltipDiv.style.left= posx  + 8 + 'px';
	tooltipDiv.style.top = posy  + 8 + 'px';
	tooltipDiv.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +   value;
}

function hideToolTip()
{
	byId('tooltip').style.display = 'none';
}