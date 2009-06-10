
// -----------------------------------------------------------------------------
// Save
// -----------------------------------------------------------------------------

function saveName()
{
    var name = document.getElementById( "patientName" ).value;
    var gender = document.getElementById( "gender" ).value;
    alert(name);
    var url = "index.action?patientName=" + name;
    var request = new Request();
    request.setCallbackSuccess( nameReceived );
    request.send( url );
    request.setResponseTypeXML('');
    return false;
}
function nameReceived( name )
{
    document.getElementById( "patientName" ).disabled = true;
}
