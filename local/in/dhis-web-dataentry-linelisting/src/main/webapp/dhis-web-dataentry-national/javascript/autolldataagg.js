
function getOUDetails(orgUnitIds)
{
	//var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	/*
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOUDetailsRecevied );
	
    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString );
    */
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds[0]
			},
			function (data)
			{
				getOUDetailsRecevied(data);
			},'xml');

    
    
}

function getOUDetailsRecevied(xmlObject)
{
	var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		document.getElementById("selOrgUnitName").value = orgUnitName;	
    }    		
}

