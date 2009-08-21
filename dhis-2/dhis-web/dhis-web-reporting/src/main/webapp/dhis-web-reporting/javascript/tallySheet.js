//-----------------------------------------------------------------------------
// Step 1 functions
//----------------------------------------------------------------------------

function continueToStepTwo() {
	if (validateData()) {
		setMessage(i18n_processing + "...");
		document.getElementById("tallySheetForm").submit();
	}
}

//-----------------------------------------------------------------------------
// Step 2 functions
// ----------------------------------------------------------------------------

function setChecked(id) {
	var value = document.getElementById("checkbox" + id).checked;
	document.getElementById("checked" + id).value = value;
	if (value == true) {
		if (document.getElementById("rows" + id).value == 0) {
			document.getElementById("rows" + id).value = 1;
		}
	}
}

function doRecalculate() {
	document.getElementById('recalculate').value = true;
	document.getElementById('configureTallySheetForm').submit();
}

function selectAll() {
	var length = document.configureTallySheetForm.checkbox.length;
	for (i = 0; i < length; i++) {
		document.configureTallySheetForm.checkbox[i].checked = true;
		document.configureTallySheetForm.checked[i].value = true;
		if (document.configureTallySheetForm.rows[i].value == 0) {
			document.configureTallySheetForm.rows[i].value = 1;
		}
	}
}

function selectNone() {
	var length = document.configureTallySheetForm.checkbox.length;
	for (i = 0; i < length; i++) {
		document.configureTallySheetForm.checkbox[i].checked = false;
		document.configureTallySheetForm.checked[i].value = false;
	}
}

function generatePdf() {
	document.getElementById('configureTallySheetForm').action = "generateTallySheetPDF.action";
	document.getElementById('configureTallySheetForm').submit();
	document.getElementById('configureTallySheetForm').action = "configureTallySheetGenerator.action";
}

//-----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;

function setSelectedOrganisationUnitIds(ids) {
	selectedOrganisationUnitIds = ids;
}

if (selectionTreeSelection) {
	selectionTreeSelection.setListenerFunction(setSelectedOrganisationUnitIds);
}

function validateData() {
	var tallySheetName = document.getElementById("tallySheetName").value;

	if (!getListValue("selectedDataSetId")
			|| getListValue("selectedDataSetId") == "null") {
		setMessage(i18n_select_data_set);
		return false;
	}

	if (!tallySheetName) {
		setMessage(i18n_type_tally_sheet_name);
		return false;
	}

	if (selectedOrganisationUnitIds == null
			|| selectedOrganisationUnitIds.length == 0) {
		setMessage(i18n_select_organisation_unit);
		return false;
	}

	return true;
}