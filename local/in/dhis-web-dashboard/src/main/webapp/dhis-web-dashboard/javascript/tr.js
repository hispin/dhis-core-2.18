function formValidations()
{
    var orgUnitListCB = document.getElementById("orgUnitListCB");
    var selOUListLength = document.tabularAnalysisForm.orgUnitListCB.options.length;
    
    sDateIndex    = document.targetAnalysisForm.sDateLB.selectedIndex;
    eDateIndex    = document.targetAnalysisForm.eDateLB.selectedIndex;
    sDateTxt = document.targetAnalysisForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.targetAnalysisForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");

    if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
    orgUnitListCB.disabled = false;
    
    return true;
} // formValidations Function End

function deSelectionChangeFuntion( listId1, listId2 )
{
    var list1 = document.getElementById( listId1 );
    var list2 = document.getElementById( listId2 );

    clearList( list1 );
	
    for(var i=list2.options.length-1; i >= 0; i--)
    {
        option = list2.options[ i ];
        var optValue = option.value;
        var partsOfOptVal = new Array();
        partsOfOptVal = optValue.split(":");
        if(partsOfOptVal[0] == "D")
        {
            list2.remove( i );
        }
    }
	
    getDataElements();
}

function getDataElements()
{
	var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
    
    var deSelectionList = document.getElementById("deSelection");    
    var deOptionValue = deSelectionList.options[ deSelectionList.selectedIndex ].value;
    
    if ( dataElementGroupId != null )
    {
		$.post("getDataElementsForTA.action",
		{
			id:dataElementGroupId,
			deOptionValue:deOptionValue
		},
		function (data)
		{
			getDataElementsReceived(data);
		},'xml');
    }
}// getDataElements end      

function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = "D:"+dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);
    }
    
}
// getDataElementsReceived end

function getOUDeatilsForTaget( orgUnitIds )
{
	document.getElementById( "ougGroupSetCB" ).disabled = false;
	document.getElementById( "orgUnitGroupList" ).disabled = false;
	jQuery.postJSON("getOrgUnitName.action",{
  	  id : orgUnitIds[0]
   }, function( json ){
         setFieldValue( "ouNameTB",json.organisationUnit.name );
   });
}

function getOUDetailsForTargetRecevied(xmlObject)
{
	var element = dataelement.getElementsByTagName("orgunit");
    var orgUnitname = element[0].getElementsByTagName("OugUnitName")[0].firstChild.nodeValue;
    document.targetAnalysisForm.ouNameTB.value = orgUnitname;
}

function getOrgUnitGroupsDataElements() 
{
	var checked = byId('ougGroupSetCB').checked;
	clearListById('orgUnitGroupList');
	
	if (checked)
	{
		var ouGroupId = document.getElementById("orgUnitGroupList");
		for ( var i = 0; i < orgUnitGroupIds.length; i++) 
		{

			var option = document.createElement("option");
			option.value = orgUnitGroupIds[i];
			option.text = orgUnitGroupNames[i];
			option.title = orgUnitGroupNames[i];
			ouGroupId.add(option, null);
		}
	}
	else
	{
	//document.getElementById( "ougGroupSetCB" ).disabled = true;
	}
	//clearList( ouGroupId );
}

function showOverlay() 
{
    var o = document.getElementById('overlay');
    o.style.visibility = 'visible';
    jQuery("#overlay").css({
        "height": jQuery(document).height()
    });
    jQuery("#overlayImg").css({
        "top":jQuery(window).height()/2
    });
}
function hideOverlay() 
{
    var o = document.getElementById('overlay');
    o.style.visibility = 'hidden';
}