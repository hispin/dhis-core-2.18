
function dataElementCriteriaChanged()
{
    clearFilter( "dataElementFilter", "availableDataElements" );
    
    var dataElementGroupId = getListValue( "dataElementGroupList" );
    var dataDictionaryId = getListValue( "dataDictionaryDataElementList" );
    
    var url = "getDataElementList.action";
    
    $.getJSON(
        url,
        {
            "dataDictionaryId": dataDictionaryId,
            "dataElementGroupId": dataElementGroupId
        },
        function( json )
        {
            var list = document.getElementById( "availableDataElements" );
            
            clearList( list );
            
            var objects = json.dataElements;
            
            for ( var i=0; i<objects.length; i++ )
            {
                addOptionToList( list, objects[i].id, objects[i].name );
            }
        }
    );
}

function indicatorCriteriaChanged()
{
    clearFilter( "indicatorFilter", "availableIndicators" );
    
    var indicatorGroupId = getListValue( "indicatorGroupList" );
    var dataDictionaryId = getListValue( "dataDictionaryIndicatorList" );
    
    var url = "getIndicatorList.action";
    
    $.getJSON(
        url,
        {
            "dataDictionaryId": dataDictionaryId,
            "indicatorGroupId": indicatorGroupId
        },
        function( json )
        {
            var list = document.getElementById( "availableIndicators" );
            
            clearList( list );
            
            var objects = json.indicators;
            
            for ( var i=0; i<objects.length; i++ )
            {
                addOptionToList( list, objects[i].id, objects[i].name );
            }
        }
    );
}

function exportDetailedMetaData()
{
    selectAllById( "selectedDataElements" );
    selectAllById( "selectedIndicators" );
    
    document.getElementById( "exportForm" ).submit();
}
