

function changeViewBy(){
	if(byId('viewby').value=='period'){	
		byId('period').multiple = true;
		byId('dataSet').multiple = false;
		byId('selectedPeriods').multiple = true;
		byId('selectedDataSets').multiple = false;
	}else{		
		byId('period').multiple = false;
		byId('dataSet').multiple = true;
		byId('selectedPeriods').multiple = false;
		byId('selectedDataSets').multiple = true;
	}
}

function getDataSetAndPeriod(){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getDataSetAndPeriodCompleted );
    request.send( 'getDataSetAndPeriodByPeriodType.action?periodTypeName=' + getFieldValue('periodType'));
}

function getDataSetAndPeriodCompleted( xmlObject ){
	var listPeriod = byId('period');
	listPeriod.options.length = 0;
	var periods = xmlObject.getElementsByTagName("period");
	for(var i=0;i<periods.length;i++){
		var id = periods.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = periods.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		listPeriod.add(new Option(name, id), null);
	}
	
	var listDataSet = byId('dataSet');
	listDataSet.options.length = 0;
	var dataSets = xmlObject.getElementsByTagName("dataSet");
	for(var i=0;i<dataSets.length;i++){
		var id = dataSets.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = dataSets.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		listDataSet.add(new Option(name, id), null);
	}
}

function viewData(dataSetId, periodId, organisationUnitId){
	var url = "viewCustomDataSetReport.action?dataSetId=" + dataSetId + "&periodId=" + periodId + "&organisationUnitId=" + organisationUnitId;
	window.open(  url , '_blank', 'width='+document.documentElement.clientWidth+',height='+document.documentElement.clientHeight+',scrollbars=yes' );
}

