// ----------------------------------------------------------------
// organization Unit Selected
// -------------------------------------showEquipmentDetails---------------------------

window.onload=function(){
	jQuery('#equipmentStatusHistoryDiv').dialog({autoOpen: false});	
	jQuery('#editEquipmentStatusDiv').dialog({autoOpen: false});
	jQuery('#editEquipmentStatusDiv').dialog({autoOpen: false});
	jQuery('#equipmentDataEntryDiv').dialog({autoOpen: false});
	jQuery('#editEquipmentDiv').dialog({autoOpen: false});
	jQuery('#equipmentDetailsDiv').dialog({autoOpen: false});
	jQuery('#fullOrgUnitDetailsDiv').dialog({autoOpen: false});
	jQuery('#updateOrgUnitDetailsDiv').dialog({autoOpen: false});
	jQuery('#facilityDataEntryDiv').dialog({autoOpen: false});
}
function organisationUnitSelected( orgUnits )
{   
	
	document.getElementById('selectedOrgunitID').value = orgUnits;
	
	hideById('selectOrgUnitDiv');
	//hideById('orgUnitDetailsDiv');
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnits[0]
	},
	function(){		
		showById('orgUnitDetailsDiv');
		loadAllEquipments();
		inventoryTypeChange();		
	});
	
}
selection.setListenerFunction( organisationUnitSelected );
function loadAllEquipments()
{
	document.getElementById('overlay').style.visibility = 'visible';
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	//alert( inventoryTypeId );
	
	document.getElementById("searchText").value = "";
	
	var filteredOrgUnitList = document.getElementById('filteredOrgUnitList');
	
	//alert( filteredOrgUnitList );
	
	if( inventoryTypeId == 0 )
	{	
		//alert("Plese select Inventorytype");
		showWarningMessage( i18n_select_inventorytype );
		return;
	}
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');

	//jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	isAjax = true;
	
	jQuery('#listEquipmentDiv').load('getEquipmentInstances.action',{
		listAll:true,
		orgUnitId:orgUnitId,
		//filteredOrgUnitList:getParamsStringBySelected( filteredOrgUnitList ),
		InventoryTypeId:inventoryTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		document.getElementById('overlay').style.visibility = 'hidden';
	});
	//hideLoader();
}



function loadAllEquipmentsByOrgUnitFilter()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	
	//alert( inventoryTypeId );
	
	//document.getElementById("searchText").value = "";
	
	//var filteredOrgUnitList = document.getElementById('filteredOrgUnitList');
	
	//alert( "inside  loadAllEquipmentsByOrgUnitFilter " );
	
	if( inventoryTypeId == 0 )
	{	
		//alert("Plese select Inventorytype");
		showWarningMessage( i18n_select_inventorytype );
		return;
	}
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');

	//jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	isAjax = true;

	document.getElementById('overlay').style.visibility = 'visible';
	var url = "getEquipmentInstances.action?" + getParamString( 'filteredOrgUnitList', 'filteredOrgUnitList' )
	
	jQuery('#listEquipmentDiv').load(url,
	{
		listAll:true,
		orgUnitId:orgUnitId,
		InventoryTypeId:inventoryTypeId
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		
		inventoryTypeChangeForFilteredOrgUnit();
		document.getElementById('overlay').style.visibility = 'hidden';
		//jQuery('#loaderDiv').hide();
	});
	//hideLoader();
}

function inventoryTypeChangeForFilteredOrgUnit()
{
	//loadAllEquipments();
	var inventoryTypeId = $( '#inventoryType' ).val();
	if( inventoryTypeId == "0" ) return;
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');
	
	//showById('selectDiv');
	//disable('listAllEquipmentBtn');
 
	//jQuery('#loaderDiv').show();
	document.getElementById('overlay').style.visibility = 'visible';
	$.post("getInventoryTypeAttributeList.action",
			{
				id:inventoryTypeId
			},
			function(data)
			{
				showById('listEquipmentDiv');
				populateInventoryTypeAttributes( data );
			},'xml');	
}


