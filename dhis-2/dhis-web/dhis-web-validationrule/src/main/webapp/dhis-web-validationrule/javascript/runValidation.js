var startDate;
var endDate;
var validationRuleGroupId;
var aggregate;
var doDataMart;
var organisationUnitId;

function organisationUnitSelected( ids )
{
    organisationUnitId = ids[0];
}

function validateRunValidation()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( runValidationCompleted );

    request.send( 'validateRunValidation.action?startDate=' + getFieldValue( 'startDate' ) + '&endDate='
            + getFieldValue( 'endDate' ) + '&aggregate=' + getFieldValue( 'aggregate' ) );

    return false;
}

function runValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
        setWaitMessage( "Analysing data, please wait" );

        startDate = getFieldValue( 'startDate' );
        endDate = getFieldValue( 'endDate' );
        validationRuleGroupId = $( '#validationRuleGroupId' ).val();
        aggregate = $( '#aggregate' ).val();
        doDataMart = $( '#doDataMart' ).val();

        var url = 'runValidationAction.action?organisationUnitId=' + organisationUnitId + '&startDate=' + startDate
                + '&endDate=' + endDate + '&validationRuleGroupId=' + validationRuleGroupId + '&aggregate=' + aggregate
                + '&doDataMart=' + doDataMart;

        $.get( url, function( data )
        {
            $( "div#analysisInput" ).hide();
            $( "div#analysisResult" ).show();
            $( "div#analysisResult" ).html( data );
            pageInit();
        } );
    } else if ( type == 'error' )
    {
        window.alert( i18n_validation_failed + ':' + '\n' + message );
    } else if ( type == 'input' )
    {
        setMessage( message );
    }
}

function drillDownValidation( orgUnitId )
{
    setHeaderWaitMessage( i18n_analysing_please_wait );

    var url = 'runValidationAction.action?organisationUnitId=' + orgUnitId + '&startDate=' + startDate + '&endDate='
            + endDate + '&validationRuleGroupId=' + validationRuleGroupId + '&aggregate=' + aggregate + '&doDataMart='
            + doDataMart;

    $.get( url, function( data )
    {
        hideHeaderMessage();
        $( "div#analysisResult" ).html( data );
        pageInit();
    } );
}

function viewValidationResultDetails( validationRuleId, sourceId, periodId )
{
    var url = "viewValidationResultDetails.action?validationRuleId=" + validationRuleId + "&sourceId=" + sourceId
            + "&periodId=" + periodId;

    var dialog = window
            .open(
                    url,
                    "_blank",
                    "directories=no, \
    		 height=550, width=500, location=no, menubar=no, status=no, \
    		 toolbar=no, resizable=yes" );
}

function aggregateChanged()
{
    var aggregate = getListValue( 'aggregate' );

    if ( aggregate == 'true' )
    {
        $( 'span#info' ).html( i18n_aggregate_data_info );
        $( '#doDataMart' ).removeAttr( 'disabled' );
    } else
    {
        $( 'span#info' ).html( i18n_captured_data_info );
        $( '#doDataMart' ).attr( 'disabled', 'disabled' );
    }
}

function showAggregateResults()
{
    $( 'div#validationResults' ).hide();
    $( 'div#aggregateResults' ).show();
    var button = document.getElementById( "resultTypeButton" );
    button.onclick = function()
    {
        showValidationResults();
    };
    button.value = "See validation";
}

function showValidationResults()
{
    $( 'div#aggregateResults' ).hide();
    $( 'div#validationResults' ).show();

    var button = document.getElementById( "resultTypeButton" );
    button.onclick = function()
    {
        showAggregateResults();
    };
    button.value = "See statistics";
}

function exportValidationResult( type )
{
    window.location.href = 'exportValidationResult.action?type=' + type;
}
