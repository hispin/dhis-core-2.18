$(document).ready(function() {
	validation2('saveDataEntryForm', function() {
		autoSave = false;
		validateDataEntryForm();
	}, {
		'rules' : getValidationRules("dataEntry")
	});

	leftBar.hideAnimated();

	$("#selectionDialog").dialog({
		minWidth: 500,
		minHeight: 263,
		position: [($("body").width() - 500) - 50, 50]
	});

	$("#selectionDialog").parent().bind("resize", function(e) {
		var dialog = $("#selectionDialog");
		var indicatorSelector = $("#indicatorSelector");
		var dataElementSelector = $("#dataElementSelector");

		dataElementSelector.height( dialog.height() - 106 );
		indicatorSelector.height( dialog.height() - 106 );
	});

	$(":button").button();
	$(":submit").button();

	$("#dataElementInsertButton").click(insertDataElement);
	$("#indicatorInsertButton").click(insertIndicator);

	$("#selectionDialog").bind("dialogopen", function(event, ui) {
		$("#showSelectionBoxButton").button("disable");
	});

	$("#selectionDialog").bind("dialogclose", function(event, ui) {
		$("#showSelectionBoxButton").button("enable");
	});

	$("#showSelectionBoxButton").button("disable");

	$("#showSelectionBoxButton").click(function() {
		$("#selectionDialog").dialog("open");
	});
	
	showDataElements();

	$("#dataElementsButton").click(function() {
		$("#dataElementsButton").addClass("ui-state-active2");
		$("#indicatorsButton").removeClass("ui-state-active2");

		showDataElements();
	});

	$("#dataElementsButton").addClass("ui-state-active2");

	$("#indicatorsButton").click(function() {
		$("#indicatorsButton").addClass("ui-state-active2");
		$("#dataElementsButton").removeClass("ui-state-active2");

		showIndicators();
	});

	$("#insertButton").click(function() {
		if( $("#dataElementsTab").is(":visible") ) {
			insertDataElement();
		} else {
			insertIndicator();
		}
	})

	$("#insertButton").button("option", "icons", { primary: "ui-icon-plusthick" });
	$("#saveButton").button("option", "icons", { primary: "ui-icon-disk" });
	$("#saveCloseButton").button("option", "icons", { primary: "ui-icon-disk" });
	$("#showSelectionBoxButton").button("option", "icons", { primary: "ui-icon-newwin" });
	$("#cancelButton").button("option", "icons", { primary: "ui-icon-cancel" });
	$("#delete").button("option", "icons", { primary: "ui-icon-trash" });
	
	$("#dataElementsFilterButton").button({
		icons: {
			primary: "ui-icon-search"
		},
		text: false
	}).click(function() {
		filterSelectList( 'dataElementSelector', $("#dataElementsFilterInput").val() );
	});
	
	$("#indicatorsFilterButton").button({
		icons: {
			primary: "ui-icon-search"
		},
		text: false
	}).click(function() {
		filterSelectList( 'indicatorSelector', $("#indicatorsFilterInput").val() );
	});
});

function showDataElements() {
	$("#dataElementsTab").show();
	$("#dataElementsFilter").show();
	$("#indicatorsTab").hide();
	$("#indicatorsFilter").hide();
}

function showIndicators() {
	$("#indicatorsTab").show();
	$("#indicatorsFilter").show();
	$("#dataElementsTab").hide();
	$("#dataElementsFilter").hide();
}

function filterSelectList( select_id, filter )
{
	var select_selector = "#" + select_id;
	var select_hidden_id = select_id + "_ghost"
	var select_hidden_selector = "#" + select_hidden_id;

	var $select_options = $(select_selector).find("option"); 
	var $select_hidden_options = $(select_hidden_selector).find("option"); 

	if( $(select_hidden_selector).length === 0 ) {
		var $element = $("<select multiple=\"multiple\" id=\"" + select_hidden_id + "\" style=\"display: none\"></select>");
		$element.appendTo( "body" );
	}

	$select_options.each(function() {
		var val = $(this).val().toLowerCase();

		if(val.indexOf( filter ) == -1) {
			var $option = $(this).detach();
			$option.appendTo( select_hidden_selector );
		}
	});

	$select_hidden_options.each(function() {
		var val = $(this).val().toLowerCase();

		if(val.indexOf( filter ) != -1) {
			var $option = $(this).detach();
			$option.appendTo( select_selector );
		}
	});

	var $sorted = $(select_selector).find("option").sort(function(a, b) {
		var idxa = +$(a).data("idx");
		var idxb = +$(b).data("idx");

		if(idxa > idxb) return 1;
		else if(idxa < idxb) return -1;
		else return 0;
	})
	
	$(select_selector).empty();
	$sorted.appendTo( select_selector );
}

function showThenFadeOutMessage( message )
{
	$("#message_").html(message);
	$("#message_").fadeOut(1000, function() {
		$("#message_").html("");
		$("#message_").show();
	});
}

function insertIndicator() {
	var oEditor = $("#designTextarea").ckeditorGet();
	var $option = $("#indicatorSelector option:selected");

	if( $option.length !== 0 ) {
		var id = $option.data("id");
		var title = $option.val();
		var template = '<input id="indicator' + id + '" value="[ ' + title + ' ]" title="' + title + '" name="indicator" indicatorid="' + id + '" style="width:7em;text-align:center;" readonly="readonly" />';

		if(!checkExisted("indicator" + id)) {
			oEditor.insertHtml( template )
		} else {
			showThenFadeOutMessage( "<b>" + i18n_indicator_already_inserted + "</b>" );
		}
	} else {
		showThenFadeOutMessage( "<b>" + i18n_no_indicator_was_selected + "</b>" );
	}
}

function insertDataElement() {
	var oEditor = $("#designTextarea").ckeditorGet();
	var $option = $("#dataElementSelector option:selected");

	if( $option.length !== 0 ) {
		var dataElementId = $option.data("dataelement-id");
		var dataElementName = $option.data("dataelement-name");
		var dataElementType = $option.data("dataelement-type");
		var optionComboId = $option.data("optioncombo-id");
		var optionComboName = $option.data("optioncombo-name");
	
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
					+ "\" style=\"width:7em;text-align:center\"/>";
		} 
		else {
			id = dataEntryId;
			html = "<input title=\"" + titleValue
					+ "\" value=\"" + displayName + "\" id=\"" + dataEntryId
					+ "\" style=\"width:7em;text-align:center\"/>";
		}
	
		if (!checkExisted(id)) {
			oEditor.insertHtml(html);
		} else {
			showThenFadeOutMessage( "<b>" + i18n_dataelement_already_inserted + "</b>" );
		}
	} else {
		showThenFadeOutMessage( "<b>" + i18n_no_dataelement_was_selected + "</b>" );
	}
}

function checkExisted(id) {
	var result = false;
	var html = $("#designTextarea").ckeditorGet().getData();
	var input = $(html).find("select, :text");
	input.each(function(i, item) {
		if (id == item.id)
			result = true;
	});

	return result;
}
