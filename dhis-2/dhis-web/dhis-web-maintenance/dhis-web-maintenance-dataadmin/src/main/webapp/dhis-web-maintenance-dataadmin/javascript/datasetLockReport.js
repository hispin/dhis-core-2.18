
var clickedButtonElement = null;
var numberOfSelects = 0;
var selectedOrgunits = new Array();
	
function setClickedButtonElementValue( buttonElement )
{
	clickedButtonElement = buttonElement.value;
	document.getElementById("selectBetweenLockUnlock").value = clickedButtonElement;
}

function validateCollectiveDataLockingForm()
{		
	periodIdOptions = document.getElementById("periodId").options;
		
	if( periodIdOptions.length > 0 )
	{
		var i;
		for( i=0; i < periodIdOptions.length ; i++ )
		{
			if( periodIdOptions[i].selected == true )
					break;
		}
		if( i == periodIdOptions.length )
		{
			setHeaderDelayMessage(i18n_period_not_selected);
			return false;
		}
	}
	else
	{
		setHeaderDelayMessage( i18n_period_not_selected );
		return false;
	}
		
	dataSetIdsOptions = document.getElementById("dataSetIds").options;
			
	if( dataSetIdsOptions.length > 0 )
	{
		var i;
		for( i=0; i < dataSetIdsOptions.length ; i++)
		{
			if( dataSetIdsOptions[i].selected == true )
				break;
		}
		if( i==dataSetIdsOptions.length )
		{
			setHeaderDelayMessage( i18n_dataset_not_selected );
			return false;
		}
	}
	else
	{
		setHeaderDelayMessage( i18n_dataset_not_selected );
		return false;
	}
			
    if( clickedButtonElement == i18n_lock || clickedButtonElement == i18n_unlock )
    {
		if ( selectedOrgunits == null || selectedOrgunits.length <= 0 )
		{
			setHeaderDelayMessage( i18n_organisation_unit_not_selected );
			return false;			
		}
    }
	else if( clickedButtonElement == i18n_select_all_at_level || clickedButtonElement == i18n_unselect_all_at_level )
	{	
		levelIdOptions = document.getElementById("levelId").options;
		if(!levelIdOptions.length>0)
			return false;
	}
    else if( clickedButtonElement == i18n_select_all_at_group || clickedButtonElement == i18n_unselect_all_at_group )
    {
		ougGroupOptions = document.getElementById("orgGroup").options;
		if( !ougGroupOptions.length > 0 )
			return false;
    } 
	
	return true;
}

//------------------------------------------------------------------------------
// Organisation Tree
//------------------------------------------------------------------------------
function treeClicked() {
	numberOfSelects++;
	
	document.getElementById("Lock").disabled = true;
	document.getElementById("Unlock").disabled = true;
}

function selectCompleted(selectedUnits) {
	numberOfSelects--;

	if (numberOfSelects <= 0) {

		document.getElementById("Lock").disabled = false;
		document.getElementById("Unlock").disabled = false;
	}
	
	selectedOrgunits = selectedUnits;
}

// ------------------------------------------------------------------------------
// Get Periods correspond to Selected Period Type
// ------------------------------------------------------------------------------

function getPeriods() {
	var periodTypeList = document.getElementById("periodTypeId");
	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;

	if (periodTypeId != null) {
		var url = "getPeriodsForLock.action?name=" + periodTypeId;
		$.ajax( {
			url :url,
			cache :false,
			success : function(response) {
				dom = parseXML(response);
				$('#periodId >option').remove();
				$(dom).find('period').each(
						function() {
							$('#periodId').append(
									"<option value="
											+ $(this).find('id').text() + ">"
											+ $(this).find('name').text()
											+ "</option>");
						});
			}

		});
	}

	enable("periodId");
	enable("Lock");
	enable("Unlock");

	enable("Slevel");
	enable("Unlevel");
	enable("Sgroup");
	enable("Ugroup");
	getDataSets();

}

function parseXML(xml) {
	if (window.ActiveXObject && window.GetObject) {
		var dom = new ActiveXObject('Microsoft.XMLDOM');
		dom.loadXML(xml);
		return dom;
	}
	if (window.DOMParser)
		return new DOMParser().parseFromString(xml, 'text/xml');
	throw new Error('No XML parser available');
}

function getDataSets() {
	var periodTypeList = document.getElementById("periodTypeId");
	var periodType = periodTypeList.options[periodTypeList.selectedIndex].value;

	if (periodType != null) {
		var url = "getDataSetsForPeriodType.action?periodType=" + periodType;
		$.ajax( {
			url :url,
			cache :false,
			success : function(response) {
				$('#dataSetIds >option').remove();
				// $( '#lockedDataSets >option' ).remove();
			$(response).find('dataSet').each(
					function() {
						$('#dataSetIds').append(
								"<option value=" + $(this).find('id').text()
										+ ">" + $(this).find('name').text()
										+ "</option>");
					});
			enable("dataSetIds");
			// enable( "lockedDataSets" );
			// loadEmptyOrgUnitTree();
		}
		});
	}
}
