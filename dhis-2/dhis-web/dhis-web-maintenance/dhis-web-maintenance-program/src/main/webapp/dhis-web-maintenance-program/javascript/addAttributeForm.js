jQuery(document).ready(function() {
  validation('addAttributeForm', function( form ) {
    form.submit();
  })

  checkValueIsExist("name", "validateAttribute.action");
  checkValueIsExist("shortName", "validateAttribute.action");
  checkValueIsExist("code", "validateAttribute.action");

  $('#unique').on('click', function() {
    $("[name='localIdField']").toggle();
  });
});