// -----------------------------------------------------------------------------
// Filter by data element group
// -----------------------------------------------------------------------------

function getOptionCombos(){
	
	clearListById('availableOptionCombos');
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getOptionCombosReceived );
	request.send( "getOptionCombos.action?dataElementId=" + byId("availableDataElements").value );

}

function getOptionCombosReceived( xmlObject ){
	
	xmlObject = xmlObject.getElementsByTagName('categoryOptions')[0];		
			
	var optionComboList = byId( "availableOptionCombos" );			
			
	optionComboList.options.length = 0;		
	var optionCombos = xmlObject.getElementsByTagName( "categoryOption" );		
	for ( var i = 0; i < optionCombos.length; i++)
	{
		var id = optionCombos[ i ].getAttribute('id');
		var name = optionCombos[ i ].firstChild.nodeValue;			
		var option = document.createElement( "option" );
		option.value = id ;
		option.text = name;
		optionComboList.add( option, null );	
	}
}

// -----------------------------------------------------------------------------
// Filter by data element group
// -----------------------------------------------------------------------------

function filterByDataElementGroup( selectedDataElementGroup )
{
	
	var request = new Request();
	var requestString = 'filterAvailableDataElementsByDataElementGroup.action';
	var params = 'dataElementGroupId=' + selectedDataElementGroup;
	var selectedList = byId( 'selectedDataElements' );
	
	for ( var i = 0; i < selectedList.options.length; ++i)
	{
		var selectedValue = selectedList.options[i].value;
	  
		var id = selectedValue.substring(selectedValue.indexOf(".") + 1, selectedValue.length);

		params += '&selectedDataElements=' + id;
	 }

	  // Clear the Dataelement list
	  var availableList = document.getElementById( 'availableDataElements' );
	  availableList.options.length = 0;

	  // Clear the OptionCombo list
	  availableList = document.getElementById( 'availableOptionCombos' );
	  availableList.options.length = 0;
	  
	  request.setResponseTypeXML( 'dataElementGroup' );
	  request.setCallbackSuccess( filterByDataElementGroupCompleted );
	  request.sendAsPost( params );
	  request.send( requestString );

}

function filterByDataElementGroupCompleted( dataElementGroup )
{
  var dataElements = dataElementGroup.getElementsByTagName( 'dataElements' )[0];
  var dataElementList = dataElements.getElementsByTagName( 'dataElement' );

  var availableList = byId( 'availableDataElements' );
 
  for ( var i = 0; i < dataElementList.length; i++ )
  {
    var dataElement = dataElementList[i];
    var value = dataElement.firstChild.nodeValue;
    var id = dataElement.getAttribute( 'id' );
	var option = new Option( value, id );
	
	option.onmousemove  = function(e){
		showToolTip( e, this.text);
	}
	
	availableList.add( option , null );
  }
  
}

// -----------------------------------------------------------------------------
// Filter available data elements
// -----------------------------------------------------------------------------

function filterAvailableDataElements()
{
	var filter = document.getElementById( 'availableDataElementsFilter' ).value;
    var list = document.getElementById( 'availableDataElements' );
    
    list.options.length = 0;
    
    for ( var id in availableAllDataElements )
    {
        var value = availableAllDataElements[id];
        
		var option = new Option( value, id );
		
		option.onmousemove  = function(e){
			showToolTip(e, name);
		}
			
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add(option, null );
        }
    }
}

function initLists()
{
    var id;

    for ( id in availableAllDataElements )
    {
		var option = new Option( availableAllDataElements[id], id );
		
		option.onmousemove  = function(e){
			showToolTip(e, this.text);
		}
		$("#availableDataElements").append( option ) ;
    }
}

// -----------------------------------------------------------------------------
// Add and Remove dataSet members
// -----------------------------------------------------------------------------

