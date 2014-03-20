
$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdateAttributeForm( context ) {
  location.href = 'showUpdateAttributeForm.action?id=' + context.id;
}

function showAttributeDetails( context ) {
	jQuery.getJSON( 'getAttribute.action', { id: context.id },
		function ( json ) {
			setInnerHTML( 'nameField', json.attribute.name );	
			setInnerHTML( 'descriptionField', json.attribute.description );
			setInnerHTML( 'optionSetField', json.attribute.optionSet );
			
			var unique = ( json.attribute.unique == 'true') ? i18n_yes : i18n_no;
			setInnerHTML( 'uniqueField', unique );
			
			var inherit = ( json.attribute.inherit == 'true') ? i18n_yes : i18n_no;
			setInnerHTML( 'inheritField', inherit );
			
			var valueType = json.attribute.valueType;
			var typeMap = attributeTypeMap();
			setInnerHTML( 'valueTypeField', typeMap[valueType] );    
			
			showDetails();
	});
}

function attributeTypeMap()
{
	var typeMap = [];
	typeMap['number'] = i18n_number;
	typeMap['string'] = i18n_text;
	typeMap['bool'] = i18n_yes_no;
	typeMap['trueOnly'] = i18n_yes_only;
	typeMap['date'] = i18n_date;
	typeMap['phoneNumber'] = i18n_phone_number;
	typeMap['trackerAssociate'] = i18n_tracker_associate;
	typeMap['combo'] = i18n_attribute_combo_type;
	return typeMap;
}

// -----------------------------------------------------------------------------
// Remove Attribute
// -----------------------------------------------------------------------------

function removeAttribute( context )
{
	removeItem( context.id, context.name, i18n_confirm_delete, 'removeAttribute.action' );
}


function typeOnChange() {
	var type = getFieldValue('valueType');
	if( type=="combo"){
		showById("optionSetRow");
		jQuery('[name=localIdField]').hide();
	}
	else if( type == 'localId' ) {
		jQuery('[name=localIdField]').show();
		hideById("optionSetRow");
	}
	else {
		jQuery('[name=localIdField]').hide();
		hideById("optionSetRow");
	}
}

