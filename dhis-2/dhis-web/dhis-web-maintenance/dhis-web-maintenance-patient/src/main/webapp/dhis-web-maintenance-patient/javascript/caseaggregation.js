
//------------------------------------------------------------------------------
// Get dataelements by dataset
//------------------------------------------------------------------------------

function getDataElementsByDataset()
{
	var dataSets = document.getElementById( 'dataSets' );
	var dataSetId = dataSets.options[ dataSets.selectedIndex ].value;
	clearList( byId('aggregationDataElementId'));
	
	if( dataSetId == "" ){
		disabled( 'dataElementsButton' );
		return;
	}

	jQuery.getJSON( 'getDataElementsByDataset.action', 
		{ 
			id:dataSetId 
		}, function( json )
        {
			var de = byId( 'aggregationDataElementId' );
			clearList( de );
		  
			for ( i in json.dataElements ) 
			{ 
				var id = json.dataElements[i].id;
				var name = json.dataElements[i].name;

				var option = document.createElement("option");
				option.value = id;
				option.text = name;
				option.title = name;
				
				de.add(option, null);  			
			}
			
			autoCompletedField( '' );
		});
}

function autoCompletedField( id )
{
	var select = jQuery( "#aggregationDataElementId" );
	$( "#dataElementsButton" ).unbind('click');
	enable( 'dataElementsButton' );
	hideById( id );
	
	var input = jQuery( "#aggregationDataElementInput" )
		.insertAfter( select )
		.val( "" )
		.autocomplete({
			delay: 0,
			minLength: 0,
			source: function( request, response ) {
				var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
				response( select.children( "option" ).map(function() {
					var text = $( this ).text();
					if ( this.value && ( !request.term || matcher.test(text) ) )
						return {
							label: text,
							value: text,
							option: this
						};
				}) );
			},
			select: function( event, ui ) {
				ui.item.option.selected = true;
			},
			change: function( event, ui ) {
				if ( !ui.item ) {
					var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
						valid = false;
					select.children( "option" ).each(function() {
						if ( $( this ).text().match( matcher ) ) {
							this.selected = valid = true;
							return false;
						}
					});
					if ( !valid ) {
						// remove invalid value, as it didn't match anything
						$( this ).val( "" );
						select.val( "" );
						input.data( "autocomplete" ).term = "";
						return false;
					}
				}
			}
		})
		.addClass( "ui-widget ui-widget-content ui-corner-left" );

	input.data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.label + "</a>" )
			.appendTo( ul );
	};

	showById('dataElementsButton');
	var button = $( "#dataElementsButton" )
		.attr( "title", i18n_show_all_items )
		.button({
			icons: {
				primary: "ui-icon-triangle-1-s"
			},
			text: false
		})
		.removeClass( "ui-corner-all" )
		.addClass( "ui-corner-right ui-button-icon" )
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

//------------------------------------------------------------------------------
// Get Program Stages
//------------------------------------------------------------------------------

function getProgramStages()
{
	clearListById( 'programStage' );
  	clearListById( 'programstageDE' );
	
	var program = document.getElementById( 'program' );
	var programId = program.options[ program.selectedIndex ].value;
	if( programId == '0' ){
		return;  
	}

	$.get( 'getProgramStages.action', { programId:programId }, getProgramStagesCompleted, 'xml');
}

function getProgramStagesCompleted( programstageElement )
{
	var programstage = document.getElementById( 'programStage' );
	var programstageList = $(programstageElement).find( 'programstage' );

	$( programstageList ).each( function( i, item )
	{
		var id = $( item ).find("id").text();
		var name = $( item ).find("name").text();

		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;

		programstage.add(option, null);       	
	});

	if( programstage.options.length > 0 )
	{
		programstage.options[0].selected = true;
		getPrgramStageDataElements();
	}   
}


function getProgramStagesForFormula()
{
	clearListById( 'programStageFormula' );
	
	var program = document.getElementById( 'programFormula' );
	var programId = program.options[ program.selectedIndex ].value;
	if( programId == '0' ){
		return;  
	}

	$.get( 'getProgramStages.action', { programId:programId }, getProgramStagesFomulaCompleted, 'xml' );
}

