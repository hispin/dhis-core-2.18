
var selectedOrganisationUnit = null;

function organisationUnitSelected( units ) {
	if ( units && units[0] ) {	
		selectedOrganisationUnit = units[0];
	}
	else {
		selectedOrganisationUnit = null;
	}
}

function displayOrgUnitDistribution() {
	if ( inputInvalid() ) {
		return false;
	}
	
	$( "#chartDiv" ).hide();
	$( "#tableDiv" ).show();	
	var groupSetId = $( "#groupSetId" ).val();
	var url = "getOrgUnitDistribution.action?groupSetId=" + groupSetId + "&type=html";
	$( "#tableDiv" ).load( url, pageInit );
}

function getOrgUnitDistribution( type ) {	
	if ( inputInvalid() ) {
		return false;
	}
	
	var groupSetId = $( "#groupSetId" ).val();
	var url = "getOrgUnitDistribution.action?groupSetId=" + groupSetId + "&type=" + type;
	window.location.href = url;
}

function displayOrgUnitDistributionChart() {
	if ( inputInvalid() ) {
		return false;
	}
	
	$( "#tableDiv" ).hide();
	$( "#chartDiv" ).show();
	var groupSetId = $( "#groupSetId" ).val();
	var random = getRandomNumber();
	var source = "getOrgUnitDistributionChart.action?groupSetId=" + groupSetId + "&r=" + random;
	$( "#chartImg" ).attr( "src", source );
}

function inputInvalid() {
	var groupSetId = $( "#groupSetId" ).val();
	
	if ( groupSetId == null || groupSetId == 0 ) {
		setHeaderDelayMessage( i18n_select_group_set );
		return true;
	}
	
	if ( selectedOrganisationUnit == null || selectedOrganisationUnit == "" ) {
		setHeaderDelayMessage( i18n_select_org_unit );
		return true;
	}
	
	return false;
}
