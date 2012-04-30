
jQuery(document).ready(	function(){
		validation( 'catalogForm', function(form){
			form.submit();
		}, function(){
			isSubmit = true;
			
			});
		
		checkValueIsExist( "name", "validateCatalog.action");
	});


//-----------------------------------------------------------------------------
//View catalog type change
//-----------------------------------------------------------------------------

function catalogTypeChange()
{
    var catalogTypeList = document.getElementById("catalogType");
    var catalogTypeId = catalogTypeList.options[ catalogTypeList.selectedIndex ].value;
	
    hideById('addCatalogFormDiv');
    jQuery('#loaderDiv').show();
    contentDiv = 'addCatalogFormDiv';
	
	jQuery('#addCatalogFormDiv').load('showAddCataLogForm.action',
		{
			catalogTypeId:catalogTypeId
		}, function()
		{
			showById('addCatalogFormDiv');
			jQuery('#loaderDiv').hide();
		});
	hideLoader();
}

//-----------------------------------------------------------------------------
//View details
//-----------------------------------------------------------------------------

function showCatalogDetails( catalogId )
{
	/*
	jQuery.getJSON( 'getCatalogDetails.action', { id: catalogId }, function ( json ) {
		setInnerHTML( 'nameField', json.catalog.name );	
		setInnerHTML( 'descriptionField', json.catalog.description );
		setInnerHTML( 'catalogTypeField', json.catalog.catalogType );   
	   
		showDetails();
	});
	*/
	
	
	 $('#detailsCatalogInfo').load("getCatalogDetails.action", 
				{
					id:catalogId
				}
				, function( ){
				}).dialog({
					title: i18n_catalog_details,
					maximize: true, 
					closable: true,
					modal:true,
					overlay:{background:'#000000', opacity:0.1},
					width: 500,
					height: 500
				});;
}

//-----------------------------------------------------------------------------
//Remove catalog
//-----------------------------------------------------------------------------
function removeCatalog( catalogId, name )
{
	removeItem( catalogId, name, i18n_confirm_delete, 'removeCatalog.action' );	
}

//----------------------------------------------------------------
//	Update Catalog
//----------------------------------------------------------------
/*
unction showUpdateCatalogForm( catalogId )
{
	setInnerHTML('addCatalogFormDiv', '');
				
	jQuery('#loaderDiv').show();
	jQuery('#addCatalogFormDiv').load('showUpdateCatalogForm.action',
		{
			catalogId:catalogId
		}, function()
		{
			showById('addCatalogFormDiv');
		});
	hideLoader();
}
*/