function getParamsStringBySelected( elementId )
{
	//alert( "getParamsStringBySelected" );
	var result = "";
	var list = jQuery( "#" + elementId ).children( ":selected" );
	
	list.each( function( i, item ){
		
		result += elementId + "=" + item.value;
		result += ( i < list.length - 1 ) ? "&" : "";
		
	});
	
	//alert( result );
	return result;
}

//----------------------------------------------------------------
//On InventoryType Change - Loading InventoryTypeAttributes
//----------------------------------------------------------------
//function inventoryTypeChange( inventoryTypeId )
function inventoryTypeChange()
{
	//loadAllEquipments();
	var inventoryTypeId = $( '#inventoryType' ).val();
	
	var inventoryType = document.getElementById('inventoryType');
	
	var inventoryTypeName = inventoryType.options[ inventoryType.selectedIndex ].text;

	if( inventoryTypeId == "0" ) return;
	
	
	//alert( inventoryTypeName );
	
	if( inventoryTypeName == "Ice packs" )
	{
		showById('addEquipmentIcePacksTD');
		hideById('addEquipmentTD');
	}
	
	else
	{
		showById('addEquipmentTD');
		hideById('addEquipmentIcePacksTD');
	}
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');
	
	//showById('selectDiv');
	//disable('listAllEquipmentBtn');
 
	//jQuery('#loaderDiv').show();
	document.getElementById('overlay').style.visibility = 'visible';
	$.post("getInventoryTypeAttributeList.action",
			{
				id:inventoryTypeId
			},
			function(data)
			{
				showById('listEquipmentDiv');
				
				populateInventoryTypeAttributes( data );
				loadAllEquipments();				
			},'xml');
}


function inventoryTypeChangeForDisplay()
{
	//loadAllEquipments();
	var inventoryTypeId = $( '#inventoryType' ).val();
	if( inventoryTypeId == "0" ) return;
	
	//showById('selectDiv');
	//disable('listAllEquipmentBtn');
 
	//jQuery('#loaderDiv').show();	
	$.post("getInventoryTypeAttributes.action",
			{
				id:inventoryTypeId
			},
			function(data)
			{
				populateInventoryTypeAttributes( data );				
			},'xml');	
}


function populateInventoryTypeAttributes( data )
{
	var searchingAttributeId = document.getElementById("searchingAttributeId");
	clearList( searchingAttributeId );
	
	var catalog_name_text = "Catalog Name";
	var catalog_name_value = "catalogname";
	
	var orunit_name_text = "OrgUnit Name";
	var orgunit_name_value = "orgunitname";
	
	var invenTypeAttribs = data.getElementsByTagName("inventory-type-attribute");
	for ( var i = 0; i < invenTypeAttribs.length; i++ )
	{
		var id = invenTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = invenTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		searchingAttributeId.add(option, null);
	} 
	$("#searchingAttributeId").append("<option value='"+ catalog_name_value + "' title='" + catalog_name_text + "' text='" + catalog_name_text + "'>" + catalog_name_text + "</option>");
	$("#searchingAttributeId").append("<option value='"+ orgunit_name_value + "' title='" + orunit_name_text + "' text='" + orunit_name_text + "'>" + orunit_name_text + "</option>");

}

/**
* Hides the document element with the given identifier.
* 
* @param id the element identifier.
*/
function hideFilter()
{
	hideById('filterDiv');
	//alert("fffff");
	showById('searchingAttributeTD');
	showById('searchingTextTD');
	showById('searchDiv');
	showById('clearDiv');
	//jQuery("#" + 'filterDiv').hide();
 
	//jQuery("#" + 'clearDiv' ).show();
}

function hideClear()
{
	//alert("ccccc");
	//jQuery("#" + 'clearDiv').hide();
	hideById('clearDiv');
	hideById('searchDiv');
	hideById('searchingTextTD');
	hideById('searchingAttributeTD');
	
	inventoryTypeChange();
	//loadAllEquipments();
	showById('filterDiv');
	//jQuery("#" + 'filterDiv' ).show();
}


