
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
  
  var requestString = 'getAggDataElements.action?dataElementGroupId=' + dataElementGroupId;

  var request = new Request();
  request.setResponseTypeXML( 'dataelement' );
  request.setCallbackSuccess( getAggDataElementsCompleted );

  request.send( requestString );
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
  var program = document.getElementById( 'program' );
  var programId = program.options[ program.selectedIndex ].value;
  if(programId == '0'){
	clearList( byId( 'programStage' ) );
	clearList( byId( 'programstageDE' ) );
	return;  
  }
	
  var requestString = 'getProgramStages.action?programId=' + programId;

  var request = new Request();
  request.setResponseTypeXML( 'programStage' );
  request.setCallbackSuccess( getProgramStagesCompleted );

  request.send( requestString );	
}

function getProgramStagesCompleted( programstageElement )
{
  var programstage = document.getElementById( 'programStage' );
  
  clearList( programstage );
  	
  var programstageList = programstageElement.getElementsByTagName( 'programstage' );
 
  for( var i = 0; i < programstageList.length; i++ )
  {
    var id = programstageList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
    var name = programstageList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

    var option = document.createElement("option");
    option.value = id;
    option.text = name;
    option.title = name;
    
    programstage.add(option, null);       	
  }
  
  if( programstage.options.length > 0 )
  {
  	programstage.options[0].selected = true;
    	
   	getPrgramStageDataElements();
  }
  else
  {
   	var programstageDE = document.getElementById( 'programstageDE' );
  
  	clearList( programstageDE );
  }	    
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage
//------------------------------------------------------------------------------

function getPrgramStageDataElements()
{
  var programStage = document.getElementById( 'programStage' );
  var psId = programStage.options[ programStage.selectedIndex ].value;

  var requestString = 'getPSDataElements.action?psId=' + psId;

  var request = new Request();
  request.setResponseTypeXML( 'dataelement' );
  request.setCallbackSuccess( getPrgramStageDataElementsCompleted );

  request.send( requestString );	
}

function getPrgramStageDataElementsCompleted( dataelementElement )
{
  var programstageDE = document.getElementById( 'programstageDE' );
  
  clearList( programstageDE );
  	
  var programstageDEList = dataelementElement.getElementsByTagName( 'dataelement' );
 
  for ( var i = 0; i < programstageDEList.length; i++ )
  {
    var id = programstageDEList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
    var name = programstageDEList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
	var type = programstageDEList[ i ].getElementsByTagName("type")[0].firstChild.nodeValue;

    var option = document.createElement("option");
    option.value = id;
    option.text = name;
    option.title = name;
    jQuery(option).attr({data:"{type:'"+type+"'}"});
    programstageDE.add(option, null);       	
  }	    
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
    var request = new Request();
    request.setResponseTypeXML( 'caseAggregation' );
    request.setCallbackSuccess( caseAggregationReceived );
    request.send( 'getCaseAggregation.action?id=' + caseAggregationId );
}

function caseAggregationReceived( caseAggregationElement )
{
	setInnerHTML( 'idField', getElementValue( caseAggregationElement, 'id' ) );
	setInnerHTML( 'descriptionField', getElementValue( caseAggregationElement, 'description' ) );	
    setInnerHTML( 'operatorField', getElementValue( caseAggregationElement, 'operator' ) );
    setInnerHTML( 'aggregationDataElementField', getElementValue( caseAggregationElement, 'aggregationDataElement' ) );
	setInnerHTML( 'optionComboField', getElementValue( caseAggregationElement, 'optionCombo' ) );	
    setInnerHTML( 'aggregationExpressionField', getElementValue( caseAggregationElement, 'aggregationExpression' ) );
    
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