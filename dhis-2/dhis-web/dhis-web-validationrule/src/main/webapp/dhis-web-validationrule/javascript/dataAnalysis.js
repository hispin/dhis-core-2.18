
var selectedOrganisationUnit = null;

function organisationUnitSelected( organisationUnits )
{
	selectedOrganisationUnit = organisationUnits[0];
}

function analyseData()
{
	if ( analysisFormIsValid() == true )
	{
		setWaitMessage( i18n_analysing_please_wait );
		
		var url = "getAnalysis.action" +
			"?key=" + $( "#key" ).val() +
			"&toDate=" + $( "#toDate" ).val() + 
			"&fromDate=" + $( "#fromDate" ).val() +
			"&organisationUnit=" + selectedOrganisationUnit +
			"&" + getParamString( "dataSets", "dataSets" );
			
		if ( byId( "standardDeviation" ) != null )
		{
			url += "&standardDeviation=" + $( "#standardDeviation" ).val();
		}
		
		$.get( url, function( data ) {
			$( "div#analysisInput" ).hide();
			$( "div#analysisResult" ).show();
			$( "div#analysisResult" ).html( data );
		} );
	}
}

function analysisFormIsValid()
{	
	var dataSets = document.getElementById( "dataSets" );
	
	if ( dataSets.options.length == 0 )
	{
		setMessage( "Please select at least one data set" );
		return false;
	}
	
	if ( selectedOrganisationUnit == null )
	{
		setMessage( "Please select an organisation unit" );
		return false;
	}
	
	return true;
}

function getFollowUpAnalysis()
{
	setWaitMessage( i18n_analysing_please_wait );
	
	var url = "getAnalysis.action?key=followup";
	
	$.get( url, function( data ) {
		hideMessage();
		$( "div#analysisResult" ).show();
		$( "div#analysisResult" ).html( data );
	} );
}