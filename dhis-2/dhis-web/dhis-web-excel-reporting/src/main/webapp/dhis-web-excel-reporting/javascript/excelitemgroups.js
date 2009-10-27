// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================
/*
*	Open Add Excel item group form
*/

function openAddExcelItemGroup(){
	$("#name").attr("disabled", false);
	$("#type").attr("disabled", false);
	$("#divExcelitemGroup").showAtCenter( true );
}

/*
*	Open Update Excel item group form
*/
function openUpdateExcelItemGroup( id ){
	$.post("getExcelItemGroup.action",{id:id},
	function ( xmlObject ){
		
		$("#id").val(id);
		$("#name").val( xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue );
		$("#excelTemplateFile").val( xmlObject.getElementsByTagName('excelTemplateFile')[0].firstChild.nodeValue);
		$("#type").val( xmlObject.getElementsByTagName('type')[0].firstChild.nodeValue  );
	
		
		$("#divExcelitemGroup").showAtCenter( true );
		$("#name").attr("disabled", true);
		$("#type").attr("disabled", true);
		
	},'xml');	
}

/*
*	Validate Update Excel item group
*/
function validateExcelItemGroup(){
	$.post("validateExcelItemGroup.action",{		
		name:$("#name").val(),
		excelTemplateFile:$("#excelTemplateFile").val(),
		type:$("#type").val()
	},function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}else if(type=='success')
		{		
			if(mode == 'add'){
				addExcelItemGroup();
			}else{
				updateExcelItemGroup();
			}
			
		}
	},'xml');	
}

function addExcelItemGroup(){
	$.post("addExcelItemGroup.action",{
		name:$("#name").val(),
		excelTemplateFile:$("#excelTemplateFile").val(),
		type:$("#type").val()
	},function(data){
		window.location.reload();
	},'xml');	
}


function updateExcelItemGroup(){
	$.post("updateExcelItemGroup.action",{
		id:$("#id").val(),
		name:$("#name").val(),
		excelTemplateFile:$("#excelTemplateFile").val(),
		type:$("#type").val()
	},function(data){
		window.location.reload();
	},'xml');	
}

function deleteExcelItemGroup(id){
	if(window.confirm(i18n_confirm_delete)){
		$.post("deleteExcelItemGroup.action",{
				id:id
			},function(data){
				window.location.reload();
			},'xml');
	}
}