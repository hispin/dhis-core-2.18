jQuery(document).ready(function() {
	validation2('saveDataEntryForm', function() {
		autoSave = false;
		validateDataEntryForm();
	}, {
		'rules' : getValidationRules("dataEntry")
	});

	jQuery("#dataElementSelection").resizable({
		minHeight : 210,
		minWidth : 400,
		width : 400,
		alsoResize : "#dataElementList"
	});

	jQuery("#dataElementSelection").draggable({
		handle : 'h3'
	});

	leftBar.hideAnimated();

	select(1);

});

function timedCount() {
	validateDataEntryForm();
	t = setTimeout("timedCount()", 20000); // ms
	byId('message').style.display = 'none';
}

function select(id) {
	if (selected != null) {
		jQuery("#tr" + selected).removeClass("selected");
	}

	jQuery("#tr" + id).addClass("selected");

	selected = id;
}

function insertDataElement() {
	var oEditor = $("#designTextarea").ckeditorGet();

	var json = JSON.parse(jQuery("#json_" + selected).val());

	var dataElementId = json.dataElement.id;
	var dataElementName = json.dataElement.name;
	var dataElementType = json.dataElement.type;
	var optionComboName = json.optionCombo.name;
	var optionComboId = json.optionCombo.id;

	var titleValue = dataElementId + " - " + dataElementName + " - "
			+ optionComboId + " - " + optionComboName + " - " + dataElementType;

	var displayName = "[ " + dataElementName + " " + optionComboName + " ]";
	var dataEntryId = "value[" + dataElementId + "].value:value["
			+ optionComboId + "].value";
	var boolDataEntryId = "value[" + dataElementId + "].value:value["
			+ optionComboId + "].value";

	var id = "";
	var html = "";

	if (dataElementType == "bool") {
		id = boolDataEntryId;
		html = "<input title=\"" + titleValue
				+ "\" value=\"" + displayName + "\" id=\"" + boolDataEntryId
				+ "\" style=\"width:10em;text-align:center\"/>";
	} 
	else {
		id = dataEntryId;
		html = "<input title=\"" + titleValue
				+ "\" value=\"" + displayName + "\" id=\"" + dataEntryId
				+ "\" style=\"width:10em;text-align:center\"/>";
	}

	if (checkExisted(id)) {
		jQuery("#message_").html("<b>" + i18n_dataelement_is_inserted + "</b>");
		return;
	}
	else {
		jQuery("#message_").html("");
	}

	oEditor.insertHtml(html);
}

function checkExisted(id) {
	var result = false;
	var html = $("#designTextarea").ckeditorGet().getData();
	var input = jQuery(html).find("select, :text");
	input.each(function(i, item) {
		if (id == item.id)
			result = true;
	});

	return result;
}
