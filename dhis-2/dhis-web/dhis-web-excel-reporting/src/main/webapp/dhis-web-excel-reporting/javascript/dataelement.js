
// init a new list from xml file //
function initElementList( targetListId, parentElement, tagName, isHaveOptionALL )  {

	var elementList = byId( targetListId );
	elementList.options.length = 0;
	var elements = parentElement.getElementsByTagName( tagName );

	if ( isHaveOptionALL == true ) {
	
		var id = '-1';
		var elementName = '[ Select All ]';
		addOptionToList( elementList, id, elementName );
	}	
	
	for ( var i = 0; i < elements.length; i++)
	{
		var id = elements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var elementName = elements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		addOptionToList( elementList, id, elementName );
	}
}


// Open DataelementCategoryReportDiv //
function openAddDataElementForm( reportId ) {
	
	setMessage("Div is opened");
	
	setFieldValue( 'hiddenId', reportId );
	
	initDataElementGroupsAndCateCombosForm();
	showSelectedElementsAndAvailableElements( reportId );
	
	resetALLValue( 'dataelementCategoryReportDiv' );
	setPositionCenter( 'dataelementCategoryReportDiv' );
	showDivEffect();
	showById( 'dataelementCategoryReportDiv' );
		
	mode = 'UPDATE';
}


function initDataElementGroupsAndCateCombosForm() {
	
	var url = "getDataElementGroupsAndCateCombosList.action";

	var request = new Request();
	request.setResponseTypeXML( 'elementcategory' );
	request.setCallbackSuccess( initDataElementGroupsAndCateCombosFormCompleted );
	request.send( url );
	
}

function initDataElementGroupsAndCateCombosFormCompleted( elementCategory ) {

	initElementList( 'dataElementGroupList', elementCategory, 'dataElementGroup', true );
	initElementList( 'categoryComboList', elementCategory, 'categoryCombo', false );
}


// get List of SelectedElements AND AvailableElements //
function showSelectedElementsAndAvailableElements( reportId ) {
	
	var url = "getSelectedElementsAndAvailableElements.action?id=" + reportId;
	var request = new Request();
	request.setResponseTypeXML( 'report' );
	request.setCallbackSuccess( showSelectedElementsAndAvailableElementsCompleted );
	request.send( url );
}

function showSelectedElementsAndAvailableElementsCompleted( report ) {

	setMessageReport( getElementValue( report, 'name' ) );

	initElementList( 'availableDataElements', report, 'availableElement', false );
	initElementList( 'selectedDataElements', report, 'selectedElement', false );
}


function writeFieldValueHiddenForm( hiddenDiv, hiddenTag ) {
	
	byId(hiddenDiv).innerHTML = byId(hiddenDiv).innerHTML + hiddenTag;
}


function setMessageReport( message_report )
{
    byId( 'message_report' ).innerHTML = message_report;   
    byId( 'message_report' ).style.display = 'block';
}


function isExistInList( listId, elementName )
{
	var list = byId( listId );
  
    for ( var i=0; i < list.options.length; i++ )
    {
        var value = list.options[i].text;
     
        if ( value.toLowerCase().indexOf( elementName.toLowerCase() ) != -1 )
        {
            return true;
        }
    }
	return false;
}


// Validate to add DataElement AND add CateCombo for ReportCategory //
function validateAddDataElementAndCateCombo()
{
	var element_no = byId('selectedDataElements').options.length;
	var catecombo_id = getListValue( 'categoryComboList' );
	
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddDataElementAndCateComboCompleted );
    request.send( 'validateAddDataElementAndCateCombo.action?dataelement_no=' + element_no + '&catecomboid=' + catecombo_id);
	
	return false;
}

function validateAddDataElementAndCateComboCompleted( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );
    var message = xmlObject.firstChild.nodeValue;
	
	setMessage( message );
		
    if ( type == 'success' )
    {
        var availableDataElements = byId( 'availableDataElements' );
		var categoryCombos = byId( 'categoryComboList' );
        availableDataElements.selectedIndex = -1;
        
		selectAllById('selectedDataElements');
		categoryCombos.options[categoryCombos.selectedIndex].selected = true;
		
		hideById('dataelementCategoryReportDiv');
		hideById('report');
		
		setMessage("hidden all Divs");
		checkModeForModifyReport( mode );
    }
}


function checkModeForModifyReport( mode )
{
	setMessage( mode );
	var dataelementForm = byId( 'addDataElementForm' );
	
	// Call action for UpdateReportExcelCategory //
	if (mode == 'UPDATE') {
	
		var id = getFieldValue("hiddenId");
		setMessage("report.id = [" + id + "] is being updated ...");
		dataelementForm.action = 'updateElementsAndComboReportCategory.action?updateId=' + id;
		dataelementForm.method = 'POST';
		dataelementForm.submit();
	}
	window.location.reload();
}



function checkModeForModifyReport_BIS( mode )
{
	setMessage( mode );
	var dataelementForm = byId( 'addDataElementForm' );
	
	// Call action for AddReportExcelCategory //
	if ( mode == 'ADD' ) {
		dataelementForm.submit();
	}
	// Call action for UpdateReportExcelCategory //
	else {
		var id = getFieldValue("id");
		dataelementForm.action = 'updateReportExcelCategory.action?id=' + id;
		dataelementForm.method = 'POST';
		dataelementForm.submit();
	}
	window.location.reload();
}


function getCategories( dataElementComboById ) {

	var dataElementComboId = getListValue( dataElementComboById );
	
	var url = "getCategoryList.action?dataElementComboId=" + dataElementComboId;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getFilteredCategoriesReceived );
    request.send( url );
}


function getFilteredCategoriesReceived( xmlObject )
{
	initElementList( 'categoryList', xmlObject, 'category', false );
}


function getGroupMembersCompleted( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	
	if( type == 'error' )
    {
        setMessage(xmlObject.firstChild.nodeValue);
    }
    if( type == 'success' )
    {
		setMessage(xmlObject.firstChild.nodeValue);
    }
}


function showCateOptionCombos( cateComboByID )
{	
	var cateComboId = getListValue( cateComboByID );
	
	var url = "getOptionComboList.action?categoryComboId=" + cateComboId;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( showCateOptionCombosReceived );
    request.send( url );
}

function showCateOptionCombosReceived( xmlObject )
{
	var optionComboList = byId( "cateOptionCombo" );
	optionComboList.options.length = 0;
	
	var categoryOptions = xmlObject.getElementsByTagName( "categoryOption" );
	
	for ( var i = 0 ; i < categoryOptions.length ; i++ )
	{
		var categoryOption = categoryOptions[i];
		var id = categoryOption.getAttribute( "id" );
		id = '[*.'+ id + ']';
		var optionComboName = categoryOption.firstChild.nodeValue;
		
		optionComboList.add( new Option(optionComboName, id) , null );
	}
}
