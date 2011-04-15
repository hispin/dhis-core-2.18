jQuery(document).ready(function()
{
    var rules = getValidationRules("user");
    rules["rawPassword"].required = false;
    rules["retypePassword"].required = false;

    validation2('updateUserForm', function( form )
    {
        form.submit()
    },
    {
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
