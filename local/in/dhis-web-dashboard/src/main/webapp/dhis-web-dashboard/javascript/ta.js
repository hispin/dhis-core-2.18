
function ouSelCBChange()
{
	var ouSelCBId = document.getElementById( "ouSelCB" );
	var ouListCDId = document.getElementById( "orgUnitListCB" );
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );
	
	if( ouSelCBId.checked )
	{
		ouListCDId.disabled = false;
		
		clearList( ouLevelId );
    	ouLevelId.disabled = true;
	}
	else
	{
		ouLevelId.disabled = false;
		
		clearList( ouListCDId );
		ouListCDId.disabled = true;
	}
	
	getOUDeatilsForTA( selOrgUnitId );
	
}

function aggPeriodCBChange()
{
	var aggPeriodCBId = document.getElementById( "aggPeriodCB" );
	
	if( aggPeriodCBId.checked )
	{
		var periodTypeList = document.getElementById( "periodTypeLB" );
		
		for( var i =0; i < periodTypeList.options.length; i++ )
		{
			if( periodTypeList.options[i].text == monthlyPeriodTypeName )
			{
				periodTypeList.options[i].selected = true;
				break;
			}
		}
		
		getPeriods();
		
		periodTypeList.disabled = true;
	}
	else
	{
		var periodTypeList = document.getElementById( "periodTypeLB" );
		
		for( var i =0; i < periodTypeList.options.length; i++ )
		{
			if( periodTypeList.options[i].text == monthlyPeriodTypeName )
			{
				periodTypeList.options[i].selected = true;
				break;
			}
		}
		
		getPeriods();
		
		periodTypeList.disabled = false;
	}
}

function moveup( movelistId )
{
	var moveList = document.getElementById( movelistId );
	
	var selIndex = moveList.selectedIndex;
	
	if( selIndex <= 0 ) return;
		
	var tempOptionText = moveList.options[ selIndex ].text;
	var tempOptionValue = moveList.options[ selIndex ].value;
	
	moveList.options[ selIndex ].text = moveList.options[ selIndex -1 ].text;
	moveList.options[ selIndex ].value = moveList.options[ selIndex -1 ].value;
	
	moveList.options[ selIndex - 1 ].text = tempOptionText;
	moveList.options[ selIndex - 1 ].value = tempOptionValue;
	
	
	moveList.options[ selIndex ].selected = false;
	moveList.options[ selIndex - 1 ].selected = true;
		
}

function movedown( movelistId )
{
	var moveList = document.getElementById( movelistId );
	
	var selIndex = moveList.selectedIndex;
	
	if( selIndex >= moveList.options.length-1 ) return;
		
	var tempOptionText = moveList.options[ selIndex ].text;
	var tempOptionValue = moveList.options[ selIndex ].value;
	
	moveList.options[ selIndex ].text = moveList.options[ selIndex + 1 ].text;
	moveList.options[ selIndex ].value = moveList.options[ selIndex + 1 ].value;
	
	moveList.options[ selIndex + 1 ].text = tempOptionText;
	moveList.options[ selIndex + 1 ].value = tempOptionValue;

	
	moveList.options[ selIndex ].selected = false;
	moveList.options[ selIndex + 1 ].selected = true;		
}

function moveSelectedServices( fromListId, targetListId1, targetListId2 )
{
	var fromList = document.getElementById( fromListId );
    var targetList1 = document.getElementById( targetListId1 );
    var targetList2 = document.getElementById( targetListId2 );
    
    if ( fromList.selectedIndex == -1 )
    {
        return;
    }

    while ( fromList.selectedIndex > -1 )
    {
        option = fromList.options[ fromList.selectedIndex ];
        var optValue = option.value;
        var partsOfOptVal = new Array();
        partsOfOptVal = optValue.split(":");
        if(partsOfOptVal[0] == "D")
        {
        	fromList.remove( fromList.selectedIndex );
	        targetList1.add(option, null);
    	    option.selected = true;        	
        } 
        else
        {
        	fromList.remove( fromList.selectedIndex );
	        targetList2.add(option, null);
    	    option.selected = true;        	
        }        
    }
}


// DataElement and Its options Change Function
function deSelectionChangeFuntion( listId1, listId2 )
{
    var list1 = document.getElementById( listId1 );
    var list2 = document.getElementById( listId2 );

	clearList( list1 );
	
	for(var i=list2.options.length-1; i >= 0; i--)
    {
        option = list2.options[ i ];
        var optValue = option.value;
        var partsOfOptVal = new Array();
        partsOfOptVal = optValue.split(":");
        if(partsOfOptVal[0] == "D")
        {
        	list2.remove( i );	          	
        }
    }
	
	getDataElements();
}

function getDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    
    if ( dataElementGroupId != null )
    {
        var url = "getDataElements.action?id=" + dataElementGroupId + "&deOptionValue=" + deOptionValue;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getDataElementsReceived);
        request.send(url);
    }
}// getDataElements end           

function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedServices");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = "D:"+dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedDataElements, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);
        }
    }
    
    // If the list of available dataelements is empty, an empty placeholder will be added
    //addOptionPlaceHolder( availableDataElements );
}// getDataElementsReceived end

function getIndicators()
{
	var indicatorGroupList = document.getElementById( "indicatorGroupId" );
	var indicatorGroupId = indicatorGroupList.options[ indicatorGroupList.selectedIndex ].value;
	
	if ( indicatorGroupId != null )
	{
		var url = "getIndicators.action?id=" + indicatorGroupId;
		
		var request = new Request();
	    request.setResponseTypeXML( 'indicator' );
	    request.setCallbackSuccess( getIndicatorsReceived );
	    request.send( url );	    
	}
}

function getIndicatorsReceived( xmlObject )
{	
	var availableIndicators = document.getElementById( "availableIndicators" );
	var selectedIndicators = document.getElementById( "selectedServices" );
	
	clearList( availableIndicators );
	
	var indicators = xmlObject.getElementsByTagName( "indicator" );
	
	for ( var i = 0; i < indicators.length; i++ )
	{
		var id = "I:"+indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		if ( listContains( selectedIndicators, id ) == false )
		{				
			var option = document.createElement( "option" );
			option.value = id;
			option.text = indicatorName;
			availableIndicators.add( option, null );
		}
	}
	
	// If the list of available indicators is empty, an empty placeholder will be added
	//addOptionPlaceHolder( availableIndicators );
}


function getPeriods()
{
	var periodTypeList = document.getElementById( "periodTypeLB" );
	var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
	var startDateList = document.getElementById( "sDateLB" );
	var endDateList = document.getElementById( "eDateLB" );
	
	if ( periodTypeId != "NA" )
	{		
		var url = "getPeriods.action?id=" + periodTypeId;
		
		var request = new Request();
	    request.setResponseTypeXML( 'period' );
	    request.setCallbackSuccess( getPeriodsReceived );
	    request.send( url );
	}
	else
	{
	    clearList( startDateList );
	    clearList( endDateList );
	}
}

function getPeriodsReceived( xmlObject )
{	
	var startDateList = document.getElementById( "sDateLB" );
	var endDateList = document.getElementById( "eDateLB" );
	
	clearList( startDateList );
	clearList( endDateList );
	
	var periods = xmlObject.getElementsByTagName( "period" );
	
	for ( var i = 0; i < periods.length; i++)
	{
		var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		//var startDate = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
		//var endDate = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;		
		var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
		
		var option1 = document.createElement( "option" );
		option1.value = id;
		option1.text = periodName;

		var option2 = document.createElement( "option" );
		option2.value = id;
		option2.text = periodName;
		
		startDateList.add( option1, null );
		
		endDateList.add( option2, null );						
	}	
}

function getOUDeatilsForTA( orgUnitIds )
{
	var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOUDetailsForTARecevied );
	request.send( url );	    
}

function getOUDetailsForTARecevied(xmlObject)
{
	var ouSelCBId = document.getElementById( "ouSelCB" );	
	var ouListCDId = document.getElementById( "orgUnitListCB" );
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );
		
	var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		var ouLevel = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;

    	if( ouSelCBId.checked )
    	{
    		ouListCDId.disabled = false;
    		
    		clearList( ouLevelId );
    		ouLevelId.disabled = true;
    		
        	for(var i=0; i < ouListCDId.options.length; i++)
	    	{
		        if( id == ouListCDId.options[i].value ) return;
        	}
        	
        	ouListCDId.options[ouListCDId.options.length] = new Option(orgUnitName, id, false, false);
    	}
    	else
    	{   
    		clearList( ouListCDId );
    		
    		ouListCDId.options[ouListCDId.options.length] = new Option(orgUnitName,id,false,false);
    		
    		getorgUnitLevels( ouLevel );
    	}    
    }    		
}

function getorgUnitLevels( ouLevel )
{
	var ouLevelId = document.getElementById( "orgUnitLevelCB" );
	var j = 0;
	
	clearList( ouLevelId );
	
	var i = parseInt( ouLevel );
	
	
	for( i= i+1; i <= maxOrgUnitLevels; i++ )
	{		
		ouLevelId.options[j] = new Option("Level - "+i,i,false,false);
		
		j++;
	}	
}

// Removes slected orgunits from the Organisation List
function remOUFunction()
{
	var ouListCDId = document.getElementById( "orgUnitListCB" );

    for( var i = ouListCDId.options.length-1; i >= 0; i-- )
    {
    	if( ouListCDId.options[i].selected )
    	{
    		ouListCDId.options[i] = null;
    	}
    }    
}// remOUFunction end

