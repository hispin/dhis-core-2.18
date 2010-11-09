// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramAttributeDetails( programAttributeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'programAttribute' );
    request.setCallbackSuccess( programAttributeReceived );
    request.send( 'getProgramAttribute.action?id=' + programAttributeId );
}

function programAttributeReceived( programAttributeElement )
{
	setInnerHTML( 'idField', getElementValue( programAttributeElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( programAttributeElement, 'name' ) );	
    setInnerHTML( 'descriptionField', getElementValue( programAttributeElement, 'description' ) );
    
    var valueTypeMap = { 'NUMBER':i18n_number, 'BOOL':i18n_yes_no, 'TEXT':i18n_text, 'DATE':i18n_date, 'COMBO':i18n_combo };
    var valueType = getElementValue( programAttributeElement, 'valueType' );    
	
    setInnerHTML( 'valueTypeField', valueTypeMap[valueType] );    
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Program Attribute
// -----------------------------------------------------------------------------
function removeProgramAttribute( programAttributeId, name )
{
	removeItem( programAttributeId, name, i18n_confirm_delete, 'removeProgramAttribute.action' );	
}

ATTRIBUTE_OPTION = 
{
	selectValueType : function (this_)
	{
		if ( jQuery(this_).val() == "COMBO" )
		{
			jQuery("#attributeComboRow").show();
			if( jQuery("#attrOptionContainer").find("input").length ==0 ) 
			{
				ATTRIBUTE_OPTION.addOption();
				ATTRIBUTE_OPTION.addOption();
			}
		}else {
			jQuery("#attributeComboRow").hide();
		}
	},
	checkOnSubmit : function ()
	{
		if( jQuery("#valueType").val() != "COMBO" ) 
		{
			jQuery("#attrOptionContainer").children().remove();
			return true;
		}else {
			$("input","#attrOptionContainer").each(function(){ 
				if( !jQuery(this).val() )
					jQuery(this).remove();
			});
			if( $("input","#attrOptionContainer").length < 2)
			{
				alert(i18n_at_least_2_option);
				return false;
			}else return true;
		}
	},
	addOption : function ()
	{
		jQuery("#attrOptionContainer").append(ATTRIBUTE_OPTION.createInput());
	},
	remove : function (this_, optionId)
	{
		
		if( jQuery(this_).siblings("input").attr("name") != "attrOptions")
		{
			jQuery.get("removeProgramAttributeOption.action?id="+optionId,function(data){
				var type  = jQuery(data).find("message").attr("type");
				alert(type);
				if( type == "success")
				{
					alert("success");
					jQuery(this_).parent().parent().remove();
					alert(jQuery(data).text());
				}else 
				{
					alert(jQuery(data).text());
				}
			});
		}else
		{
			jQuery(this_).parent().parent().remove();
		}
	},
	removeInAddForm : function(this_)
	{
		jQuery(this_).parent().parent().remove();
	},
	createInput : function ()
	{
		return "<tr><td><input type='text' name='attrOptions' style='width:28em'/><a href='#' style='text-decoration: none; margin-left:0.5em;' title='"+i18n_remove_option+"'  onClick='ATTRIBUTE_OPTION.remove(this,null)'>[ - ]</a></td></tr>";
	}
}