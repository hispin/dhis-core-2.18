
function getOUDeatilsForGADataElements(orgUnitIds)
{
	document.getElementById( "ougGroupSetCB" ).disabled = false;
	document.getElementById( "orgUnitGroupList" ).disabled = false;
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId:orgUnitIds
		},
		function (data)
		{
			getOUDetailsForGARecevied(data);
		},'xml');
}

function getOUDetailsForGARecevied(xmlObject)
{
	var ouListCDId = document.getElementById( "orgUnitListCB" );
	var categoryIndex = document.ChartGenerationForm.categoryLB.selectedIndex;
 
	
    var index = 0;		
    var i=0;
		
    var orgUnits = xmlObject.getElementsByTagName("orgunit");
   
    for ( var i = 0; i < orgUnits.length; i++ )
    {
       
    	var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

         currentOrgUnitId = id;
         currentOrgUnitName = orgUnitName;

        if(document.ChartGenerationForm.categoryLB.options[categoryIndex].value == "period" || document.ChartGenerationForm.categoryLB.options[categoryIndex].value == "children" )
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                document.ChartGenerationForm.orgUnitListCB.options[0] = null;
            }
            document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(orgUnitName,id,false,false);
        }
        //22/10/2010
        else if( document.ChartGenerationForm.categoryLB.options[categoryIndex].value == "random" && document.getElementById( 'ougGroupSetCB' ).checked )
        {
        	//ouListCDId.options[ouListCDId.options.length] = new Option(orgUnitName,id,false,false);
        	
        	index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                document.ChartGenerationForm.orgUnitListCB.options[0] = null;
            }
            document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(orgUnitName,id,false,false);
            
        }
        else
        {
            index = document.ChartGenerationForm.orgUnitListCB.options.length;
            for(i=0;i<index;i++)
            {
                if(id == document.ChartGenerationForm.orgUnitListCB.options[i].value) return;
            }
            document.ChartGenerationForm.orgUnitListCB.options[index] = new Option(orgUnitName,id,false,false);
        }
    }	
    		
}

// function for getting periods
function getPeriods() {
	var periodTypeList = document.getElementById("periodTypeLB");
	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;

	var periodLB = document.getElementById("periodLB");

	periodLB.disabled = false;

	clearList(periodLB);

	if (periodTypeId == monthlyPeriodTypeName) {
		for (i = 0; i < monthNames.length; i++) {
			periodLB.options[i] = new Option(monthNames[i], i, false, false);
		}
	} else if (periodTypeId == quarterlyPeriodTypeName) {
		for (i = 0; i < quarterNames.length; i++) {
			periodLB.options[i] = new Option(quarterNames[i], i, false, false);
		}
	} else if (periodTypeId == sixmonthPeriodTypeName) {
		for (i = 0; i < halfYearNames.length; i++) {
			periodLB.options[i] = new Option(halfYearNames[i], i, false, false);
		}
	} else if (periodTypeId == yearlyPeriodTypeName) {
		periodLB.disabled = true;
	}
}
//function for getting periods ends

