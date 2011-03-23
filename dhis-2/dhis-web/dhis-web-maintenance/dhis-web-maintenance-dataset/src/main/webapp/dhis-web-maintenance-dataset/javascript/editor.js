
// Global variables:

arrayIds = new Array();


function displayAssociatedForm()
{
	var url = "showedAssociationsEditor.action";
	
	showLoader();
	
	$( "#contentDiv" ).load( url, function(){
		hideLoader();
	});
}

function changeAssociatedStatus( orgunitId, dataSetId, assigned )
{
	var url = "definedAssociationsEditor.action?";
	url += "orgUnitId=" + orgunitId ;
	url += "&dataSetId=" + dataSetId;
	url += "&assigned=" + !assigned;
	
	$( "#div" + orgunitId + dataSetId ).load( url );
}

function assignAll( orgunitId )
{
	for ( var i = 0 ; i < arrayIds.length ; i++ )
	{
		changeAssociatedStatus( orgunitId, arrayIds[i], eval($("#div" + orgunitId + arrayIds[i] + " input[type='hidden']").val()) );
	}
}