jQuery(document).ready(	function() {
	
	jQuery('name').focus();

	validation2( 'updateAttributeGroupForm', function( form )
		{
			form.submit();
		},{
			'beforeValidateHandler' : function()
			{
				selectAllById('selectedAttributes');
				if(jQuery("#selectedAttributes option").length > 0 ){
					setFieldValue('attributeList', 'true');
				}
			},
			'rules' : getValidationRules( "trackedEntityAttributeGroup" )
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