// OrgUnit GroupSet Change Function
/*
function orgUnitGroupSetCB1() {
	var orgUnitGroupSetList = document.getElementById('orgUnitGroupSetListCB');
	var orgUnitList = document.getElementById('orgUnitListCB');
	if (document.getElementById('ougSetCB').checked) {
		$('#orgUnitGroupSetListCB').removeAttr('disabled');

		getOrgUnitGroups();
	} else {
		$("#orgUnitGroupSetListCB").attr("disabled", "disabled");
	}
	clearList(orgUnitList);
}
*/
function getOrgUnitGroupsDataElements() 
{
	var checked = byId('ougGroupSetCB').checked;
	clearListById('orgUnitGroupList');
	clearListById('orgUnitListCB');
	
	document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(currentOrgUnitName,currentOrgUnitId,false,false);

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



 //Category ListBox Change function
function categoryChangeFunction1(evt)
{
        selCategory = $("select#categoryLB").val();

	if(selCategory == "period" || selCategory == "children" )
	{
		clearListById('orgUnitListCB');
		document.ChartGenerationForm.orgUnitListCB.options[0] = new Option(currentOrgUnitName,currentOrgUnitId,false,false);
	}
	else
	{
           // $('#facilityLB').removeAttr('disabled');
	}
} // categoryChangeFunction end
			          
//Removes slected orgunits from the Organisation List
function remOUFunction()
{
	var index = document.ChartGenerationForm.orgUnitListCB.options.length;
	var i = 0;
	for (i = index - 1; i >= 0; i--)
	{
		if (document.ChartGenerationForm.orgUnitListCB.options[i].selected)
			document.ChartGenerationForm.orgUnitListCB.options[i] = null;
	}
}// remOUFunction end

// singleSelectionOption OrgUnitGroup
function selectSingleOptionOrgUnitGroup()
{
	//alert("inside single selection");
	var categoryObj = document.getElementById( 'categoryLB' );// view by
    var categoryVal = categoryObj.options[ categoryObj.selectedIndex ].value;
	
    var orgGroupObj = document.getElementById( 'orgUnitGroupList' ); // org unit group
    var orgGroupVal = orgGroupObj.options[ orgGroupObj.selectedIndex ].value;
    
//    var categoryObj = document.getElementById( 'categoryLB' );
//    var categoryVal = categoryObj.options[ categoryObj.selectedIndex ].value;
	
    if( document.getElementById( 'ougGroupSetCB' ).checked &&  categoryVal == "period"  )
    {
        var orgUnitGroupListObj = document.getElementById('orgUnitGroupList');
	
        for( var i = 0; i < orgUnitGroupListObj.length; i++ )
        {
            if( i != orgUnitGroupListObj.selectedIndex )
            	orgUnitGroupListObj.options[i].selected = false;
        }
    }
}


//  singleSelectionOption OrgUnit
/*
function selectSingleOrgUnitOption()
{
    var orgUnitObj = document.getElementById( 'categoryLB' ); //view by 
    var orgUnitVal = orgUnitObj.options[ orgUnitObj.selectedIndex ].value;
	
   // var categoryObj = document.getElementById( 'orgUnitGroup' );// org unit group
   // var categoryVal = categoryObj.options[ categoryObj.selectedIndex ].value;
	
    if( document.getElementById( 'ougGroupSetCB' ).checked && ( orgUnitVal == "random" ))
    {
        var orgUnitListObj = document.getElementById('orgUnitListCB');
	
        for( var i = 0; i < orgUnitListObj.length; i++ )
        {
            if( i != orgUnitListObj.selectedIndex )
                orgUnitListObj.options[i].selected = false;
        }
    }
}
*/

// Selected Button (ie ViewSummary or ViewChart) Function
function selButtonFunction1(selButton)
{
	document.ChartGenerationForm.selectedButton.value = selButton;
}
 // selButtonFunction end


//Graphical Analysis Form Validations
function formValidationsDataElement()
{
		
	//var selectedServices = document.getElementById("selectedServices");

	var selOUListLength = document.ChartGenerationForm.orgUnitListCB.options.length;//alert(selOUListLength);
	var selDEListSize  = document.ChartGenerationForm.selectedDataElements.options.length;//alert(selDEListSize);
	
	var orgUnitListCB = document.getElementById("orgUnitListCB");
	var selectedDataElements = document.getElementById("selectedDataElements");
	
	var orgUnitGroupCB = document.getElementById("orgUnitGroupList");
	
	var selOUGroupListLength = document.ChartGenerationForm.orgUnitGroupList.options.length;
	
	var selyearLB = document.getElementById("yearLB");
    var selperiodLB = document.getElementById("periodLB");
  
    var periodTypeList = document.getElementById( "periodTypeLB" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;//alert(periodTypeId);
	
    var k = 0;

    if(  selDEListSize <= 0 ) 
		{
	        alert( "Please Select DataElement(s)" );
	        return false;
		}
    
    else if(  selOUListLength <= 0 ) 
		{
	        alert( "Please Select OrganisationUnit" );
	        return false;
		}
   
    else if(document.getElementById( 'ougGroupSetCB' ).checked && orgUnitGroupCB.selectedIndex < 0 ) 
    	{
    		alert( "Please select OrgUnitGroup" );
    		return false;
    	/*if( orgUnitGroupCB.selectedIndex < 0 ) 
	    	{
	            alert( "Please select OrgUnitGroup" );
	            
	        }*/
    	}	
    else if( periodTypeId == yearlyPeriodTypeName )
	   {
	       if( selyearLB.selectedIndex < 0 ) 
	       {
	           alert("Please select Year(s)");
	           return false;
	       }
	   }
   else
   {
       if( selyearLB.selectedIndex < 0 ) 
       {
           alert("Please select Year(s)");
           return false;
       }
       if( selperiodLB.selectedIndex < 0 ) 
       {
           alert("Please select Period(s)");
           return false;
       }
   }
  
	if( selDEListSize > 0 )
	{
		for(k=0;k<document.ChartGenerationForm.selectedDataElements.options.length;k++)
    	{
    		document.ChartGenerationForm.selectedDataElements.options[k].selected = true;
        } 
	}

    if( selOUListLength > 0 )
    {
    	for(k = 0; k < orgUnitListCB.options.length; k++)
        {
    		orgUnitListCB.options[k].selected = true;
        }
    }
  
    var sWidth = 1000;
	var sHeight = 1000;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindowDataElement','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
} 
// formValidations Function End