function hideOrgFilter()
{
	hideById('filterOrgDiv');
	
	showById('searchingOrgUnitAttributeTD');
	
	searchingOrgUnitFilterOptionOnChange();
	
	//showById('searchingOrgTextTD');
	//showById('searchingOrgUnitGroupSetMemberTD');
	//showById('searchOrgDiv');
	showById('clearOrgDiv');
	//jQuery("#" + 'filterDiv').hide();
 
	//jQuery("#" + 'clearDiv' ).show();
}

function hideOrgClear()
{
	//alert("ccccc");
	//jQuery("#" + 'clearDiv').hide();
	
	hideById('clearOrgDiv');
	hideById('searchOrgDiv');
	hideById('searchingOrgTextTD');
	hideById('searchingOrgUnitAttributeTD');
	
	hideById('searchingOrgUnitGroupSetMemberTD');
	
	hideById('selectOrgUnitDiv');
	//hideById('orgUnitDetailsDiv');
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		inventoryTypeChange();
		//loadAllEquipments();
	});
	
	
	//inventoryTypeChange();
	//loadAllEquipments();
	showById('filterOrgDiv');
	//jQuery("#" + 'filterDiv' ).show();
}

//function searchingOrgUnitFilterOptionOnChange( searchingOrgUnitFilterOptionId )
function searchingOrgUnitFilterOptionOnChange()
{
	var orgUnitFilterOptionId = $( '#searchingOrgUnitFilterOptionId' ).val();
	
	var orgUnitFilterOption = document.getElementById('searchingOrgUnitFilterOptionId');
	
	var orgUnitFilterOptionName = orgUnitFilterOption.options[ orgUnitFilterOption.selectedIndex ].text;
	
	
	if( orgUnitFilterOptionName ==  "Facility Type" )
	{
		hideById('filterOrgDiv');
		showById('searchingOrgUnitGroupSetMemberTD');
		hideById('searchingOrgTextTD');
		hideById('searchOrgDiv');
		showById('clearOrgDiv');
	}
	else
	{
		hideById('filterOrgDiv');
		hideById('searchingOrgUnitGroupSetMemberTD');
		showById('searchingOrgTextTD');
		showById('searchOrgDiv');
		showById('clearOrgDiv');
	}
	
	var searchOrgText = document.getElementById('searchOrgText').value;
	document.getElementById("searchOrgText").value = "";
}



//----------------------------------------------------------------
//Load Equipments On Filter by InventoryType Attribute
//----------------------------------------------------------------

function loadEquipmentsByFilter()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	var searchText = document.getElementById('searchText').value;
	
	if( inventoryTypeId == 0 )
	{	
		return;
	}
	
	var inventoryTypeAttribute = document.getElementById('searchingAttributeId');
	var inventoryTypeAttributeId = inventoryTypeAttribute.options[ inventoryTypeAttribute.selectedIndex ].value;
	
	hideById('selectOrgUnitDiv');
	showById('orgUnitDetailsDiv');
	
	/*
	var url = "getEquipmentInstances.action?" + getParamString( 'filteredOrgUnitList', 'filteredOrgUnitList' )
	
	jQuery('#listEquipmentDiv').load(url,
	{
		listAll:true,
		orgUnitId:orgUnitId,
		InventoryTypeId:inventoryTypeId
	},
	*/
	
	//jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';
	
	isAjax = true;
	document.getElementById('overlay').style.visibility = 'visible';
	var url = "getEquipmentInstances.action?" + getParamString( 'filteredOrgUnitList', 'filteredOrgUnitList' )
	
	jQuery('#listEquipmentDiv').load(url,
	{		
		orgUnitId:orgUnitId, 
		inventoryTypeId:inventoryTypeId,
		inventoryTypeAttributeId:inventoryTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		document.getElementById('overlay').style.visibility = 'hidden';
		//jQuery('#loaderDiv').hide();
	});
	//hideLoader();
}

function searchingAttributeOnChange( inventoryTypeAttributeId )
{
	//alert( inventoryTypeAttributeId );
	var searchText = document.getElementById('searchText').value;
	document.getElementById("searchText").value = "";
}


function isEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		//alert( e.keycode );
		loadEquipmentsByFilter();
		//return false;
    }   
}





