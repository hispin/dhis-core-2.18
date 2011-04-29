
jQuery(document).ready(	function(){
	validation( 'programValidationForm', function( form ){			
		form.submit();
	});
	
	var isSingle = eval(jQuery.getUrlVars('value')['single']);
	
	if( isSingle || getFieldValue('rightSide') == '1==1')
	{
		hideById('rightSideDiv');
		setFieldValue('rightSide','1==1');
	}
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramValidationDetails ( programValidationId )
{
    var request = new Request();
    request.setResponseTypeXML( 'programValdiation' );
    request.setCallbackSuccess( programValdiationReceived );
    request.send( 'getProgramValidation.action?validationId=' + programValidationId );
}

function programValdiationReceived( programValidationElement )
{
	setInnerHTML( 'idField', getElementValue( programValidationElement, 'id' ) );
	setInnerHTML( 'descriptionField', getElementValue( programValidationElement, 'description' ) );	
    setInnerHTML( 'leftSideField', getElementValue( programValidationElement, 'leftSide' ) );
	setInnerHTML( 'rightSideField', getElementValue( programValidationElement, 'rightSide' ) );
	setInnerHTML( 'programField', getElementValue( programValidationElement, 'program' ) );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove ProgramValidation
// -----------------------------------------------------------------------------

function removeProgramValidation( programValidationId, name )
{
	removeItem( programValidationId, name, i18n_confirm_delete, 'removeProgramValidation.action' );	
}

//-----------------------------------------------------------------
// Insert items data-element
//-----------------------------------------------------------------

function insertDataElement( element, target )
{
	byId(target).value += " " + element.options[element.selectedIndex].value + " ";
}


//------------------------------------------------------------------------------
// Get DataElements of Program-Stage into left-side
//------------------------------------------------------------------------------

function getLeftPrgramStageDataElements()
{
  var programStage = document.getElementById( 'leftStage' );
  var psId = programStage.options[ programStage.selectedIndex ].value;
  if( psId == '')
  {
	return;
  }
  
  var requestString = 'getPSDataElements.action?psId=' + psId;

  var request = new Request();
  request.setResponseTypeXML( 'leftSideDE' );
  request.setCallbackSuccess( getLeftPrgramStageDataElementsCompleted );

  request.send( requestString );	
}

function getLeftPrgramStageDataElementsCompleted( dataelementElement )
{
  var programstageDE = document.getElementById( 'leftSideDE' );
  
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

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage into right-side
//------------------------------------------------------------------------------

function getRightPrgramStageDataElements()
{
  var programStage = document.getElementById( 'rightStage' );
  var psId = programStage.options[ programStage.selectedIndex ].value;
  if( psId == '')
  {
	return;
  }
  
  var requestString = 'getPSDataElements.action?psId=' + psId;

  var request = new Request();
  request.setResponseTypeXML( 'rightSideDE' );
  request.setCallbackSuccess( getRightPrgramStageDataElementsCompleted );

  request.send( requestString );	
}

function getRightPrgramStageDataElementsCompleted( dataelementElement )
{
  var programstageDE = document.getElementById( 'rightSideDE' );
  
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


$.extend({
  getUrlVars: function(){
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
      hash = hashes[i].split('=');
      vars.push(hash[0]);
      vars[hash[0]] = hash[1];
    }
    return vars;
  },
  getUrlVar: function(name){
    return $.getUrlVars()[name];
  }
});

