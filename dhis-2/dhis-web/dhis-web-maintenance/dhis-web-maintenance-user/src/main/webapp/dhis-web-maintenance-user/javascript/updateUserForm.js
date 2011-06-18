jQuery(document).ready(function()
{
    var rules = getValidationRules("user");
    rules["rawPassword"].required = false;
    rules["retypePassword"].required = false;

    validation2('updateUserForm', function( form )
    {
        jQuery("#selectedList").children().attr("selected", true);
        form.submit()
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator('roleValidator', 'selectedList');
        },
        'rules' : rules
    });

    jQuery("#cancel").click(function()
    {
        referrerBack("alluser.action");
    });
});
