// Current offset, next or previous corresponding to increasing or decreasing
// value with one
var currentPeriodOffset = 0;

// Period type object
var periodTypeFactory = new PeriodType();

// The current selected period type name
var currentPeriodTypeName = '';

// Functions
function organisationUnitSelected( orgUnits )
{	
	getExportReportsByGroup();	
}

selection.setListenerFunction( organisationUnitSelected );

function getExportReportsByGroup() {
	
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
		
		var selectedOrganisationUnit = null;

		try {
			selectedOrganisationUnit = json.organisationUnit;
			
			enable("group");
			enable("exportReport");
			enable("selectedPeriodId");
			enable("generateExportReport");
			enable("previewButton");
			enable("nextPeriod");
			enable("lastPeriod");
		}catch(e){
			disable("group");
			disable("exportReport");
			disable("selectedPeriodId");
			disable("generateExportReport");
			disable("previewButton");
			disable("nextPeriod");
			disable("lastPeriod");		
		}

		setInnerHTML( "selectedOrganisationUnit", selectedOrganisationUnit );
	});
}

function reportSelected()
{
	currentPeriodTypeName = (getFieldValue( 'exportReport' ).split( '_' )[1] == "true") ? 'Daily' : 'Monthly';
}

function displayPeriodsInternal()
{
    var periods = periodTypeFactory.get( currentPeriodTypeName ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.filterFuturePeriods( periods );

    clearListById( 'selectedPeriodId' );

    for ( i in periods )
    {
        addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
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

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getALLExportReportByGroupReceived );
	request.sendAsPost( "group=" + byId("group").value );
	request.send( 'getALLExportReportByGroup.action');
	
}

function getALLExportReportByGroupReceived( xmlObject ) {

	clearListById('exportReport');
	var list = xmlObject.getElementsByTagName("exportReport");
	
	for(var i=0;i<list.length;i++){
		var item = list[i];
		var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('exportReport',name,id);
	}
}

function generateAdvancedExportReport()
{
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateExportReportReceived );
	request.send( 'generateAdvancedExportReport.action?organisationGroupId='+ byId("availableOrgunitGroups").value );
}