function searchingOrgUnitAttributeOnChange( orgUnitAttributeId )
{
	var searchOrgText = document.getElementById('searchOrgText').value;
	document.getElementById("searchOrgText").value = "";
}



function isOrgUnitEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		loadOrgUnitsByFilter();
    }   
}

function loadOrgUnitsByFilter()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	
	var searchOrgText = document.getElementById('searchOrgText').value;
	
	var orgUnitAttribute = document.getElementById('searchingOrgUnitFilterOptionId');
	var searchingOrgUnitFilterOptionId = orgUnitAttribute.options[ orgUnitAttribute.selectedIndex ].value;
	
	
	var orgUnitGroup = document.getElementById('searchingOrgUnitGroupId');
	var searchingOrgUnitGroupId = orgUnitGroup.options[ orgUnitGroup.selectedIndex ].value;
	
	//alert( searchingOrgUnitFilterOptionId );
	
	
	hideById('selectOrgUnitDiv');
	//hideById('searchOrgDiv');
	
	//showById('searchOrgDiv');
	showById('orgUnitDetailsDiv');
	

	//jQuery('#loaderDiv').show();
	contentDiv = 'orgUnitDetailsDiv';
	
	isAjax = true;
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{		
		orgUnitId:orgUnitId, 
		searchingOrgUnitFilterOptionId:searchingOrgUnitFilterOptionId,
		searchingOrgUnitGroupId:searchingOrgUnitGroupId,
		searchOrgText:searchOrgText,
		listFilterOrgUnit:true
	},
	function(){
		
		
		showById('orgUnitDetailsDiv');
		hideById('filterOrgDiv');
		
		showById('searchingOrgUnitAttributeTD');
		showById('searchingOrgUnitGroupSetMemberTD');
		//showById('searchingOrgTextTD');
		//showById('searchOrgDiv');
		showById('clearOrgDiv');
		
		
		
		loadAllEquipmentsByOrgUnitFilter();
		
		//inventoryTypeChange();
		
		
		
		//jQuery('#loaderDiv').hide();
		//document.getElementById('overlay').style.visibility = 'hidden';
	});
	//hideLoader();
}


/**
* Shows the document element with the given identifier.
* 
* @param id the element identifier.
*/
/*
function showById( id )
{
 jQuery("#" + id).show();
}
*/

var tempClinicName = "";
var tempCatalogName = "";

function showEquipmentStatusHistoryForm( equipmentInstanceId , clinicName, catalogName )
{
	tempClinicName = clinicName;
	tempCatalogName = catalogName;
	
	jQuery('#equipmentStatusHistoryDiv').dialog('destroy').remove();
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="equipmentStatusHistoryDiv">' ).load( 'showEquipmentStatusHistoryForm.action?equipmentInstanceId='+equipmentInstanceId ).dialog({
		title: tempCatalogName + " at " + tempClinicName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 450
	});
	//document.getElementById('overlay').style.visibility = 'hidden';
}

function closewindow()
{
	jQuery('#equipmentStatusHistoryDiv').dialog('destroy').remove();
	//hideById('equipmentStatusHistoryDiv');
	//setInnerHTML('equipmentStatusHistoryDiv', '');
	//$.modal.close();
}


function showEquipmentStatusForm( equipmentInstanceId )
{
	
	jQuery('#equipmentStatusHistoryDiv').dialog('close');
	
	jQuery('#editEquipmentStatusDiv').dialog('destroy').remove();
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="editEquipmentStatusDiv">' ).load( 'showEquipmentStatusForm.action?equipmentInstanceId='+ equipmentInstanceId ).dialog({
		title: tempCatalogName + " at " + tempClinicName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 600,
		height: 350
	});
	//document.getElementById('overlay').style.visibility = 'hidden';
	
	/*
	jQuery('#editEquipmentStatusDiv').load('showEquipmentStatusForm.action',
		{
			equipmentInstanceId:equipmentInstanceId
		}, function()
		{
			showById('editEquipmentStatusDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
	*/	
	
}

