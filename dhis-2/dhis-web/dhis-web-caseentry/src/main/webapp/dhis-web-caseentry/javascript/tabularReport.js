
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	setInnerHTML( 'contentDiv' , '');
	setFieldValue( 'orgunitname', orgUnitNames[0] );
			
	showCriteria();
}

selection.setListenerFunction( organisationUnitSelected );


// ---------------------------------------------------------------------
// Load patient-identifiers && patient-attributes
// ---------------------------------------------------------------------

function getTabularParams()
{
	clearListById( 'availableIdenIds' );
	clearListById( 'availableAttrIds' );
	clearListById( 'programStageId');
	if( getFieldValue('programStageId') == '' )
	{
		return;
	}
	else
	{		
		clearListById( 'availableAttrIds' );
		$.getJSON( 'getTabularParams.action',
			{
				programId: getFieldValue('programId')
			}
			, function( json ) 
			{
				// Load patient-identifier-types
				for ( i in json.identifierTypes ) 
				{ 
					$('#availableIdenIds').append('<option value="iden_' + json.identifierTypes[i].id + '_" >' + json.identifierTypes[i].name + '</option>');
				}
				
				// Load patient-attributes
				for ( i in json.patientAttributes ) 
				{ 
					$('#availableAttrIds').append('<option value="attr_' + json.patientAttributes[i].id + '_" >' + json.patientAttributes[i].name + '</option>');
				}
				
				// Load program-stages
				var noProgramStage = 0;
				for ( i in json.programStages ) 
				{ 
					if( !json.programStages[i].irregular )
					{
						$('#programStageId').append('<option value=' + json.programStages[i].id + '>' + json.programStages[i].name + '</option>');
						noProgramStage++;
					}
				}
				
				if( noProgramStage > 1 )
				{
					$('#programStageId').prepend('<option value="">' + i18n_please_select_a_program_stage + '</option>');
					$('#programStageId option:first-child').attr("selected", "selected");
				}
				else if( noProgramStage == 1 )
				{
					loadDataElements();
				}
			} );
	}
}

// ---------------------------------------------------------------------
// Load dataelements by stage
// ---------------------------------------------------------------------

function loadDataElements()
{
	clearListById( 'availableDataElementIds' );
	if( getFieldValue('programStageId') == '' )
	{
		return;
	}
	else
	{
		var result = false;
		if( byId( 'dataElementIds' ).options.length > 0 )
		{
			result = window.confirm( i18n_remove_selected_data_elements );
		}
		
		if ( result || byId( 'dataElementIds' ).options.length == 0 )
		{
			clearListById( 'dataElementIds' );
			$.getJSON( 'loadDataElements.action',
				{
					programStageId: getFieldValue('programStageId')
				}
				, function( json ) 
				{
					for ( i in json.dataElements ) 
					{ 
						$('#availableDataElementIds').append('<option value="de_' + json.dataElements[i].id + '_" >' + json.dataElements[i].name + '</option>');
					}
				} );
		}
	}
}


function showCriteria()
{
	$( "#criteriaDiv" ).show( "fast" );
}

function hideCriteria()
{
	$( "#criteriaDiv" ).hide( "fast" );
}

function entryFormContainerOnReady()
{
	jQuery("#gridTable :input").each(function()
		{ 
			if( jQuery(this).attr( 'options' )!= null )
			{
				autocompletedField( jQuery(this).attr('id') );
			}
		});
}

