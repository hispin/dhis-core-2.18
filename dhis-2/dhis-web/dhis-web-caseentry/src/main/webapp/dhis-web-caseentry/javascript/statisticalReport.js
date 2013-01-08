isAjax = true;
function organisationUnitSelected( orgUnits, orgUnitNames )
{
    setFieldValue( 'orgunitname', orgUnitNames[0] );
}

selection.setListenerFunction( organisationUnitSelected );

function generatedStatisticalProgramReport()
{
	hideById('statisticalReportDiv');
	hideById('detailsDiv');
	showLoader();
	jQuery( "#statisticalReportDiv" ).load( "generateStatisticalProgramReport.action",
	{
		programId: getFieldValue('programId'),
		startDate: getFieldValue('startDate'),
		endDate: getFieldValue( 'endDate' ),
		facilityLB: $('input[name=facilityLB]:checked').val()
	}, function() 
	{ 
		setTableStyles();
		hideById('reportForm');
		showById('statisticalReportDiv');
		showById('reportTbl');
		hideLoader();
	});
}

function statisticalProgramDetailsReport( programStageId, status, total )
{
	showLoader();
	hideById( 'reportTbl' );
	hideById( 'detailsDiv' );
	contentDiv = 'detailsDiv';
	jQuery( "#detailsDiv" ).load( "statisticalProgramDetailsReport.action",
	{
		programStageId: programStageId,
		startDate: getFieldValue( 'startDate' ),
		endDate: getFieldValue( 'endDate' ),
		status:status,
		total: total,
		facilityLB: $('input[name=facilityLB]:checked').val()
	}, function() 
	{ 
		setFieldValue('status',status);
		setFieldValue('total',total);
		var programStageTitle = "&raquo; " + getStatusString( status ) 
			+ " - " + getFieldValue("programStageName");
		setInnerHTML('programStageTitleLbl', programStageTitle );
		setInnerHTML('totalLbl', i18n_total_result + ": " + total );
		showById('totalLbl');
		showById('programStageTitleLbl');
		showById( 'detailsDiv');
		hideLoader();
	});
}

function getStatusString( status )
{
	switch(status){
		case 1: return i18n_completed;
		case 2: return i18n_incomplete;
		case 3: return i18n_scheduled;
		case 4: return i18n_overdue;
		default: return "";
	}
}

function loadDataEntry( programStageInstanceId ) 
{
	hideById("detailsDiv");
	jQuery('#viewRecordsDiv' )
		.load( 'viewProgramStageRecords.action?programStageInstanceId=' + programStageInstanceId
		,function(){
			jQuery("#viewRecordsDiv :input" ).attr("disabled", true);
			jQuery("#viewRecordsDiv :input" ).datepicker("destroy");
			showById('patientNameLbl');
			jQuery(".ui-combobox" ).hide();
			showById("viewRecordsDiv");
			hideById('inputCriteriaDiv');
			hideById('totalLbl');
		});
}

function entryFormContainerOnReady(){}

function showCriteriaForm()
{
	showById('reportForm');
	hideById('statisticalReportDiv');
}

function showStatisticalReport()
{
	showById('reportTbl');
	hideById('detailsDiv');
	hideById('totalLbl');
	hideById('viewRecordsDiv');
	hideById('programStageTitleLbl');
	hideById('patientNameLbl');
	hideById('totalLbl');
}

function detailsReport()
{
	hideById('viewRecordsDiv');
	showById('detailsDiv');
	showById('totalLbl');
	showById('programStageTitleLbl');
	hideById('patientNameLbl');
}

function showStatisticalReportForm()
{
	if( getInnerHTML('statisticalReportDiv' )!="")
	{
		hideById('reportForm');
		showById('statisticalReportDiv');
	}
}