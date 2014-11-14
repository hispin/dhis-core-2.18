
// Load DataElement and Period List
function loadDataElementAndPeriod()
{

	var dataSetId = $( '#dataSetId' ).val();
	
	if ( dataSetId == "-1" )
	{
		showWarningMessage( "Please Select DataSet" );
		
		//document.getElementById( "dataSetId" ).disabled = true;
		
		//document.getElementById( "selectedPeriodId" ).disabled = true;
		//document.getElementById( "prevButton" ).disabled = true;
		//document.getElementById( "nextButton" ).disabled = true;
		
		return false;
	}
	
	else
	{
		//enable('dataSetId');
		
		$.post("getDataElementsAndPeriodList.action",
				{
					dataSetId:dataSetId
				},
				function(data)
				{
					
					populateDataElementAndPeriodList( data );
					//loadDataSets();				
				},'xml');
	}
	
}


function populateDataElementAndPeriodList( data )
{
	var dataElementId = document.getElementById("dataElementId");
	clearList( dataElementId );
	
	//var periodId = document.getElementById("periodId");
	//clearList( periodId );
	
	var dataElementList = data.getElementsByTagName("dataelement");
	
	dataElementId.options[0] = new Option( "Select", "-1" , false, false);
	
	for ( var i = 0; i < dataElementList.length; i++ )
	{
		var id = dataElementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = dataElementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		dataElementId.add(option, null);
	} 
	
	/*var periodList = data.getElementsByTagName("period");
	
	periodId.options[0] = new Option( "Select", "-1" , false, false);
	
	for ( var i = 0; i <  periodList.length; i++ )
	{
		var id = periodList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = periodList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		periodId.add(option, null);
	} */
	
}

