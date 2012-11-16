function checkPermission( reportId, url )
{
	jQuery.get( 'checkPermission.action', { id: reportId }, function( json )
	{
		if ( json.response == "error" || json.message != "granted" ) {
			showErrorMessage( json.message, 10000 );
			return;
		}
		
		window.location = 'openDefineAssociationsForm.action?exportReportId=' + reportId;
	} );
}

function openDefineAssociationsForm( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "openDefineAssociationsForm" ) )
	{
		checkPermission( reportId, 'openDefineAssociationsForm.action?exportReportId=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function openExportReportUserRole( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "openExportReportUserRole" ) )
	{
		checkPermission( reportId, 'openExportReportUserRole.action?id=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function updateExportReportForm( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "updateExportReportForm" ) )
	{
		checkPermission( reportId, 'updateExportReportForm.action?id=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function removeReport( reportId, reportName )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "deleteExportReport" ) )
	{
		jQuery.get( 'checkPermission.action', { id: reportId }, function( json )
		{
			if ( json.response == "error" || json.message != "granted" ) {
				showErrorMessage( json.message, 10000 );
				return;
			}
			
			removeItem( reportId, reportName, '$i18n.getString( 'confirm_delete' )', 'deleteExportReport.action' );
		} );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function listExportItemAction( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "listExportItemAction" ) )
	{
		checkPermission( reportId, 'listExportItemAction.action?exportReportId=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function openUpdateOrgUnitGroupListingReport( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "openUpdateOrgUnitGroupListingReport" ) )
	{
		jQuery.get( 'checkPermission.action', { id: reportId }, function( json )
		{
			if ( json.response == "error" || json.message != "granted" ) {
				showErrorMessage( json.message, 10000 );
				return;
			}
			
			window.location = 'openUpdateOrgUnitGroupListingReport.action?id=' + reportId;
		} );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function organisationUnitAtLevels( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "organisationUnitAtLevels" ) )
	{
		checkPermission( reportId, 'organisationUnitAtLevels.action?id=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function listAttributeValueGroupOrderForExportReport( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "listAttributeValueGroupOrderForExportReport" ) )
	{
		checkPermission( reportId, 'listAttributeValueGroupOrderForExportReport.action?id=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function listDataElementGroupOrderForExportReport( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "listDataElementGroupOrderForExportReport" ) )
	{
		checkPermission( reportId, 'listDataElementGroupOrderForExportReport.action?id=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function listCategoryOptionGroupOrderForExportReport( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "listCategoryOptionGroupOrderForExportReport" ) )
	{
		checkPermission( reportId, 'listCategoryOptionGroupOrderForExportReport.action?id=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}

function periodColumns( reportId )
{
	if ( $auth.hasAccess( "dhis-web-spreadsheet-reporting", "periodColumns" ) )
	{
		checkPermission( reportId, 'periodColumns.action?id=' );
	} else {
		showErrorMessage( '$i18n.getString( 'access_denied' )' );
	}
}