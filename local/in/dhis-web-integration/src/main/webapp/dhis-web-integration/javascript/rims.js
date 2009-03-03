

//--------------------------------------
// getRIMS PHCs by District
//--------------------------------------
function getRIMSPHCs()
{
    var rimsDistricts = document.getElementById("rimsDistricts");
    var districtId = rimsDistricts.options[ rimsDistricts.selectedIndex ].value;
    var connections = document.getElementById("connection");
    var connectionId = connections.options[ connections.selectedIndex ].value;
        
    if ( districtId != null )
    {
        var url = "getRIMSPHCs.action?rimsDistrictId=" + districtId +"&connection="+ connectionId;
        var request = new Request();
        request.setResponseTypeXML('orgunit');
        request.setCallbackSuccess(getRIMSPHCsReceived);
        request.send(url);
    }
}// getRIMSDataElements end           


function getRIMSPHCsReceived( xmlObject )
{
    var availableOrgunits = document.getElementById("availableOrgunits");
    var selectedOrgunits = document.getElementById("selectedOrgunits");

    clearList(availableOrgunits);

    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedOrgunits, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = name;
            option.title = name;
            availableOrgunits.add(option, null);
        }
    }
}// getDataElementsReceived end



//--------------------------------------
// get RIMS DataElements
//--------------------------------------
function getRIMSDataElements()
{
    var dataElementGroupList = document.getElementById("rimsDEGroups");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
        
    if ( dataElementGroupId != null )
    {
        var url = "getRIMSDataElements.action?rimsDeGroupId=" + dataElementGroupId;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getRIMSDataElementsReceived);
        request.send(url);
    }
}// getRIMSDataElements end           


function getRIMSDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedDataElements, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);
        }
    }
}// getDataElementsReceived end
