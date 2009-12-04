function openAddPriodColumn(){
	byId("action").onclick = function(e){validateAddPeriodColumn();};
	$("#periodColumn").showAtCenter( true );	
}

function validateAddPeriodColumn(){
	var startdate = byId("startdate").value;
	var enddate = byId("enddate").value;
	var column = byId("column").value;
	if(startdate==""){
		setMessage(i18n_startdate_null);
	}else if(enddate==""){
		setMessage(i18n_enddate_null);
	}else if(column==""){
		setMessage(i18n_column_is_null);
	}else{
		document.forms['periodColumn'].action = "savePeriodColumn.action";
		document.forms['periodColumn'].submit();
	}
}

function openUpdatePriodColumn( id ){

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( openUpdatePriodColumnCompleted );
	request.send( "getPeriodColumn.action?id=" + id );
	
}

function openUpdatePriodColumnCompleted( xmlObject ){	
	
	var id = xmlObject.getElementsByTagName("id")[0].firstChild.nodeValue;	
	var startdate = xmlObject.getElementsByTagName("startdate")[0].firstChild.nodeValue;
	var enddate = xmlObject.getElementsByTagName("enddate")[0].firstChild.nodeValue;
	var periodType = xmlObject.getElementsByTagName("periodType")[0].firstChild.nodeValue;
	var column = xmlObject.getElementsByTagName("column")[0].firstChild.nodeValue;
	
	byId("startdate").value = startdate;
	byId("enddate").value = enddate;
	byId("periodType").value = periodType;
	byId("column").value = column;
	byId("id").value = id;
	
	byId("action").onclick = function(e) {validateUpdatePeriodColumn() ; };
	$("#periodColumn").showAtCenter( true );	
		
}

function validateUpdatePeriodColumn(){
	var startdate = byId("startdate").value;
	var enddate = byId("enddate").value;
	var column = byId("column").value;
	if(startdate==""){
		setMessage(i18n_startdate_null);
	}else if(enddate==""){
		setMessage(i18n_enddate_null);
	}else if(column==""){
		setMessage(i18n_column_is_null);
	}else{
		document.forms['periodColumn'].action = "updatePeriodColumn.action";
		document.forms['periodColumn'].submit();
	}
}

function deletePeriodColumn( reportId, periodColumnId ){
	if(window.confirm(i18n_confirm_delete)){
		window.location="deletePeriodColumn.action?reportId=" + reportId + "&id=" + periodColumnId;
	}	
}