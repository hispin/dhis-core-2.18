
function performMaintenance()
{
    var clearDataMart = document.getElementById( "clearDataMart" ).checked;
    var dataMartIndex = document.getElementById( "dataMartIndex" ).checked;
    var zeroValues = document.getElementById( "zeroValues" ).checked;
    var dataSetCompleteness = document.getElementById( "dataSetCompleteness" ).checked;
    var prunePeriods = document.getElementById( "prunePeriods" ).checked;
    
    if ( clearDataMart || dataMartIndex || zeroValues || dataSetCompleteness || prunePeriods )
    {
        setWaitMessage( i18n_performing_maintenance );
        
        var params = "clearDataMart=" + clearDataMart + 
            "&dataMartIndex=" + dataMartIndex +
            "&zeroValues=" + zeroValues +
            "&dataSetCompleteness=" + dataSetCompleteness +
            "&prunePeriods=" + prunePeriods;
            
        var url = "performMaintenance.action";
        
        var request = new Request();
        request.sendAsPost( params );
        request.setCallbackSuccess( performMaintenanceReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function performMaintenanceReceived( messageElement )
{
    setMessage( i18n_maintenance_performed );
}
