
// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------

function initLists()
{
    var id;

	for ( id in selectedDataSets )
    {
        $("#selectedDataSets").append( $( "<option></option>" ).attr( "value",id ).text( selectedDataSets[id] )) ;
    }

    for ( id in availableDataSets )
    {
        $("#availableDataSets").append( $( "<option></option>" ).attr( "value",id ).text( availableDataSets[id] )) ;
    }
}