function addDataSetMembers()
{
	var list = document.getElementById( 'availableDataElements' );
	
	var listOptionCombo = document.getElementById( 'availableOptionCombos' );

    if ( listOptionCombo.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value + "." + listOptionCombo[listOptionCombo.selectedIndex].value;

        dataSetMembers[id] = list.options[list.selectedIndex].text + " - " + listOptionCombo[listOptionCombo.selectedIndex].text;
    
		var option =  new Option( dataSetMembers[id], id );
	
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
		
		byId( 'selectedDataElements' ).add(option, null );
		
		listOptionCombo.remove( listOptionCombo.selectedIndex );
    }
}

function removeDataSetMembers()
{
	var listOptionCombo = document.getElementById( 'availableOptionCombos' );

	var list = document.getElementById( 'selectedDataElements' );

	var selectedIndex = list.selectedIndex;
	
    if ( selectedIndex != -1 )
    {
		var selectedValue = list.options[selectedIndex].value;

        var id = selectedValue.substring(selectedValue.indexOf(".") + 1, selectedValue.length);

		var name = dataSetMembers[selectedValue].substring(dataSetMembers[selectedValue].lastIndexOf(" - ") + 2, dataSetMembers[selectedValue].length);
		
		listOptionCombo.add( new Option( name, id ), null );
    
        list.remove( selectedIndex );
    }
}


function filterDataSetMembers()
{
	var filter = document.getElementById( 'dataSetMembersFilter' ).value;
    
	var list = document.getElementById( 'selectedDataElements' );
    
    list.options.length = 0;
    
    for ( var id in dataSetMembers )
    {
        var value = dataSetMembers[id];
        
        if (value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

// -----------------------------------------------------------------------------
// Get Period by PeriodType
// -----------------------------------------------------------------------------

function getPeriods(){

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getListPeriodCompleted );
	request.send( '../dhis-web-commons-ajax/getPeriods.action?name=' + $("#availabelPeriodTypes").val()); 	
}

function getListPeriodCompleted( xmlObject ){
	
	clearListById('availablePeriods');
	var nodes = xmlObject.getElementsByTagName('period');
	for ( var i = 0; i < nodes.length; i++ )
    {
        node = nodes.item(i);  
        var id = node.getElementsByTagName('id')[0].firstChild.nodeValue;
        var name = node.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('availablePeriods', name, id);
    }
}

// -----------------------------------------------------------------------------
// Generate individual report excel
// -----------------------------------------------------------------------------

function generateIndividualReportExcel(){
	// Check operands
	var selectedDataElements = byId('selectedDataElements').options;
	var message = '';
	if( selectedDataElements.length==0)
	{
		message = i18n_choose_dataelement + '<br/>';
	}
	// Check periods
	selectedDataElements = byId('selectedPeriods').options;
	if(selectedDataElements.length==0)
	{
		message += i18n_choose_period + '<br/>';
	}
	
	if(message.length > 0 ){
		setMessage(message);
		return;
	}
	$("#loading").showAtCenter( true );	
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateIndividualReportExcelReceived );
	var params = getQueryStringFromList('selectedDataElements', 'operands');
	params += getQueryStringFromList('selectedPeriods', 'periods');
	request.sendAsPost(params);
	request.send( "generateIndividualReportExcel.action");
	
}

function generateIndividualReportExcelReceived(xmlObject){
	
	var type = xmlObject.getAttribute( 'type' );
	
	if(type=='error')
	{
		setMessage( xmlObject.firstChild.nodeValue);
	}else if(type=='success')
	{
		window.location = "downloadFile.action?outputFormat=";
	}
	
	deleteDivEffect();
	$("#loading").hide();
}


function getPeriodsByPeriodTypeName() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'getPeriodsByPeriodTypeDB.action?periodTypeName=' + byId("availabelPeriodTypes").value);
}

function lastPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'previousPeriodsDB.action' ); 
}

function nextPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'nextPeriodsDB.action' ); 
}

function responseListPeriodReceived( xmlObject ) {

	clearListById('availablePeriods');
	var list = xmlObject.getElementsByTagName('period');
	for ( var i = 0; i < list.length; i++ )
    {
        item = list[i];  
        var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
        var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('availablePeriods', name, id);
    }
}