function updateEquipmentStatus()
{
	var tempEquipmentInstanceId = document.getElementById('equipmentInstanceId').value;
	var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
	var tempEquipmentCatalogName = document.getElementById('equipmentCatalogName').value;
	
	//alert(tempEquipmentInstanceId + "--" + tempEquipmentOrgUnitName + "--" + tempEquipmentCatalogName );
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('#loaderDiv').show();
	
	$.ajax({
      type: "POST",
      url: 'updateEquipmentStatus.action',
      data: getParamsForDiv('editEquipmentStatusDiv'),
      success: function( json ) {
		//loadAllEquipments();
		jQuery('#editEquipmentStatusDiv').dialog('destroy').remove();
		inventoryTypeChange();
		jQuery('#loaderDiv').hide();
		showEquipmentStatusHistoryForm( tempEquipmentInstanceId , tempEquipmentOrgUnitName, tempEquipmentCatalogName );
		//document.getElementById('overlay').style.visibility = 'hidden';
	}
     });
}


function closewindow2()
{
	var tempEquipmentInstanceId = $( '#equipmentInstanceId' ).val();
	//alert(tempEquipmentInstanceId);
			
	var tempEquipmentInstanceId = document.getElementById('equipmentInstanceId').value;
	var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
	var tempEquipmentCatalogName = document.getElementById('equipmentCatalogName').value;
	
	//alert(tempEquipmentInstanceId + "--" + tempEquipmentOrgUnitName + "--" + tempEquipmentCatalogName );
	jQuery('#editEquipmentStatusDiv').dialog('destroy').remove();
	showEquipmentStatusHistoryForm( tempEquipmentInstanceId , tempEquipmentOrgUnitName, tempEquipmentCatalogName );
	
	//hideById('equipmentStatusHistoryDiv');
	//setInnerHTML('equipmentStatusHistoryDiv', '');
	//$.modal.close();
}


function showEquipmentDataEntryForm( equipmentInstanceId , clinicName, catalogName )
{
	//jQuery('#loaderDiv').show();
	
	var orgUnitName = clinicName;
	var equipmentCatalogName = catalogName;
	
	jQuery('#equipmentDataEntryDiv').dialog('destroy').remove();
	//document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="equipmentDataEntryDiv">' ).load( 'showEquipmentDataEntryForm.action?equipmentInstanceId='+ equipmentInstanceId ).dialog({
		title: equipmentCatalogName + " at " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	//document.getElementById('overlay').style.visibility = 'hidden';
	/*
	
	jQuery('#equipmentDataEntryDiv').load('showEquipmentDataEntryForm.action',
		{
			equipmentInstanceId:equipmentInstanceId
		}, function()
		{
			showById('equipmentDataEntryDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			//jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
	
	*/
	//window.location.href = "showEquipmentDataEntryForm.action?equipmentInstanceId=" + equipmentInstanceId;
	
}

function editEquipmentDataEntryForm()
{
	$.ajax({
      type: "POST",
      url: 'saveDataEntryForm.action',
      data: getParamsForDiv('equipmentDataEntryDiv'),
      success: function( json ) {
		//loadAllEquipments();
		jQuery('#equipmentDataEntryDiv').dialog('destroy').remove();
		//inventoryTypeChange();
      }
     });
}


function closewindow3()
{
	jQuery('#equipmentDataEntryDiv').dialog('destroy').remove();
}

function showAddEquipmentForm( orgUnitName )
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	
	var orgUnitName = orgUnitName;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	var inventoryTypeName = inventoryType.options[ inventoryType.selectedIndex ].text;
	
	//alert( inventoryTypeName );
	if( inventoryTypeId == 0 )
	{	
		//alert("Plese select inventorytype");
		showWarningMessage( i18n_select_inventorytype );
		return;
	}

	
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showAddEquipmentForm.action?inventoryTypeId='+ inventoryTypeId + "&orgUnitId=" + orgUnitId ).dialog({
		title: 'Add New ' + inventoryTypeName + ' to ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
	
	/*
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentDiv').load('showAddEquipmentForm.action',{
		orgUnitId:orgUnitId, 
		inventoryTypeId:inventoryTypeId
		}, 
		function()
		{
			showById('editEquipmentDiv');
			jQuery('#loaderDiv').hide();
		});
	*/
}


