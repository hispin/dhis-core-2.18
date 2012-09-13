
function orgunitSelected( orgUnits, orgUnitNames )
{
	hideById("listPatientDiv");
	clearListById('programIdAddPatient');
	$('#contentDataRecord').html('');
	setFieldValue('orgunitName', orgUnitNames[0]);
	setFieldValue('orgunitId', orgUnits[0]);
	jQuery.get("getPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programIdAddPatient').append( '<option value="">' + i18n_please_select + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			enableBtn();
		});
}

selection.setListenerFunction( orgunitSelected );

function showActitityList()
{
	setFieldValue('listAll', "true");
	hideById('listPatientDiv');
	contentDiv = 'listPatientDiv';
	$('#contentDataRecord').html('');
	var searchBySelectedOrgunit = false;
	var programId = getFieldValue('programIdAddPatient');
	var searchTexts = "stat_" + programId
					+ "_" + getFieldValue("statusEvent")
					+ "_" + getFieldValue('startDueDate')
					+ "_" + getFieldValue('endDueDate');
					
	if( getFieldValue("statusEvent") != '3' 
		&& getFieldValue("statusEvent") != '4' 
		&& getFieldValue("statusEvent") != '0' )
	{
		searchTexts	+= "_" + getFieldValue('orgunitId');
	}
	else
	{
		searchBySelectedOrgunit = true
	}
	showLoader();
	jQuery('#listPatientDiv').load('getActivityPlanRecords.action',
		{
			programId:programId,
			listAll:false,
			searchBySelectedOrgunit: searchBySelectedOrgunit,
			searchTexts: searchTexts
		}, 
		function()
		{
			showById('colorHelpLink');
			showById('listPatientDiv');
			hideLoader();
		});
}

function eventFlowToggle( programInstanceId )
{
	jQuery("#tb_" + programInstanceId + " .stage-object").each( function(){
			var programStageInstance = this.id.split('_')[1];
			jQuery('#arrow_' + programStageInstance ).toggle();
			jQuery('#td_' + programStageInstance ).toggle();
			jQuery(this).removeClass("stage-object-selected");
		});
	
	if( jQuery("#tb_" + programInstanceId + " .searched").length>0)
	{	
		var id = jQuery("#tb_" + programInstanceId + " .searched").attr('id').split('_')[1];
		showById("arrow_" + id);
		showById("td_" + id );
	}
	resize();
}

// --------------------------------------------------------------------
// Patient program tracking
// --------------------------------------------------------------------

function loadDataEntry( programStageInstanceId ) 
{
	jQuery("#patientList input[name='programStageBtn']").each(function(i,item){
		jQuery(item).removeClass('stage-object-selected');
	});
	jQuery( '#' + prefixId + programStageInstanceId ).addClass('stage-object-selected');
	setFieldValue('programStageInstanceId', programStageInstanceId);
	
	$('#contentDataRecord' ).load("viewProgramStageRecords.action",
		{
			programStageInstanceId: programStageInstanceId
		}).dialog(
		{
			title:i18n_program_stage,
			maximize:true, 
			closable:true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width:1000,
			height:500
		});
}

function statusEventOnChange()
{
	var statusEvent = getFieldValue("statusEvent");
	
	if( statusEvent == '0' ){
		enable('showEventSince');
		enable('showEventUpTo');
		setDateRange();
	}
	else if( statusEvent == '3' ){
		disable('showEventSince');
		enable('showEventUpTo');
		setDateRange();
	}
	else{
		enable('showEventSince');
		disable('showEventUpTo');
		setDateRange();
	}
}

function setDateRange()
{
	var statusEvent = getFieldValue("statusEvent");
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y= date.getFullYear();
		
	var startDateSince = "";
	var endDateSince = "";
	var startDateUpTo = "";
	var endDateUpTo = "";
	var startDate = "";
	var endDate = "";
		
	// Get dateRangeSince
	var days = getFieldValue('showEventSince');
	if( days == 'ALL'){
		startDateSince = jQuery.datepicker.formatDate( dateFormat, new Date(y-100, m, d) ) ;
	}
	else{
		startDateSince = jQuery.datepicker.formatDate( dateFormat, new Date(y, m, d + eval(days)) ) ;
	}
	endDateSince = jQuery.datepicker.formatDate( dateFormat, new Date() );
	
	// getDateRangeUpTo
	days = getFieldValue('showEventUpTo');
	startDateUpTo = jQuery.datepicker.formatDate( dateFormat, new Date() );
	endDateUpTo = "";
	if( days == 'ALL'){
		endDateUpTo = jQuery.datepicker.formatDate( dateFormat, new Date(y+100, m, d) ) ;
	}
	else{
		endDateUpTo = jQuery.datepicker.formatDate( dateFormat, new Date(y, m, d + eval(days)) ) ;
	}

	// check status to get date-range
	if( statusEvent == "0")
	{
		startDate = startDateSince;
		endDate = endDateUpTo;
	
	}else if (statusEvent=='3'){
		startDate = startDateUpTo;
		endDate = endDateUpTo;
	}
	else
	{
		startDate = startDateSince;
		endDate = endDateSince;
	}
	
	jQuery("#startDueDate").val(startDate);
	jQuery("#endDueDate").val(endDate);
}

function setDateRangeUpTo( days )
{
	if(days == "")
		return;
		
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y= date.getFullYear();
	
	var startDate = jQuery.datepicker.formatDate( dateFormat, new Date() );
	var endDate = "";
	if( days == 'ALL'){
		endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y+100, m, d) ) ;
	}
	else{
		d = d + eval(days);
		endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y, m, d) ) ;
	}
	
	jQuery("#startDueDate").val(startDate);
	jQuery("#endDueDate").val(endDate);
}


function setDateRangeAll()
{
	var date = new Date();
	var d = date.getDate();
	var m = date.getMonth();
	var y= date.getFullYear();
	
	
}