jQuery(document).ready(	function() {
	
	jQuery('name').focus();

	validation( 'updateAttributeGroupForm', function(form){
			form.submit();
		}, function(){
			selectAllById('selectedAttributes');
			if(jQuery("#selectedAttributes option").length > 0 ){
				setFieldValue('hasAttributes', 'true');
			}
		});

	checkValueIsExist( "name", "validateAttributeGroup.action", {id:getFieldValue('id')});
	
	jQuery("#availableAttributes").dhisAjaxSelect({
		source: 'getAttributeWithoutGroup.action',
		iterator: 'attributes',
		connectedTo: 'selectedAttributes',
		handler: function(item){
			var option = jQuery( "<option/>" );
			option.attr( "value", item.id );
			option.text( item.name );
			
			return option;
		}
	});
});		