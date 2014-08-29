
function performMaintenance()
{
    var clearAnalytics = document.getElementById( "clearAnalytics" ).checked;
    var clearDataMart = document.getElementById( "clearDataMart" ).checked;
    var dataMartIndex = document.getElementById( "dataMartIndex" ).checked;
    var zeroValues = document.getElementById( "zeroValues" ).checked;
    var dataSetCompleteness = document.getElementById( "dataSetCompleteness" ).checked;
    var prunePeriods = document.getElementById( "prunePeriods" ).checked;
    var updateCategoryOptionCombos = document.getElementById( "updateCategoryOptionCombos" ).checked;
    
    if ( clearAnalytics || clearDataMart || dataMartIndex || zeroValues || dataSetCompleteness || prunePeriods || updateCategoryOptionCombos )
    {
        setHeaderWaitMessage( i18n_performing_maintenance );
        
        var params = "clearAnalytics=" + clearAnalytics + 
        	"&clearDataMart=" + clearDataMart + 
            "&dataMartIndex=" + dataMartIndex +
            "&zeroValues=" + zeroValues +
            "&dataSetCompleteness=" + dataSetCompleteness +
            "&prunePeriods=" + prunePeriods +
            "&updateCategoryOptionCombos=" + updateCategoryOptionCombos;
        
		$.ajax({
			   type: "POST",
			   url: "performMaintenance.action",
			   data: params,
			   dataType: "xml",
			   success: function(result){
				   setHeaderDelayMessage( i18n_maintenance_performed );
			   }
			});
    }
    else
    {
    	setHeaderDelayMessage( i18n_select_options );
    }
}