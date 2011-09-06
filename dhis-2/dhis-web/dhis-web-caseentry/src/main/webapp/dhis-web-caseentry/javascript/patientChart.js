
// -----------------------------------------------------------------------------
// View chart
// -----------------------------------------------------------------------------

function viewChart( url, size )
{
    var width = size === 'wide' ? 1000 : 700;
    var height = size === 'tall' ? 800 : 500;

    $( '#chartImage' ).attr( 'src', url );
    $( '#chartView' ).dialog( {
        autoOpen : true,
        modal : true,
        height : 450,
        width : 600,
        resizable : false,
        title : 'Viewing Chart'
    } );
}