function showAddEquipmentIcePacksForm( orgUnitName )
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	
	var orgUnitName = orgUnitName;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	var inventoryTypeName = inventoryType.options[ inventoryType.selectedIndex ].text;
	
	//alert( inventoryTypeName );
	if( inventoryTypeId == 0 )
	{	
		//alert("Plese select inventorytype");
		showWarningMessage( i18n_select_inventorytype );
		return;
	}

	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showAddEquipmentIcePacksForm.action?inventoryTypeId='+ inventoryTypeId + "&orgUnitId=" + orgUnitId ).dialog({
		title: 'Add New ' + inventoryTypeName + ' to ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
}




function getInventoryIcePacksData()
{
	
	$( '#loadEquipmentIcePacksDataForm' ).html('');
	
	$( '#saveButtonIcePacks' ).removeAttr( 'disabled' );

	
	var inventoryTypeId = $( '#inventoryTypeIcePacksId' ).val();
	
	var healthFacility = $( '#healthFacility' ).val();
    
	if ( healthFacility == "-1" )
	{
		$( '#loadEquipmentIcePacksDataForm' ).html('');
		document.getElementById( "saveButtonIcePacks" ).disabled = true;
		return false;
	}
	
	else
	{
	    jQuery('#loaderDiv').show();
	    
		jQuery('#loadEquipmentIcePacksDataForm').load('loadEquipmentIcePacksData.action',
			{
				inventoryTypeId:inventoryTypeId,
				orgUnitId:healthFacility,
			}, function()
			{
				showById('loadEquipmentIcePacksDataForm');
				jQuery('#loaderDiv').hide();
			});
		hideLoader();
	}

}


function addEquipment()
{
	$.ajax({
      type: "POST",
      url: 'addEquipment.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function(json) {
		var type = json.response;
		//jQuery('#resultSearchDiv').dialog('close');
		//loadAllEquipments();
		
		jQuery('#editEquipmentDiv').dialog('destroy').remove();
		inventoryTypeChange();
		
      }
     });
    //return false;
}

function addUpdateEquipmentIcePacks()
{
	$.ajax({
      type: "POST",
      url: 'addUpdateEquipmentIcePacks.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function(json) {
		var type = json.response;
		//jQuery('#resultSearchDiv').dialog('close');
		//loadAllEquipments();
		
		jQuery('#editEquipmentDiv').dialog('destroy').remove();
		inventoryTypeChange();
		
      }
     });
    //return false;
}





function closewindowEquipmentIcePacksData()
{
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
}

function closewindow4()
{
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
}

function getParamsForDiv( equipmentDiv )
{
	var params = '';
	
	jQuery("#" + equipmentDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				params += elementId + "="+ value + "&";
			}
			
		});
	
	//alert( params );
	
	return params;
}


