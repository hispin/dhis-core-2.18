
jQuery(document).ready(function() {
	validation2('addInventoryTypeForm', function(form) {
		form.submit();
	}, {
		'beforeValidateHandler' : function() {
            $("#selectedInventoryTypeAttributeList").find("option").attr("selected", "selected");
		},
	});

	checkValueIsExist("name", "validateInventoryType.action");
});

