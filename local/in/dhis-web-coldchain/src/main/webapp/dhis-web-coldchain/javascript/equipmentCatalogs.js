
// ----------------------------------------------------------------
// On CatalogTypeChange  - Loading CatalogType Attributes
// ----------------------------------------------------------------
//function getCatalogTypeChange( catalogTypeId )
window.onload=function(){
	jQuery('#addEditCatalogFormDiv').dialog({autoOpen: false});	
	jQuery('#catalogDetailsDiv').dialog({autoOpen: false});	
}
function getCatalogTypeChange()
{
	loadAllCatalogs();
	var catalogTypeId = $( '#catalogType' ).val();
	
	if( catalogTypeId == "0" )
	{
		return;
	}
		
	$.post("getCatalogTypeAttribute.action",
			{
				id:catalogTypeId
			},
			function(data)
			{
				populateCatalogTypeAttributes( data );
			},'xml');	
}

function populateCatalogTypeAttributes( data )
{
	var searchingCatalogAttributeId = document.getElementById("searchingCatalogAttributeId");
	clearList( searchingCatalogAttributeId );
	
	var name_text = "Name";
	var name_value = "catalogname";
	
	
	var catalogTypeAttribs = data.getElementsByTagName("catalog-type-attribute");
	
	//$("#searchingCatalogAttributeId").append("<option value='"+ name_value + "' title='" + name_text + "' text='" + name_text + "'>" + name_text + "</option>");
    for ( var i = 0; i < catalogTypeAttribs.length; i++ )
    {
        var id = catalogTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = catalogTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        searchingCatalogAttributeId.add(option, null);
    }
}


// 

//----------------------------------------------------------------
// Loading VaccinesType Attributes
//----------------------------------------------------------------

function getVaccinesTypeAttribute()
{
	loadAllVaccines();
	
	var catalogTypeId = document.getElementById('catalogTypeId').value;
	
	//alert( catalogTypeId );
	
	if( catalogTypeId == "0" )
	{
		return;
	}
		
	$.post("getCatalogTypeAttribute.action",
			{
				id:catalogTypeId
			},
			function(data)
			{
				populateVaccinesTypeAttributes( data );
			},'xml');	
}

function populateVaccinesTypeAttributes( data )
{
	var searchingCatalogAttributeId = document.getElementById("searchingCatalogAttributeId");
	clearList( searchingCatalogAttributeId );
	
	var name_text = "Name";
	var name_value = "catalogname";
	
	
	var catalogTypeAttribs = data.getElementsByTagName("catalog-type-attribute");
	
	$("#searchingCatalogAttributeId").append("<option value='"+ name_value + "' title='" + name_text + "' text='" + name_text + "'>" + name_text + "</option>");
	 for ( var i = 0; i < catalogTypeAttribs.length; i++ )
	 {
	     var id = catalogTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
	     var name = catalogTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
			
	     var option = document.createElement("option");
	     option.value = id;
	     option.text = name;
	     option.title = name;
	     searchingCatalogAttributeId.add(option, null);
	 }
}


//----------------------------------------------------------------
//On LoadAllCatalogs
//----------------------------------------------------------------

