
// -----------------------------------------------------------------------------
// Report table
// -----------------------------------------------------------------------------

function getReportParams( id )
{
	window.location.href = "getReportParams.action?id=" + id;
}

var paramOrganisationUnit = null;

function paramOrganisationUnitSet( id )
{
	paramOrganisationUnit = id;
}

function validationError()
{
	if ( $( "#selectionTree" ).length && paramOrganisationUnit == null )
	{
		setMessage( i18n_please_select_unit );
		return true;
	}
	
	return false;
}	

function generateReportTable()
{
	if ( validationError() )
	{
		return false;
	}
	
	var url = "id=" + $( "#id" ).val() + "&mode=" + $( "#mode" ).val();
	    
    if ( $( "#reportingPeriod" ).length )
    {
        url += "&reportingPeriod=" + $( "#reportingPeriod" ).val();
    }
        
    if ( paramOrganisationUnit != null )
    {
        url += "&organisationUnitId=" + paramOrganisationUnit;
    }
    
	window.location.href = "exportTable.action?type=html&" + url;
}
