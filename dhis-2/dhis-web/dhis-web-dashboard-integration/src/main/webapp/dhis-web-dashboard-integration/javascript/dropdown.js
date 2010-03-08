
function setAreaItem( area, item )
{
  var request = new Request();  
  request.setCallbackSuccess( setAreaItemReceived );    
  request.send( "setAreaItem.action?area=" + area + "&item=" + item );
}

function setAreaItemReceived( messageElement )
{
  window.location.href = "index.action";
}

function clearArea( area )
{
  var request = new Request();  
  request.setCallbackSuccess( clearAreaReceived );    
  request.send( "clearArea.action?area=" + area );
}

function clearAreaReceived( messageElement )
{
  window.location.href = "index.action";
}

function viewChart( url )
{
    window.open( url, "_blank", "directories=no, height=560, width=760, location=no, menubar=no, status=no, toolbar=no, resizable=yes, scrollbars=yes" );
}
