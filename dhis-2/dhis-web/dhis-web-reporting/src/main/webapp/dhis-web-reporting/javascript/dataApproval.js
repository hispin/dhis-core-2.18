
dhis2.util.namespace( 'dhis2.appr' );

dhis2.appr.currentPeriodOffset = 0;

dhis2.appr.dataSetSelected = function()
{
	dhis2.appr.displayPeriods();
}

dhis2.appr.displayPeriods = function()
{
	var pt = $( '#dataSetId :selected' ).data( "pt" );
	dhis2.dsr.displayPeriodsInternal( pt, dhis2.appr.currentPeriodOffset );
}

dhis2.appr.displayNextPeriods = function()
{	
    if ( dhis2.appr.currentPeriodOffset < 0 ) // Cannot display future periods
    {
        dhis2.appr.currentPeriodOffset++;
        dhis2.appr.displayPeriods();
    }
}

dhis2.appr.displayPreviousPeriods = function()
{
    dhis2.appr.currentPeriodOffset--;
    dhis2.appr.displayPeriods();
}
