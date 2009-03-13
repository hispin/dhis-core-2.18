var selectedIndicatorList;
var availableIndicatorList;
var selectedList;
var availableList;

function move( listId ) {
	

  var fromList = document.getElementById(listId);

  if ( fromList.selectedIndex == -1 ) { return; }

  if ( ! availableList ) {
    availableList = document.getElementById('availableList');
  }

  if ( ! selectedList ) {
    selectedList = document.getElementById('selectedList');
  }

  var toList = ( fromList == availableList ? selectedList : availableList );

  while ( fromList.selectedIndex > -1 ) {
    option = fromList.options.item(fromList.selectedIndex);
    fromList.remove(fromList.selectedIndex);
    toList.add(option, null);
  }

}

function moveIndicator( listId ) {
	
  var fromList = document.getElementById(listId);

  if ( fromList.selectedIndex == -1 ) { return; }

  if ( ! availableIndicatorList ) {
    availableIndicatorList = document.getElementById('availableIndicatorList');
  }

  if ( ! selectedIndicatorList ) {
    selectedIndicatorList = document.getElementById('selectedIndicatorList');
  }

  var toList = ( fromList == availableIndicatorList ? selectedIndicatorList : availableIndicatorList );

  while ( fromList.selectedIndex > -1 ) {
    option = fromList.options.item(fromList.selectedIndex);
    fromList.remove(fromList.selectedIndex);
    toList.add(option, null);
  }

}

function submitForm() {

  if ( ! selectedIndicatorList ) {
    selectedIndicatorList = document.getElementById('selectedIndicatorList');
  }  

  if ( ! selectedList ) {
    selectedList = document.getElementById('selectedList');
  }

  // selectAll(availableList);
  selectAll(selectedList);
  selectAll(selectedIndicatorList);

  return false;

}

function selectAll(list) {

  for ( var i = 0, option; option = list.options.item(i); i++ ) {
    option.selected = true;
  }

}

// legend-----------------------------------------------

function validateAddLegend(){
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addLegendValidationCompleted ); 
  
  var name = document.getElementById( 'name' ).value;
  var color = document.getElementById( 'color' ).value;
  var minValue = document.getElementById( 'min' ).value;
  var maxValue = document.getElementById( 'max' ).value;
  var autoMax =  document.getElementById( 'automax' ).checked;
  
  var requestString = "validateLegend.action?nameField=" + name + "&colorField=" + color + "&minField=" + minValue + "&maxField=" + maxValue + "&action=add" + "&autoCreateMax=" + 	autoMax;

  request.send( requestString );  
 
}
function addLegendValidationCompleted(messageElement){
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	 if ( type == 'success' )
 	 {      
      document.forms['addLegend'].submit();
  	 } else if ( type == 'input' )
 		 {
   			 document.getElementById( 'message' ).innerHTML = message;
   			 document.getElementById( 'message' ).style.display = 'block';
  		  }
}

function validateUpdateLegend(){
	var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( updateLegendValidationCompleted ); 
  
  var name = document.getElementById( 'name' ).value;
  var color = document.getElementById( 'color' ).value;
  var minValue = document.getElementById( 'min' ).value;
  var maxValue = document.getElementById( 'max' ).value;
  var autoMax =  document.getElementById( 'automax' ).checked;
  
  var requestString = "validateLegend.action?nameField=" + name + "&colorField=" + color + "&minField=" + minValue + "&maxField=" + maxValue + "&action=update"	+ "&autoCreateMax=" + 	autoMax;
	
  request.send( requestString );
  
  return false;
}

function updateLegendValidationCompleted(messageElement){
	var type = messageElement.getAttribute( 'type' );
	
	var message = messageElement.firstChild.nodeValue;
	 if ( type == 'success' )
 	 {
      // Both edit and add form has id='dataSetForm'      
      document.forms['editLegend'].submit();
  	 } else if ( type == 'input' )
 		 {
   			 document.getElementById( 'message' ).innerHTML = message;
   			 document.getElementById( 'message' ).style.display = 'block';
  		  }
}

// legend set-----------------------------------------------

function validateAddLegendSet(){
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( addLegendSetValidationCompleted ); 

	var name = document.getElementById( 'name' ).value;

	var requestString = "validateLegendSet.action";
	var params = "name=" + name;
	params += "&action=add";
	var selectedDataElementMembers = document.getElementById( 'selectedIndicatorList' );

	for ( var i = 0; i < selectedDataElementMembers.options.length; ++i)
	{
		params += '&indicatorIds=' + selectedDataElementMembers.options[i].value;
	}   
	request.sendAsPost( params );
	request.setResponseTypeXML( 'xmlObject' );  
	request.setCallbackSuccess( addLegendSetValidationCompleted );
	request.send( requestString );  
  
}
function addLegendSetValidationCompleted( xmlObject ){
	var type = xmlObject.getAttribute( 'type' );
	var message = xmlObject.firstChild.nodeValue;
	if ( type == 'success' )
	{    
		submitForm();
		document.forms['addLegendSet'].submit();
	} else 
	{
		setMessage(message);
	}
}

function validateUpdateLegendSet(){

	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( addLegendSetValidationCompleted ); 

	var name = document.getElementById( 'name' ).value;

	var requestString = "validateLegendSet.action";
	var params = "name=" + name;	
	var selectedDataElementMembers = document.getElementById( 'selectedIndicatorList' );

	for ( var i = 0; i < selectedDataElementMembers.options.length; ++i)
	{
		params += '&indicatorIds=' + selectedDataElementMembers.options[i].value;
	}   
	request.sendAsPost( params );
	request.setResponseTypeXML( 'xmlObject' );  
	request.setCallbackSuccess( updateLegendSetValidationCompleted );
	request.send( requestString );  
 
}

function updateLegendSetValidationCompleted(xmlObject){
	var type = xmlObject.getAttribute( 'type' );
	
	var message = xmlObject.firstChild.nodeValue;
	if ( type == 'success' )
	{	   
		submitForm();
		document.forms['editLegendSet'].submit();
	} else
	{
		setMessage(message);
	}
}

function getIndicatorByIndicatorGroup(){

	var indicatorGroupList = document.getElementById( "indicatorGroupId" );
	var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;	
	
	if ( indicatorGroupId != null )
	{
		var url = "getIndicatorByIndicatorGroup.action?indicatorGroupId=" + indicatorGroupId;
		
		var request = new Request();
	    request.setResponseTypeXML( 'indicator' );
	    request.setCallbackSuccess( responseGetIndicatorByIndicatorGroup );
	    request.send( url );	    
	}
	
    
}
function responseGetIndicatorByIndicatorGroup( xmlObject ){

	var indicatorList = document.getElementById( "availableIndicatorList" );
    var indicators = xmlObject.getElementsByTagName( "indicator" );
	
	var selectedList = document.getElementById( "selectedIndicatorList" ).options;
    
	clearList( indicatorList );
	
    for ( var i = 0; i < indicators.length; i++ )
    {
		var id = indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;		
		var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		indicatorList.add(new Option(indicatorName, id), null); 
		for(var j=0;j<selectedList.length;j++){
			if(selectedList[j].value == id){
				indicatorList.remove(i);
			}
		}
		
		 
	}
	
	
	
        
    
}