function loadAllCatalogs()
{
	var catalogType = document.getElementById('catalogType');
	var catalogTypeId = catalogType.options[ catalogType.selectedIndex ].value;
	
	//document.getElementById('searchText').value == "";
	
	document.getElementById("searchCatalogText").value = "";

	
	if( catalogTypeId == 0 )
	{	
		return;
	}
	
	showById('selectDiv');

	jQuery('#loaderDiv').show();
	contentDiv = 'listCatalogDiv';
	isAjax = true;
	
	jQuery('#listCatalogDiv').load('getCatalogList.action',{
		listAll:true,
		catalogTypeId:catalogTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listCatalogDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}


//----------------------------------------------------------------
//On LoadAllVaccines
//----------------------------------------------------------------

function loadAllVaccines()
{
	var catalogTypeId = document.getElementById('catalogTypeId').value;
	
	document.getElementById("searchCatalogText").value = "";
	
	//alert( catalogTypeId );
	
	if( catalogTypeId == 0 )
	{	
		return;
	}
	
	showById('selectDiv');

	jQuery('#loaderDiv').show();
	contentDiv = 'listCatalogDiv';
	isAjax = true;
	
	jQuery('#listCatalogDiv').load('getCatalogList.action',{
		listAll:true,
		catalogTypeId:catalogTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listCatalogDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}



//----------------------------------------------------------------
//Load Catalogs On Filter by catalogType Attribute and Cataog Name
//----------------------------------------------------------------
function loadCatalogsByFilter( )
{
	var catalogType = document.getElementById('catalogType');
	var catalogTypeId = catalogType.options[ catalogType.selectedIndex ].value;
	
	var searchText = document.getElementById('searchCatalogText').value;
	
	if( catalogTypeId == 0 )
	{	
		return;
	}
	
	var catalogTypeAttribute = document.getElementById('searchingCatalogAttributeId');
	var catalogTypeAttributeId = catalogTypeAttribute.options[ catalogTypeAttribute.selectedIndex ].value;
	
	
	showById('selectDiv');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listCatalogDiv';
	isAjax = true;
	
	jQuery('#listCatalogDiv').load('getCatalogList.action',{		
		catalogTypeId:catalogTypeId,
		catalogTypeAttributeId:catalogTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listCatalogDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}







//----------------------------------------------------------------
//Load Vaccines On Filter by Vaccines Attribute and Vaccines Name
//----------------------------------------------------------------

function loadVaccinesByFilter()
{
	var catalogTypeId = document.getElementById('catalogTypeId').value;
	
	var searchText = document.getElementById('searchCatalogText').value;
	
	if( catalogTypeId == 0 )
	{	
		return;
	}
	
	var catalogTypeAttribute = document.getElementById('searchingCatalogAttributeId');
	var catalogTypeAttributeId = catalogTypeAttribute.options[ catalogTypeAttribute.selectedIndex ].value;
	
	
	showById('selectDiv');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listCatalogDiv';
	isAjax = true;
	
	jQuery('#listCatalogDiv').load('getCatalogList.action',{		
		catalogTypeId:catalogTypeId,
		catalogTypeAttributeId:catalogTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listCatalogDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}





/**
* Hides the document element with the given identifier.
* 
* @param id the element identifier.
*/


function hideVaccineFilter()
{
	hideById('filterCatalogDiv');
	showById('searchingCatalogAttributeTD');
	showById('searchingCatalogTextTD');
	showById('searchCatalogDiv');
	showById('clearCatalogDiv');
}

function hideVaccinesClear()
{
	hideById('clearCatalogDiv');
	hideById('searchCatalogDiv');
	hideById('searchingCatalogTextTD');
	hideById('searchingCatalogAttributeTD');
	
	getVaccinesTypeAttribute();
	
	showById('filterCatalogDiv');
	
}



function hideCatalogFilter()
{
	hideById('filterCatalogDiv');
	showById('searchingCatalogAttributeTD');
	showById('searchingCatalogTextTD');
	showById('searchCatalogDiv');
	showById('clearCatalogDiv');
}

function hideCatalogClear()
{
	hideById('clearCatalogDiv');
	hideById('searchCatalogDiv');
	hideById('searchingCatalogTextTD');
	hideById('searchingCatalogAttributeTD');
	
	getCatalogTypeChange();
	
	showById('filterCatalogDiv');
	
}


function isCatalogEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		loadCatalogsByFilter();
    }   
}

function isVaccinesEnter( e )
{
	if ( e.keyCode == 13) 
    {   
		loadVaccinesByFilter();
    }   
}


function searchingCatalogAttributeOnChange( catalogTypeAttributeId )
{
	//alert( inventoryTypeAttributeId );
	var searchCatalogText = document.getElementById('searchCatalogText').value;
	document.getElementById("searchCatalogText").value = "";
}

//----------------------------------------------------------------
//Add New Catalog
//----------------------------------------------------------------

function showAddCatalogForm()
{
	var catalogType = document.getElementById('catalogType');
	var catalogTypeId = catalogType.options[ catalogType.selectedIndex ].value;
	
	var catalogTypeName = catalogType.options[ catalogType.selectedIndex ].text;
	
	//alert( catalogTypeId );
	
	if( catalogTypeId == 0 )
	{	
		return;
	}
	
	jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
	jQuery('<div id="addEditCatalogFormDiv">' ).load( 'showAddCataLogForm.action?catalogTypeId='+ catalogTypeId ).dialog({
		title: 'Add Catalog Item in ' + catalogTypeName ,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});	
}


function showAddVaccineForm()
{
	var catalogTypeId = document.getElementById('catalogTypeId').value;
	
	var catalogTypeName = document.getElementById('catalogTypeName').value;
	
	//alert( catalogTypeId );
	
	if( catalogTypeId == 0 )
	{	
		return;
	}
	
	jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
	jQuery('<div id="addEditCatalogFormDiv">' ).load( 'showAddCataLogForm.action?catalogTypeId='+ catalogTypeId ).dialog({
		title: 'Add Catalog Item in ' + catalogTypeName ,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});	
}


function addCatalog()
{	
	var catalogTypeName = document.getElementById('catalogTypeName').value;
	
	$.ajax({
    type: "POST",
    url: 'addCatalog.action',
    data: getParamsForDiv('addEditCatalogFormDiv'),
    success: function(json) {
		var type = json.response;
		jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
		
		//getCatalogTypeChange();
		
		if( catalogTypeName == "Vaccines" )
		{
			getVaccinesTypeAttribute();
		}
		else
		{
			getCatalogTypeChange();
		}
    }
   });
  
}


function closeAddCatalogWindow()
{
	jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
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
	
	return params;
}

//----------------------------------------------------------------
//Show Catalog Details
//----------------------------------------------------------------


function showCatalogDetails( catalogId , catalogName, catalogTypeName )
{
	var catalogName = catalogName;
	
	jQuery('#catalogDetailsDiv').dialog('destroy').remove();
	jQuery('<div id="catalogDetailsDiv">' ).load( 'getCatalogDetails.action?id='+ catalogId ).dialog({
		title: catalogTypeName + " , " + catalogName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
}

function closeCatalogDetailsWindow()
{
	jQuery('#catalogDetailsDiv').dialog('destroy').remove();
}

//----------------------------------------------------------------
//Update Catalog
//----------------------------------------------------------------

function showUpdateCatalogForm( catalogId, catalogName, catalogTypeName )
{
	
	var catalogName = catalogName;
	
	jQuery('#catalogDetailsDiv').dialog('close');
	
	jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
	jQuery('<div id="addEditCatalogFormDiv">' ).load( 'showUpdateCatalogForm.action?id='+ catalogId ).dialog({
		title: 'Edit ' + catalogTypeName + " , " + catalogName,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
}

function updateCatalog()
{
	var catalogId = document.getElementById('catalogID').value;
	var catalogName = document.getElementById('catalogName').value;
	var catalogTypeName = document.getElementById('catalogTypeName').value;
	
	$.ajax({
    type: "POST",
    url: 'updateCatalog.action',
    data: getParamsForDiv('addEditCatalogFormDiv'),
    success: function( json ) {
	
		jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
		
		//getCatalogTypeChange();
		
		if( catalogTypeName == "Vaccines" )
		{	
			getVaccinesTypeAttribute();
		}
		else
		{
			getCatalogTypeChange();
		}
		
		
		showCatalogDetails( catalogId ,catalogName );
		
		}
	
	});
}

function closeCatalogUpdateWindow()
{
	var catalogId = document.getElementById('catalogID').value;
	var catalogName = document.getElementById('catalogName').value;
	
	jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
	showCatalogDetails( catalogId ,catalogName );
}


//-----------------------------------------------------------------------------
//Remove catalog
//-----------------------------------------------------------------------------
function removeCatalog( catalogId , catalogName , catalogTypeName )
{
	var itemName = catalogName;
	
	var result = window.confirm( i18n_confirm_delete_catalog + "\n\n" + itemName );
	
	if ( result )
	{
		$.ajax({
		    type: "POST",
		    url: 'removeCatalog.action?id='+ catalogId,
		    	//data: getParamsForDiv('addEditCatalogFormDiv'),
		    	success: function( json ) {
				jQuery('#addEditCatalogFormDiv').dialog('destroy').remove();
				
					// for load vaccines
					if( catalogTypeName == "Vaccines" )
					{
						getVaccinesTypeAttribute();
					}
					else
					{
						getCatalogTypeChange();
					}
				
				}
			});
	}
}







