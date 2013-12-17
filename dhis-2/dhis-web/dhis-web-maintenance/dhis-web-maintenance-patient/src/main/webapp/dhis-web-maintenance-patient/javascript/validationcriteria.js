$(function() {
  dhis2.contextmenu.makeContextMenu({
    menuId: 'contextMenu',
    menuItemActiveClass: 'contextMenuItemActive'
  });
});

function showProgramsForValidationCriteriaForm( context ) {
  location.href = 'showProgramsForValidationCriteriaForm.action?criteriaId=' + context.id;
}

function showUpdateValidationCriteriaForm( context ) {
  location.href = 'showUpdateValidationCriteriaForm.action?id=' + context.id;
}

// -----------------------------------------------------------------------------
// Remove Criteria
// -----------------------------------------------------------------------------

function removeCriteria( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, 'removeValidationCriteria.action');
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showValidationCriteriaDetails( context ) {
  jQuery.getJSON('getValidationCriteria.action', { id: context.id }, function( json ) {
    setInnerHTML('nameField', json.validationCriteria.name);
    setInnerHTML('descriptionField', json.validationCriteria.description);

    var property = json.validationCriteria.property;
    var operator = json.validationCriteria.operator;
    var value = json.validationCriteria.value;

    // get operator
    if( operator == 0 ) {
      operator = '=';
    } else if( operator == -1 ) {
      operator = '<';
    } else {
      operator = '>';
    }

    setInnerHTML('criteriaField', property + " " + operator + " " + value);
    showDetails();
  });
}

// ----------------------------------------------------------------------------------------
// Show div to Add or Update Validation-Criteria
// ----------------------------------------------------------------------------------------
function showDivValue() {

  var propertyName = byId('property').value;
  hideDiv();
  if( propertyName != '' ) {
    hideById('emptyCriteria');

    var div = byId(propertyName + 'Div');
    div.style.display = 'block';
    if( propertyName == 'gender' ||
      propertyName == 'dobType' ) {

      byId('operator').selectedIndex = 1;
      disable('operator');
    }
    else {
      enable('operator');
    }
  }
}

function hideDiv() {
  hideById('genderDiv');
  hideById('integerValueOfAgeDiv');
  hideById('birthDateDiv');
  hideById('dobTypeDiv');
  showById('emptyCriteria');
}

function fillValue( value ) {
  byId('value').value = value;
}
