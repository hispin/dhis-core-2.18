function beforeSubmit()
{
	memberValidator = jQuery( "#memberValidator");
	memberValidator.children().remove();
	
	jQuery.each( jQuery( "#selectedAttributes" ).children(), function(i, item){
		item.selected = 'selected';
		memberValidator.append( '<option value="' + item.value + '" selected="selected">' + item.value + '</option>');
	});
}
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientAttributeGroupDetails( patientAttributeGroupId )
{
	jQuery.post( 'getPatientAttributeGroup.action', { id: patientAttributeGroupId },
		function ( json ) {
				
			setInnerHTML( 'idField', json.patientAttributeGroup.id );
			setInnerHTML( 'nameField', json.patientAttributeGroup.name );	
			setInnerHTML( 'descriptionField', json.patientAttributeGroup.description );
			setInnerHTML( 'noAttributeField', json.patientAttributeGroup.noAttribute );

			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove Patient Attribute
// -----------------------------------------------------------------------------

function removePatientAttributeGroup( patientAttributeGroupId, name )
{
    removeItem( patientAttributeGroupId, name, i18n_confirm_delete, 'removePatientAttributeGroup.action' );
}

// -----------------------------------------------------------------------------
// Show and Hide tooltip
// -----------------------------------------------------------------------------

function patientAttributeGroupAssociation()
{
	selectAllById('selectedAttributeGroups');

    var form = document.getElementById( 'patientAttributeGroupAssociationForm' );        

    form.submit();
}

// -----------------------------------------------------------------------------
// Show and Hide tooltip
// -----------------------------------------------------------------------------

function showToolTip( e, value){
	
	var tooltipDiv = byId('tooltip');
	tooltipDiv.style.display = 'block';
	
	var posx = 0;
    var posy = 0;
	
    if (!e) var e = window.event;
    if (e.pageX || e.pageY)
    {
        posx = e.pageX;
        posy = e.pageY;
    }
    else if (e.clientX || e.clientY)
    {
        posx = e.clientX;
        posy = e.clientY;
    }
	
	tooltipDiv.style.left= posx  + 8 + 'px';
	tooltipDiv.style.top = posy  + 8 + 'px';
	tooltipDiv.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +   value;
}

function hideToolTip(){
	byId('tooltip').style.display = 'none';
}