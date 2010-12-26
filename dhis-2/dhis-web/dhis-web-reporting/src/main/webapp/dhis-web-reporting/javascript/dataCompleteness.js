
function getPeriods( periodTypeList, availableList, selectedList, timespan )
{
	getAvailablePeriods( periodTypeList, availableList, selectedList, timespan );
}

function displayCompleteness()
{
    var criteria = $( "input[name='criteria']:checked" ).val();
    var dataSetList = byId( "dataSetId" ); //TODO simplify
    var dataSetId = dataSetList.options[ dataSetList.selectedIndex ].value;    
    var periodList = byId( "periodId" );
    var periodId = null;
    
    if ( !periodList.disabled && (periodList.options.length > 0) )
    {
        periodId = periodList.options[ periodList.selectedIndex ].value;
    }
    
    if ( periodId != null )
    {
        showLoader();
        
        var url = "getDataCompleteness.action" + "?periodId=" + periodId + "&criteria=" + criteria + 
        	"&dataSetId=" + dataSetId + "&type=html";
        
        $( "#contentDiv" ).load( url, function() {
        	hideLoader();
        	pageInit();
        } );
    }
}

function getCompleteness( type )
{
	window.location.href = "getDataCompleteness.action?type=" + type;
}