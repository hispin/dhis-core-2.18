
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