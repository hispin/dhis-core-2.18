
function generateResourceTable()
{
    var organisationUnit = document.getElementById( "organisationUnit" ).checked;
    var groupSet = document.getElementById( "groupSet" ).checked;
    var dataElementGroupSetStructure = document.getElementById( "dataElementGroupSetStructure" ).checked;
    var indicatorGroupSetStructure = document.getElementById( "indicatorGroupSetStructure" ).checked;
    var organisationUnitGroupSetStructure = document.getElementById( "organisationUnitGroupSetStructure" ).checked;
    var categoryOptionComboName = document.getElementById( "categoryOptionComboName" ).checked;
    
    if ( organisationUnit || groupSet || dataElementGroupSetStructure || 
        indicatorGroupSetStructure || organisationUnitGroupSetStructure || categoryOptionComboName )
    {
        setMessage( i18n_generating_resource_tables );
            
        var params = "organisationUnit=" + organisationUnit + 
            "&groupSet=" + groupSet + 
            "&dataElementGroupSetStructure=" + dataElementGroupSetStructure +
            "&indicatorGroupSetStructure=" + indicatorGroupSetStructure +
            "&organisationUnitGroupSetStructure=" + organisationUnitGroupSetStructure +
            "&categoryOptionComboName=" + categoryOptionComboName;
            
        var url = "generateResourceTable.action";
        
        var request = new Request();
        request.sendAsPost( params );
        request.setCallbackSuccess( generateResourceTableReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function generateResourceTableReceived( messageElement )
{
    setMessage( i18n_resource_tables_generated );
}
