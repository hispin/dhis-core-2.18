isAjax = true;
function organisationUnitSelected( orgUnits, orgUnitNames )
{
    setFieldValue( 'orgunitname', orgUnitNames[0] );
}

selection.setListenerFunction( organisationUnitSelected );

function generatedStatisticalProgramReport()
{
	hideById('backBtn');
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
		var subTitle = getFieldValue("programStageName") 
			+ " - " + getStatusString( status ) 
			+ " - " + i18n_total_result + ": " + total;
		setInnerHTML('gridSubtitleDetails', subTitle );
		showById( 'detailsDiv');
		showById('backBtn');
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

function backOnClick()
{
	hideById('backBtn');
	showById('reportTbl');
	hideById('detailsDiv');
}
