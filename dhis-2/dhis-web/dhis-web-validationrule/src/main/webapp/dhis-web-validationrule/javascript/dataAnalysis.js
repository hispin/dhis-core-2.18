var selectedOrganisationUnit = null;

function organisationUnitSelected( organisationUnits )
{
    selectedOrganisationUnit = organisationUnits[0];
}

function validateRunAnalyseData()
{
    if ( analyseDataInvalid() )
    {
        $.post( "validateRunAnalysis.action", {
            fromDate : getFieldValue( 'fromDate' ),
            toDate : getFieldValue( 'toDate' )
        }, function( data )
        {
            runValidationCompleted( data );
        }, 'xml' );
    }
}

function analyseDataInvalid()
{
    if ( $( '#fromDate' ).val().length == 0 )
    {
        setMessage( i18n_specify_a_start_date );
        return false;
    }

    if ( $( '#toDate' ).val().length == 0 )
    {
        setMessage( i18n_specify_an_ending_date );
        return false;
    }

    var dataSets = document.getElementById( "dataSets" );

    if ( dataSets.options.length == 0 )
    {
        setMessage( i18n_specify_dataset );
        return false;
    }

    return true;
}

function runValidationCompleted( messageElement )
{
    var type = messageElement.firstChild.getAttribute( 'type' );
    var message = messageElement.firstChild.firstChild.nodeValue;

    if ( type == 'success' )
    {
        analyseData();
    } else if ( type == 'error' )
    {
        window.alert( i18n_validation_failed + ':' + '\n' + message );
    } else if ( type == 'input' )
    {
        setMessage( message );
    }
}

function analyseData()
{
    setWaitMessage( i18n_analysing_please_wait );

    var url = "getAnalysis.action" + "?key=" + $( "#key" ).val() + "&toDate=" + $( "#toDate" ).val() + "&fromDate="
            + $( "#fromDate" ).val() + "&" + getParamString( "dataSets", "dataSets" );

    if ( byId( "standardDeviation" ) != null )
    {
        url += "&standardDeviation=" + $( "#standardDeviation" ).val();
    }

    $.get( url, function( data )
    {
        $( "div#analysisInput" ).hide();
        $( "div#analysisResult" ).show();
        $( "div#analysisResult" ).html( data );
    } );
}

function getFollowUpAnalysis()
{
    setWaitMessage( i18n_analysing_please_wait );

    var url = "getAnalysis.action?key=followup";

    $.get( url, function( data )
    {
        hideMessage();
        $( "div#analysisResult" ).show();
        $( "div#analysisResult" ).html( data );
    } );
}