
// DataElement and Its options Change Function
function deSelectionChangeFuntion(evt)
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedDataElements");

	clearList(availableDataElements);
	clearList(selectedDataElements);
	
	getDataElements();
}
		          
//Facility ListBox Change Function
function facilityChangeFunction(evt)
{
	selFacility = evt.target.value;
	if(selFacility == "Immediate Children")
	{
		var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        for(i=0;i<index;i++)
    	{
    		document.ChartGenerationForm.orgUnitListCB.options[0] = null;
    	}
	}
}// facilityChangeFunction end

// Selected Button (ie ViewSummary or ViewChart) Function
function selButtonFunction(selButton)
{
	document.ChartGenerationForm.selectedButton.value = selButton;
}// selButtonFunction end


//Graphical Analysis Form Validations
function formValidations()
{
	
	var selDEListSize  = document.ChartGenerationForm.selectedDataElements.options.length;
	var selOuId = document.ChartGenerationForm.ouIDTB.value;
    sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM - y")),"yyyy-MM-dd");
    eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM - y")),"yyyy-MM-dd");

    if(selOuId == null || selOuId == "") {alert("Please Select OrganisationUnit");return false;}
    else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
    else if(selDEListSize <=0 ) {alert("Please Select Dataelements");return false;}

	var k=0;
	
	for(k=0;k<document.ChartGenerationForm.selectedDataElements.options.length;k++)
    	{
    		document.ChartGenerationForm.selectedDataElements.options[k].selected = true;
        } // for l
	
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
} // formValidations Function End

