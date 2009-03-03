
function validate()
{
    var periodType = getListValue( "periodTypeId" );
    var fromDate = document.getElementById( "fromDate" ).value.split('-');
    var toDate = document.getElementById( "toDate" ).value.split('-');
    var mode = document.getElementById( "searchOption" ).value;
    
    if ( periodType == "null" )
    {
        setMessage( "Please choose period type" );
        return false;
    }
    
    if ( fromDate[0] != "" && fromDate.length != 3 )
    {
        setMessage( "Please enter valid from date" );
        return false;
    }
    
    if ( toDate[0] != "" && toDate.length != 3 )
    {
        setMessage( "Please enter valid to date" );
        return false;
    }
    
    if ( fromDate[0] > toDate[0] )
    {
        setMessage( "From date is later than to date" );
        return false;
    }
    
    if ( fromDate[0] == toDate[0] && fromDate[1] > toDate[1] )
    {
        setMessage( "From date is later than to date" );
        return false;
    }
    
    if ( fromDate[0] == toDate[0] && fromDate[1] == toDate[1] && fromDate[2] > toDate[2] )
    {
        setMessage( "From date is later than to date" );
        return false;
    }
    
    if ( mode == "null" )
    {
        setMessage( "Please select browse mode" );
        return false;
    }
    
    return true;
}

function modeHandler()
{
    var modeList = document.getElementById( "searchOption" );
    var modeSelection = modeList.value;
    
    var treeSection = document.getElementById( "treeSection" );
    
    if ( modeSelection == "OrganisationUnit" )
    {   
        treeSection.style.display = "block";
    }
    else
    {   
        treeSection.style.display = "none";
    }
}
