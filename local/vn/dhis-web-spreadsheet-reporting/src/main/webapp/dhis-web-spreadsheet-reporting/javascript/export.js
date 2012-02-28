// Current offset, next or previous corresponding to increasing or decreasing
// value with one
var currentPeriodOffset = 0;

// Period type object
var periodTypeFactory = new PeriodType();

// The current selected period type name
var currentPeriodTypeName = '';

// The current selected orgunit name
var currentOrgunitName = '';

// Functions
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	currentOrgunitName = orgUnitNames[0];
	getExportReportsByGroup( currentOrgunitName );	
}

selection.setListenerFunction( organisationUnitSelected );

function getExportReportsByGroup( selectedOrgUnitName ) {

	if ( selectedOrgUnitName )
	{
		setInnerHTML( "selectedOrganisationUnit", selectedOrgUnitName );
		
		jQuery.postJSON( 'getExportReportsByGroup.action',
		{
			group: getFieldValue( 'group' )
		},
		function ( json )
		{
			jQuery('#exportReport').empty();
			jQuery.each( json.exportReports, function(i, item){
				addOptionById( 'exportReport', item.id + '_' + item.flag, item.name );
			});

			currentPeriodOffset = 0;
			reportSelected();
			displayPeriodsInternal();
		});
	}
}

function reportSelected()
{
	var value = getFieldValue( 'exportReport' );
	
	if ( value && value != null )
	{
		currentPeriodTypeName = (value.split( '_' )[1] == "true") ? 'Daily' : 'Monthly';
	}
}

function displayPeriodsInternal()
{
	if ( currentPeriodTypeName )
	{
		var periods = periodTypeFactory.get( currentPeriodTypeName ).generatePeriods( currentPeriodOffset );
		periods = periodTypeFactory.filterFuturePeriods( periods );

		clearListById( 'selectedPeriodId' );

		for ( i in periods )
		{
			addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
		}
	}
}

function getNextPeriod()
{
    if ( currentPeriodOffset < 0 ) // Cannot display future periods
    {
        currentPeriodOffset++;
        displayPeriodsInternal();
    }
}

function getPreviousPeriod()
{
    currentPeriodOffset--;
    displayPeriodsInternal();
}

function validateGenerateReport( isAdvanced )
{
	var exportReport = getFieldValue('exportReport');

	if ( exportReport.length == 0 )
	{
		showErrorMessage( i18n_specify_export_report );
		return;
	}
	
	lockScreen();

	jQuery.postJSON( 'validateGenerateReport.action',
	{
		'exportReportId': getFieldValue( 'exportReport' ),
		'periodIndex': getFieldValue( 'selectedPeriodId' )
	},
	function( json )
	{
		if ( json.response == "success" ) {
			if ( isAdvanced ) {
				generateAdvancedExportReport();
			}
			else generateExportReport();
		}
		else {
			unLockScreen();
			showWarningMessage( json.message );
		}
	});
}

function generateExportReport() {
		
	jQuery.postJSON( 'generateExportReport.action', {}, function ( json ) {
		if ( json.response == "success" ) {
			window.location = "downloadFile.action";		
			unLockScreen();
		}
	});
}

function getALLExportReportByGroup() {

	jQuery.postJSON( "getALLExportReportByGroup.action", {
		group: byId("group").value
	}, function( json ) {
		clearListById( 'exportReport' );
		var list = json.exportReports;
		
		for ( var i = 0 ; i < list.length ; i++ )
		{
			addOption( 'exportReport', item[i].name, item[i].id );
		}
	} );
}

function generateAdvancedExportReport()
{
	jQuery.postJSON( 'generateAdvancedExportReport.action', {
		organisationGroupId: byId("availableOrgunitGroups").value
	}, function( json ) {
		if ( json.response == "success" ) {
			showSuccessMessage( json.message );
		}
	} );
}
