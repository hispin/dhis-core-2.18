
function setAreaItem( area, item )
{
    $.get( "setAreaItem.action", {
        'area' : area,
        'item' : item
    }, function()
    {
        window.location.href = "index.action";
    } );
}

function clearArea( area )
{
    $.get( "clearArea.action", {
        'area' : area
    }, function()
    {
        window.location.href = "index.action";
    } );
}

function viewChart( url )
{
    var width = 700 + 20;
    var height = 500 + 20;

    $( "#chartImage" ).attr( "src", url );
    $( "#chartView" ).dialog( {
        autoOpen : true,
        modal : true,
        height : height + 35,
        width : width,
        resizable : false,
        title : "Viewing Chart"
    } );
}
