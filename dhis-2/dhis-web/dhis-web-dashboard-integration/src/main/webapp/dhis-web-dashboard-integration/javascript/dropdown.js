
function setAreaItem( area, item )
{
    var request = new Request();
    request.setCallbackSuccess(setAreaItemReceived);
    request.send("setAreaItem.action?area=" + area + "&item=" + item);
}

function setAreaItemReceived( messageElement )
{
    window.location.href = "index.action";
}

function clearArea( area )
{
    var request = new Request();
    request.setCallbackSuccess(clearAreaReceived);
    request.send("clearArea.action?area=" + area);
}

function clearAreaReceived( messageElement )
{
    window.location.href = "index.action";
}

function viewChart( url )
{
    var width = size === 'wide' ? 1000 : 700;
    var height = size === 'tall' ? 800 : 500;

    $('#chartImage').attr('src', url);
    $('#chartView').dialog({
        autoOpen : true,
        modal : true,
        height : height + 35,
        width : width,
        resizable : false,
        title : 'Viewing Chart'
    });
}