function getProgramStagesFomulaCompleted( programstageElement )
{
	var programstage = document.getElementById( 'programStageFormula' );
	var programstageList = $(programstageElement).find( 'programstage' );

	$( programstageList ).each( function( i, item )
	{
		var id = $( item ).find("id").text();
		var name = $( item ).find("name").text();

		var option = document.createElement("option");
		option.value = "[PS:" + id + "]";
		option.text = name;
		option.title = name;

		programstage.add(option, null);       	
	});

	if( programstage.options.length > 0 )
	{
		programstage.options[0].selected = true;
	}   
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage
//------------------------------------------------------------------------------

function getPrgramStageDataElements()
{
	clearListById( 'programstageDE' );

	var programStage = document.getElementById( 'programStage' );
	var psId = programStage.options[ programStage.selectedIndex ].value;
	
	$.get( 'getPSDataElements.action', { psId:psId }, getPrgramStageDataElementsCompleted, 'xml' );
}

function getPrgramStageDataElementsCompleted( dataelementElement )
{
	var programstageDE = jQuery('#programstageDE');
	var psDataElements = $(dataelementElement).find( 'dataelement' );

	$( psDataElements ).each( function( i, item )
	{
		var id = $(item).find("id").text();
		var name = $(item).find("name").text();
		var optionset =$(item).find("optionset").text();
		
		programstageDE.append( "<option value='" + id + "' title='" + name + "' suggestedValues='" + optionset + "'>" + name + "</option>" );
	} );	    
}

//-----------------------------------------------------------------
// Insert items into Condition
//-----------------------------------------------------------------

function insertInfo( element )
{
	insertTextCommon('aggregationCondition', element.options[element.selectedIndex].value );
	getConditionDescription();
}

function insertOperator( value )
{
	insertTextCommon('aggregationCondition', ' ' + value + ' ' );
	getConditionDescription();
}

// -----------------------------------------------------------------------------
// Remove Case Aggregation Condition
// -----------------------------------------------------------------------------

function removeCaseAggregation( caseAggregationId, caseAggregationName )
{
	removeItem( caseAggregationId, caseAggregationName, i18n_confirm_delete, 'removeCaseAggregation.action' );
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showCaseAggregationDetails( caseAggregationId )
{
    jQuery.getJSON( 'getCaseAggregation.action', { id:caseAggregationId }, function ( json )
	{
		setInnerHTML( 'descriptionField', json.caseAggregation.description );	
		setInnerHTML( 'operatorField', json.caseAggregation.operator );
		setInnerHTML( 'aggregationDataElementField', json.caseAggregation.aggregationDataElement );
		setInnerHTML( 'optionComboField', json.caseAggregation.optionCombo );	
		setInnerHTML( 'aggregationExpressionField', json.caseAggregation.aggregationExpression );
		
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function getConditionDescription()
{
	$.postUTF8( 'getCaseAggregationDescription.action', 
		{ 
			condition:getFieldValue('aggregationCondition') 
		},function (data)
		{
			byId('aggregationDescription').innerHTML = data;
		},'html');
}

// -----------------------------------------------------------------------------
// Test condition
// -----------------------------------------------------------------------------

function testCaseAggregationCondition()
{
	$.postUTF8( 'testCaseAggregationCondition.action', 
		{ 
			condition:getFieldValue('aggregationCondition') 
		},function (json)
		{
			var type = json.response;
			
			if ( type == "input" )
			{
				showWarningMessage( i18n_run_fail );
			}
			else
			{
				showSuccessMessage( i18n_run_success );
			}
		});
}

function getSuggestedValues( sourceId, targetId )
{
	clearListById( targetId );
	
	var suggestedValues = jQuery('select[id=' + sourceId + '] option:selected').attr('suggestedValues');	
	
	var arrValues = new Array();
	arrValues = suggestedValues.replace(/[//[]+/g,'').replace(/]/g, '').split(', ');

	var suggestedValueSelector = byId( targetId );
	for( var i=0; i< arrValues.length; i++ )
	{
		var option = document.createElement("option");
		var value = jQuery.trim( arrValues[i] );
		option.value = "'" + value + "'";
		option.text = value;
		option.title = value;

		suggestedValueSelector.add(option, null); 
	}
}

function insertSingleValue( elementId )
{
	var element = byId( elementId );
	insertTextCommon('aggregationCondition', "=" + element.options[element.selectedIndex].value );
	getConditionDescription();
}

function insertMultiValues( elementId )
{
	var list = jQuery('select[id=' + elementId + '] option:selected')
	if( list.length > 1 )
	{
		var selectedValues = "";
		list.each(function(){
			selectedValues += jQuery(this).val() + ", ";
		});
		selectedValues = " IN @ " + selectedValues.substring( 0, selectedValues.length - 2) + " #";
		
		insertTextCommon('aggregationCondition', selectedValues );
		getConditionDescription();
	}
	else
	{
		insertSingleValue( elementId );
	}
}

function getCaseAggConditionByDataset()
{
	$.get( 'getCaseAggConditionByDataset.action',
		{
			dataSetId: getFieldValue( 'dataSetId' )
		}
		, function( html ) 
		{
			setInnerHTML('list', html );
		} );
}

function showAddCaseAggregationForm()
{
	window.location.href='showAddCaseAggregationForm.action?dataSetId=' + getFieldValue( 'dataSetId' );
}
