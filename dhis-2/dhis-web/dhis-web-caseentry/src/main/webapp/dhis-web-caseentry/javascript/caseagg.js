
// Removes slected orgunits from the Organisation List
function remOUFunction()
{
	var ouList = document.getElementById("orgUnitListCB");
	var index = ouList.options.length;
    var i=0;
    for(i=index-1;i>=0;i--)
    {
    	if(ouList.options[i].selected)
    		ouList.options[i] = null;
    }
}// remOUFunction end


//Getting corresponding Period List for Datasets. 
function getdSetPeriods()
{
  var dataSetList = document.getElementById("selectedDataSets");
  var dataSetId = dataSetList.options[ dataSetList.selectedIndex].value;
  
  if(dataSetId == '0')
  {
	return;
  }
  
	var url = "getDataSetPeriods.action?selectedDataSets=" + dataSetId;
    
    var request = new Request();
    request.setResponseTypeXML( 'period' );
    request.setCallbackSuccess( getdSetPeriodsReceived );
    request.send( url ); 
}	 


function getdSetPeriodsReceived( xmlObject )
{	
	var sDateLB = document.getElementById( "sDateLB" );
	var eDateLB = document.getElementById( "eDateLB" );
	
	
	var periods = xmlObject.getElementsByTagName( "period" );
	
	for ( var i = 0; i < periods.length; i++)
	{
		var periodType = periods[ i ].getElementsByTagName( "periodtype" )[0].firstChild.nodeValue;
		
		if(i ==0 )
		{
		  if( periodType == curPeriodType )
		  {
			   break;
		  }
		  else
		  {
			   curPeriodType = periodType;
			   clearList( sDateLB );
               clearList( eDateLB );
		  }
		}
		
		
		var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
							
			var option1 = document.createElement( "option" );
			option1.value = id;
			option1.text = periodName;
			sDateLB.add( option1, null );
			
			var option2 = document.createElement( "option" );
			option2.value = id;
			option2.text = periodName;
			eDateLB.add( option2, null);				
	}	
}


function getOrgUDetails(orgUnitIds)
{
	var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOrgUDetailsRecevied );
	request.send( url );
}

function getOrgUDetailsRecevied(xmlObject)
{		
	var ouList = document.getElementById("orgUnitListCB");
	var orgUnits = xmlObject.getElementsByTagName("orgunit");
	
	clearList(ouList);
	
    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		ouList.options[0] = new Option(orgUnitName,id,false,false);
    }
}

//CaseAgg Form Validations
function formValidationsForCaseAggMapping()
{
	var selOUListIndex = document.caseAggregationForm.orgUnitListCB.options.length;
	var selDSListSize  = document.caseAggregationForm.selectedDataSets.options.length;
	
    sDateIndex    = document.caseAggregationForm.sDateLB.selectedIndex;
    eDateIndex    = document.caseAggregationForm.eDateLB.selectedIndex;
    sDateTxt = document.caseAggregationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM - y")),"yyyy-MM-dd");
    eDateTxt = document.caseAggregationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM - y")),"yyyy-MM-dd");
    if(selOUListIndex <= 0) {alert("Please Select OrganisationUnit"); return false;}
    else if(selDSListSize <= 0) {alert("Please Select DataSet"); return false;}
    else if(sDateIndex < 0) {alert("Please Select Starting Period"); return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period"); return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater"); return false;}

	var k=0;
	
    for(k=0;k<selOUListIndex;k++)
    {
    	document.caseAggregationForm.orgUnitListCB.options[k].selected = true;
    }
  	  	 	
  	return true;

} // formValidations Function End	
