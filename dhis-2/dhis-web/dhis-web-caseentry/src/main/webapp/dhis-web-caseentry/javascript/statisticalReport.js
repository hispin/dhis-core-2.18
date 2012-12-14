function organisationUnitSelected( orgUnits, orgUnitNames )
{
    setFieldValue( 'orgunitname', orgUnitNames[0] );
}

selection.setListenerFunction( organisationUnitSelected );

function generatedStatisticalProgramReport()
{
	showLoader();
	
	jQuery( "#contentDiv" ).load( "generateStatisticalProgramReport.action",
	{
		programId: getFieldValue( 'programId' ),
		startDate: getFieldValue( 'startDate' ),
		endDate: getFieldValue( 'endDate' ),
		facilityLB: $('input[name=facilityLB]:checked').val()
	}, function() 
	{ 
		setTableStyles();
		hideLoader();
		showById( 'contentDiv' );
	});
}