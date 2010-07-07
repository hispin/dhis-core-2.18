function formValidations()
{
		
		return true;
} // formValidations Function End

function getDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    if(document.getElementById("deopcombo").checked)
    {
     deopcombo = "optioncombo";
    }
    else
    {
    	deopcombo = "dataelement";
    }
    //var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    if ( dataElementGroupId != null )
    {
        var url = "getDataElements.action?id=" + dataElementGroupId + "&deOptionValue=" + deopcombo;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getDataElementsReceived);
        request.send(url);
    }
}// getDataElements end     

function getDataElementTargets()
{
    var dataElementGroupList = document.getElementById("dataElementTargetGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    if(document.getElementById("deopcombo").checked)
    {
     deopcombo = "optioncombo";
    }
    else
    {
    	deopcombo = "dataelement";
    }
    if ( dataElementGroupId != null )
    {
        var url = "getDataElements.action?id=" + dataElementGroupId + "&deOptionValue=" + deopcombo;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getTargetsReceived);
        request.send(url);
    }
}// getDataElementTargets end     

function getmsg()
{
    var de = document.getElementById("availableDataElements");
    var deID = de.options[ de.selectedIndex ].value;
    
     var detarget = document.getElementById("availableDataElementTarget");
    if(detarget.selectedIndex >=0) detarget.options[detarget.selectedIndex].selected = false ;
    if ( deID != null )
    {
        var url = "getmessage.action?id=" + deID;
        
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess( getMsgReceived );
        request.send(url);
    }
}

function getMsgReceived( xmlObject )
{	
    var availableTargets = document.getElementById("availableDataElementTarget");

    var dataElements = xmlObject.getElementsByTagName("dataElement");
    
    for ( var i = 0; i < dataElements.length; i++ )
    {
        var dename = dataElements[ i ].getElementsByTagName("dename")[0].firstChild.nodeValue;
        var targetname = dataElements[ i ].getElementsByTagName("targetname")[0].firstChild.nodeValue;
        var targetid = dataElements[ i ].getElementsByTagName("targetid")[0].firstChild.nodeValue;
                                       
        if( targetid != "-1" )
        {
        	for( var k=0; k < availableTargets.options.length; k++ )
        	{
        		if( availableTargets.options[k].value == targetid )
        		{
        			availableTargets.options[k].selected = true;
        		}
        	}	
        }
        
        document.getElementById("message").innerHTML = "<h3><font color='blue'>"+ dename + " - " + targetname + "</font></h3>";                
    }
        
}// getDataElementsReceived end


function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");
    //var selectedDataElements = document.getElementById("selectedDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        
        
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);       
    }
        
}// getDataElementsReceived end


function getTargetsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElementTarget");
    //var selectedDataElements = document.getElementById("selectedDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        
        
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);       
    }
        
}// getDataElementsReceived end


function getsave()
{
	var de = document.getElementById("availableDataElements");
    var deID = de.options[ de.selectedIndex ].value;
    var detarget = document.getElementById("availableDataElementTarget");
    var detargetID = detarget.options[ detarget.selectedIndex ].value;    
       
    if(detarget.selectedIndex >=0) detarget.options[detarget.selectedIndex].selected = false ;
    
     var url = 'saveMapping.action?deID=' + deID + '&detargetID=' + detargetID;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess( getMsgReceived );
        request.send(url);
	
}

function remMappingFunction()
{
	var de = document.getElementById("availableDataElements");
    var deID = de.options[ de.selectedIndex ].value;
    
	var detarget = document.getElementById("availableDataElementTarget");
    var detargetID = detarget.options[ detarget.selectedIndex ].value;    
    
     detarget.options[detarget.selectedIndex].selected = false ;
	       
            var url = 'delMapping.action?deID=' + deID + '&detargetID=' + detargetID;
 				var request = new Request();
                request.setResponseTypeXML('dataElement');
                request.setCallbackSuccess( getMsgReceived );
                request.send(url);           
      
}// remMappingFunction end


