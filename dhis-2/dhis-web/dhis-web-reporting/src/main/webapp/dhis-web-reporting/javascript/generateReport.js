var MODE_REPORT = "report";
var MODE_TABLE = "table";

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validationError()
{
    if ( $( "#selectionTree" ).length && !selectionTreeSelection.isSelected() )
    {
        setMessage( i18n_please_select_unit );
        return true;
    }

    return false;
}

// -----------------------------------------------------------------------------
// Report
// -----------------------------------------------------------------------------

function viewReport( type )
{
	var reportType = type != null && type != "" ? type : "pdf";

    if ( validationError() )
    {
        return false;
    }
    
    var mode = $( "#mode" ).val();
    var uid = $( "#uid" ).val();

    setMessage( i18n_process_completed );

    if ( mode == MODE_REPORT )
    {
    	window.location.href = "../api/reports/" + uid + "/data." + type + "?" + getUrlParams();
    } 
    else // MODE_TABLE
    {
        window.location.href = "exportTable.action?uid=" + uid + "&type=html&" + getUrlParams();
    }
}

function getUrlParams()
{
    var url = "";

    if ( $( "#reportingPeriod" ).length )
    {
        url += "pe=" + $( "#reportingPeriod" ).val() + "&";
    }

    if ( selectionTreeSelection.isSelected() )
    {
        url += "ou=" + selectionTreeSelection.getSelectedUid();
    }

    return url;
}

// -----------------------------------------------------------------------------
// Report table
// -----------------------------------------------------------------------------

function exportReport( type )
{
    var url = "exportTable.action?type=" + type + "&useLast=true";

    url += $( "#id" ).length ? ( "&id=" + $( "#id" ).val() ) : "";

    window.location.href = url;
}
