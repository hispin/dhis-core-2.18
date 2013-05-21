isAjax = true;

function orgunitSelected( orgUnits, orgUnitNames )
{
	hideById("listPatientDiv");
	setFieldValue('orgunitName', orgUnitNames[0]);
	setFieldValue('orgunitId', orgUnits[0]);
	clearListById('programId');
	jQuery.get("getPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programId').append( '<option value="">' + i18n_please_select + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programId').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			hideById('programLoader');
			enable('programId');
		});
}

selection.setListenerFunction( orgunitSelected );

function generateStageCompleteness()
{
	showLoader();
	jQuery('#completenessDiv').load('generateProgramStageCompleteness.action',
		{
			programId: getFieldValue('programId'),
			startDate: getFieldValue('startDate'),
			endDate: getFieldValue('endDate')
		}, 
		function()
		{
			showById('completenessDiv');
			setTableStyles();
			hideLoader();
		});
}

function exportStageCompleteness( type )
{
	var params = "type=" + type;
	params += "&programId=" + getFieldValue('programId');
	params += "&startDate=" + getFieldValue('startDate');
	params += "&endDate=" + getFieldValue('endDate');
	
	var url = "generateProgramStageCompleteness.action?" + params;
	window.location.href = url;
}
