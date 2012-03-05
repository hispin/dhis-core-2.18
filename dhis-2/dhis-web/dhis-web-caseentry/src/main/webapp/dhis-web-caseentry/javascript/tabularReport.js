
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	hideById('contentDiv');
	setFieldValue('startDate', '');
	setFieldValue('endDate', '');
	
	$.getJSON( 'loadProgramsByOrgunit.action',{}
		, function( json ) 
		{
			clearListById( 'programId' );
			addOptionById( 'programId', '', i18n_please_select );
			setFieldValue('orgunitname', orgUnitNames[0]);
			var preSelectedProgramId = getFieldValue('selectedProgramId');
			for ( i in json.programs ) 
			{ 
				$('#programId').append('<option value=' + json.programs[i].id + '>' + json.programs[i].name + '</option>');
			}

			if( json.programs.length > 0 )
			{
				enable('generateBtn');
			}
			else
			{
				disable('generateBtn');
			}
			
			showCriteria();
			
		} );
}

selection.setListenerFunction( organisationUnitSelected );

function loadProgramStages()
{
	clearListById( 'programStageId' );
	clearListById( 'dataElementIds' );
	
	if( getFieldValue('programId') == '' )
	{
		return;
	}
	$.getJSON( 'loadTabularProgramStages.action',
		{
			programId: getFieldValue('programId')
		}
		, function( json ) 
		{
			addOptionById( 'programStageId', '', i18n_please_select );
			
			for ( i in json.programStages ) 
			{ 
				$('#programStageId').append('<option value=' + json.programStages[i].id + '>' + json.programStages[i].name + '</option>');
			}
		} );
}

function loadDataElements()
{
	clearListById( 'dataElementIds' );
	if( getFieldValue('programStageId') == '' )
	{
		return;
	}
	
	$.getJSON( 'loadDataElements.action',
		{
			programStageId: getFieldValue('programStageId')
		}
		, function( json ) 
		{
			for ( i in json.dataElements ) 
			{ 
				$('#dataElementIds').append('<option value=' + json.dataElements[i].id + '>' + json.dataElements[i].name + '</option>');
			}
		} );
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
		options.push("='" + attrValues[i] + "'")
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

function loadGeneratedReport()
{
	hideCriteria();
	showLoader();
	isAjax = true;
	contentDiv = 'contentDiv';
	
	var params = getParams();
	if( params != '' )
	{
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

function searchTabularReport( event )
{	
	var key = event.keyCode || event.charCode || event.which;
	
	if ( key == 13 || key == 1 ) // Enter
	{
		showById('loaderDiv');
		hideById('gridContent');
		hideById('pagingDiv');
		
		isAjax = true;
		contentDiv = 'gridContent';

		var params = getParams();
		if( params != '' )
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

function getParams()
{
	hideMessage();
	
	var searchingValues = "";
	var listSeachingValues = jQuery("#gridTable input");
	var regExp = new RegExp([]); 
	
	listSeachingValues.each( function( i, item ){
		if( item.value!= '' )
		{
			var value = item.value;
			var flag = value.match(/[>|>=|<|<=|=|!=]'[%]?.+[%]?'/);
			
			if( flag == null )
			{
				setMessage( i18n_syntax_error_in_search_value );
				item.style.backgroundColor = '#ffcc00';
				return "";
			}
			else
			{
				item.style.backgroundColor = '#ffffff';
				searchingValues += "searchingValues=" + item.id + "_" + htmlEncode( item.value ) + "&";
			}
		}
	});
	
	var dataElementIds = "";
	var listDataElementIds = jQuery( "select[id=dataElementIds] option:selected" );
	listDataElementIds.each( function( i, item ){
		dataElementIds += "dataElementIds=" + item.value;
		dataElementIds += ( i < listDataElementIds.length - 1 ) ? "&" : "";
	});
	
	return searchingValues + dataElementIds
				+ "&programStageId=" + getFieldValue('programStageId')
				+ "&startDate=" + getFieldValue('startDate')
				+ "&endDate=" + getFieldValue('endDate');
}
