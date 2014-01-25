// -----------------------------------------------------------------------------
// Export to PDF file
// -----------------------------------------------------------------------------

function exportPDF( type ) {
  var params = "type=" + type;
  params += "&months=" + jQuery('#months').val();

  exportPdfByType(type, params);
}

// -----------------------------------------------------------------------------
// Search users
// -----------------------------------------------------------------------------

function searchUserName() {
  var key = getFieldValue('key');

  if( key != '' ) {
    jQuery('#userForm').load('searchUser.action', {key: key}, unLockScreen);
    lockScreen();
  }
  else {
    jQuery('#userForm').submit();
  }
}

function getInactiveUsers() {
  var months = $('#months').val();

  window.location.href = 'alluser.action?months=' + months;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUpdateUserForm( context ) {
  location.href = 'showUpdateUserForm.action?id=' + context.id;
}

function showUserProfile( context ) {
  location.href = '../dhis-web-dashboard-integration/profile.action?id=' + context.id;
}

function showUserDetails( context ) {
  jQuery.post('getUser.action', { id: context.id }, function( json ) {
    setInnerHTML('usernameField', json.user.username);

    var fullName = json.user.firstName + ", " + json.user.surname;
    setInnerHTML('fullNameField', fullName);

    var email = json.user.email;
    setInnerHTML('emailField', email ? email : '[' + i18n_none + ']');

    var phoneNumber = json.user.phoneNumber;
    setInnerHTML('phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']');

    var lastLogin = json.user.lastLogin;
    setInnerHTML('lastLoginField', lastLogin ? lastLogin : '[' + i18n_none + ']');

    var created = json.user.created;
    setInnerHTML('createdField', created ? created : '[' + i18n_none + ']');

    var disabled = json.user.disabled;
    setInnerHTML('disabledField', disabled ? i18n_yes : i18n_no);

    var organisationUnits = joinNameableObjects(json.user.organisationUnits);
    setInnerHTML('assignedOrgunitField', organisationUnits ? organisationUnits : '[' + i18n_none + ']');

    var roles = joinNameableObjects(json.user.roles);
    setInnerHTML('roleField', roles ? roles : '[' + i18n_none + ']');

    showDetails();
  });
}

// -----------------------------------------------------------------------------
// Add user
// -----------------------------------------------------------------------------

var saved = {};

function changeAccountAction()
{
    if( $('#accountAction').val() == 'create' )
    {
        $('#username').val( saved["username"] );
        $('#rawPassword').val( saved["rawPassword"] );
        $('#retypePassword').val( saved["retypePassword"] );
        $('#surname').val( saved["surname"] );
        $('#firstName').val( saved["firstName"] );
        $('#phoneNumber').val( saved["phoneNumber"] );
        $('#email').val( $('#inviteEmail').val() );
        $('#inviteEmail').val( 'validEmail@domain.com' );

        showById('usernameTR');
        showById('rawPasswordTR');
        showById('retypePasswordTR');
        showById('surnameTR');
        showById('firstNameTR');
        showById('phoneNumberTR');
        showById('emailTR');

        hideById('inviteEmailTR');
    }
    else
    {
        hideById('usernameTR');
        hideById('rawPasswordTR');
        hideById('retypePasswordTR');
        hideById('surnameTR');
        hideById('firstNameTR');
        hideById('phoneNumberTR');
        hideById('emailTR');

        showById('inviteEmailTR');

        saved["username"] = $('#username').val();
        saved["rawPassword"] = $('#rawPassword').val();
        saved["retypePassword"] = $('#retypePassword').val();
        saved["surname"] = $('#surname').val();
        saved["firstName"] = $('#firstName').val();
        saved["phoneNumber"] = $('#phoneNumber').val();

        $('#username').val( 'nonExistingUserName_RpuECtIlVoRKTpYmEkYrAHmPtX4m1U' );
        $('#rawPassword').val( 'validPassword_123' );
        $('#retypePassword').val( 'validPassword_123' );
        $('#surname').val( 'validSurname' );
        $('#firstName').val( 'validFirstName' );
        $('#phoneNumber').val( '5555555555' );
        $('#inviteEmail').val( $('#email').val() );
        $('#email').val( '' );
    }
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( context ) {
  removeItem(context.id, context.name, i18n_confirm_delete, "removeUser.action");
}

function disableUser( context ) {
  var confirmation = confirm("Are you sure you want to disable this user?\n\n" + context.username);

  if( confirmation ) {
    $.post("disableUser.action", {
        username: context.username
      },
      function( json ) {
        location.reload();
      });
  }
}

function enableUser( context ) {
  var confirmation = confirm("Are you sure you want to enable this user?\n\n" + context.username );

  if( confirmation ) {
    $.post("disableUser.action",
      {
        username: context.username,
        enable: true
      },
      function( json ) {
        location.reload();
      });
  }
}
