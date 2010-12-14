
function displayOrgUnitDistributionChart() {
	$( "#chartDiv" ).show();
	$( "#tableDiv" ).hide();
	var groupSetId = $( "#groupSetId" ).val();
	var random = getRandomNumber();
	var source = "getOrgUnitDistributionChart.action?groupSetId=" + groupSetId + "&r=" + random;
	$( "#chartImg" ).attr( "src", source );
}
