/*
 * Data element selector plugin for FCK editor.
 * Christian Mikalsen <chrismi@ifi.uio.no>
 */

// Register the command.
var programStageIdField = window.parent.document.getElementById( 'associationIdField' );
var programStageId = programStageIdField.value;
var urlLocation = window.parent.location.href;
var urlParts = new Array();
urlParts = urlLocation.split('viewDataEntryForm.action');
var urlPath = urlParts[0]+'selectProgramStageDataElement.action?programStageId='+programStageId;

FCKCommands.RegisterCommand( 'Insert data element of other program stage', new FCKDialogCommand( 'Insert data element of other program stage', 'Data element selector', urlPath, 1000, 550 ) ) ;


// Create the "Insert Data element" toolbar button.
var oInsertDataElementItem = new FCKToolbarButton( 'Insert data element of other program stage', FCKLang.PlaceholderBtn ) ;
oInsertDataElementItem.IconPath = FCKPlugins.Items['programstagedataelement'].Path + 'programStage.gif' ;
FCKToolbarItems.RegisterItem( 'InsertProgramStageDataElement', oInsertDataElementItem ) ;

// The object used for all operations.
var FCKSelectProgramStageElement = new Object() ;

// Called by the popup to insert the selected data element.
FCKSelectProgramStageElement.Add = function( programStageId, dataElementId, dataElementName, dataElementType, dispName, viewByValue, selectedOptionComboIds, selectedOptionComboNames)
{
    viewByValue = "@@"+viewByValue+"@@";
    var strPSDataEntryId   = "value["+ programStageId +"].value:value["+ dataElementId +"].value";
    var comboPSDataEntryId = "value["+ programStageId +"].combo:value["+ dataElementId +"].combo";
    var boolPSDataEntryId  = "value["+ programStageId +"].boolean:value["+ dataElementId +"].boolean";
    var datePSDataEntryId  = "value["+ programStageId +"].date:value["+ dataElementId +"].date";
    
    
//    	for(k=0; k<selectedOptionComboIds.length; k++)
//        {
//    		var optionComboId = selectedOptionComboIds[k];
//            var optionComboName = selectedOptionComboNames[k];
//            var titleValue = "-- "+dataElementId + ". "+ dataElementName+" "+optionComboId+". "+optionComboName+" ("+dataElementType+") --";
//            var strPSDataEntryId =  "value["+ programStageId +"].value:value["+ dataElementId +"].value:value["+ optionComboId +"].value";
//    		var selectString = "";
//            selectString = '<input type="text" id='+strPSDataEntryId+' title="'+titleValue+'" value="" />' ;
//            FCK.InsertHtml(selectString);
//        }
    	 var selectString = "";
         if(dataElementType == "string" && (selectedOptionComboNames.indexOf("(default)")== -1 ))
         {
             selectString = "<select name=\"entryselect\" id=\""+comboPSDataEntryId+"\" > <option value=\"\">i18n_select_value</option>";

             for(k=0; k<selectedOptionComboIds.length; k++)
             {
                 //FCK.InsertHtml("<option value=\""+psOptionComboId+"\" >$encoder.htmlEncode(\""+psOptionComboName+"\")</option>");
                 selectString += "<option value=\""+selectedOptionComboIds[k]+"\" id=\"combo["+selectedOptionComboIds[k]+"].combo\" >("+selectedOptionComboNames[k]+")</option>";
             }
             selectString += "</select>";

             FCK.InsertHtml(selectString);
         }
         else if (dataElementType == "bool")
         {
             selectString = "<select name=\"entryselect\" id=\""+boolPSDataEntryId+"\" > <option value=\"\">i18n_select_value</option>";
             selectString += "<option value=\"true\" >i18n_yes</option>";
             selectString += "<option value=\"false\" >i18n_no</option>";
             selectString += "</select>";

             FCK.InsertHtml(selectString);
         }
         else if (dataElementType == "date")
         {
             selectString = "<input type=\"text\" id=\""+datePSDataEntryId+"\" name=\"entryfield\" value=\"\" >";
             selectString += "<img src=\"../images/calendar_icon.gif\"   id=\"get_"+programStageId+"_"+dataElementId+"\" style=\"cursor: pointer;\" title=\"Select Date\" onmouseover=\"this.style.background=\'orange\';\" onmouseout=\"this.style.background=\'\'\"";
             selectString += "<script type=\"text/javascript\">";
             selectString += "Calendar.setup({";
             selectString += "inputField     :    \""+datePSDataEntryId+"\",";
             selectString += "ifFormat       :    \"yyyy-mm-dd\"," ;
             selectString += "button         :    \"get_"+programStageId+"_"+dataElementId+"\" ";
             selectString += "});";
             selectString += "</script>";

             FCK.InsertHtml(selectString);

         }
         else if ( dataElementType == "int" &&  selectedOptionComboIds.length > 0 )
         {
         	for(k=0; k<selectedOptionComboIds.length; k++)
         	{
         		  var optionComboId = selectedOptionComboIds[k];
                  var optionComboName = selectedOptionComboNames[k];

                  var titleValue = "-- "+dataElementId + ". "+ dataElementName+" "+optionComboId+". "+optionComboName+" ("+dataElementType+") --";
                  var displayName = dispName+" - "+optionComboName+" ]";
                  var dataEntryId = "value[" + programStageId + "].value:value[" + dataElementId + "].value:value[" + optionComboId + "].value";
                  FCK.InsertHtml("<input title=\"" + titleValue + "\" view=\""+viewByValue+"\" value=\"" + displayName + "\" name=\"entryfield\" id=\"" + dataEntryId + "\" /><br/>");
         	}
         }else{
        	 strPSDataEntryId  = strPSDataEntryId + ":value["+ selectedOptionComboIds[0] +"].value";
             FCK.InsertHtml("<input name=\"entryfield\" id=\""+strPSDataEntryId+"\" type=\"text\" value=\"\" onkeypress=\"return keyPress(event, this)\" >");
         }
   
}
