
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
	
    
    setInnerHTML('addCatalogFormDiv', '');
	showById('addCatalogFormDiv');
    
    
    //setInnerHTML('catalogDataEntryFormDiv', '');
    setInnerHTML('addCatalogFormDiv', '');
    //showById('dataEntryFormDiv');
    showById('addCatalogFormDiv');
    
    //hideById('addCatalogFormDiv');
    //jQuery('#loaderDiv').show();
    //contentDiv = 'addCatalogFormDiv';
    
    
    //jQuery(".stage-object-selected").removeClass('stage-object-selected');
	//var tempCatalogTypeId = jQuery( '#' + catalogTypeId );
	//tempCatalogTypeId.addClass('stage-object-selected');
    
    
    
    showLoader();
	jQuery('#addCatalogFormDiv').load('showAddCataLogForm.action',
		{
			catalogTypeId:catalogTypeId
		}, function()
		{
			
			hideLoader();
			//showById('addCatalogFormDiv');
			//jQuery('#loaderDiv').hide();
		});
	hideLoader();
}

//-----------------------------------------------------------------------------
//View catalog by catalog type change
//-----------------------------------------------------------------------------
function getCatalogByCatalogType( catalogTypeId )
{
	window.location.href = "catalog.action?id=" + catalogTypeId;
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


//-----------------------------------------------------------------
//
//-----------------------------------------------------------------


TOGGLE = {
	    init : function() {
	        jQuery(".togglePanel").each(function(){
	            jQuery(this).next("table:first").addClass("sectionClose");
	            jQuery(this).addClass("close");
	            jQuery(this).click(function(){
	                var table = jQuery(this).next("table:first");
	                if( table.hasClass("sectionClose")){
	                    table.removeClass("sectionClose").addClass("sectionOpen");
	                    jQuery(this).removeClass("close").addClass("open");
	                    window.scroll(0,jQuery(this).position().top);
	                }else if( table.hasClass("sectionOpen")){
	                    table.removeClass("sectionOpen").addClass("sectionClose");
	                    jQuery(this).removeClass("open").addClass("close");
	                }
	            });
	        });
	    }
	};



function entryFormContainerOnReady()
{
	alert( "options");
	var currentFocus = undefined;
	
    if( jQuery("#entryFormContainer") ) {
		
        jQuery("input[name='entryfield'],select[name='entryselect']").each(function(){
            jQuery(this).focus(function(){
                currentFocus = this;
            });
            
            jQuery(this).addClass("inputText");
        });
		
        TOGGLE.init();
				
		jQuery("#entryForm :input").each(function()
		{ 
			if( jQuery(this).attr( 'options' )!= null )
			{
				
				autocompletedField(jQuery(this).attr('id'));
			}
		});
    }
}


function autocompletedField( idField )
{
	var input = jQuery( "#" +  idField )
	var catalogTypeAttributeId = input.attr( 'catalogTypeAttributeId' );
	var options = new Array();
	options = input.attr('options').replace('[', '').replace(']', '').split(', ');
	options.push(" ");

	input.autocomplete({
			delay: 0,
			minLength: 0,
			source: options,
			select: function( event, ui ) {
				input.val(ui.item.value);
				//saveVal( catalogTypeAttributeId );
				input.autocomplete( "close" );
			},
			change: function( event, ui ) {
				if ( !ui.item ) {
					var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
						valid = false;
					for (var i = 0; i < options.length; i++)
					{
						if (options[i].match( matcher ) ) {
							this.selected = valid = true;
							break;
						}
					}
					if ( !valid ) {
						// remove invalid value, as it didn't match anything
						$( this ).val( "" );
						input.data( "autocomplete" ).term = "";
						return false;
					}
				}
				//saveVal( catalogTypeAttributeId );
			}
		})
		.addClass( "ui-widget" );

	this.button = $( "<button type='button'>&nbsp;</button>" )
		.attr( "tabIndex", -1 )
		.attr( "title", i18n_show_all_items )
		.insertAfter( input )
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.addClass( "optionset-small-button" )
		.click(function() {
			// close if already visible
			if ( input.autocomplete( "widget" ).is( ":visible" ) ) {
				input.autocomplete( "close" );
				return;
			}

			// work around a bug (likely same cause as #5265)
			$( this ).blur();

			// pass empty string as value to search for, displaying all results
			input.autocomplete( "search", "" );
			input.focus();
		});
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


 $(document).ready(function() {
                $('#j_username').focus();

                $('#loginForm').bind('submit', function() {
					$('#submit').attr('disabled', 'disabled');
					$('#reset').attr('disabled', 'disabled');

	                sessionStorage.removeItem( 'orgUnitSelected' );
                });
            });
            
*/            
