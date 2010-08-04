
//------------------------------------------------------------------------------
// Organisation Tree
//------------------------------------------------------------------------------
function treeClicked() {
	numberOfSelects++;

	setMessage(i18n_loading);

	document.getElementById("Lock").disabled = true;
	document.getElementById("Unlock").disabled = true;
}

function selectCompleted(selectedUnits) {
	numberOfSelects--;

	if (numberOfSelects <= 0) {
		hideMessage();

		document.getElementById("Lock").disabled = false;
		document.getElementById("Unlock").disabled = false;
	}
}

function selectReceived() {
	selectionTree.buildSelectionTree();
}
// ------------------------------------------------------------------------------
// Tree Selection validation Method
// ------------------------------------------------------------------------------
function orgUnitSelectValidation()
{
	var request = new Request();
	request.setResponseTypeXML('message');
	request.setCallbackSuccess(orgUnitSelectValidationCompleted);

	var requestString = 'orgUnitValidate.action';

	request.send(requestString);

	return false;
}

function orgUnitSelectValidationCompleted(messageElement)
{
	var type = messageElement.getAttribute('type');
	var message = messageElement.firstChild.nodeValue;

	if (type == 'success')
	{
		document.forms['lockingForm'].submit();
	} 
	else if (type == 'input') 
	{
		// setMessage( i18n_loading );
		document.getElementById('message').innerHTML = message;
		document.getElementById('message').style.display = 'block';
	}
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