function autocompletedField( idField )
{
	var input = jQuery( "#" +  idField )
	var dataElementId = input.attr( 'dataElementId' );
	var options = new Array();
	var attrValues = input.attr('options').replace('[', '').replace(']', '').split(', ');
	for( var i in attrValues )
	{
		options.push( attrValues[i] )
	}
	options.push(" ");

	input.autocomplete({
			delay: 0,
			minLength: 0,
			source: options,
			select: function( event, ui ) {
				input.val(ui.item.value);
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
				}
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
		.addClass( "small-button" )
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

function validateTabularReport()
{
	$.post( 'validateTabularReport.action',
		{
			facilityLB: getFieldValue('facilityLB')
		}
		, function( json ) 
		{
			if( json.response == 'success' )
			{
				loadGeneratedReport();
			}
			else
			{
				setMessage( json.message );
			}
		} );
}

function loadGeneratedReport()
{
	hideCriteria();
	showLoader();
	isAjax = true;
	contentDiv = 'contentDiv';
	jQuery( "#gridContent" ).html( "" );
	
	var params = getParams();
	if( params != '' )
	{	
		setInnerHTML( 'contentDiv' , '');
		$.ajax({
			   type: "POST",
			   url: "generateTabularReport.action",
			   data: params,
			   dataType: "html",
			   success: function( result ){
					hideLoader();
					jQuery( "#contentDiv" ).html( result );
			   }
			});
	}
}

function onkeypressSearch( event )
{
var key = event.keyCode || event.charCode || event.which;
	
	if ( key == 13 || key == 1 ) // Enter
	{
		searchTabularReport();
	}
}

function searchTabularReport()
{	
	showById('loaderDiv');
	hideById('gridContent');
	hideById('pagingDiv');
	
	isAjax = true;
	contentDiv = 'gridContent';

	var params = getParams();
	if( params == '' )
	{
		hideById('loaderDiv');
	}
	else
	{
		$.ajax({
			   type: "POST",
			   url: "searchTabularReport.action",
			   data: params,
			   dataType: "html",
			   success: function( result ){
					hideById('loaderDiv');
					jQuery( "#gridContent" ).html( result );
					showById( "gridContent" );
					showById('pagingDiv');
			   }
			});
	}
}

function exportTabularReport( type )
{
	var params = getParams();
	if( params != '' )
	{
		var url = "generateTabularReport.action?" + params + "&type=" + type;
		window.location.href = url;
	}
}

function onchangeOrderBy( elementId )
{
	searchTabularReport();
	
	var element = jQuery( "#" + elementId );
	var isAcs = jQuery( element ).attr( 'orderBy' );
	if( isAcs == 'true')
	{
		element.src = "../images/desc.gif";
		jQuery( element ).attr( 'orderBy' ,'false' );
	}
	else
	{
		element.src = "../images/asc.gif";
		jQuery( element ).attr( 'orderBy' ,'true' );
	}
}

function getParams()
{
	hideMessage();
	
	var searchingValues = "";
	var listSeachingValues = jQuery("#gridTable input[type=text]");
	
	if( listSeachingValues.length > 0 )
	{
		listSeachingValues.each( function( i, item ){
			var value = getFormula( item.value );
			searchingValues += "&searchingValues=" + item.id + "_" ;
			if( item.value != '' )
			{
				searchingValues += htmlEncode( value );
			}
		});
	}
	else
	{
		var listIdentifierTypes = jQuery( "select[id=selectedIdenIds] option" );
		listIdentifierTypes.each( function( i, item ){
			searchingValues += "searchingValues=" + item.value;
			searchingValues += ( i < listIdentifierTypes.length - 1 ) ? "&" : "";
		});
		
		var listPatientAttributes = jQuery( "select[id=selectedAttrIds] option" );
		listPatientAttributes.each( function( i, item ){
			searchingValues += "searchingValues=" + item.value;
			searchingValues += ( i < listPatientAttributes.length - 1 ) ? "&" : "";
		});
		
		var listDataElementIds = jQuery( "select[id=dataElementIds] option" );
		listDataElementIds.each( function( i, item ){
			searchingValues += "searchingValues=" + item.value;
			searchingValues += ( i < listDataElementIds.length - 1 ) ? "&" : "";
		});
	}
	
	var orderByOrgunitAsc = jQuery( '#orderByOrgunitAsc' ).attr('orderBy');
	if( orderByOrgunitAsc == null )
	{
		orderByOrgunitAsc = 'true';
	}
	
	var orderByExecutionDateByAsc = jQuery( '#orderByExecutionDateByAsc' ).attr('orderBy');
	if( orderByExecutionDateByAsc == null )
	{
		orderByExecutionDateByAsc = 'true';
	}
	
	return searchingValues + "&programStageId=" + getFieldValue('programStageId')
				+ "&startDate=" + getFieldValue('startDate')
				+ "&endDate=" + getFieldValue('endDate')
				+ "&facilityLB=" + getFieldValue('facilityLB')
				+ "&orderByOrgunitAsc=" + orderByOrgunitAsc
				+ "&orderByExecutionDateByAsc=" + orderByExecutionDateByAsc
				+ "&level=" + getFieldValue('level');
}

function getFormula( value )
{
	if( value.indexOf('"') != value.lastIndexOf('"') )
	{
		value = value.replace(/"/g,"'");
	}
	if( value.indexOf("'") == value.lastIndexOf("'") )
	{
		value += "'";
		var flag = value.match(/[>|>=|<|<=|=|!=]+[ ]*/);
	
		if( flag == null )
		{
			value = "='"+ value;
		}
		else
		{
			value.replace( flag, flag + "'")
		}
	}
	else
	{
		var flag = value.match(/[>|>=|<|<=|=|!=]+/);
	
		if( flag == null )
		{
			value = "="+ value;
		}
	}
	return value;
}

function clearFilter()
{
	var listSeachingValues = jQuery("#gridTable input[type=text]");
	listSeachingValues.each( function( i, item ){
		item.value = '';
	});	
}


//------------------------------------------------------------------------------
// Filter data-element
//------------------------------------------------------------------------------

function filterDE( event, value, fieldName )
{
	var field = byId(fieldName);
	for ( var index = 0; index < field.options.length; index++ )
    {
		var option = field.options[index];
		
		if(value.length == 0 )
		{
			option.style.display = "block";
		}
		else
		{
			if (option.text.toLowerCase().indexOf( value.toLowerCase() ) != -1 )
			{
				option.style.display = "block";
			}
			else
			{
				option.style.display = "none";
			}
		}
    }	    
}

function toogleTB(tbody)
{
	jQuery( '#' + tbody ).toggle();
}