function showEquipmentDetails( equipmentInstanceId , clinicName, catalogName,inventoryTypeName )
{
	
	var orgUnitName = clinicName;
	var equipmentCatalogName = catalogName;
	
	jQuery('#equipmentDetailsDiv').dialog('destroy').remove();
	jQuery('<div id="equipmentDetailsDiv">' ).load( 'showEquipmentInstanceDetails.action?equipmentInstanceId='+ equipmentInstanceId ).dialog({
		title: equipmentCatalogName + " at " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
	/*
	jQuery('#equipmentStatusHistoryDiv').dialog('destroy').remove();
	jQuery('<div id="equipmentStatusHistoryDiv">' ).load( 'showEquipmentInstanceDetails.action?equipmentInstanceId='+equipmentInstanceId ).dialog({
		title: i18n_equipment_details,
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 500,
		height: 450
	});
	*/
}

function closewindow5()
{
	jQuery('#equipmentDetailsDiv').dialog('destroy').remove();
}



//----------------------------------------------------------------
//Update Equipment
//----------------------------------------------------------------

function showUpdateEquipmentForm( equipmentInstanceId , clinicName, catalogName )
{
	
	var orgUnitName = clinicName;
	var equipmentCatalogName = catalogName;
	
	jQuery('#equipmentDetailsDiv').dialog('close');
	
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showUpdateEquipmentForm.action?equipmentInstanceId='+ equipmentInstanceId ).dialog({
		title: equipmentCatalogName + " at " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
	/*
	hideById('listEquipmentDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	hideById('equipmentDataEntryDiv');
	
	setInnerHTML('editEquipmentDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentDiv').load('showUpdateEquipmentForm.action',
		{
			equipmentInstanceId:equipmentInstanceId
		}, function()
		{
			showById('editEquipmentDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
	
	*/
}

function updateEquipment()
{
		var tempEquipmentInstanceId = document.getElementById('equipmentInstanceID').value;
		var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
		var tempEquipmentCatalogName = document.getElementById('equipmentCatalogName').value;
		
		$.ajax({
	    type: "POST",
	    url: 'updateEquipment.action',
	    data: getParamsForDiv('editEquipmentDiv'),
	    	success: function( json ) {
			//loadAllEquipments();
			
			jQuery('#editEquipmentDiv').dialog('destroy').remove();
			inventoryTypeChange();
			showEquipmentDetails( tempEquipmentInstanceId , tempEquipmentOrgUnitName, tempEquipmentCatalogName );
			}
		});
}

function closeUpdateWindow()
{
	//var tempEquipmentInstanceId = $( '#equipmentInstanceID' ).val();
	//alert(tempEquipmentInstanceId);
			
	var tempEquipmentInstanceId = document.getElementById('equipmentInstanceID').value;
	var tempEquipmentOrgUnitName = document.getElementById('equipmentOrgUnitName').value;
	var tempEquipmentCatalogName = document.getElementById('equipmentCatalogName').value;
	
	//alert(tempEquipmentInstanceId + "--" + tempEquipmentOrgUnitName + "--" + tempEquipmentCatalogName );
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	showEquipmentDetails( tempEquipmentInstanceId , tempEquipmentOrgUnitName, tempEquipmentCatalogName );
	
	//hideById('equipmentStatusHistoryDiv');
	//setInnerHTML('equipmentStatusHistoryDiv', '');
	//$.modal.close();
}

function removeEquipment( equipmentInstanceId , clinicName, catalogName )
{
	var itemName = catalogName + " at " + clinicName;
	
	//removeItem( equipmentInstanceId, name, i18n_confirm_delete, 'removeEquipmentInstance.action' );
	
	var result = window.confirm( i18n_confirm_delete + "\n\n" + itemName );
	
	if ( result )
	{
		$.ajax({
		    type: "POST",
		    url: 'removeEquipmentInstance.action?id='+ equipmentInstanceId,
		    	//data: getParamsForDiv('editEquipmentDiv'),
		    	success: function( json ) {
				jQuery('#editEquipmentDiv').dialog('destroy').remove();
				inventoryTypeChange();
				}
			});
		
		/*
		$.post("removeEquipmentInstance.action",
				{
					id:equipmentInstanceId
				},
				function( data )
				{
					jQuery('#editEquipmentDiv').dialog('destroy').remove();
					
				},'xml');
		*/		
		
		//window.location.href = 'removeEquipmentInstance.action?id=' + equipmentInstanceId;
		
	}
}


function showFullOrgUnitDetails( orgUnitId, orgUnitName )
{	
	var orgUnitId = orgUnitId;
	var orgUnitName = orgUnitName;
	//lockScreen();
	//jQuery('#loaderDiv').show();	
	jQuery('#fullOrgUnitDetailsDiv').dialog('destroy').remove();
	document.getElementById('overlay').style.visibility = 'visible';
	jQuery('<div id="fullOrgUnitDetailsDiv">' ).load( 'getFullOrganisationUnitDetails.action?orgUnitId='+ orgUnitId ).dialog({
		title: 'Full Details of ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.45},
		width: 800,
		height: 600
	});		
	//jQuery('#loaderDiv').hide();
	document.getElementById('overlay').style.visibility = 'hidden';
	//unLockScreen();
	
	/*
	jQuery('#editEquipmentDiv').dialog('destroy').remove();
	jQuery('<div id="editEquipmentDiv">' ).load( 'showAddEquipmentForm.action?inventoryTypeId='+ inventoryTypeId + "&orgUnitId=" + orgUnitId ).dialog({
		title: 'Add New Unit to ' + " " + orgUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	*/

}

function closeFullOrgDetailsWindow()
{
	jQuery('#fullOrgUnitDetailsDiv').dialog('destroy').remove();
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		inventoryTypeChange();
	});
	
}


