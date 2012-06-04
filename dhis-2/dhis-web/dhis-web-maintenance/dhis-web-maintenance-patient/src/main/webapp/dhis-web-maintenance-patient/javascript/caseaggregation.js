
//------------------------------------------------------------------------------
// Get dataelements by dataset
//------------------------------------------------------------------------------

function getDataElementsByDataset()
{
	var dataSets = document.getElementById( 'dataSets' );
	var dataSetId = dataSets.options[ dataSets.selectedIndex ].value;
	clearListById('aggregationDataElementId');
	
	if( dataSetId == "" ){
		disable( 'dataElementsButton' );
		setFieldValue( 'aggregationDataElementInput','');
		return;
	}

	jQuery.getJSON( 'getDataElementsByDataset.action', 
		{ 
			id:dataSetId 
		}, function( json )
        {
			var de = byId( 'aggregationDataElementId' );
			clearListById( 'aggregationDataElementId' );
		  
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
			
			autoCompletedField();
		});
}

function autoCompletedField()
{
	var select = jQuery( "#aggregationDataElementId" );
	$( "#dataElementsButton" ).unbind('click');
	enable( 'dataElementsButton' );
	var selected = select.children( ":selected" );
	var value = selected.val() ? selected.text() : "";
	
	var input = jQuery( "#aggregationDataElementInput" )
		.insertAfter( select )
		.val( value )
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
		});

	input.data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.label + "</a>" )
			.appendTo( ul );
	};

	/* var button = $( "#dataElementsButton" )
		.attr( "title", i18n_show_all_items )
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
		}); */
}

//------------------------------------------------------------------------------
// Get Program Stages
//------------------------------------------------------------------------------

function getParams()
{
	clearListById( 'programStageId' );
  	clearListById( 'dataElements' );
	
	jQuery.getJSON( 'getParamsByProgram.action',{ programId:getFieldValue( 'programId' ) }
		,function( json ) 
		{
			var programstage = jQuery('#programStageId');
			
			for ( i in json.programStages ) 
			{ 
				var id = json.programStages[i].id;
				var formularId = "[PS:" + id + "]";
				var name = json.programStages[i].name;

				programstage.append( "<option value='" + id + "' title='" + name + "'>" + name + "</option>" );
			}
			
			if( json.programStages.length > 0 )
			{
				programstage.prepend( "<option value='' title='" + i18n_all + "'>" + i18n_all + "</option>" );
				byId('programStageId').options[0].selected = true;
				getPatientDataElements();
			}  
			
			clearListById( 'caseProperty' );
			var type = jQuery('#programId option:selected').attr('type');
			if( type!='3')
			{
				var caseProperty = jQuery( '#caseProperty' );
				for ( i in json.fixedAttributes )
				{
					var id = json.fixedAttributes[i].id;
					var name = json.fixedAttributes[i].name;
					
					caseProperty.append( "<option value='" + id + "' title='" + name + "'>" + name + "</option>" );
				}
				
				for ( i in json.patientAttributes )
				{ 
					var id = json.patientAttributes[i].id;
					var name = json.patientAttributes[i].name;
					var suggested = json.patientAttributes[i].suggested;
					
					caseProperty.append( "<option value='" + id + "' title='" + name + "' suggested='" + suggested + "'>" + name + "</option>" );	
				}
			}
		});
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage
//------------------------------------------------------------------------------

function getPatientDataElements()
{
	clearListById( 'dataElements' );
	var programStage = document.getElementById( 'programStage' );
	jQuery.getJSON( 'getPatientDataElements.action',
		{ 
			programId:getFieldValue( 'programId' ),
			progamStageId: getFieldValue('progamStageId')  
		}
		,function( json )
		{
			var dataElements = jQuery('#dataElements');
			for ( i in json.dataElements )
			{ 
				dataElements.append( "<option value='" + json.dataElements[i].id + "' title='" + json.dataElements[i].name + "' suggested='" + json.dataElements[i].optionset + "'>" + json.dataElements[i].name + "</option>" );
			}
		});
}

//-----------------------------------------------------------------
// Insert items into Condition
//-----------------------------------------------------------------

function insertDataElement( element )
{
	var progamId = getFieldValue('programId');
	var progamStageId = getFieldValue('programStageId');
	progamStageId = ( progamStageId == "" ) ? "*" : progamStageId;
	var dataElementId = element.options[element.selectedIndex].value;
	
	insertTextCommon( 'aggregationCondition', "[DE:" + progamId + "." + progamStageId + "." + dataElementId + "]" );
	getConditionDescription();
}

function insertInfo( element, isProgramStageProperty )
{
	var id = "";
	if( isProgramStageProperty )
	{
		id = getFieldValue('programId');
	}
	else
	{
		id = getFieldValue('programStageId');
	}
	
	value = element.options[element.selectedIndex].value.replace( '*', id );
	insertTextCommon('aggregationCondition', value );
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
		setInnerHTML( 'nameField', json.caseAggregation.name );	
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
	
	var suggestedValues = jQuery('select[id=' + sourceId + '] option:selected').attr('suggested');	
	if( suggestedValues )
	{
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
