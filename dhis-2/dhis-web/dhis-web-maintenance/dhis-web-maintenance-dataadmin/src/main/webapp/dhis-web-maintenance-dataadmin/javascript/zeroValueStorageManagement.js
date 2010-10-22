function init()
{
    var id;
    for ( id in ignoreZeroValueDataElements )
    {
        $("#ignoreZeroValueDataElements").append( $( "<option></option>" ).attr( "value",id ).text( ignoreZeroValueDataElements[id] )) ;
    }

    for ( id in zeroDataValueElements )
    {
        $("#zeroDataValueElements").append( $( "<option></option>" ).attr( "value",id ).text( zeroDataValueElements[id] )) ;
    }
}

function getDataElementsByGroupIgnoreZeroValue ( dataElementGroupId )
{
	var request = new Request();
    request.setResponseTypeXML( 'dataElements' );
    request.setCallbackSuccess( getDataElementsByGroupIgnoreZeroValueCompleted );
    request.send( 'getDataElementsByZeroIsSignificantAndGroup.action?dataElementGroupId=' + dataElementGroupId +
        '&saveZeroValue=false' );
}

function getDataElementsByGroupIgnoreZeroValueCompleted( dataElements )
{	
	ignoreZeroValueDataElements = new Object();
	var dataElementTags = 	dataElements.getElementsByTagName( 'dataElement' );
	for(var i=0;i<dataElementTags.length;i++){		
		var listDataElement = dataElementTags.item(i);
		var id = listDataElement.getElementsByTagName( 'id' )[0].firstChild.nodeValue;
		var name = listDataElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;
		ignoreZeroValueDataElements[id] = name;
	}
	
	filterIgnoreZeroValueDataElements();
}

function getDataElementsByGroupSaveZeroValue ( dataElementGroupId )
{
	var request = new Request();
    request.setResponseTypeXML( 'dataElements' );
    request.setCallbackSuccess( getDataElementsByGroupSaveZeroValueCompleted );
    request.send( 'getDataElementsByZeroIsSignificantAndGroup.action?dataElementGroupId=' + dataElementGroupId +
        '&saveZeroValue=true' );
}

function getDataElementsByGroupSaveZeroValueCompleted( dataElements )
{
	zeroDataValueElements = new Object();
	var dataElementTags = 	dataElements.getElementsByTagName( 'dataElement' );
	for(var i=0;i<dataElementTags.length;i++){		
		var listDataElement = dataElementTags.item(i);
		var id = listDataElement.getElementsByTagName( 'id' )[0].firstChild.nodeValue;
		var name = listDataElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;
		zeroDataValueElements[id] = name;
	}
	
	filterSaveZeroValueDataElements();
}

function moveLeftToRight()
{
	var list = byId('ignoreZeroValueDataElements');

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        zeroDataValueElements[id] = ignoreZeroValueDataElements[id];
        
        delete ignoreZeroValueDataElements[id];        
    }
	filterIgnoreZeroValueDataElements();
	filterSaveZeroValueDataElements();
}

function moveAllLeftToRight()
{
	selectAllById('ignoreZeroValueDataElements');
	moveLeftToRight();	
}

function moveRightToLeft()
{
	var list = byId('zeroDataValueElements');

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        ignoreZeroValueDataElements[id] = zeroDataValueElements[id];
        
        delete zeroDataValueElements[id];        
    }
	filterIgnoreZeroValueDataElements();
	filterSaveZeroValueDataElements();
}

function moveAllRightToLeft()
{
	selectAllById('zeroDataValueElements');
	moveRightToLeft();	
}

function filterIgnoreZeroValueDataElements()
{
	
	var filter = byId( 'filterIgnoreZeroValue' ).value;
	var list = byId('ignoreZeroValueDataElements');
	list.options.length = 0;
	for ( var id in ignoreZeroValueDataElements )
    {
        var value = ignoreZeroValueDataElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterSaveZeroValueDataElements()
{
	
	var filter = byId( 'filterSaveZeroValue' ).value;
	var list = byId('zeroDataValueElements');
	list.options.length = 0;
	for ( var id in zeroDataValueElements )
    {
        var value = zeroDataValueElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function submitForm()
{
	selectAllById('zeroDataValueElements');
	byId('ZeroDataValueManagement').submit();
}
