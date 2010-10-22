
// Indicator or Dataelement radio button changed function
function riradioSelection(evt)
{
    var selriRadioButton = $( "input[name='riRadio']:checked" ).val();
       
    if(selriRadioButton == "dataElementsRadio")
    {
		document.ChartGenerationForm.indicatorGroupId.disabled = true;
	    document.ChartGenerationForm.availableIndicators.disabled = true;
	    document.ChartGenerationForm.selectedIndicators.disabled = true;
	    
	    document.ChartGenerationForm.dataElementGroupId.disabled = false;
	    document.ChartGenerationForm.availableDataElements.disabled = false;
	    document.ChartGenerationForm.selectedDataElements.disabled = false;
  	}// if block end
	else
	{
		document.ChartGenerationForm.indicatorGroupId.disabled = false;
	    document.ChartGenerationForm.availableIndicators.disabled = false;
	    document.ChartGenerationForm.selectedIndicators.disabled = false;
	    
	    document.ChartGenerationForm.dataElementGroupId.disabled = true;
	    document.ChartGenerationForm.availableDataElements.disabled = true;
	    document.ChartGenerationForm.selectedDataElements.disabled = true;
	}// else end
}// function riradioSelection end


function selButtonFunction( selButton )
{  	  
  	if( formValidations() )
  	{
		if(selButton == "SurveyAnalysis")
  	 	{  	 		
  	 		document.ChartGenerationForm.action = "viewSurveyAnalysisResult.action";
  	 		document.ChartGenerationForm.submit();
  	 	}  	 
  	 	else
  	 	{  	 	
  	 		document.ChartGenerationForm.action = "generateAnnualData.action";
  	 		document.ChartGenerationForm.submit();  	 	
  	 	}
  	}  	 
}
  
//Anaul Analysis Form Validations
function formValidations()
{
	var selOuId = document.ChartGenerationForm.ouIDTB.value;
	var availDEListIndex  = document.ChartGenerationForm.availableDataElements.selectedIndex;
	var availIndListIndex = document.ChartGenerationForm.availableIndicators.selectedIndex;
	
    annualPeriodListIndex    = document.ChartGenerationForm.annualPeriodsListCB.selectedIndex;
    monthlyPeriodListIndex    = document.ChartGenerationForm.monthlyPeriodsListCB.selectedIndex;

    if(selOuId == null || selOuId == "") {alert("Please Select OrganisationUnit");return false;}
    else if(selriRadioButton == "dataElementsRadio" && availDEListIndex < 0)	 {alert("Please Select DataElement");return false;}
    else if(selriRadioButton == "indicatorsRadio" && availIndListIndex < 0) {alert("Please Select Indicator");return false;}
    else if(annualPeriodListIndex < 0) {alert("Please Select Year(s)");return false;}
    else if(monthlyPeriodListIndex < 0) {alert("Please Select Month(s)");return false;}
	
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
} // formValidations Function End

//Survey Analysis Form Validations
function formValidationsForSurvey()
{
	var selOuId = document.ChartGenerationForm.ouIDTB.value;
	var availIndListIndex = document.ChartGenerationForm.availableIndicators.selectedIndex;
	
    sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");


    if(selOuId == null || selOuId == "") {alert("Please Select OrganisationUnit");return false;}
    else if(availIndListIndex < 0) {alert("Please Select Indicator");return false;}
    else if(sDateIndex < 0) {alert("Please Select Starting Period");return false;}
    else if(eDateIndex < 0) {alert("Please Select Ending Period");return false;}
    else if(sDate > eDate) {alert("Starting Date is Greater");return false;}
	
    var sWidth = 850;
	var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	return true;
} // formValidations Function End

