function openAddPriodColumn(){
	$("#action").click(validateAddPeriodColumn);
	$("#periodColumn").showAtCenter( true );	
}

function validateAddPeriodColumn(){
	var startdate = $("#startdate").val();
	var enddate = $("#enddate").val();
	var column = $("#column").val();
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
	
	
	$("#startdate").val(startdate);
	$("#enddate").val(enddate);
	$("#periodType").val(periodType);
	$("#column").val(column);
	$("#id").val(id);
	
	
	$("#action").click(validateUpdatePeriodColumn);
	$("#periodColumn").showAtCenter( true );	
		
}

function validateUpdatePeriodColumn(){
	var startdate = $("#startdate").val();
	var enddate = $("#enddate").val();
	var column = $("#column").val();
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