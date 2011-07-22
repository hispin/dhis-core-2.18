
//------------------------------------------------------------------------------
// Get Aggregated Dataelements
//------------------------------------------------------------------------------

function getAggDataElements( )
{
	var dataElementGroup = document.getElementById( 'dataElementGroup' );
	var dataElementGroupId = dataElementGroup.options[ dataElementGroup.selectedIndex ].value;
	if( dataElementGroupId == 0 ){
		clearList( byId('aggregationDataElementId'));
		return;
	}
	$.ajax({
		url: 'getAggDataElements.action?dataElementGroupId=' + dataElementGroupId,
		cache: false,
		dataType: "xml",
		success: getAggDataElementsCompleted
	});
}

function getAggDataElementsCompleted( dataelementElement )
{
  var de = document.getElementById( 'aggregationDataElementId' );
  
  clearList( de );
  
  var dataElementList = dataelementElement.getElementsByTagName( 'dataelement' );
  
  for ( var i = 0; i < dataElementList.length; i++ )
    {
        var id = dataElementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = dataElementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        de.add(option, null);  
    }	    
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

	$.ajax({
		url: 'getProgramStages.action?programId=' + programId,
		cache: false,
		dataType: "xml",
		success: getProgramStagesCompleted
	});  
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

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage
//------------------------------------------------------------------------------

function getPrgramStageDataElements()
{
	clearListById( 'programstageDE' );

	var programStage = document.getElementById( 'programStage' );
	var psId = programStage.options[ programStage.selectedIndex ].value;
	
	$.ajax({
		url: 'getPSDataElements.action?psId=' + psId,
		cache: false,
		dataType: "xml",
		success: getPrgramStageDataElementsCompleted
	});
}

function getPrgramStageDataElementsCompleted( dataelementElement )
{
	var programstageDE = byId('programstageDE');
	var psDataElements = $(dataelementElement).find( 'dataelement' );

	$( psDataElements ).each( function( i, item )
	{
		var id = $(item).find("id").text();
		var name = $(item).find("name").text();
		var type =$(item).find("type").text();

		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		jQuery(option).attr({data:"{type:'"+type+"'}"});
		programstageDE.add(option, null);       	
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
    $.ajax({
		url: 'getCaseAggregation.action?id=' + caseAggregationId,
		cache: false,
		dataType: "xml",
		success: caseAggregationReceived
	});
}

function caseAggregationReceived( caseAggregationElement )
{
	setInnerHTML( 'idField', $( caseAggregationElement).find('id' ).text() );
	setInnerHTML( 'descriptionField', $( caseAggregationElement).find('description' ).text() );	
    setInnerHTML( 'operatorField', $( caseAggregationElement).find('operator' ).text() );
    setInnerHTML( 'aggregationDataElementField', $( caseAggregationElement).find('aggregationDataElement' ).text() );
	setInnerHTML( 'optionComboField', $( caseAggregationElement).find('optionCombo' ).text() );	
    setInnerHTML( 'aggregationExpressionField', $( caseAggregationElement).find('aggregationExpression' ).text() );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function getConditionDescription()
{
	$.post("getCaseAggregationDescription.action",
		{
			condition: getFieldValue('aggregationCondition')
		},
		function (data)
		{
			byId('aggregationDescription').innerHTML = data;
		},'html');
}