//----------------------------------------------------------------
//Update OrganisationUnit and Close Window
//----------------------------------------------------------------

function showUpdateOrganisationUnitForm( organisationUnitId, organisationUnitName )
{
	
	var organisationUnitId = organisationUnitId;
	var organisationUnitName = organisationUnitName;
	
	jQuery('#fullOrgUnitDetailsDiv').dialog('close');
	
	jQuery('#updateOrgUnitDetailsDiv').dialog('destroy').remove();
	jQuery('<div id="updateOrgUnitDetailsDiv">' ).load( 'showUpdateOrganisationUnitForm.action?organisationUnitId='+ organisationUnitId ).dialog({
		title: organisationUnitName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 600
	});
}

function updateOrganisationUnit()
{
	//alert("updateOrganisationUnit");
	var orgUnitId = document.getElementById('orgUnitId').value;
	var orgUnitName = document.getElementById('orgUnitName').value;
	
	//alert( orgUnitId +" --- " + orgUnitName );
	
	//url: 'updateOrganisationUnit.action?jsonAttributeValues=' + jsonAttributeValues
	
	$.ajax({
    type: "POST",
    url: 'updateOrganisationUnit.action',
    data: getParamsForDiv('updateOrgUnitDetailsDiv'),
    
    success: function( json ) {
		jQuery('#updateOrgUnitDetailsDiv').dialog('destroy').remove();
		
		//alert( url +" -- "+ data );
		showFullOrgUnitDetails( orgUnitId, orgUnitName );
		}
	});
}


function closeUpdateOrganisationUnitWindow()
{
	var orgUnitId = document.getElementById('orgUnitId').value;
	var orgUnitName = document.getElementById('orgUnitName').value;
	
	jQuery('#updateOrgUnitDetailsDiv').dialog('destroy').remove();
	showFullOrgUnitDetails( orgUnitId, orgUnitName );
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		inventoryTypeChange();
	});
	
	
	
	
	
}



function showUpdateFacilityDataEntryForm( organisationUnitId, organisationUnitName )
{

	var orgUnitId = organisationUnitId;
	var orgUnitName = organisationUnitName;
	
	jQuery('#fullOrgUnitDetailsDiv').dialog('close');
	
	jQuery('#facilityDataEntryDiv').dialog('destroy').remove();
	jQuery('<div id="facilityDataEntryDiv">' ).load( 'showUpdateFacilityDataEntryForm.action?orgUnitId='+ organisationUnitId ).dialog({
		title: " Edit " + orgUnitName + " data",
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 800,
		height: 600
	});
}


function closeFacilityDataEntryWindow()
{
	var orgUnitId = document.getElementById('organisationUnitId').value;
	var orgUnitName = document.getElementById('organisationUnitName').value;
	
	jQuery('#facilityDataEntryDiv').dialog('destroy').remove();
	showFullOrgUnitDetails( orgUnitId, orgUnitName );
	
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	jQuery('#orgUnitDetailsDiv').load('getOrganisationUnitDetails.action',{
		
		orgUnitId:orgUnitId
	},
	function(){
		showById('orgUnitDetailsDiv');
		inventoryTypeChange();
	});
	
}


function updateFacilityDataEntryForm()
{
	var orgUnitId = document.getElementById('organisationUnitId').value;
	var orgUnitName = document.getElementById('organisationUnitName').value;
	
	//alert( orgUnitId + "--" + orgUnitName);
	
	$.ajax({
      type: "POST",
      url: 'saveFacilityDataEntryForm.action',
      data: getParamsForDiv('facilityDataEntryDiv'),
      success: function( json ) {
		jQuery('#facilityDataEntryDiv').dialog('destroy').remove();
		showFullOrgUnitDetails( orgUnitId, orgUnitName );
      }
     });
}

			
