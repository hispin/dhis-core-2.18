
// -----------------------------------------------------------------------------
// Delete Survey
// -----------------------------------------------------------------------------

var tmpSurveyId;

var tmpSource;

function removeSurvey( surveyId, surveyName )
{
  var result = window.confirm( i18n_confirm_delete + '\n\n' + surveyName );

  if ( result )
  {
    //window.location.href = 'delSurvey.action?surveyId=' + surveyId;
    
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( removeSurveyCompleted );
    
    var requestString = 'delSurvey.action?surveyId=' + surveyId;
                      
    request.send( requestString ); 
  }
}


function removeSurveyCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;
  
  if ( type == 'error' )
  {
    //document.getElementById( 'message' ).innerHTML = message;
    //document.getElementById( 'message' ).style.display = 'block';
    setFieldValue( 'warningField', message );
        
    showWarning();
  }
  else
  {
  	window.location.href = 'index.action';
  }
}


function validateAddSurvey()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addSurveyValidationCompleted ); 
  
  var requestString = 'validateSurvey.action?name=' + document.getElementById( 'name' ).value +
                      '&shortName=' + document.getElementById( 'shortName' ).value;
                      
  request.send( requestString );
  return false;
}

function addSurveyValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      // Both edit and add form has id='dataSetForm'  
      
      var selectedList = document.getElementById( 'selectedList' );
      for(var k=0;k<selectedList.length;k++)
      {
        selectedList.options[k].selected = "true";
      }  
      document.forms['addSurveyForm'].submit();
  } 
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}


function validateEditSurvey()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( editSurveyValidationCompleted );

  var requestString = 'validateSurvey.action?name=' + document.getElementById( 'name' ).value +
                      '&shortName=' + document.getElementById( 'shortName' ).value +
                      '&url=' + document.getElementById( 'url' ).value +
  		              '&surveyId=' + document.getElementById( 'surveyId' ).value;

  request.send( requestString );

  return false;
}
function editSurveyValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      // Both edit and add form has id='dataSetForm'
      var selectedList = document.getElementById( 'selectedList' );
      for(var k=0;k<selectedList.length;k++)
      {
        selectedList.options[k].selected = "true";
      } 
      
      document.forms['editSurveyForm'].submit();
  }
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}

// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------

function initLists()
{
    var id;
	
	var list = document.getElementById( 'selectedList' );
	
    for ( id in surveyMembers )
    {
        list.add( new Option( surveyMembers[id], id ), null );
    }	
	
    list = document.getElementById( 'availableList' );
    
    for ( id in availableIndicators )
    {
        list.add( new Option( availableIndicators[id], id ), null );
    }
}

function filterSurveyMembers()
{
	var filter = document.getElementById( 'surveyMembersFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    
    list.options.length = 0;
    
    for ( var id in surveyMembers )
    {
        var value = surveyMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableIndicators()
{
	var filter = document.getElementById( 'availableIndicatorsFilter' ).value;
    var list = document.getElementById( 'availableList' );
    
    list.options.length = 0;
    
    for ( var id in availableIndicators )
    {
        var value = availableIndicators[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addSurveyMembers()
{
	var list = document.getElementById( 'availableList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        surveyMembers[id] = availableIndicators[id];
        
        delete availableIndicators[id];        
    }
    
    filterSurveyMembers();
    filterAvailableIndicators();
}

function removeSurveyMembers()
{
	var list = document.getElementById( 'selectedList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableIndicators[id] = surveyMembers[id];
        
        delete surveyMembers[id];        
    }
    
    filterSurveyMembers();
    filterAvailableIndicators();
}

function filterByIndicatorGroup( selectedIndicatorGroup )
{
  var request = new Request();

  var requestString = 'filterAvailableIndicatorsByIndicatorGroup.action';
  
  var params = 'indicatorGroupId=' + selectedIndicatorGroup;

  var selectedList = document.getElementById( 'selectedList' );

  for ( var i = 0; i < selectedList.options.length; ++i)
  {
  	params += '&selectedIndicators=' + selectedList.options[i].value;
  }

  // Clear the list
  var availableList = document.getElementById( 'availableList' );

  availableList.options.length = 0;

  request.setResponseTypeXML( 'indicatorGroup' );
  request.setCallbackSuccess( filterByIndicatorGroupCompleted );
  request.sendAsPost( params );
  request.send( requestString );
}

function filterByIndicatorGroupCompleted( indicatorGroup )
{
  var indicators = indicatorGroup.getElementsByTagName( 'indicators' )[0];
  var indicatorList = indicators.getElementsByTagName( 'indicator' );

  var availableList = document.getElementById( 'availableList' );
  
  for ( var i = 0; i < indicatorList.length; i++ )
  {
    var indicator = indicatorList[i];
    var name = indicator.firstChild.nodeValue;
    var id = indicator.getAttribute( 'id' );

    availableList.add( new Option( name, id ), null );
  }
}

function showSurveyDetails( surveyId )
{
  var request = new Request();
  request.setResponseTypeXML( 'survey' );
  request.setCallbackSuccess( surveyRecieved );
  request.send( 'getSurvey.action?surveyId=' + surveyId );
}

function surveyRecieved( surveyElement )
{
  setFieldValue( 'idField', getElementValue( surveyElement, 'id' ) );
  setFieldValue( 'nameField', getElementValue( surveyElement, 'name' ) ); 
  setFieldValue( 'indicatorCountField', getElementValue( surveyElement, 'indicatorCount' ) );
  setFieldValue( 'descriptionField', getElementValue( surveyElement, 'description' ) );
  var urlOrg = getElementValue( surveyElement, 'url' );
 
  if( urlOrg == null || urlOrg.length <=0 )
  {
    urlOrg = 'NONE';
    setFieldValue( 'urlField', urlOrg );
  }
  else
  {  
    var occur = urlOrg.match("http://");
    if( occur == null || occur.length <=0 )
      setFieldValue( 'urlField', "<a href='http://"+urlOrg+"' target='_blank'>"+urlOrg+"</a>" );
    else
      setFieldValue( 'urlField', "<a href='"+urlOrg+"' target='_blank'>"+urlOrg+"</a>" );
  }   
      
    showDetails();
}

