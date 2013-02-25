
function openPropertiesSelector()
{
	$('#selectionDialog' ).dialog(
		{
			title:'fafds',
			maximize:true, 
			closable:true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width:500,
			height:460
		});
}

function fixAttrOnClick()
{
	hideById('attributeTab');
	hideById('identifierTypeTab');
	hideById('programAttrTab');
	showById('fixedAttrTab');
}

function identifierTypeOnClick()
{
	hideById('attributeTab');
	hideById('fixedAttrTab');
	hideById('programAttrTab');
	showById('identifierTypeTab');
}

function attributesOnClick()
{
	hideById('identifierTypeTab');
	hideById('fixedAttrTab');
	hideById('programAttrTab');
	showById('attributeTab');
}

function programAttrOnClick()
{
	hideById('attributeTab');
	hideById('identifierTypeTab');
	hideById('fixedAttrTab');
	showById('programAttrTab');
}

function validateForm()
{
	var result = false;
	var html = jQuery("#designTextarea").ckeditorGet().getData();
	
	var requiredFields = new Array();
	requiredFields.push('fixedattributeid=registrationDate');
	requiredFields.push('fixedattributeid=fullName');
	requiredFields.push('fixedattributeid=gender');
	requiredFields.push('fixedattributeid=birthDate');
	
	jQuery('#identifiersSelector option').each(function() {
		var item = jQuery(this);
		if( item.attr('mandatory')=='true'){
			requiredFields.push('identifierid=' + item.val());
		}
	});

	jQuery('#attributesSelector option').each(function() {
		var item = jQuery(this);
		if( item.attr('mandatory')=='true'){
			requiredFields.push('attributeid=' + item.val());
		}
	});

	var input = jQuery( html ).find("input");
	if( input.length > 0 )
	{
		input.each( function(i, item){	
			var key = "";
			var inputKey = jQuery(item).attr('fixedattributeid');
			if( inputKey!=undefined)
			{
				key = 'fixedattributeid=' + inputKey
			}
			else if( jQuery(item).attr('identifierid')!=undefined ){
				inputKey = jQuery(item).attr('identifierid');
				key = 'identifierid=' + inputKey
			}
			else if( jQuery(item).attr('attributeid')!=undefined ){
				inputKey = jQuery(item).attr('attributeid');
				key = 'attributeid=' + inputKey
			}
			else if( jQuery(item).attr('programid')!=undefined ){
				inputKey = jQuery(item).attr('programid');
				key = 'programid=' + inputKey
			}
			
			for (var idx=0; idx<requiredFields.length; idx++){
				var field = requiredFields[idx];
				if( key == field)
				{
					requiredFields.splice(idx,1);
				}
			}
		});
	
	}
	if( requiredFields.length > 0 ) {
		setFieldValue('requiredField','');
		return false;
	}
	else{
		setFieldValue('requiredField','everything_is_ok');
		setInnerHTML( 'designTextarea' , jQuery("#designTextarea").ckeditorGet().getData() );
		byId('saveDataEntryForm').submit();
	}
}

function checkExisted( id )
{	
	var result = false;
	var html = jQuery("#designTextarea").ckeditorGet().getData();
	var input = jQuery( html ).find("input");

	input.each( function(i, item){		
		var key = "";
		var inputKey = jQuery(item).attr('fixedattributeid');
		if( inputKey!=undefined)
		{
			key = 'fixedattributeid="' + inputKey + '"';
		}
		else if( jQuery(item).attr('identifierid')!=undefined ){
			inputKey = jQuery(item).attr('identifierid');
			key = 'identifierid="' + inputKey + '"';
		}
		else if( jQuery(item).attr('attributeid')!=undefined ){
			inputKey = jQuery(item).attr('attributeid');
			key = 'attributeid="' + inputKey + '"';
		}
		else if( jQuery(item).attr('programid')!=undefined ){
			inputKey = jQuery(item).attr('programid');
			key = 'programid="' + inputKey + '"';
		}
		
		if( id == key ) result = true;		
		
	});

	return result;
}

function insertElement( type )
{
	var id = '';
	var value = '';
	if( type == 'fixedAttr' ){
		var element = jQuery('#fixedAttrSelector option:selected');
		id = 'fixedattributeid="' + element.attr('value') + '"';
		value = element.text();
	}
	else if( type == 'iden' ){
		var element = jQuery('#identifiersSelector option:selected');
		id = 'identifierid="' + element.attr('value') + '"';
		value = element.text();
	}
	else if( type == 'attr' ){
		var element = jQuery('#attributesSelector option:selected');
		id = 'attributeid="' + element.attr('value') + '"';
		value = element.text();
	}
	else if( type == 'prg' ){
		var element = jQuery('#programAttrSelector option:selected');
		id = 'programid="' + element.attr('value') + '"';
		value = element.text();
	}
	
	var htmlCode = "<input " + id + " value=\"[" + value + "]\" title=\"" + value + "\">";
	
	if( checkExisted( id ) ){		
		setMessage( "<span class='bold'>" + i18n_property_is_inserted + "</span>" );
		return;
	}else{
		var oEditor = jQuery("#designTextarea").ckeditorGet();
		oEditor.insertHtml( htmlCode );
		setMessage("");
	}

}
