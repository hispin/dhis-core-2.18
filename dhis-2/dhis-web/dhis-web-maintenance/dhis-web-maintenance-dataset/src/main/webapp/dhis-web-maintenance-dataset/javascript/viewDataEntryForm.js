jQuery(document).ready(function() {
	validation2('saveDataEntryForm', function() {
		autoSave = false;
		validateDataEntryForm();
	}, {
		'rules' : getValidationRules("dataEntry")
	});

	leftBar.hideAnimated();

	jQuery("#selectionDialog").dialog({
		minWidth: 560,
		minHeight: 320,
		position: [900, 60]
	});

	jQuery("#selectionDialog").parent().bind("resize", function(e) {
		var dialog = jQuery("#selectionDialog");
		var indicatorSelector = jQuery("#indicatorSelector");
		var dataElementSelector = jQuery("#dataElementSelector");

		dataElementSelector.height( dialog.height() - 80 );
		indicatorSelector.height( dialog.height() - 80 );
	});

	jQuery(":button").button();
	jQuery(":submit").button();

	jQuery("#dataElementInsertButton").click(insertDataElement);
	jQuery("#indicatorInsertButton").click(insertIndicator);

	jQuery("#selectionDialog").bind("dialogopen", function(event, ui) {
		jQuery("#showSelectionBoxButton").button("disable");
	});

	jQuery("#selectionDialog").bind("dialogclose", function(event, ui) {
		jQuery("#showSelectionBoxButton").button("enable");
	});

	jQuery("#showSelectionBoxButton").button("disable");

	jQuery("#showSelectionBoxButton").click(function() {
		jQuery("#selectionDialog").dialog("open");
	});
	
	showDataElements();

	jQuery("#dataElementsButton").click(function() {
		jQuery("#dataElementsButton").addClass("ui-state-active2");
		jQuery("#indicatorsButton").removeClass("ui-state-active2");

		showDataElements();
	});

	jQuery("#dataElementsButton").addClass("ui-state-active2");

	jQuery("#indicatorsButton").click(function() {
		jQuery("#indicatorsButton").addClass("ui-state-active2");
		jQuery("#dataElementsButton").removeClass("ui-state-active2");

		showIndicators();
	});

	jQuery("#insertButton").click(function() {
		if( jQuery("#dataElementsTab").is(":visible") ) {
			insertDataElement();
		} else {
			insertIndicator();
		}
	})

	jQuery("#insertButton").button("option", "icons", { primary: "ui-icon-plusthick" });
	jQuery("#saveButton").button("option", "icons", { primary: "ui-icon-disk" });
	jQuery("#saveCloseButton").button("option", "icons", { primary: "ui-icon-disk" });
	jQuery("#showSelectionBoxButton").button("option", "icons", { primary: "ui-icon-newwin" });
	jQuery("#cancelButton").button("option", "icons", { primary: "ui-icon-cancel" });
	jQuery("#delete").button("option", "icons", { primary: "ui-icon-trash" });
});

function showDataElements() {
	jQuery("#dataElementsTab").show();
	jQuery("#dataElementsFilter").show();
	jQuery("#indicatorsTab").hide();
	jQuery("#indicatorsFilter").hide();
}

function showIndicators() {
	jQuery("#indicatorsTab").show();
	jQuery("#indicatorsFilter").show();
	jQuery("#dataElementsTab").hide();
	jQuery("#dataElementsFilter").hide();
}

function filterSelectList( select_id, filter )
{
	var select_selector = "#" + select_id;
	var select_hidden_id = select_id + "_ghost"
	var select_hidden_selector = "#" + select_hidden_id;

	if( $(select_hidden_selector).length === 0 ) {
		var $element = $("<select multiple=\"multiple\" id=\"" + select_hidden_id + "\" style=\"display: none\"></select>");
		$element.appendTo( "body" );
	}

	$(select_selector).find("option").each(function() {
		var val = $(this).val().toLowerCase();

		if(val.indexOf( filter ) == -1) {
			var $option = $(this).detach();
			$option.appendTo( select_hidden_selector );
		}
	});

	$(select_hidden_selector).find("option").each(function() {
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
	jQuery("#message_").html(message);
	jQuery("#message_").fadeOut(1000, function() {
		jQuery("#message_").html("");
		jQuery("#message_").show();
	});
}

function insertIndicator() {
	var oEditor = $("#designTextarea").ckeditorGet();
	var $option = $("#indicatorSelector option:selected");

	if( $option.length !== 0 ) {
		var id = $option.data("id");
		var title = $option.val();
		var template = '<input id="indicator' + id + '" value="[ ' + title + ' ]" title="' + title + '" name="indicator" indicatorid="' + id + '" style="width:10em;text-align:center;" readonly="readonly" />';

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
					+ "\" style=\"width:10em;text-align:center\"/>";
		} 
		else {
			id = dataEntryId;
			html = "<input title=\"" + titleValue
					+ "\" value=\"" + displayName + "\" id=\"" + dataEntryId
					+ "\" style=\"width:10em;text-align:center\"/>";
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
	var input = jQuery(html).find("select, :text");
	input.each(function(i, item) {
		if (id == item.id)
			result = true;
	});

	return result;
}
