
function changeViewBy() {

	if ( byId('viewby').value == 'period' ) {
	
		byId('period').multiple = true;
		byId('dataSet').multiple = false;
		byId('selectedPeriods').multiple = true;
		byId('selectedDataSets').multiple = false;
		
	}
	else {
	
		byId('period').multiple = false;
		byId('dataSet').multiple = true;
		byId('selectedPeriods').multiple = false;
		byId('selectedDataSets').multiple = true;
	}
}

function viewData (dataSetId, periodId, organisationUnitId) {

	var url = "viewCustomDataSetReport.action?dataSetId=" + dataSetId + "&periodId=" + periodId + "&organisationUnitId=" + organisationUnitId;
	
	window.open(  url , '_blank', 'width='+document.documentElement.clientWidth+',height='+document.documentElement.clientHeight+',scrollbars=yes' );
}

// ---------------------------------------------------------------------------
// View dataset completed reportS
// ---------------------------------------------------------------------------

function viewDataSetCompleted(){	
	
	var params = "viewBy=" + byId('viewby').value;
		
	var selectedDataSets = document.getElementById( 'selectedDataSets' );
	for ( var i = 0; i < selectedDataSets.options.length; ++i)
	{
		params += '&selectedDataSets=' + selectedDataSets.options[i].value;
	} 
		
	var selectedPeriods = document.getElementById( 'selectedPeriods' );
	for ( var i = 0; i < selectedPeriods.options.length; ++i)
	{
		params += '&selectedPeriods=' + selectedPeriods.options[i].value;
	} 	
	
	var url = "viewDataSetCompleted.action?" + params;
	window.open(  url , '_blank', 'width=' + document.documentElement.clientWidth+',height='+document.documentElement.clientHeight+',scrollbars=yes' );
		
}
