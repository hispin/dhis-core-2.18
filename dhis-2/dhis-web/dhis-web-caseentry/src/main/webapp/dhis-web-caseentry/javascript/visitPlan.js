
function organisationUnitSelected( orgUnits )
{
    window.location.href = 'visitPlanSelect.action';
}

selection.setListenerFunction( organisationUnitSelected );

function sortByAttribute( sortingAttributeId )
{	
	window.location = "visitplan.action?sortingAttributeId=" + sortingAttributeId;
}
