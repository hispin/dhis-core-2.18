
function removeDataElementCategory( categoryId, categoryName )
{
	removeItem( categoryId, categoryName, i18n_confirm_delete, 'removeDataElementCategory.action' );
}
	
function addCategoryOption()
{
	var value = getFieldValue( 'categoryOptionName' );
	if( value.length == 0 ) 
	{
		markInvalid( 'categoryOptionName', i18n_specify_category_option_name );
	} 
	else if ( listContainsById( 'categoryOptionNames', value ) ) 
	{
		markInvalid( 'categoryOptionName', i18n_category_option_name_already_exists );
	} 
	else 
	{
		jQuery.postJSON( 'validateDataElementCategoryOption.action', { name:value }, function( json ) 
		{
			if ( json.response == 'success' )
			{					
				addOption( 'categoryOptionNames', value, value );
				setFieldValue( 'categoryOptionName', '' );
			}
			else
			{
				markInvalid( 'categoryOptionName', i18n_category_option_name_already_exists );
			}
		} );
	}
}

function updateCategoryOption()
{
	try
	{
		var name = getFieldValue( 'categoryOptionName' );
		var id = getFieldValue( 'categoryOptions' );
		
		if ( name.length == 0 )
		{
			markInvalid( 'categoryOptionName', i18n_specify_category_option_name );
		}		
		else if ( listContainsById( 'categoryOptions', name, true ) )
		{
			markInvalid( 'categoryOptionName', i18n_category_option_name_already_exists );
		}
		else
		{
			jQuery.postJSON( 'validateDataElementCategoryOption.action', { name:name, id:id }, function( json )
			{
				if( json.response == 'success' )
				{
					updateCategoryOptionName();
				}
				else
				{
					markInvalid( 'categoryOptionName', i18n_category_option_name_already_exists );
				}
			} );
		}
	}
	catch ( e )
	{
		markInvalid( 'categoryOptionName', i18n_specify_category_option_name );
	}
}

function getSelectedCategoryOption()
{
	var categoryOptions = byId('categoryOptions');
	byId('categoryOptionName').value=categoryOptions[categoryOptions.selectedIndex].text;
}

function updateCategoryOptionName()
{
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( updateCategoryOptionNameReceived );
	var params = "id=" + byId('categoryOptions').value;
		params += '&name=' + byId('categoryOptionName').value;
	request.sendAsPost(params);
	request.send('updateDataElementCategoryOption.action');
}

function updateCategoryOptionNameReceived(xmlObject){
	var categoryOptions = byId('categoryOptions');
	categoryOptions[categoryOptions.selectedIndex].text = byId('categoryOptionName').value;
	showSuccessMessage( i18n_option_rename_successfully );
}
