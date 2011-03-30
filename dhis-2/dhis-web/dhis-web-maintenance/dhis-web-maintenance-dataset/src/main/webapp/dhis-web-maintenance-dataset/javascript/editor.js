
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

function assignAll( element, orgunitId )
{
	var status = false;
	var checked = element.checked;

	lockScreen();
	
	for ( var i = 0 ; i < arrayIds.length ; i++ )
	{
		status = eval( $("#div" + orgunitId + arrayIds[i] + " input[type='hidden']").val() );
	
		if ( (checked && !status) || (!checked && status) )
		{
			changeAssociatedStatus( orgunitId, arrayIds[i], !checked );
		}
	}
	
	unLockScreen();
}